package ru.ares4322;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import ru.ares4322.args.SearchParams;
import ru.ares4322.args.SimpleSearchParams;

/**
 * домашний комп, /home/ares4322/work - 2 мин 16 сек первый раз и 9 секунд после
 * нескольких запусков рабочий комп, /home/ares4322/Knowledge - 1 сек рабочий
 * комп, /home/ares4322 (654320 строк) - 5 минут 6 секунд
 *
 * толк от многопоточности будет, только если исходные папки находятся на разных
 * физических дисках.
 *
 * @author ares4322
 */
public class NIOSingleThreadedSearcher implements Searcher {

	@Override
	public void search(SearchParams params) {
		SimpleSearchParams searchParams = (SimpleSearchParams) params;

		List<Path> resultPathList = new LinkedList<>();

		final int processorQuantity = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(processorQuantity);
		Map<Path, List<Path>> sortedPathMap = searchParams.getSortedPathMap();
		List<Future<List<Path>>> futures = new LinkedList<>();

		for (Map.Entry<Path, List<Path>> entry : sortedPathMap.entrySet()) {
			Path searchPath = entry.getKey();
			List<Path> excludePathList = entry.getValue();
			futures.add(executor.submit(new PlainFileVisitorTask(searchPath, excludePathList)));
		}

		for (Iterator<Future<List<Path>>> it = futures.iterator(); it.hasNext();) {
			Future<List<Path>> future = it.next();
			if (future != null) {
				try {
					resultPathList.addAll(future.get());
				} catch (InterruptedException | ExecutionException ex) {
					System.err.println("ERROR: " + ex.getMessage());
				}
			}
		}
		executor.shutdown();

		Collections.sort(resultPathList);

		Utils.writePathListToFileExt2(searchParams.getOutputFilePath(), resultPathList, searchParams.getOutputFileCharset());
	}
}
