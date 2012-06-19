package ru.ares4322;

/**
 *
 * @author ares4322
 */
public class SearcherFactory {

	public static Searcher build(SearcherEnum type) throws ClassNotFoundException {
		Searcher result = null;
		switch (type) {
			case NIO_SINGLE_THREADED:
				result = new NIOSingleThreadedSearcher();
				break;
			case OIO_FORK_JOIN:
				result = new OIOForkJoinSearcher();
				break;
			case OIO_MULTI_THREADED_LOCKED:
				result = new OIOMultithreadedLockedSearcher();
				break;
			case OIO_MULTI_THREADED_WAIT_FREE:
				result = new OIOMultithreadedWaitFreeSearcher();
				break;
			default:
				throw new ClassNotFoundException("Создание Searcher неподдерживаемого типа");
		}
		return result;
	}
}
