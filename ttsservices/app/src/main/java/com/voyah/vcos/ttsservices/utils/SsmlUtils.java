package com.voyah.vcos.ttsservices.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author:lcy
 * @data:2024/11/19
 **/
public class SsmlUtils {
    public static String switchStyle(String emotion, String speaker) {
        if (!TextUtils.isEmpty(emotion)) {
            switch (emotion.toLowerCase()) {
                case "pat":
                    return "Assistant";
//                    return "Fearful";
                case "happy":
//                    String styleHappy = "Cheerful";
//                    if (TextUtils.equals(speaker, "zh-CN-XiaochenNeural") || TextUtils.equals(speaker, "zh-CN-YunyiMultilingualNeural"))
//                        styleHappy = "Default";
//                    return styleHappy;
                    return "Cheerful";
                case "awkward":
                case "fail":
                    String style = "Default";
                    if (TextUtils.equals(speaker, "zh-CN-XiaoxiaoNeural"))
                        style = "affectionate";
                    else if (TextUtils.equals(speaker, "zh-CN-YunxiNeural"))
                        style = "embarrassed";
                    return style;
                case "sad":
                case "entry":
                case "general":
                    return "Chat";
            }
        }
        return "Default";
    }

    //微软离线发音临时处理方案(微软离线模型更新慢)
    public static String replaceSpecialString(String originString) {
        if (TextUtils.isEmpty(originString))
            return "";
        if (!TextUtils.equals("OK", originString))
            originString = ensureEndsWithPunctuation(originString);
        originString = replaceString(originString, "ARHUD", "A-R-H-U-D");
        originString = replaceString(originString, "HUDAR", "H-U-D-A-R");
        originString = replaceString(originString, "HUD", "H-U-D");
        originString = replaceString(originString, "AR", "A-R");
        originString = replaceString(originString, "ETC", "E-T-C");
        originString = replaceString(originString, "KTV", "K-T-V");
        originString = replaceString(originString, "AI", "“AI”");
        originString = replaceString(originString, "OK", "“OK”");
        return originString;
    }

    public static String replaceCopySpecialString(String originString) {
        if (TextUtils.isEmpty(originString))
            return "";
        originString = replaceString(originString, "苹果翻译成英文是：Apple", "苹果翻译成英文是Apple。");
        return originString;
    }

    private static String replaceString(String originString, String val1, String val2){
        if (originString == null || val1 == null || val1.isEmpty()) {
            return originString;
        }

        // 构建正则表达式，使用正向预查和负向预查确保是完整单词
        String regex = "(?<![a-zA-Z])" + Pattern.quote(val1) + "(?![a-zA-Z])";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(originString);

        // 执行替换
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            // 保留原始字符串中的大小写
            String matchedText = matcher.group();
            if (matchedText.equalsIgnoreCase(val1)) {
                matcher.appendReplacement(result, val2);
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public static String ensureEndsWithPunctuation(String str) {
        // 正则表达式匹配任何非标点符号字符的序列，直到字符串末尾
        if (!str.matches(".*[。！？；：]")) {
            // 如果字符串不以标点符号结尾，添加一个句号
            str += "。";
        }
        return str;
    }
}
