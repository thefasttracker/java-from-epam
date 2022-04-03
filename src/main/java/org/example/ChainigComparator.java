package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChainigComparator implements Comparator<Squirrel>{
    @Override
    public int compare(Squirrel s1, Squirrel s2) {
//        Comparator<Squirrel> c = Comparator.comparing(Squirrel::getSpecies).thenComparingInt(Squirrel::getWieght);
        return Comparator
                .comparing(Squirrel::getSpecies)
                .thenComparingInt(Squirrel::getWieght)
                .compare(s1, s2);
    }
}

@FunctionalInterface
interface Climb {
    boolean isTooHigh( int height, int limit);
}

class Climber {
    public static void main(String[] args) {
        check((h, l) -> h > l, 5);
        check((h, l) -> h == l, 10);
        Squirrel s1 = (new Squirrel("a"));
        s1.setWieght(2);
        Squirrel s2 = (new Squirrel("a"));
        s1.setWieght(1);
        List<Squirrel> list = new ArrayList<>();
        list.add(s1);
        list.add(s2);
        System.out.println(list);
        list.sort(new ChainigComparator());
        //the same
        /*list.sort((sq1, sq2) -> Comparator
                .comparing(Squirrel::getSpecies)
                .thenComparingInt(Squirrel::getWieght)
                .compare(sq1, sq2));
        System.out.println(list);*/
    }

    private static void check(Climb climb, int height) {
        if (climb.isTooHigh(height, 10)) System.out.println("too high");
        else System.out.println("ok");
    }
}

class Squirrel {
    private int wieght;
    private String species;
    public Squirrel( String species) {
        if(species == null) { throw new IllegalArgumentException(); }
        this.species = species;
    }

    public int getWieght() { return wieght; }

    public void setWieght(int wieght) { this.wieght = wieght; }

    public String getSpecies() { return species;}

    @Override
    public String toString() {
        return "Squirrel{" +
                "wieght=" + wieght +
                ", species='" + species + '\'' +
                '}';
    }
}