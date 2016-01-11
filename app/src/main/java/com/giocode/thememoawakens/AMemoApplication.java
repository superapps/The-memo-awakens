package com.giocode.thememoawakens;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AMemoApplication extends Application {

    public static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("amemo.realm")
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
