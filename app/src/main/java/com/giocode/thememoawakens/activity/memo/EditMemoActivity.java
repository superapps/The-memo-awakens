package com.giocode.thememoawakens.activity.memo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.activity.TagPopupMenuHelper;
import com.giocode.thememoawakens.activity.reserved.ReservedActivity;
import com.giocode.thememoawakens.bo.MemoBo;
import com.giocode.thememoawakens.util.TextConverter;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;

public class EditMemoActivity extends AppCompatActivity {

    private static final String EXTRA_MEMO_ID = "memoId";
    private static final String EXTRA_HTMLTEXT = "htmlText";
    private static final String EXTRA_TIME = "time";

    private EditText editText;
    private long memoId;
    private MemoBo memoBo;
    private Realm realm;
    private TagPopupMenuHelper tagPopupMenuHelper;
    private Calendar memoDateTime;


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
        long memoTime = getIntent().getLongExtra(EXTRA_TIME, 0);
        memoId = getIntent().getLongExtra(EXTRA_MEMO_ID, 0);

        memoDateTime = Calendar.getInstance();
        memoDateTime.setTime(new Date(memoTime));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        updateTitle();

        realm = Realm.getDefaultInstance();
        memoBo = new MemoBo(realm);
        tagPopupMenuHelper = new TagPopupMenuHelper(this, realm, editText);
    }

    private void updateTitle() {
        String dateString = DateUtils.formatDateTime(this, memoDateTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE);
        String timeString = DateUtils.formatDateTime(this, memoDateTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
        getSupportActionBar().setTitle(dateString + " " + timeString);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chnage_time: {
                DialogFragment newFragment = new TimePickerFragment()
                        .setActivity(this)
                        .setMemoDateTime(memoDateTime);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
            break;
            case R.id.action_change_date: {
                DialogFragment newFragment = new DatePickerFragment()
                        .setActivity(this)
                        .setMemoDateTime(memoDateTime);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
            break;
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickSaveButton(View view) {
        memoBo.update(memoId, TextConverter.toTextSpanInfo(editText.getText())
                , memoDateTime.getTimeInMillis()
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

    public void onTimeSet(int hourOfDay, int minute) {
        memoDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        memoDateTime.set(Calendar.MINUTE, minute);
        updateTitle();
    }

    public void onDateSet(int year, int month, int day) {
        memoDateTime.set(Calendar.YEAR, year);
        memoDateTime.set(Calendar.MONTH, month);
        memoDateTime.set(Calendar.DAY_OF_MONTH, day);
        updateTitle();
    }

    public static Intent createIntent(Context context, long memoId, String htmlText, long time) {

        return new Intent(context, EditMemoActivity.class)
                .putExtra(EXTRA_MEMO_ID, memoId)
                .putExtra(EXTRA_HTMLTEXT, htmlText)
                .putExtra(EXTRA_TIME, time);
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private EditMemoActivity activity;
        private Calendar memoDateTime;

        public TimePickerFragment setActivity(EditMemoActivity activity) {
            this.activity = activity;
            return this;
        }

        public TimePickerFragment setMemoDateTime(Calendar memoDateTime) {
            this.memoDateTime = memoDateTime;
            return this;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hour = memoDateTime.get(Calendar.HOUR_OF_DAY);
            int minute = memoDateTime.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            activity.onTimeSet(hourOfDay, minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private EditMemoActivity activity;
        private Calendar memoDateTime;

        public DatePickerFragment setActivity(EditMemoActivity activity) {
            this.activity = activity;
            return this;
        }

        public DatePickerFragment setMemoDateTime(Calendar memoDateTime) {
            this.memoDateTime = memoDateTime;
            return this;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = memoDateTime.get(Calendar.YEAR);
            int month = memoDateTime.get(Calendar.MONTH);
            int day = memoDateTime.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            activity.onDateSet(year, month, day);
        }
    }

}
