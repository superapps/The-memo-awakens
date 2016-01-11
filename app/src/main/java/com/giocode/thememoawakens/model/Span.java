package com.giocode.thememoawakens.model;

import io.realm.RealmObject;

public class Span extends RealmObject {

    private int start;
    private int end;
    private String name;
    private String extra;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public static Span createSpan(int start, int end, String name, String extra) {
        Span span = new Span();
        span.setStart(start);
        span.setEnd(end);
        span.setName(name);
        span.setExtra(extra);
        return span;
    }
}
