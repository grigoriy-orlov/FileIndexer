package ru.ares4322;

/**
 *
 * @author ares4322
 */
public class SearcherFactory {

	public static Searcher build(SearcherEnum type) throws ClassNotFoundException {
		Searcher result = null;
		switch (type) {
			case NIO_SINGLE_THREADED_INMEMORY:
				result = new NIOSingleThreadedMemorySearcher();
				break;
			case OIO_FORKJOIN_INMEMORY:
				result = new OIOForkJoinInMemorySearcher();
				break;
			case OIO_MULTI_THREADED_INMEMORY:
				result = new OIOMultithreadedInMemorySearcher();
				break;
			case OIO_MULTI_THREADED_INMEMORY2:
				result = new OIOMultithreadedInMemorySearcher2();
				break;
			default:
				throw new ClassNotFoundException("Создание Searcher неподдерживаемого типа");
		}
		return result;
	}
}
