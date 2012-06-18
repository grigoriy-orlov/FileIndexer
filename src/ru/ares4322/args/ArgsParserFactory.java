package ru.ares4322.args;

/**
 *
 * @author ares4322
 */
public class ArgsParserFactory {

	public static ArgsParser build(ArgsParserEnum type) throws ClassNotFoundException {
		ArgsParser result;
		switch (type) {
			case SIMPLE:
				result = new SimpleArgsParser();
				break;
			default:
				throw new ClassNotFoundException("creating parser of unknown type");
		}
		return result;
	}
}
