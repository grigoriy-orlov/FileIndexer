package ru.ares4322.filescanner;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;

/**
 * Класс сортировщика с помощью внешней памяти. Разбивает сортируемый файл на
 * промежуточные файлы такого размера, чтобы каждый из них мог поместиться
 * целиком в память. Затем сортирует их. Далее сливает их в один, читая из
 * каждого по строке и записывая наименьшую в итоговый файл. По-умолчанию предел
 * количества промежуточных файлов - 1024. Кодировка по-умолчанию UTF-8. При
 * дальнейшем развитии можно вынести настройки данного алгоритма (например,
 * предел количества временных файлов) в параметры командной строки.
 *
 * @author By (in alphabetical order) Philippe Beaudoin, Jon Elsas, Christan
 * Grant, Daniel Haran, Daniel Lemire, April 2010 originally posted at
 * http://www.daniel-lemire.com/blog/archives/2010/04/01/external-memory-sorting-in-java/
 */
class SimpleExternalSorter implements ExternalSorter {

	/**
	 * Класс, с помощью которого получаются верхние строки из файлов
	 */
	class BinaryFileBuffer {

		public final int BUFFERSIZE = 2048;
		public BufferedReader fbr;
		public File originalfile;
		private String cache;
		private boolean empty;

		public BinaryFileBuffer(File f, Charset cs) throws IOException {
			originalfile = f;
			fbr = new BufferedReader(new InputStreamReader(new FileInputStream(f), cs), BUFFERSIZE);
			reload();
		}

		public boolean empty() {
			return empty;
		}

		private void reload() throws IOException {
			try {
				if ((this.cache = fbr.readLine()) == null) {
					empty = true;
					cache = null;
				} else {
					empty = false;
				}
			} catch (EOFException oef) {
				empty = true;
				cache = null;
			}
		}

		public void close() throws IOException {
			fbr.close();
		}

		public String peek() {
			if (empty()) {
				return null;
			}
			return cache.toString();
		}

		public String pop() throws IOException {
			String answer = peek();
			reload();
			return answer;
		}
	}
	final int DEFAULT_MAXTEMPFILES = 1024;
	final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/**
	 * Сортирует файл inputFile и пишет результат в outputFile
	 */
	@Override
	public void sort(Path input, Path output) throws SortException {
		try {
			//разбиваем файл с промежуточными результатами на файлы такого размера, чтобы один такой файл целиком влезал в память.
			//перед записью на диск сортируем такие файлы
			List<File> l = this.sortInBatch(input.toFile());

			//сливаем временные файлы в один результирующий.
			//читаем построчно каждый файл и каждый раз пишем в результирующий файл меньшую строку (лексикографически)
			this.mergeSortedFiles(l, output.toFile());
		} catch (Exception ex) {
			throw new SortException(ex);
		}
	}

	/**
	 * Разбиваем сортируемый файл на блоки. Если блоки будут слишком маленькие,
	 * то будет слишком много вспомогательных файлов. Если слишком большие, то
	 * они не влезут в память.
	 */
	protected long estimateBestSizeOfBlocks(File filetobesorted, int maxtmpfiles) {
		//если промежуточный результат будет не в UTF-8, а в кодировке с меньшим количеством байт на символ, то надо использовать следующую строку
		//long sizeoffile = filetobesorted.length() * 2;
		long sizeoffile = filetobesorted.length();

		//изначально не планируется использовать более, чем maxtmpfiles вспомогательных файлов, лучше пусть сначала не хватит памяти
		long blocksize = sizeoffile / maxtmpfiles + (sizeoffile % maxtmpfiles == 0 ? 0 : 1);

		//с другой стороны много вспомогательных файлов - тоже плохо. Если размер таких файлов меньше половину доступной памяти - надо этот размер увеличить.
		long freemem = Runtime.getRuntime().freeMemory();
		if (blocksize < freemem / 2) {
			blocksize = freemem / 2;
		}
		return blocksize;
	}

	/**
	 * Загружает файл блоками заданного размера, сортирует их в памяти и пишет
	 * блоки во временные файлы. Использует параметры по-умолчанию
	 */
	protected List<File> sortInBatch(File file) throws IOException {
		Comparator<String> comparator = new Comparator<String>() {

			@Override
			public int compare(String r1, String r2) {
				return r1.compareTo(r2);
			}
		};
		return sortInBatch(file, comparator, DEFAULT_MAXTEMPFILES, DEFAULT_CHARSET, null);
	}

	/**
	 * Загружает файл блоками заданного размера, сортирует их в памяти и пишет
	 * блоки во временные файлы
	 */
	protected List<File> sortInBatch(File file, Comparator<String> cmp, int maxtmpfiles, Charset cs, File tmpdirectory) throws IOException {
		List<File> files = new ArrayList<>();
		BufferedReader fbr = new BufferedReader(new InputStreamReader(new FileInputStream(file), cs));
		long blocksize = estimateBestSizeOfBlocks(file, maxtmpfiles);// в байтах

		try {
			List<String> tmplist = new ArrayList<>();
			String line = "";
			try {
				while (line != null) {
					long currentblocksize = 0;// в байтах
					while ((currentblocksize < blocksize)
							&& ((line = fbr.readLine()) != null)) { // пока хватает памяти
						tmplist.add(line);
						currentblocksize += line.length();
						//currentblocksize += line.length() * 2; // если будет поддержка других кодировок
					}
					files.add(sortAndSave(tmplist, cmp, cs, tmpdirectory));
					tmplist.clear();
				}
			} catch (EOFException oef) {
				if (tmplist.size() > 0) {
					files.add(sortAndSave(tmplist, cmp, cs, tmpdirectory));
					tmplist.clear();
				}
			}
		} finally {
			fbr.close();
		}
		return files;
	}

	/**
	 * Сортирует список и сохраняет его во временный файл
	 */
	protected File sortAndSave(List<String> tmplist, Comparator<String> cmp, Charset cs, File tmpdirectory) throws IOException {
		Collections.sort(tmplist, cmp);
		File newtmpfile = File.createTempFile("sortInBatch", "flatfile", tmpdirectory);
		newtmpfile.deleteOnExit();
		try (BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newtmpfile), cs))) {
			for (String r : tmplist) {
				fbw.write(r);
				fbw.newLine();
			}
		}
		return newtmpfile;
	}

	/**
	 * Сливает вместе временные файлы
	 *
	 * @return Количество обработанных строки
	 */
	protected int mergeSortedFiles(List<File> files, File outputfile) throws Exception {
		Comparator<String> comparator = new Comparator<String>() {

			@Override
			public int compare(String r1, String r2) {
				return r1.compareTo(r2);
			}
		};
		return mergeSortedFiles(files, outputfile, comparator, DEFAULT_CHARSET, Utils.getPathDelimeter());
	}

	/**
	 * Сливает вместе временные файлы
	 *
	 * @return Количество обработанных строки
	 */
	protected int mergeSortedFiles(List<File> files, File outputfile, final Comparator<String> cmp, Charset cs, String delimeter) throws IOException {
		PriorityQueue<BinaryFileBuffer> pq = new PriorityQueue<>(11,
				new Comparator<BinaryFileBuffer>() {

					@Override
					public int compare(BinaryFileBuffer i, BinaryFileBuffer j) {
						return cmp.compare(i.peek(), j.peek());
					}
				});
		for (File f : files) {
			BinaryFileBuffer bfb = new BinaryFileBuffer(f, cs);
			pq.add(bfb);
		}
		BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfile), cs));
		int rowcounter = 0;
		try {
			while (pq.size() > 0) {
				BinaryFileBuffer bfb = pq.poll();
				String r = bfb.pop();
				//преобразуем информацию о файле в заданный формат
				String[] splitedString = r.split(delimeter);
				fbw.write("[");
				fbw.newLine();
				fbw.write("file = ");
				fbw.write(splitedString[0]);
				fbw.newLine();
				fbw.write("date = ");
				fbw.write(splitedString[1]);
				fbw.newLine();
				fbw.write("size = ");
				fbw.write(splitedString[2]);
				fbw.write("]");
				++rowcounter;
				if (bfb.empty()) {
					bfb.fbr.close();
					bfb.originalfile.delete();// больше не нужен
				} else {
					pq.add(bfb); // добавляем обратно
				}
			}
		} finally {
			fbw.close();
			for (BinaryFileBuffer bfb : pq) {
				bfb.close();
			}
		}
		return rowcounter;
	}
}
