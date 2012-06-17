package ru.ares4322;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ares4322
 */
public class OIOMultithreadedInMemorySearcher implements Searcher {

	@Override
	public void search(String from) {
		Lock lock = null;
		try {
			int processorQuantity = Runtime.getRuntime().availableProcessors();
			ExecutorService executor = Executors.newFixedThreadPool(processorQuantity);
			ConcurrentLinkedQueue<File> pathQueue = new ConcurrentLinkedQueue<>();
			lock = new ReentrantLock();
			Condition condition = lock.newCondition();

			pathQueue.add(new File(from));
			while (true) {
				lock.lock();
				executor.execute(null);
				condition.await();
				lock.unlock();
			}
		} catch (InterruptedException ex) {
			Logger.getLogger(OIOMultithreadedInMemorySearcher.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}
}
