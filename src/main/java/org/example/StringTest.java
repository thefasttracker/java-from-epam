package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.System.out;

public class StringTest {
    public static void main(String[] args) {
        Str1 str1 = new Str1();
        String str3 = String.join("-",str1.str);
        System.out.println(str3);
        String s1 = "Java12";
        String s2 = "Ja" + "va" + 12;
        String s3 = new String("Java12");
        String s4 = new String(s1);
        System.out.println(s1 + " equals " + s2 + " : " + s1.equals(s2));
        System.out.println(s2 + " equals " + s4 + " : " + s2.equals(s4));
        StringJoiner joiner = new StringJoiner(":", "<<", ">>");
        String result = joiner.add("one").add("two").add("three").toString();
        System.out.println(result);
        StringBuilder builder = new StringBuilder();
        builder.append("Internationalization");
        System.out.println("content = " + builder);
        System.out.println("length = " + builder.length());
        System.out.println("capacity = " + builder.capacity());
        out.println("reverse = " + builder.reverse());
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        sb1.append("Java");
        sb2.append("Java");
        out.println(sb1.toString().contentEquals(sb2));
        String xssString = "<script>alert('hello')</script>";
        xssString = xssString.replaceAll("</?script>","");
        out.println(xssString);
        String[] arrayStr = {"12.9", "44", "the", "7,1", "27..2", "211"};
        List<Integer> list = Arrays.stream(arrayStr)
                .flatMap(Pattern.compile("\\D+")::splitAsStream)
                .peek((s -> System.out.printf(" %s ", s)))
                .filter(Pattern.compile("[2-9]+").asPredicate())
                .map(Integer::valueOf)
                .collect(Collectors.toList());
        System.out.println("\n" + list);
        String regex = "\\w{6,}@\\w+\\.\\p{Lower}{2,4}";
        String input = "адреса эл.почты:blinov@gmail.com, romanchik@bsu.by!";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        List<String> ls = new ArrayList<>();
        while (matcher.find()) {
            System.out.println("e-mail: " + matcher.group());
            ls.add("e-mail: " + matcher.group());
        }
        out.println("ls -  " + ls.toString());
        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("E, MMM d yyyy");
        String date3 = "Fri, May 25 2018";
        LocalDate localDate3 = LocalDate.parse(date3, formatter3);
        System.out.println(localDate3);
        System.out.println(formatter3.format(localDate3));
        out.println();
    }
}

class Str1 {
    final String[] str = {"qwe", "rewr", "rrbgr"};
}
