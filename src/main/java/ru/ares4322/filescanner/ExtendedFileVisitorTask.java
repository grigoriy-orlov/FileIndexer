package ru.ares4322.filescanner;

import java.io.PrintWriter;
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
 * операции взятия по индеску не будет. Но если будет нужно можно использовать
 * ArrayList
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ExtendedFileVisitorTask implements Callable<ExtendedScanResult> {

	protected List<Path> excludePathList;
	protected Path scanPath;
	protected String diskName;
	private long blockSize;

	public ExtendedFileVisitorTask(Path scanPath, List<Path> excludePathList, String diskName, long blockSize) {
		this.scanPath = scanPath;
		this.excludePathList = excludePathList;
		this.diskName = diskName;
		this.blockSize = blockSize;
	}

	@Override
	public ExtendedScanResult call() throws Exception {
		LinkedList<Path> tmpPathList = new LinkedList<>();
		System.out.println("create task for: '"+this.scanPath.toAbsolutePath()+"'");
		Files.walkFileTree(scanPath, new ExtendedFileVisitor(excludePathList, blockSize));
		return new ExtendedScanResult(this.diskName, tmpPathList);
	}
}
