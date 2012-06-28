package ru.ares4322.filescanner;

import java.nio.file.Path;
import java.util.Date;

/**
 * Класс информации о файле
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class FileInfo implements Comparable<FileInfo> {

	Path path;
	String absPath;
	long size;
	Date lastModTime;

	public FileInfo() {
	}

	public FileInfo(Path path, String absPath, long size, Date lastModTime) {
		this.path = path;
		this.absPath = absPath;
		this.size = size;
		this.lastModTime = lastModTime;
	}

	@Override
	public int compareTo(FileInfo o) {
		return this.path.compareTo(o.path);
	}
}
