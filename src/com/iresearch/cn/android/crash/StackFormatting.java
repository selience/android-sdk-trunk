package com.iresearch.cn.android.crash;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import android.provider.Settings.SettingNotFoundException;

public class StackFormatting {
	
	public static String getStackMessageString(Throwable e) {
		StringBuffer message = new StringBuffer();
		StackTraceElement[] stack = e.getStackTrace();
		StackTraceElement stackLine = stack[(stack.length - 1)];
		message.append(stackLine.getFileName());
		message.append(":");
		message.append(stackLine.getLineNumber());
		message.append(":");
		message.append(stackLine.getMethodName());
		message.append(" ");
		message.append(e.getMessage());
		return message.toString();
	}

	public static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}
		
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			tr.printStackTrace(pw);
			String error = sw.toString();
			sw.close();
			pw.close();
			return error;
		} catch (IOException e) {
			return e.getMessage();
		}
	}
	
	public static String collectCrashReport(Context context, String version, String deviceId, Throwable tr) {
	    StringBuilder sb = new StringBuilder();

        sb.append("App version: v" + version + "\n");
        sb.append("Device locale: " + Locale.getDefault().toString() + "\n\n");
        sb.append("Android ID: " + deviceId);

        // phone information
        sb.append("PHONE SPECS\n");
        sb.append("model: " + Build.MODEL + "\n");
        sb.append("brand: " + Build.BRAND + "\n");
        sb.append("product: " + Build.PRODUCT + "\n");
        sb.append("device: " + Build.DEVICE + "\n\n");

        // android information
        sb.append("PLATFORM INFO\n");
        sb.append("Android " + Build.VERSION.RELEASE + " " + Build.ID + " (build " + Build.VERSION.INCREMENTAL + ")\n");
        sb.append("build tags: " + Build.TAGS + "\n");
        sb.append("build type: " + Build.TYPE + "\n\n");

        // settings
        sb.append("SYSTEM SETTINGS\n");
        String networkMode = null;
        ContentResolver resolver = context.getContentResolver();
        try {
            if (Settings.Secure.getInt(resolver, Settings.Global.WIFI_ON) == 0) {
                networkMode = "DATA";
            } else {
                networkMode = "WIFI";
            }
            sb.append("network mode: " + networkMode + "\n");
            sb.append("HTTP proxy: " + Settings.Secure.getString(resolver, Settings.Secure.HTTP_PROXY) + "\n\n");
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }

        sb.append("STACK TRACE FOLLOWS\n\n");

        sb.append(getStackMessageString(tr));
        sb.append("\n\n");
        sb.append(getStackTraceString(tr));

        return sb.toString();
	}
}