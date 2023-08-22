package Server;

import Logger.Logger;

import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadPool {
    static int minThreadNum = 2;

    private HashSet<Thread> threads = new HashSet<>();

    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private final AtomicInteger currentNumThread = new AtomicInteger(0);
    private final int maxThreadNum;
    private final ReentrantLock mainLock = new ReentrantLock();
    private final AtomicBoolean isShutdown = new AtomicBoolean();

    public MyThreadPool(int maxThreadNum) {
        this.maxThreadNum = Math.max(Math.max(minThreadNum, Runtime.getRuntime().availableProcessors()), maxThreadNum);
    }

    public void shutdown() {
        this.threads.forEach(Thread::interrupt);
        isShutdown.set(true);
    }

    public boolean isShutdown() {
        return isShutdown.get();
    }

    public void execute(Runnable task) {
        if (task == null) throw new NullPointerException();

        if (currentNumThread.get() < maxThreadNum) {
            runTask(task);
        } else {
            workQueue.add(task);
        }

    }

    private Thread runTask(Runnable task) {
        Worker worker = new Worker();
        try {
            currentNumThread.addAndGet(1);
            mainLock.lock();
            Runnable workerTask = () -> {
                try {
                    task.run();
                } finally {
                    threadExitEffect(Thread.currentThread());
                }
            };
            worker.bindTask(workerTask);
            Thread thread = worker.run();
            this.threads.add(thread);
        } catch (Exception error) {
            threadExitEffect(worker.getThread());
            Logger.getInstance().logErr(error);
        } finally {
            mainLock.unlock();
        }
        return worker.getThread();
    }

    private void threadExitEffect(Thread thread) {
        try {
            mainLock.lock();
            currentNumThread.addAndGet(-1);
            threads.remove(thread);
            tryExecuteTasksInQueue();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mainLock.unlock();
        }
    }

    private void tryExecuteTasksInQueue() throws InterruptedException {
        while (currentNumThread.get() < maxThreadNum) {
           var task = workQueue.poll(1000, TimeUnit.MILLISECONDS);
           runTask(task);
        }
    }
}

class Worker {
    private Runnable task;
    private Thread thread;

    public Worker() {}

    public void bindTask(Runnable task) {
        this.task = task;
    }

    public Thread run() {
        Thread thread = new Thread(task);
        this.thread = thread;
        thread.start();
        return thread;
    }

    public Thread getThread() {
        return thread;
    }
}