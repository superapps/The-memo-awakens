package com.giocode.thememoawakens;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AMemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("amemo.realm")
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
