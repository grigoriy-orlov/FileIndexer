package ru.ares4322;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 2 мин 16 сек первый раз и 9 секунд после нескольких запусков для /home/ares4322/work
 *
 * @author ares4322
 */
public class NIOSingleThreadedMemorySearcher implements Searcher {

	@Override
	public void search(String from) {
		PrintWriter writer = null;
		try {
			int pathArrayLength = 10000000;
			String[] pathArray = new String[pathArrayLength];
			Files.walkFileTree(Paths.get(from), new JustFileVisitor(pathArray));
			int indexOfLastNotNullElement = (pathArrayLength - 1);
			for (int i = (pathArrayLength - 1), l = -1; i > l; i--) {
				if (pathArray[i] != null) {
					indexOfLastNotNullElement = i;
					break;
				}
			}
			//либо можно не удалять, а заполнить сначала пустыми строками и во время печати в исходный файл такие элементы пропускать
			pathArray = Arrays.copyOfRange(pathArray, 0, indexOfLastNotNullElement, String[].class);
			Arrays.sort(pathArray);
			Path resultFile = Files.createFile(Paths.get("/home/ares4322/tmp/result.txt"));
			writer = new PrintWriter(Files.newBufferedWriter(resultFile, Charset.forName("UTF-8")));
			for (int i = 0, l = pathArray.length; i < l; i++) {
				writer.println(pathArray[i]);
			}
		} catch (IOException ex) {
			Logger.getLogger(NIOSingleThreadedMemorySearcher.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
