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
class Order implements Cloneable{
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
class Current extends Base { }
