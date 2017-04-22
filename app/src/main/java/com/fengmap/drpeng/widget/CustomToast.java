package com.fengmap.drpeng.widget;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by yangbin on 16/1/20.
 */
public class CustomToast {
    private static Toast toast;

    public static void show(Context context, String text, int time) {
        if (toast == null) {
            toast = Toast.makeText(context, text, time);
        } else {
            toast.setText(text);
            toast.setDuration(time);
        }

        toast.show();
    }


    public static void show(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_LONG);
        }

        toast.show();
    }


}
