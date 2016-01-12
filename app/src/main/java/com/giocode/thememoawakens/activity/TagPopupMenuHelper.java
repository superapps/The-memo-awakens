package com.giocode.thememoawakens.activity;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.giocode.thememoawakens.R;
import com.giocode.thememoawakens.activity.reserved.ReservedActivity;
import com.giocode.thememoawakens.bo.ReservedBo;
import com.giocode.thememoawakens.model.Reserved;
import com.giocode.thememoawakens.util.ColorUtils;
import com.giocode.thememoawakens.util.TextConverter;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class TagPopupMenuHelper implements PopupMenu.OnMenuItemClickListener, PopupMenu.OnDismissListener {

    private final Context context;
    private final EditText editText;
    private final ReservedBo reservedBo;

    private boolean reservedItemClicked;
    private RealmResults<Reserved> currentReservedResults;
    private Reserved currentSelected;
    private ArrayList<String> parentHtmlTexts = new ArrayList<>();
    private int tagOrder;
    private int rootTagColorId;
    private RealmChangeListener reservedResultsChangeListener;
    private boolean waitingMenuItemLoading;
    private View tagButton;
    private RealmResults<Reserved> reservedResults;

    public TagPopupMenuHelper(final Context context, Realm realm, EditText editText) {
        this.context = context;
        this.editText = editText;
        this.reservedBo = new ReservedBo(realm);
        updateReservedResult(reservedBo.load(0));
    }

    private void updateReservedResult(RealmResults<Reserved> reservedResults) {
        this.reservedResults = reservedResults;
        this.currentReservedResults = reservedResults;
    }

    public void showPopupMenu(View tagButton) {
        this.tagButton = tagButton;
        PopupMenu popupMenu = new PopupMenu(context, tagButton);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.setOnDismissListener(this);
        Menu menu = popupMenu.getMenu();
        int itemId = 0;
        for (Reserved reserved : currentReservedResults) {
            menu.addSubMenu(0, itemId++, 0, TextConverter.toCharSequence(context, reserved.getText(), reserved.getSpans(), null));
        }
        menu.addSubMenu(0, itemId++, 0, R.string.add);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        reservedItemClicked = true;
        int itemId = item.getItemId();
        if (itemId < currentReservedResults.size()) {
            if (currentSelected != null) {
                Spannable spannable = (Spannable) TextConverter.toCharSequence(context, currentSelected.getText(), currentSelected.getSpans(), null);
                parentHtmlTexts.add(TextConverter.toHtmlString(spannable));
            }
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            if (tagOrder > 0) {
                ssb.append(ColorUtils.getTagSpannableStringBuilder(context, editText, ColorUtils.DELIMITER_START_ID + rootTagColorId));
            }

            currentSelected = currentReservedResults.get(itemId);
            CharSequence charSequence = TextConverter.toCharSequence(context, currentSelected.getText(), currentSelected.getSpans(), editText);
            if (tagOrder == 0) {
                rootTagColorId = ColorUtils.getTagColorId(context, (Spannable) charSequence);
            }

            ssb.append(charSequence);
            editText.getText().insert(editText.getSelectionStart(), ssb);
            if (reservedResultsChangeListener != null) {
                currentReservedResults.removeChangeListener(reservedResultsChangeListener);
            }
            currentReservedResults = reservedBo.load(currentReservedResults.get(itemId).getId());
            waitingMenuItemLoading = true;
            if (reservedResultsChangeListener == null) {
                reservedResultsChangeListener = new RealmChangeListener() {
                    @Override
                    public void onChange() {
                        if (currentReservedResults.size() > 0 && waitingMenuItemLoading) {
                            showPopupMenu(tagButton);
                        } else {
                            reservedItemClicked = false;
                            clearTagLink();
                        }
                        waitingMenuItemLoading = false;
                    }
                };
            }
            currentReservedResults.addChangeListener(reservedResultsChangeListener);
            tagOrder++;
        } else {
            context.startActivity(ReservedActivity.createIntent(context, tagOrder, currentSelected, parentHtmlTexts, rootTagColorId));
        }
        return true;

    }

    @Override
    public void onDismiss(PopupMenu menu) {
        clearTagLink();
    }

    private void clearTagLink() {
        if (!reservedItemClicked) {
            if (reservedResultsChangeListener != null) {
                currentReservedResults.removeChangeListener(reservedResultsChangeListener);
            }
            currentReservedResults = reservedResults;
            currentSelected = null;
            parentHtmlTexts.clear();
            tagOrder = 0;
        }
        reservedItemClicked = false;
    }

    public boolean isEmptyTag() {
        return reservedResults.isEmpty();
    }

    public void close() {
        if (currentReservedResults != null && reservedResultsChangeListener != null) {
            currentReservedResults.removeChangeListener(reservedResultsChangeListener);
        }
    }
}
