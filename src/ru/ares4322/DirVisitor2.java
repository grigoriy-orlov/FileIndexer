package ru.ares4322;

import java.io.File;
import java.util.AbstractQueue;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
class DirVisitor2 implements Callable<List<String>> {

	private final AbstractQueue<File> pathQueue;
	private final LinkedList<String> results;
	private static byte classCounter=1;
	private byte classNumber;

	public DirVisitor2(AbstractQueue<File> pathQueue) {
		this.pathQueue = pathQueue;
		this.results = new LinkedList<>();
		classNumber = classCounter;
		classCounter++;
		//System.out.println("start: "+classNumber);
	}

	@Override
	public List<String> call() throws Exception {

		while (this.pathQueue.isEmpty() == false) {
			File file = this.pathQueue.poll();

			this.results.add(file.getAbsolutePath());

			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files != null) {
					for (int i = 0, l = files.length; i < l; i++) {
						File childFile = files[i];
						if (childFile.isDirectory()) {
							this.pathQueue.add(childFile);
						} else if (childFile.isFile()) {
							this.results.add(childFile.getAbsolutePath());
						}
					}
				}
			}
		}
		return this.results;
	}
}
