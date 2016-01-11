package com.giocode.thememoawakens.activity.reserved;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.activity.event.ListItemClickEvent;
import com.giocode.thememoawakens.eventbus.EventBus;
import com.giocode.thememoawakens.model.Reserved;
import com.giocode.thememoawakens.util.TextConverter;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class ReservedAdapter extends RecyclerView.Adapter<ReservedAdapter.ReservedViewHolder> {

    List<Reserved> reservedResults;
    List<Reserved> selectedReservedResults;
    private boolean selectedMode;
    private boolean enableChild;

    @Override
    public ReservedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReservedViewHolder(this, LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.reserved_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ReservedViewHolder holder, int position) {

        holder.update(position, reservedResults.get(position));
    }

    @Override
    public int getItemCount() {
        return reservedResults.size();
    }

    public void updateMemoResults(RealmResults<Reserved> reservedResults) {
        this.reservedResults = reservedResults;
    }

    public boolean isSelectedMode() {
        return selectedMode;
    }

    public void setSelectedMode(boolean selectMode, int position) {
        this.selectedMode = selectMode;
        if (selectMode) {
            toggleSelectItem(position);
        } else {
            selectedReservedResults = null;
            notifyDataSetChanged();
        }
    }

    public void toggleSelectItem(int position) {
        if (selectedReservedResults == null) {
            selectedReservedResults = new ArrayList<>();
        }
        Reserved selected = reservedResults.get(position);
        if (selectedReservedResults.contains(selected)) {
            selectedReservedResults.remove(selected);
        } else {
            selectedReservedResults.add(selected);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedReservedResults != null ? selectedReservedResults.size() : 0;
    }

    public List<Reserved> getSelected() {
        return selectedReservedResults;
    }

    public void selectAll() {
        selectedReservedResults.clear();
        selectedReservedResults.addAll(reservedResults);
        notifyDataSetChanged();
    }

    public void setEnableChild(boolean enableChild) {
        this.enableChild = enableChild;
    }

    public class ReservedViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final ReservedAdapter adapter;
        private final View bgView;
        private final TextView reservedText;
        private final TextView childCount;
        private final View plusView;
        private int position;

        public ReservedViewHolder(ReservedAdapter adapter, View itemView) {
            super(itemView);
            this.adapter = adapter;
            bgView = itemView.findViewById(R.id.reserved_bg);
            reservedText = (TextView) itemView.findViewById(R.id.reserved_text);
            childCount = (TextView) itemView.findViewById(R.id.reserved_child_count);
            plusView = itemView.findViewById(R.id.reserved_plus);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void update(final int position, final Reserved reserved) {
            this.position = position;
            reservedText.setText(TextConverter.toCharSequence(reservedText.getContext(), reserved.getText(), reserved.getSpans(), reservedText));
            if (adapter.enableChild) {
                if (reserved.getChildCount() > 0) {
                    childCount.setText(String.valueOf(reserved.getChildCount()));
                    childCount.setVisibility(View.VISIBLE);
                    plusView.setVisibility(View.GONE);
                } else {
                    childCount.setVisibility(View.GONE);
                    plusView.setVisibility(View.VISIBLE);
                }
            } else {
                childCount.setVisibility(View.GONE);
                plusView.setVisibility(View.GONE);
            }

            if (adapter.isSelectedMode() && adapter.selectedReservedResults.contains(reserved)) {
                bgView.setBackgroundResource(R.drawable.selected_rect);
            } else {
                bgView.setBackgroundColor(bgView.getContext().getResources().getColor(android.R.color.transparent));
            }
        }

        @Override
        public void onClick(View v) {
            if (adapter.enableChild) {
                EventBus.postOnMainThread(new ListItemClickEvent(position, false));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (adapter.isSelectedMode()) {
                return false;
            }
            EventBus.postOnMainThread(new ListItemClickEvent(position, true));
            return true;
        }
    }
}
