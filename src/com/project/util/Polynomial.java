package com.project.util;

public class Polynomial {
    final static int[] p1 =  {1};
    final static int[] p2 =  {1,1};
    final static int[] p3 =  {1,1,0};
    final static int[] p4 =  {1,0,1,0};
    final static int[] p5 =  {1,1,1,0,1};
    final static int[] p6 =  {1,0,0,0,0,1};
    final static int[] p7 =  {1,0,1,0,0,1,1};
    final static int[] p8 =  {1,0,0,0,1,1,0,1};
    final static int[] p9 =  {1,1,1,1,1,0,1,0,0};
    final static int[] p10 = {0,0,1,1,1,1,1,1,1,1}; //
    final static int[] p11 = {1,1,1,1,1,1,0,1,0,0,1}; //
    final static int[] p12 = {1,0,1,1,1,0,1,1,1,1,1,1}; //
    final static int[] p13 = {1,1,1,1,1,0,1,0,1,0,0,0,1}; //
    final static int[] p14 = {1,0,0,1,0,0,0,0,1,0,0,0,0,1};
    final static int[] p15 = {1,0,0,0,0,1,0,0,0,0,1,0,0,0,1};
    final static int[] p16 = {1,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0};
    final static int[] p17 = {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0};
    final static int[] p18 = {1,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,1,1};

    public static int[] getPoly(int d) {
        return switch (d) {
            case (1)  -> p1;
            case (2)  -> p2;
            case (3)  -> p3;
            case (4)  -> p4;
            case (5)  -> p5;
            case (6)  -> p6;
            case (7)  -> p7;
            case (8)  -> p8;
            case (9)  -> p9;
            case (10) -> p10;
            case (11) -> p11;
            case (12) -> p12;
            case (13) -> p13;
            case (14) -> p14;
            case (15) -> p15;
            case (16) -> p16;
            case (17) -> p17;
            case (18) -> p18;
            default -> new int[]{0};
        };
    }
}
