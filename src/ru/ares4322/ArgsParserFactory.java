package ru.ares4322;

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
				throw new ClassNotFoundException("Создание парсера параметров неподдерживаемого типа");
		}
		return result;
	}
}
