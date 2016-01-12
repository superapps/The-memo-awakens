package com.giocode.thememoawakens.activity.memo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.activity.TagPopupMenuHelper;
import com.giocode.thememoawakens.activity.reserved.ReservedActivity;
import com.giocode.thememoawakens.bo.MemoBo;
import com.giocode.thememoawakens.util.TextConverter;

import io.realm.Realm;

public class EditMemoActivity extends AppCompatActivity {

    private static final String EXTRA_MEMO_ID = "memoId";
    private static final String EXTRA_HTMLTEXT = "htmlText";
    private static final String EXTRA_TIME = "time";

    private EditText editText;
    private long memoId;
    private long memoTime;
    private MemoBo memoBo;
    private Realm realm;
    private TagPopupMenuHelper tagPopupMenuHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = (EditText) findViewById(R.id.memo_edit);
        String htmlText = getIntent().getStringExtra(EXTRA_HTMLTEXT);
        editText.setText(TextConverter.toCharSequence(htmlText, editText));
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setCursorVisible(true);
            }
        });
        memoTime = getIntent().getLongExtra(EXTRA_TIME, 0);
        memoId = getIntent().getLongExtra(EXTRA_MEMO_ID, 0);
        TextView timeView = (TextView) findViewById(R.id.memo_time);
        String timeString = DateUtils.formatDateTime(this, memoTime, DateUtils.FORMAT_SHOW_TIME);
        timeView.setText(timeString);
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        realm = Realm.getDefaultInstance();
        memoBo = new MemoBo(realm);
        tagPopupMenuHelper = new TagPopupMenuHelper(this, realm, editText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tagPopupMenuHelper.close();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        editText.setCursorVisible(false);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickSaveButton(View view) {
        memoBo.update(memoId, TextConverter.toTextSpanInfo(editText.getText()), memoTime
                , new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                finish();
            }
        });
    }

    public void onClickTagButton(View view) {
        if (tagPopupMenuHelper.isEmptyTag()) {
            startActivity(ReservedActivity.createIntent(this, 0));
        } else {
            tagPopupMenuHelper.showPopupMenu(view);
        }
    }

    public static Intent createIntent(Context context, long memoId, String htmlText, long time) {

        return new Intent(context, EditMemoActivity.class)
                .putExtra(EXTRA_MEMO_ID, memoId)
                .putExtra(EXTRA_HTMLTEXT, htmlText)
                .putExtra(EXTRA_TIME, time);
    }

}
