package ru.ares4322.filescanner;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collections;
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
	public static void writePathListToFileExt(Path resultFilePath, List<FileInfo> fileInfoList, Charset charset) {
		PrintWriter writer = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
		try {
			Files.deleteIfExists(resultFilePath);
			Path resultFile = Files.createFile(resultFilePath);
			writer = new PrintWriter(Files.newBufferedWriter(resultFile, charset));
			for (Iterator<FileInfo> it = fileInfoList.iterator(); it.hasNext();) {
				FileInfo fileInfo = it.next();
				writer.println("[");
				writer.print("file = ");
				writer.println(fileInfo.absPath);
				writer.print("date = ");
				writer.println(formatter.format(fileInfo.lastModTime));
				writer.print("size = ");
				writer.print(fileInfo.size);
				writer.print("]");
			}
		} catch (IOException ex) {
			System.err.println(new StringBuilder(2).append("ERROR: ").append(ex.getMessage()));
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Ищет путь в списке путей
	 *
	 * @param path Путь, который ищем
	 * @param sortedPathList Отсортированный список путей, в котором ищем
	 * @return Возвращает - найден или нет
	 */
	public static boolean searchPathInListNew(Path path, List<Path> sortedPathList) {
		return (Collections.binarySearch(sortedPathList, path) >= 0) ? true : false;
	}

	/**
	 * Ищет путь в списке путей и удаляет из списка найденный
	 *
	 * @param path Путь, который ищем
	 * @param pathList Список путей, в котором ищем
	 * @return Возвращает - найден или нет
	 */
	public static boolean searchPathInListWithRemove(Path path, List<Path> pathList) {
		boolean result = false;
		for (Iterator<Path> it = pathList.iterator(); it.hasNext();) {
			Path excludePath = it.next();
			if (path.equals(excludePath)) {
				result = true;
				it.remove();
				break;
			}
		}
		return result;
	}

	/**
	 * Возвращает тип операционной системы
	 *
	 * @return Тип операционной системы
	 */
	public static OSTypeEnum getOSName() {
		OSTypeEnum os = OSTypeEnum.UNKNOWN;

		if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
			os = OSTypeEnum.WINDOWS;
		} else if (System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) {
			os = OSTypeEnum.LINUX;
		} else if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) {
			os = OSTypeEnum.MACOS;
		}

		return os;
	}
}
