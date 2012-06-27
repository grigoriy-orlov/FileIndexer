package ru.ares4322.filescanner.args;

/**
 *
 * @author ares4322
 */
public interface SearchParamsFactory {

	ArgsParser buildParamsParser();

	ParamsProcessor buildParamsProcessor();
}
