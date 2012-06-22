package ru.ares4322;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 *
 * @author ares4322
 */
class RecursiveFileVisitor extends RecursiveTask<List<Path>> {

	private Path path;
	private List<Path> excludePathList;

	public RecursiveFileVisitor(Path path, List<Path> excludePathList) {
		this.path = path;
		this.excludePathList = excludePathList;
	}

	@Override
	protected List<Path> compute() {
		List<Path> paths = new LinkedList<>();
		List<RecursiveFileVisitor> subTaskList = new LinkedList<>();

		boolean addPath = Utils.searchPathInList(this.path, this.excludePathList);

		if (addPath) {
			paths.add(this.path.toAbsolutePath());

			System.out.println(this.path.toAbsolutePath());
			if (Files.isDirectory(this.path)) {
				subTaskList.addAll(this.processPath(this.path));
			}

			for (RecursiveFileVisitor visitor : subTaskList) {
				paths.addAll(visitor.join());
			}
		}

		return paths;
	}

	private List<RecursiveFileVisitor> processPath(Path path) {
		List<RecursiveFileVisitor> subTaskList = new LinkedList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
			for (Iterator<Path> it = directoryStream.iterator(); it.hasNext();) {
				Path pathInDirectory = it.next();
				RecursiveFileVisitor visitor = new RecursiveFileVisitor(pathInDirectory, this.excludePathList);
				visitor.fork();
				subTaskList.add(visitor);
			}
		} catch (IOException ex) {
			System.out.println("Fail visit file: " + ex.getMessage());
		}
		return subTaskList;
	}
}
