package ru.ares4322.filescanner;

import ru.ares4322.filescanner.args.ScanParams;

/**
 * Интерфейс сканера файлов
 *
 * @author ares4322
 *
 */
public interface FileScanner {

	public void scan(ScanParams scanParams, ScanResultOutputParams scanResultOutputParams) throws ScanException;
}
