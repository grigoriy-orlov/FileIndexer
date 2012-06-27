package ru.ares4322.filescanner.args;

/**
 * Интерфейс фабрики для создания взаимосвязанных объектов парсера аргументов
 * командной строки и обработчика параметров сканирования
 *
 * @author ares4322
 */
public interface ScanParamsFactory {

	/**
	 * Создает ArgsParser
	 *
	 * @return ArgsParser
	 */
	ArgsParser buildArgsParser();

	/**
	 * Создает ParamsProcessor
	 *
	 * @return ParamsProcessor
	 */
	ParamsProcessor buildParamsProcessor();
}
