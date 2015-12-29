package com.giocode.thememoawakens.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtils {

    public static int toPixel(final Context context, float dip) {
        final float scale = getDisplayDensity(context);
        return (int) (dip * scale + 0.5f);
    }

    public static float getDisplayDensity(final Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getDisplayWidth(final Context context) {
        int pixel = context.getResources().getDisplayMetrics().widthPixels;
        if (pixel <= 0) {
            final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            final DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            return dm.widthPixels;
        } else {
            return pixel;
        }
    }

    public static int getDisplayHeight(final Context context) {
        int pixel = context.getResources().getDisplayMetrics().heightPixels;
        if (pixel <= 0) {
            final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            final DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            return dm.heightPixels;
        } else {
            return pixel;
        }
    }


}
