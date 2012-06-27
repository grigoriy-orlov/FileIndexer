package ru.ares4322.filescanner.args;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class SimpleParamsProcessorTest{

	@Test(expected = ParamsProcessingException.class)
	public void testProcess1() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();
		processor.process(null);
	}

	@Test(expected = ParamsProcessingException.class)
	public void testProcess2() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();
		processor.process(new SimpleSearchParams());
	}

	@Test()
	public void testProcess3() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();

		List<Path> pathList = new LinkedList<>();
		pathList.add(Paths.get("/var").toAbsolutePath().normalize());
		pathList.add(Paths.get("/var").toAbsolutePath().normalize());

		List<Path> resultPathList = processor.removePathRedunduncy(pathList);

		assertEquals(1, resultPathList.size());
		assertEquals(Paths.get("/var").toAbsolutePath().normalize(), resultPathList.get(0).toAbsolutePath().normalize());

	}

	@Test()
	public void testProcess4() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();

		List<Path> pathList = new LinkedList<>();
		pathList.add(Paths.get("/var").toAbsolutePath().normalize());
		pathList.add(Paths.get("/var/log").toAbsolutePath().normalize());

		List<Path> resultPathList = processor.removePathRedunduncy(pathList);

		assertEquals(1, resultPathList.size());
		assertEquals(Paths.get("/var").toAbsolutePath().normalize(), resultPathList.get(0).toAbsolutePath().normalize());

	}

	@Test()
	public void testProcess5() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();

		List<Path> resultPathList = new LinkedList<>();
		resultPathList.add(Paths.get("/var").toAbsolutePath().normalize());

		List<Path> excludePathList = new LinkedList<>();
		excludePathList.add(Paths.get("/var").toAbsolutePath().normalize());

		SortedMap<Path, List<Path>> sortedPathMap = processor.getSortedPathList(resultPathList, excludePathList);

		assertEquals(0, sortedPathMap.size());
	}

	@Test()
	public void testProcess6() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();

		List<Path> resultPathList = new LinkedList<>();
		resultPathList.add(Paths.get("/var/log").toAbsolutePath().normalize());

		List<Path> excludePathList = new LinkedList<>();
		excludePathList.add(Paths.get("/var").toAbsolutePath().normalize());

		SortedMap<Path, List<Path>> sortedPathMap = processor.getSortedPathList(resultPathList, excludePathList);

		assertEquals(0, sortedPathMap.size());
	}

	@Test()
	public void testProcess7() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();

		List<Path> resultPathList = new LinkedList<>();
		resultPathList.add(Paths.get("/var/").toAbsolutePath().normalize());

		List<Path> excludePathList = new LinkedList<>();
		excludePathList.add(Paths.get("/var/log").toAbsolutePath().normalize());

		SortedMap<Path, List<Path>> sortedPathMap = processor.getSortedPathList(resultPathList, excludePathList);

		assertEquals(1, sortedPathMap.size());
		assertEquals(Paths.get("/var/").toAbsolutePath().normalize(), sortedPathMap.firstKey().toAbsolutePath().normalize());
		assertEquals(1, sortedPathMap.get(sortedPathMap.firstKey().toAbsolutePath().normalize()).size());
		assertEquals(Paths.get("/var/log").toAbsolutePath().normalize(), sortedPathMap.get(sortedPathMap.firstKey().toAbsolutePath().normalize()).get(0).toAbsolutePath().normalize());
	}
}
