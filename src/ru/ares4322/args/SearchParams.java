package ru.ares4322.args;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class SearchParams {

	protected String[] searchPaths;

	public String[] getSearchPaths() {
		return searchPaths;
	}

	public SearchParams setSearchPaths(String[] searchPaths) {
		this.searchPaths = searchPaths;
		return this;
	}
}
