package com.lyc.tmp;

import java.util.Random;

/**
 * CREATE WITH Lenovo
 * DATE 2019/1/3
 * TIME 20:13
 */
public class Test {

    public static void main(String[] args) {
        String yuyan = "你将会在" + (new Random().nextInt(12) + 1 )+ " 月毕业";
        System.out.println(yuyan);
    }
}
