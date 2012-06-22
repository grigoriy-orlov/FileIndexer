package ru.ares4322;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
class WaitFreeFileVisitor implements Callable<List<Path>> {

	private AbstractQueue<Path> pathQueue;
	private LinkedList<Path> resultList;
	private List<Path> excludePathList;

	public WaitFreeFileVisitor(AbstractQueue<Path> pathQueue, List<Path> excludePathList) {
		this.pathQueue = pathQueue;
		this.resultList = new LinkedList<>();
		this.excludePathList = excludePathList;
	}

	@Override
	public List<Path> call() {

		while (this.pathQueue.isEmpty() == false) {
			Path path = this.pathQueue.poll();

			this.resultList.add(path.toAbsolutePath());

			if (Files.isDirectory(path)) {
				this.processPath(path, this.pathQueue, this.resultList);
			}
		}
		return this.resultList;
	}

	private void processPath(Path path, AbstractQueue<Path> pathQueue, LinkedList<Path> resultList) {
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
			for (Iterator<Path> it = directoryStream.iterator(); it.hasNext();) {
				Path pathInDirectory = it.next();
				boolean addPath = Utils.searchPathInList(pathInDirectory, this.excludePathList);
				if (addPath) {
					if (Files.isDirectory(pathInDirectory)) {
						pathQueue.add(pathInDirectory);
					} else if (Files.isRegularFile(pathInDirectory)) {
						resultList.add(pathInDirectory.toAbsolutePath());
					}
				} else {
					System.out.println(pathInDirectory);
				}
			}
		} catch (IOException ex) {
			System.out.println("Fail visit file: " + ex.getMessage());
		}
	}
}