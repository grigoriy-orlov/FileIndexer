package ru.ares4322.filescanner.args;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.ares4322.filescanner.Utils;

/**
 * @todo тут еще можно сделать проверку раскидывания словарей по дискам
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class SimpleParamsProcessorTest {

	@Test(expected = ParamsProcessingException.class)
	public void testProcess1() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();
		processor.process(null);
	}

	@Test(expected = ParamsProcessingException.class)
	public void testProcess2() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();
		processor.process(new SimpleScanParams());
	}

	@Test()
	public void testProcess3() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();

		String path;
		List<Path> pathList = new LinkedList<>();
		switch (Utils.getOSName()) {
			case LINUX:
			case MACOS:
				path = "/var";
				break;
			case WINDOWS:
				path = "C:\\Documents and Settings";
				break;
			default:
				throw new Exception("unsupported operating system");
		}
		pathList.add(Paths.get(path).toAbsolutePath().normalize());
		pathList.add(Paths.get(path).toAbsolutePath().normalize());

		List<Path> resultPathList = processor.removePathRedunduncy(pathList);

		assertEquals(1, resultPathList.size());
		assertEquals(Paths.get(path).toAbsolutePath().normalize(), resultPathList.get(0).toAbsolutePath().normalize());

	}

	@Test()
	public void testProcess4() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();

		String pathExt;
		String pathInt;
		List<Path> pathList = new LinkedList<>();
		switch (Utils.getOSName()) {
			case LINUX:
			case MACOS:
				pathExt = "/var";
				pathInt = "/var/log";
				break;
			case WINDOWS:
				pathExt = "C:\\Documents and Settings";
				pathInt = "C:\\Documents and Settings\\All Users";
				break;
			default:
				throw new Exception("unsupported operating system");
		}
		pathList.add(Paths.get(pathExt).toAbsolutePath().normalize());
		pathList.add(Paths.get(pathInt).toAbsolutePath().normalize());

		List<Path> resultPathList = processor.removePathRedunduncy(pathList);

		assertEquals(1, resultPathList.size());
		assertEquals(Paths.get(pathExt).toAbsolutePath().normalize(), resultPathList.get(0).toAbsolutePath().normalize());

	}

	@Test()
	public void testProcess5() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();

		String path;
		List<Path> resultPathList = new LinkedList<>();
		switch (Utils.getOSName()) {
			case LINUX:
			case MACOS:
				path = "/var";
				break;
			case WINDOWS:
				path = "C:\\Documents and Settings";
				break;
			default:
				throw new Exception("unsupported operating system");
		}
		resultPathList.add(Paths.get(path).toAbsolutePath().normalize());

		List<Path> excludePathList = new LinkedList<>();
		excludePathList.add(Paths.get(path).toAbsolutePath().normalize());

		SortedMap<Path, List<Path>> sortedPathMap = processor.sortExcludePathsToSearchPath(resultPathList, excludePathList);

		assertEquals(0, sortedPathMap.size());
	}

	@Test()
	public void testProcess6() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();

		String pathExt;
		String pathInt;
		List<Path> resultPathList = new LinkedList<>();
		switch (Utils.getOSName()) {
			case LINUX:
			case MACOS:
				pathExt = "/var";
				pathInt = "/var/log";
				break;
			case WINDOWS:
				pathExt = "C:\\Documents and Settings";
				pathInt = "C:\\Documents and Settings\\All Users";
				break;
			default:
				throw new Exception("unsupported operating system");
		}
		resultPathList.add(Paths.get(pathInt).toAbsolutePath().normalize());

		List<Path> excludePathList = new LinkedList<>();
		excludePathList.add(Paths.get(pathExt).toAbsolutePath().normalize());

		SortedMap<Path, List<Path>> sortedPathMap = processor.sortExcludePathsToSearchPath(resultPathList, excludePathList);

		assertEquals(0, sortedPathMap.size());
	}

	@Test()
	public void testProcess7() throws Exception {
		SimpleParamsProcessor processor = new SimpleParamsProcessor();

		String pathExt;
		String pathInt;
		List<Path> resultPathList = new LinkedList<>();
		switch (Utils.getOSName()) {
			case LINUX:
			case MACOS:
				pathExt = "/var";
				pathInt = "/var/log";
				break;
			case WINDOWS:
				pathExt = "C:\\Documents and Settings";
				pathInt = "C:\\Documents and Settings\\All Users";
				break;
			default:
				throw new Exception("unsupported operating system");
		}
		resultPathList.add(Paths.get(pathExt).toAbsolutePath().normalize());

		List<Path> excludePathList = new LinkedList<>();
		excludePathList.add(Paths.get(pathInt).toAbsolutePath().normalize());

		SortedMap<Path, List<Path>> sortedPathMap = processor.sortExcludePathsToSearchPath(resultPathList, excludePathList);

		assertEquals(1, sortedPathMap.size());
		assertEquals(Paths.get(pathExt).toAbsolutePath().normalize(), sortedPathMap.firstKey().toAbsolutePath().normalize());
		assertEquals(1, sortedPathMap.get(sortedPathMap.firstKey().toAbsolutePath().normalize()).size());
		assertEquals(Paths.get(pathInt).toAbsolutePath().normalize(), sortedPathMap.get(sortedPathMap.firstKey().toAbsolutePath().normalize()).get(0).toAbsolutePath().normalize());
	}
}
