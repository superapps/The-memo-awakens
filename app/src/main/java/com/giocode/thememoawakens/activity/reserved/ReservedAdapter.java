package com.giocode.thememoawakens.activity.reserved;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.model.Reserved;

import java.util.List;

import io.realm.RealmResults;

public class ReservedAdapter extends RecyclerView.Adapter<ReservedAdapter.ReservedViewHolder> {

    List<Reserved> reservedResults;

    @Override
    public ReservedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReservedViewHolder(LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.reserved_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ReservedViewHolder holder, int position) {

        holder.update(reservedResults.get(position));
    }

    @Override
    public int getItemCount() {
        return reservedResults.size();
    }

    public void updateMemoResults(RealmResults<Reserved> reservedResults) {
        this.reservedResults = reservedResults;
    }


    public class ReservedViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView reservedText;
        private final TextView childCount;
        private Reserved reserved;

        public ReservedViewHolder(View itemView) {
            super(itemView);
            reservedText = (TextView) itemView.findViewById(R.id.reserved_text);
            childCount = (TextView) itemView.findViewById(R.id.reserved_child_count);
            itemView.setOnClickListener(this);
        }

        public void update(Reserved reserved) {
            this.reserved = reserved;
            reservedText.setText(reserved.getText());
            childCount.setText(reserved.getChildCount() > 0 ? String.valueOf(reserved.getChildCount()) : "+");
        }

        @Override
        public void onClick(View v) {
            v.getContext().startActivity(ReservedActivity.createIntent(v.getContext(), reserved));
        }
    }
}
