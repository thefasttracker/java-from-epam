package org.example;

public class Ext {
    public final <T extends Ext> void getCh(T obj) {
        obj.check();
    }
    void check() {
        System.out.println("Ext");
    }
}

class ExtOne extends Ext {
    @Override
    void check() {
        System.out.println("Ext from One");
    }
    void checkOne() {
        System.out.println("ExtOne");
    }
}
class ExtTwo extends Ext {
    @Override
    void check() {
        System.out.println("Ext from Two");
    }
    void checkTwo() {
        System.out.println("ExtTwo");
    }
}

class AppExt implements Cloneable {
    public static void main(String[] args) {
        Ext ext = new Ext();
        ExtOne one = new ExtOne();
        Ext two = new ExtTwo();
        one.check();
        two.check();
        ext.getCh(one);
        ext.getCh(two);
        ExtTwo three = (ExtTwo) two;
        three.check();
    }
}

abstract class Abs {
    void fds() {
        System.out.println("abs");
    }
    abstract void s();
}