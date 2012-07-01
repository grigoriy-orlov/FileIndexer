package ru.ares4322.filescanner;

import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ExtendedScanResult {
	public String diskName;
	public List<Path> tmpPathList;

	public ExtendedScanResult() {
	}

	public ExtendedScanResult(String diskName, List<Path> tmpPathList) {
		this.diskName = diskName;
		this.tmpPathList = tmpPathList;
	}
}
