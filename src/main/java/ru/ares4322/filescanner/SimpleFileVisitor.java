package ru.ares4322.filescanner;

import ru.ares4322.filescanner.utils.Utils;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;

/**
 * Методы класса вызываются средой исполнения при переборе файлов. Данные о
 * файле(время модификации, размер) запрашиваются здесь, так как эта информация
 * при запросе будет уже находится в кеше и доступ к ней будет осуществлен
 * быстро. Если это делать потом, то эта информация может быть уже не в кеше и,
 * соответственно, возможно обращение к диску. Каждый файл проверяется на
 * исключение по списку путей исключения. Результаты сканированяи пишутся в
 * список.
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

	/*
	 * Вызывается перед входом в каталог. Ищем данный каталог в списке
	 * исключений и если нашли, то пропускаем его и удаляем найденный путь
	 * исключения из списка.
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		FileVisitResult result = FileVisitResult.CONTINUE;
		boolean addPath = !Utils.searchPathInListWithRemove(dir, this.excludePathList);

		if (addPath == true) {
			this.addPath(dir);
		} else {
			result = FileVisitResult.SKIP_SUBTREE;
		}
		return result;
	}

	/*
	 * Вызывается при сканировании файла
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		boolean addPath = !Utils.searchPathInListWithRemove(file, this.excludePathList);

		if (addPath == true) {
			this.addPath(file);
		}

		return FileVisitResult.CONTINUE;
	}

	/**
	 * Вызывается при ошибке сканирования файла
	 */
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
		System.err.println((new StringBuilder(3)).append("WARNING: ").append(preffix).append(file.toAbsolutePath()));
		return FileVisitResult.CONTINUE;
	}

	/*
	 * Вызывается при выходе их каталога
	 */
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	/**
	 * Добавялет путь в итоговый список. Сразу записывается информация о файле.
	 */
	private void addPath(Path path) {
		try {
			this.scanPathList.add(new FileInfo(path, path.toAbsolutePath().toString(), Files.size(path), new Date(Files.getLastModifiedTime(path).toMillis())));
		} catch (IOException ex) {
			System.err.println((new StringBuilder(2)).append("WARNING: Fail save file info for ").append(path));
		}
	}
}
