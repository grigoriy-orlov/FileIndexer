package ru.ares4322.filescanner;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Методы класса вызываются средой исполнения при переборе файлов.
 *
 * @author ares4322
 */
public class SimpleFileVisitor implements FileVisitor<Path> {

	protected List<FileInfo> scanPathList;
	protected List<Path> excludePathList;

	public SimpleFileVisitor(List<FileInfo> scanPathList, List<Path> excludePathList) {
		this.scanPathList = scanPathList;
		this.excludePathList = excludePathList;
	}

	//@todo здесь можно заменить перебор на двоичный поиск с подбором
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		FileVisitResult result = FileVisitResult.CONTINUE;
		boolean addPath = true;

		//@todo прикрутить тут Utils.scanPathInList и можно сделать удаление найденного пути из списка исключений
		for (Iterator<Path> it = this.excludePathList.iterator(); it.hasNext();) {
			Path excludePath = it.next();
			if (dir.startsWith(excludePath) || dir.equals(excludePath)) {
				result = FileVisitResult.SKIP_SUBTREE;
				addPath = false;
				break;
			}
		}
		if (addPath == true) {
			this.addPath(dir);
		}
		return result;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		boolean addPath = Utils.searchPathInListNew(file, this.excludePathList);

		if (addPath == true) {
			this.addPath(file);
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		String preffix;
		if (Files.isDirectory(file)) {
			preffix = "Fail visit directory: ";
		} else if (Files.isRegularFile(file)) {
			preffix = "Fail visit file: ";
		} else if (Files.isSymbolicLink(file)) {
			preffix = "Fail visit link: ";
		} else {
			preffix = "Fail visit unknown filesystem entity: ";
		}
		System.err.println((new StringBuilder(5)).append("WARNING: ").append(preffix).append(file.toAbsolutePath()));
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	private void addPath(Path path) {
		try {
			this.scanPathList.add(new FileInfo(path, path.toAbsolutePath().toString(), Files.size(path), new Date(Files.getLastModifiedTime(path).toMillis())));
		} catch (IOException ex) {
			System.err.println((new StringBuilder(4)).append("WARNING: Fail save file info for ").append(path).append(", cause: ").append(ex.getMessage()));
		}
	}
}
