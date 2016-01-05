package com.giocode.thememoawakens.util;

import android.content.Context;
import android.content.res.ColorStateList;

import com.giocode.thememoawakens.R;

import java.util.Random;

public class ColorUtils {

    private static int[] COLOR_IDS = new int[]{
            R.color.color_1,
            R.color.color_2,
            R.color.color_3,
            R.color.color_4,
            R.color.color_5,
            R.color.color_6,
            R.color.color_7,
            R.color.color_white,
    };

    private static int[] COLOR_PRESSED_IDS = new int[]{
            R.color.color_1_pressed,
            R.color.color_2_pressed,
            R.color.color_3_pressed,
            R.color.color_4_pressed,
            R.color.color_5_pressed,
            R.color.color_6_pressed,
            R.color.color_7_pressed,
            R.color.color_white_pressed
    };

    public static int COLORS_SIZE = COLOR_IDS.length;
    public static int COLOR_WHITE = -1;

    public static int getRandomIndex() {
        return new Random(System.currentTimeMillis()).nextInt(COLOR_IDS.length);
    }

    public static int getColor(final Context context, final int index) {
        return getColor(context, index, false);
    }

    public static int getPressedColor(final Context context, final int index) {
        return getColor(context, index, true);
    }

    public static ColorStateList getColorStateList(final Context context, final int index) {
        return ColorStateList.valueOf(getColor(context, index, false));
    }

    public static ColorStateList getPressedColorStateList(final Context context, final int index) {
        return ColorStateList.valueOf(getColor(context, index, true));
    }

    private static int getColor(final Context context, final int index, final boolean pressed) {
        final int colorId;
        int[] colorIds = pressed ? COLOR_PRESSED_IDS : COLOR_IDS;
        if (index >= 0 && index < colorIds.length) {
            colorId = colorIds[index];
        } else {
            colorId = android.R.color.transparent;
        }
        return context.getResources().getColor(colorId);
    }

}
