package ru.ares4322.args;

/**
 *
 * @author ares4322
 */
public class SimpleSearchParams extends SearchParams {

	protected String[] excludePaths;

	public String[] getExcludePaths() {
		return excludePaths;
	}

	public SimpleSearchParams setExcludePaths(String[] excludePaths) {
		this.excludePaths = excludePaths;
		return this;
	}
}
