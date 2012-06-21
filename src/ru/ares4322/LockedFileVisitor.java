package ru.ares4322;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

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

		boolean addPath = Utils.searchPathInList(path, this.excludePathList);

		if (addPath) {
			resultQueue.add(this.path.toAbsolutePath());

			if (Files.isDirectory(this.path)) {
				try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
					for (Iterator<Path> it = directoryStream.iterator(); it.hasNext();) {
						Path pathInDirectory = it.next();
						if (Files.isDirectory(pathInDirectory)) {
							dirs.add(pathInDirectory);
						} else if (Files.isRegularFile(pathInDirectory)) {
							this.resultQueue.add(pathInDirectory.toAbsolutePath());
						}
						//@todo сюда тоже нужна обработка исключения? (когда файл в этой директории не открывается)
					}
				} catch (DirectoryIteratorException ex) {
					System.out.println("Fail visit file: " + ex.getCause().getMessage());
				} catch (IOException ex) {
					Logger.getLogger(RecursiveFileVisitor.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
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
}
