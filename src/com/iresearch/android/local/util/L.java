
package com.iresearch.android.local.util;

import android.util.Log;

import static com.iresearch.android.local.util.Util.notNullArg;

/**
 * @hide
 */
public final class L {
    private static boolean sLogEnabled = true;

    public static void setLogEnabled(boolean logEnabled) {
        sLogEnabled = logEnabled;
    }

    private L() {
        // No instances baby!
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (sLogEnabled) {
            validate(tag, msg);
            Log.e(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (sLogEnabled) {
            validate(tag, msg);
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (sLogEnabled) {
            validate(tag, msg);
            Log.w(tag, msg);
        }
    }

    private static void validate(String tag, String msg) {
        notNullArg(tag, "Log tag must not be null.");
        notNullArg(msg, "Log message must not be null.");
    }

}
