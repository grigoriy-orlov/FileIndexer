package ru.ares4322.filescanner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Класс индикатора прогресса, который в консоль каждую 6ю секунду выводит
 * точку, а каждую минуту - палочку.
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class SimpleConsoleProgressBar implements ProgressBar {

	private ScheduledExecutorService scheduledExecutor;

	public SimpleConsoleProgressBar() {
		this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public void start() {
		this.scheduledExecutor.scheduleWithFixedDelay(new ProgressBarTask(), 6, 1, TimeUnit.SECONDS);
	}

	@Override
	public void stop() {
		scheduledExecutor.shutdown();
	}
}
