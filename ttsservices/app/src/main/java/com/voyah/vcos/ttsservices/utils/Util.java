package com.voyah.vcos.ttsservices.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.voyah.vcos.ttsservices.info.TtsBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author:lcy
 * @data:2024/3/12
 **/
public class Util {
    private static final String TAG = "Util";

    private static final String VOYAH_DEVICE_NAME = "Qualcomm SA8295 Cockpit";

    /**
     * 生成随机数字串
     *
     * @param length 数字串长度
     * @return
     */
    public static String generateRandom(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return new String(digits);
    }

    /**
     * 通过sharedUserId来判断当前是车机还是模拟器
     *
     * @return true是车机，false是模拟器
     */
    public static boolean vehicleSimulatorJudgment() {
        return VOYAH_DEVICE_NAME.equals(getDeviceName());
//        String sharedUserId = "";
//        try {
//            PackageManager pm = context.getPackageManager();
//            String packageName = context.getPackageName();
//            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SHARED_LIBRARY_FILES);
//            if (packageInfo.sharedUserId != null) {
//                // 应用设置了sharedUserId
//                sharedUserId = packageInfo.sharedUserId;
//                LogUtils.e(TAG, sharedUserId);
//                // 可以根据sharedUserId进行相应的操作
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        if ("android.uid.system".equals(sharedUserId)) {
//            Log.e(TAG, "sharedUserId = android.uid.system");
//            return true;
//        } else {
//            Log.e(TAG, "sharedUserId = null");
//            return false;
//        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceName;
        if (model.startsWith(manufacturer)) {
            deviceName = capitalize(model);
        } else {
            deviceName = capitalize(manufacturer) + " " + model;
        }
        LogUtils.i(TAG, "deviceName:" + deviceName);
        return deviceName;
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static void unzipFile(String zipFilePath, String destDirectory) {
//        File zipFile = new File(zipFilePath);
        File destDir = new File(destDirectory);
//        if (!zipFile.exists() || destDir.exists()) {
//            LogUtils.i(TAG, "source is error zipFile is " + zipFile.exists() + " ,destDir is " + destDir.exists());
//            return;
//        }
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    if (!entry.getName().startsWith("__MACOSX")) {
                        extractFile(zipIn, filePath);
                    }
                } else {
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = zipIn.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
        }
    }

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

    public static boolean isConnected(Context context, boolean isCar) {
        boolean result = false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = null;
        if (manager != null) {
            network = manager.getActiveNetwork();
            NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(network);
            if (networkCapabilities != null) {
                result = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                        && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
            Log.i(TAG, "isConnected: [" + network + "] ------ " + result);
        } else {
            Log.w(TAG, "isConnected: ------ " + result);
        }
        return isCar ? result : (null != network || result);
    }

    public static String replaceClosestSymbolWithPeriod(String text) {
        // 检查字符串长度是否超过200
        if (text.length() <= 200) {
            return text;
        }

        // 指定要检查的标点符号
        String punctuation = ",。!?！？；;";

        // 检查是否包含指定的标点符号
        boolean containsPunctuation = false;
        for (char symbol : punctuation.toCharArray()) {
            if (text.indexOf(symbol) != -1) {
                containsPunctuation = true;
                break;
            }
        }

        // 如果包含指定的标点符号，则不进行替换
        if (containsPunctuation) {
            return text;
        }

        // 找到最靠近中间位置的符号
        int middleIndex = text.length() / 2;
        int closestSymbolIndex = -1;
        int closestDistance = Integer.MAX_VALUE;

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            // 检查是否为非字母数字字符
            if (!Character.isLetterOrDigit(currentChar)) {
                int distance = Math.abs(i - middleIndex);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestSymbolIndex = i;
                }
            }
        }

        // 如果找到了符号，则替换它
        if (closestSymbolIndex != -1) {
            return text.substring(0, closestSymbolIndex) + '。' + text.substring(closestSymbolIndex + 1);
        }

        // 如果没有找到符号，则直接返回原文本
        return text;
    }

    public static void copyQueue(LinkedList<TtsBean> source, LinkedList<TtsBean> destination) {
        // 使用迭代器遍历原始队列
        for (TtsBean ttsBean : source) {
            // 将每个元素添加到目标队列中
            destination.add(ttsBean);
        }
    }
}
