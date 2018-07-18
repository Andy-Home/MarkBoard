package com.andy.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;


public class ImageUtils {

    public static Bitmap createBitmapForView(Bitmap bitmap, int viewW, int viewH) {
        int imgH = bitmap.getHeight();
        int imgW = bitmap.getWidth();

        float multiple = needScale(imgW, imgH, viewW, viewH);

        Matrix matrix = new Matrix();
        matrix.postScale(multiple, multiple);
        return Bitmap.createBitmap(bitmap, 0, 0, imgW, imgH, matrix, true);
    }

    public static float needScale(int imgW, int imgH, int viewW, int viewH) {

        if ((imgH == viewH && imgW <= viewW) || (imgH <= viewH && imgW == viewW)) {
            return 1f;
        }

        float w = (float) viewW / (float) imgW;
        float h = (float) viewH / (float) imgH;

        return w > h ? h : w;
    }

}
