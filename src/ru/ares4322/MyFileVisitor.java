/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ares4322;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.AbstractQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author ares4322
 */
public class MyFileVisitor implements FileVisitor<Path> {

	private PrintWriter writer;
	protected Condition condition;
	protected AbstractQueue<Path> pathQueue;
	protected Lock lock;
	protected Path searchPath;

	MyFileVisitor(PrintWriter writer, Condition condition, AbstractQueue<Path> pathQueue, Lock lock, Path searchPath) {
		this.writer = writer;
		this.condition = condition;
		this.pathQueue = pathQueue;
		this.lock = lock;
		this.searchPath = searchPath;
	}

	/**
	 * @todo здесь нжно сделать проверку пути на excludePaths
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		FileVisitResult result = FileVisitResult.CONTINUE;
		System.out.println(" preVisitDirectory: " + dir.toAbsolutePath());
		if (dir.equals(this.searchPath) == false) {
			this.pathQueue.add(dir);
			try {
				this.lock.lock();
				this.condition.signalAll();
			} finally {
				this.lock.unlock();
			}
			this.writer.println(dir.toRealPath());
			System.out.println("Found directory: " + dir.toRealPath());
			result = FileVisitResult.SKIP_SUBTREE;
		}
		return result;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		System.out.println(" visitFile: " + file.toAbsolutePath());
		FileVisitResult result = FileVisitResult.CONTINUE;
		if (Files.isRegularFile(file)) {
			this.writer.println(file.toRealPath());
		} else if (Files.isSymbolicLink(file)) {
			System.out.println(file.toRealPath() + " is symbolic link (not write)");
		}

		return result;
	}

	/**
	 * @todo здесь нужно сделать вывод сообщения об ошибке в консоль
	 */
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		System.out.println("ERROR: visit failed file " + file.toRealPath());
		return FileVisitResult.CONTINUE;
	}

	//@todo здесь можно сделать сортировку перед сбросом на диск
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		System.out.println(" postVisitDirectory: " + dir.toAbsolutePath());
		try {
			this.lock.lock();
			this.condition.signalAll();
		} finally {
			this.lock.unlock();
		}
		return FileVisitResult.CONTINUE;
	}
}
