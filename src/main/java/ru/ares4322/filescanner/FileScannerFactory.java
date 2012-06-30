package ru.ares4322.filescanner;

/**
 * Фабрика для создания сканеров
 *
 * @author ares4322
 */
public class FileScannerFactory {

	public static FileScanner buildSimpleScanner() {
		return new SimpleScanner();
	}
}
