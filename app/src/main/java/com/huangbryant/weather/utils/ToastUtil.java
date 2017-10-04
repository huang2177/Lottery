package com.huangbryant.weather.utils;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

/**
 * Created by kobe on 2017/10/3.
 */

public class ToastUtil {
    public static void topTSnackbar(String s) {
        View view1 = null;
        try {
            Activity activity = ActivityManager.getInstance().getCurrentActivity();
            view1 = activity.findViewById(android.R.id.content);
        } catch (Exception e) {
        }

        TSnackbar snackbar = TSnackbar.make(view1, s, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.WHITE);
        ViewGroup.LayoutParams lp = snackbarView.getLayoutParams();
        lp.height = 200;
        snackbarView.setLayoutParams(lp);
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        snackbar.show();
    }
}
