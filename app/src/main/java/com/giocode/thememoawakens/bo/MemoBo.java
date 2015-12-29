package com.giocode.thememoawakens.bo;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.giocode.thememoawakens.dao.MemoDao;
import com.giocode.thememoawakens.model.Memo;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MemoBo {

    public static final int EVENT_LOAD_COMPLETED = 1;

    @NonNull
    private final Realm realm;
    @NonNull
    private final Handler handler;
    @NonNull
    private final MemoDao memoDao = new MemoDao();


    public MemoBo(Realm realm, Handler handler) {
        this.realm = realm;
        this.handler = handler;
    }

    public void saveAndReload(final String htmlText, final long time) {
        if (TextUtils.isEmpty(htmlText)) {
            return;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                memoDao.insert(realm, htmlText, time);
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                load();
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    public void wipeAllAndReload() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                memoDao.clearAll(realm);
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                load();
            }
        });
    }

    public void load() {
        final RealmResults<Memo> realmResults = memoDao.getAsync(realm);
        realmResults.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                handler.dispatchMessage(Message.obtain(handler, EVENT_LOAD_COMPLETED, realmResults));
            }
        });
    }

}
