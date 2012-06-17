package ru.ares4322;

import java.util.Arrays;

/**
 *
 * @author ares4322
 */
public class ParamsProcessor {

	ParamsProcessor sortArray(String[] paths) {
		Arrays.sort(paths);
		return this;
	}

	ParamsProcessor sortArrayIfNotNull(String[] paths) {
		if (paths != null) {
			Arrays.sort(paths);
		}
		return this;
	}

	ParamsProcessor removePathRedundancy(String[] paths) {
		return this;
	}
	
	ParamsProcessor removePathRedundancyIfNotNull(String[] paths) {
		return this;
	}
}
