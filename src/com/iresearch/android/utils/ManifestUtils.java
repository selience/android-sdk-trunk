package com.iresearch.android.utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.DisplayMetrics;
import com.iresearch.android.log.L;

/**
 * @file ManifestSupport.java
 * @create 2013-4-10 上午10:23:44
 * @author Jacky.Lee
 * @description TODO
 */
public class ManifestUtils {
    private static final String TAG = "ManifestUtils";
    
    private ManifestUtils() {
    }
    
    /**
     * Checks if the application is in the background (i.e behind another application's Activity).
     * 
     * @param context
     * @return true if another application is above this one.
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * 检查应用是否正在运行
     * @param context
     * @param pid
     * @param processName
     * @return
     */
    public static boolean checkIfIsAppRunning(Context context, String processName) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> mRunningList = am.getRunningAppProcesses();
        Iterator<RunningAppProcessInfo> iterator = mRunningList.iterator();
        while (iterator.hasNext()) {
            RunningAppProcessInfo info = (RunningAppProcessInfo) iterator.next();
            if (info.pid == pid) {
                if (processName.equals(info.processName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 获取当前应用版本名称
     * @param context
     * @return
     */
    public static String getApplicationForVersionName(Context context) {
        try {
            final String PACKAGE_NAME = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(PACKAGE_NAME, 0);
            return pi.versionName;
        } catch (NameNotFoundException ex) {
            L.e(TAG, "Failed to load versionName, NameNotFound: " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * 获取当前应用的版本号
     * @param context
     * @return
     */
    public static int getApplicationForVersionCode(Context context) {
        try {
            final String PACKAGE_NAME = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(PACKAGE_NAME, 0);
            return pi.versionCode;
        } catch (NameNotFoundException ex) {
            L.e(TAG, "Failed to load versionCode, NameNotFound: " + ex.getMessage());
            return -1;
        }
    }
    
    /**
     * 获取应用程序元数据Meta-Data
     * @param context
     * @param key
     * @return
     */
    public static String getApplicationForMetaData(Context context, String key) {
        try {
            final String PACKAGE_NAME = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(PACKAGE_NAME, PackageManager.GET_META_DATA);
            return ai.metaData.getString(key);
        } catch (NameNotFoundException ex) {
            L.e(TAG, "Failed to load meta-data, NameNotFound: " + ex.getMessage());
        } catch (NullPointerException ex) {
            L.e(TAG, "Failed to load meta-data, NullPointer: " + ex.getMessage());
        }
        return null;
    }
    
    /**
     * 获取所有安装过的应用，过滤掉了系统应用
     * @param context
     * @return
     */
    public static List<PackageInfo> getApplicationInstalledApps(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> mApps = new ArrayList<PackageInfo>(); //获取所有安装的应用
        for (PackageInfo itemInfo : pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)) {
            // 过滤掉系统应用
            if ((itemInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                mApps.add(itemInfo);
            }
        }
        return mApps;
    }
    
    /**
     * 构建User-Agent应用标识用于远程请求标识
     * @param context
     * @return
     */
    public static String buildUserAgent(Context context) {
        String versionName = "unknown";
        int versionCode = 0;

        try {
            final String PACKAGE_NAME = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(PACKAGE_NAME, 0);
            versionName = info.versionName;
            versionCode = info.versionCode;
        } catch (NameNotFoundException ex) {
            L.e(TAG, "Failed to load meta-data, NameNotFound: " + ex.getMessage());
        }

        return context.getPackageName() + "/" + versionName + " (" + versionCode + ") (gzip)";
    }
    
    /**
     * 通过比对签名判断是否为最终发布版本
     * @param context
     * @param resId
     * @return
     */
    public static boolean isReleaseVersion(Context context, int resId) {
        final String releaseSignatureString = context.getString(resId);
            if (releaseSignatureString == null || releaseSignatureString.length() == 0) {
            throw new RuntimeException("Release signature string is null or missing.");
        }

        final Signature releaseSignature = new Signature(context.getString(resId));

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature sig : pi.signatures) {
                if (sig.equals(releaseSignature)) {
                    L.v(TAG, "Determined that this is a RELEASE build.");
                    return true;
                }
            }
        }
        catch (Exception e) {
            L.w(TAG, "Exception thrown when detecting if app is signed by a release keystore, assuming this is a release build.", e);
            return true; // Return true if we can't figure it out
        }
        L.v(TAG, "Determined that this is a DEBUG build.");

        return false;
    }
    
    /**
     * 根据PackageName获取已安装的Apk的签名信息
     * @param context
     * @param packageName
     * @return
     */
    public static Signature getPackageSignature(Context context, String packageName){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        Iterator<PackageInfo> it = apps.iterator();
        while(it.hasNext()){
            PackageInfo info = it.next();
            if(info.packageName.equals(packageName)){
                return info.signatures[0];
            }
        }
        return null;
    }

    /**
     * ApkFile 文件的全路径信息（包括apk文件的名称），如果是无效的Apk文件，返回值为null
     * @param context
     * @param apkFile
     * @return
     */
    public static Signature getApkSignatureByFilePath(Context context, String apkFile){
        PackageInfo newInfo = getPackageArchiveInfo(apkFile, PackageManager.GET_ACTIVITIES | PackageManager.GET_SIGNATURES);
        if(newInfo != null){
            if(newInfo.signatures != null && newInfo.signatures.length >0){
                return newInfo.signatures[0];
            }
        }
        return null;
    }

    /**
     * 根据文件路径获取未安装的apk的签名信息 ，通过反射方式实现
     * @param archiveFilePath
     * @param flags
     * @return
     */
    private static PackageInfo getPackageArchiveInfo(String archiveFilePath, int flags){
        // Workaround for https://code.google.com/p/android/issues/detail?id=9151#c8
        //必须使用反射机制才能获取未安装apk的签名信息
        final String PACKAGEPARSER_CLASS_NAME = "android.content.pm.PackageParser";
        final String PARSEPACKAGE_METHOD_NAME = "parsePackage";
        final String SUBCLASS_PACKAGE_CLASS_NAME = "Package";
        final String COLLECTCERTIFICATES_METHOD_NAME = "collectCertificates";
        final String GENERATEPACKAGEINFO_METHOD_NAME = "generatePackageInfo";
        
        try{
            //根据反射获取隐藏类
            Class<?> packageParserClass = Class.forName(PACKAGEPARSER_CLASS_NAME);
            //遍历类的子类集，找到Package内部类
            Class<?>[] innerClasses = packageParserClass.getDeclaredClasses();
            Class<?> packageParserPackageClass = null;
            for (Class<?> innerClass : innerClasses){
                if (0 == innerClass.getName().compareTo(PACKAGEPARSER_CLASS_NAME+"$"+SUBCLASS_PACKAGE_CLASS_NAME)){
                    packageParserPackageClass = innerClass;
                    break;
                }
            }
            //获取类的构造方法
            Constructor<?> packageParserConstructor = packageParserClass.getConstructor(String.class);
            Method parsePackageMethod = packageParserClass.getDeclaredMethod(
                    PARSEPACKAGE_METHOD_NAME, File.class, String.class, DisplayMetrics.class, int.class);
            //获取名为"collectCertificates"的方法
            Method collectCertificatesMethod = packageParserClass.getDeclaredMethod(
                    COLLECTCERTIFICATES_METHOD_NAME, packageParserPackageClass, int.class);
            //获取名为"generatePackageInfo"的方法
            Method generatePackageInfoMethod = packageParserClass.getDeclaredMethod(
                    GENERATEPACKAGEINFO_METHOD_NAME, packageParserPackageClass, int[].class, int.class, long.class, long.class);
            packageParserConstructor.setAccessible(true);
            parsePackageMethod.setAccessible(true);
            collectCertificatesMethod.setAccessible(true);
            generatePackageInfoMethod.setAccessible(true);
            //调用构造方法，构造类实例
            Object packageParser = packageParserConstructor.newInstance(archiveFilePath);
    
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            
            final File sourceFile = new File(archiveFilePath);
            //调用parseMethod方法，获取Package的实例
            Object pkg = parsePackageMethod.invoke(
                    packageParser,
                    sourceFile,
                    archiveFilePath,
                    metrics,
                    0);
            if (pkg == null){
                return null;
            }
            //调用collectCertificates方法，收集证书
            if ((flags & android.content.pm.PackageManager.GET_SIGNATURES) != 0){
                collectCertificatesMethod.invoke(packageParser, pkg, 0);
            }
            //最后调用generatePackageInfo方法，得到签名信息
            return (PackageInfo)generatePackageInfoMethod.invoke(null, pkg, null, flags, 0, 0);
        }
        catch (Exception e) {
            L.e("Signature Monitor", "android.content.pm.PackageParser reflection failed: " + e.toString());
        }

        return null;
    }
}