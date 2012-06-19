package ru.ares4322.args;

/**
 *
 * @author ares4322
 */
public interface ArgsParser {

	SearchParams parse(String[] args) throws ArgsParsingException;
}
