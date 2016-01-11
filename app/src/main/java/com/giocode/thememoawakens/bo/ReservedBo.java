package com.giocode.thememoawakens.bo;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.giocode.thememoawakens.dao.ReservedDao;
import com.giocode.thememoawakens.model.Reserved;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReservedBo {

    @NonNull
    private final Realm realm;
    @NonNull
    private final ReservedDao dao = new ReservedDao();

    public ReservedBo(@NonNull Realm realm) {
        this.realm = realm;
    }

    public void add(final long parentId, final String htmlText) {

        if (TextUtils.isEmpty(htmlText)) {
            return;
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                dao.insert(realm, parentId, htmlText);
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
                dao.delete(selected);
            }
        });

    }

    public RealmResults<Reserved> loadParent(long[] parentIds) {
        return dao.getParentReserved(realm, parentIds);
    }
}
