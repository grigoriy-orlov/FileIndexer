package ru.ares4322.filescanner;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Класс различных утилит
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class Utils {

	/**
	 * Пишет в файл пути из списка в соответствии с шаблоном из ТЗ
	 *
	 * @param resultFilePath Путь файла, в который необходимо писать
	 * @param pathList Список путей для записи
	 * @param charset Кодировка итогового файла
	 */
	public static void writePathListToFile(Path resultFilePath, List<Path> pathList, Charset charset) {
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
	 * Пишет в файл пути из списка в соответствии с шаблоном из ТЗ
	 *
	 * @param resultFilePath Путь файла, в который необходимо писать
	 * @param pathList Список путей для записи
	 * @param charset Кодировка итогового файла
	 */
	public static void writePathListToFileExt(Path resultFilePath, List<FileInfo> fileInfoList, Charset charset) {
		PrintWriter writer = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
		try {
			Files.deleteIfExists(resultFilePath);
			Path resultFile = Files.createFile(resultFilePath);
			writer = new PrintWriter(Files.newBufferedWriter(resultFile, charset));
			for (Iterator<FileInfo> it = fileInfoList.iterator(); it.hasNext();) {
				StringBuilder stringBuilder = new StringBuilder();
				FileInfo fileInfo = it.next();
				stringBuilder.append("[file = ");
				stringBuilder.append(fileInfo.absPath);
				stringBuilder.append("\ndate = ");
				stringBuilder.append(formatter.format(fileInfo.lastModTime));
				stringBuilder.append("\nsize = ");
				stringBuilder.append(fileInfo.size);
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
	 * @todo разобраться, почему неправльно работает Ищет путь в отсортированном
	 * списке путей
	 *
	 * @param path Путь, который ищем
	 * @param sortedPathList Отсортированный список путей, в котором ищем
	 * @return Возвращает - найден или нет
	 */
	public static boolean searchPathInListNew(Path path, List<Path> sortedPathList) {
		return (Collections.binarySearch(sortedPathList, path) >= 0) ? false : true;
	}

	/**
	 * Ищет путь в списке путей
	 *
	 * @param path Путь, который ищем
	 * @param pathList Список путей, в котором ищем
	 * @return Возвращает - найден или нет
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
