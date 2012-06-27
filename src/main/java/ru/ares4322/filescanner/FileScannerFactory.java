package ru.ares4322.filescanner;

/**
 *
 * @author ares4322
 */
public class FileScannerFactory {

	public static FileScanner build(FileScannerEnum type) throws ClassNotFoundException {
		FileScanner result = null;
		switch (type) {
			case NIO:
				result = new NIOScanner();
				break;
			case OIO_FORK_JOIN:
				result = new OIOForkJoinScanner();
				break;
			case OIO_LOCKED:
				result = new OIOLockedScanner();
				break;
			case OIO_WAIT_FREE:
				result = new OIOWaitFreeScanner();
				break;
			default:
				throw new ClassNotFoundException("Unknown Scanner type");
		}
		return result;
	}
}
