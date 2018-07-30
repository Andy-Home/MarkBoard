package com.andy.view.action;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.andy.R;
import com.andy.utils.ViewUtils;
import com.andy.view.board.Board;
import com.andy.view.board.BoardView;

import java.util.Observable;

public class StandardAction extends Action {
    private final String TAG = StandardAction.class.getSimpleName();
    private PointF currentPoint;

    private Paint mPaint;
    private TextPaint mTextPaint;

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
        mPaint.setAntiAlias(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(40);
    }

    @Override
    public void update(Observable o, Object arg) {
        //Log.d(TAG, "action收到数据改变");
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
        if (!TextUtils.isEmpty(getContent())) {
            if (!showContent) {
                if (mBitmapLabel != null) {
                    Rect src = new Rect(0, 0, mBitmapLabel.getWidth(), mBitmapLabel.getHeight());
                    Rect dst = new Rect((int) currentPoint.x - 5, (int) currentPoint.y - 5, (int) currentPoint.x + 50, (int) currentPoint.y + 50);
                    canvas.drawBitmap(mBitmapLabel, src, dst, mPaint);
                }
            }
        }
        canvas.drawCircle(currentPoint.x, currentPoint.y, radius, mPaint);
        if (showContent) {
            String content = getContent();

            StaticLayout mStaticLayout = new StaticLayout(content, 0, content.length(), mTextPaint, parentWidth / 4, Layout.Alignment.ALIGN_NORMAL, 1.2f, 0.0f, false);

            float textWith = mStaticLayout.getLineWidth(0);
            int textHeight = mStaticLayout.getHeight();

            float translateX = currentPoint.x;
            if (currentPoint.x + textWith >= bg_r || currentPoint.x + textWith >= parentWidth) {
                translateX = currentPoint.x - textWith;
            }

            float translateY = currentPoint.y;
            if (currentPoint.y + textHeight >= bg_b || currentPoint.y + textHeight >= parentHeight) {
                translateY = currentPoint.y - textHeight;
            }

            Rect src = new Rect(0, 0, mBitmapBackground.getWidth(), mBitmapBackground.getHeight());
            int margin = 12;
            Rect dst = new Rect((int) translateX - margin, (int) translateY - margin, (int) (translateX + textWith) + margin, (int) translateY + textHeight + margin);
            canvas.drawBitmap(mBitmapBackground, src, dst, mPaint);

            canvas.translate(translateX, translateY);
            mStaticLayout.draw(canvas);

            canvas.translate(-translateX, -translateY);
        }
    }

    @Override
    public boolean isTouch(float touchX, float touchY) {
        double length = ViewUtils.getLength(touchX, currentPoint.x, touchY, currentPoint.y);
        return length <= radius + 20;
    }

    private static Bitmap mBitmapLabel = null;

    public static void showLabel(boolean isShow, Context context) {
        if (isShow) {
            if (mBitmapLabel != null) {
                return;
            }

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            mBitmapLabel = BitmapFactory.decodeResource(context.getResources(), R.mipmap.label, opts);
        } else {
            mBitmapLabel = null;
        }

    }

    private static Bitmap mBitmapBackground = null;
    private boolean showContent = false;

    public boolean showContent(Context context) {
        if (TextUtils.isEmpty(getContent())) {
            showContent = false;
            return false;
        }
        showContent = true;

        if (mBitmapBackground != null) {
            return true;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        mBitmapBackground = BitmapFactory.decodeResource(context.getResources(), R.mipmap.bg_action_content, opts);
        return true;
    }

    public boolean showContent(String content, Context context) {
        setContent(content);
        return showContent(context);
    }

    public boolean isShowContent() {
        return showContent;
    }

    public boolean hideContent() {
        showContent = false;
        return true;
    }

    /////////////////////// 序列化 //////////////////////////////////
    private StandardAction(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<StandardAction> CREATOR = new Parcelable.Creator<StandardAction>() {
        public StandardAction createFromParcel(Parcel in) {
            return new StandardAction(in);
        }

        public StandardAction[] newArray(int size) {
            return new StandardAction[size];
        }
    };
}
