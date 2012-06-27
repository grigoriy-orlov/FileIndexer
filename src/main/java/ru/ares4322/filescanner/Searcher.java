package ru.ares4322.filescanner;

import ru.ares4322.filescanner.args.SearchParams;

/**
 * Все подклассы работают со списками путей (в строках), так как это более гибко для будущего модифицирования.
 * Можно работать с массивами, тогда, возможно(!), будет быстрее работать и меньше потребляться память.
 * Но оптимизации лучше проводить потом.
 * @author ares4322
 *
 * @todo надо сделать обработку пути вывода в параметрах и в поисковиках
 */
public interface Searcher {

	public void search(SearchParams params);
}
