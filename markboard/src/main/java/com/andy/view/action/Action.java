package com.andy.view.action;

import android.graphics.PointF;

import java.util.Observer;

public abstract class Action implements Observer {

    private PointF originalPoint;

    public PointF getOriginalPoint() {
        return originalPoint;
    }

    public void setOriginalPoint(PointF originalPoint) {
        this.originalPoint = originalPoint;
    }
}
