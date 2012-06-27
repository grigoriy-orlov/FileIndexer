package ru.ares4322.filescanner;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class LockedFileVisitor implements Runnable {

	private final AbstractQueue<Path> pathQueue;
	private final Lock lock;
	private final Condition condition;
	private final AbstractQueue<Path> resultQueue;
	private final Path path;
	private final List<Path> excludePathList;

	public LockedFileVisitor(Path path, List<Path> excludePathList, AbstractQueue<Path> pathQueue, Lock lock, Condition condition, AbstractQueue<Path> resultQueue) {
		this.pathQueue = pathQueue;
		this.lock = lock;
		this.condition = condition;
		this.resultQueue = resultQueue;
		this.path = path;
		this.excludePathList = excludePathList;
	}

	@Override
	public void run() {
		List<Path> dirs = new LinkedList<>();

		resultQueue.add(this.path.toAbsolutePath());

		if (Files.isDirectory(this.path)) {
			this.processPath(this.path, dirs, this.resultQueue);

			if (dirs.isEmpty() == false) {
				pathQueue.addAll(dirs);
			}
			this.lock.lock();
			try {
				condition.signalAll();
			} finally {
				this.lock.unlock();
			}
		}
	}

	private void processPath(Path path, List<Path> dirs, AbstractQueue<Path> resultQueue) {
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
			for (Iterator<Path> it = directoryStream.iterator(); it.hasNext();) {
				Path pathInDirectory = it.next();
				boolean addPath = Utils.searchPathInList(pathInDirectory, this.excludePathList);
				if (addPath) {
					if (Files.isDirectory(pathInDirectory)) {
						dirs.add(pathInDirectory);
					} else if (Files.isRegularFile(pathInDirectory)) {
						resultQueue.add(pathInDirectory.toAbsolutePath());
					}
				}
			}
		}catch (IOException ex) {
			System.out.println("Fail visit file: "+ex.getMessage());
		}
	}
}
