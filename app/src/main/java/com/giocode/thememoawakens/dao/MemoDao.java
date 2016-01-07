package com.giocode.thememoawakens.dao;

import android.util.Log;

import com.giocode.thememoawakens.BuildConfig;
import com.giocode.thememoawakens.model.Memo;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MemoDao {

    public void insert(final Realm realm, final String memoText, final long time) {
        long curId = 0;
        Number maxId = realm.where(Memo.class).max("id");
        if (maxId != null) {
            curId = maxId.longValue() + 1;
        }

        Memo memo = new Memo();
        memo.setId(curId);
        memo.setHtmlText(memoText);
        memo.setTime(time);
        insertOrUpdate(realm, memo);
    }

    public void insertOrUpdate(final Realm realm, final Memo memo) {
        realm.copyToRealmOrUpdate(memo);
    }

    public RealmResults<Memo> getAsync(final Realm realm) {
        final RealmResults<Memo> realmResults = realm.where(Memo.class)
                .findAllSortedAsync("time", Sort.ASCENDING);
        return realmResults;
    }

    public void clearAll(final Realm realm) {
        final RealmResults<Memo> realmResults = realm.where(Memo.class)
                .findAll();
        realmResults.clear();
    }

    public void delete(List<Memo> selectedMemos) {
        for (Memo memo : selectedMemos) {
            memo.removeFromRealm();
        }
    }
}
