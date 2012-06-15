package ru.ares4322;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1 способ -
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class App {

	//@todo сделать обработку IOException и InterruptedException
	public static void main(String[] args) throws IOException, InterruptedException {

		SearchParams searchParams = ArgsParser.parse(args);

		//сортируем эти массивы
		Arrays.sort(searchParams.searchPaths);
		if (searchParams.excludePaths != null) {
			Arrays.sort(searchParams.excludePaths);
		}

		//удаляем избыточность путей
		searchParams.searchPaths = removePathRedundancy(searchParams.searchPaths);

		//делаем перебор путей поиска
		//@todo нужно сделать параллельный обход дерева папок для каждого аргумента,
		//так как аргумент можнт быть один, а папка большая и тогда будет работать только один поток
		ConcurrentLinkedQueue<Path> pathQueue = new ConcurrentLinkedQueue<Path>();
		final Lock lock = new ReentrantLock();
		Condition condition = lock.newCondition();

		String tmpDirPath = "/home/ares4322/tmp/";
		Path tmpDir = Paths.get(tmpDirPath);
		if (Files.isDirectory(tmpDir) == false) {
			Files.createDirectory(tmpDir);
		}
		ExecutorService threadPool = Executors.newFixedThreadPool(5);
		int tmpFileIndex = 0;
		Charset charset = Charset.forName("UTF-8");
		for (int i = 0; i < searchParams.searchPaths.length; i++) {
			condition.await();
			Path tmpFile = Files.createFile(Paths.get(tmpDirPath + tmpFileIndex));
			threadPool.execute(new Walker(Paths.get(searchParams.searchPaths[i]), tmpFile, charset, condition, pathQueue));
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
