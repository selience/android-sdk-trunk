
package com.iresearch.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.io.File;
import java.io.IOException;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import com.iresearch.android.log.XLog;
import com.iresearch.android.tools.accessor.EnvironmentAccessor;

public class UIUtils {

    /**
     * 检查是否安装SD卡
     * 
     * @return
     */
    public static boolean isExternalStorageMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
    
    /**
     * 获取APP数据缓存目录
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context) {
        if (isExternalStorageMounted()) {
            Context appContext=context.getApplicationContext();
            File rootDir=EnvironmentAccessor.getExternalCacheDir(appContext);
            if (rootDir!=null && !rootDir.exists()) {
                if (rootDir.mkdirs()) return rootDir;
            }
        }
        return null;
    }
    
    /**
     * 获取APP文件存储目录
     * @param context
     * @return
     */
    public static File getExternalFilesDir(Context context) {
        if (isExternalStorageMounted()) {
            Context appContext=context.getApplicationContext();
            File rootDir=EnvironmentAccessor.getExternalFilesDir(appContext, null);
            if (rootDir!=null && !rootDir.exists()) {
                if (rootDir.mkdirs()) return rootDir;
            }
        }
        return null;
    }
    
    /**
     * 强制隐藏输入法窗口
     * 
     * @param view
     */
    public static void hideSoftInputFromWindow(View view) {
        // 实例化输入法控制对象，通过hideSoftInputFromWindow来控制
        final Context context=view.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 设置全屏模式
     * @param activity
     * @param isFullScreen true 设置全拼; false 退出全屏；
     */
    public static void setFullScreen(Activity activity, boolean isFullScreen) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (isFullScreen) {
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(params);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
    
    /**
     * 扫描指定文件到媒体库
     * 
     * @param context
     * @param filePath
     */
    public static void scanFile(Context context, File filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(filePath));
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 平移动画处理
     * 
     * @param v
     * @param startX
     * @param toX
     * @param startY
     * @param toY
     */
    public static void moveFrontAnimation(View v, int startX, int toX, int startY, int toY) {
        TranslateAnimation anim = new TranslateAnimation(startX, toX, startY, toY);
        anim.setDuration(200);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    /**
     * Returns the location of the view on the screen. The screen includes the
     * 'notification area' (aka 'status bar').
     */
    public static Rect getLocationInScreen(View v) {
        int[] location = new int[2];
        v.getLocationInWindow(location);
        int x = location[0];
        int y = location[1];
        int width = v.getWidth();
        int height = v.getHeight();
        Rect rectPick = new Rect(x, y, x + width, y + height);
        return rectPick;
    }

    /**
     * Returns the location of the view on its window. The window does not
     * include the 'notification area' (aka 'status bar').
     */
    public static Rect getLocationInWindow(View v) {
        // Height of status bar
        Rect rect = new Rect();
        ((Activity) v.getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        Rect res = getLocationInScreen(v);

        res.offset(0, -statusBarHeight);
        return res;
    }

    /**
     * 获取照片旋转的方向
     * 
     * @param filepath
     * @return
     */
    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            XLog.e("BackwardSupport", "cannot read exif", ex);
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }
}
