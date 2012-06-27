package ru.ares4322.filescanner;

/**
 *
 * @author ares4322
 */
public enum FileScannerEnum {
	NIO(1),
	OIO_FORK_JOIN(2),
	OIO_LOCKED(3),
	OIO_WAIT_FREE(4);


	public final int ID;

	FileScannerEnum(int id) {
		this.ID = id;
	}
}
