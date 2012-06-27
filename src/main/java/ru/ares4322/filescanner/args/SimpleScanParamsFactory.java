package ru.ares4322.filescanner.args;

/**
 * Фабрика для создания парсера аргументов командной строки и обработчика
 * параметров сканирования для разбора указанного в ТЗ формата аргументов
 * командной строки
 *
 * @author ares4322
 */
public class SimpleScanParamsFactory implements ScanParamsFactory {

	/**
	 * Создает SimpleArgsParser
	 *
	 * @return SimpleArgsParser
	 */
	@Override
	public ArgsParser buildArgsParser() {
		return new SimpleArgsParser();
	}

	/**
	 * Создает SimpleParamsProcessor
	 *
	 * @return SimpleParamsProcessor
	 */
	@Override
	public ParamsProcessor buildParamsProcessor() {
		return new SimpleParamsProcessor();
	}
}
