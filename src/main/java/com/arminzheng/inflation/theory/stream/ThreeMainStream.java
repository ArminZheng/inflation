package com.arminzheng.inflation.theory.stream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * for-loop to stream program (three mainly stream-op: filter/takeWhile, map, flatMap)
 */
public class ThreeMainStream {
    // 筛选 转换 组合
    public static void test() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        LongSummaryStatistics longSummaryStatistics = Collections.synchronizedList(ids).stream()
                .parallel() // ForkJoin only
                .mapToLong(Long::longValue).summaryStatistics();

        // custom fork-join thread-pool
        ForkJoinPool customThreadPool = new ForkJoinPool(4);
        try {
            // flatMap 展平 操作 element -> Stream
            double average = customThreadPool.submit(
                    () -> LongStream.range(1, 100).flatMap(e -> LongStream.of(e, e / 2)).parallel()
                            // .peek(System.out::println)
                            .average().orElse(0)).get();
            System.out.println("average: " + average);
        } catch (InterruptedException | ExecutionException ignored) {
        } finally {
            customThreadPool.shutdown();
        }
        // -Djava.util.concurrent.ForkJoinPool.common.parallelism=16
        Runnable r = () -> IntStream
                .range(-42, +42)
                .parallel()
                .map(i -> Thread.activeCount())
                .max()
                // .mapToObj(i -> Thread.currentThread().getName())
                // .distinct()
                // .count()
                .ifPresent(System.out::println);
        ForkJoinPool.commonPool().submit(r).join();
        new ForkJoinPool(42).submit(r).join();

        LongSummaryStatistics longSummaryStatistics1 = (LongStream.range(1, 10)).parallel()
                .summaryStatistics();
        System.out.println("max: " + longSummaryStatistics.getMax());
        System.out.println("min: " + longSummaryStatistics.getMin());
        System.out.println("sum: " + longSummaryStatistics.getSum());
        System.out.println("average: " + longSummaryStatistics.getAverage());
        System.out.println("count: " + longSummaryStatistics1.getCount());
        // StreamSupport.stream(ids.spliterator(), false);

        // group by
        Map<Integer, List<Integer>> groupBy = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6)
                .takeWhile(x -> x < 7).collect(Collectors.groupingBy(i -> i % 2));
        System.out.println(groupBy);

        Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6)
                .peek(System.out::println)
                .map(String::valueOf) // Stateless: 每个元素的处理不依赖其他元素
                .peek(System.out::println)
                // .flatMap()
                .sorted() // Stateful: 需缓存整个流，大数据量可能引发 OutOfMemoryError
                // Stateful:  sorted(), distinct(), limit()
                .forEach(e -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException ignore) {
                    }
                    System.out.println(e);
                });
    }

    /**
     * special flatMap: Stream.empty().
     */
    public static void testII() {

        List<Object> conditions = List.of(List.of("a", "b"), List.of("c", "d"));
        List<String> specialFlatMap = conditions.stream().flatMap(e -> {
            // use Stream.empty() to remove ir.rel.e.vant element.
            return e instanceof List ? ((List<?>) e).stream().map(String::valueOf) : Stream.empty();
        }).collect(Collectors.toList());
        System.out.println(specialFlatMap);
    }

    /**
     * sealed code.
     */
    private String formatCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
