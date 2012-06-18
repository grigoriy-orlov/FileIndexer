package ru.ares4322;

import java.util.ArrayList;
import java.util.List;
import ru.ares4322.args.ArgsParserEnum;
import ru.ares4322.args.ArgsParserFactory;
import ru.ares4322.args.ArgsParsingException;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class App {

	//@todo сделать обработку IOException и InterruptedException
	public static void main(String[] args) {
		try {
			int processorQuantity = Runtime.getRuntime().availableProcessors();
			System.out.println("processorQuantity: " + processorQuantity);

			//@todo можно сделать абстрактную фабрику для семейств классов
			SearchParams searchParams = ArgsParserFactory.build(ArgsParserEnum.SIMPLE).parse(args);

			(new ParamsProcessor()).sortArray(searchParams.searchPaths).sortArrayIfNotNull(searchParams.excludePaths).
					removePathRedundancy(searchParams.searchPaths).removePathRedundancyIfNotNull(searchParams.excludePaths);

			Searcher searcher = SearcherFactory.build(SearcherEnum.OIO_MULTI_THREADED_INMEMORY2);
			searcher.search(searchParams.searchPaths[0]);

		} catch (ClassNotFoundException | ArgsParsingException ex) {
			System.out.println("Error: "+ex.getMessage());
		}

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
}
