package com.giocode.thememoawakens.model;

import com.giocode.thememoawakens.util.TextConverter;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Reserved extends RealmObject {

    @PrimaryKey
    private long id;
    @Required
    private String text;
    @Index
    private long parentId;
    private int childCount;
    private RealmList<Span> spans;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public RealmList<Span> getSpans() {
        return spans;
    }

    public void setSpans(RealmList<Span> spans) {
        this.spans = spans;
    }
}
