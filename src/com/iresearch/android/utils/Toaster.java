
package com.iresearch.android.utils;

import android.widget.Toast;
import android.app.Activity;
import android.content.Context;

/**
 * Helper to show {@link Toast} notifications
 */
public class Toaster {

    private static Toast toast;

    private Toaster() {
    }

    public static void show(Activity activity, int resId) {
        if (activity == null)
            return;
        show(activity, activity.getResources().getText(resId), Toast.LENGTH_SHORT);
    }

    public static void show(Activity activity, int resId, int duration) {
        if (activity == null)
            return;
        show(activity, activity.getResources().getText(resId), duration);
    }

    public static void show(Activity activity, CharSequence text) {
        show(activity, text, Toast.LENGTH_SHORT);
    }

    public static void show(Activity activity, int resId, Object... args) {
        if (activity == null)
            return;
        show(activity, activity.getResources().getString(resId), args);
    }

    public static void show(Activity activity, String format, Object... args) {
        show(activity, String.format(format, args), Toast.LENGTH_SHORT);
    }

    public static void show(Activity activity, int resId, int duration, Object... args) {
        if (activity == null)
            return;
        show(activity, activity.getResources().getString(resId), duration, args);
    }

    public static void show(Activity activity, String format, int duration, Object... args) {
        show(activity, String.format(format, args), duration);
    }

    public static void show(final Activity activity, final CharSequence text, final int duration) {
        if (activity == null)
            return;

        final Context context = activity.getApplication();
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (toast != null) {
                    toast.setText(text);
                    toast.setDuration(duration);
                    toast.show();
                } else {
                    toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }
}
