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
    
    /**
     * Checks whether there are applications installed which are able to handle the given intent.
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return !list.isEmpty();
    }

    /**
     * Create an intent to send an email with an attachment
     */
    public static Intent newEmailIntent(String subject, String body, Uri attachment, String... addresses) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (addresses != null) intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        if (body != null) intent.putExtra(Intent.EXTRA_TEXT, body);
        if (subject != null) intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (attachment != null) intent.putExtra(Intent.EXTRA_STREAM, attachment);
        intent.setType(MIME_TYPE_EMAIL);

        return intent;
    }

    /**
     * Creates a chooser to share some data.
     */
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

    /**
     * Intent that should allow opening a map showing the given address (if it exists)
     */
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

    /**
     * Intent that should allow opening a map showing the given location (if it exists)
     */
    public static Intent newMapsIntent(float latitude, float longitude) {
        StringBuilder sb = new StringBuilder();
        sb.append("geo:");

        sb.append(latitude);
        sb.append(",");
        sb.append(longitude);

        return new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));
    }
    
    /**
     * Intent that should allow opening a map showing the given address (if it exists)
     */
    public static Intent newNavigationIntent(String address) {
        StringBuilder sb = new StringBuilder();
        sb.append("google.navigation:q=");

        String addressEncoded = Uri.encode(address);
        sb.append(addressEncoded);

        return new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));
    }
    
    /**
     * Creates an intent that will launch the camera to take a picture that's saved to a temporary file so you can use
     * it directly without going through the gallery.
     */
    public static Intent newTakePictureIntent(File tempFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        return intent;
    }

    /**
     * Creates an intent that will launch the phone's picture gallery to select a picture from it.
     */
    public static Intent newSelectPictureIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        return intent;
    }
    
    /**
     * Creates an intent that will launch a browser (most probably as other apps may handle specific URLs, e.g. YouTube)
     * to view the provided URL.
     */
    public static Intent newOpenWebBrowserIntent(String url) {
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "http://" + url;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        return intent;
    }
    
    /**
     * Open the video player to play the given
     */
    public static Intent newPlayVideoIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "video/*");
        return intent;
    }
    
    /**
     * Creates an intent that will capture a video file.
     */
    public static Intent newVideoIntent(File tempFile) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE); 
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        
        return intent;
    }
    
    /**
     *  Creates an intent that will select a media file.
     */
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
    
    /**
     * Creates an intent that will allow to send an SMS to a phone number
     */
    public static Intent newSmsIntent(String phoneNumber, String body) {
        final Intent intent;
        if (phoneNumber == null || phoneNumber.trim().length() <= 0) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        }
        intent.putExtra("sms_body", body);
        return intent;
    }
    
    /**
     * Creates an intent that will open the phone app and enter the given number.
     */
    public static Intent newDialNumberIntent(String phoneNumber) {
        final Intent intent;
        if (phoneNumber == null || phoneNumber.trim().length() <= 0) {
            intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"));
        } else {
            intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber.replace(" ", "")));
        }
        return intent;
    }

    /**
     * Creates an intent that will immediately dispatch a call to the given number.
     */
    public static Intent newCallNumberIntent(String phoneNumber) {
        final Intent intent;
        if (phoneNumber == null || phoneNumber.trim().length() <= 0) {
            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"));
        } else {
            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber.replace(" ", "")));
        }
        return intent;
    }

    /**
     * Intent that should open the app store of the device on the given application
     */
    public static Intent newMarketForAppIntent(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));

        if (!IntentUtils.isIntentAvailable(context, intent)) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + packageName));
        }

        if (!IntentUtils.isIntentAvailable(context, intent)) {
            intent = null;
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }

        return intent;
    }
    
    /**
     * Intent that should open either the Google Play app or if not available, the web browser on the Google Play website
     */
    public static Intent newGooglePlayIntent(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));

        if (!IntentUtils.isIntentAvailable(context, intent)) {
            intent = newOpenWebBrowserIntent("https://play.google.com/store/apps/details?id=" + packageName);
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }

        return intent;
    }
    
    /**
     * Intent that should open either the Amazon store app or if not available, the web browser on the Amazon website
     */
    public static Intent newAmazonStoreIntent(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + packageName));

        if (!IntentUtils.isIntentAvailable(context, intent)) {
            intent = newOpenWebBrowserIntent("http://www.amazon.com/gp/mas/dl/android?p=" + packageName);
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }

        return intent;
    }
	
    /**
     * 获取谷歌搜索Intent
     * @param term
     * @return
     */
	public static Intent newGoogleSearchIntent(String term) {
		 Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		 intent.putExtra(SearchManager.QUERY, term);
		 return intent;
	}
	
	/**
	 * 启动WiFi网络设置Intent
	 * @return
	 */
	public static Intent newWiFiSettingIntent() {
	    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
	    return intent;
	}
	
	/**
	 * 启动GPS定位设置Intent
	 * @return
	 */
	public static Intent newGpsSettingsIntent() {
	    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    return intent;
	}
	
	/**
	 * 截取图像部分区域 ;魅族的机器没有返回data字段，但是返回了filePath
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @return
	 */
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
	
	/**
	 * 创建系统应用管理Intent
	 * @return
	 */
	public static Intent newManageApplicationIntent() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClassName("com.android.settings", "com.android.settings.ManageApplications");
		
		return intent;
	}
	
	/**
	 * 创建应用安装Intent
	 * @param apkFile
	 * @return
	 */
	public static Intent newInstallApkIntent(File apkFile) {
		Intent intent=new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://"+apkFile.toString()), "application/vnd.android.package-archive");
		
		return intent;
	}
	
	/**
	 * 创建卸载应用Intent
	 * @param data
	 * @return
	 */
	public static Intent newUnInstallApkIntent(Uri data) {
		return new Intent(Intent.ACTION_DELETE, data);
	}
	
	/**
	 * 创建文件扫描Intent
	 * @param data
	 * @return
	 */
	public static Intent newScanFileIntent(Uri data) {
		return new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data);
	}
}
