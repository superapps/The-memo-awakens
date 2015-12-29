package com.giocode.thememoawakens.activity.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.activity.memo.MemoActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabPlus = (FloatingActionButton) findViewById(R.id.fab_plus);
        FloatingActionButton fabColor = (FloatingActionButton) findViewById(R.id.fab_color);
        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        fabColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public static Intent createIntent(final Context context) {
        return new Intent(context, RegisterActivity.class);
    }
}
