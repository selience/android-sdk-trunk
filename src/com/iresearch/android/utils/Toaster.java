
package com.iresearch.android.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import android.content.Context;

/**
 * Helper to show {@link Toast} notifications
 */
public class Toaster {

    private static Toast toast;
    private static Handler handler;

    static {
        handler=new Handler(Looper.getMainLooper());
    }
    
    public static void show(Context context, int resId) {
        if (context == null)
            return;
        show(context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration) {
        if (context == null)
            return;
        show(context, context.getResources().getText(resId), duration);
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, Object... args) {
        if (context == null)
            return;
        show(context, context.getResources().getString(resId), args);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, String.format(format, args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration, Object... args) {
        if (context == null)
            return;
        show(context, context.getResources().getString(resId), duration, args);
    }

    public static void show(Context context, String format, int duration, Object... args) {
        show(context, String.format(format, args), duration);
    }

    public static void show(final Context context, final CharSequence text, final int duration) {
        if (context == null)
            return;

        final Context appContext=context.getApplicationContext();
        handler.post(new Runnable() {
            public void run() {
                if (toast != null) {
                    toast.setText(text);
                    toast.setDuration(duration);
                    toast.show();
                } else {
                    toast = Toast.makeText(appContext, text, duration);
                    toast.show();
                }
            }
        });
    }
}
