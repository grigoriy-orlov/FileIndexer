package ru.ares4322;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.ares4322.args.SearchParams;
import ru.ares4322.args.SimpleSearchParams;

/**
 * домашний комп, /home/ares4322/work - хз сколько первый раз и 14 секунд после
 * нескольких запусков рабочий комп , /home/ares4322/Knowledge - 1 сек
 *
 * @author ares4322
 */
public class OIOForkJoinSearcher implements Searcher {

	@Override
	public void search(SearchParams params) {
		SimpleSearchParams searchParams = (SimpleSearchParams) params;

		PrintWriter writer = null;
		try {
			List<Path> resultPathList = new LinkedList<>();

			final int availableProcessors = Runtime.getRuntime().availableProcessors();
			ForkJoinPool forkJoinPool = new ForkJoinPool(availableProcessors);
			Map<Path, List<Path>> sortedPathMap = searchParams.getSortedPathMap();
			for (Map.Entry<Path, List<Path>> entry : sortedPathMap.entrySet()) {
				Path searchPath = entry.getKey();
				List<Path> excludePathList = entry.getValue();
				RecursiveFileVisitor dirVisitorTask = new RecursiveFileVisitor(searchPath, excludePathList);
				List<Path> oneParamPathList = forkJoinPool.invoke(dirVisitorTask);
				resultPathList.addAll(oneParamPathList);
			}
			Collections.sort(resultPathList);

			Utils.writePathListToFileExt("/home/ares4322/tmp/result.txt", resultPathList, "UTF-8");
		} catch (IOException ex) {
			Logger.getLogger(OIOForkJoinSearcher.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
