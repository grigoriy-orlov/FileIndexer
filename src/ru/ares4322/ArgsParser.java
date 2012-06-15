package ru.ares4322;

import java.util.Arrays;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ArgsParser {

	static public SearchParams parse(String[] args) {
		//разделяем массив аргументов на 2 массива
		int delimeterIndex = Arrays.binarySearch(args, "-");
		String[] searchPaths;
		String[] excludePaths = null;
		if (delimeterIndex < 0) {
			searchPaths = args;
		} else {
			searchPaths = Arrays.copyOfRange(args, 0, delimeterIndex - 1);
			excludePaths = Arrays.copyOfRange(args, delimeterIndex + 1, (args.length - 1));
		}
		return new SearchParams(searchPaths, excludePaths);
	}
}
