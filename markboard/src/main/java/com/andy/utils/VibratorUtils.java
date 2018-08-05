package com.andy.utils;

import android.app.Service;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibratorUtils {
    private Vibrator mVibrator;

    private VibratorUtils() {
    }

    public static VibratorUtils getInstance() {
        return SingleHolder.vibratorUtils;
    }

    private static class SingleHolder {
        private static VibratorUtils vibratorUtils = new VibratorUtils();
    }

    public void init(Context context) {
        mVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
    }

    public void longClick() {
        VibrationEffect vibrationEffect;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrationEffect = VibrationEffect.createOneShot(100, 10);
            mVibrator.vibrate(vibrationEffect);
        } else {
            mVibrator.vibrate(100);
        }
    }

    public void click() {
        VibrationEffect vibrationEffect;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrationEffect = VibrationEffect.createOneShot(50, 10);
            mVibrator.vibrate(vibrationEffect);
        } else {
            mVibrator.vibrate(50);
        }
    }
}
