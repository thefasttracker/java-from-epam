package org.p2;

import org.p1.P1;

public class P2 extends P1 {

    public void process(P1 a) {
        // a.i = a.i * 2; -- doesn't compile
    }

    public static void main(String[] args) {
        P1 a = new P2();
        P2 b = new P2();

        b.process(a);
        System.out.println(a.getI());
    }

}

