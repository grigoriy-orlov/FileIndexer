package ru.ares4322;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.ares4322.args.SearchParams;
import ru.ares4322.args.SimpleSearchParams;

/**
 * домашний комп, /home/ares4322/work - 2 мин 16 сек первый раз и 9 секунд после
 * нескольких запусков рабочий комп, /home/ares4322/Knowledge - 1 сек рабочий
 * комп, /home/ares4322 (654320 строк) - 5 минут 6 секунд
 *
 * @author ares4322
 */
public class NIOSingleThreadedSearcher implements Searcher {

	@Override
	public void search(SearchParams params) {
		SimpleSearchParams searchParams = (SimpleSearchParams) params;

		PrintWriter writer = null;
		try {
			List<Path> resultPathList = new LinkedList<>();

			Map<Path, List<Path>> sortedPathMap = searchParams.getSortedPathMap();
			for (Map.Entry<Path, List<Path>> entry : sortedPathMap.entrySet()) {
				Path searchPath = entry.getKey();
				List<Path> excludePathList = entry.getValue();
				List<Path> oneParamPathResultList = new LinkedList<>();
				Files.walkFileTree(searchPath, new PlainFileVisitor(oneParamPathResultList, excludePathList));
				resultPathList.addAll(oneParamPathResultList);
			}

			Collections.sort(resultPathList);

			Utils.writePathListToFileExt("/home/ares4322/tmp/result.txt", resultPathList, "UTF-8");

		} catch (IOException ex) {
			Logger.getLogger(NIOSingleThreadedSearcher.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
