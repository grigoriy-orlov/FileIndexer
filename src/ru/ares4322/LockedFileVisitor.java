package ru.ares4322;

import java.io.File;
import java.util.AbstractQueue;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class LockedFileVisitor implements Runnable {

	private final AbstractQueue<File> pathQueue;
	private final Lock lock;
	private final Condition condition;
	private final AbstractQueue<String> resultQueue;
	private final File file;

	public LockedFileVisitor(File file, AbstractQueue<File> pathQueue, Lock lock, Condition condition, AbstractQueue<String> resultQueue) {
		this.pathQueue = pathQueue;
		this.lock = lock;
		this.condition = condition;
		this.resultQueue = resultQueue;
		this.file = file;
	}

	@Override
	public void run() {
		List<File> dirs = new LinkedList<>();
		resultQueue.add(this.file.getAbsolutePath());

		if (this.file.isDirectory()) {
			File[] files = this.file.listFiles();
			if (files != null) {
				for (int i = 0, l = files.length; i < l; i++) {
					File childFile = files[i];
					if (childFile.isDirectory()) {
						dirs.add(childFile);
					} else if (childFile.isFile()) {
						resultQueue.add(childFile.getAbsolutePath());
					}
				}
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
