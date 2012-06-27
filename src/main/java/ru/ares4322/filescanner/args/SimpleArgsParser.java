package ru.ares4322.filescanner.args;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Парсер параметров командной строки
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class SimpleArgsParser implements ArgsParser {

	/**
	 * Разбирает параметры командной строки и преобразует их в SimpleScanParams.
	 * В случае ошибки бросает ArgsParsingException
	 *
	 * @param args Массив параметров командной строки
	 * @return ScanParams
	 * @throws ArgsParsingException
	 */
	@Override
	public ScanParams parse(String[] args) throws ArgsParsingException {
		if (args == null || args.length == 0) {
			throw new ArgsParsingException("you must specify at least one argument");
		}
		int delimeterIndex = Arrays.binarySearch(args, "-");
		String[] searchPaths;
		String[] excludePaths = null;
		List<Path> searchPathList;
		List<Path> excludePathList;
		if (delimeterIndex < 0) {
			searchPathList = this.pathArrayToList(args);
			excludePathList = this.pathArrayToList(excludePaths);
		} else if (delimeterIndex == 0 || delimeterIndex == (args.length - 1)) {
			throw new ArgsParsingException("wrong args format. must be searchPath1 ... [searchPathsN] [-] [excludePath1] ... [excludePathN]");
		} else {
			searchPaths = Arrays.copyOfRange(args, 0, delimeterIndex);
			excludePaths = Arrays.copyOfRange(args, delimeterIndex + 1, args.length);
			searchPathList = this.pathArrayToList(searchPaths);
			excludePathList = this.pathArrayToList(excludePaths);
		}
		return (new SimpleScanParams()).setExcludePathList(excludePathList).setSearchPathList(searchPathList);
	}

	/**
	 * Преобразует массив путей файлов(String) в список путей (Path)
	 *
	 * @param pathArray Массив путей файлов (String)
	 * @return Список путей (Path)
	 */
	private List<Path> pathArrayToList(String[] pathArray) {
		List<Path> pathList = new LinkedList<>();

		if (pathArray != null) {
			for (int i = 0, l = pathArray.length; i < l; i++) {
				String searchPathName = pathArray[i];
				Path searchPath = Paths.get(searchPathName).toAbsolutePath().normalize();
				pathList.add(searchPath);
			}
		}

		return pathList;
	}
}
