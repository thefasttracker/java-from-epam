package org.example;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamJava {
    static List<String> words = Stream.of("one", "tw", "three").collect(Collectors.toList());

    public static void main(String[] args) {
        Collections.sort(words, (a, b) -> a.length() - b.length());
        System.out.println(words);
        Converter<String, Integer> converter = Integer::valueOf;
        System.out.println(converter.convert("123"));
        Optional<String> count = words.parallelStream().filter(w -> w.length() > 7).findAny();
        count.ifPresent(System.out::println);
    }
}

@FunctionalInterface
interface Converter<F, T> {
    T convert(F from);
}
