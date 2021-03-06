package org.example;

import java.time.Instant;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//Filter change length Not change elems (Predicate)
//Map changes elems Not changes length (Function)
//Peek Not changes (Runnable)
//FlatMap changes length And elems (Stream<R> Function<T, Stream<R>)

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

class App02 {
    public static void main(String[] args) {
        Britable print = App02::print;
        Consumer<String> print1 = App02::printC;
        Supplier<String> print2 = App02::printS;
        //etc...
        print.setBright();
    }

    private static void printC(String o) { System.out.println(o); }
    private static String printS() {  return "42"; }
    private static void print() { System.out.println("hello!"); }
}

//anonymous class vs functional interface
class IFuncVsAnon {
    public static void main(String[] args) {

        System.out.println(iter.get());
        System.out.println(iter.get());
        System.out.println(iter.get());
        System.out.println(iter.get()); // 0,0,0,0
        System.out.println(iter1.get());
        System.out.println(iter1.get());
        System.out.println(iter1.get());
        System.out.println(iter1.get()); //0,1,2,3
    }

    static public Supplier<Long> iter = () -> { long count = 0; return count++; };

    static public Supplier<Long> iter1 = new Supplier<Long>() { long count = 0; @Override public Long get() {  return count++; } };
}

//flatmap interface
class myFlatmap {
    //Filter change length Not change elems (Predicate)
    //Map changes elems Not changes length (Function)
    //Peek Not changes (Runnable)
    //flatMap changes length And elems (Stream<R> Function<T, Stream<R>)
    public static void main(String[] args) {
        Function<String, Stream<String>> f = s -> Arrays.stream(s.split(" "));
        System.out.println(
                Stream.of("1", "2 33", "4 55 666")
                .flatMap(f)
                .collect(Collectors.toList())
        );
        Stream.of(1,2,3,4,5,6,7,8,9,10).forEach(k -> {
            System.out.println(
                Stream
                    .iterate(1, i -> i + 1)
                    .limit(10)
                    .parallel()
                    .reduce(0, (x, y) -> x + y)
            );
        });
    }
}

interface Carnivore{
    default int calories(List<String> food){
        System.out.println("food.size()*100: " + food.size()*100);
        return food.size()*100;
    }
    int eat(List<String> foods);
}

class Tiger implements Carnivore{
    public int eat(List<String> foods){
        System.out.println("Eating "+foods);
        return foods.size()*200;
    }
}

class TestClass1 {
    public static int size(List<String> names){
        System.out.println("names.size()*2: " + names.size()*100);
        return names.size()*2;
    }
    public static void process(List<String> names, Carnivore c){
        c.eat(names);
    }
    public static void main(String[] args) {
        List<String> fnames = Arrays.asList("a", "b", "c");
        Tiger t = new Tiger();
        process(fnames, TestClass1::size);
        process(fnames, t::calories);
        process(fnames, a -> t.eat(a));
        UUU uuu = new UUU() {
            public <T> void set(T t) {}
        };
        uuu.set("dsa");
        List<Integer> ls = Arrays.asList(1,2,3);
    }
}

interface UUU {
    <T> void set(T t);
}



class TestClass3 {

    public double process(double payment, int rate) {
        double defaultrate = 0.10;
        if(rate>10) defaultrate = rate;  //2

        class Implement {
            public int apply(double data) {
                Function<Integer, Integer> f = x->x+(int)(x*rate);  //3
                return f.apply((int)data);
            }

        }
        Implement i = new Implement();
        return i.apply(payment);
    }
 }

class Eerwre {

    static void getDar() {};
    static int ew;
    static class User {

        String name;
        int age;

        public int getAge() {
            return age;
        }

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
     public static void main(String[] args) {
         TestClass3 tc3 = new TestClass3();
         System.out.println(tc3.process(5.0, 22));

         Rerw.getDar();
         Rerw rer = new Rerw();
         rer.getDar();


         BinaryOperator<String> bo = String::concat;
         BiFunction<String, Integer, String> ba = (s, n) -> s;
         List<String> names  = new ArrayList<>();
         names.add("Bill");
         names.add("George");
         names.add("Obama");
         String finalvalue = names.stream().reduce("Hello : ", bo);
         System.out.println(finalvalue);
         List<User> users = Arrays.asList(new User("John", 30), new User("Julie", 35));
         int result = users.stream()
                 .reduce(0, (partialAgeResult, user) -> partialAgeResult + user.getAge(), Integer::sum);
         System.out.println(Instant.now());
     }
}

class Rerw extends Eerwre implements wewe {
}

interface wewe {
    static void ww() {};
}