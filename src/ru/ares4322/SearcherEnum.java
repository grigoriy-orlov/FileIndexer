package ru.ares4322;

/**
 *
 * @author ares4322
 */
public enum SearcherEnum {
	NIO_SINGLE_THREADED_INMEMORY(1),
	OIO_FORKJOIN_INMEMORY(2),
	OIO_MULTI_THREADED_INMEMORY(3),
	OIO_MULTI_THREADED_INMEMORY2(4);


	public final int ID;

	SearcherEnum(int id) {
		this.ID = id;
	}
}
