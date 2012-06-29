package ru.ares4322.filescanner;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import ru.ares4322.filescanner.args.*;

/**
 * @todo подумать над внешним буфером и внешней сортировкой
 *
 * Программа сохраняет результаты сканирования каждого пути в память, так как
 * для 1000000 файлов требуется приблизительно 500 Мб. Столько свободной памяти
 * найдется на всех современных машинах. Для обработки количества файлов, для
 * которых памяти не хватит, даже если дл JVM отдать всю свободную память, можно
 * доработать программу, реализовав в ней дополнительный функционал, который
 * будет задаваться через параметр. При этом будет происходить периодический
 * сброс результатов сканирования на диск, а в конце сканирования такие файлы,
 * если все пути из них не будут влезать в память, будут сортироваться внешней
 * сортировкой и скидываться в один результирующий файл. Но в том случае, если
 * будет сканироваться диск, на который будут сбрасываться промежуточные файлы,
 * производительность сканирования немного упадет.
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class App {

	public static void main(String[] args) {
		String outputFileCharsetName = "UTF-8";

		String outputFilePathName;
		ProgressBar progressBar = null;

		try {
			switch (Utils.getOSName()) {
				case LINUX:
				case MACOS:
					outputFilePathName = "/tmp/filescanner/result.txt";
					break;
				case WINDOWS:
					outputFilePathName = "C:\\windows\\temp\\filescanner\\result.txt";
					break;
				default:
					throw new Exception("unsupported operating system");
			}
			progressBar = ProgressBarFactory.buildSimpleConsoleProgressBar();
			progressBar.start();

			SimpleScanParamsFactory scanParamsFactory = new SimpleScanParamsFactory();

			ArgsParser paramsParser = scanParamsFactory.buildArgsParser();
			ParamsProcessor paramsProcessor = scanParamsFactory.buildParamsProcessor();

			ScanParams scanParams = paramsParser.parse(args);
			scanParams = paramsProcessor.process(scanParams);

			ScanResultOutputParams outputParams = new ScanResultOutputParams();
			outputParams.setOutputFileCharset(Charset.forName(outputFileCharsetName));
			outputParams.setOutputFilePath(Paths.get(outputFilePathName));

			FileScanner scaner = FileScannerFactory.buildNIOScanner();
			scaner.scan(scanParams, outputParams);

			System.out.println("results are in file: "+outputFilePathName);
		} catch (Exception ex) {
			System.err.println(new StringBuilder(2).append("ERROR: ").append(ex.getMessage()));
		} finally {
			if (progressBar != null) {
				progressBar.stop();
			}
		}
	}
}
