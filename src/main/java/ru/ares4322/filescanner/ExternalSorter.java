package ru.ares4322.filescanner;

import ru.ares4322.filescanner.utils.SortException;
import java.nio.file.Path;

/**
 * Интерфейс для сортировщика с помощью внешней памяти
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public interface ExternalSorter {

	public void sort(Path input, Path output) throws SortException;
}
