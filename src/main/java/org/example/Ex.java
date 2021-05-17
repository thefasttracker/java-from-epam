package org.example;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Ex {
    String numberStr = "Y-";

    public static void main(String[] args) {
        Ex ex = new Ex();
        ex.doAction();
        System.out.println("---");
        ex.parseFromFrance(ex.numberStr);
        System.out.println("---");
        ex.doAction1();
        try {
            ex.loadFile("fds");
        } catch (ResourceException e) {
            System.err.println(e);
        }
        int size = -1;
        assert (size > 0) : "incorrect PoolSize= " + size; //запускается так: java -ea ./src/main/java/org/example/Ex.java
    }

    public void doAction() { // code here
        try {
            parseFrance(numberStr);
        } catch (ParseException e) {
            System.err.println("fuckkkk! " + e.getMessage());
        }
    }

    public void doAction1() {
        try {
            int a = (int) (Math.random() * 2);
            System.out.println("a = " + a);
            int c[] = { 1 / a }; // place of occurrence of exception #1
            c[a] = 71; // place of occurrence of exception #2
        } catch (ArithmeticException e) {
            System.err.println("divide by zero " + e);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("out of bound: " + e);
        } // end try-catch block
            System.out.println("after try-catch");
    }

    public int loadFile(String filename) throws ResourceException {
        int data;
        try {
            FileReader reader = new FileReader(filename);
            data = reader.read();
        } catch (IOException e) {
//            logger.fatal("fatal error: config file not found: " + filename, e);
            throw new ResourceException(e);
        }
        return data;
    }

    private double parseFrance(String numberStr) throws ParseException {
        NumberFormat formatFrance = NumberFormat.getInstance(Locale.FRANCE);
        double numFrance = formatFrance.parse(numberStr).doubleValue();
        return numFrance;
    }

    public double parseFromFrance(String numberStr) {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        double numFrance = 0;
        try {
            numFrance = format.parse(numberStr).doubleValue();
        }
        catch (ParseException e) { // checked exception
            // 1. throwing a standard exception,: IllegalArgumentException() — not very good
            // 2. throwing a custom exception, where ParseException as a parameter
            // 3. setting the default value - if possible
            // 4. logging if an exception is unlikely
            System.err.println(e);
        }
        return numFrance;
    }

    public double parseFromFileBefore(String filename) throws FileNotFoundException, ParseException, IOException {
        NumberFormat formatFrance = NumberFormat.getInstance(Locale.FRANCE);
        double numFrance = 0;
        BufferedReader bufferedReader = null;
        try {
            FileReader reader = new FileReader(filename);
            bufferedReader = new BufferedReader(reader);
            String number = bufferedReader.readLine();
            numFrance = formatFrance.parse(number).doubleValue();
        } catch (final Exception e) {
            throw e;
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return numFrance;
    }
}

class ResourceException extends Exception {
    public ResourceException() {
    }
    public ResourceException(String message, Throwable cause) {
        super(message, cause);
    }
    public ResourceException(String message) { super(message);
    }
    public ResourceException(Throwable cause) {
        super(cause);
    }
}

class Stone {

    public void accept(String data) throws ResourceException {
        /* more code */
    }
}
class GreenStone extends Stone {
    @Override
    public void accept(String data) {
        //some code
    }
}

class WhiteStone extends Stone {
    @Override
    public void accept(String data) throws ResourceException {
       super.accept(data);
    }
}

//class GreyStone extends Stone {
//    @Override
//    public void accept(String data) throws IOException {//compile error
//        FileWriter writer = new FileWriter("data.txt");
//    }
//}
class StoneService {
    public void buildHouse(Stone stone) {
        try {
            stone.accept("some info");
        } catch(ResourceException e) {
            // handling of ResourceException and its subclasses
            System.err.print(e);
        }
    }
}

class Quest {
    static void method() throws ArithmeticException {
        int i = 7 / 0;
        try {
            double d = 77.0;
            d /= 0.0;
        } catch (ArithmeticException e) {
            System.out.print("E1");
        } finally {
            System.out.print("Finally ");
        }
    }
    public static void main(String[] args) {
        try {
            method();
        } catch (ArithmeticException e) {
            System.out.print("E0");
        }
    }
}

class ColorException extends Exception {}
class WhiteException extends ColorException {}
abstract class Color {
    abstract void method() throws ColorException;
}
class White extends Color {
    void method() throws WhiteException {
        throw new WhiteException();
    }
    public static void main (String[] args) {
        White white = new White(); int a, b, c;
        a = b = c = 0;
        try {
            white.method();
            a++;
        } catch (WhiteException e) {
            b++;
        } finally {
            c++;
        }
        System.out.print(a + " " + b + " " + c);
    }
}

class A11{
    A11() throws IOException{}
}
class B11 extends A11 {
    B11() throws Exception{}
}