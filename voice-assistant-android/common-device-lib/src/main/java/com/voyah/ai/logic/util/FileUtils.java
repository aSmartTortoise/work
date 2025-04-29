package com.voyah.ai.logic.util;

import com.voice.sdk.util.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author:lcy
 * @data:2024/1/31
 **/
public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();


    /**
     * 读取文件
     */
    public static String readTxtFile(File file) {
        if (!file.exists()) {
            return null;
        }
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            LogUtils.i(TAG, "The File isDirectory");
            return null;
        }
        return readFile(file);
    }

    /**
     * 读取文件
     */
    public static String readTxtFile(String strFilePath) {
        //打开文件
        File file = new File(strFilePath);
        if (!file.exists()) {
            return null;
        }
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            LogUtils.i(TAG, "The File isDirectory");
            return null;
        }
        return readFile(file);
    }

    public static String readFile(File file) {
        boolean firstLine = true;
        StringBuilder builder = new StringBuilder();
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(file));
            String line;
            //分行读取
            while ((line = bf.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                } else {
                    builder.append("\n");
                }
                builder.append(line);
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bf) {
                    bf.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }


    /**
     * 写入文件
     */
    public static void writeToFile(String path, String writeString) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);// 使用缓冲数据流封装输出流
            bw.write(writeString);
//            bw.newLine();
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

    public static void copyZipFile(String sourceFilePath, String destinationFolderPath) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceFilePath))) {
            File destinationFolder = new File(destinationFolderPath);
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
            }

            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                String newFilePath = destinationFolderPath + File.separator + entry.getName();
                File newFile = new File(newFilePath);

                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }

                zis.closeEntry();
                entry = zis.getNextEntry();
            }

            System.out.println("Zip file copied to " + destinationFolderPath + " successfully!");
        } catch (IOException e) {
            System.err.println("Error copying zip file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void unzipFile(String zipFilePath, String destDirectory) {
        File zipFile = new File(zipFilePath);
        File destDir = new File(destDirectory + "res");
        if (!zipFile.exists()) {
            LogUtils.i(TAG, "source is error zipFile is " + zipFile.exists() + " ,destDir is " + destDir.exists());
            return;
        }
        if (destDir.exists()) {
            destDir.delete();
        }
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

    //===================删除文件夹及其内部文件、文件夹=====================
    public static void deleteDir(String dir) {
        File file = new File(dir);
        if (file.exists()) {
            deleteDirWithFile(file);
        }
    }

    private static void deleteDirWithFile(File file) {
        if (!file.isDirectory() || !file.exists()) {
            return;
        }
        if (file.list().length > 0) {
            for (File f : file.listFiles()) {
                if (f.isFile()) {
                    f.delete();//delete all files
                } else if (f.isDirectory()) {
                    deleteDirWithFile(f);
                }
            }
        }

        file.delete();
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
            return true;
        }

        return false;
    }


    public static void createFile(String directoryPath){
        // 创建 File 对象
        File file = new File(directoryPath);

        // 使用 mkdir() 方法创建单级目录
        boolean isCreated = file.mkdir();
        if (isCreated) {
            System.out.println("目录创建成功");
        } else {
            System.out.println("目录创建失败");
        }
    }


    public static void unzip(String zipFilePath, String destDir) throws IOException {
        // 创建文件输入流
        FileInputStream fis = new FileInputStream(zipFilePath);
        // 创建ZIP输入流
        ZipInputStream zis = new ZipInputStream(fis);
        // 获取下一个ZIP条目
        ZipEntry entry = zis.getNextEntry();

        // 遍历ZIP文件中的所有条目
        while (entry != null) {
            String filePath = destDir + "/" + entry.getName();
            if (!entry.isDirectory()) {
                // 如果条目不是目录，则提取文件
                extractFile2(zis, filePath);
            } else {
                // 如果条目是目录，则创建目录
                File dir = new File(filePath);
                dir.mkdir();
            }
            // 关闭当前条目并获取下一个条目
            zis.closeEntry();
            entry = zis.getNextEntry();
        }
        // 关闭ZIP输入流和文件输入流
        zis.close();
        fis.close();
    }

    private static void extractFile2(ZipInputStream zis, String filePath) throws IOException {
        // 创建文件输出流
        FileOutputStream fos = new FileOutputStream(filePath);
        byte[] buffer = new byte[1024];
        int bytesRead;
        // 读取并写入文件内容
        while ((bytesRead = zis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        // 关闭文件输出流
        fos.close();
    }

    /**
     * 确保某个文件夹存在，不存在则创建
     * @param path
     */
    public static void ensureDir(String path) {
        File f = new File(path);
        if (!f.exists()) {
            boolean success = f.mkdirs();
            if (!success) {
                LogUtils.d(TAG, "ensureDir mkdirs failed: " + path);
            }
        }
    }
}
