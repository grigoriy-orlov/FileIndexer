package ru.ares4322;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractQueue;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * рабочий комп , /home/ares4322/Knowledge - менее секунды
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class OIOMultithreadedInMemorySearcher2 implements Searcher {

	@Override
	public void search(String from) {
		PrintWriter writer = null;
		try {
			int processorQuantity = Runtime.getRuntime().availableProcessors();
			ExecutorService executor = Executors.newFixedThreadPool(processorQuantity);
			AbstractQueue<File> pathQueue = new ConcurrentLinkedQueue<>();

			pathQueue.add(new File(from));
			List<Future<List<String>>> futures = new LinkedList<>();

			int i = processorQuantity;
			while ((i--) > 0) {
				futures.add(executor.submit(new DirVisitor2(pathQueue)));
			}
			executor.shutdown();

			List<String> resultStrings = new LinkedList<>();
			for (Iterator<Future<List<String>>> it = futures.iterator(); it.hasNext();) {
				Future<List<String>> future = it.next();
				try {
					resultStrings.addAll(future.get());
				} catch (InterruptedException | ExecutionException ex) {
					Logger.getLogger(OIOMultithreadedInMemorySearcher2.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

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
