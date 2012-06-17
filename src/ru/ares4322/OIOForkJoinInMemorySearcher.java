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
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * хз сколько первый раз и 14 секунд после нескольких запусков для /home/ares4322/work
 * @author ares4322
 */
public class OIOForkJoinInMemorySearcher implements Searcher {

	@Override
	public void search(String from) {
		PrintWriter writer = null;
		try {
			List<String> resultStrings = (new ForkJoinPool(Runtime.getRuntime().availableProcessors())).invoke(new DirVisitorTask(new File(from)));
			Collections.sort(resultStrings);
			Path resultFile = Files.createFile(Paths.get("/home/ares4322/tmp/result.txt"));
			writer = new PrintWriter(Files.newBufferedWriter(resultFile, Charset.forName("UTF-8")));
			for (Iterator<String> it = resultStrings.iterator(); it.hasNext();) {
				String path = it.next();
				writer.println(path);
			}
		} catch (IOException ex) {
			Logger.getLogger(OIOForkJoinInMemorySearcher.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
