package ru.ares4322.args;

import java.util.Arrays;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class SimpleArgsParser implements ArgsParser {

	@Override
	public SearchParams parse(String[] args) throws ArgsParsingException {
		if (args == null || args.length == 0) {
			throw new ArgsParsingException("you must specify at least one argument");
		}
		int delimeterIndex = Arrays.binarySearch(args, "-");
		String[] searchPaths;
		String[] excludePaths = null;
		if (delimeterIndex < 0) {
			searchPaths = args;
		} else if (delimeterIndex == 0 || delimeterIndex == (args.length - 1)) {
			throw new ArgsParsingException("wrong args format. must be searchPath1 [searchPathsN] [-] [excludePath1] [[excludePathN]");
		} else {
			searchPaths = Arrays.copyOfRange(args, 0, delimeterIndex - 1);
			excludePaths = Arrays.copyOfRange(args, delimeterIndex + 1, (args.length - 1));
		}
		return (new SimpleSearchParams()).setExcludePaths(excludePaths).setSearchPaths(searchPaths);
	}
}
