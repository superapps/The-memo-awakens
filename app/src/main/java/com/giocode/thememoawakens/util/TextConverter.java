package com.giocode.thememoawakens.util;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import com.giocode.thememoawakens.AMemoApplication;

public class TextConverter {

    public static String toHtmlString(final Spannable spannable) {
        if (spannable == null || spannable.length() == 0) {
            return null;
        }

        CharacterStyle[] allSpans = spannable.getSpans(0, spannable.length(), CharacterStyle.class);
        for (CharacterStyle span : allSpans) {
            if (span instanceof UnderlineSpan) {
                spannable.removeSpan(span);
            }
        }
        String htmlText = Html.toHtml(spannable);
        return htmlText.replace("\n", "");
    }

    public static CharSequence toCharSequence(final String htmlString, final TextView textView) {
        if (TextUtils.isEmpty(htmlString)) {
            return null;
        }
        CharSequence text = Html.fromHtml(htmlString, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                try {
                    int colorIndex = Integer.valueOf(source);
                    return ColorUtils.getTagDrawable(AMemoApplication.applicationContext, textView, colorIndex);
                } catch (Exception ignore) {
                }
                return null;
            }
        }, null);

        // remove last line feed because of <p></p> tag
        while(text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }
}
