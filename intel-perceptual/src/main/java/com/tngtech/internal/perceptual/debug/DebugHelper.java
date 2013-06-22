package com.tngtech.internal.perceptual.debug;

public class DebugHelper {
    public static String getPaddedNumberOfX(float number, int padding) {
        int i = 0;
        String value = "";

        for (; i < number; i++) {
            value += "x";
        }
        for (; i < padding; i++) {
            value += " ";
        }

        return value;
    }
}
