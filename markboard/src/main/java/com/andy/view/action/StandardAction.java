package com.andy.view.action;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.andy.utils.ViewUtils;
import com.andy.view.board.Board;
import com.andy.view.board.BoardView;

import java.util.Observable;

public class StandardAction extends Action {
    private final String TAG = StandardAction.class.getSimpleName();
    private PointF currentPoint;

    private Paint mPaint;

    public StandardAction() {
        init();
    }

    public StandardAction(Observable o) {
        o.addObserver(this);
        init();
    }

    public StandardAction(Observable o, PointF original, PointF current) {
        super.setOriginalPoint(original);
        o.addObserver(this);
        currentPoint = current;
        init();
    }

    public void setOriginalPoint(PointF point) {
        super.setOriginalPoint(point);
    }

    public void setCurrentPoint(PointF point) {
        currentPoint = point;
    }

    public void setObserver(Observable o) {
        o.addObserver(this);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d(TAG, "action收到数据改变");
        if (o instanceof Board) {
            int state = ((Board) o).getState();
            switch (state) {
                case BoardView.MODE_NONE:
                    break;
                case BoardView.MODE_DRAG:
                    onMove(((Board) o).getXOffset(), ((Board) o).getYOffset());
                    break;
                case BoardView.MODE_ZOOM:
                    onScale(((Board) o).getScale(), ((Board) o).getScalePoint());
                    break;
            }
        }
    }

    private void onMove(float xOffset, float yOffset) {
        currentPoint.set(currentPoint.x + xOffset, currentPoint.y + yOffset);
    }

    private void onScale(float scale, PointF point) {
        float scaleX = (currentPoint.x - point.x) * scale;
        float scaleY = (currentPoint.y - point.y) * scale;
        currentPoint.set(point.x + scaleX, point.y + scaleY);
    }

    private float radius = 10f;
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawCircle(currentPoint.x, currentPoint.y, radius, mPaint);
    }


    @Override
    public boolean isTouch(float touchX, float touchY) {
        double length = ViewUtils.getLength(touchX, currentPoint.x, touchY, currentPoint.y);
        return length <= radius;
    }
}
