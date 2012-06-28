package ru.ares4322.filescanner;

/**
 * Фабрика для создания индикаторов прогресса
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ProgressBarFactory {

	public static SimpleConsoleProgressBar buildSimpleConsoleProgressBar() {
		return new SimpleConsoleProgressBar();
	}
}
