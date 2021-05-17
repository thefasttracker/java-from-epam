package org.example;

import lombok.Data;

class GenericNum<T extends Number> { T number;
    GenericNum(T t) {
        number = t; }

    T get() { return number;
    } }
class GenericsMain {
    public static void main(String[] args) {
        Object obj = new Object();
        String test = "admin";
        GenericNum<Integer> i1 = new GenericNum<>(500); GenericNum<Integer> i2 = new GenericNum<>(500);
        System.out.print(i1.get() == i2.get()); System.out.print(i1.get().intValue() == i2.get().intValue());
        System.out.println(Roles.valueOf("admin".toUpperCase()));
        First f = new Second();

    }
}

enum Roles {
    GUEST("guest"), ADMIN("admin"), CLIENT("client");
    private String typeName;
    Roles(String typeName) {
        this.typeName = typeName;
    }
}

@Data class First {
    public void getMe() {
        System.out.println("first");
    }
}

@Data class Second extends First {
    public void getMeTo() {
        System.out.println("second");
    }
}