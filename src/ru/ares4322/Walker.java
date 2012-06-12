package ru.ares4322;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ares4322
 */
public class Walker implements Runnable {

	private Path searchPath;
	private Path tmpFile;
	private Charset charset;

	public Walker(Path searchPath, Path tmpFile, Charset charset) {
		this.searchPath = searchPath;
		this.tmpFile = tmpFile;
		this.charset = charset;
	}

	@Override
	public void run() {
		try {
			PrintWriter writer = new PrintWriter(Files.newBufferedWriter(this.tmpFile, this.charset));
			Files.walkFileTree(this.searchPath, new MyFileVisitor(writer));
			writer.close();
		} catch (IOException ex) {
			Logger.getLogger(Walker.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
