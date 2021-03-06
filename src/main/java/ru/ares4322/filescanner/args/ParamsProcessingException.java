package ru.ares4322.filescanner.args;

/**
 * Класс исключения для ошибок обработки параметров сканирования
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ParamsProcessingException extends Exception {

	public ParamsProcessingException() {
		super();
	}

	public ParamsProcessingException(String message) {
		super(message);
	}

	public ParamsProcessingException(Throwable cause) {
		super(cause);
	}

	public ParamsProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
}
