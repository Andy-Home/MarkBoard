package com.andy.view.board;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.andy.utils.ImageUtils;
import com.andy.utils.VibratorUtils;
import com.andy.view.action.Action;
import com.andy.view.action.StandardAction;

import java.util.ArrayList;
import java.util.List;

public abstract class BoardView extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = BoardView.class.getSimpleName();
    public static final int MODE_NONE = -1;
    public static final int MODE_ZOOM = 0;
    public static final int MODE_DRAG = 1;

    private int current_status = MODE_NONE;
    /**
     * BoardView 被订阅的参数
     */
    public Board mBoard = new Board();

    private ArrayList<Action> mActionList;
    protected Matrix matrix = null;

    public BoardView(Context context) {
        super(context);
        init(context);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private SurfaceHolder mHolder = null;

    private VibratorUtils mVibratorUtils;

    private void init(Context context) {
        matrix = new Matrix();
        mActionList = new ArrayList<>();

        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);

        mVibratorUtils = VibratorUtils.getInstance();
        mVibratorUtils.init(context);
    }

    public void setState(int state) {
        current_status = state;
        mBoard.setState(state);
    }

    public int getStatus() {
        return current_status;
    }

    //传入图片初次显示时的边界值，以SurfaceView为相对坐标系
    protected int originalBitmapLeft, originalBitmapRight, originalBitmapTop, originalBitmapBottom;
    protected int bitmapWidth, bitmapHeight;

    private int bgColor = Color.BLACK;
    private Bitmap bgBitmap = null;

    public void setBackgroundColor(int color) {
        bgColor = color;
    }

    public void setBoardContent(final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isInit) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "设置图片");
                bgBitmap = ImageUtils.createBitmapForView(bitmap, mWidth, mHeight);
                Log.d(TAG, "图片宽：" + bgBitmap.getWidth() + " 图片高：" + bgBitmap.getHeight());
                initValue();
            }
        }).start();
    }

    public void setBoardContent(int resId) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resId, opts);
        setBoardContent(bmp);
    }

    private void initValue() {
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
        notifyDataChange();
    }

    protected Paint mPaint;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surface 创建成功");
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
    }

    private int mWidth, mHeight;
    private boolean isInit = false;

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surface 大小改变");
        Log.d(TAG, "surfaceView width=" + width + " height=" + height);

        mWidth = width;
        mHeight = height;
        isInit = true;
        viewChanged();

        notifyDataChange();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surface 销毁");
        isInit = false;
    }

    private void viewChanged() {
        Action.setParentView(mWidth, mHeight);
    }

    protected void reDraw(Canvas canvas) {
    }

    public void notifyDataChange() {

        if (!isInit) {
            return;
        }

        synchronized (BoardView.class) {

            Canvas canvas = mHolder.lockCanvas();
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

            if (bgBitmap != null) {

                canvas.drawColor(bgColor);
                canvas.drawBitmap(bgBitmap, matrix, mPaint);

                float[] values = new float[9];
                matrix.getValues(values);
                float left = values[2];
                float top = values[5];
                float right = values[2] + bitmapWidth * values[0];
                float bottom = values[5] + bitmapHeight * values[4];
                Action.setBackgroundPosition(left, right, top, bottom);

            } else {
                canvas.drawColor(bgColor);
            }

            for (Action action : mActionList) {
                action.onDraw(canvas);
            }

            reDraw(canvas);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 清空内容
     */
    public void clear() {
        mActionList.clear();
        notifyDataChange();
    }


    //////////////////////////////  操作Action   ////////////////////////////////////////

    /**
     * 获取所有Action在图片中的相对位置比例
     */
    public List<PointF> getActionValues() {
        if (mActionList.isEmpty()) {
            return null;
        }
        List<PointF> value = new ArrayList<>();

        for (Action action : mActionList) {

            value.add(getActionValue(action));
        }
        return value;
    }

    public PointF getLastActionValue() {
        return getActionValue(mActionList.get(mActionList.size() - 1));
    }

    private PointF getActionValue(Action action) {
        return new PointF(action.x, action.y);
    }

    public Action getAction(int index) {
        return mActionList.get(index);
    }

    public ArrayList<Action> getActionList() {
        return mActionList;
    }

    public boolean removeAction(Action action) {
        if (mActionList.remove(action)) {
            notifyDataChange();
            return true;
        }
        return false;
    }

    public Action removeAction(int index) {
        Action action = mActionList.remove(index);

        notifyDataChange();
        return action;
    }

    public boolean addAction(Action action) {
        if (mActionList.add(action)) {
            notifyDataChange();
            return true;
        }
        return false;
    }

    public boolean putActionValue(List<Action> list) {
        if (bgBitmap == null) {
            return false;
        }

        for (Action action : list) {
            float x = (originalBitmapRight - originalBitmapLeft) * action.x + originalBitmapLeft;
            float y = (originalBitmapBottom - originalBitmapTop) * action.y + originalBitmapTop;
            Action a = restoreAction(x, y);
            a.setContent(action.getContent());
            a.x = action.x;
            a.y = action.y;
            mActionList.add(a);
        }
        notifyDataChange();
        return true;
    }

    public boolean putDefaultAction(List<PointF> list) {
        if (bgBitmap == null) {
            return false;
        }

        for (PointF point : list) {
            float x = (originalBitmapRight - originalBitmapLeft) * point.x + originalBitmapLeft;
            float y = (originalBitmapBottom - originalBitmapTop) * point.y + originalBitmapTop;
            mActionList.add(restoreAction(x, y));
        }
        notifyDataChange();
        return true;
    }

    private Action restoreAction(float originalX, float originalY) {
        float[] values = new float[9];
        matrix.getValues(values);
        float left = values[2];
        float top = values[5];
        float right = values[2] + bitmapWidth * values[0];

        float bottom = values[5] + bitmapHeight * values[4];

        Action action;

        PointF original = new PointF(originalX, originalY);

        float x = (originalX - originalBitmapLeft) * (right - left) / (originalBitmapRight - originalBitmapLeft);
        float y = (originalY - originalBitmapTop) * (bottom - top) / (originalBitmapBottom - originalBitmapTop);

        PointF current = new PointF(x + left, y + top);
        action = new StandardAction(mBoard, original, current);

        return action;
    }

    ///////////////////////////////// Listener //////////////////////////
    protected void postCheckLongTouch(float x, float y) {
        mLongPressRunnable.setPressLocation(x, y);
        postDelayed(mLongPressRunnable, 1000);
    }

    protected void removeCallback() {
        removeCallbacks(mLongPressRunnable);
    }

    protected onActionLongClickListener mActionLongClickListener;

    public void setActionLongClickListener(onActionLongClickListener listener) {
        mActionLongClickListener = listener;
    }

    public interface onActionLongClickListener {

        void onLongClick(int position);
    }

    private LongPressRunnable mLongPressRunnable = new LongPressRunnable();

    private class LongPressRunnable implements Runnable {

        private float x, y;

        private void setPressLocation(float x, float y) {
            this.x = (int) x;
            this.y = (int) y;
        }

        @Override
        public void run() {
            Log.d(TAG, "触发长按点 x:" + x + " y:" + y);
            for (int i = 0; i < mActionList.size(); i++) {
                if (mActionList.get(i).isTouch(x, y)) {
                    mVibratorUtils.longClick();
                    if (mActionLongClickListener != null) {
                        mActionLongClickListener.onLongClick(i);
                        notifyDataChange();
                    }
                    break;
                }
            }
        }

    }

    protected onActionClickListener mActionClickListener;

    public void setActionClickListener(onActionClickListener listener) {
        mActionClickListener = listener;
    }

    public interface onActionClickListener {
        void onClick(int position);
    }

    protected void postCheckClick(float x, float y) {
        PerformClick performClick = new PerformClick();
        performClick.setPressLocation(x, y);
        post(performClick);
    }

    private final class PerformClick implements Runnable {

        private float x, y;

        private void setPressLocation(float x, float y) {
            this.x = (int) x;
            this.y = (int) y;
        }

        @Override
        public void run() {
            for (int i = 0; i < mActionList.size(); i++) {
                if (mActionList.get(i).isTouch(x, y)) {
                    mVibratorUtils.click();
                    if (mActionClickListener != null) {
                        mActionClickListener.onClick(i);
                        notifyDataChange();
                    }
                    break;
                }
            }
        }
    }
}
