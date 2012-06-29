package ru.ares4322.filescanner;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import ru.ares4322.filescanner.args.ScanParams;
import ru.ares4322.filescanner.args.SimpleScanParams;

/**
 * Сканер файлов с помощью nio-пакета из JDK7. Распараллеливает сканирование
 * таким образом, чтобы одновременно не выполнялось сканирование для путей с
 * одного диска. Количество потоков делается равным количеству дисков, а не
 * количеству ядер (процессоров), так как сканирование все равно блокируется на
 * вводе/выводе.
 *
 * @author ares4322
 */
public class NIOScanner implements FileScanner {

	@Override
	public void scan(ScanParams params, ScanResultOutputParams outputParams) throws ScanException {
		SimpleScanParams scanParams = (SimpleScanParams) params;

		List<FileInfo> resultList = new LinkedList<>();
		int curTaskCounter = 0;

		//создаем фиксированный пул потоков, размер которого равен количеству дисков, на которых расположены наши файлы
		Map<String, SortedMap<Path, List<Path>>> diskToPathMap = scanParams.getPathMapsToDisk();
		int diskQuantity = diskToPathMap.size();
		if (diskQuantity <= 0) {
			throw new ScanException("No paths to scan");
		}
		ExecutorService executorService = Executors.newFixedThreadPool(diskQuantity);
		ExecutorCompletionService<ScanResult> executorCompletionService = new ExecutorCompletionService<>(executorService);

		//заполняем пул задачами для каждого диска
		//при получении задачи удаляем ее из словаря и если словарь после этого пуст, то удаляем и его
		for (Iterator<Entry<String, SortedMap<Path, List<Path>>>> it = diskToPathMap.entrySet().iterator(); it.hasNext();) {
			Entry<String, SortedMap<Path, List<Path>>> entry = it.next();
			String diskName = entry.getKey();
			SortedMap<Path, List<Path>> scanToExcludeListMap = entry.getValue();
			Path scanPath = scanToExcludeListMap.firstKey();
			List<Path> excludePathList = scanToExcludeListMap.get(scanPath);
			scanToExcludeListMap.remove(scanPath);
			if (scanToExcludeListMap.isEmpty()) {
				it.remove();
			}

			executorCompletionService.submit(new SimpleFileVisitorTask(scanPath, excludePathList, diskName));
			curTaskCounter++;
		}

		//до тех пор, пока не выполнятся все задачи, получаем результат самой быстро выполненной на данный момент задачи,
		//если для диска, для которого была эта выполненная задача, есть еще задачи, то добавляем ее на выполнение.
		//если задач больше нет, то завершаем работу
		while (true) {
			try {
				if (curTaskCounter > 0) {
					Future<ScanResult> future = executorCompletionService.take();
					curTaskCounter--;

					ScanResult scanResult = future.get();
					resultList.addAll(scanResult.resultPathList);
					String diskName = scanResult.diskName;

					SortedMap<Path, List<Path>> scanToExcludeListMap = diskToPathMap.get(diskName);
					if (scanToExcludeListMap != null) {
						Path scanPath = scanToExcludeListMap.firstKey();
						List<Path> excludePathList = scanToExcludeListMap.get(scanPath);
						scanToExcludeListMap.remove(scanPath);
						if (scanToExcludeListMap.isEmpty()) {
							diskToPathMap.remove(diskName);
						}
						executorCompletionService.submit(new SimpleFileVisitorTask(scanPath, excludePathList, diskName));
						curTaskCounter++;
					}
				} else {
					break;
				}
			} catch (InterruptedException | ExecutionException ex) {
				System.err.println(new StringBuilder(2).append("ERROR: ").append(ex.getMessage()));
				curTaskCounter--;
			}
		}

		executorService.shutdown();

		Collections.sort(resultList);

		Utils.writePathListToFileExt(outputParams.getOutputFilePath(), resultList, outputParams.getOutputFileCharset());
	}
}
