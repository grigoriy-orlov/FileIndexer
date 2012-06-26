package ru.ares4322;

import java.nio.file.Path;
import java.util.AbstractQueue;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import ru.ares4322.args.SearchParams;
import ru.ares4322.args.SimpleSearchParams;

/**
 * рабочий комп , /home/ares4322/Knowledge - 1 мин 24 сек
 *
 * @author ares4322
 */
public class OIOMultithreadedLockedSearcher implements Searcher {

	@Override
	public void search(SearchParams params) {
		SimpleSearchParams searchParams = (SimpleSearchParams) params;

		List<Path> resultPathList = new LinkedList<>();

		final int processorQuantity = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(processorQuantity);
		AbstractQueue<Path> pathQueue = new ConcurrentLinkedQueue<>();
		AbstractQueue<Path> resultQueue = new ConcurrentLinkedQueue<>();
		Lock lock = new ReentrantLock();
		Condition condition = lock.newCondition();

		Map<Path, List<Path>> sortedPathMap = searchParams.getSortedPathMap();
		for (Map.Entry<Path, List<Path>> entry : sortedPathMap.entrySet()) {
			Path searchPath = entry.getKey();
			List<Path> excludePathList = entry.getValue();
			pathQueue.add(searchPath);
			while (pathQueue.isEmpty() == false) {
				Path path = pathQueue.poll();
				//@todo тут не доделал, никуда не передается resultPathList
				executor.execute(new LockedFileVisitor(path, excludePathList, pathQueue, lock, condition, resultQueue));
				lock.lock();
				try {
					condition.await();
				} catch (InterruptedException ex) {
					System.err.println("ERROR: " + ex.getMessage());
				} finally {
					lock.unlock();
				}
			}
		}
		executor.shutdown();

		Collections.sort(resultPathList);

		Utils.writePathListToFileExt2(searchParams.getOutputFilePath(), resultPathList, searchParams.getOutputFileCharset());

	}
}
