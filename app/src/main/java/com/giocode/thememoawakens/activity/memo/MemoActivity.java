package com.giocode.thememoawakens.activity.memo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.activity.register.RegisterActivity;
import com.giocode.thememoawakens.bo.MemoBo;
import com.giocode.thememoawakens.model.Memo;
import com.giocode.thememoawakens.util.MemoTextConverter;

import io.realm.Realm;
import io.realm.RealmResults;

public class MemoActivity extends AppCompatActivity {

    private EditText editText;
    private Realm realm;
    private MemoAdapter adapter;
    private RecyclerView listView;
    private MemoBo memoBo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (RecyclerView) findViewById(R.id.memo_list);
        editText = (EditText) findViewById(R.id.memo_input_edit);
        adapter = new MemoAdapter();
        listView.setAdapter(adapter);

        realm = Realm.getDefaultInstance();
        memoBo = new MemoBo(realm, new MemoEventHandler(listView, adapter));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText())) {
                    startActivity(RegisterActivity.createIntent(MemoActivity.this));
                    return;
                }

                memoBo.saveAndReload(MemoTextConverter.toHtmlString(editText.getText()), System.currentTimeMillis());
                editText.setText(null);
            }
        });

        memoBo.load();
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
            memoBo.wipeAllAndReload();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class MemoEventHandler extends Handler {

        private final RecyclerView listView;
        private final MemoAdapter adapter;

        private MemoEventHandler(RecyclerView listView, MemoAdapter adapter) {
            this.listView = listView;
            this.adapter = adapter;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MemoBo.EVENT_LOAD_COMPLETED:
                    RealmResults<Memo> memos = (RealmResults<Memo>) msg.obj;
                    this.adapter.setMemos(memos);
                    if (memos != null && memos.size() > 0) {
                        listView.smoothScrollToPosition(memos.size() - 1);
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
