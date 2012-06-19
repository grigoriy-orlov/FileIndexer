package ru.ares4322.args;

import java.util.Arrays;

/**
 *
 * @author ares4322
 */
public class SimpleParamsProcessor implements ParamsProcessor{

	SimpleParamsProcessor sortArray(String[] paths) {
		Arrays.sort(paths);
		return this;
	}

	SimpleParamsProcessor sortArrayIfNotNull(String[] paths) {
		if (paths != null) {
			Arrays.sort(paths);
		}
		return this;
	}

	SimpleParamsProcessor removePathRedundancy(String[] paths) {
		return this;
	}
	
	SimpleParamsProcessor removePathRedundancyIfNotNull(String[] paths) {
		return this;
	}

	@Override
	public SearchParams process(SearchParams params) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
