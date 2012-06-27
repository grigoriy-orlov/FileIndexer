package ru.ares4322.filescanner.args;

import java.nio.file.Paths;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
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
		String[] args = {"-", "/home"};
		SimpleArgsParser parser = new SimpleArgsParser();
		parser.parse(args);
	}

	@Test()
	public void testParse4() throws Exception {
		String[] args = {""};

		SimpleArgsParser parser = new SimpleArgsParser();
		SearchParams result = parser.parse(args);

		assertEquals(1, result.getSearchPathList().size());

		assertEquals(Paths.get("./").normalize().toAbsolutePath(), result.getSearchPathList().get(0).normalize().toAbsolutePath());
	}

	@Test()
	public void testParse5() throws Exception {
		String searchPath = "/var/log";
		String excludePath = "/var/";

		String[] args = {searchPath, "-", excludePath};
		SimpleArgsParser parser = new SimpleArgsParser();
		SimpleSearchParams result = (SimpleSearchParams) parser.parse(args);

		assertEquals(1, result.getSearchPathList().size());
		assertEquals(1, result.getExcludePathList().size());

		assertEquals(Paths.get(searchPath).normalize().toAbsolutePath(), result.getSearchPathList().get(0));
		assertEquals(Paths.get(excludePath).normalize().toAbsolutePath(), result.getExcludePathList().get(0));
	}

	@Test()
	public void testParse6() throws Exception {
		String searchPath1 = "/home/ares4322";
		String searchPath2 = "/var/log";
		String excludePath1 = "/home/";
		String excludePath2 = "/var/";

		String[] args = {searchPath1, searchPath2, "-", excludePath1, excludePath2};
		SimpleArgsParser parser = new SimpleArgsParser();
		SimpleSearchParams result = (SimpleSearchParams) parser.parse(args);

		assertEquals(2, result.getSearchPathList().size());
		assertEquals(2, result.getExcludePathList().size());

		assertEquals(Paths.get(searchPath1).normalize().toAbsolutePath(), result.getSearchPathList().get(0));
		assertEquals(Paths.get(searchPath2).normalize().toAbsolutePath(), result.getSearchPathList().get(1));
		assertEquals(Paths.get(excludePath1).normalize().toAbsolutePath(), result.getExcludePathList().get(0));
		assertEquals(Paths.get(excludePath2).normalize().toAbsolutePath(), result.getExcludePathList().get(1));
	}
}
