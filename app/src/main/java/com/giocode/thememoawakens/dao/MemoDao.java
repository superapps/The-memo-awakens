package com.giocode.thememoawakens.dao;

import android.text.TextUtils;
import android.util.Log;

import com.giocode.thememoawakens.BuildConfig;
import com.giocode.thememoawakens.model.Memo;
import com.giocode.thememoawakens.model.Reserved;
import com.giocode.thememoawakens.model.Span;

import java.util.Iterator;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MemoDao {

    public void insert(final Realm realm, final String memoText, final RealmList<Span> spans, final long time) {
        long curId = 0;
        Number maxId = realm.where(Memo.class).max("id");
        if (maxId != null) {
            curId = maxId.longValue() + 1;
        }

        Memo memo = new Memo();
        memo.setId(curId);
        memo.setText(memoText);
        memo.setSpans(spans);
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

    public void delete(final Realm realm, List<Memo> selectedMemos) {
        RealmQuery<Memo> query = realm.where(Memo.class);
        int index = 0;
        for (Memo memo : selectedMemos) {
            if (index > 0) {
                query.or();
            }
            query.equalTo("id", memo.getId());
            index++;
        }
        final RealmResults<Memo> realmResults = query.findAll();
        realmResults.clear();
    }

    public RealmResults<Memo> search(final Realm realm, String query) {
        if (TextUtils.isEmpty(query)) {
            return getAsync(realm);
        }
        return realm.where(Memo.class)
                .contains("text", query, Case.INSENSITIVE)
                .findAllSortedAsync("time", Sort.ASCENDING);
    }
}
