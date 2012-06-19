package ru.ares4322.args;

/**
 *
 * @author ares4322
 */
public class SimpleSearchParamsFactory implements SearchParamsFactory{

	@Override
	public ArgsParser buildParamsParser() {
		return new SimpleArgsParser();
	}

	@Override
	public ParamsProcessor buildParamsProcessor() {
		return new SimpleParamsProcessor();
	}
	
}
