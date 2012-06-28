package ru.ares4322.filescanner;

import java.util.List;

/**
 * Класс результата сканирования одного пути. Содержит название хранилища, к
 * которому отностися этот путь и список информации о файлах, полученных в
 * результате сканирования
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ScanResult {

	public String diskName;
	public List<FileInfo> resultPathList;

	public ScanResult() {
	}

	public ScanResult(String diskName, List<FileInfo> resultPathList) {
		this.diskName = diskName;
		this.resultPathList = resultPathList;
	}
}
