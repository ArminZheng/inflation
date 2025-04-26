package com.arminzheng.inflation.theory.stream;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class FibonacciCompute extends RecursiveTask<Integer> {

    final int n;

    FibonacciCompute(int n) {
        // System.out.println("init: " +n);
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if (n <= 1) {
            return n;
        }
        FibonacciCompute f1 = new FibonacciCompute(n - 1);
        f1.fork(); // 将此子任务异步执行
        FibonacciCompute f2 = new FibonacciCompute(n - 2);
        int i = f2.compute() + f1.join();
        System.out.println("sum: " + i);
        return i; // 合并结果
    }

    public static void main(String[] args) {
        FibonacciCompute.class.getClass().getName();
        FibonacciCompute.class.getName();
        FibonacciCompute.class.getPackage().getName();
        Thread.currentThread().getName();
        // 计算斐波那契数列的第n项值
        ForkJoinPool pool = new ForkJoinPool(
                // 1);
                Runtime.getRuntime().availableProcessors());
        int result = pool.invoke(new FibonacciCompute(10));
        System.out.println("Fibonacci number: " + result);
    }
}
