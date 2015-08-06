package com.elevenfifty.www.elevendates.Models;

/**
 * Created by kathy on 7/15/2015.
 */
public enum DatePreference {
    MALE_ONLY(0),
    FEMALE_ONLY(1),
    BOTH(2);

    private  final int value;

    DatePreference(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
