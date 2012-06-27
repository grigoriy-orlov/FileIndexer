package ru.ares4322.filescanner.args;

/**
 * Интерфейс парсера параметров командной строки
 *
 * @author ares4322
 */
public interface ArgsParser {

	/**
	 * Разбирает параметры командной строки и преобразует их в ScanParams. В
	 * случае ошибки бросает ArgsParsingException
	 *
	 * @param args Массив параметров командной строки
	 * @return ScanParams
	 * @throws ArgsParsingException
	 */
	ScanParams parse(String[] args) throws ArgsParsingException;
}
