package com.giocode.thememoawakens.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.TextView;

import com.giocode.thememoawakens.AMemoApplication;
import com.giocode.thememoawakens.BuildConfig;
import com.giocode.thememoawakens.model.Span;

import io.realm.RealmList;

public class TextConverter {

    public static Pair<String, RealmList<Span>> toTextSpanInfo(final Spannable spannable) {
        if (spannable == null || spannable.length() == 0) {
            return null;
        }

        int len = spannable.length();
        int next;
        RealmList<Span> spans = new RealmList<>();
        for (int i = 0; i < spannable.length(); i = next) {
            next = spannable.nextSpanTransition(i, len, ImageSpan.class);
            ImageSpan[] imageSpans = spannable.getSpans(i, next, ImageSpan.class);
            if (imageSpans != null && imageSpans.length > 0) {
                for(ImageSpan imageSpan : imageSpans) {
                    spans.add(Span.createSpan(i, next, ImageSpan.class.getSimpleName(), imageSpan.getSource()));
                    if (BuildConfig.DEBUG) {
                        Log.d("gio", "start=" + i + ", end=" + next + ", ImageSpan source=" + imageSpan.getSource());
                    }
                }
            }
        }


        return new Pair<>(spannable.toString(), spans);
    }

    public static CharSequence toCharSequence(final Context context, final String text, final RealmList<Span> spans, final TextView textView) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        if (spans != null) {
            for (Span span : spans) {
                if (span.getName().equals(ImageSpan.class.getSimpleName())) {
                    String source = span.getExtra();
                    int drawableIndex = Integer.valueOf(source);
                    ssb.setSpan(new ImageSpan(ColorUtils.getTagDrawable(context, textView, drawableIndex), source), span.getStart(), span.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return ssb;
    }

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
