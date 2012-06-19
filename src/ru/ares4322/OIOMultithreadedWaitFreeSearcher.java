package ru.ares4322;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.ares4322.args.SearchParams;
import ru.ares4322.args.SimpleSearchParams;

/**
 * рабочий комп , /home/ares4322/Knowledge - менее секунды
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class OIOMultithreadedWaitFreeSearcher implements Searcher {

	@Override
	public void search(SearchParams params) {
		SimpleSearchParams searchParams = (SimpleSearchParams) params;

		PrintWriter writer = null;
		try {
			final int processorQuantity = Runtime.getRuntime().availableProcessors();
			ExecutorService executor = Executors.newFixedThreadPool(processorQuantity);
			AbstractQueue<File> pathQueue = new ConcurrentLinkedQueue<>();

			List<String> resultPathList = new LinkedList<>();

			String[] searchPaths = searchParams.getSearchPaths();
			for (int i = 0, l = searchPaths.length; i < l; i++) {
				String searchPath = searchPaths[i];
				pathQueue.add(new File(searchPath));
				List<Future<List<String>>> futures = new LinkedList<>();

				int processors = processorQuantity;
				while ((processors--) > 0) {
					futures.add(executor.submit(new WaitFreeFileVisitor(pathQueue)));
				}
				for (Iterator<Future<List<String>>> it = futures.iterator(); it.hasNext();) {
					Future<List<String>> future = it.next();
					resultPathList.addAll(future.get());
				}
			}
			executor.shutdown();

			Collections.sort(resultPathList);
			Path resultFile = Files.createFile(Paths.get("/home/ares4322/tmp/result.txt"));
			writer = new PrintWriter(Files.newBufferedWriter(resultFile, Charset.forName("UTF-8")));
			for (Iterator<String> it = resultPathList.iterator(); it.hasNext();) {
				writer.println(it.next());
			}
		} catch (InterruptedException | ExecutionException ex) {
			Logger.getLogger(OIOMultithreadedWaitFreeSearcher.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(OIOMultithreadedLockedSearcher.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
