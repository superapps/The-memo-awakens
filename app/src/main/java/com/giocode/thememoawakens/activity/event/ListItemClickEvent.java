package com.giocode.thememoawakens.activity.event;

public class ListItemClickEvent {

    private final int position;
    private final boolean isLongClick;

    public ListItemClickEvent(int position, boolean isLongClick) {
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
        return "ListItemClickEvent{" +
                "position=" + position +
                ", isLongClick=" + isLongClick +
                '}';
    }
}
