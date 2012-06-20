package ru.ares4322;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 *
 * @author ares4322
 */
class RecursiveFileVisitor extends RecursiveTask<List<String>> {

	private final File file;
	private List<String> excludePathList;

	public RecursiveFileVisitor(File file, List<String> excludePaths) {
		this.file = file;
		this.excludePathList = excludePaths;
	}

	@Override
	protected List<String> compute() {
		List<String> paths = new LinkedList<>();
		List<RecursiveFileVisitor> subTasks = new LinkedList<>();

				paths.add(this.file.getAbsolutePath());

		if (this.file.isDirectory()) {
			File[] files = this.file.listFiles();
			for (int i = 0, l = files.length; i < l; i++) {
				File childFile = files[i];
				RecursiveFileVisitor visitor = new RecursiveFileVisitor(childFile, this.excludePathList);
				visitor.fork();
				subTasks.add(visitor);
			}
		}

		for (RecursiveFileVisitor visitor : subTasks) {
			paths.addAll(visitor.join());
		}

		return paths;
	}
}
