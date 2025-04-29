package com.voyah.vcos.ttsservices.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author:lcy
 * @data:2024/11/9
 **/
public class MD5Util {
    private static MessageDigest md;

    public static String getMD5(String input) {
        if (TextUtils.isEmpty(input))
            return "";
        try {
            // 创建MessageDigest实例，指定使用MD5算法
            if (null == md) {
                md = MessageDigest.getInstance("MD5");
            }
            // 使用指定的字节更新摘要
            md.update(input.getBytes());
            // 完成哈希计算并返回结果
            byte[] digest = md.digest();
            // 将字节转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // 当JVM不支持MD5算法时，会抛出此异常
            throw new RuntimeException("MD5 can not exe", e);
        }
    }
}
