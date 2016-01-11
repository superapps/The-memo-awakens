package com.giocode.thememoawakens.dao;

import com.giocode.thememoawakens.model.Reserved;
import com.giocode.thememoawakens.model.Span;

import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ReservedDao {

    public void insert(final Realm realm, final long parentId, final String text, final RealmList<Span> spans) {

        long curId = 1;
        Number maxId = realm.where(Reserved.class).max("id");
        if (maxId != null) {
            curId = maxId.longValue() + 1;
        }

        Reserved reserved = new Reserved();
        reserved.setId(curId);
        reserved.setText(text);
        reserved.setSpans(spans);
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

    public RealmResults<Reserved> getParentReserved(final Realm realm, final long[] parentIds) {
        RealmQuery<Reserved> query = realm.where(Reserved.class);
        int index = 0;
        for (long parentId : parentIds) {
            if (index > 0) {
                query.or();
            }
            query.equalTo("id", parentId);
            index++;
        }
        return query.findAllAsync();
    }


    public void clearAll(final Realm realm) {
        final RealmResults<Reserved> realmResults = realm.where(Reserved.class)
                .findAll();
        realmResults.clear();
    }

    public void delete(final Realm realm, List<Reserved> selected) {
        RealmQuery<Reserved> query = realm.where(Reserved.class);
        int index = 0;
        long parentId = 0;
        for (Reserved reserved : selected) {
            if (index > 0) {
                query.or();
            }
            query.equalTo("id", reserved.getId());
            parentId = reserved.getParentId();
            index++;
        }
        final RealmResults<Reserved> realmResults = query.findAll();
        realmResults.clear();
        if (parentId > 0) {
            updateParent(realm, parentId);
        }
    }

    private void updateParent(Realm realm, long parentId) {
        long childCount = realm.where(Reserved.class)
                .equalTo("parentId", parentId)
                .count();

        Reserved reserved = realm.where(Reserved.class)
                .equalTo("id", parentId)
                .findFirst();
        if (reserved != null) {
            reserved.setChildCount((int)childCount);
        }
    }
}
