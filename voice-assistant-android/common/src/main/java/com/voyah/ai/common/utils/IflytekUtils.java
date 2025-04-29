package com.voyah.ai.common.utils;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;

import org.json.JSONObject;

import java.io.File;

public class IflytekUtils {

    public static void copyResIfNeed(@NonNull Callback callback) {
        ThreadUtils.getSinglePool().execute(() -> {
            String srcPath = getResPart1Path();
            File sysVerFile = new File(srcPath + "/version.json");
            File sysActiveFile = new File(srcPath + "/Active");
            File sysLogFile = new File(srcPath + "/ISSLog");

            String targetPath = getResPart2Path();
            File sdVerFile = new File(targetPath + "/version.json");
            File sdActiveFile = new File(targetPath + "/Active");
            File sdLogFile = new File(targetPath + "/ISSLog");

            int sdResVer = getIflytekVersionCode(true);
            int systemResVer = getIflytekVersionCode(false);
            LogUtils.i("sdResVer:" + sdResVer + ",systemResVer:" + systemResVer);

            // 找不到系统资源版本
            if (systemResVer == -1) {
                callback.onFail("Can not find system version, please check res!!!");
                return;
            }

            if (systemResVer == sdResVer) { //系统中的版本等于SD卡中的版本
                LogUtils.i("systemResVer: " + systemResVer + " equal sdResVer: " + sdResVer);
                if (!sdActiveFile.exists()) {
                    FileUtils.copy(sysActiveFile, sdActiveFile);
                } else {
                    LogUtils.i(sdActiveFile.getAbsolutePath() + " exist, ignore copy!!!");
                }
                if (!sdLogFile.exists()) {
                    FileUtils.copy(sysLogFile, sdLogFile);
                } else {
                    LogUtils.i(sdLogFile.getAbsolutePath() + " exist, ignore copy!!!");
                }
            } else { //系统中的版本与SD卡中的版本不一致
                if (systemResVer < sdResVer) {
                    LogUtils.e("systemResVer:" + systemResVer + " lower than sdResVer:" + sdResVer);
                } else {
                    LogUtils.i("systemResVer: " + systemResVer + " greater than sdResVer: " + sdResVer);
                }
                if (sdVerFile.exists()) {
                    sdVerFile.delete();
                }
                FileUtils.copy(sysVerFile, sdVerFile);
                if (sdActiveFile.exists()) {
                    sdActiveFile.delete();
                }
                FileUtils.copy(sysActiveFile, sdActiveFile);
                if (sdLogFile.exists()) {
                    sdLogFile.delete();
                }
                FileUtils.copy(sysLogFile, sdLogFile);
            }
            callback.onSuccess();
        });
    }

    /**
     * 获取讯飞资源存储路径
     */
    public static String getResPart1Path() {
        return "/system/third_party/voice/res/iflytek";
    }

    /**
     * 获取讯飞资源分离部分存储路径
     */
    public static String getResPart2Path() {
        return Utils.getApp().getFilesDir().getAbsolutePath() + "/iflytek";
    }

    /**
     * 获取讯飞SDK版本号
     */
    public static int getIflytekVersionCode(boolean fromSD) {
        String verPath = (fromSD ? getResPart2Path() : getResPart1Path()) + "/version.json";
        String json = FileIOUtils.readFile2String(verPath);
        if (json == null) {
            return -1;
        } else {
            try {
                JSONObject object = new JSONObject(json);
                int code = object.getInt("versionCode");
                return code == 0 ? -1 : code;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    /**
     * 回调
     */
    public interface Callback {
        void onSuccess();

        void onFail(String errMsg);
    }

}
