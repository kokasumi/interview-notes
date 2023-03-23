package com.github.kokasumi.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/17 14:55
 * @description 由于线程池和消息队列都是有界的，因此，无论客户端并发连接数有多大，
 * 它都不会导致线程个数古语膨胀或内存溢出，相比于传统的一连接一线程模型，是一种改良。
 * @since v1.0
 */
public class TimerServerHandlerExecutePool {
    private ExecutorService executorService;

    public TimerServerHandlerExecutePool(int maxPoolSize, int queueSize) {
        executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize, 120L,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
    }

    public void execute(Runnable task) {
        executorService.execute(task);
    }
}
