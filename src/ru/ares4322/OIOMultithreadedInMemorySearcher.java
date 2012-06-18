package ru.ares4322;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * рабочий комп , /home/ares4322/Knowledge - 1 мин 24 сек
 * @author ares4322
 */
public class OIOMultithreadedInMemorySearcher implements Searcher {

	@Override
	public void search(String from) {
		PrintWriter writer = null;
		try {
			int processorQuantity = Runtime.getRuntime().availableProcessors();
			ExecutorService executor = Executors.newFixedThreadPool(processorQuantity);
			AbstractQueue<File> pathQueue = new ConcurrentLinkedQueue<>();
			AbstractQueue<String> resultQueue = new ConcurrentLinkedQueue<>();
			Lock lock = new ReentrantLock();
			Condition condition = lock.newCondition();

			pathQueue.add(new File(from));
			while (pathQueue.isEmpty() == false) {
				File file = pathQueue.poll();
				executor.execute(new DirVisitor(file, pathQueue, lock, condition, resultQueue));
				lock.lock();
				try {
					condition.await();
				} catch (InterruptedException ex) {
					Logger.getLogger(OIOMultithreadedInMemorySearcher.class.getName()).log(Level.SEVERE, null, ex);
				} finally {
					lock.unlock();
				}
			}
			executor.shutdown();

			List<String> resultStrings = new ArrayList<>(resultQueue);
			Collections.sort(resultStrings);
			Path resultFile = Files.createFile(Paths.get("/home/ares4322/tmp/result.txt"));
			writer = new PrintWriter(Files.newBufferedWriter(resultFile, Charset.forName("UTF-8")));
			for (Iterator<String> it = resultStrings.iterator(); it.hasNext();) {
				String path = it.next();
				writer.println(path);
			}
		} catch (IOException ex) {
			Logger.getLogger(OIOMultithreadedInMemorySearcher.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
