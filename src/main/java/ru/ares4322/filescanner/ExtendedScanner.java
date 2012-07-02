package ru.ares4322.filescanner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import ru.ares4322.filescanner.args.ScanParams;
import ru.ares4322.filescanner.args.SimpleScanParams;

/**
 * Сканер файлов с помощью nio-пакета из JDK7. Использует внешнюю память для
 * промежуточного хранения информации о файлах. Распараллеливает сканирование
 * таким образом, чтобы одновременно не выполнялось сканирование для путей с
 * одного диска. Количество потоков делается равным количеству дисков, а не
 * количеству ядер (процессоров), так как сканирование все равно блокируется на
 * вводе/выводе.
 *
 * @author ares4322
 */
public class ExtendedScanner implements FileScanner {

	@Override
	public void scan(ScanParams params, ScanResultOutputParams outputParams) throws ScanException {
		SimpleScanParams scanParams = (SimpleScanParams) params;

		//создаем один файл-буфер для всех потоков.
		//при дальнейшем развитии программы можно попробовать сделать разбиение на файлы заданного размера(чтобы можно было целиком прочитать в память),
		//на этапе перебора файлов потоками. но не факт, что это будет быстрее во всех случаях
		PrintWriter tempWriter = null;
		try {
			Path tempFile = Files.createTempFile(null, null);
			tempFile.toFile().deleteOnExit();
			tempWriter = new PrintWriter(Files.newBufferedWriter(tempFile, Charset.forName("UTF-8")));
			int curTaskCounter = 0;

			//создаем фиксированный пул потоков, размер которого равен количеству дисков, на которых расположены наши файлы
			Map<String, SortedMap<Path, List<Path>>> diskToPathMap = scanParams.getPathMapsToDisk();
			int diskQuantity = diskToPathMap.size();
			if (diskQuantity <= 0) {
				throw new ScanException("No paths to scan");
			}

			ExecutorService executorService = Executors.newFixedThreadPool(diskQuantity);
			ExecutorCompletionService<ExtendedScanResult> executorCompletionService = new ExecutorCompletionService<>(executorService);

			//заполняем пул задачами для каждого диска
			//при получении задачи удаляем ее из словаря и если словарь после этого пуст, то удаляем и его
			for (Iterator<Entry<String, SortedMap<Path, List<Path>>>> it = diskToPathMap.entrySet().iterator(); it.hasNext();) {
				Entry<String, SortedMap<Path, List<Path>>> entry = it.next();
				String diskName = entry.getKey();
				SortedMap<Path, List<Path>> scanToExcludeListMap = entry.getValue();
				Path scanPath = scanToExcludeListMap.firstKey();
				List<Path> excludePathList = scanToExcludeListMap.get(scanPath);
				scanToExcludeListMap.remove(scanPath);
				if (scanToExcludeListMap.isEmpty()) {
					it.remove();
				}

				executorCompletionService.submit(new ExtendedFileVisitorTask(scanPath, excludePathList, diskName, tempWriter));
				curTaskCounter++;
			}

			//до тех пор, пока не выполнятся все задачи, получаем результат самой быстро выполненной на данный момент задачи,
			//если для диска, для которого была эта выполненная задача, есть еще задачи, то добавляем ее на выполнение.
			//если задач больше нет, то завершаем работу
			while (true) {
				try {
					if (curTaskCounter > 0) {
						Future<ExtendedScanResult> future = executorCompletionService.take();
						curTaskCounter--;

						ExtendedScanResult scanResult = future.get();
						String diskName = scanResult.diskName;

						SortedMap<Path, List<Path>> scanToExcludeListMap = diskToPathMap.get(diskName);
						if (scanToExcludeListMap != null) {
							Path scanPath = scanToExcludeListMap.firstKey();
							List<Path> excludePathList = scanToExcludeListMap.get(scanPath);
							scanToExcludeListMap.remove(scanPath);
							if (scanToExcludeListMap.isEmpty()) {
								diskToPathMap.remove(diskName);
							}
							executorCompletionService.submit(new ExtendedFileVisitorTask(scanPath, excludePathList, diskName, tempWriter));
							curTaskCounter++;
						}
					} else {
						break;
					}
				} catch (InterruptedException | ExecutionException ex) {
					System.err.println(new StringBuilder(2).append("ERROR: ").append(ex.getMessage()));
					curTaskCounter--;
				}
			}

			executorService.shutdown();

			tempWriter.flush();

			//разбиваем файл с промежуточными результатами на файлы такого размера, чтобы один такой файл целиком влезал в память.
			//пере записью на диск сортируем такие файлы
			List<File> l = ExternalSort.sortInBatch(tempFile.toFile());
			//сливаем временные файлы в один результирующий.
			//читаем построчно каждый файл и меньшую строку (лексикографически) пишем в результирующий файл
			ExternalSort.mergeSortedFiles(l, outputParams.outputFilePath.toFile());

		} catch (IOException ex) {
			throw new ScanException(ex);
		} finally {
			if (tempWriter != null) {
				tempWriter.close();
			}
		}
	}
}
