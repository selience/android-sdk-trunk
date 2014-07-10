package com.iresearch.android.utils;

import java.io.File;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.UnsupportedEncodingException;

public class DeviceUtils {

	public static int sdkVersion() {
    	return Build.VERSION.SDK_INT;
    }
	
	/** 判断是否是模拟器  */
	public static boolean isEmulator() {
		return Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk");
	}
	
	/** 是否为GoogleTV */
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public static boolean isGoogleTV(Context context) {
		if (sdkVersion() < Build.VERSION_CODES.ECLAIR) return false;
    	return context.getPackageManager().hasSystemFeature("com.google.android.tv");
    }
	
	/** 判断是否是平板（官方用法） */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
	
    /** 判断是否具有Root权限  */
    public static boolean isRoot() {
    	try {
    		return (!new File("/system/bin/su").exists()) 
    		&& (!new File("/system/xbin/su").exists());
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return false;
    	}
    }
    
    /** 获取屏幕的宽度  */
    public static float getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    
    /** 获取屏幕的高度  */
    public static float getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    
    /** dip转化成px */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /** px转化成dip */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
    public static float pxToDp(Context ctx, float px) {
    	return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, ctx.getResources().getDisplayMetrics());
    }
    
    public static float dpToPx(Context ctx, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
    }
    
    public static float PxToSp(Context ctx, float px) {
    	float scaledDensity = ctx.getResources().getDisplayMetrics().scaledDensity;
    	return (px / scaledDensity);
    }
 
    public static float spToPx(Context ctx, float sp) {
        float scaledDensity = ctx.getResources().getDisplayMetrics().scaledDensity;
        return sp * scaledDensity;
    }
    
    
	/** 获取设备的密度大小   */
    public static float getDeviceDensity(Context context) {
    	return context.getResources().getDisplayMetrics().density;
    }
    
    /** 获取设备的密度因子  */
    public static int getDeviceDensitydpi(Context context) {
    	return context.getResources().getDisplayMetrics().densityDpi;
    }
	
    /** 屏幕分辨率，字符串显示 (例如：640*960)  */
	public static String getDeviceForResolution(Context ctx) {
		String s = "%s*%s";
		int width = ctx.getResources().getDisplayMetrics().widthPixels;
		int height = ctx.getResources().getDisplayMetrics().heightPixels;
		return String.format(s, width, height);
	}
    
	 /** 精确获取屏幕尺寸（例如：3.5、4.0、5.0寸屏幕）  */
    public static double getDevicePhysicalSize(Activity ctx) {
        DisplayMetrics dm = new DisplayMetrics();
        ctx.getWindowManager().getDefaultDisplay().getMetrics(dm);
        double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
        return diagonalPixels / (160 * dm.density);
    }

    /**
     * Returns the ANDROID_ID unique device ID for the current device. Reading that ID has changed
     * between platform versions, so this method takes care of attempting to read it in different
     * ways, if one failed.
     * 
     * @param context
     *            the context
     * @return the device's ANDROID_ID, or null if it could not be determined
     * @see Secure#ANDROID_ID
     */
    public static String getAndroidId(Context context) {
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        if (androidId == null) {
            // this happens on 1.6 and older
            androidId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        }
        if (androidId == null) {
            androidId= ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        }
        return androidId;
    }
    
	/**
	 * 获取设备唯一标识ID
	 */
	public static String generateDeviceId(Context context) {
		UUID uuid = null;
		final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		try {
			//ANDROID_ID是设备第一次启动时产生和存储的64bit的一个数，当设备被wipe后该数重置
			//它在Android <=2.1 or Android >=2.3的版本是可靠、稳定的，但在2.2的版本并不是100%可靠的
			//在主流厂商生产的设备上，有一个很经常的bug，就是每个设备都会产生相同的ANDROID_ID：9774d56d682e549c
			if (androidId != null && !"9774d56d682e549c".equals(androidId)) {
				uuid = UUID.nameUUIDFromBytes(androidId.getBytes("UTF-8"));
			} else {
				//根据不同的手机设备返回IMEI，MEID或者ESN码
				//非手机设备就没有这个DEVICE_ID，获取DEVICE_ID需要READ_PHONE_STATE权限
				final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
				if (deviceId != null) {
					uuid = UUID.nameUUIDFromBytes(deviceId.getBytes("UTF-8"));
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//UUID经常用来标识在某个应用中的唯一ID
		if (uuid == null) {
			uuid = UUID.randomUUID();
		}
		return uuid.toString().replace("-", "");
	}
}
