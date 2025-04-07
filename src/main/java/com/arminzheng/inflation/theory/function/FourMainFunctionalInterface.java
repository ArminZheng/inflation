package com.arminzheng.inflation.theory.function;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * four mainly functional-interface
 */
public class FourMainFunctionalInterface {
    // 消费 andThen
    Consumer<Void> consumer;  // accept()
    // 供给
    Supplier<Void> supplier;  // get()
    // 函数(转换器) andThen compose
    Function<Void, Void> function;  // apply()
    // 断定 and negate(not) or isEqual
    Predicate<Void> predicate;  // test()
    // compose with
    boolean b;
    int i;
    long l;
    double d;

    public static void test() {
        Supplier<String> supplier = () -> "42a";
        Predicate<String> predicate = x -> x.matches("-?\\d+(\\.\\d+)?");
        Function<String, Double> function = Double::parseDouble;
        Consumer<Double> consumer = System.out::println;

        // test accept apply get
        if (predicate.test(supplier.get())) {
            consumer.accept(function.apply(supplier.get()));
        }
        if (predicate.negate().test(supplier.get())) {
            System.err.println(supplier.get() + " is not matched.");
        }
        if (Predicate.not(predicate).test(supplier.get())) {
            System.err.println(supplier.get() + " is not matched.");
        }
    }
}
