package ru.ares4322;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ares4322
 */
public class Walker implements Runnable {

	protected Path searchPath;
	protected Path tmpFile;
	protected Charset charset;
	protected Condition condition;
	protected AbstractQueue<Path> pathQueue;
	protected Lock lock;

	public Walker(Path searchPath, Path tmpFile, Charset charset, Condition condition, AbstractQueue<Path> pathQueue, Lock lock) {
		System.out.println("Create walker for path: " + searchPath.toAbsolutePath()+", tmpFile: "+tmpFile.toAbsolutePath());
		this.searchPath = searchPath;
		this.tmpFile = tmpFile;
		this.charset = charset;
		this.pathQueue = pathQueue;
		this.condition = condition;
		this.lock = lock;
	}

	@Override
	public void run() {
		try {
			try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(this.tmpFile, this.charset))) {
				Files.walkFileTree(this.searchPath, new MyFileVisitor(writer, this.condition, this.pathQueue, this.lock, this.searchPath));
			}
		} catch (IOException ex) {
			Logger.getLogger(Walker.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
