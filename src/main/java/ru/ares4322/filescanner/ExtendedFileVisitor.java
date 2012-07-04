package ru.ares4322.filescanner;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Методы класса вызываются средой исполнения при переборе файлов. Данные о
 * файле(время модификации, размер) запрашиваются здесь, так как эта информация
 * при запросе будет уже находится в кеше и доступ к ней будет осуществлен
 * быстро. Если это делать потом, то эта информация может быть уже не в кеше и,
 * соответственно, возможно обращение к диску. Каждый файл проверяется на
 * исключение по списку путей исключения. Результаты сканированяи пишутся в
 * файл. pathDelimeter используется для того, чтобы записанную в одну строку
 * информацию о файле, можно было потом разнести на несколько строк.
 *
 * @author ares4322
 */
public class ExtendedFileVisitor implements FileVisitor<Path> {

	protected List<FileInfo> scanPathList;
	protected List<Path> excludePathList;
	protected final SimpleDateFormat formatter;
	private final PrintWriter tempWriter;
	private final String pathDelimeter;

	public ExtendedFileVisitor(List<Path> excludePathList, PrintWriter tempWriter) throws ScanException {
		try {
			this.excludePathList = excludePathList;
			this.tempWriter = tempWriter;
			this.formatter = new SimpleDateFormat("yyyy.MM.dd");
			this.pathDelimeter = Utils.getPathDelimeter();
		} catch (Exception ex) {
			throw new ScanException(ex);
		}
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
		System.err.println((new StringBuilder(5)).append("WARNING: ").append(preffix).append(file.toAbsolutePath()));
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
	 * Добавялет информация о файле в промежуточный файл. Информация пишется в
	 * одну строку через пробелы, чтобы при итоговой обработке ее можно было
	 * легко разбить на составляющие и конвертировать в необходимый формат.
	 */
	private void addPath(Path path) {
		try {
			StringBuilder stringBuilder = new StringBuilder(7);
			stringBuilder.append(path);
			stringBuilder.append(this.pathDelimeter);
			stringBuilder.append(formatter.format(new Date(Files.getLastModifiedTime(path).toMillis())));
			stringBuilder.append(this.pathDelimeter);
			stringBuilder.append(Files.size(path));
			this.tempWriter.println(stringBuilder);
		} catch (IOException ex) {
			System.err.println((new StringBuilder(4)).append("WARNING: Fail save file info for ").append(path).append(", cause: ").append(ex.getMessage()));
		}
	}
}
