package ru.ares4322.filescanner;

import ru.ares4322.filescanner.utils.Utils;
import ru.ares4322.filescanner.utils.UnsupportedOSException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ru.ares4322.filescanner.args.*;

/**
 * Программа работает в 3 этапа: разбор параметров командной строки, их
 * обработка и непосредственно сканирование. Для каждого этапа создано по
 * интерфейсу. Конкретные классы парсера и обработчика параметров командной
 * строки создаются определенным классом фабрики, так как эти классы
 * взаимосвязаны. Для сканеров есть своя фабрика. В проекте представлено по
 * одному классу парсера и обработчика параметров командной строки и 2 класса
 * сканера. Сканирование в обоих сканерах осуществляется с помощью
 * Files.walkFileTree() из JDK7. Распараллеливание сканирования в обоих сканерах
 * осуществляется по принципу: для каждого диска - один поток. В случае
 * одновременного сканирования несколькими потоками одного диска будет только
 * ухудшение производительности, так как головка у диска только одна, поэтому
 * для одного диска в один момент времени запущено не более одной задачи.
 * SimpleScanner сохраняет все результаты в память, далее сортирует результаты и
 * пишет на диск. ExtendedScanner использует для сохранения результатов
 * сканирования всех потоков файл на диске. Далее этот файл сортируется и
 * пишется в итоговый файл. Для внешних сортировщиков есть свой интерфейс,
 * фабрика и один конкретный класс. Исходники алгоритма внешней сортировки взяты
 * отсюда - http://code.google.com/p/externalsortinginjava/ и немного доработаны
 * под задачу. Модульные тесты сделаны для классов парсера и обработчика
 * параметров командной строки. Для сканеров нужны функциональные тесты, так как
 * они используют в работе файловую систему.
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class App {

	public static void main(String[] args) {
		String outputFileCharsetName = "UTF-8";

		ProgressBar progressBar = null;

		try {
			Path resultFilePath = prepareResultFile();
			progressBar = ProgressBarFactory.buildSimpleConsoleProgressBar();
			progressBar.start();

			SimpleScanParamsFactory scanParamsFactory = new SimpleScanParamsFactory();

			ArgsParser paramsParser = scanParamsFactory.buildArgsParser();
			ParamsProcessor paramsProcessor = scanParamsFactory.buildParamsProcessor();

			ScanParams scanParams = paramsParser.parse(args);
			scanParams = paramsProcessor.process(scanParams);

			ScanResultOutputParams outputParams = new ScanResultOutputParams();
			outputParams.setOutputFileCharset(Charset.forName(outputFileCharsetName));
			outputParams.setOutputFilePath(resultFilePath);

			//можно сделать поддержку переключения сканеров (можно назвать это политиками сканирования)
			//через параметры командной строки
			FileScanner scaner = FileScannerFactory.buildExtendedScanner();
			scaner.scan(scanParams, outputParams);

			System.out.println("");
			System.out.println("results are in file: " + resultFilePath.toAbsolutePath());
		} catch (Exception ex) {
			System.err.println(new StringBuilder(2).append("ERROR: ").append(ex.getMessage()));
		} finally {
			if (progressBar != null) {
				progressBar.stop();
			}
		}
	}

	/**
	 * Пересоздает папку и файл в ней для итоговых результатов в зависимости от
	 * ОС
	 */
	public static Path prepareResultFile() throws UnsupportedOSException, IOException {
		Path resultDirPath;
		Path resultFilePath;
		switch (Utils.getOSName()) {
			case LINUX:
			case MACOS:
				resultDirPath = Paths.get("/tmp/filescanner/");
				resultFilePath = Paths.get("/tmp/filescanner/result.txt");
				if (Files.exists(resultDirPath)) {
					Files.deleteIfExists(resultFilePath);
				} else {
					Files.createDirectory(resultDirPath);
					Files.createFile(resultFilePath);
				}
				break;
			case WINDOWS:
				resultDirPath = Paths.get("C:\\windows\\temp\\filescanner");
				resultFilePath = Paths.get("C:\\windows\\temp\\filescanner\\result.txt");
				if (Files.exists(resultDirPath)) {
					Files.deleteIfExists(resultFilePath);
				} else {
					Files.createDirectory(resultDirPath);
					Files.createFile(resultFilePath);
				}
				break;
			default:
				throw new UnsupportedOSException("unsupported operating system");
		}
		return resultFilePath;
	}
}
