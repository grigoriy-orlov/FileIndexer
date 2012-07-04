package ru.ares4322.filescanner.args;

import java.nio.file.Paths;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.ares4322.filescanner.UnsupportedOSException;
import ru.ares4322.filescanner.Utils;

/**
 * @todo сделать в тестах определение ОС и установку путей для конкретной ОС
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class SimpleArgsParserTest {

	@Test(expected = ArgsParsingException.class)
	public void testParse1() throws Exception {
		String[] args = null;
		SimpleArgsParser parser = new SimpleArgsParser();
		parser.parse(args);
	}

	@Test(expected = ArgsParsingException.class)
	public void testParse2() throws Exception {
		String[] args = {"-"};
		SimpleArgsParser parser = new SimpleArgsParser();
		parser.parse(args);
	}

	@Test(expected = ArgsParsingException.class)
	public void testParse3() throws Exception {
		String[] args = new String[2];
		switch (Utils.getOSName()) {
			case LINUX:
			case MACOS:
				args[0] = "-";
				args[1] = "/home";
				break;
			case WINDOWS:
				args[0] = "-";
				args[1] = "c:\\";
				break;
			default:
				throw new UnsupportedOSException("unsupported operating system");
		}
		SimpleArgsParser parser = new SimpleArgsParser();
		parser.parse(args);
	}

	@Test()
	public void testParse4() throws Exception {
		String[] args = new String[2];
		switch (Utils.getOSName()) {
			case LINUX:
			case MACOS:
				args[0] = "";
				args[1] = "/home";
				break;
			case WINDOWS:
				args[0] = "";
				args[1] = "c:\\";
				break;
			default:
				throw new UnsupportedOSException("unsupported operating system");
		}
		SimpleArgsParser parser = new SimpleArgsParser();
		ScanParams result = parser.parse(args);

		assertEquals(1, result.getSearchPathList().size());

		assertEquals(Paths.get(args[1]).normalize().toAbsolutePath(), result.getSearchPathList().get(0).normalize().toAbsolutePath());
	}

	@Test()
	public void testParse5() throws Exception {
		String searchPath;
		String excludePath;
		switch (Utils.getOSName()) {
			case LINUX:
			case MACOS:
				searchPath = "/var/log";
				excludePath = "/var";
				break;
			case WINDOWS:
				searchPath = "c:\\WINDOWS";
				excludePath = "c:\\";
				break;
			default:
				throw new UnsupportedOSException("unsupported operating system");
		}
		String[] args = {searchPath, "-", excludePath};
		SimpleArgsParser parser = new SimpleArgsParser();
		SimpleScanParams result = (SimpleScanParams) parser.parse(args);

		assertEquals(1, result.getSearchPathList().size());
		assertEquals(1, result.getExcludePathList().size());

		assertEquals(Paths.get(searchPath).normalize().toAbsolutePath(), result.getSearchPathList().get(0));
		assertEquals(Paths.get(excludePath).normalize().toAbsolutePath(), result.getExcludePathList().get(0));
	}

	@Test()
	public void testParse6() throws Exception {
		String searchPath1;
		String searchPath2;
		String excludePath1;
		String excludePath2;
		switch (Utils.getOSName()) {
			case LINUX:
			case MACOS:
				searchPath1 = "/home/ares4322";
				searchPath2 = "/var/log";
				excludePath1 = "/home/";
				excludePath2 = "/var/";
				break;
			case WINDOWS:
				searchPath1 = "C:\\Program Files\\Messenger";
				searchPath2 = "C:\\Documents and Settings\\All Users";
				excludePath1 = "C:\\Program Files";
				excludePath2 = "C:\\Documents and Settings";
				break;
			default:
				throw new UnsupportedOSException("unsupported operating system");
		}
		String[] args = {searchPath1, searchPath2, "-", excludePath1, excludePath2};
		SimpleArgsParser parser = new SimpleArgsParser();
		SimpleScanParams result = (SimpleScanParams) parser.parse(args);

		assertEquals(2, result.getSearchPathList().size());
		assertEquals(2, result.getExcludePathList().size());

		assertEquals(Paths.get(searchPath1).normalize().toAbsolutePath(), result.getSearchPathList().get(0));
		assertEquals(Paths.get(searchPath2).normalize().toAbsolutePath(), result.getSearchPathList().get(1));
		assertEquals(Paths.get(excludePath1).normalize().toAbsolutePath(), result.getExcludePathList().get(0));
		assertEquals(Paths.get(excludePath2).normalize().toAbsolutePath(), result.getExcludePathList().get(1));
	}
}
