package com.andy.view.action;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Observer;

public abstract class Action implements Observer, Parcelable {

    protected Action() {
    }

    private PointF originalPoint;

    public PointF getOriginalPoint() {
        return originalPoint;
    }

    public void setOriginalPoint(PointF originalPoint) {
        this.originalPoint = originalPoint;
    }

    abstract public void onDraw(Canvas canvas);

    public float x, y;

    /**
     * 是否已经触摸到Action
     */
    abstract public boolean isTouch(float touchX, float touchY);

    private String content = null;

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    protected static int parentWidth, parentHeight;

    /**
     * 设置白板的长宽
     */
    public static void setParentView(int width, int height) {
        parentWidth = width;
        parentHeight = height;
    }

    protected static float bg_l, bg_r, bg_t, bg_b;

    public static void setBackgroundPosition(float l, float r, float t, float b) {
        bg_l = l;
        bg_r = r;
        bg_t = t;
        bg_b = b;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeString(content);
    }

    protected Action(Parcel source) {
        x = source.readFloat();
        y = source.readFloat();
        content = source.readString();
    }
}
