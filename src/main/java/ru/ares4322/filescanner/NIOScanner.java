package ru.ares4322.filescanner;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
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

		List<FileInfo> resultPathList = new LinkedList<>();
		int taskCounter = 0;
		//создаем фиксированный пул потоков, размер которого равен количеству дисков, на которых расположены наши файлы

		Map<String, SortedMap<Path, List<Path>>> diskToPathMap = scanParams.getDiskToExcludePathsToScanPathMap();
		ExecutorService executorService = Executors.newFixedThreadPool(diskToPathMap.size());
		ExecutorCompletionService<ScanResult> executorCompletionService = new ExecutorCompletionService<>(executorService);
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

			executorCompletionService.submit(new PlainFileVisitorTask(scanPath, excludePathList, diskName));
			taskCounter++;
		}

		while (true) {
			try {
				if (taskCounter > 0) {
					Future<ScanResult> future = executorCompletionService.take();
					taskCounter--;

					ScanResult scanResult = future.get();
					resultPathList.addAll(scanResult.resultPathList);
					String diskName = scanResult.diskName;

					SortedMap<Path, List<Path>> scanToExcludeListMap = diskToPathMap.get(diskName);
					if (scanToExcludeListMap != null) {
						Path scanPath = scanToExcludeListMap.firstKey();
						List<Path> excludePathList = scanToExcludeListMap.get(scanPath);
						scanToExcludeListMap.remove(scanPath);
						if (scanToExcludeListMap.isEmpty()) {
							diskToPathMap.remove(diskName);
						}
						executorCompletionService.submit(new PlainFileVisitorTask(scanPath, excludePathList, diskName));
						taskCounter++;
					}
				} else {
					break;
				}
			} catch (InterruptedException | ExecutionException ex) {
				System.err.println("ERROR: " + ex);
				taskCounter--;
			}
		}

		executorService.shutdown();

		System.out.println("start sort result list, size: " + resultPathList.size() + " " + new Timestamp(System.currentTimeMillis()));
		Collections.sort(resultPathList, new Comparator<FileInfo>() {
			@Override
			public int compare(FileInfo o1, FileInfo o2) {
				return o1.absPath.compareTo(o2.absPath);
			}
		});

		System.out.println("finish sort result list" + new Timestamp(System.currentTimeMillis()));

		System.out.println("start write to disk " + new Timestamp(System.currentTimeMillis()));
		Utils.writePathListToFileExt(scanParams.getOutputFilePath(), resultPathList, scanParams.getOutputFileCharset());
		System.out.println("finish write to disk " + new Timestamp(System.currentTimeMillis()));
	}
}
