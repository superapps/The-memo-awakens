package com.giocode.thememoawakens.bo;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.giocode.thememoawakens.dao.MemoDao;
import com.giocode.thememoawakens.model.Memo;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MemoBo {

    @NonNull
    private final Realm realm;
    @NonNull
    private final MemoDao dao = new MemoDao();


    public MemoBo(Realm realm) {
        this.realm = realm;
    }

    public void add(final String htmlText, final long time) {
        if (TextUtils.isEmpty(htmlText)) {
            return;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                dao.insert(realm, htmlText, time);
            }
        });
    }

    public void clearAll() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                dao.clearAll(realm);
            }
        });
    }

    public RealmResults<Memo> load() {
        return dao.getAsync(realm);
    }

    public void delete(final List<Memo> selectedMemos) {
        if (selectedMemos == null || selectedMemos.isEmpty()) {
            return;
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                dao.delete(selectedMemos);
            }
        });


    }
}
