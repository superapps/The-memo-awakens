package com.giocode.thememoawakens.activity.memo;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.activity.event.ListItemClickEvent;
import com.giocode.thememoawakens.eventbus.EventBus;
import com.giocode.thememoawakens.model.Memo;
import com.giocode.thememoawakens.util.DisplayUtils;
import com.giocode.thememoawakens.util.TextConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {

    private List<Memo> memos;
    private List<Memo> selectedMemos;
    private boolean selectedMode;

    @Override
    public MemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemoViewHolder(this, LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.memo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MemoViewHolder holder, int position) {
        Memo previousMemo = null;
        if (position >= 1) {
            previousMemo = memos.get(position - 1);
        }
        holder.update(position, memos.get(position), previousMemo);
    }

    @Override
    public int getItemCount() {
        return this.memos != null ? this.memos.size() : 0;
    }

    public void updateMemoResults(List<Memo> memos) {
        this.memos = memos;
    }

    public void setSelectedMode(boolean selectedMode, int initialSelectPosition) {
        this.selectedMode = selectedMode;
        if (selectedMode) {
            toggleSelectItem(initialSelectPosition);
        } else {
            this.selectedMemos = null;
        }
        notifyDataSetChanged();
    }

    public boolean isSelectedMode() {
        return selectedMode;
    }

    public int getSelectedCount() {
        return selectedMemos != null ? selectedMemos.size() : 0;
    }

    public List<Memo> getSelectedMemos() {
        return selectedMemos;
    }

    public void toggleSelectItem(int position) {
        if (this.selectedMemos == null) {
            this.selectedMemos = new ArrayList<>();
        }
        Memo selectedMemo = memos.get(position);
        if (this.selectedMemos.contains(selectedMemo)) {
            this.selectedMemos.remove(selectedMemo);
        } else {
            this.selectedMemos.add(selectedMemo);
        }
        notifyDataSetChanged();
    }

    public void selectAll() {
        this.selectedMemos = memos;
        notifyDataSetChanged();
    }

    public static class MemoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final MemoAdapter adapter;
        private final View memoBg;
        private final TextView dateText;
        private final TextView memoText;
        private final TextView timeText;
        private int position;


        public MemoViewHolder(MemoAdapter adapter, View itemView) {
            super(itemView);
            this.adapter = adapter;
            this.memoBg = itemView.findViewById(R.id.memo_bg);
            this.dateText = (TextView) itemView.findViewById(R.id.memo_date);
            this.memoText = (TextView) itemView.findViewById(R.id.memo_text);
            this.timeText = (TextView) itemView.findViewById(R.id.memo_time);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void update(final int position, @NonNull final Memo memo, @Nullable final Memo previousMemo) {
            this.position = position;
            memoText.setText(TextConverter.toCharSequence(memo.getHtmlText(), memoText));
            String timeString = DateUtils.formatDateTime(timeText.getContext(), memo.getTime(), DateUtils.FORMAT_SHOW_TIME);
            timeText.setText(timeString);

            String dateString = null;
            if (previousMemo == null
                    || (previousMemo != null && !isSameDay(memo.getTime(), previousMemo.getTime()))) {
                dateString = DateUtils.formatDateTime(dateText.getContext(), memo.getTime(), DateUtils.FORMAT_SHOW_DATE);
            }
            if (!TextUtils.isEmpty(dateString)) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(dateString);
            } else {
                dateText.setVisibility(View.GONE);
            }

            if (adapter.isSelectedMode() && adapter.selectedMemos.contains(memo)) {
                memoBg.setBackgroundResource(R.drawable.selected_rect);
            } else {
                memoBg.setBackgroundColor(memoBg.getContext().getResources().getColor(android.R.color.transparent));
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return;
            }
            float[] measuredText = new float[1];
            int displayWidth = DisplayUtils.getDisplayWidth(memoText.getContext());
            int marginWidth = DisplayUtils.toPixel(memoText.getContext(), 14);
            int extraMargin = DisplayUtils.toPixel(memoText.getContext(), 38);

            memoText.getPaint().breakText(TextConverter.toCharSequence(memo.getHtmlText(), memoText), 0, memoText.getText().length(), false,
                    displayWidth - marginWidth
                    , measuredText);
            float timeWidth = timeText.getPaint().measureText(timeString);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) timeText.getLayoutParams();
            if (measuredText[0] < (displayWidth - timeWidth - marginWidth - extraMargin)) {
                params.addRule(RelativeLayout.ALIGN_BOTTOM, memoText.getId());
                params.removeRule(RelativeLayout.BELOW);
            } else {
                params.addRule(RelativeLayout.BELOW, memoText.getId());
                params.removeRule(RelativeLayout.ALIGN_BOTTOM);
            }
        }

        private boolean isSameDay(long time1, long time2) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(new Date(time1));
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(new Date(time2));

            return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
        }

        @Override
        public void onClick(View v) {
            EventBus.postOnMainThread(new ListItemClickEvent(position, false));
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
