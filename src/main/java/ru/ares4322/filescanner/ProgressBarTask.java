package ru.ares4322.filescanner;

/**
 * Класс задачи для вывода в консоль строки прогресса сканирования. Каждый 6й
 * запуск выводится точка, каждый 60й палочка
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ProgressBarTask implements Runnable {

	byte runQuantity = 0;

	@Override
	public void run() {
		if (runQuantity < 60) {
			if (runQuantity % 6 == 0) {
				System.out.print(".");
			}
		} else {
			runQuantity = 0;
			System.out.print("|");
		}
		runQuantity++;
	}
}
