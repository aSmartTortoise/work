package com.voyah.vcos.asraudiorecord.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final String TAG = "FileUtils";

    private FileUtils() {

    }

    public static List<String> readFileLineByLine(String filePath) {
        File file = new File(filePath);
        BufferedReader reader = null;
        List<String> strList = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) {
                    strList.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return strList;
    }

    public static void writeDataToFile(String filePath, byte[] data, int len) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath, true);
            fos.write(data, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void appendTextToFile(String filePath, String text) {
        File file = new File(filePath);

        // 如果文件不存在，则创建它
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // 处理异常
                e.printStackTrace();
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(text);
            bw.newLine(); // 添加一个新行
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }
    }



    public static String formatFileName(String path) {
        if (path == null)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < path.length(); i++) {
            if (!(path.charAt(i) == '\\' || path.charAt(i) == '/' || path.charAt(i) == ':' || path.charAt(i) == '*'
                    || path.charAt(i) == '?' || path.charAt(i) == '\"' || path.charAt(i) == '<' || path.charAt(i) == '>'
                    || path.charAt(i) == '|')) {
                stringBuilder.append(path.charAt(i));
            }
        }
        return stringBuilder.toString();
    }

    public static void deleteFile(String path, String mask) {
        File folder = new File(path);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().contains(mask)) {
                        boolean result = file.delete();
                        LogUtils.i(TAG, "deleteFile:" + file + ",result:" + result);
                    }
                }
            }
        }
    }
}
