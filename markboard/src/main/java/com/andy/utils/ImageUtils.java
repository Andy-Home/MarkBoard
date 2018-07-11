package com.andy.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageUtils {

    public static Bitmap createBitmapForView(Bitmap bitmap, int viewW, int viewH) {
        int imgH = bitmap.getHeight();
        int imgW = bitmap.getWidth();

        if ((imgH == viewH && imgW <= viewW) || (imgH <= viewH && imgW == viewW)) {
            return Bitmap.createBitmap(bitmap);
        }

        float w = (float) viewW / (float) imgW;
        float h = (float) viewH / (float) imgH;

        float multiple = w > h ? h : w;
        Matrix matrix = new Matrix();
        matrix.postScale(multiple, multiple);
        return Bitmap.createBitmap(bitmap, 0, 0, imgW, imgH, matrix, true);
    }
}
