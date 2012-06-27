package ru.ares4322.filescanner;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import ru.ares4322.filescanner.args.SearchParams;
import ru.ares4322.filescanner.args.SimpleSearchParams;

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

		Utils.writePathListToFileExt2(searchParams.getOutputFilePath(), resultPathList, searchParams.getOutputFileCharset());

	}
}