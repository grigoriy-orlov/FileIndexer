package ru.ares4322.filescanner.args;

/**
 * Интерфейс обработчика параметров сканирования
 *
 * @author ares4322
 */
public interface ParamsProcessor {

	/**
	 * Обрабатывает параметры сканирования (сортирует, удаляет избытосность и
	 * т.д.) для последующей передачи сканеру
	 *
	 * @param params Исходные параметры сканирования
	 * @return Обработанные параметры сканирования
	 * @throws ParamsProcessingException
	 */
	ScanParams process(ScanParams params) throws ParamsProcessingException;
}
