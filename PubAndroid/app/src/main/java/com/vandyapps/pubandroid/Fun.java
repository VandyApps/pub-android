package com.vandyapps.pubandroid;

/**
 * Created by ument_000 on 9/28/2014.
 */
public class Fun {

    public static void main(String[] args) {
        for (int ii = 0; ii < PubMenu.Entrees.values().length; ii++) {
            System.out.println(PubMenu.Entrees.values()[ii].name + "    " + PubMenu.Entrees.values()[ii].giveDescription());
        }

        System.out.println(PubMenu.Entrees.BUFFALO_HOT_WINGS.giveDescription());
    }
}
