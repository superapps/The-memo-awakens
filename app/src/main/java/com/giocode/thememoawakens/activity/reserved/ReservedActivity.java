package com.giocode.thememoawakens.activity.reserved;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.activity.actionmode.BaseActionModeCallback;
import com.giocode.thememoawakens.activity.event.ListItemClickEvent;
import com.giocode.thememoawakens.bo.ReservedBo;
import com.giocode.thememoawakens.eventbus.EventBus;
import com.giocode.thememoawakens.model.Reserved;
import com.giocode.thememoawakens.util.ColorUtils;
import com.giocode.thememoawakens.util.TextConverter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ReservedActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


    private static final String EXTRA_TAG_ORDER = "tagOrder";
    private static final String EXTRA_PARENT_ID = "parentId";
    private static final String EXTRA_TAG_COLOR_INDEX = "tagColorIndex";
    private static final String EXTRA_PARENT_TEXTS = "parentTexts";
    private static final String EXTRA_ROOT_TAG_COLOR_ID = "rootTagColorId";

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
    private ImageButton tagButton;
    private Toolbar toolbar;
    private ActionMode actionMode;
    private ArrayList<String> parentHtmlTexts;
    private int tagOrder;
    private int rootColorTagId;

    private static int[] ICON_RES_IDS = {
            R.drawable.ic_looks_one_white_24dp,
            R.drawable.ic_looks_two_white_24dp,
            R.drawable.ic_looks_3_white_24dp,
            R.drawable.ic_looks_4_white_24dp,
            R.drawable.ic_looks_5_white_24dp,
            R.drawable.ic_looks_6_white_24dp,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (RecyclerView) findViewById(R.id.reserved_list);
        editText = (EditText) findViewById(R.id.bottom_input_edit);
        adapter = new ReservedAdapter();
        listView.setAdapter(adapter);
        tagButton = (ImageButton) findViewById(R.id.bottom_tag);

        realm = Realm.getDefaultInstance();
        reservedBo = new ReservedBo(realm);

        parseIntent(getIntent());
        adapter.setEnableChild(tagOrder < ICON_RES_IDS.length - 1);
        updateUI();
        updateReservedResults(reservedBo.load(parentId));
    }

    private void updateUI() {
        toolbar.setTitle(R.string.tag_title);
        toolbar.setNavigationIcon(ICON_RES_IDS[tagOrder]);
        final TextView tagTextView = (TextView) findViewById(R.id.tag_text);
        if (tagOrder > 0 && parentHtmlTexts != null && !parentHtmlTexts.isEmpty()) {
            tagButton.setVisibility(View.GONE);
            tagTextView.setVisibility(View.VISIBLE);
//            SpannableStringBuilder ssb = new SpannableStringBuilder();
//            for (String htmlText : parentHtmlTexts) {
//                ssb.append(TextConverter.toCharSequence(this, htmlText, tagTextView));
//                ssb.append(ColorUtils.getTagSpannableStringBuilder(this, tagTextView, ColorUtils.DELIMITER_START_ID + rootColorTagId));
//            }
//            tagTextView.setText(ssb);
        } else {
            colorIndex = ColorUtils.getRandomIndex();
            tagButton.setVisibility(View.VISIBLE);
            tagTextView.setVisibility(View.GONE);
            tagButton.setImageDrawable(ColorUtils.getTagDrawable(this, null, colorIndex));
        }
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, tagButton);
        popupMenu.setOnMenuItemClickListener(this);
        Menu menu = popupMenu.getMenu();

        int itemId = 0;
        for (int i = 0; i < ColorUtils.COLORS_SIZE; i++) {
            menu.addSubMenu(0, itemId++, 0, ColorUtils.getTagSpannableStringBuilder(this, null, i));
        }
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        colorIndex = item.getItemId();
        tagButton.setImageDrawable(ColorUtils.getTagDrawable(this, null, colorIndex));
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getInstance().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeReservedResults();
        realm.close();
    }

    public void onClickTagButton(View view) {
        showPopupMenu();
    }

    public void onClickSaveButton(View view) {
        shouldScrollToBottom = true;
        if (tagButton.getVisibility() == View.VISIBLE) {
            SpannableStringBuilder ssb = ColorUtils.getTagSpannableStringBuilder(this, editText, colorIndex);
            ssb.append(editText.getText());
            reservedBo.add(parentId, TextConverter.toHtmlString(ssb));
        } else {
            reservedBo.add(parentId, TextConverter.toHtmlString(editText.getText()));
        }
        editText.setText(null);
    }

    @Subscribe
    public void onItemClickEvent(ListItemClickEvent event) {
        if (adapter.isSelectedMode()) {
            if (!event.isLongClick()) {
                adapter.toggleSelectItem(event.getPosition());
            }
        } else {
            if (!event.isLongClick()) {
                if (tagOrder == 0) {
                    Reserved reserved = reservedResults.get(event.getPosition());
                    Spannable spannable = (Spannable)TextConverter.toCharSequence(this, reserved.getText(), reserved.getSpans(), null);
                    rootColorTagId = ColorUtils.getTagColorId(this, spannable);
                }
                startActivity(ReservedActivity.createIntent(this, tagOrder + 1, reservedResults.get(event.getPosition()), parentHtmlTexts, rootColorTagId));
            } else {
                adapter.setSelectedMode(true, event.getPosition());
                actionMode = toolbar.startActionMode(new BaseActionModeCallback(this, R.menu.menu_onselected) {
                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_delete) {
                            reservedBo.delete(adapter.getSelected());
                            adapter.setSelectedMode(false, 0);
                            actionMode.finish();
                            return true;
                        } else if (id == R.id.action_select_all) {
                            adapter.selectAll();
                            updateActionModeTitle();
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        super.onDestroyActionMode(mode);
                        actionMode = null;
                        adapter.setSelectedMode(false, 0);
                    }

                });
            }
        }

        updateActionModeTitle();
    }

    private void updateActionModeTitle() {
        if (actionMode != null) {
            actionMode.setTitle(String.valueOf(adapter.getSelectedCount()));
        }
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
        tagOrder = intent.getIntExtra(EXTRA_TAG_ORDER, 0);
        if (tagOrder > 0) {
            colorIndex = intent.getIntExtra(EXTRA_TAG_COLOR_INDEX, 0);
        }
        parentHtmlTexts = intent.getStringArrayListExtra(EXTRA_PARENT_TEXTS);
        rootColorTagId = intent.getIntExtra(EXTRA_ROOT_TAG_COLOR_ID, 0);
    }

    public static Intent createIntent(final Context context, final int tagOrder, final Reserved reserved,
                                      final ArrayList<String> parentHtmlTexts, final int rootTagColorId) {
        Intent intent = new Intent(context, ReservedActivity.class);
        intent.putExtra(EXTRA_TAG_ORDER, tagOrder);
        intent.putExtra(EXTRA_ROOT_TAG_COLOR_ID, rootTagColorId);
        if (reserved != null) {
            intent.putExtra(EXTRA_PARENT_ID, reserved.getId());
            ArrayList<String> texts;
            if (parentHtmlTexts != null) {
                texts = new ArrayList<>(parentHtmlTexts);
            } else {
                texts = new ArrayList<>();
            }
            texts.add(reserved.getText());
            intent.putStringArrayListExtra(EXTRA_PARENT_TEXTS, texts);
        }
        return intent;
    }

    public static Intent createIntent(final Context context, final long parentId) {
        Intent intent = new Intent(context, ReservedActivity.class);
        intent.putExtra(EXTRA_PARENT_ID, parentId);
        return intent;
    }
}
