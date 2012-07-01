package ru.ares4322.filescanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map.Entry;
import java.util.*;
import java.util.concurrent.*;
import ru.ares4322.filescanner.args.ScanParams;
import ru.ares4322.filescanner.args.SimpleScanParams;

/**
 * @todo надо сделать сохранение во временные файлы размера посчитанного буфера в каждом потоке и далее сделать внешнюю сортировку этих файлов
 *
 * Сканер файлов с помощью nio-пакета из JDK7. Распараллеливает сканирование
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

		//создаем файлы-буфер для всех потоков. время на синхронизация при записи будет меньше,
		//чем время записи на диск в разные файлы
		try (PrintWriter bufferWriter = new PrintWriter(Files.newBufferedWriter(outputParams.outputFilePath, Charset.forName("UTF-8")))) {
			//List<FileInfo> resultList = new LinkedList<>();
			int curTaskCounter = 0;

			//создаем фиксированный пул потоков, размер которого равен количеству дисков, на которых расположены наши файлы
			Map<String, SortedMap<Path, List<Path>>> diskToPathMap = scanParams.getPathMapsToDisk();
			int diskQuantity = diskToPathMap.size();
			if (diskQuantity <= 0) {
				throw new ScanException("No paths to scan");
			}

			ExecutorService executorService = Executors.newFixedThreadPool(diskQuantity);
			ExecutorCompletionService<ExtendedScanResult> executorCompletionService = new ExecutorCompletionService<>(executorService);

			int maxtempFiles = 1024;
			long blockSize = ExternalSort.estimateBestSizeOfBlocks(new File(outputParams.outputFilePath.toAbsolutePath().toString()), maxtempFiles);
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

				executorCompletionService.submit(new ExtendedFileVisitorTask(scanPath, excludePathList, diskName, blockSize));
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
						//resultList.addAll(scanResult.resultPathList);
						String diskName = scanResult.diskName;

						SortedMap<Path, List<Path>> scanToExcludeListMap = diskToPathMap.get(diskName);
						if (scanToExcludeListMap != null) {
							Path scanPath = scanToExcludeListMap.firstKey();
							List<Path> excludePathList = scanToExcludeListMap.get(scanPath);
							scanToExcludeListMap.remove(scanPath);
							if (scanToExcludeListMap.isEmpty()) {
								diskToPathMap.remove(diskName);
							}
							executorCompletionService.submit(new ExtendedFileVisitorTask(scanPath, excludePathList, diskName, blockSize));
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

			bufferWriter.flush();

			Comparator<String> comparator = new Comparator<String>() {

				@Override
				public int compare(String r1, String r2) {
					return r1.compareTo(r2);
				}
			};

			int maxtmpfiles = 1024;
			Charset cs = Charset.forName("UTF-8");
			List<File> l = ExternalSort.sortInBatch(new File(outputParams.outputFilePath.toAbsolutePath().toString()), comparator, maxtmpfiles, cs, null);
			System.out.println("created " + l.size() + " tmp files");
			ExternalSort.mergeSortedFiles(l, new File("/tmp/filescanner/res.txt"), comparator, cs);

			//this.sortResultFile(outputParams.outputFilePath);

			//Utils.writePathListToFileExt(outputParams.getOutputFilePath(), resultList, outputParams.getOutputFileCharset());
		} catch (IOException ex) {
			throw new ScanException("fail create buffer file", ex);
		}
	}

	/**
	 * Сортирует файл с результатами
	 *
	 * @param resultFilePath
	 */
	protected void sortResultFile(Path resultFilePath) {
		Charset charset = Charset.forName("UTF-8");

		try (BufferedReader initFileReader = new BufferedReader(Files.newBufferedReader(resultFilePath, charset))) {

			//создаем вспомогательных файла
			Path auxFile1 = Paths.get("/tmp/filescanner/aux1.txt");
			Path auxFile2 = Paths.get("/tmp/filescanner/aux2.txt");
			Path auxFile3 = Paths.get("/tmp/filescanner/aux3.txt");
			Path auxFile4 = Paths.get("/tmp/filescanner/aux4.txt");
			Files.deleteIfExists(auxFile1);
			Files.deleteIfExists(auxFile2);
			Files.deleteIfExists(auxFile3);
			Files.deleteIfExists(auxFile4);
			Files.createFile(auxFile1);
			Files.createFile(auxFile2);
			Files.createFile(auxFile3);
			Files.createFile(auxFile4);

			//читаем исходный файл отрезками длины bufferSize, каждый отрезок сортируем и попеременно пишем то в Aux1, то в Aux2
			int bufferSize = 1000;
			List<String> buffer = new ArrayList<>(bufferSize);
			Path[] auxFileArray = new Path[2];
			auxFileArray[0] = auxFile1;
			auxFileArray[1] = auxFile2;
			int currentAuxFileIndex = 0;
			Path currentAuxFile = auxFileArray[currentAuxFileIndex];
			String line;
			int curBufferSize = 0;
			while ((line = initFileReader.readLine()) != null) {
				if (curBufferSize == bufferSize) {
					Collections.sort(buffer);
					Files.write(currentAuxFile, buffer, charset, StandardOpenOption.APPEND);
					buffer.clear();
					curBufferSize = 0;
					currentAuxFileIndex = (currentAuxFileIndex == 0) ? 1 : 0;
					currentAuxFile = auxFileArray[currentAuxFileIndex];
				}
				buffer.add(curBufferSize, line);
				curBufferSize++;
			}
			if (curBufferSize > 0) {
				List<String> lastBuffer = buffer.subList(0, curBufferSize - 1);
				Collections.sort(lastBuffer);
				Files.write(currentAuxFile, lastBuffer, charset, StandardOpenOption.APPEND);
			}

			//читаем файлы Aux1 и Aux2 попеременно отрезками по bufferSize/2, сортируем и пишем попеременно то в Aux3, то в Aux4
			BufferedReader[][] auxReaders = new BufferedReader[2][2];
			auxReaders[0][0] = new BufferedReader(Files.newBufferedReader(auxFile1, charset));
			auxReaders[0][1] = new BufferedReader(Files.newBufferedReader(auxFile2, charset));
			auxReaders[1][0] = new BufferedReader(Files.newBufferedReader(auxFile3, charset));
			auxReaders[1][1] = new BufferedReader(Files.newBufferedReader(auxFile4, charset));

			Path[][] auxFiles = new Path[2][2];
			auxFiles[0][0] = auxFile1;
			auxFiles[0][1] = auxFile2;
			auxFiles[1][0] = auxFile3;
			auxFiles[1][1] = auxFile4;

			int curAuxReaderGroupIndex = 0;
			int curAuxReaderIndex = 0;
			BufferedReader curAuxReader = auxReaders[curAuxReaderGroupIndex][curAuxReaderIndex];
			currentAuxFile = auxFile3;
			currentAuxFileIndex = 0;
			String line1;
			curBufferSize = 0;
			int oneFileBufferNumbers = 1;
			while ((line1 = curAuxReader.readLine()) != null) {
				if (curBufferSize == bufferSize / 2) {
					curAuxReaderIndex = 1;
					curAuxReader = auxReaders[curAuxReaderGroupIndex][curAuxReaderIndex];
				} else if (curBufferSize == bufferSize) {
					Collections.sort(buffer);
					Files.write(currentAuxFile, buffer, charset, StandardOpenOption.APPEND);
					buffer.clear();
					curBufferSize = 0;
					curAuxReaderIndex = 0;
					curAuxReader = auxReaders[curAuxReaderGroupIndex][curAuxReaderIndex];
					currentAuxFileIndex = (currentAuxFileIndex == 0) ? 1 : 0;
					currentAuxFile = auxFiles[(curAuxReaderGroupIndex == 0) ? 1 : 0][currentAuxFileIndex];
				}
				buffer.add(curBufferSize, line1);
				curBufferSize++;
			}
			//тут надо дочитать другой файл

			auxReaders[0][0].close();
			auxReaders[0][1].close();
			auxReaders[1][0].close();
			auxReaders[1][1].close();

		} catch (IOException ex) {
			System.err.println(ex);
		}
	}
}
