package ru.ares4322.filescanner.utils;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class UnsupportedOSException extends Exception {

	public UnsupportedOSException() {
	}

	public UnsupportedOSException(String message) {
		super(message);
	}

	public UnsupportedOSException(Throwable cause) {
		super(cause);
	}

	public UnsupportedOSException(String message, Throwable cause) {
		super(message, cause);
	}
}
