package com.giocode.thememoawakens.activity.memo;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.activity.TagPopupMenuHelper;
import com.giocode.thememoawakens.activity.actionmode.BaseActionModeCallback;
import com.giocode.thememoawakens.activity.event.ListItemClickEvent;
import com.giocode.thememoawakens.activity.reserved.ReservedActivity;
import com.giocode.thememoawakens.bo.MemoBo;
import com.giocode.thememoawakens.eventbus.EventBus;
import com.giocode.thememoawakens.model.Memo;
import com.giocode.thememoawakens.util.TextConverter;
import com.squareup.otto.Subscribe;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MemoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editText;
    private Realm realm;
    private MemoAdapter adapter;
    private RecyclerView listView;
    private MemoBo memoBo;
    private RealmResults<Memo> memoResults;
    private RealmChangeListener memoResultsChangeListener;
    private boolean shouldScrollToBottom = true;

    private ActionMode actionMode;
    private View bottomToolbar;
    private TagPopupMenuHelper tagPopupMenuHelper;

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
        tagPopupMenuHelper = new TagPopupMenuHelper(this, realm, editText);

        updateMemoResults(memoBo.load());
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
        if (tagPopupMenuHelper.isEmptyTag()) {
            startActivity(ReservedActivity.createIntent(MemoActivity.this, 0));
        } else {
            tagPopupMenuHelper.showPopupMenu(view);
        }
    }

    public void onClickSaveButton(View view) {
        shouldScrollToBottom = true;
        memoBo.add(TextConverter.toTextSpanInfo(editText.getText()), System.currentTimeMillis());
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
                    bottomToolbar.setVisibility(View.VISIBLE);
                }
            });
        } else {
            if (adapter.isSelectedMode()) {
                adapter.toggleSelectItem(event.getPosition());
            } else {
                Memo memo = memoResults.get(event.getPosition());
                View memoView = event.getItemView().findViewById(R.id.memo_text);
                View memoTimeView = event.getItemView().findViewById(R.id.memo_time);

                Pair<View, String> p1 = Pair.create(memoView, getString(R.string.transition_name_memo));
                Pair<View, String> p2 = Pair.create(memoTimeView, getString(R.string.transition_name_time));
                Pair<View, String> p3 = Pair.create(bottomToolbar, getString(R.string.transition_name_bottom));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3);
                Spannable spannable = (Spannable) TextConverter.toCharSequence(this, memo.getText(), memo.getSpans(), null);
                ActivityCompat.startActivity(this
                        , EditMemoActivity.createIntent(this, memo.getId(), TextConverter.toHtmlString(spannable), memo.getTime()), options.toBundle());
            }
        }

        updateActionModeTitle();
    }

    private void updateActionModeTitle() {
        if (actionMode != null) {
            actionMode.setTitle(String.valueOf(adapter.getSelectedCount()));
        }
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
                        listView.smoothScrollToPosition(memoResults.size());
                    }
                    shouldScrollToBottom = false;
                }
            };
        }

        if (memoResults != null && memoResultsChangeListener != null) {
            memoResults.addChangeListener(memoResultsChangeListener);
        }

    }

    private void closeMemoResults() {
        if (memoResults != null && memoResultsChangeListener != null) {
            memoResults.removeChangeListener(memoResultsChangeListener);
        }
        tagPopupMenuHelper.close();
    }
}
