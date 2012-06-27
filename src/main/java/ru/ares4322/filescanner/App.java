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

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import ru.ares4322.filescanner.args.*;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class App {

	public static void main(String[] args) {
		try {
			String outputFileCharsetName = "UTF-8";
			String outputFilePathName = "/home/ares4322/tmp/result.txt";

			SimpleScanParamsFactory scanParamsFactory = new SimpleScanParamsFactory();

			ArgsParser paramsParser = scanParamsFactory.buildArgsParser();
			ParamsProcessor paramsProcessor = scanParamsFactory.buildParamsProcessor();

			ScanParams scanParams = paramsParser.parse(args);
			scanParams = paramsProcessor.process(scanParams);
			scanParams.setOutputFileCharset(Charset.forName(outputFileCharsetName));
			scanParams.setOutputFilePath(Paths.get(outputFilePathName));

			ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
			scheduledExecutor.scheduleWithFixedDelay(new ProgressBarTask(), 0, 1, TimeUnit.SECONDS);

			FileScanner scaner = FileScannerFactory.build(FileScannerEnum.NIO);
			scaner.scan(scanParams);

			scheduledExecutor.shutdown();

		} catch (ClassNotFoundException | ParamsProcessingException | ArgsParsingException ex) {
			System.out.println("Error: " + ex.getMessage());
		}
		System.out.println("last line of program");
	}
}
