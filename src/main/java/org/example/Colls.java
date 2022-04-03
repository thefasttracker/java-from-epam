package org.example;

import com.oracle.tools.packager.Log;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Colls {
    public static void main(String[] args) {

    }
}

class IteratorMain {
    public static void main(String[] args) {
        List<Order> orders = new ArrayList<Order>() {
            {
                add(new Order(231, 12.));
                add(new Order(389, 29.));
                add(new Order(747, 32.));
                add(new Order(517, 18.));
                add(new Order(414, 17.));
                add(new Order(777, 10.));
            }
        };

        List<Order> disOrders = new ArrayList<Order>() {
            {
                add(new DiscountOrder(1, 12.f));
                add(new DiscountOrder(2, 29.f));
                add(new DiscountOrder(3, 32.f));
                add(new DiscountOrder(4, 18.f));
                add(new DiscountOrder(5, 17.f));
                add(new DiscountOrder(6, 10.f));
            }
        };

        System.out.println("orders:");
        Iterator<Order> iterator = orders.iterator(); //3
        while (iterator.hasNext()) {
            Order order = iterator.next();
            System.out.println(order);
        }

        final int controlAmount1 = 15;
        final int controlAmount2 = 28;
        final int discountPercent = 10;

        //Classic way
        orders.removeIf(o -> o.getSum() <= controlAmount1);
        orders.forEach(o -> o.setSum(o.getSum() * (100 - discountPercent)/100.0));
        System.out.println("orders:");
        orders.forEach(System.out::println); //1

        //Stream way
//        List<Order> ordersImmutableCopy = Optional.ofNullable(orders)
//                .map(Collection::stream)
//                .orElseGet(Stream::empty)
//                .collect(Collectors.toList());
        //Immutable list copy classic way
//        List<Order> ordersImmutableCopy = new ArrayList<>(orders.size());
//        for (Order item : orders) ordersImmutableCopy.add(item.clone());
//        List<Order> orderList = ordersImmutableCopy.stream()
//                .filter(o -> o.getSum() <= controlAmount2)
//                .map(o -> {
//                    o.setSum(o.getSum() * (100 - discountPercent) / 100.0);
//                    return o;
//                })
//                .collect(Collectors.toList());

        List<Order> orderList = orders.stream()
                .map(Order::clone) //Immutable list copy
                .filter(o -> o.getSum() <= controlAmount2)
                .map(o -> {
                    o.setSum(o.getSum() * (100 - discountPercent) / 100.0);
                    return o;
                })
                .collect(Collectors.toList());

        System.out.println("orderList:");
        for (Order order : orderList) { //2
            System.out.println(order);
        }
        System.out.println("orders:");
        orders.forEach(System.out::println);

        OrderType orderType = new OrderType(1);
        List<String> currencyNames1 = orderType.getCurrencyNames();
        List<String> currencyNames2 = orderType.getCurrencyNames();
        currencyNames1.add("dsa");
        System.out.println(currencyNames1);
        System.out.println(currencyNames2);
        System.out.println(orderType);

        // Create unmodifiable list
        List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
        List<String> unmodifiableList = Collections.unmodifiableList(list);

        List<String> list1 = list.stream()
                .filter( o -> o.length() > 3)
                .map(o -> "wer")
                .collect(Collectors.toList());
        System.out.println("List :" + list);
        System.out.println("List1 :" + list1);
        List<Order> orders1 = orders.stream().map(Order::clone).collect(Collectors.toList());
        orders1.forEach(o -> o.setSum(0));
        System.out.println("orders1" + orders1);
        System.out.println("orders" + orders);

        OrderType type1 = new OrderType(771);
        type1.add("SEK");
        type1.add("DKK");
        type1.add("NOK");
        type1.add("EUR");
        OrderType type2 = new OrderType(779);
        type2.add("SEK");
        type2.add("PLN");
        type2.add("CZK");
        type2.add("EUR");
        System.out.println("type1: " + type1);
        System.out.println("type2: " + type2);
        List<OrderType> orderTypes = new ArrayList<>(Arrays.asList(type1, type2));
        System.out.println("orderTypes: " + orderTypes);
        List<String> currencyList =
                orderTypes.stream()
                        .map(OrderType::getCurrencyNames)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toCollection(ArrayList::new));
        System.out.println("currencyList: " + currencyList);
        int[] arr = {1, 2, 3};
        int arrSum = Arrays.stream(arr)
                .reduce(0, Integer::sum);
        System.out.println(arrSum);

        OrderType.action(disOrders);
        System.out.println(disOrders);
        System.out.println(orders1);
        System.out.println(orders);

        Queue<String> queue = new LinkedList<>();
        queue.offer("one");
        queue.offer("two");
        queue.offer("three");
        queue.removeIf(o -> !(o.length() < 4));
        queue.stream().filter(s -> s.endsWith("e")).forEach(System.out::println);
    }
}

@Data
class OrderType implements Iterable<String> {
    private int orderId;
    private final List<String> currencyNames = new ArrayList<>(Arrays.asList(
            "SEK", "DKK", "NOK", "CZK", "GBP", "EUR", "PLN"
    ));
    public OrderType(int orderId) {
        this.orderId = orderId;
    }
    //return new item of currencyNames
    public List<String> getCurrencyNames() {
        return new ArrayList<>(currencyNames);
    }
    // delegated method
    public boolean add(String e) {
        return currencyNames.add(e);
    }
    @Override
    public Iterator<String> iterator() {
        return currencyNames.iterator();
    }

    static List<? super Order> action(List<? super Order> orders) {
        orders.add(new DiscountOrder(231, 12.f));
        orders.remove(0);
        return orders;
    }
}

class DiscountOrder extends Order {
    public DiscountOrder(int orderId, float amount) {
        super(orderId, amount); }
}
class PriorityMain {
    public static void main(String[] args) {
        Queue<String> priorQ = new PriorityQueue<>();
        priorQ.offer("J");
        priorQ.offer("A");
        priorQ.offer("V");
        priorQ.offer("A");
        priorQ.offer("1");
        priorQ.offer("4");
        while(!priorQ.isEmpty()) {
            System.out.println(priorQ.poll());
        }
    }
}

class HashSetMain {
    public static void main(String[] args) {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("8Y");
        hashSet.add("2Y");
        hashSet.add("2Y");
        hashSet.add("8Y");
        hashSet.add("6Y");
        hashSet.add("5Y");
        hashSet.add("Y-");
        System.out.println("hashSet :" + hashSet);
        hashSet.stream()
                .peek(System.out::print)
                .forEach(s -> System.out.println(" " + s.hashCode()));

        TreeSet<String> treeSet = new TreeSet<>(hashSet);
        System.out.println("treeSet: " + treeSet);

        HashSet<String> set1 = new HashSet<>(Arrays.asList("2Y", "8Y", "6Y", "5Y", "Y-"));
        System.out.println("HashSet :" + set1);
        TreeSet<String> treeSet1 = new TreeSet<>(set1);
        treeSet1.add("-1Y-");
        System.out.println("treeSet1: " + treeSet1);
        System.out.println(treeSet1.last() + " " + treeSet1.first());
        Set<String> set2 = Stream.of("a", "b", "c")
                .collect(Collectors.toCollection(HashSet::new));
    }
}

enum Country {
    ARMENIA, BELARUS, INDIA, KAZAKHSTAN, POLAND, UKRAINE
}

class EnumSetCountryMain {
    public static void main(String[] args) {
        EnumSet<Country> asiaCountries = EnumSet.of(Country.ARMENIA, Country.INDIA, Country.KAZAKHSTAN);
        String nameCountry = "Belarus";
        Country current = Country.valueOf(nameCountry.toUpperCase());
        EnumSet<Country> none = EnumSet.complementOf(asiaCountries);
        if (asiaCountries.contains(current)) {
            System.out.println(current + " is in Asia");
        } else {
            System.out.println(current + " is not in Asia");
        }
        System.out.println(none);
    }
}

class HashMapMain {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("Jeans", 40); // adding a pair
        map.put("T-Shirt", 35);
        map.put("Gloves", 42);
        map.compute("Shoes", (k,v) -> 77); // adding a pair
        System.out.println(map);
        // replacing value if key exists
        map.computeIfPresent("Shoes", (k,v) -> v + k.length());
        System.out.println(map.get("Jeans"));
        System.out.println(map);
        map.computeIfAbsent("Shoes", v -> 11);
        // adding a pair if the key is missing
        map.computeIfAbsent("Shoes_2", v -> 11);
        System.out.println(map);
    }
}

class MapEntryMain {
    public static void main(String[] args) {
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        hashMap.put("Пряник", 5);
        hashMap.put("Кефир", 1);
        hashMap.put("Хлеб", 1);
        hashMap.putIfAbsent("Хлеб", 2); // replacement will not happen
        hashMap.putIfAbsent("Молоко", 5);
        hashMap.computeIfAbsent("Сырок", v -> 3); // adding a pair
        hashMap.computeIfPresent("Сырок", (k, v) -> 4); // replacement a value
        hashMap.computeIfAbsent("Сырок", v -> 144);// replacement will not happen
        System.out.println(hashMap);
        hashMap.put("Пряник", 4); // replacement or addition in the absence of a key
        System.out.println(hashMap + " after replacing the element");
        System.out.println(hashMap.get("Хлеб") + " - found by key 'Хлеб'");
        Set<Map.Entry<String, Integer>> entrySet = hashMap.entrySet();
        System.out.println(entrySet);
        entrySet.stream()
                .forEach(e -> System.out.println(e.getKey() + " : " + e.getValue()));
        Set<Integer> values = new HashSet<Integer>(hashMap.values());
        System.out.println(values);
    }
}

class EnumMapCountryMain {
    public static void main(String[] args) {
        EnumMap<Country, Integer> map = new EnumMap<>(Country.class);
        map.put(Country.POLAND, 8);
        map.put(Country.UKRAINE, 1);
        map.put(Country.BELARUS, 0);
        map.forEach((k, v) -> System.out.println(k + " " + v));
    }
}

class CurrentOrdersMain {
    public static void main(String[] args) throws InterruptedException {
        CurrentOrders orders = new CurrentOrders();
        List<Key> keys = new ArrayList<>();
        keys.add(new Key(100));
        keys.add(new Key(220));
        keys.add(new Key(770));
        orders.put(keys.get(0), new Order(77, 10d));
        orders.put(keys.get(1), new Order(65, 54d));
        orders.put(keys.get(2), new Order(41, 93d));
        keys.get(1).setProcessed(true);
        keys.removeIf(Key::isProcessed);
        System.out.println("keys: " + keys);
        System.out.println(orders.size() + " " + orders.getOrders());
        System.gc();
        Thread.sleep(1_000);
        System.out.println(orders.size() + " " + orders.getOrders());
    }
}

class CurrentOrders {
    private WeakHashMap<Key, Order> orders = new WeakHashMap<>();
    public Order put(Key key, Order value) {
        return orders.put(key, value);
    }
    public Order get(Object key) {
        return orders.get(key);
    }
    public WeakHashMap<Key, Order> getOrders() {
        return orders;
    }
    public int size() {
        return orders.size();
    }
}

@ToString
class Key {
    private int keyUnique;
    @ToString.Exclude
    private boolean isProcessed;
    public Key(int keyUnique) {
        this.keyUnique = keyUnique;
    }
    public boolean isProcessed() {
        return isProcessed;
    }
    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }
}

class HashtableMain {
    public static void main(String[] args) {
        Hashtable<String, Integer> table = new Hashtable<>();
        table.put("Jeans", 40); // adding a pair table.put("T-Shirt", 35);
        table.put("Gloves", 42);
        table.compute("Shoes", (k,v) -> 77); // adding a pair System.out.println(table);
        Enumeration<String> keys = table.keys();
        while (keys.hasMoreElements()) {
            System.out.println(keys.nextElement());
        }
        Enumeration<Integer> values = table.elements();
        while (values.hasMoreElements()) {
            System.out.println(values.nextElement());
        }
    }
}

class VectorMain {
    public static void main(String[] args) {
        Vector<String> vector = new Vector<>(777);
        vector.add("java");
        vector.add("epam");
        vector.add(1, null);
        vector.addAll(vector);
        System.out.println(vector);
        vector.removeIf(e -> e==null);
        vector.replaceAll(String::toUpperCase);
        System.out.println(vector);
        long size = vector.stream().count();
        System.out.println(size);
        Enumeration<String> enumeration = vector.elements();
        while(enumeration.hasMoreElements()) {
            System.out.printf("%s ", enumeration.nextElement());
        }
    }
}

class PropertiesStoreMain {
    public static void main(String[] args) {
        Properties props = new Properties();
        try {
            props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            props.setProperty("db.url", "jdbc:mysql://127.0.0.1:3306/testphones");
            props.setProperty("user", "root");
            props.setProperty("password", "pass");
            props.setProperty("poolsize", "5");
            props.store(new FileWriter("data/base.properties"), "No Comment’s");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class PropertiesLoadDemo {
    public static void main(String[] args) {
        Properties props = new Properties();
        try {
            props.load(new FileReader("data/base.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String dbUrl = props.getProperty("db.url");
        // following two names are missing in the file
        String maxIdle = props.getProperty("maxIdle"); // maxIdle = null
        // value "20" will be assigned to the key if it is not found in the file
        String maxActive = props.getProperty("maxActive", "20");
        System.out.println("dbUrl: " + dbUrl);
        System.out.println("maxIdle: " + maxIdle );
        System.out.println("maxActive: " + maxActive);
    }
}

class AlgoritmMain {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList();
        Collections.addAll(list, 1, 2, 3, 4, 5);
        Collections.shuffle(list);
        System.out.println(list);
        Collections.sort(list);
        System.out.println(list);
        Collections.reverse(list);
        System.out.println(list);
        Collections.rotate(list, 3);
        System.out.println(list);
        System.out.println("min: " + Collections.min(list));
        System.out.println("max: " + Collections.max(list));
        List<Integer> singletonList = Collections.singletonList(777);
        System.out.println(singletonList);
        //singletonList.add(21); // runtime error
    }
}

@Slf4j
class Order1 {
    long orderId;
    double amount;
    public Order1(long orderId, double amount) {
        this.orderId = orderId;
        this.amount = amount;
        Log.info("dfdsfs");
        System.err.println();
    }
    public String toString() {
        return orderId + ", " + amount ;
    }

    public static void main(String[] args) {
        List<Order1> orders = Arrays.asList(new Order1(1, 50), new Order1(5, 70),
                new Order1(7, 70));
        orders.stream()
                .reduce((p1, p2) -> p1.amount > p2.amount ? p1 : p2) .ifPresent(System.out::println);
    }
}
class HashMapTest {
    public static void main(String[] args) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "One");
        map.put(2, "Two");
        map.put(3, "Three");
        System.out.println(map.entrySet());

        Integer i1 = 1000;
        Integer i2 = 1000;
        Integer i3 = 10;
        Integer i4 = 10;
        System.out.println(i1 == i2); //Reference equality
        System.out.println(i1 != i2); //Reference equality
        System.out.println(i3 == i4); //The JLS states that auto-boxing of numbers in the range of -128 to 127
        System.out.println(i3 != i4); //will result in the same object. Integer constant pool?

        Set<String> set = new HashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
    }
}