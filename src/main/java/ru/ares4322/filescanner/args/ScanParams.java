package ru.ares4322.filescanner.args;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

/**
 * Параметры сканирования
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ScanParams {

	protected List<Path> scanPathList;
	protected Path outputFilePath;
	protected Charset outputFileCharset;

	public List<Path> getSearchPathList() {

		return scanPathList;
	}

	public ScanParams setSearchPathList(List<Path> scanPathList) {
		this.scanPathList = scanPathList;
		return this;
	}

	public Path getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(Path outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public Charset getOutputFileCharset() {
		return outputFileCharset;
	}

	public void setOutputFileCharset(Charset outputFileCharset) {
		this.outputFileCharset = outputFileCharset;
	}
}
