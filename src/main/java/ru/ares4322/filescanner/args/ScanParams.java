package ru.ares4322.filescanner.args;

import java.nio.file.Path;
import java.util.List;

/**
 * Параметры сканирования
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ScanParams {

	protected List<Path> scanPathList;

	public List<Path> getSearchPathList() {

		return scanPathList;
	}

	public ScanParams setSearchPathList(List<Path> scanPathList) {
		this.scanPathList = scanPathList;
		return this;
	}
}
