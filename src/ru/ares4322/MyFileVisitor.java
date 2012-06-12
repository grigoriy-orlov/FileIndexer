/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ares4322;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author ares4322
 */
public class MyFileVisitor implements FileVisitor<Path> {

	private Path tmpFile;
	private Charset charset;
	private PrintWriter writer;

	MyFileVisitor(PrintWriter writer){
		this.writer = writer;
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
		this.writer.println(file.getFileName());
		
		return FileVisitResult.CONTINUE;
	}

	/**
	 * @todo здесь нужно сделать вывод сообщения об ошибке в консоль
	 */
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
}
