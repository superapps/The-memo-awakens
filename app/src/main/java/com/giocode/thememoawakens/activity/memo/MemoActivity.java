package com.giocode.thememoawakens.activity.memo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.activity.memo.event.MemoItemClickEvent;
import com.giocode.thememoawakens.activity.reserved.ReservedActivity;
import com.giocode.thememoawakens.bo.MemoBo;
import com.giocode.thememoawakens.bo.ReservedBo;
import com.giocode.thememoawakens.eventbus.EventBus;
import com.giocode.thememoawakens.model.Memo;
import com.giocode.thememoawakens.model.Reserved;
import com.giocode.thememoawakens.util.TextConverter;
import com.squareup.otto.Subscribe;

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
    private FloatingActionButton addButton;
    private boolean reservedItemClicked;
    private ActionMode actionMode;


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

        realm = Realm.getDefaultInstance();
        memoBo = new MemoBo(realm);
        reservedBo = new ReservedBo(realm);

        addButton = (FloatingActionButton) findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText())) {
                    if (reservedResults.size() == 0) {
                        startActivity(ReservedActivity.createIntent(MemoActivity.this, null));
                        return;
                    } else {
                        showPopupMenu();
                    }
                }
                shouldScrollToBottom = true;
                memoBo.add(TextConverter.toHtmlString(editText.getText()), System.currentTimeMillis());
                editText.setText(null);
            }
        });

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
            editText.getText().insert(editText.getSelectionStart(),
                    new SpannableStringBuilder().append(currentReservedResults.get(itemId).getText()).append(" "));
            currentReservedResults = reservedBo.load(currentReservedResults.get(itemId).getId());
            if (reservedResultsChangeListener != null) {
                currentReservedResults.removeChangeListener(reservedResultsChangeListener);
            }
            if (reservedResultsChangeListener == null) {
                reservedResultsChangeListener = new RealmChangeListener() {
                    @Override
                    public void onChange() {
                        if (currentReservedResults.size() > 0) {
                            showPopupMenu();
                        } else {
                            if (reservedResultsChangeListener != null) {
                                currentReservedResults.removeChangeListener(reservedResultsChangeListener);
                            }
                            currentReservedResults = reservedResults;
                        }
                    }
                };
            }
            currentReservedResults.addChangeListener(reservedResultsChangeListener);
        } else {
            startActivity(ReservedActivity.createIntent(MemoActivity.this, currentReservedResults.get(0).getParentId()));
        }
        return true;
    }

    @Override
    public void onDismiss(PopupMenu menu) {
        if (!reservedItemClicked) {
            if (reservedResultsChangeListener != null) {
                currentReservedResults.removeChangeListener(reservedResultsChangeListener);
            }
            currentReservedResults = reservedResults;
        }
        reservedItemClicked = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Subscribe
    public void onMemoItemClickEvent(MemoItemClickEvent event) {
        if (event.isLongClick()) {
            adapter.setSelectedMode(true, event.getPosition());
            actionMode = toolbar.startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.menu_onselected, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
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
                        return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    actionMode = null;
                    adapter.setSelectedMode(false, 0);
                }
            });
        } else {
            if (adapter.isSelectedMode()) {
                adapter.toggleSelectItem(event.getPosition());
            }
        }

        if (actionMode != null) {
            actionMode.setTitle(String.valueOf(adapter.getSelectedCount()));
        }
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, addButton);
        popupMenu.setOnMenuItemClickListener(MemoActivity.this);
        popupMenu.setOnDismissListener(MemoActivity.this);
        Menu menu = popupMenu.getMenu();
        int itemId = 0;
        for (Reserved reserved : currentReservedResults) {
            menu.addSubMenu(0, itemId++, 0, reserved.getText());
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
