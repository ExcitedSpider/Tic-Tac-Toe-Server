package interfaces.common;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class DateUtil {

    public static String now() {
        return LocalDateTime.now().format(ISO_DATE_TIME);
    }
    public static LocalDateTime parse(String datetimeString) {
        return LocalDateTime.parse(datetimeString, ISO_DATE_TIME);
    }
}
