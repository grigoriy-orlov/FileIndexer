package ru.ares4322.filescanner.args;

/**
 *
 * @author ares4322
 */
public interface ParamsProcessor {

	SearchParams process(SearchParams params) throws ParamsProcessingException;
}
