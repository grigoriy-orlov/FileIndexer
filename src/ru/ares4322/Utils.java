package ru.ares4322;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class Utils {

	public static void writePathListToFile(String resultFilePath, List<String> pathList, String charset) throws IOException {
		PrintWriter writer = null;
		try {
			Path resultFile = Files.createFile(Paths.get(resultFilePath));
			writer = new PrintWriter(Files.newBufferedWriter(resultFile, Charset.forName(charset)));
			for (Iterator<String> it = pathList.iterator(); it.hasNext();) {
				writer.println(it.next());
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
