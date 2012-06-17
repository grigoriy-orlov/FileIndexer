/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ares4322;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 *
 * @author ares4322
 */
class DirVisitorTask extends RecursiveTask<List<String>> {

	private final File file;

	public DirVisitorTask(File file) {
		this.file = file;
	}

	@Override
	protected List<String> compute() {
		List<String> paths = new LinkedList<>();
		List<DirVisitorTask> subTasks = new LinkedList<>();

		paths.add(this.file.getAbsolutePath());

		if (this.file.isDirectory()) {
			File[] files = this.file.listFiles();
			for (int i = 0, l = files.length; i < l; i++) {
				File childFile = files[i];
				DirVisitorTask visitor = new DirVisitorTask(childFile);
				visitor.fork();
				subTasks.add(visitor);
			}
		}

		for (DirVisitorTask visitor : subTasks) {
			paths.addAll(visitor.join());
		}

		return paths;
	}
}
