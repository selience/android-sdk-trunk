
package com.iresearch.cn.android.utils;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

/**
 * @className StringUtils
 * @create 2014年4月16日 上午10:22:34
 * @author Jacky.Lee
 * @description TODO 封装常用的字符串操作 
 */
public class StringUtils {

    private StringUtils() {
    }

    /**
     * 检查字符串是否存在值
     * 
     * @param str 待检验的字符串
     * @return 当 str 不为 null 或 "" 就返回 True，否则False
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0 || "null".equals(str.toLowerCase()));
    }

    /**
     * 对参数进行UTF-8编码，并替换特殊字符
     * 
     * @param paramDecString 待编码的参数字符串
     * @return 完成编码转换的字符串
     */
    public static String paramEncode(String decString) {
        try {
            return URLEncoder.encode(decString, "UTF-8").replace("+", "%20")
                    .replace("*", "%2A").replace("%7E", "~").replace("#", "%23");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * 将 %XX 换为原符号，并进行UTF-8反编码
     * 
     * @param paramEncString 待反编码的参数字符串
     * @return 未进行UTF-8编码和字符替换的字符串
     */
    public static String paramDecode(String encString) {
        int nCount = 0;
        for (int i = 0; i < encString.length(); i++) {
            if (encString.charAt(i) == '%') {
                i += 2;
            }
            nCount++;
        }

        byte[] sb = new byte[nCount];

        for (int i = 0, index = 0; i < encString.length(); i++) {
            if (encString.charAt(i) != '%') {
                sb[index++] = (byte) encString.charAt(i);
            } else {
                StringBuilder sChar = new StringBuilder();
                sChar.append(encString.charAt(i + 1));
                sChar.append(encString.charAt(i + 2));
                sb[index++] = Integer.valueOf(sChar.toString(), 16).byteValue();
                i += 2;
            }
        }
        String decode = "";
        try {
            decode = new String(sb, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decode;
    }
}
