/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

package T3Server.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Logger {
    private final static Logger instance = new Logger();
    public static Logger getInstance() {
        return instance;
    }

    public void logInfo(String content) {
        System.out.println("[Info]" + "[" + Logger.getCurrentTimeString() + "]" + content);
    }

    public void logInfo(Object obj) {
        System.out.println("[Info]" + "[" + Logger.getCurrentTimeString() + "]" + obj.toString());
    }

    public void logErr(Exception exception) {
        System.err.println("[Error]" + "[" + Logger.getCurrentTimeString() + "]" + exception.getMessage() + "\n" + Arrays.toString(exception.getStackTrace()));
        exception.printStackTrace(System.err);
    }

    public void logErr(String content) {
        System.err.println("[Error]" + "[" + Logger.getCurrentTimeString() + "]" + content);
    }




    static private String getCurrentTimeString() {
        LocalDateTime currentTime = LocalDateTime.now();
        var datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentTime.format(datePattern);
    }
}
