package ru.ares4322.filescanner;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import ru.ares4322.filescanner.args.ScanParams;
import ru.ares4322.filescanner.args.SimpleScanParams;

/**
 * Сканер файлов с помощью nio-пакета из JDK7.
 *
 * Увеличение производительности сканирования файлов от распараллеливания будет
 * только в том случае, если потоки будут работать со разными физическими
 * дискоами. В общем случае причиной является постоянно обращение к диску за
 * списков файлов в каталоге (в частном случае файлы будут в кеше ОС в ОЗУ, но
 * можно это опустить). При данной простейшей реализации сканирования не
 * определяется на каких дисках находятся файлы и поэтому в общем случае
 * увеличения производительности от распараллеливания не будет. От более сложных
 * алгоритов распараллеивания (с общим состоянием потоков, блокировками и т.д.)
 * будут только больше задержки при сканировании. Поэтому здесь такие алгоритмы
 * не применяются. Но задача распараллеливается в соответствии с требованием ТЗ,
 * но с минимальным влиянием на производительность.
 *
 * @author ares4322
 */
public class NIOScanner implements FileScanner {

	@Override
	public void scan(ScanParams params) {
		SimpleScanParams scanParams = (SimpleScanParams) params;

		List<Path> resultPathList = new LinkedList<>();

		//создаем фиксированный пул потоков, размер которого равен количеству процессоров (ядер).
		//больше делать смысла нет, так как сканирование - операция

		//@todo можно сделать разделение
		int processorsQuantity = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(processorsQuantity);
		
		Map<Path, List<Path>> sortedPathMap = scanParams.getExcludePathsToScanPathMap();
		List<Future<List<Path>>> futures = new LinkedList<>();

		for (Map.Entry<Path, List<Path>> entry : sortedPathMap.entrySet()) {
			Path searchPath = entry.getKey();
			List<Path> excludePathList = entry.getValue();
			futures.add(executor.submit(new PlainFileVisitorTask(searchPath, excludePathList)));
		}

		for (Iterator<Future<List<Path>>> it = futures.iterator(); it.hasNext();) {
			Future<List<Path>> future = it.next();
			if (future != null) {
				try {
					resultPathList.addAll(future.get());
				} catch (InterruptedException | ExecutionException ex) {
					System.err.println("ERROR: " + ex.getMessage());
				}
			}
		}
		executor.shutdown();

		Collections.sort(resultPathList);

		Utils.writePathListToFile(scanParams.getOutputFilePath(), resultPathList, scanParams.getOutputFileCharset());
	}
}
