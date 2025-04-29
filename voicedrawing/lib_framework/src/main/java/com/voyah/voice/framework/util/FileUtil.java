package com.voyah.voice.framework.util;


import android.os.StatFs;
import android.util.Log;

import java.io.File;

/**
 * author : jie wang
 * date : 2025/2/27 14:56
 * description :
 */
public class FileUtil {

    public static void updateLocalSpace(String path) {
        if (path == null || path.isEmpty()) {
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            Log.w("FileUtil", "updateLocalSpace, path:" + path + "not found!");
            return;
        }
        StatFs sf = new StatFs(path);
        long countLong = sf.getBlockCountLong();
        long sizeLong = sf.getBlockSizeLong();
        long freeLong = sf.getFreeBlocksLong();
        long availableBlocksLong = sf.getAvailableBlocksLong();
        long freeBytes = sf.getFreeBytes();
        long total = sf.getTotalBytes();

        Log.i("FileUtil", "count Long=" + countLong
                + "size Long=" + sizeLong
                + "Free Long=" + freeLong
                + "Available Blocks Long=" + availableBlocksLong
                + "-----Free Bytes=" + freeBytes
                + "-----Total=" + total
    );
        long AvailableBlocksLong = availableBlocksLong * sizeLong;
        long Total = file.getTotalSpace();
        long Free = file.getFreeSpace();
        long Used = Total - Free;
        Log.i("FileUtil", "updateLocalSpace: total = " + Total + " free = " + Free + " used = " + Used);
        Log.i("FileUtil", "updateLocalSpace: AvailableBlocksLong = " + AvailableBlocksLong);
    }

    public static int getAvailableSpace(String path) {
        if (path == null || path.isEmpty()) {
            return -1;
        }
        File file = new File(path);
        if (!file.exists()) {
            Log.w("FileUtil", "getAvailableSpace: path:" + path + "not found!");
            return -1;
        }
        StatFs sf = new StatFs(path);
        long countLong = sf.getBlockCountLong();
        long sizeLong = sf.getBlockSizeLong();
        long freeLong = sf.getFreeBlocksLong();
        long availableBlocksLong = sf.getAvailableBlocksLong();
        long freeBytes = sf.getFreeBytes();
        long total = sf.getTotalBytes();

        Log.i("FileUtil", "getAvailableSpace: count Long=" + countLong
                + "\nsize Long=" + sizeLong
                + "\nFree Long=" + freeLong
                + "\nAvailable Blocks Long=" + availableBlocksLong
                + "\nFree Bytes=" + freeBytes
                + "\nTotal=" + total
        );
        long AvailableBlocksLong = availableBlocksLong * sizeLong;
        long Total = file.getTotalSpace();
        long Free = file.getFreeSpace();
        long Used = Total - Free;
        int availableSpace = (int) (AvailableBlocksLong / 1024 / 1024);
        Log.i("FileUtil", "getAvailableSpace: total = " + Total + " free = " + Free + " used = " + Used);
        Log.i("FileUtil", "getAvailableSpace: AvailableBlocksLong = " + AvailableBlocksLong);
        Log.i("FileUtil", "getAvailableSpace: availableSpace = " + availableSpace);

        return availableSpace;
    }
}
