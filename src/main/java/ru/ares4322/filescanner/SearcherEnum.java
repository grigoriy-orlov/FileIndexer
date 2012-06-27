package ru.ares4322.filescanner;

/**
 *
 * @author ares4322
 */
public enum SearcherEnum {
	NIO_SINGLE_THREADED(1),
	OIO_FORK_JOIN(2),
	OIO_MULTI_THREADED_LOCKED(3),
	OIO_MULTI_THREADED_WAIT_FREE(4);


	public final int ID;

	SearcherEnum(int id) {
		this.ID = id;
	}
}
