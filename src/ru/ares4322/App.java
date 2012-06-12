package ru.ares4322;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 1 способ -
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class App {

	//@todo сделать обработку IOException
	public static void main(String[] args) throws IOException {

		//разделяем массив аргументов на 2 массива
		int delimeterIndex = Arrays.binarySearch(args, "-");
		String[] searchPaths;
		String[] excludePaths = null;
		if (delimeterIndex < 0) {
			searchPaths = args;
		} else {
			searchPaths = Arrays.copyOfRange(args, 0, delimeterIndex - 1);
			excludePaths = Arrays.copyOfRange(args, delimeterIndex + 1, (args.length - 1));
		}

		//сортируем эти массивы
		Arrays.sort(searchPaths);
		if (excludePaths != null) {
			Arrays.sort(excludePaths);
		}

		//удаляем избыточность путей
		searchPaths = removePathRedundancy(searchPaths);

		String tmpDirPath = "/home/ares4322/tmp/";
		Path tmpDir = Files.createDirectory(Paths.get(tmpDirPath));
		ExecutorService threadPool = Executors.newFixedThreadPool(5);
		//делаем перебор путей поиска
		int tmpFileIndex = 0;
		Charset charset = Charset.forName("UTF-8");
		for (int i = 0; i < searchPaths.length; i++) {
			Path tmpFile = Files.createFile(Paths.get(tmpDirPath + tmpFileIndex));
			threadPool.execute(new Walker(Paths.get(searchPaths[i]), tmpFile, charset));
		}

		Path finalFile = Paths.get(tmpDirPath + "final.txt");
		//@todo склеиваем файлы в один большой
		for (int i = 0, l = tmpFileIndex; i < l; i++) {
			Files.write(finalFile, Files.readAllBytes(Paths.get(tmpDirPath + i)));
		}


	}

	/**
	 * @todo подумать, как тут можно оптимизировать
	 */
	public static String[] removePathRedundancy(String[] searchPaths) {
		String[] returnPaths = Arrays.copyOf(searchPaths, searchPaths.length);

		for (int i = 0, k = returnPaths.length; i < k; i++) {
			if (returnPaths[i] == null) {
				continue;
			}
			for (int j = (i + 1), l = returnPaths.length; j < l; j++) {
				if (returnPaths[j] == null) {
					continue;
				}
				if (returnPaths[j].startsWith(returnPaths[i])) {
					returnPaths[j] = null;
				}
			}
		}
		return returnPaths;
	}
}
