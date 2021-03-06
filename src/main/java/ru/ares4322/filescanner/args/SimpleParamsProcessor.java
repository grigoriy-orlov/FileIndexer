package ru.ares4322.filescanner.args;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @todo вместо списк тут можно использовать SortedSet, так как на надо работать
 * с отсортированным набором и в нем не должно быть повторений Обработчик
 * параметров сканирования
 *
 * @author ares4322
 */
public class SimpleParamsProcessor implements ParamsProcessor {

	/**
	 * Обрабатывает параметры сканирования (сортирует, удаляет избыточность и
	 * т.д.) для последующей передачи сканеру
	 *
	 * @param params Исходные параметры сканирования
	 * @return Обработанные параметры сканирования
	 * @throws ParamsProcessingException
	 */
	@Override
	public ScanParams process(ScanParams params) throws ParamsProcessingException {
		SimpleScanParams scanParams = (SimpleScanParams) params;

		if (scanParams == null) {
			throw new ParamsProcessingException("scan params is null");
		}
		if (scanParams.scanPathList == null) {
			throw new ParamsProcessingException("scan path list is null");
		}
		if (scanParams.excludePathList == null) {
			throw new ParamsProcessingException("exclude path list is null");
		}

		this.removeNonexistentPaths(scanParams.scanPathList);
		this.removeNonexistentPaths(scanParams.excludePathList);

		this.removeSymlinksPaths(scanParams.scanPathList);
		this.removeSymlinksPaths(scanParams.excludePathList);

		this.sort(scanParams.scanPathList);
		this.sort(scanParams.excludePathList);

		this.removePathRedunduncy(scanParams.scanPathList);
		this.removePathRedunduncy(scanParams.excludePathList);

		scanParams.setExcludePathsToScanPathMap(this.sortExcludePathsToSearchPath(scanParams.scanPathList, scanParams.excludePathList));

		scanParams.setPathMapsToDisk(this.sortPathMapsToDisk(scanParams.getExcludePathsToScanPathMap()));

		return scanParams;
	}

	/**
	 * Сортирует список путей сканирования
	 *
	 * @param pathList Список путей для обработки
	 */
	protected void sort(List<Path> pathList) {
		Collections.sort(pathList);
	}

	/**
	 * Удаляет пути для несуществующих файлов
	 *
	 * @param pathList Список путей для обработки
	 */
	protected void removeNonexistentPaths(List<Path> pathList) {
		for (ListIterator<Path> it = pathList.listIterator(); it.hasNext();) {
			Path scanPath = it.next();
			if (Files.exists(scanPath) == false) {
				it.remove();
				System.err.println(new StringBuilder(2).append("WARNING: file not exists: ").append(scanPath));
			}
		}
	}

	/**
	 * Удаляет пути для символических ссылок
	 *
	 * @param pathList Список путей для обработки
	 */
	protected void removeSymlinksPaths(List<Path> pathList) {
		for (ListIterator<Path> it = pathList.listIterator(); it.hasNext();) {
			Path scanPath = it.next();
			if (Files.isSymbolicLink(scanPath) == true) {
				it.remove();
				System.err.println(new StringBuilder(2).append("WARNING: symbolic link (not processed): ").append(scanPath));
			}

		}
	}

	/**
	 * Удаляет избыточность в путях. Если для исходного пути есть путь, который
	 * включает этот исходный путь в себя, то исходный путь можно удалить из
	 * списка путей. Список должен быть отсортирован.
	 *
	 * @param sortedPathList Сортированный список путей для обработки
	 * @return Список путей без избытости
	 */
	protected List<Path> removePathRedunduncy(List<Path> sortedPathList) {
		for (ListIterator<Path> extIt = sortedPathList.listIterator(); extIt.hasNext();) {
			Path extScanPath = extIt.next();
			for (ListIterator<Path> intIt = sortedPathList.listIterator(extIt.nextIndex()); intIt.hasNext();) {
				Path intScanPath = intIt.next();
				if (intScanPath.toAbsolutePath().startsWith(extScanPath.toAbsolutePath())) {
					intIt.remove();
				}
			}
		}
		return sortedPathList;
	}

	/**
	 * Рассортировывает пути исключения по путям сканирования.
	 *
	 * @param scanPathList Списков путей сканирования
	 * @param excludePathList Список путей исключения
	 * @return Словарь, в котором ключи - пути сканирования, а значения - списки
	 * путей исключения
	 */
	protected SortedMap<Path, List<Path>> sortExcludePathsToSearchPath(List<Path> scanPathList, List<Path> excludePathList) {
		SortedMap<Path, List<Path>> resultMap = new TreeMap<>();
		boolean put;

		for (ListIterator<Path> scanPathIt = scanPathList.listIterator(); scanPathIt.hasNext();) {
			Path scanPath = scanPathIt.next();
			List<Path> connectedExcludePathList = new LinkedList<>();
			put = true;
			for (ListIterator<Path> excludePathIt = excludePathList.listIterator(); excludePathIt.hasNext();) {
				Path excludePath = excludePathIt.next();
				if (excludePath.compareTo(scanPath) > 0 && excludePath.toAbsolutePath().startsWith(scanPath.toAbsolutePath())) {
					//если путь исключения начинается с пути поиска, то этот путь поиска добавляем в поиск
					connectedExcludePathList.add(excludePath);
				} else if (excludePath.compareTo(scanPath) <= 0 && scanPath.toAbsolutePath().startsWith(excludePath.toAbsolutePath())) {
					//если путь исключения включает путь поиска, то этот путь поиска не добавляем в поиск
					put = false;
					break;
				}
			}
			if (put) {
				resultMap.put(scanPath, connectedExcludePathList);
			}
		}

		return resultMap;
	}

	/**
	 * Рассортировывает словари с путями сканирования и исключения по дискам, к
	 * которым эти пути относятся. Диск в данном случае определяется по названию
	 * хранилища (Files.getFileStore(scanPath).name()) для пути
	 *
	 * @param sortedPathMap Словарь с путями сканирования и исключения
	 * @return Словарь с путями сканирования и исключения рассортированный по
	 * дискам
	 */
	protected Map<String, SortedMap<Path, List<Path>>> sortPathMapsToDisk(SortedMap<Path, List<Path>> sortedPathMap) {
		Map<String, SortedMap<Path, List<Path>>> pathMapsByDisks = new HashMap<>(sortedPathMap.size());

		for (SortedMap.Entry<Path, List<Path>> entry : sortedPathMap.entrySet()) {
			Path scanPath = entry.getKey();
			List<Path> excludePathList = entry.getValue();
			//если не можем определить хранилище для пути, то сохраняем его для названия диска, равной пустой строке
			String diskName = "";
			try {
				diskName = Files.getFileStore(scanPath).name();
			} catch (IOException ex) {
				System.err.println(new StringBuilder(2).append("WARNING: can't get filestore of ").append(scanPath));
			}

			SortedMap<Path, List<Path>> pathMapForDisk = pathMapsByDisks.get(diskName);
			if (pathMapForDisk == null) {
				pathMapForDisk = new TreeMap<>();
				pathMapsByDisks.put(diskName, pathMapForDisk);
			}
			pathMapForDisk.put(scanPath, excludePathList);

		}
		return pathMapsByDisks;
	}
}
