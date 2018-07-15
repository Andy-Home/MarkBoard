package com.andy.view.board;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.andy.utils.ImageUtils;
import com.andy.utils.ViewUtils;
import com.andy.view.action.Action;
import com.andy.view.action.StandardAction;

import java.util.ArrayList;
import java.util.List;

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

    private InitListener mInitListener;

    public void init(InitListener listener) {
        mInitListener = listener;
        isInit = false;
    }

    public interface InitListener {
        void onInit();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);

        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);

        matrix = new Matrix();

        mActionList = new ArrayList<>();
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
                    mBoard.setOffset(dx, dy);
                } else if (getStatus() == MODE_ZOOM) {
                    float endDis = ViewUtils.distance(event);
                    //横竖轴放大倍数一样，保证图片比例
                    float scale = endDis / startDis;
                    startDis = endDis;
                    PointF midPoint = ViewUtils.mid(event);
                    matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    if (checkMinimum()) {
                        matrix.set(temp);
                    } else {
                        mBoard.setScale(scale, midPoint);
                    }
                }
                if (!matrix.equals(temp)) {
                    redraw();
                }
                break;

            case MotionEvent.ACTION_DOWN:
                clickDownTime = System.currentTimeMillis();
                startPoint.set(event.getX(), event.getY());
                setState(MODE_DRAG);
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                startDis = ViewUtils.distance(event);
                setState(MODE_ZOOM);
                break;

            case MotionEvent.ACTION_UP:
                setState(MODE_NONE);
                if (isClick()) {
                    createAction(event.getX(), event.getY());
                }
                redraw();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                setState(MODE_NONE);
                redraw();
                break;
        }
        return super.onTouchEvent(event);
    }

    private long clickDownTime;

    private boolean isClick() {
        return System.currentTimeMillis() - clickDownTime < 500;
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

    /**
     * 创建Action
     *
     * @param touchX 相对当前触摸View的坐标系的X
     * @param touchY 相对当前触摸View的坐标系的Y
     */
    public void createAction(float touchX, float touchY) {
        float[] values = new float[9];
        matrix.getValues(values);
        float left = values[2];
        float top = values[5];
        float right = values[2] + bitmapWidth * values[0];

        float bottom = values[5] + bitmapHeight * values[4];
        //Log.d(TAG,"图片宽高： l:"+values[2]+" t:"+values[5]+" r:"+right+" b:"+bottom);
        Action action;

        PointF current = new PointF(touchX, touchY);

        float x = (touchX - left) * (originalBitmapRight - originalBitmapLeft) / (right - left);
        float y = (touchY - top) * (originalBitmapBottom - originalBitmapTop) / (bottom - top);
        PointF original = new PointF(x + originalBitmapLeft, y + originalBitmapTop);
        action = new StandardAction(mBoard, original, current);

        Log.d(TAG, "创建Action成功");
        mActionList.add(action);
    }

    private Paint mPaint;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surface 创建成功");
        mPaint = new Paint();
    }

    private int mWidth, mHeight;
    private boolean isInit = false;

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surface 大小改变");
        Log.d(TAG, "surfaceView width=" + width + " height=" + height);

        mWidth = width;
        mHeight = height;
        if (!isInit) {
            isInit = true;
            mInitListener.onInit();
        }

        redraw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surface 销毁");
    }

    private int bgColor = Color.WHITE;
    private Bitmap bgBitmap = null;

    //传入图片初次显示时的边界值，以SurfaceView为相对坐标系
    private int originalBitmapLeft, originalBitmapRight, originalBitmapTop, originalBitmapBottom;

    int bitmapWidth, bitmapHeight;

    public void setBackground(Bitmap bitmap) {
        Log.d(TAG, "设置图片");
        bgBitmap = ImageUtils.createBitmapForView(bitmap, mWidth, mHeight);
        Log.d(TAG, "图片宽：" + bgBitmap.getWidth() + " 图片高：" + bgBitmap.getHeight());
        bitmapWidth = bgBitmap.getWidth();
        bitmapHeight = bgBitmap.getHeight();
        if (bgBitmap.getWidth() != mWidth) {
            int marginWidth = (mWidth - bgBitmap.getWidth()) / 2;
            matrix.postTranslate(marginWidth, 0);

            originalBitmapTop = 0;
            originalBitmapBottom = mHeight;
            originalBitmapLeft = marginWidth;
            originalBitmapRight = mWidth - marginWidth;

        } else if (bgBitmap.getHeight() != mHeight) {
            int marginHeight = (mHeight - bgBitmap.getHeight()) / 2;
            matrix.postTranslate(0, marginHeight);

            originalBitmapLeft = 0;
            originalBitmapRight = mWidth;
            originalBitmapTop = marginHeight;
            originalBitmapBottom = mHeight - marginHeight;
        }
        redraw();
    }

    public void setColor(int color) {
        bgColor = color;
    }

    private void redraw() {
        synchronized (StandardBoard.class) {
            Canvas canvas = mHolder.lockCanvas();
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            if (bgBitmap != null) {
                canvas.drawColor(bgColor);
                canvas.drawBitmap(bgBitmap, matrix, mPaint);

            } else {
                canvas.drawColor(bgColor);
            }
            for (Action action : mActionList) {
                action.onDraw(canvas);
            }
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 清空内容
     */
    public void clear() {
        bgBitmap = null;
        mActionList.clear();
        redraw();
    }

    /**
     * 获取所有Action在图片中的相对位置比例
     */
    public List<PointF> getActionValue() {
        if (mActionList.isEmpty()) {
            return null;
        }
        List<PointF> value = new ArrayList<>();

        for (Action action : mActionList) {
            //获取Action在View正常情况下的点的位置
            PointF point = action.getOriginalPoint();
            float x = (point.x - originalBitmapLeft) / (originalBitmapRight - originalBitmapLeft);
            float y = (point.y - originalBitmapTop) / (originalBitmapBottom - originalBitmapTop);
            PointF item = new PointF(x, y);
            value.add(item);
        }
        return value;
    }
}
