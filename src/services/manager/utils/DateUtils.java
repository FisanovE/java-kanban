package services.manager.utils;

import java.time.format.DateTimeFormatter;

public class DateUtils {
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS]");
	public static DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd.MM.yy|HH:mm");
}
