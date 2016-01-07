package com.giocode.thememoawakens.activity.memo.event;

public class MemoItemClickEvent {

    private final int position;
    private final boolean isLongClick;

    public MemoItemClickEvent(int position, boolean isLongClick) {
        this.position = position;
        this.isLongClick = isLongClick;
    }

    public int getPosition() {
        return position;
    }

    public boolean isLongClick() {
        return isLongClick;
    }

    @Override
    public String toString() {
        return "MemoItemClickEvent{" +
                "position=" + position +
                ", isLongClick=" + isLongClick +
                '}';
    }
}
