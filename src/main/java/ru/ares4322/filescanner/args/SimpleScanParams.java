package ru.ares4322.filescanner.args;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Параметры сканирования
 *
 * @author ares4322
 */
public class SimpleScanParams extends ScanParams {

	protected List<Path> excludePathList;
	protected SortedMap<Path, List<Path>> excludePathsToScanPathMap;
	protected Map<String, SortedMap<Path, List<Path>>> pathMapsToDisk;

	public List<Path> getExcludePathList() {
		return excludePathList;
	}

	public SimpleScanParams setExcludePathList(List<Path> excludePathList) {
		this.excludePathList = excludePathList;
		return this;
	}

	public SortedMap<Path, List<Path>> getExcludePathsToScanPathMap() {
		return excludePathsToScanPathMap;
	}

	public void setExcludePathsToScanPathMap(SortedMap<Path, List<Path>> excludePathsToScanPathMap) {
		this.excludePathsToScanPathMap = excludePathsToScanPathMap;
	}

	public Map<String, SortedMap<Path, List<Path>>> getPathMapsToDisk() {
		return pathMapsToDisk;
	}

	public void setPathMapsToDisk(Map<String, SortedMap<Path, List<Path>>> pathMapsToDisk) {
		this.pathMapsToDisk = pathMapsToDisk;
	}
}
