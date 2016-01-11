package com.giocode.thememoawakens.activity.memo;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.activity.actionmode.BaseActionModeCallback;
import com.giocode.thememoawakens.activity.event.ListItemClickEvent;
import com.giocode.thememoawakens.activity.reserved.ReservedActivity;
import com.giocode.thememoawakens.bo.MemoBo;
import com.giocode.thememoawakens.bo.ReservedBo;
import com.giocode.thememoawakens.eventbus.EventBus;
import com.giocode.thememoawakens.model.Memo;
import com.giocode.thememoawakens.model.Reserved;
import com.giocode.thememoawakens.util.ColorUtils;
import com.giocode.thememoawakens.util.TextConverter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MemoActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, PopupMenu.OnDismissListener {

    private Toolbar toolbar;
    private EditText editText;
    private Realm realm;
    private MemoAdapter adapter;
    private RecyclerView listView;
    private MemoBo memoBo;
    private ReservedBo reservedBo;
    private RealmResults<Memo> memoResults;
    private RealmChangeListener memoResultsChangeListener;
    private boolean shouldScrollToBottom;

    private RealmResults<Reserved> reservedResults;
    private RealmResults<Reserved> currentReservedResults;
    private RealmChangeListener reservedResultsChangeListener;
    private boolean reservedItemClicked;
    private ActionMode actionMode;
    private View tagButtonView;
    private View bottomToolbar;
    private ArrayList<String> parentHtmlTexts = new ArrayList<>();
    private Reserved currentSelected;
    private int tagOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (RecyclerView) findViewById(R.id.memo_list);
        editText = (EditText) findViewById(R.id.memo_input_edit);
        adapter = new MemoAdapter();
        listView.setAdapter(adapter);
//        LinearLayoutManager layoutManager = (LinearLayoutManager) listView.getLayoutManager();
//        layoutManager.setReverseLayout(true);
        bottomToolbar = findViewById(R.id.memo_toolbar);

        realm = Realm.getDefaultInstance();
        memoBo = new MemoBo(realm);
        reservedBo = new ReservedBo(realm);

        updateMemoResults(memoBo.load());
        updateReservedResult(reservedBo.load(0));
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
        closeMemoResults();
        realm.close();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        reservedItemClicked = true;
        int itemId = item.getItemId();
        if (itemId < currentReservedResults.size()) {
            if (currentSelected != null) {
                parentHtmlTexts.add(currentSelected.getHtmlText());
            }
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            if (tagOrder > 0) {
                ssb.append(ColorUtils.getTagSpannableStringBuilder(this, editText, ColorUtils.DELIMITER_START_ID));
            }

            currentSelected = currentReservedResults.get(itemId);
            tagOrder++;
            CharSequence charSequence = TextConverter.toCharSequence(currentSelected.getHtmlText(), editText);
            ssb.append(charSequence);
            editText.getText().insert(editText.getSelectionStart(), ssb);
            currentReservedResults = reservedBo.load(currentReservedResults.get(itemId).getId());
            if (reservedResultsChangeListener != null) {
                currentReservedResults.removeChangeListener(reservedResultsChangeListener);
            }
            if (reservedResultsChangeListener == null) {
                reservedResultsChangeListener = new RealmChangeListener() {
                    @Override
                    public void onChange() {
                        if (currentReservedResults.size() > 0) {
                            showPopupMenu(tagButtonView);
                        } else {
                            clearTagLink();
                        }
                    }
                };
            }
            currentReservedResults.addChangeListener(reservedResultsChangeListener);
        } else {
            startActivity(ReservedActivity.createIntent(MemoActivity.this, tagOrder, currentSelected, parentHtmlTexts));
        }
        return true;
    }

    @Override
    public void onDismiss(PopupMenu menu) {
        clearTagLink();
    }

    private void clearTagLink() {
        if (!reservedItemClicked) {
            if (reservedResultsChangeListener != null) {
                currentReservedResults.removeChangeListener(reservedResultsChangeListener);
            }
            currentReservedResults = reservedResults;
            currentSelected = null;
            parentHtmlTexts.clear();
            tagOrder = 0;
        }
        reservedItemClicked = false;
    }

    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                bottomToolbar.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                bottomToolbar.setVisibility(View.VISIBLE);
                updateMemoResults(memoBo.load());
                return true;
            }
        });
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                updateMemoResults(memoBo.search(s));
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_wipe_all_memos) {
            memoBo.clearAll();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickTagButton(View view) {
        tagButtonView = view;
        if (reservedResults.size() == 0) {
            startActivity(ReservedActivity.createIntent(MemoActivity.this, 0));
            return;
        } else {
            showPopupMenu(tagButtonView);
        }
    }

    public void onClickSaveButton(View view) {
        shouldScrollToBottom = true;
        memoBo.add(TextConverter.toHtmlString(editText.getText()), System.currentTimeMillis());
        editText.setText(null);
    }

    @Subscribe
    public void onMemoItemClickEvent(ListItemClickEvent event) {
        if (event.isLongClick()) {
            adapter.setSelectedMode(true, event.getPosition());
            actionMode = toolbar.startActionMode(new BaseActionModeCallback(this, R.menu.menu_onselected) {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    bottomToolbar.setVisibility(View.GONE);
                    return super.onCreateActionMode(mode, menu);
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.action_delete) {
                        memoBo.delete(adapter.getSelectedMemos());
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
                    bottomToolbar.setVisibility(View.VISIBLE);
                }
            });
        } else {
            if (adapter.isSelectedMode()) {
                adapter.toggleSelectItem(event.getPosition());
            }
        }

        updateActionModeTitle();
    }

    private void updateActionModeTitle() {
        if (actionMode != null) {
            actionMode.setTitle(String.valueOf(adapter.getSelectedCount()));
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(MemoActivity.this);
        popupMenu.setOnDismissListener(MemoActivity.this);
        Menu menu = popupMenu.getMenu();
        int itemId = 0;
        for (Reserved reserved : currentReservedResults) {
            menu.addSubMenu(0, itemId++, 0, TextConverter.toCharSequence(reserved.getHtmlText(), null));
        }
        menu.addSubMenu(0, itemId++, 0, R.string.add);
        popupMenu.show();
    }

    private void updateMemoResults(final RealmResults<Memo> memoResults) {
        closeMemoResults();

        this.memoResults = memoResults;
        adapter.updateMemoResults(memoResults);

        if (memoResultsChangeListener == null) {
            memoResultsChangeListener = new RealmChangeListener() {
                @Override
                public void onChange() {
                    adapter.notifyDataSetChanged();
                    if (shouldScrollToBottom && memoResults.size() > 0) {
                        listView.smoothScrollToPosition(memoResults.size() - 1);
                    }
                    shouldScrollToBottom = false;
                }
            };
        }

        if (memoResults != null && memoResultsChangeListener != null) {
            memoResults.addChangeListener(memoResultsChangeListener);
        }

    }

    private void updateReservedResult(RealmResults<Reserved> reservedResults) {
        this.reservedResults = reservedResults;
        this.currentReservedResults = reservedResults;
    }

    private void closeMemoResults() {
        if (memoResults != null && memoResultsChangeListener != null) {
            memoResults.removeChangeListener(memoResultsChangeListener);
        }
        if (currentReservedResults != null && reservedResultsChangeListener != null) {
            currentReservedResults.removeChangeListener(reservedResultsChangeListener);
        }
    }
}
