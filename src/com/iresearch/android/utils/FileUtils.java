package com.iresearch.android.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @file FileUtils.java
 * @create 2013-7-17 下午05:19:08
 * @author Jacky.Lee
 * @description TODO 封转常用的文件操作
 */
public class FileUtils {
    
    /**
     * 创建文件
     * 
     * @param folderPath
     * @param fileName
     * @return
     */
    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName + fileName);
    }
    
    /**
     * 重命名
     * 
     * @param oldName
     * @param newName
     * @return
     */
    public static boolean reNamePath(String oldName, String newName) {
        File f = new File(oldName);
        return f.renameTo(new File(newName));
    }
    
    /**
     * 根据文件绝对路径获取文件名
     * 
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }
    
    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     * 
     * @param filePath
     * @return
     */
    public static String getFileNameNoExtension(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf('.');
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1, point);
    }
    
    /**
     * 获取文件扩展名
     * 
     * @param fileName
     * @return
     */
    public static String getFileExtension(String fileName) {
        if (StringUtils.isEmpty(fileName))
            return "";

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }
    
    /**
     * 列出root目录下所有子目录
     * 
     * @param path
     * @return 绝对路径
     */
    public static List<String> listPath(String root) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        // 过滤掉以.开始的文件夹
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isDirectory() && !f.getName().startsWith(".")) {
                    allDir.add(f.getAbsolutePath());
                }
            }
        }
        return allDir;
    }
    
    /**
     * 获取一个文件夹下的所有文件
     * @param root
     * @return
     */
    public static List<File> listPathFiles(String root) {
        List<File> allDir = new ArrayList<File>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        File[] files = path.listFiles();
        for (File f : files) {
            if (f.isFile())
                allDir.add(f);
            else 
                listPath(f.getAbsolutePath());
        }
        return allDir;
    }
    
	/**
	 * 删除除指定文件之外的所有文件
	 * 
	 * @param appCachePath 被删除的文件夹目录
	 * @param excludedFileName 文件夹第一级目录下，不想被删除的文件的名称
	 */
	public static void cleanCacheFile(final String appCachePath,
			final String excludedFileName) {
		try {
			File file = new File(appCachePath.trim());
			if (null != file && file.isDirectory()) {
				for (File file1 : file.listFiles()) {
					if (file1.isDirectory()) {
						deleteAllFile(file1.getAbsolutePath());
					} else {
						if (!excludedFileName.equals(file1.getName())) {
							file1.delete();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除所有文件
	 */
	public static void deleteAllFile(String path) {
		File file = new File(path);
		if (null != file && file.exists()) {
			if (file.isDirectory()) {
				File[] fileList = file.listFiles();
				for (int i = 0; i < fileList.length; i++) {
					String filePath = fileList[i].getPath();
					deleteAllFile(filePath);
				}
			} else if (file.isFile()) {
				file.delete();
			}
		}
	}

	/**
     * 获取文件夹或者文件大小，递归循环
     * 
     * @param String path 路径或者文件
     * @return String 文件的大小，以BKMG来计量
     */
    public static String getPathSize(String path) {
        String flieSizesString = "";
        File file = new File(path.trim());
        long fileSizes = 0;
        if (null != file && file.exists()) {
            if (file.isDirectory()) { // 如果路径是文件夹的时候
                fileSizes = getFileFolderTotalSize(file);
            } else if (file.isFile()) {
                fileSizes = file.length();
            }
        }
        flieSizesString = formatFileSizeToString(fileSizes);
        return flieSizesString;
    }

    /**
     * 获取文件夹或者文件大小，循环迭代
     * 
     * @param String path 路径或者文件
     * @return String 文件的大小，以BKMG来计量
     */
	private static long getFileFolderTotalSize(File fileDir) {
		long totalSize = 0;
		File fileList[] = fileDir.listFiles();
		for (int fileIndex = 0; fileIndex < fileList.length; fileIndex++) {
			if (fileList[fileIndex].isDirectory()) {
				totalSize = totalSize
						+ getFileFolderTotalSize(fileList[fileIndex]);
			} else {
				totalSize = totalSize + fileList[fileIndex].length();
			}
		}
		return totalSize;
	}

	/**
	 * 格式化文件长度大小
	 */
	private static String formatFileSizeToString(long fileSize) {// 转换文件大小
		String fileSizeString = "";
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		if (fileSize == 0) {
			fileSizeString = "0KB";
		} else if (fileSize < 1024) {
			fileSizeString = decimalFormat.format((double) fileSize) + "B";
		} else if (fileSize < (1 * 1024 * 1024)) {
			fileSizeString = decimalFormat.format((double) fileSize / 1024) + "KB";
		} else if (fileSize < (1 * 1024 * 1024 * 1024)) {
			fileSizeString = decimalFormat.format((double) fileSize / (1 * 1024 * 1024)) + "MB";
		} else {
			fileSizeString = decimalFormat.format((double) fileSize / (1 * 1024 * 1024 * 1024)) + "GB";
		}
		return fileSizeString;
	}

}
