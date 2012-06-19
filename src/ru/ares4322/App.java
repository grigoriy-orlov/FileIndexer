/**
 *************
 * TODO list * 
 *************
 * 1) Сделать обработку путей исключения из поиска
 *
 * 2) Сделать SimpleParamsProcessor с сортировкой и удалением избыточности.
 *
 * 3) Сделать вывод сообщений о прогрессе поиска. Делать как в ТЗ плохо, потому что полоска прогресса в консоли при
 * выводе сообщений будет смещаться. Лучше сделать вывод метки времени (прошло столько то) каждые сколько-то секунд
 * (межно сделать настраиваемым).
 *
 * 4) Сделать правильную обработку исключений. Почитать, нужно ли объявлять свои исключения или нет. Написать свои
 * исключения.
 *
 * 5) Сделать поддержку сетевого поиска.
 *
 * 6) Сделать unit-тесты.
 *
 */
package ru.ares4322;

import java.util.ArrayList;
import java.util.List;
import ru.ares4322.args.*;

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

			SimpleSearchParamsFactory searchParamsFactory = new SimpleSearchParamsFactory();

			ArgsParser paramsParser = searchParamsFactory.buildParamsParser();
			ParamsProcessor paramsProcessor = searchParamsFactory.buildParamsProcessor();

			SearchParams searchParams = paramsParser.parse(args);
			searchParams = paramsProcessor.process(searchParams);

			Searcher searcher = SearcherFactory.build(SearcherEnum.OIO_MULTI_THREADED_WAIT_FREE);
			searcher.search(searchParams);

		} catch (ClassNotFoundException | ArgsParsingException ex) {
			System.out.println("Error: " + ex.getMessage());
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
