package ru.ares4322.filescanner.args;

/**
 *
 * @author ares4322
 */
public interface ArgsParser {

	SearchParams parse(String[] args) throws ArgsParsingException;
}
