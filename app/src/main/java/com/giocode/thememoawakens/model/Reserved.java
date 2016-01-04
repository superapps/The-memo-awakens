package com.giocode.thememoawakens.model;

import com.giocode.thememoawakens.util.TextConverter;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Reserved extends RealmObject {

    @PrimaryKey
    private long id;
    @Required
    private String htmlText;
    @Index
    private long parentId;
    private int childCount;

    @Ignore
    private CharSequence text;

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public CharSequence getText() {
        if (text == null) {
            String htmlText = getHtmlText();
            text = TextConverter.toCharSequence(htmlText);
        }
        return text;
    }

}
