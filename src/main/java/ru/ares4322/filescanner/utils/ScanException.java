package ru.ares4322.filescanner.utils;

/**
 * Класс исключения при сканировании
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ScanException extends Exception {

	public ScanException() {
	}

	public ScanException(String message) {
		super(message);
	}

	public ScanException(Throwable cause) {
		super(cause);
	}

	public ScanException(String message, Throwable cause) {
		super(message, cause);
	}
}
