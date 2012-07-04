package ru.ares4322.filescanner;

/**
 * Фабрика для сортировщиков с помощью внешней памяти
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ExternalSorterFactory {

	public static ExternalSorter buildSimpleExternalSorter() {
		return new SimpleExternalSorter();
	}
}
