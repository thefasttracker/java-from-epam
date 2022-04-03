package org.example;

import java.io.*;

public enum EnumTest {
    WINTER("low", "cold"),
    SUMMER("Hi"),
    SPRING {
        public void getHi() {
            System.out.println("Hi there from SPRING!");
        }
    };

    private String amountOfVisitors;
    private String temperature;

    EnumTest(String amountOfVisitors, String temperature) {
        this.amountOfVisitors = amountOfVisitors;
        this.temperature = temperature;
    }

    EnumTest(String amountOfVisitors) {
        this.amountOfVisitors = amountOfVisitors;
    }

    EnumTest() {
    }

    public String getTemperature() {
        return temperature;
    }

    public String getAmountOfVisitors() {
        return amountOfVisitors;
    }

    public void getHi() {
        System.out.println("Hi there: " + EnumTest.this.name());
    }

    public static void main(String[] args) {
        System.out.println(EnumTest.WINTER.getTemperature());
        EnumTest.SPRING.getHi();
        EnumTest.SUMMER.getHi();
    }
}

class NewConsole {
    public static void main(String[] args) {
        String name = "";
        Console c = System.console();
        char[] pw;
        pw = c.readPassword("%s", "pw: ");
        for(char ch: pw)
            c.format("%c", ch);
        c.format("\n");

        MyUtility mu = new MyUtility();
        while(true) {
            name = c.readLine("%s", "input?: ");
            c.format("output: %s \n", mu.doStuff(name));
        }
    }
}

class MyUtility {
    String doStuff(String arg1) {
        return "result is: " + arg1;
    }
}

class Dogg implements Serializable {
    transient private Collar collar;
    private int dogSize;
    public Dogg(Collar collar, int dogSize) {
        this.collar = collar;
        this.dogSize = dogSize;
    }
    private void writeObject(ObjectOutputStream os) {
        try {
            os.defaultWriteObject();
            os.writeInt(collar.getCollarSize());
        } catch (IOException e) {
            /*NOP*/
        }
    }

    public static void main(String[] args) throws IOException {
        Dogg dogg = new Dogg(new Collar(4),9);
        try(ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream("data/testSer.dat"))) {
                ois.writeObject(dogg);
        }
    }
}

class Collar {
    private int collarSize;
    public Collar(int collarSize){
        this.collarSize = collarSize;
    }
    public int getCollarSize() {
        return collarSize;
    }
}