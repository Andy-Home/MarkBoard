package com.andy.utils;

public class ViewUtils {

    public static double getLength(int t, int b, int l, int r) {
        return Math.sqrt(
                Math.pow((float) (b - t), 2d)
                        + Math.pow((float) (l - r), 2d)
        );
    }
}
