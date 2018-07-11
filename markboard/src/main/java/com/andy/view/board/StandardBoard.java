package com.andy.view.board;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.andy.utils.ImageUtils;
import com.andy.utils.ViewUtils;
import com.andy.view.action.Action;
import com.andy.view.action.StandardAction;

import java.util.ArrayList;

public class StandardBoard extends BoardView implements SurfaceHolder.Callback {
    private final String TAG = StandardBoard.class.getSimpleName();
    private ArrayList<Action> mActionList;

    public StandardBoard(Context context) {
        super(context);
        init();
    }

    public StandardBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StandardBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Matrix matrix = null;
    private SurfaceHolder mHolder = null;

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);

        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);

        matrix = new Matrix();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return super.onTouchEvent(event);
    }

    public float getTotalScale() {
        float original = (float) ViewUtils
                .getLength(originalTop, originalBottom, originalLeft, originalRight);

        float current = (float) ViewUtils
                .getLength(getTop(), getBottom(), getLeft(), getRight());

        return current / original;
    }

    public void createAction(int touchX, int touchY) {
        Action action = null;

        //未移动时
        if (getTotalScale() == 1f
                && getTop() == originalTop
                && getLeft() == originalLeft) {
            action = new StandardAction(mBoard, new PointF(touchX, touchY), new PointF(touchX, touchY));
        } else {
            PointF current = new PointF(touchX, touchY);

            int x = (touchX - getLeft()) * (originalRight - originalLeft) / (getRight() - getLeft());
            int y = (touchY - getBottom()) * (originalBottom - originalTop) / (getBottom() - getTop());
            PointF original = new PointF(x + originalLeft, y + originalTop);
            action = new StandardAction(mBoard, original, current);
        }
        mActionList.add(action);
    }

    private boolean isInit = false;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surface 创建成功");
    }

    private int mWidth, mHeight;

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surface 大小改变");
        Log.d(TAG, "surfaceView width=" + width + " height=" + height);

        mWidth = width;
        mHeight = height;
        isInit = true;
        redraw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surface 销毁");
    }

    private int bgClolr = Color.WHITE;
    private Bitmap bgBitmap = null;

    public void setBackground(Bitmap bitmap) {
        Log.d(TAG, "设置图片");
        if (isInit) {
            bgBitmap = ImageUtils.createBitmapForView(bitmap, mWidth, mHeight);
        } else {
            bgBitmap = bitmap;
        }
        redraw();
    }

    private void redraw() {
        if (isInit) {
            Canvas canvas = mHolder.lockCanvas();
            if (bgBitmap != null) {
                bgBitmap = ImageUtils.createBitmapForView(bgBitmap, mWidth, mHeight);
                canvas.drawBitmap(bgBitmap, matrix, new Paint());
            } else {
                canvas.drawColor(bgClolr);
            }
            mHolder.unlockCanvasAndPost(canvas);
        }

    }
}
