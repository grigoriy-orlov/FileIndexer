package ru.ares4322.filescanner;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import ru.ares4322.filescanner.args.ScanParams;
import ru.ares4322.filescanner.args.SimpleScanParams;

/**
 * рабочий комп , /home/ares4322/Knowledge - менее секунды рабочий комп ,
 * /home/ares4322/work - с выводом в консоль - 300000 файлов, 500 Мб и 10 минут
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class OIOWaitFreeScanner implements FileScanner {

	@Override
	public void scan(ScanParams params, ScanResultOutputParams outputParams) {
		SimpleScanParams searchParams = (SimpleScanParams) params;

		final int processorQuantity = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(processorQuantity);
		AbstractQueue<Path> pathQueue = new ConcurrentLinkedQueue<>();

		List<Path> resultPathList = new LinkedList<>();

		Map<Path, List<Path>> sortedPathMap = searchParams.getExcludePathsToScanPathMap();
		for (Map.Entry<Path, List<Path>> entry : sortedPathMap.entrySet()) {
			Path searchPath = entry.getKey();
			List<Path> excludePathList = entry.getValue();
			pathQueue.add(searchPath);
			List<Future<List<Path>>> futures = new LinkedList<>();
			int processors = processorQuantity;
			while ((processors--) > 0) {
				futures.add(executor.submit(new WaitFreeFileVisitor(pathQueue, excludePathList)));
			}
			for (Iterator<Future<List<Path>>> it = futures.iterator(); it.hasNext();) {
				Future<List<Path>> future = it.next();
				if (future != null) {
					try {
						resultPathList.addAll(future.get());
					} catch (InterruptedException | ExecutionException ex) {
						System.err.println("ERROR: "+ ex.getMessage());
					}
				}
			}
		}
		executor.shutdown();

		Collections.sort(resultPathList);

		Utils.writePathListToFile(outputParams.getOutputFilePath(), resultPathList, outputParams.getOutputFileCharset());
	}
}
