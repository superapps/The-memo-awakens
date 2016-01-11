package com.giocode.thememoawakens.activity.actionmode;

import android.app.Activity;
import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.giocode.thememoawakens.R;

public class BaseActionModeCallback implements ActionMode.Callback {

    private final Activity activity;
    private int statusBarColor;
    private final int inflateMenuResId;

    public BaseActionModeCallback(Activity activity, int inflateMenuResId) {
        this.activity = activity;
        this.inflateMenuResId = inflateMenuResId;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBarColor = activity.getWindow().getStatusBarColor();
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.bg_action_mode_status));
        }
        mode.getMenuInflater().inflate(inflateMenuResId, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(statusBarColor);
        }
    }
}
