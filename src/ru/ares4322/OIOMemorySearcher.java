package ru.ares4322;

import java.io.File;

/**
 *
 * @author ares4322
 */
public class OIOMemorySearcher implements Searcher{

	@Override
	public void search(String from) {
		File dir = new File(from);
		if(dir.isDirectory()){
		
		}
	}
	
}
