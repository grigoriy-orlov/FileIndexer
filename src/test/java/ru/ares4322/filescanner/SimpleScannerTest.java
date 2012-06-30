/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ares4322.filescanner;

import java.nio.file.Path;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Test;
import ru.ares4322.filescanner.args.SimpleScanParams;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public class SimpleScannerTest {

	@Test(expected = ScanException.class)
	public void testScan1() throws Exception {
		SimpleScanParams params = new SimpleScanParams();
		params.setPathMapsToDisk(new TreeMap<String, SortedMap<Path, List<Path>>>());
		ScanResultOutputParams outputParams = null;
		SimpleScanner scanner = new SimpleScanner();
		scanner.scan(params, outputParams);
	}
}
