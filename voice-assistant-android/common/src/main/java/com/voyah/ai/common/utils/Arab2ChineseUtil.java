package com.voyah.ai.common.utils;

public class Arab2ChineseUtil {

    private static final char[] CN_NUMBERS = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};

    /**
     * 将整型转换为中文数字
     */
    public static String convert(int num) {
        String numStr = String.valueOf(num);
        char[] chars = numStr.toCharArray();
        return convert(chars);
    }

    /**
     * 将浮点型转换为中文数字
     */
    public static String convert(float num) {
        String numStr = String.valueOf(num);
        String[] split = numStr.split("\\.");
        char[] chars1 = split[0].toCharArray();
        char[] chars2 = split[1].toCharArray();
        StringBuilder sb = new StringBuilder();
        String part1 = convert(chars1);
        if ("".equals(part1)) {
            part1 = "零";
        }
        String part2 = convertOnly(chars2);
        sb.append(part1).append("点").append(part2);
        return sb.toString();
    }

    public static String convertOnly(char[] chars) {
        StringBuilder sb = new StringBuilder();
        for (char ch : chars) {
            int val = Integer.parseInt(String.valueOf(ch));
            sb.append(CN_NUMBERS[val]);
        }
        return sb.toString();
    }

    private static String convert(char[] chars) {
        StringBuilder sb = new StringBuilder();
        int length = chars.length;
        int val;
        int zeroCount = 0;
        for (int i = 0; i < length; i++) {
            val = Integer.parseInt(String.valueOf(chars[i]));
            if (0 == val) {
                zeroCount++;
                continue;
            }
            if (zeroCount != 0) {
                sb.append("零");
                zeroCount = 0;
            }
            sb.append(CN_NUMBERS[val]);
            switch (length - 1 - i) {
                case 3:
                    sb.append("千");
                    break;
                case 2:
                    sb.append("百");
                    break;
                case 1:
                    sb.append("十");
                    break;
            }
        }

        //一十一转换成十一
        String cnText = sb.toString();

        if ("".equals(cnText)) {
            cnText = "零";
        } else if (cnText.startsWith("一十")) {
            cnText = cnText.substring(1);
        } else if (cnText.startsWith("二万")) {
            cnText = "两万";
        } else if (cnText.startsWith("二千")) {
            cnText = "两千";
        } else if (cnText.startsWith("二百")) {
            cnText = "两百";
        }
        return cnText;
    }
}
