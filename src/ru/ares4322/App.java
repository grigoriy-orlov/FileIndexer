package ru.ares4322;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 1 способ - 
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class App {

	/**
	 * @param args the command line arguments
	 */
	//@todo сделать обработку IOException
	public static void main(String[] args) throws IOException {
		for (int i = 0; i < args.length; i++) {
			String string = args[i];
			Path path = Paths.get(URI.create(string));
			boolean exists = Files.exists(path, LinkOption.NOFOLLOW_LINKS);
			StringBuilder stringBuilder = new StringBuilder(6);
			stringBuilder.append(path).append(": ");
			if (exists) {
				stringBuilder.append("exists, size: ").append(Files.size(path)).append(", last modified time: ").append(Files.readAttributes(path, BasicFileAttributes.class).lastModifiedTime());
			} else {
				stringBuilder.append("not exists");
			}
			System.out.println(stringBuilder);
		}

	}
}
