package com.example.filter_process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * @author:lcy
 * @data:2024/1/31
 **/
public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 写入文件
     */
    public static void writeToFile(String path, String writeString,boolean append) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            fw = new FileWriter(file,append);
            bw = new BufferedWriter(fw);// 使用缓冲数据流封装输出流
            bw.write(writeString);
            if (append)
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fw) {
                    fw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != bw) {
                    bw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
