package ru.ares4322.args;

import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class SearchParams {

	protected List<Path> searchPathList;

	public List<Path> getSearchPathList() {

		return searchPathList;
	}

	public SearchParams setSearchPathList(List<Path> searchPathList) {
		this.searchPathList = searchPathList;
		return this;
	}
}
