package com.andy.view.board;

import android.graphics.PointF;

import java.util.Observable;

public class Board extends Observable {
    private final String TAG = Board.class.getSimpleName();
    /**
     * 缩放倍数与缩放中心点
     */
    private float mScale = Float.POSITIVE_INFINITY;
    private PointF mScalePoint;

    /**
     * X与Y方向的偏移量
     */
    private float mXOffset = Float.NaN;
    private float mYOffset = Float.NaN;

    public void setScale(float scale, PointF point) {
        mScalePoint = point;
        mScale = scale;
        dataChanged();
    }

    public void setOffset(float x, float y) {
        mXOffset = x;
        mYOffset = y;
        dataChanged();
    }

    private void dataChanged() {
        setChanged();
        notifyObservers();
    }

    private int state = BoardView.MODE_NONE;

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public float getScale() {
        return mScale;
    }

    public float getXOffset() {
        return mXOffset;
    }

    public float getYOffset() {
        return mYOffset;
    }

    public PointF getScalePoint() {
        return mScalePoint;
    }
}
