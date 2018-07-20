package com.andy.view.board;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.andy.R;
import com.andy.utils.ViewUtils;
import com.andy.view.action.StandardAction;

public class StandardBoard extends BoardView {
    private final String TAG = StandardBoard.class.getSimpleName();

    public StandardBoard(Context context) {
        super(context);
    }

    public StandardBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StandardBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float centerX, centerY;
    private Bitmap mPointer = null;

    public void showPointer(boolean isShow) {
        if (isShow) {
            BitmapFactory.Options opts = new BitmapFactory.Options();

            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            mPointer = BitmapFactory.decodeResource(getResources(), R.mipmap.pointer, opts);

            centerX = getLeft() + (getWidth() / 2);
            centerY = getTop() + (getHeight() / 2);
        } else {
            mPointer = null;
        }
        notifyDataChange();
    }

    /**
     * 缩放的两个值，用来计算两者之间的比例值
     */
    private float startDis;

    private PointF startPoint = new PointF();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                Matrix temp = new Matrix();
                temp.set(matrix);
                if (getStatus() == MODE_DRAG) {
                    float dx = event.getX() - startPoint.x;
                    float dy = event.getY() - startPoint.y;
                    startPoint.set(event.getX(), event.getY());
                    matrix.postTranslate(dx, dy);
                    if (checkCrossing()) {
                        matrix.set(temp);
                    } else {
                        mBoard.setOffset(dx, dy);
                    }
                } else if (getStatus() == MODE_ZOOM) {
                    float endDis = ViewUtils.distance(event);
                    //横竖轴放大倍数一样，保证图片比例
                    float scale = endDis / startDis;
                    startDis = endDis;
                    PointF midPoint = ViewUtils.mid(event);
                    matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    if (checkMinimum() || checkCrossing()) {
                        matrix.set(temp);
                    } else {
                        mBoard.setScale(scale, midPoint);
                    }
                }
                if (!matrix.equals(temp)) {
                    notifyDataChange();
                }
                break;

            case MotionEvent.ACTION_DOWN:
                startPoint.set(event.getX(), event.getY());
                setState(MODE_DRAG);
                if (event.getPointerCount() == 1) {
                    super.removeCallback();
                    postCheckLongTouch(event.getX(), event.getY());
                }
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                startDis = ViewUtils.distance(event);
                setState(MODE_ZOOM);
                break;

            case MotionEvent.ACTION_UP:
                setState(MODE_NONE);
                super.removeCallback();
                notifyDataChange();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                setState(MODE_NONE);
                notifyDataChange();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断图片的缩小是否小于原图
     */
    private boolean checkMinimum() {
        float[] values = new float[9];
        matrix.getValues(values);
        //防止缩放图片比原图片更小
        return values[Matrix.MSCALE_X] < 1f;
    }

    private boolean checkCrossing() {
        float[] values = new float[9];
        matrix.getValues(values);
        float left = values[2];
        float top = values[5];
        float right = values[2] + bitmapWidth * values[0];
        float bottom = values[5] + bitmapHeight * values[4];

        return left > centerX || right < centerX || top > centerY || bottom < centerY;
    }

    /**
     * 创建Action
     */
    public void createAction(StandardAction action) {
        float[] values = new float[9];
        matrix.getValues(values);
        float left = values[2];
        float top = values[5];
        float right = values[2] + bitmapWidth * values[0];
        float bottom = values[5] + bitmapHeight * values[4];

        action.setObserver(mBoard);

        PointF current = new PointF(centerX, centerY);
        action.setCurrentPoint(current);

        float x = (centerX - left) * (originalBitmapRight - originalBitmapLeft) / (right - left);
        float y = (centerY - top) * (originalBitmapBottom - originalBitmapTop) / (bottom - top);
        PointF original = new PointF(x + originalBitmapLeft, y + originalBitmapTop);
        action.setOriginalPoint(original);

        action.x = (original.x - originalBitmapLeft) / (originalBitmapRight - originalBitmapLeft);
        action.y = (original.y - originalBitmapTop) / (originalBitmapBottom - originalBitmapTop);

        Log.d(TAG, "创建Action成功");
        addAction(action);
    }

    @Override
    protected void reDraw(Canvas canvas) {
        if (mPointer != null) {
            Rect src = new Rect(0, 0, mPointer.getWidth(), mPointer.getHeight());
            Rect dst = new Rect((int) centerX, (int) centerY, (int) centerX + 100, (int) centerY + 100);
            canvas.drawBitmap(mPointer, src, dst, mPaint);
        }
    }
}
