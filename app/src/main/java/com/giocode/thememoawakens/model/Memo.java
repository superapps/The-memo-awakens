package com.giocode.thememoawakens.model;

import com.giocode.thememoawakens.util.TextConverter;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Memo extends RealmObject {

    @PrimaryKey
    private long id;
    @Index
    private long time;
    private String text;
    private RealmList<Span> spans;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RealmList<Span> getSpans() {
        return spans;
    }

    public void setSpans(RealmList<Span> spans) {
        this.spans = spans;
    }
}
