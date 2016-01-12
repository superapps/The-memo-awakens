package com.giocode.thememoawakens.bo;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import com.giocode.thememoawakens.BuildConfig;
import com.giocode.thememoawakens.dao.MemoDao;
import com.giocode.thememoawakens.model.Memo;
import com.giocode.thememoawakens.model.Span;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MemoBo {

    @NonNull
    private final Realm realm;
    @NonNull
    private final MemoDao dao = new MemoDao();


    public MemoBo(Realm realm) {
        this.realm = realm;
    }

    public void add(final Pair<String, RealmList<Span>> memoInfo, final long time) {
        if (memoInfo == null || TextUtils.isEmpty(memoInfo.first)) {
            return;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                dao.insert(realm, memoInfo.first, memoInfo.second, time);
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
                dao.delete(realm, selectedMemos);
            }
        });


    }

    public RealmResults<Memo> search(String query) {
        RealmResults<Memo> results = dao.search(realm, query);
        return results;
    }

    public void update(final long memoId, final Pair<String, RealmList<Span>> memoInfo
            , final long memoTime, final Realm.Transaction.Callback callback) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Memo memo = new Memo();
                memo.setId(memoId);
                memo.setText(memoInfo.first);
                memo.setSpans(memoInfo.second);
                memo.setTime(memoTime);
                dao.insertOrUpdate(realm, memo);
            }
        }, callback);
    }
}
