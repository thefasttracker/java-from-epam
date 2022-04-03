package org.example;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;


public class App {
    public static void main(String[] args) {
        List<Order> list = new ArrayList<>();
        list.add(new Order(71L, 100D));
        list.add(new Order(18L, 132D));
        list.add(new Order(24L, 210D));
        list.add(new Order(35L, 693D));
        OrderAction action = new OrderAction();
        Optional<Order> optionalOrder = action.findById(list, 23); // replaced by 23
        optionalOrder.ifPresent(System.out::println);
        Set<Order> set = new HashSet<>();
        optionalOrder.ifPresent(set::add);// or o->set.add(o)
        System.out.println(set);
    }
}

@Data
@AllArgsConstructor
class Order implements Cloneable {
    private long orderId;
    private double sum;

    @Override
    public Order clone() {
        return new Order(this.orderId, this.sum);
    }
}

class OrderAction {
    public Optional<Order> findById(List<Order> orders, long id) {
        return orders
                .stream()
                .filter(o -> id == o.getOrderId())
                .findAny();
    }
}

class Base {
    private long idBase;
    public Base(long idBase) {
        this.idBase = idBase;
    }
    Base() {this(1); }
}

class AAA {
    protected int i = 5;
    public int getI() {
        return i;
    }
}

class BBB extends AAA {

    public void process(AAA a) {
        a.i = a.i * 2;
    }

    public static void main(String[] args) {
        AAA a = new BBB();
        BBB b = new BBB();
        b.process(a);
        System.out.println(a.getI());
    }

}

abstract class CCC {
    final void fds(){};
    public static void main(String[] args) {
        AAA a = new BBB();
        BBB b = new BBB();
        b.process(a);
        System.out.println(a.getI());
    }
}

interface I1 {
    int value = 1;
    static void h() {
        System.out.println("I2");
    };
}

interface I2 extends I1 {
    int value = 1;
    default void h() {
        System.out.println("I2");
    };
}

class TestClass implements I1, I2 {

    public void h() {
        System.out.println("Class");
    };

    public static void main(String[] args) {
        TestClass ts = new TestClass();
        ts.h();

        TreeSet<Integer> original = new TreeSet<>();
        original.add(1);
        original.add(6);
        original.add(2);
        original.add(7);

        TreeSet<Integer> sub = (TreeSet<Integer>) original.subSet(1,7);
        sub.add(5);
        sub.forEach(System.out::println);
    }
}

