package org.example;

import lombok.*;
import java.util.Arrays;

public class Parametrized {
    public static void main(String[] args) {
        One<String> one = new One<>("str");
        Two<String> two = new Two<>();
        Object[] a = two.get("abs");
        Object[] b = two.get(4);
        System.out.println(Role.GUEST);
        Role role = Role.ADMIN;
        System.out.println(Arrays.toString(Role.values()));
        System.out.println(role);
        two.<Integer>sum(1,2,3,4,5);
        System.out.println(Arrays.toString(two.get("abs")));
        System.out.println(Arrays.toString(b));
    }
};

@Data
class One<T extends String> {
    @NonNull
    private T value;
}
enum Role {GUEST, CLIENT, MODERATOR, ADMIN }

class Two<T> {
    <K> K[] get(K param) {
        return (K[])new Object[]{param};
    }
    <P> P check(P num) {
        return num;
    };
    public <V> void sum(V... args) {
        System.out.println(args.length);

    }
    String str = "ewr";
    String[] arr = new String[]{str};
}

class A {
    protected <T extends Number> T get(T i) {return i;}
}

class B extends A implements Ss {
    @Override
    public long get(int i) {return (long)i;}
}

interface Ss {
    public long get(int i);
}