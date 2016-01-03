package com.giocode.thememoawakens.dao;

import com.giocode.thememoawakens.model.Reserved;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReservedDao {

    public void insert(final Realm realm, final long parentId, final String text) {

        long curId = 1;
        Number maxId = realm.where(Reserved.class).max("id");
        if (maxId != null) {
            curId = maxId.longValue() + 1;
        }

        Reserved reserved = new Reserved();
        reserved.setId(curId);
        reserved.setHtmlText(text);
        reserved.setChildCount(0);
        reserved.setParentId(parentId);

        realm.copyToRealmOrUpdate(reserved);

        if (parentId > 0) {
            Reserved result = realm.where(Reserved.class)
                    .equalTo("id", parentId)
                    .findFirst();
            if (result != null) {
                result.setChildCount(result.getChildCount() + 1);
            }
            realm.copyToRealmOrUpdate(result);
        }
    }

    public void update(final Realm realm, final Reserved reserved) {
        realm.copyToRealmOrUpdate(reserved);
    }

    public RealmResults<Reserved> getReserved(final Realm realm, final long parentId) {
        return realm.where(Reserved.class)
                .equalTo("parentId", parentId)
                .findAllAsync();
    }

    public void clearAll(final Realm realm) {
        final RealmResults<Reserved> realmResults = realm.where(Reserved.class)
                .findAll();
        realmResults.clear();
    }


}
