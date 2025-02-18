package ru.fokin;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final List<WorkerThread> workers;
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    private final Object lock = new Object();

    public CustomThreadPool(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.taskQueue = new LinkedBlockingQueue<>();
        this.workers = new LinkedList<>();

        for (int i = 0; i < capacity; i++) {
            WorkerThread worker = new WorkerThread();
            synchronized (lock) {
                workers.add(worker);
            }
            worker.start();
        }
    }

    public void execute(Runnable task) {
        if (isShutdown.get()) {
            throw new IllegalStateException("ThreadPool is shutdown");
        }
        if (task == null) {
            throw new NullPointerException("Task cannot be null");
        }
        taskQueue.offer(task);
    }

    public void shutdown() {
        isShutdown.set(true);
        for (WorkerThread worker : workers) {
            worker.interrupt();
        }
    }

    public void awaitTermination() throws InterruptedException {
        for (WorkerThread worker : workers) {
            worker.join();
        }
    }

    private class WorkerThread extends Thread {
        @Override
        public void run() {
            while (!isShutdown.get() || !taskQueue.isEmpty()) {
                try {
                    Runnable task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (task != null) {
                        task.run();
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CustomThreadPool pool = new CustomThreadPool(3);

        for (int i = 0; i < 10; i++) {
            int taskId = i;
            pool.execute(() -> {
                System.out.println("Task " + taskId + " is running on " + Thread.currentThread().getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination();
        System.out.println("All tasks completed");
    }

}
