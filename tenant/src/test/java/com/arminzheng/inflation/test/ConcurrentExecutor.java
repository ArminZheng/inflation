package com.arminzheng.inflation.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConcurrentExecutor<R> {

    public final ExecutorService executorService;

    /**
     * 信号量
     */
    private int permits;

    /**
     * 默认信号量大小
     */
    public static final int DEFAULT_PERMITS = 4;

    /**
     * 信号量实例
     */
    private final Semaphore semaphore;

    public static final int SEMAPHORE_TIMEOUT_MILL_SEC = 100;
    /**
     * 需要异步执行的任务
     */
    private List<R> syncExecuteResult = new ArrayList<>();

    private List<CompletableFuture<R>> futureList = new ArrayList<>();

    public ConcurrentExecutor(int permits, ExecutorService executorService) {
        this.permits = permits;
        this.semaphore = new Semaphore(permits);// 并发数；控制
        this.executorService = executorService;
    }

    public static <R> ConcurrentExecutor<R> newConService(int permits,
            ExecutorService executorService) {
        return new ConcurrentExecutor<>(permits, executorService);
    }

    public static <R> ConcurrentExecutor<R> newConService(ExecutorService executorService) {
        return new ConcurrentExecutor<>(ConcurrentExecutor.DEFAULT_PERMITS, executorService);
    }

    /**
     * 提交单个任务
     *
     * @return
     */
    public ConcurrentExecutor<R> submit(Supplier<R> supplier) {
        boolean acquire;
        try {
            acquire = semaphore.tryAcquire(SEMAPHORE_TIMEOUT_MILL_SEC, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            acquire = false;
        }
        if (acquire) {
            CompletableFuture<R> future = CompletableFuture.supplyAsync(supplier, executorService);
            future.whenComplete((t, throwable) -> semaphore.release());
            futureList.add(future);
        } else {
            syncExecuteResult.add(supplier.get());
        }
        return this;
    }

    /**
     * 等待所有任务结束，无超时
     *
     * @return CompletableFuture<List < R>>
     */
    public CompletableFuture<List<R>> invokeAllFuture() {
        return CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                .thenApply(e -> futureList.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .whenComplete((rs, throwable) -> {
                    if (rs != null && syncExecuteResult != null && syncExecuteResult.size() > 0) {
                        rs.addAll(syncExecuteResult);
                    }
                });
    }

    public List<R> invokeAll() {
        return invokeAllFuture().join();
    }
}
