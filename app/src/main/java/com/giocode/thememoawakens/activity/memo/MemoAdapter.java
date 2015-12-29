package com.giocode.thememoawakens.activity.memo;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.model.Memo;
import com.giocode.thememoawakens.util.DisplayUtils;

import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {

    private List<Memo> memos;

    @Override
    public MemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemoViewHolder(LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.memo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MemoViewHolder holder, int position) {
        holder.update(memos.get(position));
    }

    @Override
    public int getItemCount() {
        return this.memos != null ? this.memos.size() : 0;
    }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
        notifyDataSetChanged();
    }

    public static class MemoViewHolder extends RecyclerView.ViewHolder {

        private final TextView memoText;
        private final TextView timeText;

        public MemoViewHolder(View itemView) {
            super(itemView);
            this.memoText = (TextView) itemView.findViewById(R.id.memo_text);
            this.timeText = (TextView) itemView.findViewById(R.id.memo_time);
        }

        public void update(final Memo memo) {
            memoText.setText(memo.getText());
            String timeString = DateUtils.formatDateTime(timeText.getContext(), memo.getTime(), DateUtils.FORMAT_SHOW_TIME);
            timeText.setText(timeString);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return;
            }
            float[] measuredText = new float[1];
            int displayWidth = DisplayUtils.getDisplayWidth(memoText.getContext());
            int marginWidth = DisplayUtils.toPixel(memoText.getContext(), 14);

            memoText.getPaint().breakText(memo.getText(), 0, memoText.getText().length(), false,
                    displayWidth - marginWidth
                    , measuredText);
            float timeWidth = timeText.getPaint().measureText(timeString);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) timeText.getLayoutParams();
            if (measuredText[0] < (displayWidth - timeWidth - marginWidth - 100)) {
                params.addRule(RelativeLayout.ALIGN_END, memoText.getId());
                params.removeRule(RelativeLayout.BELOW);
            } else {
                params.addRule(RelativeLayout.BELOW, memoText.getId());
                params.removeRule(RelativeLayout.ALIGN_END);
            }
        }
    }
}
