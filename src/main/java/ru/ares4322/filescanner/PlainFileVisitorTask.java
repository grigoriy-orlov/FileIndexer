package ru.ares4322.filescanner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Класс задачи сканирования. На вход передается путь для сканирования, список
 * путей исключения сканирования и название диска. Сканирование осуществляется с
 * помощью java,file.nio.Files.walkFileTree()
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class PlainFileVisitorTask implements Callable<ScanResult> {

	protected List<Path> excludePathList;
	protected Path scanPath;
	protected String diskName;

	public PlainFileVisitorTask(Path scanPath, List<Path> excludePathList, String diskName) {
		this.scanPath = scanPath;
		this.excludePathList = excludePathList;
		this.diskName = diskName;
		System.out.println(new StringBuilder().append("create task, scan path: ").append(scanPath).append(", disk name: ").append(diskName));
	}

	@Override
	public ScanResult call() throws Exception {
		System.out.println(new StringBuilder().append("call task, scan path: ").append(scanPath).append(", disk name: ").append(diskName));
		List<FileInfo> scanPathList = new LinkedList<>();
		Files.walkFileTree(scanPath, new PlainFileVisitor(scanPathList, excludePathList));
		System.out.println(new StringBuilder().append("finish task, scan path: ").append(scanPath).append(", disk name: ").append(diskName));
		return new ScanResult(this.diskName, scanPathList);
	}
}
