package ru.ares4322;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.ares4322.args.SearchParams;
import ru.ares4322.args.SimpleSearchParams;

/**
 * домашний комп, /home/ares4322/work - хз сколько первый раз и 14 секунд после нескольких запусков рабочий комп ,
 * /home/ares4322/Knowledge - 1 сек
 *
 * @author ares4322
 */
public class OIOForkJoinSearcher implements Searcher {

	@Override
	public void search(SearchParams params) {
		SimpleSearchParams searchParams = (SimpleSearchParams) params;

		PrintWriter writer = null;
		try {
			List<String> resultPathList = new LinkedList<>();
			String[] searchPaths = searchParams.getSearchPaths();

			final int availableProcessors = Runtime.getRuntime().availableProcessors();
			ForkJoinPool forkJoinPool = new ForkJoinPool(availableProcessors);
			for (int i = 0, l = searchPaths.length; i < l; i++) {
				String searchPath = searchPaths[i];
				RecursiveFileVisitor dirVisitorTask = new RecursiveFileVisitor(new File(searchPath));
				List<String> oneParamPathList = forkJoinPool.invoke(dirVisitorTask);
				resultPathList.addAll(oneParamPathList);
			}
			Collections.sort(resultPathList);
			Path resultFile = Files.createFile(Paths.get("/home/ares4322/tmp/result.txt"));
			writer = new PrintWriter(Files.newBufferedWriter(resultFile, Charset.forName("UTF-8")));
			for (Iterator<String> it = resultPathList.iterator(); it.hasNext();) {
				writer.println(it.next());
			}
		} catch (IOException ex) {
			Logger.getLogger(OIOForkJoinSearcher.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
