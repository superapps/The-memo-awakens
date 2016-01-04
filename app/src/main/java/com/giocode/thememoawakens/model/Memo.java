package com.giocode.thememoawakens.model;

import com.giocode.thememoawakens.util.TextConverter;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Memo extends RealmObject {

    @PrimaryKey
    private long id;
    @Index
    private long time;
    private String htmlText;

    @Ignore
    private CharSequence text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
            String htmlText = getHtmlText();
            text = TextConverter.toCharSequence(htmlText);
        }
        return text;
    }
}
