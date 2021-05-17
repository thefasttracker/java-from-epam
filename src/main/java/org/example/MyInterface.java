package org.example;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.*;
import java.util.stream.Collectors;

public class MyInterface {
    public static void main(String[] args) {
        BrightControl bc = new BrightControl();
        BritableImpl bi = new BritableImpl();
        BritNo b = new BritNo();
        bc.setDefaultBright(bi);
        ISum summator = x -> {
            x = Math.sqrt(x);
            System.out.println((int)x);
            return x;
        };
        int[] c = {9, 16, 25};
        List<Integer> d = Arrays
                            .stream(c)     // IntStream
                            .boxed()        // Stream<Integer>
                            .collect(Collectors.toList());
        d.forEach(summator::sum);
        System.out.println(Arrays.toString(Arrays
                    .stream(c)
                    .filter(((IntPredicate) i -> i < 16).or(i -> i > 16))
                    .toArray()
                )
        );
        String[] arrayStr = {"as", "a", "the", "d", "on", "and"};
        List<String> ls = Arrays
                .stream(arrayStr)
                .map(((Function<String, Integer>) String::length)
                .andThen(Integer::toBinaryString))
                .collect(Collectors.toList());
        int[] arrayInt = { 1, 3, 5, 9, 17, 33, 65};
        List<Integer> li = Arrays
                .stream(arrayInt)
                .boxed()
                .map(((Function<String, Integer>) String::length).compose(Integer::toBinaryString))
                .collect(Collectors.toList());
        int i = 37;
        Supplier<Integer> numberSupplier = () -> new Random().nextInt(i);
        System.out.println("numberSupplier: " + numberSupplier.get());
        Comparator<String> comparator = String::compareTo; //(s1, s2) -> s2.length() - s1.length();
        String str = "and java course epam the rose lion wolf hero green white red white";
        Arrays.stream(str.split("\\s"))
                .sorted(comparator.reversed())
                .forEach(s -> System.out.printf("%s ", s)
        );
        System.out.println();
        Arrays.stream(str.split("\\s"))
                .sorted(Comparator.comparing(String::length)
                .thenComparing(String::compareTo))
                .forEach(s -> System.out.printf("%s ", s)
        );
        IntFunction<IntUnaryOperator> curriedAdd = a1 -> b1 -> a1 + b1;
        curriedAdd.apply(4).applyAsInt(5);
    }
}

@FunctionalInterface
interface Britable {
    void setBright();
}

@FunctionalInterface
interface ISum {
    double sum(double a);
}

class BritableImpl implements Britable {
    @Override
    public void setBright(){};
    private int gi() {
        return 5;
    }
}

class BritNo {
    public void setBright(){};
    private int gi() {
        return 5;
    }
}

class BrightControl {
        public void setDefaultBright(Britable obj){
            obj.setBright();
        }
}

interface Ee {
    static void ff() {};
}
