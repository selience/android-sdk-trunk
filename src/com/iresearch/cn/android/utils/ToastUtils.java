package com.iresearch.cn.android.utils;

import android.widget.Toast;
import android.content.Context;

/**
 * ToastUtils
 * 
 * @author lilong@qiyi.com
 */
public class ToastUtils {

    private static Toast toast;
    
    public static void show(Context context, int resId) {
        show(context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration) {
        show(context, context.getResources().getText(resId), duration);
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, String.format(format, args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), duration);
    }

    public static void show(Context context, String format, int duration, Object... args) {
        show(context, String.format(format, args), duration);
    }
    
    public static void show(Context context, CharSequence text, int duration) {
        if (toast != null) {
            toast.setText(text);
            toast.setDuration(duration);
            toast.show();
        } else {
            toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}
