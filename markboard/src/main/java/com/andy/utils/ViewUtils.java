package com.andy.utils;

import android.graphics.PointF;
import android.view.MotionEvent;

public class ViewUtils {

    public static double getLength(int t, int b, int l, int r) {
        return Math.sqrt(
                Math.pow((float) (b - t), 2d)
                        + Math.pow((float) (l - r), 2d)
        );
    }

    /**
     * 计算双点触控的时候两触控点的距离
     */
    public static float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 计算两点之间的中点位置
     */
    public static PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }
}
