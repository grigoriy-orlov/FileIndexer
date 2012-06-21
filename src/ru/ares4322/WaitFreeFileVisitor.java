package ru.ares4322;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
class WaitFreeFileVisitor implements Callable<List<Path>> {

	private AbstractQueue<Path> pathQueue;
	private LinkedList<Path> resultList;
	private static byte classCounter = 1;
	private byte classNumber;
	private List<Path> excludePathList;

	public WaitFreeFileVisitor(AbstractQueue<Path> pathQueue, List<Path> excludePathList) {
		this.pathQueue = pathQueue;
		this.resultList = new LinkedList<>();
		this.excludePathList = excludePathList;
		classNumber = classCounter;
		classCounter++;
		System.out.println("start: " + classNumber);
	}

	@Override
	public List<Path> call() throws Exception {

		while (this.pathQueue.isEmpty() == false) {
			System.out.println("process: " + classNumber);
			Path path = this.pathQueue.poll();

			boolean addPath = Utils.searchPathInList(path, this.excludePathList);

			if (addPath) {
				this.resultList.add(path.toAbsolutePath());

				if (Files.isDirectory(path)) {
					try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
						for (Iterator<Path> it = directoryStream.iterator(); it.hasNext();) {
							Path pathInDirectory = it.next();
							if (Files.isDirectory(pathInDirectory)) {
								this.pathQueue.add(pathInDirectory);
							} else if (Files.isRegularFile(pathInDirectory)) {
								this.resultList.add(pathInDirectory.toAbsolutePath());
							}
							//@todo сюда тоже нужна обработка исключения? (когда файл в этой директории не открывается)
						}
					} catch (DirectoryIteratorException ex) {
						System.out.println("Fail visit file: " + ex.getCause().getMessage());
					} catch (IOException ex) {
						Logger.getLogger(RecursiveFileVisitor.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		}
		return this.resultList;
	}
}
