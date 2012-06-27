package ru.ares4322.filescanner.args;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ArgsParsingException extends Exception {

	ArgsParsingException() {
		super();
	}

	ArgsParsingException(String msg) {
		super(msg);
	}

	public ArgsParsingException(Throwable cause) {
		super(cause);
	}

	public ArgsParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}
