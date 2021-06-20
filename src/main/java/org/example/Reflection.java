package org.example;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    public static void main(String[] args) {
        //get private fields
        MyClass myClass = new MyClass();
        int number = myClass.getNumber();
        String name = null; //no getter =(
        System.out.println(number + name);//output 0null
        printData(myClass); // outout 0default
        try {
            Field field = myClass.getClass().getDeclaredField("name");
            field.setAccessible(true);
            name = (String) field.get(myClass);
            field.set(myClass, (String) "new value");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println(number + name);//output 0default
        printData(myClass);

        // invoke private default constructor
        myClass = null;
        try {
            Class clazz = Class.forName(MyClass.class.getName());
            myClass = (MyClass) clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println(myClass);//output created object reflection.MyClass@60e53b93

        // invoke private constructor with args
        myClass = null;
//        try {
//            Class clazz = Class.forName(MyClass.class.getName());
//            Class[] params = {int.class, String.class};
//            myClass = (MyClass) clazz.getConstructor(params).newInstance(1, "default2");
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        System.out.println(myClass);//output created object reflection.MyClass@60e53b93

        // get all constructors
        try {
            Class clazz = Class.forName(MyClass.class.getName());
            Constructor[] constructors = clazz.getConstructors();
            for (Constructor constructor : constructors) {
                Class[] paramTypes = constructor.getParameterTypes();
                for (Class paramType : paramTypes) {
                    System.out.print(paramType.getName() + " ");
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    // invoke private methods
    public static void printData(Object myClass){
        try {
            Method method = myClass.getClass().getDeclaredMethod("printData");
            method.setAccessible(true);
            method.invoke(myClass);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

class MyClass {
    private int number;
    private String name = "default";
    //    public MyClass(int number, String name) {
//        this.number = number;
//        this.name = name;
//    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public void setName(String name) {
        this.name = name;
    }
    private void printData(){
        System.out.println(number + name);
    }
}