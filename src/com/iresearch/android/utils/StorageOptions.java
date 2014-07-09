
package com.iresearch.android.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import android.text.TextUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.os.storage.StorageManager;
import com.iresearch.android.common.NoDuplicatesArrayList;

/**
 * @file StorageOptions.java
 * @create 2014年7月9日 下午1:37:02
 * @author Jacky.Lee
 * @description TODO
 */
public class StorageOptions {
    private static final String TMPFS = "tmpfs";

    // 存储卡当前的扫描状态
    private static final int MOUNTED_DEFAULT = 0x1000;
    private static final int MOUNTED_RUNNING = 0x1001;
    private static final int MOUNTED_FINISHED = 0x1002;
    
    private volatile int mStatus;
    private List<String> mRoots;
    
    private static class SingletonHolder {
        final static StorageOptions INSTANCE = new StorageOptions();
    }
    
    public static StorageOptions getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    private StorageOptions() {
        mStatus=MOUNTED_DEFAULT;
        mRoots=new NoDuplicatesArrayList<String>();
    }
    
    /**
     * 初始化外部存储根路径
     * @param context
     */
    public synchronized void init(final Context context) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                initExternalSdRoot(context);
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }
    
    /**
     * 初始化外部存储根路径
     * 
     * @param context
     */
    private void initExternalSdRoot(final Context context) {
        mRoots.clear();
        mStatus=MOUNTED_RUNNING;
        // 获取内部存储卡路径
        String rootPath=getSdRoot();
        if (!TextUtils.isEmpty(rootPath)) {
            mRoots.add(rootPath);
        } 
        // 获取外部存储卡根路径
        final Context appContext=context.getApplicationContext();
        List<String> dirPath=getExternalSdRootByVolumePaths(appContext);
        if (dirPath!=null && dirPath.size()>0) {
            mRoots.addAll(dirPath);
        } else {
            dirPath=getExternalSdRootByMounted();
            mRoots.addAll(dirPath);
        }
        mStatus=MOUNTED_FINISHED;
    }
    
    /**
     * 判断初始化是否完成
     * @return  如果未初始化或在初始化中，返回false；初始化完成返回true
     */
    public boolean isInitFinish() {
        return mStatus == MOUNTED_RUNNING;
    }
    
    /**
     * 获取所有外部存储根路径的集合
     * 
     * @return 如果未初始化或在初始化中返回null；初始化完返回路劲
     */
    public List<String> getRoots() {
        return (isInitFinish() ? new ArrayList<String>(mRoots) : null);
    }
    
    /**
     * 获取一个外部存储根路径。
     * 优先返回Environment.getExternalStorage()的路径，
     * 如果没有，则返回其他外部存储存储根路径的一个。
     * @return 返回一个可用的根路径；如果未初始化或在初始化中，或初始化完但无根路径，则返回null
     */
    public String getRoot() {
        if (isInitFinish()) {
            return (mRoots.size()>0 ? mRoots.get(0) : null);
        }
        return null;
    }
    
    /**
     * 立即获取一个外部存储根路径(无需初始化)。
     * 优先返回Environment.getExternalStorage()的路径，
     * 如果没有，则返回其他外部存储存储根路径的一个。
     * 注：不能通过此方法判断设备无外部存储根路径
     * @return 返回一个外部存储根路径；如果没有，则返回null
     */
    public String getSdRootNotInit(Context context) {
        // 优先返回getExternalStorage()获取的根路径
        String rootPath=getSdRoot();
        if (!TextUtils.isEmpty(rootPath)) {
            return rootPath;
        }
        // 然后返回通过反射获取的第一个根路径
        List<String> roots = getExternalSdRootByVolumePaths(context);
        if (roots != null && roots.size() > 0) {
            return roots.get(0);
        }
        return null;
    }
    
    
    /**
     * 通过Environment.getExternalStorage()获取内部存储根路径
     * 
     * @return 如果没有存储设备，返回null；否则返回内部存储路径
     */
    private String getSdRoot() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = Environment.getExternalStorageDirectory();
            if (file.exists() && file.canRead() && file.canWrite()) {
                return file.getPath();
            }
        }
        return null;
    }
    
    /**
     * 通过反射getVolumePath()反射，获取所有外部存储的路径。
     * 1.Android3.2及以上，StorageManager才有getVolumePath()这个方法;
     * 2.Android2.3及以上，才可以调用这个方法；否则返回null;
     * 
     * @param context
     * @return 如果是Android 2.3，返回null；否则当前返回所有外部存储的根路径
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private List<String> getExternalSdRootByVolumePaths(Context context) {
        // Android 2.3以下，没有StorageManager，所以无法执行下面的逻辑
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            return null;
        }

        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        String[] paths = (String[]) ReflectionUtils.tryInvoke(storageManager, "getVolumePaths");
        if (paths == null) {
            return null;
        }

        List<String> results = new ArrayList<String>();
        for (String path : paths) {
            File file = new File(path);
            if (file.exists() && file.canRead() && file.canWrite()) {
                results.add(path);
            }
        }
        return results;
    }

    /**
     * 通过Linux的df和mount命令，经过筛选排除，获取所有外部存储的路径。
     * 注：由于一些山寨手机，无法通过getExternalStorage()等方法获取外部存储的路径
     * 
     * @return 返回所有外部存储的根路径
     */
    private List<String> getExternalSdRootByMounted() {
        Runtime runtime = Runtime.getRuntime();
        
        // 通过DF命令查看磁盘占用情况
        List<String> dfPaths = getDiskListByCommand(runtime);
        Map<String, String> devPathMap = new HashMap<String, String>();
        
        // 用mount命令去除dfPaths中的属性为tmpfs的路径，并生成devPathMap
        Process mountProcess = null;
        try {
            mountProcess = runtime.exec("mount");
            InputStream input = mountProcess.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String strLine;
            while (null != (strLine = br.readLine())) {
                if (TextUtils.isEmpty(strLine)) {
                    continue;
                }
                // 判断mount这一行是否含有df中的路径
                int indexOfDfName = getIndexOfDfNames(dfPaths, strLine);
                if (indexOfDfName == -1) {
                    continue;
                }
                // mount这一行路径为tmpfs,则去除dfPaths中该path
                if (strLine.contains(TMPFS)) {
                    dfPaths.remove(indexOfDfName);
                } else { // 否则，该path为有效的，添加进devPathMap
                    String path = dfPaths.get(indexOfDfName);
                    int index = strLine.indexOf(" ");
                    if (index != -1) {
                        String devName = strLine.substring(0, index);
                        if (!devPathMap.containsKey(devName)) {
                            devPathMap.put(devName, path);
                        } else {
                            // 如果同一设备挂载点有多个，则保留路径名短的挂载点
                            String sameDfName = devPathMap.get(devName);
                            if (path.length() < sameDfName.length()) {
                                devPathMap.put(devName, path);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (mountProcess != null) {
                    mountProcess.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 返回当前扫描结果
        return new ArrayList<String>(devPathMap.values());
    }

    /**
     * 通过DF命令来获取可用路径。 DF命令：检查文件系统的磁盘空间占用情况；m1手机，df命令第一列不是挂载点路径;
     * 
     * @param runtime
     * @return
     */
    private List<String> getDiskListByCommand(Runtime runtime) {
        List<String> dfPaths = new ArrayList<String>();
        Process dfProcess = null;
       
        try {
            dfProcess = runtime.exec("df");
            InputStream input = dfProcess.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (TextUtils.isEmpty(strLine)) {
                    continue;
                }

                // 取出df命令第一列的路径名
                String path = strLine;
                int splitIndex = strLine.indexOf(" ");
                if (splitIndex > 0) {
                    path = strLine.substring(0, splitIndex);
                }
                if (path.length() > 1) {
                    // 去除结尾异常字符
                    char c = path.charAt(path.length() - 1);
                    if (!Character.isLetterOrDigit(c) && c != '-' && c != '_') {
                        path = path.substring(0, path.length() - 1);
                    }
                    // 判断该路径是否存在并可写
                    File canW = new File(path);
                    if (canW.exists() && canW.canRead() && canW.canWrite()) {
                        if (!dfPaths.contains(path)) {
                            dfPaths.add(path);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dfProcess != null) {
                    dfProcess.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return dfPaths;
    }
    
    /**
     * 根据当前的mount命令结果的一行，找到对应的dfPaths中的索引
     * 
     * @param mountLine 当前的mount命令行
     * @return 找到则返回对应index，否则返回-1
     */
    private int getIndexOfDfNames(List<String> dfPaths, String mountLine) {
        String[] mountColumns = mountLine.split(" ");
        for (int i = 0; i < dfPaths.size(); i++) {
            String path = dfPaths.get(i);
            boolean match = false;
            for (String mountColumn : mountColumns) {
                if (mountColumn.equals(path)) {
                    match = true;
                }
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 立即获取一个App专属放置大文件的外部存储根路径
     * 格式：/[root]/Android/data/[package name]/files
     * 
     * @return 返回一个外部存储根路径；如果没有，则返回null
     */
    public File getExternalFilesDir(Context context, File externalStorageDirectory) {
        return new File(externalStorageDirectory, "Android/data/" + context.getPackageName()+ "/files");
    }

    /**
     * 立即获取一个App专属放置临时文件的外部存储根路径
     * 格式：/[root]/Android/data/[package name]/cache
     *
     * @return 返回一个外部存储根路径；如果没有，则返回null
     */
    public File getExternalCacheDir(Context context, File externalStorageDirectory) {
        return new File(externalStorageDirectory, "Android/data/" + context.getPackageName()+ "/cache");
    }
}
