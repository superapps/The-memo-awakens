package com.giocode.thememoawakens.activity.memo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.giocode.thememoawakens.BuildConfig;
import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.model.Memo;
import com.giocode.thememoawakens.util.MemoTextConverter;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MemoActivity extends AppCompatActivity {

    private EditText editText;
    private Realm realm;
    private MemoAdapter adapter;
    private RecyclerView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = (EditText) findViewById(R.id.memo_input_edit);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText())) {
                    return;
                }

                saveAndReload(MemoTextConverter.toHtmlString(editText.getText()), System.currentTimeMillis());
                editText.setText(null);
            }
        });

        listView = (RecyclerView) findViewById(R.id.memo_list);
        adapter = new MemoAdapter();
        listView.setAdapter(adapter);
        realm = Realm.getDefaultInstance();
        load();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_wipe_all_memos) {
            wipeAllAndReload();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveAndReload(final String htmlText, final long time) {
        if (TextUtils.isEmpty(htmlText)) {
            return;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (BuildConfig.DEBUG) {
                    Log.d("gio.memo", "save.execute");
                }
                Memo memo = new Memo();
                memo.setHtmlText(htmlText);
                memo.setTime(time);
                realm.copyToRealm(memo);
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                load();
            }

            @Override
            public void onError(Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.d("gio.memo", "save.onError", e);
                }
            }
        });
    }

    private void wipeAllAndReload() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmResults<Memo> realmResults = realm.where(Memo.class)
                        .findAll();
                realmResults.clear();
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                load();
            }
        });
    }

    private void load() {
        if (BuildConfig.DEBUG) {
            Log.d("gio.memo", "load.start");
        }
        final RealmResults<Memo> realmResults = realm.where(Memo.class)
                .findAllSortedAsync("time", Sort.ASCENDING);
        realmResults.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                if (BuildConfig.DEBUG) {
                    Log.d("gio.memo", "load.onChange");
                }
                adapter.setMemos(realmResults);
                if (realmResults != null && realmResults.size() > 0) {
                    listView.smoothScrollToPosition(realmResults.size() - 1);
                }
            }
        });
    }
}
