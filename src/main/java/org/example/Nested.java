package org.example;

import lombok.Data;

@Data
public class Nested {
    private final static int BASE_COEFFICIENT = 6;
    public double defineScholarship(float averageMark) {
        double value = 100;
        if (averageMark > BASE_COEFFICIENT) {
            value *= 1 + (BASE_COEFFICIENT / 10.0); }
        return value; }
}

class StudentActionMain {
    public static void main(String[] args) {
        Nested action = new Nested();// usually object
        Nested actionAnon = new Nested() {// anonymous class object
            int base = 9; // invisible
            void a() {
                System.out.println("fds");
            }
            @Override
            public double defineScholarship(float averageMark) {
                double value = 100;
                a();
                if (averageMark > base) {
                    value *= 1 + (base / 10.0); }
                return value; }
        };
        System.out.println(action.defineScholarship(9.05f));
        System.out.println(actionAnon.defineScholarship(9.05f));
        D d = new D() {
            void f(){
                System.out.println("vdf");
            };
        };
        d.f();
        Garden.Plant plant = new Garden.Plant();
    }
}

class Garden {
    public static class Plant {}
}

class D {
    void f(){};
};