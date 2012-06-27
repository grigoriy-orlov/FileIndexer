package ru.ares4322.filescanner.args;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Параметры сканирования
 *
 * @author ares4322
 */
public class SimpleScanParams extends ScanParams {

	protected List<Path> excludePathList;
	protected Map<Path, List<Path>> excludePathsToScanPathMap;

	public List<Path> getExcludePathList() {
		return excludePathList;
	}

	public SimpleScanParams setExcludePathList(List<Path> excludePathList) {
		this.excludePathList = excludePathList;
		return this;
	}

	public Map<Path, List<Path>> getExcludePathsToScanPathMap() {
		return excludePathsToScanPathMap;
	}

	public void setExcludePathsToScanPathMap(Map<Path, List<Path>> excludePathsToScanPathMap) {
		this.excludePathsToScanPathMap = excludePathsToScanPathMap;
	}
}
