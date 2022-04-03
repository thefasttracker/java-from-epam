package org.example;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamJava {
    static List<String> words = Stream.of("one", "tw", "three").collect(Collectors.toList());

    public static void main(String[] args) {
        BiFunction<String, Character, String> acc = (c, s1) -> {
            System.out.println("Thread acc: "+ Thread.currentThread().getName() +", c: "+ c + ", s1: " + s1);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return c + s1;
        };
        BinaryOperator<String> combiner = (s2, s3) -> {
            System.out.println("Thread comb: "+ Thread.currentThread().getName() +", s2: "+ s2 + ", s3: " + s3);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return s2 + s3;
        };
        Collections.sort(words, (a, b) -> a.length() - b.length());
        System.out.println(words);
        Converter<String, Integer> converter = Integer::valueOf;
        System.out.println(converter.convert("123"));
        Optional<String> count = words.parallelStream().filter(w -> w.length() > 7).findAny();
        count.ifPresent(System.out::println);
        System.out.println(Arrays.asList('w','o','l','f','c','a','t')
                .stream()
                .reduce("", acc, combiner)
        );

    }
}

@FunctionalInterface
interface Converter<F, T> {
    T convert(F from);
}
