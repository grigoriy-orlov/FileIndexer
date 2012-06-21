package ru.ares4322;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
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
					if(future != null)
						resultPathList.addAll(future.get());
				}
			}
			executor.shutdown();

			Collections.sort(resultPathList);

			Utils.writePathListToFileExt("/home/ares4322/tmp/result.txt", resultPathList, "UTF-8");
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
