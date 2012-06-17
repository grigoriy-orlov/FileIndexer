package ru.ares4322;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class App {

	//@todo сделать обработку IOException и InterruptedException
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

		int processorQuantity = Runtime.getRuntime().availableProcessors();
		System.out.println("processorQuantity: " + processorQuantity);

		//@todo можно сделать абстрактную фабрику для семейств классов
		SearchParams searchParams = ArgsParserFactory.build(ArgsParserEnum.SIMPLE).parse(args);

		(new ParamsProcessor()).sortArray(searchParams.searchPaths).sortArrayIfNotNull(searchParams.excludePaths).
				removePathRedundancy(searchParams.searchPaths).removePathRedundancyIfNotNull(searchParams.excludePaths);

		Searcher searcher = SearcherFactory.build(SearcherEnum.OIO_FORKJOIN_INMEMORY);
		searcher.search(searchParams.searchPaths[0]);

	}

	private static void memoryTest() {
		int testListLength = 10000000;
		System.out.println("Memory before (Mb): " + Runtime.getRuntime().freeMemory() / 1024 / 1024);
		List<String> testList = new ArrayList<>(testListLength);
		for (int i = 0; i < testListLength; i++) {
			testList.add("/path/path/path/path");
		}
		System.out.println("Memory after (Mb): " + Runtime.getRuntime().freeMemory() / 1024 / 1024);
		System.exit(0);
	}

	/**
	 * @todo подумать, как тут можно оптимизировать
	 */
	public static String[] removePathRedundancy(String[] searchPaths) {
		//@todo доделать
		/*
		 * List<String> resultList = new ArrayList<>(searchPaths.length);
		 *
		 * for (int i = (searchPaths.length - 1), k = 0; i >= k; i--) { for (int j = (i - 1), l = 0; j >= l; j--) { if
		 * (searchPaths[j].startsWith(searchPaths[i])) { returnPaths[j] = null; } } }
		 *
		 */

		return searchPaths;
	}
}
