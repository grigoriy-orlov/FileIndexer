package ru.ares4322;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class SearchParams {

	public String[] searchPaths;
	public String[] excludePaths;

	public SearchParams(String[] searchPaths, String[] excludePaths) {
		this.searchPaths = searchPaths;
		this.excludePaths = excludePaths;
	}
}
