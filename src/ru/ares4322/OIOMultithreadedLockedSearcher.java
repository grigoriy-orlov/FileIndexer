package ru.ares4322;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractQueue;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
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

		PrintWriter writer = null;
		try {
			List<String> resultPathList = new LinkedList<>();
			String[] searchPaths = searchParams.getSearchPaths();

			final int processorQuantity = Runtime.getRuntime().availableProcessors();
			ExecutorService executor = Executors.newFixedThreadPool(processorQuantity);
			AbstractQueue<File> pathQueue = new ConcurrentLinkedQueue<>();
			AbstractQueue<String> resultQueue = new ConcurrentLinkedQueue<>();
			Lock lock = new ReentrantLock();
			Condition condition = lock.newCondition();

			for (int i = 0, l = searchPaths.length; i < l; i++) {
				String searchPath = searchPaths[i];
				pathQueue.add(new File(searchPath));
				while (pathQueue.isEmpty() == false) {
					File file = pathQueue.poll();
					executor.execute(new LockedFileVisitor(file, pathQueue, lock, condition, resultQueue));
					lock.lock();
					try {
						condition.await();
					} catch (InterruptedException ex) {
						Logger.getLogger(OIOMultithreadedLockedSearcher.class.getName()).log(Level.SEVERE, null, ex);
					} finally {
						lock.unlock();
					}
				}
			}
			executor.shutdown();

			Collections.sort(resultPathList);

			Utils.writePathListToFile("/home/ares4322/tmp/result.txt", resultPathList, "UTF-8");
		} catch (IOException ex) {
			Logger.getLogger(OIOMultithreadedLockedSearcher.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
