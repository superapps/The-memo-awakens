package com.giocode.thememoawakens.bo;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.giocode.thememoawakens.dao.ReservedDao;
import com.giocode.thememoawakens.model.Reserved;
import com.giocode.thememoawakens.model.Span;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ReservedBo {

    @NonNull
    private final Realm realm;
    @NonNull
    private final ReservedDao dao = new ReservedDao();

    public ReservedBo(@NonNull Realm realm) {
        this.realm = realm;
    }

    public void add(final long parentId, final Pair<String, RealmList<Span>> textInfo) {

        if (textInfo == null || TextUtils.isEmpty(textInfo.first)) {
            return;
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                dao.insert(realm, parentId, textInfo.first, textInfo.second);
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

    public RealmResults<Reserved> load(final long parentId) {
        return dao.getReserved(realm, parentId);
    }

    public void delete(final List<Reserved> selected) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                dao.delete(realm, selected);
            }
        });

    }

    public RealmResults<Reserved> loadParent(long[] parentIds) {
        return dao.getParentReserved(realm, parentIds);
    }
}
