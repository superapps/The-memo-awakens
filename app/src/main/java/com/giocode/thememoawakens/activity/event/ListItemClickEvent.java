package com.giocode.thememoawakens.activity.event;

import android.view.View;

public class ListItemClickEvent {

    private final View itemView;
    private final int position;
    private final boolean isLongClick;

    public ListItemClickEvent(View itemView, int position, boolean isLongClick) {
        this.itemView = itemView;
        this.position = position;
        this.isLongClick = isLongClick;
    }

    public int getPosition() {
        return position;
    }

    public boolean isLongClick() {
        return isLongClick;
    }

    public View getItemView() {
        return itemView;
    }

    @Override
    public String toString() {
        return "ListItemClickEvent{" +
                "position=" + position +
                ", isLongClick=" + isLongClick +
                '}';
    }
}
