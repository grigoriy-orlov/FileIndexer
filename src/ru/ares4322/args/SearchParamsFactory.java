package ru.ares4322.args;

/**
 *
 * @author ares4322
 */
public interface SearchParamsFactory {

	ArgsParser buildParamsParser();

	ParamsProcessor buildParamsProcessor();
}
