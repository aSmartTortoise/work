package com.voyah.ai.engineer.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;

import com.blankj.utilcode.util.CloseUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.concurrent.CopyOnWriteArrayList;


public class StorageUtil {
    private static UsbDiskReceiver receiver;
    private static final CopyOnWriteArrayList<UsbListener> mUsbListeners = new CopyOnWriteArrayList<>();
    private static long mCopiedBytes = 0;

    private StorageUtil() {
    }

    /**
     * 注册U盘插拔事件监听
     */
    public static void registerUsbListener(UsbListener listener) {
        LogUtils.d("registerUsbListener() called with: listener = [" + listener + "]");
        if (mUsbListeners.size() == 0) {
            registerUDiskReceiver();
        }
        mUsbListeners.add(listener);
    }

    /**
     * 反注册U盘插拔事件监听
     */
    public static void unregisterUsbListener(UsbListener listener) {
        LogUtils.d("unregisterUsbListener() called with: listener = [" + listener + "]");
        mUsbListeners.remove(listener);
        if (mUsbListeners.size() == 0 && receiver != null) {
            Utils.getApp().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    /**
     * 获取U盘路径
     *
     * @return
     */
    public static String getUDiskPath() {
        StorageManager storageManager = (StorageManager) Utils.getApp().getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] storageVolumes;
        try {
            Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
            storageVolumes = (StorageVolume[]) getVolumeList.invoke(storageManager);
            for (StorageVolume storageVolume : storageVolumes) {
                Method getPath = StorageVolume.class.getMethod("getPath");
                String path = (String) getPath.invoke(storageVolume);
                LogUtils.d("getUDiskPath ====path==" + path);
                if (path != null && path.startsWith("/storage/") && !path.startsWith("/storage/emulated/")
                        && !path.startsWith("/storage/self")) {
                    return path;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isUsbConnected() {
        return !TextUtils.isEmpty(getUDiskPath());
    }

    private static void registerUDiskReceiver() {
        LogUtils.w("registerUDiskReceiver()");
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        Utils.getApp().registerReceiver(receiver = new UsbDiskReceiver(), filter);
    }

    private static class UsbDiskReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                LogUtils.d("usb device detached");
                for (UsbListener listener : mUsbListeners) {
                    listener.onUDiskRemove();
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                LogUtils.d("usb device attached");
                ThreadUtils.getMainHandler().postDelayed(() -> {
                    String uDiskPath = StorageUtil.getUDiskPath();
                    for (UsbListener listener : mUsbListeners) {
                        listener.onUDiskMount(uDiskPath);
                    }
                }, 3000);
            }
        }
    }


    /**
     * 拷贝文件
     */
    public static boolean copyFile(String sourceFile, String destFile, CopyProgressCallback callback, long totalSize) {
        File sourceFileObj = new File(sourceFile);
        File destFileObj = new File(destFile);

        if (!sourceFileObj.exists()) {
            LogUtils.e("源文件不存在：" + sourceFile);
            return false;
        }

        if (destFileObj.exists()) {
            destFileObj.delete();
        }

        try (FileInputStream fis = new FileInputStream(sourceFileObj);
             FileOutputStream fos = new FileOutputStream(destFileObj);
             FileChannel fileChannel = fos.getChannel()) {

            byte[] buffer = new byte[1024 * 20];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
                mCopiedBytes += length;
                if (callback != null) {
                    callback.onProgress(mCopiedBytes, totalSize);
                }
            }
            // 强制将更改写入磁盘
            fileChannel.force(true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 拷贝文件
     */
    public static boolean copyFile(String sourceFile, String destFile) {
        return copyFile(sourceFile, destFile, null, 0);
    }

    /**
     * 拷贝整个文件夹
     */
    public static boolean copyDirectory(String sourceDir, String destDir, long totalSize,
                                        long copiedBytes, CopyProgressCallback callback) {
        File sourceDirObj = new File(sourceDir);
        File destDirObj = new File(destDir);
        mCopiedBytes = copiedBytes;
        if (!sourceDirObj.exists() || !sourceDirObj.isDirectory()) {
            LogUtils.e("源文件夹不存在或不是一个文件夹：" + sourceDir);
            return false;
        }

        if (!createOrExistsDir(destDirObj)) {
            LogUtils.e("无法创建目标文件夹：" + destDir);
            return false;
        }

        File[] files = sourceDirObj.listFiles();
        if (files != null) {
            for (File file : files) {
                String destFilePath = destDir + File.separator + file.getName();
                if (file.isDirectory()) {
                    // 递归拷贝子文件夹
                    copyDirectory(file.getAbsolutePath(), destFilePath, totalSize, mCopiedBytes, callback);
                } else {
                    // 拷贝文件
                    copyFile(file.getAbsolutePath(), destFilePath, callback, totalSize);
                }
            }
        }
        return true;
    }

    public static long calculateDirectorySize(File dir) {
        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    size += calculateDirectorySize(file);
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }


    public static boolean writeFileFromBytesByStream(String filePath, byte[] bytes, boolean append) {
        File file = new File(filePath);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return writeFileFromIS(file, inputStream, append);
    }

    public static boolean writeFileFromIS(File file, InputStream is, boolean append) {
        OutputStream os = null;
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file, append);
            os = new BufferedOutputStream(fos, 1024);
            byte[] data = new byte[1024];
            for (int len; (len = is.read(data)) != -1; ) {
                os.write(data, 0, len);
            }
            fos.getFD().sync();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            CloseUtils.closeIOQuietly(is, os);
        }
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 删除整个目录
     */
    public static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    /**
     * USB 操作监听
     */
    public interface UsbListener {
        void onUDiskMount(String path);
        void onUDiskRemove();
    }

    /**
     * 拷贝进度
     */
    public interface CopyProgressCallback {
        void onProgress(long copiedBytes, long totalBytes);
    }
}
