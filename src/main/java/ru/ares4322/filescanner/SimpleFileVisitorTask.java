package ru.ares4322.filescanner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Класс задачи сканирования. На вход передается путь для сканирования, список
 * путей исключения сканирования и название диска. Сканирование осуществляется с
 * помощью java,file.nio.Files.walkFileTree(). Возвращает список
 * объектов---информации-о-файлах. Используется LinkedList, так как ArrayList не
 * подходит из-за того, что неизвестно начальное количество объектов для
 * добавления в него и со списком чаще будут выполняться операции добавления, а
 * операции взятия по индеску не будет.
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class SimpleFileVisitorTask implements Callable<SimpleScanResult> {

	protected List<Path> excludePathList;
	protected Path scanPath;
	protected String diskName;

	public SimpleFileVisitorTask(Path scanPath, List<Path> excludePathList, String diskName) {
		this.scanPath = scanPath;
		this.excludePathList = excludePathList;
		this.diskName = diskName;
	}

	@Override
	public SimpleScanResult call() throws Exception {
		List<FileInfo> scanPathList = new LinkedList<>();
		Files.walkFileTree(scanPath, new SimpleFileVisitor(scanPathList, excludePathList));
		return new SimpleScanResult(this.diskName, scanPathList);
	}
}
