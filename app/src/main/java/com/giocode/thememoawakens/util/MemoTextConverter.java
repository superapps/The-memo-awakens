package com.giocode.thememoawakens.util;

import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.UnderlineSpan;

public class MemoTextConverter {

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

    public static CharSequence toCharSequence(final String htmlString) {
        if (TextUtils.isEmpty(htmlString)) {
            return null;
        }
        CharSequence text = Html.fromHtml(htmlString);
        // remove last line feed because of <p></p> tag
        while(text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

}
