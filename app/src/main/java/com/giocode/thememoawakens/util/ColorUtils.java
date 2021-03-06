package com.giocode.thememoawakens.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.giocode.thememoawakens.R;

import java.util.Random;

public class ColorUtils {

    public static int DELIMITER_START_ID = 100;

    private static int[] COLOR_IDS = new int[]{
            R.color.color_1,
            R.color.color_2,
            R.color.color_3,
            R.color.color_4,
            R.color.color_5,
            R.color.color_6,
            R.color.color_7,
    };

    public static int COLORS_SIZE = COLOR_IDS.length;
    public static int COLOR_WHITE = -1;

    public static int getRandomIndex() {
        return new Random(System.currentTimeMillis()).nextInt(COLOR_IDS.length);
    }

    public static Drawable getTagDrawable(final Context context, final TextView textView, int id) {
        int drawableResId;
        if (id >= DELIMITER_START_ID) {
            drawableResId = R.drawable.ic_keyboard_arrow_right_black_24dp;
            id -= DELIMITER_START_ID;
        } else {
            drawableResId = R.drawable.ic_label_black_24dp;
        }
        return DrawableUtils.getTintDrawable(context, drawableResId, ColorUtils.getColor(context, id)
                , textView != null ? textView.getLineHeight() : 0);
    }

    public static SpannableStringBuilder getTagSpannableStringBuilder(final Context context, final TextView textView, int id) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(" ");
        spannable.setSpan(new ImageSpan(getTagDrawable(context, textView, id), String.valueOf(id), ImageSpan.ALIGN_BOTTOM), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static int getTagColorId(final Context context, final Spannable spannable) {
        ImageSpan[] imageSpans = spannable.getSpans(0, spannable.length(), ImageSpan.class);
        if (imageSpans != null && imageSpans.length > 0) {
            try {
                return Integer.valueOf(imageSpans[0].getSource());
            } catch (Exception ignore) {
            }
        }

        return 0;
    }

    private static int getColor(final Context context, final int index) {
        final int colorId;
        if (index >= 0 && index < COLOR_IDS.length) {
            colorId = COLOR_IDS[index];
        } else {
            colorId = android.R.color.transparent;
        }
        return context.getResources().getColor(colorId);
    }

}
