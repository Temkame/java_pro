package ru.fokin;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomThreadPool {
    private final BlockingQueue<Runnable> taskQueue; // Очередь задач
    private final List<WorkerThread> workers; // Список рабочих потоков
    private volatile boolean isShutdown = false; // Флаг завершения работы пула

    // Конструктор пула потоков
    public CustomThreadPool(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.taskQueue = new LinkedBlockingQueue<>(); // Инициализация очереди задач
        this.workers = new LinkedList<>(); // Инициализация списка потоков

        // Создание и запуск рабочих потоков
        for (int i = 0; i < capacity; i++) {
            WorkerThread worker = new WorkerThread();
            workers.add(worker);
            worker.start();
        }
    }

    // Метод для добавления задачи в пул
    public void execute(Runnable task) {
        if (isShutdown) {
            throw new IllegalStateException("ThreadPool is shutdown");
        }
        if (task == null) {
            throw new NullPointerException("Task cannot be null");
        }
        taskQueue.offer(task); // Добавление задачи в очередь
    }

    // Метод для завершения работы пула
    public void shutdown() {
        isShutdown = true; // Устанавливаем флаг завершения
        for (WorkerThread worker : workers) {
            worker.interrupt(); // Прерываем все потоки
        }
    }

    // Метод для ожидания завершения всех задач
    public void awaitTermination() throws InterruptedException {
        for (WorkerThread worker : workers) {
            worker.join(); // Ожидаем завершения каждого потока
        }
    }

    // Внутренний класс, представляющий рабочий поток
    private class WorkerThread extends Thread {
        @Override
        public void run() {
            while (!isShutdown || !taskQueue.isEmpty()) {
                try {
                    Runnable task = taskQueue.poll(); // Берем задачу из очереди
                    if (task != null) {
                        task.run(); // Выполняем задачу
                    } else {
                        Thread.sleep(100); // Если очередь пуста, ждем
                    }
                } catch (InterruptedException e) {
                    // Поток прерван, завершаем работу
                    break;
                }
            }
        }
    }

    // Пример использования
    public static void main(String[] args) throws InterruptedException {
        CustomThreadPool pool = new CustomThreadPool(3); // Создаем пул из 3 потоков

        // Добавляем задачи в пул
        for (int i = 0; i < 10; i++) {
            int taskId = i;
            pool.execute(() -> {
                System.out.println("Task " + taskId + " is running on " + Thread.currentThread().getName());
                try {
                    Thread.sleep(500); // Имитация работы задачи
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        pool.shutdown(); // Завершаем работу пула
        pool.awaitTermination(); // Ожидаем завершения всех задач
        System.out.println("All tasks completed");
    }
}
