package ru.ares4322;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import ru.ares4322.args.SearchParams;
import ru.ares4322.args.SimpleSearchParams;

/**
 * рабочий комп , /home/ares4322/Knowledge - менее секунды рабочий комп ,
 * /home/ares4322/work - с выводом в консоль - 300000 файлов, 500 Мб и 10 минут
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class OIOMultithreadedWaitFreeSearcher implements Searcher {

	@Override
	public void search(SearchParams params) {
		SimpleSearchParams searchParams = (SimpleSearchParams) params;

		final int processorQuantity = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(processorQuantity);
		AbstractQueue<Path> pathQueue = new ConcurrentLinkedQueue<>();

		List<Path> resultPathList = new LinkedList<>();

		Map<Path, List<Path>> sortedPathMap = searchParams.getSortedPathMap();
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

		Utils.writePathListToFileExt2(searchParams.getOutputFilePath(), resultPathList, searchParams.getOutputFileCharset());
	}
}
