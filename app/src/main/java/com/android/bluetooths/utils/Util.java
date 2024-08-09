package com.android.bluetooths.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Util {

    public static  void DisplayToast(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();
    }
}
