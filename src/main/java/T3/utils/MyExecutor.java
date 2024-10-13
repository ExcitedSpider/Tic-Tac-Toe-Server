package T3.utils;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyExecutor {
    private MyExecutor() {};

    private static MyExecutor instance;

    public static MyExecutor getInstance(){
        if(instance == null){
            instance = new MyExecutor();
        }

        return instance;
    }

    private ExecutorService threadpoll = Executors.newFixedThreadPool(4);

    public void execute(Runnable task) {
        threadpoll.execute(task);
    }
}
