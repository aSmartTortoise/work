package com.voyah.vcos.ttsservices.utils;

import android.content.Context;

import com.voyah.vcos.ttsservices.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author:lcy
 * @data:2024/9/24
 **/
public class FileUtils {

    public static void fileCreator(String filePath) {
        File file = new File(filePath);

        // 检查文件是否存在，如果不存在则创建
        if (!file.exists()) {
            try {
                // 创建文件
                if (file.createNewFile()) {
                    System.out.println("文件已创建: " + file.getName());
                } else {
                    System.out.println("文件创建失败");
                }
            } catch (IOException e) {
                System.out.println("创建文件时发生错误: " + e.getMessage());
            }
        } else {
            System.out.println("文件已存在: " + file.getName());
        }
    }


    public static void fileChecker(String filePath) {
        // 创建File对象
        File file = new File(filePath);

        // 检查文件是否存在
        if (!file.exists()) {
            // 文件不存在，检查父目录是否存在
            if (!file.getParentFile().exists()) {
                // 父目录不存在，创建父目录
                if (!file.getParentFile().mkdirs()) {
                    // 创建父目录失败
                    System.out.println("无法创建目录: " + file.getParentFile());
                    return;
                }
            }

            // 尝试创建文件
            try {
                if (file.createNewFile()) {
                    System.out.println("文件已创建: " + file.getPath());
                } else {
                    System.out.println("文件创建失败: " + file.getPath());
                }
            } catch (IOException e) {
                // 处理异常
                System.out.println("创建文件时发生错误: " + e.getMessage());
            }
        } else {
            // 文件已存在
            System.out.println("文件已存在: " + file.getPath());
        }
    }

    public static void directoryCreator(String directoryPath) {
        // 创建File对象
        File directory = new File(directoryPath);

        // 检查文件夹是否存在，如果不存在则创建
        if (!directory.exists()) {
            // 创建文件夹
            if (directory.mkdirs()) {
                System.out.println("文件夹已创建: " + directory.getAbsolutePath());
            } else {
                System.out.println("文件夹创建失败");
            }
        } else {
            System.out.println("文件夹已存在: " + directory.getAbsolutePath());
        }
    }

    public static HashMap<String, String> readLocalAudioFiles() {
        // 指定本地路径
        String directoryPath = Constant.Path.VOICE_LOCAL_AUDIO_PATH;
        // 创建File对象
        File directory = new File(directoryPath);
        if (!directory.exists())
            return null;
        HashMap<String, String> fileNameMap = new HashMap<>();
        // 确保路径存在并且是一个目录
        if (directory.exists() && directory.isDirectory()) {
            // 获取目录中的所有文件和文件夹
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    // 获取文件名（不包含路径）
                    String fileName = file.getName();

                    // 将文件名作为键和值存储到HashMap中
                    fileNameMap.put(fileName, fileName);
                }
            }
            LogUtils.d("FileUtils", "readLocalAudioFiles size:" + fileNameMap.size());

            // 打印HashMap内容
            for (String key : fileNameMap.keySet()) {
                System.out.println("File Name: " + key + " - Value: " + fileNameMap.get(key));
            }

            return fileNameMap;
        } else {
            System.out.println("The path specified does not exist or is not a directory.");
            return null;
        }
    }

    public static JSONObject readJsonFromAsset(Context context, String fileName) {
        JSONObject jsonObject = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            jsonObject = new JSONObject(json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    public static HashMap<String, String> stringMapToHashMap(String mapString) {
        JSONObject jsonObject;
        HashMap<String, String> hashMap = null;
        try {
            jsonObject = new JSONObject(mapString);
            hashMap = new HashMap<>();
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();
                String value = (String) jsonObject.get(key);
//                LogUtils.d("FileUtils", "key:" + key + " ,value:" + value);
                hashMap.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return hashMap;
    }

}
