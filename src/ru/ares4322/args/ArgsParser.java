package ru.ares4322.args;

import ru.ares4322.SearchParams;

/**
 *
 * @author ares4322
 */
public interface ArgsParser {
	SearchParams parse(String[] args) throws ArgsParsingException;
}
