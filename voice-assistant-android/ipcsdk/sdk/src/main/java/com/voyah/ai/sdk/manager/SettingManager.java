package com.voyah.ai.sdk.manager;

import static com.voyah.ai.sdk.DhSpeechSDK.assistantConnector;

import android.util.Log;

import androidx.annotation.NonNull;

import com.voyah.ai.sdk.bean.DhDialect;
import com.voyah.ai.sdk.bean.DhDirection;
import com.voyah.ai.sdk.bean.DhPickupMode;
import com.voyah.ai.sdk.bean.DhScreenType;
import com.voyah.ai.sdk.bean.DhSwitch;
import com.voyah.ai.sdk.listener.IPvcResultListener;

import java.time.LocalTime;

/**
 * 语音设置接口管理
 */
public class SettingManager {

    private static final String TAG = SettingManager.class.getSimpleName();


    /**
     * 获取语音开关当前值
     *
     * @param switchName 开关名字，参考DhSwitch类定义
     * @return
     */
    public static boolean isEnableSwitch(@DhSwitch String switchName) {
        boolean isEnable = true;
        if (assistantConnector != null) {
            isEnable = assistantConnector.isEnableSwitch(switchName);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return isEnable;
    }

    /**
     * 设置语音开关
     *
     * @param switchName 开关名字，参考DhSwitch类定义
     * @param enable     true 开启；false 关闭
     */
    public static void enableSwitch(@DhSwitch String switchName, boolean enable) {
        Log.d(TAG, "enableSwitch() called with: switchName = [" + switchName + "], enable = [" + enable + "]");
        if (assistantConnector != null) {
            assistantConnector.enableSwitch(switchName, enable);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }


    /**
     * 设置拾音模式
     *
     * @param mode
     */
    @Deprecated
    public static void setPickupMode(@DhPickupMode int mode) {
        Log.d(TAG, "setPickupMode() called with: mode = [" + mode + "]");
        if (assistantConnector != null) {
            assistantConnector.setPickupMode(mode);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 获取当前设置的拾音模式
     *
     * @return 返回值
     * 0 :人声自适应模式:
     * 定向抑制非声源，如主驾唤醒，只识别主驾的声音，其他位置的声音不收听不识别
     * 1 :全驾模式:
     * 如主驾唤醒后，其他位置的声音也会被收听和识别
     */
    @Deprecated
    public static int getPickupMode() {
        int mask = DhPickupMode.CAR_PICKUP_MODE_POSITION;
        if (assistantConnector != null) {
            mask = assistantConnector.getPickupMode();
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return mask;
    }

    /**
     * 设置连续对话延时聆听时长
     *
     * @param time
     */
    public static void setContinuousWaitTime(int time) {
        if (assistantConnector != null) {
            assistantConnector.setContinuousWaitTime(time);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 获取连续对话延时聆听时长
     */
    public static int getContinuousWaitTime() {
        if (assistantConnector != null) {
            return assistantConnector.getContinuousWaitTime();
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return -1;
    }

    /**
     * 音区设定
     *
     * @param direction 方位
     * @param enable    开关值
     */
    public static void setMicZoneEnable(@DhDirection int direction, boolean enable) {
        Log.d(TAG, "setMicZoneEnable() called with: direction = [" + direction + "], enable = [" + enable + "]");
        if (assistantConnector != null) {
            assistantConnector.setMicZoneEnable(direction, enable);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 音区开关是否开启
     *
     * @return 返回值boolean值
     */
    public static boolean isMicZoneEnable(@DhDirection int direction) {
        if (assistantConnector != null) {
            return assistantConnector.isMicZoneEnable(direction);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return true;
    }


    /**
     * 切换方言
     *
     * @param dialect
     */
    public static void setDialect(DhDialect dialect) {
        Log.d(TAG, "setDialect() called with: dialect = [" + dialect + "]");
        if (assistantConnector != null) {
            assistantConnector.setDialect(dialect);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 获取当前方言
     */
    public static DhDialect getCurrentDialect() {
        DhDialect dialect = DhDialect.OFFICIAL_1;
        if (assistantConnector != null) {
            dialect = assistantConnector.getCurrentDialect();
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return dialect;
    }

    /**
     * 获取声音复刻(PVC)列表
     */
    public static void getPvcList(IPvcResultListener listener) {
        if (assistantConnector != null) {
            assistantConnector.getPvcList(listener);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 设置呢称
     *
     * @param nickname 呢称
     */
    public static void setNickname(@NonNull String nickname) {
        Log.d(TAG, "setNickname() called with: nickname = [" + nickname + "]");
        if (assistantConnector != null) {
            assistantConnector.setNickname(nickname);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 恢复呢称
     */
    public static void restoreNickname() {
        Log.d(TAG, "restoreNickname() called");
        if (assistantConnector != null) {
            assistantConnector.restoreNickname();
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 获取呢称
     */
    public static String getNickname() {
        String nickName = null;
        if (assistantConnector != null) {
            nickName = assistantConnector.getNickname();
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return nickName;
    }

    /**
     * 设置自定义唤醒欢迎语
     *
     * @param direction
     * @param greet     自定义唤醒欢迎语
     */
    public static void setDiyGreeting(@DhDirection int direction, String greet) {
        Log.d(TAG, "setDiyGreeting() called with: direction = [" + direction + "], greet = [" + greet + "]");
        if (assistantConnector != null) {
            assistantConnector.setDiyGreeting(direction, greet);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 获取自定义唤醒欢迎语
     *
     * @param direction
     * @return
     */
    public static String getDiyGreeting(@DhDirection int direction) {
        String greet = null;
        if (assistantConnector != null) {
            greet = assistantConnector.getDiyGreeting(direction);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return greet;
    }


    /**
     * 获取音乐媒体偏好
     *
     * @return
     */
    public static int getMusicPreference() {
        int preference = 0;
        if (assistantConnector != null) {
            preference = assistantConnector.getMusicPreference();
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return preference;
    }

    /**
     * 设置音乐媒体偏好
     *
     * @param preference 偏好, 0:智能选择， 1: QQ音乐， 2：网易云音乐
     */
    public static void setMusicPreference(int preference) {
        Log.d(TAG, "setMusicPreference() called with: preference = [" + preference + "]");
        if (assistantConnector != null) {
            assistantConnector.setMusicPreference(preference);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 获取视频媒体偏好
     *
     * @return
     */
    public static int getVideoPreference() {
        int preference = 0;
        if (assistantConnector != null) {
            preference = assistantConnector.getVideoPreference();
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return preference;
    }

    /**
     * 设置视频媒体偏好
     *
     * @param preference 偏好, 0:智能选择， 1: 腾讯视频， 2：爱奇艺
     */
    public static void setVideoPreference(int preference) {
        Log.d(TAG, "setVideoPreference() called with: preference = [" + preference + "]");
        if (assistantConnector != null) {
            assistantConnector.setVideoPreference(preference);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 根据屏幕类型获取displayId
     */
    public static int getDisplayIdForScreenType(@DhScreenType int screenType) {
        int displayId = -1;
        if (assistantConnector != null) {
            displayId = assistantConnector.getDisplayIdForScreenType(screenType);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return displayId;
    }

    /**
     * 获取分屏时左侧宽度
     */
    public static int getLeftSplitScreenWidth() {
        int with = 850;
        if (assistantConnector != null) {
            with = assistantConnector.getLeftSplitScreenWidth();
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return with;
    }

    /**
     * 设置新闻消息推送配置时间
     *
     * @param customTime: 自定义时间， null-系统默认时间， 否则是用户自定义时间
     */
    public static void setNewsPushConfigTime(LocalTime customTime) {
        if (assistantConnector != null) {
            assistantConnector.setNewsPushConfigTime(customTime);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 获取新闻消息推送配置时间
     */
    public static LocalTime getNewsPushConfigTime() {
        if (assistantConnector != null) {
            return assistantConnector.getNewsPushConfigTime();
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return null;
    }

    /**
     * 获取大模型选择偏好
     *
     * @return
     */
    public static int getAiModelPreference() {
        int preference = 0;
        if (assistantConnector != null) {
            preference = assistantConnector.getAiModelPreference();
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return preference;
    }

    /**
     * 设置大模型偏好
     *
     * @param preference 偏好, 0:逍遥座舱大模型，1:DeepSeek
     */
    public static void setAiModelPreference(int preference) {
        Log.d(TAG, "setAiModelPreference() called with: preference = [" + preference + "]");
        if (assistantConnector != null) {
            assistantConnector.setAiModelPreference(preference);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

}
