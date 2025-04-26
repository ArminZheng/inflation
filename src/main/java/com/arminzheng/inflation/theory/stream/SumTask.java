package com.arminzheng.inflation.theory.stream;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class SumTask extends RecursiveTask<Long> {
    private final long[] array;
    private final int start;
    private final int end;
    private static final int THRESHOLD = 10; // 基线条件

    public SumTask(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        // 如果任务的大小小于或等于阈值，则直接计算和
        if (end - start <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum; // 返回计算结果
        } else {
            // 否则，分解任务
            int mid = (start + end) / 2;
            // 创建左侧和右侧子任务
            SumTask leftTask = new SumTask(array, start, mid);
            SumTask rightTask = new SumTask(array, mid, end);
            
            // 异步启动左侧任务
            leftTask.fork();
            // 同步计算右侧任务 并等待其完成
            return rightTask.compute() + leftTask.join();
        }
    }

    public static void main(String[] args) {
        long[] array = new long[100];
        for (int i = 0; i < 100; i++) {
            array[i] = i + 1; // 初始化数组
        }
        // 创建 ForkJoinPool
        ForkJoinPool pool = new ForkJoinPool();
        // 创建任务实例
        SumTask task = new SumTask(array, 0, array.length);
        // 执行任务并获取结果
        long result = pool.invoke(task);
        // 输出结果
        System.out.println("数组的总和: " + result);
    }
}
