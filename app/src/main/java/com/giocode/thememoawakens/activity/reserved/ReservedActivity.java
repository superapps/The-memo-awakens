package com.giocode.thememoawakens.activity.reserved;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.bo.ReservedBo;
import com.giocode.thememoawakens.model.Reserved;
import com.giocode.thememoawakens.util.ColorUtils;
import com.giocode.thememoawakens.util.MemoTextConverter;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ReservedActivity extends AppCompatActivity {

    private static final String EXTRA_PARENT_ID = "parentId";
    private static final String EXTRA_PARENT_RESERVED_TEXT = "parentReservedText";

    private EditText editText;
    private Realm realm;
    private ReservedAdapter adapter;
    private RecyclerView listView;
    private ReservedBo reservedBo;

    private RealmResults<Reserved> reservedResults;
    private RealmChangeListener reservedResultsChangeListener;
    private boolean shouldScrollToBottom = true;
    private long parentId;
    private int colorIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (RecyclerView) findViewById(R.id.reserved_list);
        editText = (EditText) findViewById(R.id.reserved_input_edit);
        adapter = new ReservedAdapter();
        listView.setAdapter(adapter);

        realm = Realm.getDefaultInstance();
        reservedBo = new ReservedBo(realm);
        colorIndex = ColorUtils.getRandomIndex();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                BackgroundColorSpan[] spans = s.getSpans(0, s.length(), BackgroundColorSpan.class);
                for (BackgroundColorSpan backgroundColorSpan : spans) {
                    s.removeSpan(backgroundColorSpan);
                }
                s.setSpan(new BackgroundColorSpan(ColorUtils.getColor(ReservedActivity.this, colorIndex)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        });

        FloatingActionButton fabPlus = (FloatingActionButton) findViewById(R.id.fab_plus);
        FloatingActionButton fabColor = (FloatingActionButton) findViewById(R.id.fab_color);
        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldScrollToBottom = true;
                reservedBo.add(parentId, MemoTextConverter.toHtmlString(editText.getText()));
                editText.setText(null);
            }
        });
        fabColor.setBackgroundTintList(ColorUtils.getColorStateList(this, colorIndex));
        fabColor.setRippleColor(ColorUtils.getPressedColor(this, colorIndex));
        fabColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        parseIntent(getIntent());
        updateReservedResults(reservedBo.load(parentId));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeReservedResults();
        realm.close();
    }

    private void updateReservedResults(final RealmResults<Reserved> reservedResults) {
        closeReservedResults();

        this.reservedResults = reservedResults;
        adapter.updateMemoResults(reservedResults);
        if (reservedResultsChangeListener == null) {
            reservedResultsChangeListener = new RealmChangeListener() {
                @Override
                public void onChange() {
                    adapter.notifyDataSetChanged();
                    if (shouldScrollToBottom && reservedResults.size() > 0) {
                        listView.smoothScrollToPosition(reservedResults.size() - 1);
                    }
                    shouldScrollToBottom = false;
                }
            };
        }

        if (reservedResults != null && reservedResultsChangeListener != null) {
            reservedResults.addChangeListener(reservedResultsChangeListener);
        }

    }

    private void closeReservedResults() {
        if (reservedResults != null && reservedResultsChangeListener != null) {
            reservedResults.removeChangeListener(reservedResultsChangeListener);
        }
    }

    private void parseIntent(final Intent intent) {
        parentId = intent.getLongExtra(EXTRA_PARENT_ID, 0);
    }

    public static Intent createIntent(final Context context, final Reserved parentReserved) {
        Intent intent = new Intent(context, ReservedActivity.class);
        if (parentReserved != null) {
            intent.putExtra(EXTRA_PARENT_ID, parentReserved.getId());
            intent.putExtra(EXTRA_PARENT_RESERVED_TEXT, parentReserved.getText());
        }
        return intent;
    }

    public static Intent createIntent(final Context context, final long parentId) {
        Intent intent = new Intent(context, ReservedActivity.class);
        intent.putExtra(EXTRA_PARENT_ID, parentId);
        return intent;
    }

}
