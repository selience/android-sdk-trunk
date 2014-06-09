package com.iresearch.cn.android.utils;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;

/**
 * @file ClipboardUtils.java
 * @create 2013-4-10 下午12:00:42
 * @author Jacky.Lee
 * @description TODO 剪贴板操作管理
 */
public class ClipboardUtils {

    private ClipboardUtils() {
    }
    
	@SuppressWarnings("deprecation")
    public static CharSequence getText(final Context context) {
		return ((android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).getText();
	}

	@SuppressWarnings("deprecation")
	public static boolean setText(final Context context, final CharSequence text) {
		if (context == null) return false;
		((android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setText(text);
		return true;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static ClipData getData(Context context) {
		return ((android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static boolean setData(Context context, final ClipData clipData) {
		if (context == null) return false;
		((android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
		return true;
	}
}
