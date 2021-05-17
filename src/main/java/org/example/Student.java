package org.example;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;


public class Student {
    public static void main(String[] args) {
        final List<StudentItem> students = new ArrayList<StudentItem>(){{
            add(new StudentItem(1, "E1234"));
            add(new StudentItem(2, "D123"));
            add(new StudentItem(3, "C12"));
            add(new StudentItem(4, "B1"));
            add(new StudentItem(5, "A"));
        }};
        students
            .stream()
            .filter(stud -> stud.getAge() < 4)
            .map(StudentItem::getName)
            .filter(name -> name.length() < 5)
            .forEach(System.out::println);
        System.out.println("abd".compareTo("abcd"));
    }
}

@Data
class StudentItem {
    @NonNull
    private int age;
    @NonNull
    private String name;
}

