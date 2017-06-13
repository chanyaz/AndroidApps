package com.july.cloud.timepickerdemo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;


public class DimensionUtil {

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
    public static float convertDpToPixel(Context context, float dp) {
        Resources res = context.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertSpToPixel(Context context, float spValue) {
        Resources res = context.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        float fontScale = metrics.scaledDensity;
        return spValue * fontScale + 0.5f;
    }

    public static int calcTextWidth(Paint paint, String demoText) {
        return (int) paint.measureText(demoText);
    }

    public static int calcTextHeight(Paint paint, String demoText) {

        Rect r = new Rect();
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return r.height();
    }
}
