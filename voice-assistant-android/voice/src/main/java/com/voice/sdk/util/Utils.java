package com.voice.sdk.util;

import android.text.TextUtils;

import java.security.SecureRandom;


/**
 * @author:lcy
 * @data:2024/1/31
 **/
public class Utils {
    /**
     * 字符串首字母转大写
     *
     * @param letter
     * @return
     */
    public static String upperFirstLatter(String letter) {
        if (TextUtils.isEmpty(letter)) {
            return letter;
        }
        char[] chars = letter.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * 判断一个字符串是否是数字。
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str != null && !"".equals(str.trim()))
            return str.matches("^[0-9]*$");
        else
            return false;
    }

    public static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(Character.valueOf((char) (random.nextInt(26) + 'A')));
        }
        return sb.toString();
    }

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     *
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    public static int getStringLength(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }
}
