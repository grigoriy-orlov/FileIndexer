package ru.ares4322;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.List;

/**
 * @WARNING Если работать с массивами или методами get()/set() списков, то
 * максимальный адресуемый индекс 2,147,483,647 Для обхода этого ограничения
 * надо использовать add() и LinkedList (у ArrayList это медленно)
 *
 * Название SimpleFileVisitor уже занято в библиотеке
 *
 * @author ares4322
 */
public class PlainFileVisitor implements FileVisitor<Path> {

	protected List<String> searchPathList;
	protected List<String> excludePathList;

	public PlainFileVisitor(List<String> searchPathList, List<String> excludePathList) {
		this.searchPathList = searchPathList;
		this.excludePathList = excludePathList;
	}

	//@todo здесь можно заменить перебор на двоичный поиск с подбором
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		FileVisitResult result = FileVisitResult.CONTINUE;
		boolean addPath = true;
		for (Iterator<String> it = excludePathList.iterator(); it.hasNext();) {
			String excludePath = it.next();
			if (dir.startsWith(excludePath) || dir.toAbsolutePath().toString().equals(excludePath)) {
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
		boolean addPath = true;
		for (Iterator<String> it = excludePathList.iterator(); it.hasNext();) {
			String excludePath = it.next();
			if (file.toAbsolutePath().toString().equals(excludePath)) {
				addPath = false;
				break;
			}
		}
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
		System.out.println((new StringBuilder(2)).append(preffix).append(file.toAbsolutePath()));
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	private void addPath(Path path) {
		this.searchPathList.add(path.toAbsolutePath().toString());
	}
}
