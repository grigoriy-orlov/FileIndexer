package ru.ares4322;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class Utils {

	public static void writePathListToFile(String resultFilePath, List<String> pathList, String charset) {
		PrintWriter writer = null;
		try {
			Path resultFile = Files.createFile(Paths.get(resultFilePath));
			writer = new PrintWriter(Files.newBufferedWriter(resultFile, Charset.forName(charset)));
			for (Iterator<String> it = pathList.iterator(); it.hasNext();) {
				writer.println(it.next());
			}
		} catch (IOException ex) {
			System.err.println("ERROR: " + ex.getMessage());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static void writePathListToFileExt(String resultFilePath, List<Path> pathList, String charset) {
		PrintWriter writer = null;
		try {
			Files.deleteIfExists(Paths.get(resultFilePath));
			Path resultFile = Files.createFile(Paths.get(resultFilePath));
			writer = new PrintWriter(Files.newBufferedWriter(resultFile, Charset.forName(charset)));
			for (Iterator<Path> it = pathList.iterator(); it.hasNext();) {
				writer.println(it.next().toAbsolutePath());
			}
		} catch (IOException ex) {
			System.err.println("ERROR: " + ex.getMessage());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static void writePathListToFileExt2(Path resultFilePath, List<Path> pathList, Charset charset) {
		PrintWriter writer = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
		try {
			Files.deleteIfExists(resultFilePath);
			Path resultFile = Files.createFile(resultFilePath);
			writer = new PrintWriter(Files.newBufferedWriter(resultFile, charset));
			for (Iterator<Path> it = pathList.iterator(); it.hasNext();) {
				StringBuilder stringBuilder = new StringBuilder();
				Path path = it.next();
				stringBuilder.append("[file = ");
				stringBuilder.append(path);
				stringBuilder.append("\ndate = ");
				stringBuilder.append(formatter.format(new Date(Files.getLastModifiedTime(path).toMillis())));
				stringBuilder.append("\nsize = ");
				stringBuilder.append(Files.size(path));
				stringBuilder.append("]");
				writer.println(stringBuilder);
			}
		} catch (IOException ex) {
			System.err.println("ERROR: " + ex.getMessage());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * @todo Можно попробовать заменить перебор Collections.binarySearch()
	 * @todo Так же можно сделать здесь удаление найденного элемента
	 */
	public static boolean searchPathInList(Path path, List<Path> pathList) {
		boolean result = true;
		for (Iterator<Path> it = pathList.iterator(); it.hasNext();) {
			Path excludePath = it.next();
			if (path.equals(excludePath)) {
				result = false;
				break;
			}
		}
		return result;
	}
}
