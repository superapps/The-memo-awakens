package com.giocode.thememoawakens.util;

import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;

public class TextConverter {

    public static String toHtmlString(final Spannable spannable) {
        if (spannable == null || spannable.length() == 0) {
            return null;
        }

        CharacterStyle[] allSpans = spannable.getSpans(0, spannable.length(), CharacterStyle.class);
        for (CharacterStyle span : allSpans) {
            if (span instanceof UnderlineSpan) {
                spannable.removeSpan(span);
            } else if (span instanceof BackgroundColorSpan) {
                spannable.removeSpan(span);
                int color = ((BackgroundColorSpan)span).getBackgroundColor();
                if (color != ColorUtils.COLOR_WHITE) {
                    spannable.setSpan(new ForegroundColorSpan(color), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        String htmlText = Html.toHtml(spannable);
        return htmlText.replace("\n", "");
    }

    public static CharSequence toCharSequence(final String htmlString) {
        if (TextUtils.isEmpty(htmlString)) {
            return null;
        }
        CharSequence text = Html.fromHtml(htmlString);
        Spannable spannable = (Spannable)text;
        ForegroundColorSpan[] foregroundColorSpans = spannable.getSpans(0, spannable.length(), ForegroundColorSpan.class);
        if (foregroundColorSpans != null && foregroundColorSpans.length > 0) {
            for (ForegroundColorSpan foregroundColorSpan : foregroundColorSpans) {
                spannable.removeSpan(foregroundColorSpan);
            }
            int color = ((ForegroundColorSpan) foregroundColorSpans[0]).getForegroundColor();
            spannable.setSpan(new BackgroundColorSpan(color), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        // remove last line feed because of <p></p> tag
        while(text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

    public static void changeBgColor(final Spannable spannable, final int bgColor) {
        BackgroundColorSpan[] spans = spannable.getSpans(0, spannable.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan backgroundColorSpan : spans) {
            spannable.removeSpan(backgroundColorSpan);
        }
        if (bgColor != ColorUtils.COLOR_WHITE) {
            spannable.setSpan(new BackgroundColorSpan(bgColor), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

}
