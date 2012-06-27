package ru.ares4322.filescanner.args;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class SearchParams {

	protected List<Path> searchPathList;
	protected Path outputFilePath;
	protected Charset outputFileCharset;

	public List<Path> getSearchPathList() {

		return searchPathList;
	}

	public SearchParams setSearchPathList(List<Path> searchPathList) {
		this.searchPathList = searchPathList;
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
