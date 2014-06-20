package com.iresearch.android.utils;

import java.io.File;
import java.util.List;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;

/**
 * @className IntentUtils
 * @create 2014年4月16日 上午11:44:14
 * @author lilong@qiyi.com
 * @description 封装常用的Intent操作 
 */
public class IntentUtils {

	public static final String MIME_TYPE_TEXT = "text/*";
	public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_EMAIL = "message/rfc822";

    private IntentUtils() {
    }
    
    public static boolean isIntentAvailable(Context context, String action, Uri uri, String mimeType) {
        final Intent intent = (uri != null) ? new Intent(action, uri) : new Intent(action);
        if (mimeType != null) {
            intent.setType(mimeType);
        }
        return isIntentAvailable(context, intent);
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return !list.isEmpty();
    }

    public static Intent newEmailIntent(Context context, String subject, String body, String... address) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.setType(MIME_TYPE_EMAIL);
        return intent;
    }

    public static Intent newShareIntent(Context context, String content, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND); //启动分享发送的属性  
        if (uri != null) {
        	intent.putExtra(Intent.EXTRA_STREAM, uri); //分享流媒体
        	intent.setType(MIME_TYPE_IMAGE);
        	intent.putExtra("sms_body", content); 
        } else {
        	intent.setType(MIME_TYPE_TEXT);
        }
        intent.putExtra(Intent.EXTRA_TEXT, content);
        return intent;
    }

    public static Intent newMapsIntent(String address, String placeTitle) {
        StringBuilder sb = new StringBuilder();
        sb.append("geo:0,0?q=");

        String addressEncoded = Uri.encode(address);
        sb.append(addressEncoded);
        // pass text for the info window
        String titleEncoded = Uri.encode("(" + placeTitle + ")");
        sb.append(titleEncoded);
        // set locale; probably not required for the maps app?
        sb.append("&hl=" + Locale.getDefault().getLanguage());

        return new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));
    }

    public static Intent newTakePictureIntent(File tempFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        return intent;
    }

    @SuppressLint("InlinedApi")
    public static Intent newMediaIntent(String type) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        intent.setType(type);
        return intent;
    }
    
    public static Intent newDialNumberIntent(String phoneNumber) {
        return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber.replace(" ", "")));
    }

    public static Intent newCallNumberIntent(String phoneNumber) {
        return new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber.replace(" ", "")));
    }

    public static Intent newTakeVideoIntent() {
    	return new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    }
    
    public static Intent newSmsIntent(String phoneNumber, String message) {
    	Uri uri = Uri.parse("smsto:" + phoneNumber);
    	Intent intent = new Intent(Intent.ACTION_SENDTO);
    	intent.setData(uri);
    	intent.putExtra("sms_body", message);
    	intent.setType("vnd.android-dir/mms-sms");
    	return intent;
    }
    
    /** 从market市场安装应用app */
	public static Intent newMarketIntent(String packageName) {
		Intent installIntent = new Intent(Intent.ACTION_VIEW);  
		// market://details?id=com.adobe.flashplayer
        installIntent.setData(Uri.parse("market://details?id=" + packageName));  
        return installIntent;
	}
	
	public static Intent newWebViewIntent(String uri) {
		 return new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
	}
	
	public static Intent newGoogleSearchIntent(String term) {
		 Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		 intent.putExtra(SearchManager.QUERY, term);
		 return intent;
	}
	
	public static Intent newWiFiSettingIntent() {
		return new Intent(Settings.ACTION_WIRELESS_SETTINGS);
	}
	
	public static Intent newGpsSettingsIntent() {
		return new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	}
	
	public static Intent newVideoIntent(File tempFile) {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE); 
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
	    return intent;
	}
	
	/** 截取图像部分区域 ;魅族的机器没有返回data字段，但是返回了filePath */
	public static Intent newCropImageUri(Uri uri, int outputX, int outputY){
		//android1.6以后只能传图库中图片
		//http://www.linuxidc.com/Linux/2012-11/73940.htm
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true"); //发送裁剪信号
		intent.putExtra("aspectX", 1); 	//X方向上的比例
		intent.putExtra("aspectY", 1); 	//Y方向上的比例
		intent.putExtra("outputX", outputX); //裁剪区的宽
		intent.putExtra("outputY", outputY); //裁剪区的高
		intent.putExtra("scale", true);//是否保留比例
		//拍摄的照片像素较大，建议直接保存URI，否则内存溢出，较小图片可以直接返回Bitmap
		/*Bundle extras = data.getExtras();
		if (extras != null) {	
			Bitmap bitmap = extras.getParcelable("data");
	    }*/
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);  //是否返回数据     
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); // 图片格式
		intent.putExtra("noFaceDetection", true); //关闭人脸检测
		return intent;
	}
	
	public static Intent newManageApplicationIntent() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClassName("com.android.settings", "com.android.settings.ManageApplications");
		return intent;
	}
	
	public static Intent newInstallApkIntent(File apkFile) {
		Intent intent=new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://"+apkFile.toString()), "application/vnd.android.package-archive");
		return intent;
	}
	
	public static Intent newUnInstallApkIntent(Uri data) {
		return new Intent(Intent.ACTION_DELETE, data);
	}
	
	public static Intent newScanFileIntent(Uri data) {
		return new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data);
	}
}
