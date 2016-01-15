package com.giocode.thememoawakens.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

public class DrawableUtils {

    public static Drawable getTintDrawable(Context context, int drawableResId, int tintColor, int drawableHeight) {
        Drawable drawableWrapper = DrawableCompat.wrap(context.getResources().getDrawable(drawableResId));
        drawableWrapper = drawableWrapper.mutate();
        DrawableCompat.setTint(drawableWrapper, tintColor);
        if (drawableHeight > 0) {
            int width = drawableHeight * drawableWrapper.getIntrinsicWidth() / drawableWrapper.getIntrinsicHeight();
            int height = drawableHeight;
            drawableWrapper.setBounds(0, 0, width, height);
        } else {
            drawableWrapper.setBounds(0, 0, drawableWrapper.getIntrinsicWidth(), drawableWrapper.getIntrinsicHeight());
        }
        return DrawableCompat.unwrap(drawableWrapper);
    }
}
