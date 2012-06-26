package ru.ares4322.args;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author ares4322
 */
public class SimpleParamsProcessor implements ParamsProcessor {

	@Override
	public SearchParams process(SearchParams params) throws ParamsProcessingException {
		SimpleSearchParams searchParams = (SimpleSearchParams) params;

		if (searchParams == null) {
			throw new ParamsProcessingException("search params is null");
		}
		if (searchParams.searchPathList == null) {
			throw new ParamsProcessingException("search path list is null");
		}
		if (searchParams.excludePathList == null) {
			throw new ParamsProcessingException("exclude path list is null");
		}
		Collections.sort(searchParams.searchPathList);
		Collections.sort(searchParams.excludePathList);

		this.removePathRedunduncy(searchParams.searchPathList);
		this.removePathRedunduncy(searchParams.excludePathList);

		searchParams.setSortedPathMap(this.getSortedPathList(searchParams.searchPathList, searchParams.excludePathList));

		return searchParams;
	}

	protected List<Path> removePathRedunduncy(List<Path> pathList) {
		for (ListIterator<Path> extIt = pathList.listIterator(); extIt.hasNext();) {
			Path extSearchPath = extIt.next();
			for (ListIterator<Path> intIt = pathList.listIterator(extIt.nextIndex()); intIt.hasNext();) {
				Path intSearchPath = intIt.next();
				if (intSearchPath.toAbsolutePath().startsWith(extSearchPath.toAbsolutePath())) {
					intIt.remove();
				}
			}
		}
		return pathList;
	}

	/**
	 * @todo можно оптимизировать, если не сравнивать все со всеми, а идти
	 * параллельно по спискам и сравнивать сначала лексикографически(так как
	 * списки отсортированы) SortedMap используется, так как если файлы для
	 * поиска будут отсортированы, то и конечная суммарная последовательность
	 * файлов будет изначально более отсортированная
	 */
	protected SortedMap<Path, List<Path>> getSortedPathList(List<Path> searchPathList, List<Path> excludePathList) {
		SortedMap<Path, List<Path>> resultMap = new TreeMap<>();
		boolean put = true;

		for (ListIterator<Path> searchPathIt = searchPathList.listIterator(); searchPathIt.hasNext();) {
			Path searchPath = searchPathIt.next();
			List<Path> connectedExcludePathList = new LinkedList<>();
			put = true;
			for (ListIterator<Path> excludePathIt = excludePathList.listIterator(); excludePathIt.hasNext();) {
				Path excludePath = excludePathIt.next();
				if (excludePath.compareTo(searchPath) > 0 && excludePath.toAbsolutePath().startsWith(searchPath.toAbsolutePath())) {
					//если путь исключения начинается с пути поиска, то этот путь поиска добавляем в поиск
					connectedExcludePathList.add(excludePath);
				} else if (excludePath.compareTo(searchPath) <= 0 && searchPath.toAbsolutePath().startsWith(excludePath.toAbsolutePath())) {
					//если путь исключения охватывает путь поиска, то этот путь поиска не добавляем в поиск
					put = false;
					break;
				}
			}
			if (put) {
				resultMap.put(searchPath, connectedExcludePathList);
			}
		}

		return resultMap;
	}
}
