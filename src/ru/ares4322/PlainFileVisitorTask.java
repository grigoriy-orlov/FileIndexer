package ru.ares4322;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class PlainFileVisitorTask implements Callable<List<Path>> {

	protected List<Path> excludePathList;
	private final Path searchPath;

	public PlainFileVisitorTask(Path searchPath, List<Path> excludePathList) {
		this.searchPath = searchPath;
		this.excludePathList = excludePathList;
	}

	@Override
	public List<Path> call() throws Exception {
		List<Path> searchPathList = new LinkedList<>();
		Files.walkFileTree(searchPath, new PlainFileVisitor(searchPathList, excludePathList));
		return searchPathList;
	}
}
