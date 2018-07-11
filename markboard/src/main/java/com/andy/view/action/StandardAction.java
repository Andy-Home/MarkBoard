package com.andy.view.action;

import android.graphics.PointF;

import com.andy.view.board.Board;

import java.util.Observable;

public class StandardAction extends Action {
    private float mScale;
    private PointF mScalePoint;

    private float mXOffset;
    private float mYOffset;

    private PointF currentPoint;

    public StandardAction(Observable o, PointF original, PointF current) {
        setOriginalPoint(original);
        o.addObserver(this);
        currentPoint = current;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Board) {
            mScale = ((Board) o).getScale();
            mScalePoint = ((Board) o).getScalePoint();

            mXOffset = ((Board) o).getXOffset();
            mYOffset = ((Board) o).getYOffset();
        }
    }

    private void updateAction() {

    }
}
