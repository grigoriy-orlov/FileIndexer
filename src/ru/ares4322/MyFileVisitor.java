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

/**
 *
 * @author ares4322
 */
public class MyFileVisitor implements FileVisitor<Path> {

	private PrintWriter writer;
	protected Condition condition;
	protected AbstractQueue<Path> pathQueue;

	MyFileVisitor(PrintWriter writer, Condition condition, AbstractQueue<Path> pathQueue) {
		this.writer = writer;
		this.condition = condition;
		this.pathQueue = pathQueue;
	}

	/**
	 * @todo здесь нжно сделать проверку пути на excludePaths
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		FileVisitResult result = FileVisitResult.CONTINUE;
		if (Files.isDirectory(file)) {
			this.pathQueue.add(file);
			this.condition.signalAll();
			this.writer.println(file.toRealPath());
		} else if (Files.isRegularFile(file)) {
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
		System.out.println("ERROR: visit failed file "+file.toRealPath());
		return FileVisitResult.CONTINUE;
	}

	//@todo здесь можно сделать сортировку перед сбросом на диск
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
}
