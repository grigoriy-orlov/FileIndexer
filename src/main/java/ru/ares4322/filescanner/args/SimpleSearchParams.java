package ru.ares4322.filescanner.args;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ares4322
 */
public class SimpleSearchParams extends SearchParams {

	protected List<Path> excludePathList;

	protected Map<Path, List<Path>> sortedPathMap;

	public List<Path> getExcludePathList() {
		return excludePathList;
	}

	public SimpleSearchParams setExcludePathList(List<Path> excludePathList) {
		this.excludePathList = excludePathList;
		return this;
	}

	public Map<Path, List<Path>> getSortedPathMap() {
		return sortedPathMap;
	}

	public void setSortedPathMap(Map<Path, List<Path>> sortedPathMap) {
		this.sortedPathMap = sortedPathMap;
	}
}
