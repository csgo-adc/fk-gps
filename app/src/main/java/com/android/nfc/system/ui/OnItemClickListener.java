package com.android.nfc.system.ui;

import android.view.View;

public interface OnItemClickListener {
    public void onItemClick(View view, int position);

    public void onItemLongClick(View view, int position);
}
