package ru.ares4322.filescanner;

/**
 * Перечисление для типа операционной системы
 *
 * @author Gregory Orlov <orlov@navtelecom.ru>
 */
public enum OSTypeEnum {

	UNKNOWN(1),
	LINUX(2),
	WINDOWS(3),
	MACOS(4);
	public final int ID;

	OSTypeEnum(int typeId) {
		this.ID = typeId;
	}
}
