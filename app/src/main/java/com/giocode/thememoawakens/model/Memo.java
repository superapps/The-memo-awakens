package com.giocode.thememoawakens.model;

import android.text.Html;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.Required;

public class Memo extends RealmObject {

    @Index
    private long time;
    private String htmlText;

    @Ignore
    private CharSequence text;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    public CharSequence getText() {
        if (text == null) {
            text = Html.fromHtml(getHtmlText());
        }
        return text;
    }
}
