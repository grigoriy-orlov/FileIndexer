package ru.ares4322.filescanner;

import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class ScanResultOutputParams {

	protected Path outputFilePath;
	protected Charset outputFileCharset;

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
