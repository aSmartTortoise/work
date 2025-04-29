package com.voyah.ai.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * @author:lcy
 * @data:2024/3/4
 **/
public class PinyinUtils {
    public static String pinyin(String chinese) {
        StringBuilder builder = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            if (!isChinese(c)) {
                builder.append(c);
            } else {
                try {
                    String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);
                    if (pinyins != null && pinyins.length > 0) {
                        builder.append(pinyins[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

//    public static String pinyin1(String chinese) {
//        System.out.println("拼音结果：" + Pinyin.toPinyin(chinese, ""));
//        return Pinyin.toPinyin(chinese, "");
//    }

    private static boolean isChinese(char a) {
        return (int) a >= 19968;
    }
}
