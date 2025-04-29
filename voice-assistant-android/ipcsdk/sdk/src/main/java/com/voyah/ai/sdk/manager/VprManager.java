package com.voyah.ai.sdk.manager;

import static com.voyah.ai.sdk.DhSpeechSDK.assistantConnector;

import android.util.Log;

import com.voyah.ai.sdk.bean.DhDirection;
import com.voyah.ai.sdk.bean.UserVprInfo;
import com.voyah.ai.sdk.listener.IVprResultListener;

import java.util.List;

/**
 * 声纹接口管理
 */
public class VprManager {

    private static final String TAG = VprManager.class.getSimpleName();

    /**
     * 开始注册用户声纹
     *
     * @param direction 音区方位
     * @param listener  监听器
     */
    public static String startRegisterVpr(@DhDirection int direction, IVprResultListener listener) {
        Log.d(TAG, "startRegisterVpr() called with: direction = [" + direction + "], listener = [" + listener + "]");
        if (assistantConnector != null) {
            return assistantConnector.startRegisterVpr(direction, listener);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return null;
    }

    /**
     * 重新注册已有的用户声纹
     *
     * @param vprId     id值
     * @param direction 音区方位
     */
    public static String startRegisterVprWithId(String vprId, @DhDirection int direction, IVprResultListener listener) {
        Log.d(TAG, "startRegisterVprWithVprId() called with: vprId = [" + vprId + "], direction = [" + direction + "], listener = [" + listener + "]");
        if (assistantConnector != null) {
            return assistantConnector.startRegisterVprWithId(vprId, direction, listener);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return null;
    }

    /**
     * 结束注册用户声纹
     *
     * @param vprId id值
     * @param errCode 错误码
     */
    public static void stopRegisterVpr(String vprId, int errCode) {
        Log.d(TAG, "stopRegisterVpr() called with: vprId = [" + vprId + "], errCode = [" + errCode + "]");
        if (assistantConnector != null) {
            assistantConnector.stopRegisterVpr(vprId, errCode);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 开始录制声纹
     *
     * @param vprId id值
     * @param text  录制文本
     */
    public static void startRecordingVpr(String vprId, String text) {
        Log.d(TAG, "startRecordingVpr() called with: vprId = [" + vprId + "], text = [" + text + "]");
        if (assistantConnector != null) {
            assistantConnector.startRecordingVpr(vprId, text);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 结束录制声纹
     *
     * @param vprId   id值
     * @param text    录制文本
     * @param errCode 错误码
     */
    public static void stopRecordingVpr(String vprId, String text, int errCode) {
        Log.d(TAG, "stopRecordingVpr() called with: vprId = [" + vprId + "], text = [" + text + "], errCode = [" + errCode + "]");
        if (assistantConnector != null) {
            assistantConnector.stopRecordingVpr(vprId, text, errCode);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 获取用户声纹
     *
     * @param vprId 声紋id
     */
    public static UserVprInfo getUserVprInfo(String vprId) {
        if (assistantConnector != null) {
            return assistantConnector.getUserVprInfo(vprId);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return null;
    }

    /**
     * 保存用户声纹信息
     *
     * @param info 声紋信息
     */
    public static int saveUserVprInfo(UserVprInfo info) {
        Log.d(TAG, "saveUserVprInfo() called with: info = [" + info + "]");
        if (assistantConnector != null) {
            return assistantConnector.saveUserVprInfo(info);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return -1;
    }


    /**
     * 删除声纹
     *
     * @param vprId 声纹id
     */
    public static int deleteUserVpr(String vprId) {
        Log.d(TAG, "deleteUserVpr() called with: vprId = [" + vprId + "]");
        if (assistantConnector != null) {
            return assistantConnector.deleteUserVpr(vprId);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return -1;
    }


    /**
     * 获取最多可注册的声纹数量
     */
    public static int getMaxSupportedVprNum() {
        if (assistantConnector != null) {
            return assistantConnector.getMaxSupportedVprNum();
        } else {
            return -1;
        }
    }


    /**
     * 获取已注册的所有声纹信息
     */
    public static List<UserVprInfo> getRegisteredVprList() {
        if (assistantConnector != null) {
            return assistantConnector.getRegisteredVprList();
        } else {
            return null;
        }
    }
}