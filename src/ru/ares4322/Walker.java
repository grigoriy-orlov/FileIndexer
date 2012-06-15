package ru.ares4322;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractQueue;
import java.util.concurrent.locks.Condition;
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

	public Walker(Path searchPath, Path tmpFile, Charset charset, Condition condition, AbstractQueue<Path> pathQueue) {
		System.out.println("--> Create walker");
		this.searchPath = searchPath;
		this.tmpFile = tmpFile;
		this.charset = charset;
		this.pathQueue = pathQueue;
	}

	@Override
	public void run() {
		try {
			PrintWriter writer = new PrintWriter(Files.newBufferedWriter(this.tmpFile, this.charset));
			Files.walkFileTree(this.searchPath, new MyFileVisitor(writer, this.condition, this.pathQueue));
			writer.close();
		} catch (IOException ex) {
			Logger.getLogger(Walker.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
