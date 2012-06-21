package ru.ares4322;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		List<RecursiveFileVisitor> subTasks = new LinkedList<>();

		boolean addPath = Utils.searchPathInList(this.path, this.excludePathList);

		if (addPath) {
			paths.add(this.path.toAbsolutePath());

			if (Files.isDirectory(this.path)) {
				try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(this.path)) {
					for (Iterator<Path> it = directoryStream.iterator(); it.hasNext();) {
						Path pathInDirectory = it.next();
						RecursiveFileVisitor visitor = new RecursiveFileVisitor(pathInDirectory, this.excludePathList);
						visitor.fork();
						subTasks.add(visitor);
						//@todo сюда тоже нужна обработка исключения? (когда файл в этой директории не открывается)
					}
				} catch (DirectoryIteratorException ex) {
					System.out.println("Fail visit file: " + ex.getCause().getMessage());
				} catch (IOException ex) {
					Logger.getLogger(RecursiveFileVisitor.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

			for (RecursiveFileVisitor visitor : subTasks) {
				paths.addAll(visitor.join());
			}
		}

		return paths;
	}
}
