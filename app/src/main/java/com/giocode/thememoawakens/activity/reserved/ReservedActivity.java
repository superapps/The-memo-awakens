package com.giocode.thememoawakens.activity.reserved;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.bo.ReservedBo;
import com.giocode.thememoawakens.model.Reserved;
import com.giocode.thememoawakens.util.ColorUtils;
import com.giocode.thememoawakens.util.TextConverter;

import org.w3c.dom.Text;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ReservedActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

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
    private FloatingActionButton colorChangeButton;

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
                TextConverter.changeBgColor(s, ColorUtils.getColor(ReservedActivity.this, colorIndex));
            }
        });

        FloatingActionButton fabPlus = (FloatingActionButton) findViewById(R.id.fab_plus);
        colorChangeButton = (FloatingActionButton) findViewById(R.id.fab_color);
        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldScrollToBottom = true;
                reservedBo.add(parentId, TextConverter.toHtmlString(editText.getText()));
                editText.setText(null);
            }
        });
        colorChangeButton.setBackgroundTintList(ColorUtils.getColorStateList(this, colorIndex));
        colorChangeButton.setRippleColor(ColorUtils.getPressedColor(this, colorIndex));
        colorChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });
        parseIntent(getIntent());
        updateReservedResults(reservedBo.load(parentId));
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, colorChangeButton);
        popupMenu.setOnMenuItemClickListener(this);
        Menu menu = popupMenu.getMenu();
        String currentText = editText.getText().length() > 0 ? editText.getText().toString() : getString(R.string.app_name);

        int itemId = 0;
        for (int i = 0; i < ColorUtils.COLORS_SIZE; i++) {
            Spannable spannable = new SpannableStringBuilder(currentText);
            TextConverter.changeBgColor(spannable, ColorUtils.getColor(this, i));
            menu.addSubMenu(0, itemId++, 0, spannable);
        }
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        colorIndex = item.getItemId();
        colorChangeButton.setBackgroundTintList(ColorUtils.getColorStateList(this, colorIndex));
        colorChangeButton.setRippleColor(ColorUtils.getPressedColor(this, colorIndex));

        TextConverter.changeBgColor(editText.getText(), ColorUtils.getColor(this, colorIndex));
        return true;
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
