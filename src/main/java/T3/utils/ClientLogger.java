package T3.utils;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import java.util.Date;

public class ClientLogger {
    private ClientLogger() {}
    static public  final ClientLogger instance = new ClientLogger();


    public void error(String error) {
        System.err.println(error);
    }

    public void error(Exception error){
        System.err.println(error.getMessage());
    }

    public void info(String info) {
        System.out.println(info);
    }
}
