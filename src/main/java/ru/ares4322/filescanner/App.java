/**
 *************
 * TODO list * ************ -2) ВАЖНО!!! Протестировать с 1000000 файлов по
 * памяти 0 влезет или нет в Гиг-полтора и сколько будет работать. Если нет, то
 * придется думать с внешней сортировкой
 *
 * -1) Переделать WaitFree и Locked алгоритмы, так как они не работают (может
 * Locked вообще убрать)
 *
 * 0) Если введен несуществующий путь, то он просто выводится в файл, как есть,
 * что не правильно.
 *
 * +1) Сделать обработку путей исключения из поиска
 *
 * +2) Сделать SimpleParamsProcessor с сортировкой, удалением избыточности и
 * удалением непредставленных путей;
 *
 * 3) Сделать вывод сообщений о прогрессе поиска. Делать как в ТЗ плохо, потому
 * что полоска прогресса в консоли при выводе сообщений будет смещаться. Лучше
 * сделать вывод метки времени (прошло столько то) каждые сколько-то секунд
 * (межно сделать настраиваемым).
 *
 * 4) ОБЯЗАТЕЛЬНО сделать правильную обработку исключений. Почитать, нужно ли
 * объявлять свои исключения или нет. Написать свои исключения. Проработать
 * исключения JDK.
 *
 * +5) Сделать поддержку сетевого поиска. В Windows все работает, в Linux надо
 * либо подмонтировать файл и работать как с локальным, либо через ftp, либо
 * прикрутить smb provider для URI. Для Linux можно сделать подсказку.
 *
 * 6) Сделать unit-тесты.
 *
 * 7) Продумать сброс на диск отсортированного куска путей. Между такими кусками
 * можно делать пустую строку, как разделитель. А потом по ней во время
 * ортировки итогового файла опрелять границы блоков
 *
 * Может, есть смысл поменять String[] в параметрах на List<String>
 */
package ru.ares4322.filescanner;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import ru.ares4322.filescanner.args.*;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class App {

	//@todo сделать обработку IOException и InterruptedException
	public static void main(String[] args) throws URISyntaxException {
		try {
			String outputFileCharsetName = "UTF-8";
			String outputFilePathname = "/home/ares4322/tmp/result.txt";

			SimpleSearchParamsFactory searchParamsFactory = new SimpleSearchParamsFactory();

			ArgsParser paramsParser = searchParamsFactory.buildParamsParser();
			ParamsProcessor paramsProcessor = searchParamsFactory.buildParamsProcessor();

			SearchParams searchParams = paramsParser.parse(args);
			searchParams = paramsProcessor.process(searchParams);
			searchParams.setOutputFileCharset(Charset.forName(outputFileCharsetName));
			searchParams.setOutputFilePath(Paths.get(outputFilePathname));

			ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
			scheduledExecutor.scheduleWithFixedDelay(new ProgressBarTask(), 0, 1, TimeUnit.SECONDS);

			Searcher searcher = SearcherFactory.build(SearcherEnum.NIO_SINGLE_THREADED);
			searcher.search(searchParams);

			scheduledExecutor.shutdown();

		} catch (ClassNotFoundException | ParamsProcessingException | ArgsParsingException ex) {
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

	private static boolean testNIONetworkLinuxSearching() throws URISyntaxException {
		Path netPath = Paths.get(new URI("file:///ntcserv/share/text.txt"));
		System.out.println("nio path: " + netPath.toAbsolutePath());
		return Files.exists(netPath);
	}

	private static boolean testOIONetworkLinuxSearching() {
		File netFile = new File("file:///ntcserv/share/text.txt");
		System.out.println("oio path: " + netFile.getAbsolutePath());
		return netFile.exists();
	}
}