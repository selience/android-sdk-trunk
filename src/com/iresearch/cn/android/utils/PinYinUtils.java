
package com.iresearch.cn.android.utils;

import java.util.ArrayList;
import static com.iresearch.cn.android.utils.HanziToPinyin.Token;

public class PinYinUtils {
    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     * 
     * @param inputString
     * @return
     */
    public static String getPingYin(String inputString) {
        ArrayList<Token> tokens = getTokens(inputString);
        if (tokens == null) {
            return inputString;
        }
        StringBuilder pinyin = new StringBuilder();
        for (Token token : tokens) {
            switch (token.type) {
                case Token.LATIN:
                case Token.UNKNOWN:
                    pinyin.append(token.source);
                    break;
                case Token.PINYIN:
                    pinyin.append(token.target);
            }
        }
        return pinyin.toString();
    }

    public static String getSortKey(String inputString) {
        ArrayList<Token> tokens = getTokens(inputString);
        if (tokens == null || tokens.size() == 0) {
            return inputString;
        }
        StringBuilder pinyin = new StringBuilder();
        for (Token token : tokens) {
            if (token.source.equals(token.target)) {
                for (int i = 0; i < token.source.length(); i++) {
                    pinyin.append(token.source.charAt(i));
                    pinyin.append(" ");
                    pinyin.append(token.source.charAt(i));
                    pinyin.append(" ");
                }
            } else {
                pinyin.append(token.source);
                pinyin.append(" ");
                pinyin.append(token.target);
                pinyin.append(" ");
            }
        }
        return pinyin.substring(0, pinyin.length() - 1);
    }

    private static ArrayList<Token> getTokens(String input) {
        return HanziToPinyin.getInstance().get(input);
    }
}
