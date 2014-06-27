
package com.iresearch.android.tools.accessor;

import java.io.File;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

public final class EnvironmentAccessor {

    /**
     * Check if the external storage is mounted 'read/write'.
     * 
     * @return {@code true} if it is mounted 'read/write', {@code false}
     *         otherwise.
     */
    public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Returns the cache directory, using {@link Context#getExternalCacheDir()}
     * on level 8+ or an equivalent call for level < 8.
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return context.getExternalCacheDir();
        }
        // API Level <8 Equivalent of context.getExternalCacheDir()
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        return new File(externalStorageDirectory, "Android/data/" + context.getPackageName()+ "/cache");
    }

    /**
     * Returns the files directory, using
     * {@link Context#getExternalFilesDir(String)} on level 8+ or an equivalent
     * call for level < 8.
     */
    public static File getExternalFilesDir(Context context, String type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return context.getExternalFilesDir(type);
        }
        // API Level <8 Equivalent of context.getExternalFilesDir()
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        return new File(externalStorageDirectory, "Android/data/" + context.getPackageName()+ "/files");
    }

}
