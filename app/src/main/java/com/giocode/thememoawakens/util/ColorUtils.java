package com.giocode.thememoawakens.util;

import android.content.Context;
import android.content.res.ColorStateList;

import com.giocode.thememoawakens.R;

import java.util.Random;

public class ColorUtils {

    private static int[] COLOR_IDS = new int[]{
            R.color.color_blue,
            R.color.color_green,
            R.color.color_indigo,
            R.color.color_pink,
            R.color.color_purple,
            R.color.color_red,
            R.color.color_yellow,
    };

    private static int[] COLOR_PRESSED_IDS = new int[]{
            R.color.color_blue_pressed,
            R.color.color_green_pressed,
            R.color.color_indigo_pressed,
            R.color.color_pink_pressed,
            R.color.color_purple_pressed,
            R.color.color_red_pressed,
            R.color.color_yellow_pressed,
    };

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
            colorId = colorIds[0];
        }
        return context.getResources().getColor(colorId);
    }

}
