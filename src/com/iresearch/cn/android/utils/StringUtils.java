package com.iresearch.cn.android.utils;

public class StringUtils {

	private StringUtils() {
	}

	/**
     * 检查字符串是否存在值
     * 
     * @param str 待检验的字符串
     * @return 当 str 不为 null 或 "" 就返回 true，否则false
     */
	public static boolean isEmpty(String str) {
		return (str == null || str.length() == 0 || "null".equals(str.toLowerCase()));
	}
}
