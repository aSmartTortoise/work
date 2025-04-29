package com.voyah.ai.basecar.utils;

import android.content.Context;
import android.provider.Settings;

import androidx.annotation.WorkerThread;

import com.blankj.utilcode.util.Utils;
import com.mega.nexus.os.IScreenRequestListener;
import com.mega.nexus.os.MegaScreenManager;
import com.mega.nexus.os.MegaUserHandle;
import com.mega.nexus.provider.MegaSettings;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.ui.IScreenStateChangeListener;
import com.voice.sdk.device.ui.ScreenStateHelper;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.appadapter.aidlimpl.EdgesSlideServiceImpl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ScreenUtils {

    private static final String TAG = ScreenUtils.class.getSimpleName();

    private static final int BRIGHTNESS_METER_DEFAULT = 127;

    private static final int BRIGHTNESS_CENTRAL_DEFAULT = 127;

    //实际显示最小1
    private static final int ACTIVE_SETTING_MIN = 1;

    //实际显示最大
    private static final int ACTIVE_SETTING_MAX = 100;

    //车机获取最小 1
    private static final int ACTIVE_BRIGHTNESS_MIN = 1;

    private static final int ACTIVE_BRIGHTNESS_LIMIT = 3;

    //车机获取最大255
    private static final int ACTIVE_BRIGHTNESS_MAX = 255;

    private static final double BRIGHTNESS_PERCENT = 2.55;

    private static ScreenUtils mInstance;

    private static Context mContext;

    public static ScreenUtils getInstance() {
        if (null == mInstance) {
            synchronized (ScreenUtils.class) {
                if (null == mInstance) {
                    mInstance = new ScreenUtils(Utils.getApp());
                }
            }
        }
        return mInstance;
    }

    private ScreenUtils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 获取仪表屏亮度.
     * 0-100
     */
    public int getMeterBrightInitValue() {
        int value = MegaSettings.getIntForUserSystem(
                Utils.getApp().getContentResolver(),
                MegaSettings.INSTRUMENT_BOARD_BRIGHTNESS,
                BRIGHTNESS_METER_DEFAULT,
                MegaUserHandle.USER_SYSTEM
        );
        LogUtils.d(TAG, "getMeterBrightInitValue = " + value);
        return transformPorForBrightness(value);
    }

    /**
     * 获取中控屏幕亮度.
     * 0-100
     */
    public int getCentralBrightInitValue() {
        int value = MegaSettings.getIntForUserSystem(
                Utils.getApp().getContentResolver(),
                MegaSettings.MAIN_CONTROL_BRIGHTNESS,
                BRIGHTNESS_CENTRAL_DEFAULT,
                MegaUserHandle.USER_SYSTEM
        );
        LogUtils.d(TAG, "getCentralBrightInitValue -> initValue " + value);
        return transformPorForBrightness(value);
    }

    /**
     * 获取副驾屏幕亮度.
     * 0-100
     */
    public int getPassengerBrightInitValue() {
        int value = MegaSettings.getIntForUserSystem(
                Utils.getApp().getContentResolver(),
                MegaSettings.PASSENGER_MEDIA_BRIGHTNESS,
                BRIGHTNESS_CENTRAL_DEFAULT,
                MegaUserHandle.USER_SYSTEM
        );
        LogUtils.d(TAG, "getPassengerBrightInitValue -> initValue " + value);
        return transformPorForBrightness(value);
    }


    /**
     * 获取吸顶屏屏幕亮度.
     * 0-100
     */
    public int getCeilingBrightInitValue() {
        int value = MegaSettings.getIntForUserSystem(
                Utils.getApp().getContentResolver(),
                MegaSettings.System.CEILING_BRIGHTNESS,
                BRIGHTNESS_CENTRAL_DEFAULT,
                MegaUserHandle.USER_SYSTEM
        );
        LogUtils.d(TAG, "getCeilingBrightInitValue -> initValue " + value);
        return transformPorForBrightness(value);
    }

    /**
     * 设置仪表屏幕亮度
     *
     * @param value 亮度值
     */
    public void setMeterBrightnessLevel(int value) {
        boolean bool = MegaSettings.putIntForUserSystem(
                Utils.getApp().getContentResolver(),
                MegaSettings.INSTRUMENT_BOARD_BRIGHTNESS,
                transformBrightnessForPro(value),
                MegaUserHandle.USER_SYSTEM
        );
        LogUtils.d(TAG,
                "setMeterBrightnessLevel ->"
                        + " value = " + value + " isSuccess = " + bool
        );
    }

    /**
     * 设置中控屏幕亮度
     *
     * @param level 亮度级别
     */
    public void setCentralBrightnessLevel(int level) {
        boolean bool = MegaSettings.putIntForUserSystem(
                Utils.getApp().getContentResolver(),
                MegaSettings.MAIN_CONTROL_BRIGHTNESS,
                transformBrightnessForPro(level),
                MegaUserHandle.USER_SYSTEM
        );
        EdgesSlideServiceImpl.getInstance(Utils.getApp()).adjustBrightnessProgress("com.voyah.ai.voice", transformBrightnessForPro(level));
        LogUtils.d(TAG,
                "setCentralBrightnessLevel ->"
                        + " value = " + level + " isSuccess = " + bool
        );
    }

    /**
     * 设置副驾屏幕亮度
     *
     * @param level 亮度级别
     */
    public void setPassengerBrightnessLevel(int level) {
        boolean bool = MegaSettings.putIntForUserSystem(
                Utils.getApp().getContentResolver(),
                MegaSettings.PASSENGER_MEDIA_BRIGHTNESS,
                transformBrightnessForPro(level),
                MegaUserHandle.USER_SYSTEM
        );
        LogUtils.d(TAG,
                "setPassengerBrightnessLevel ->"
                        + " value = " + level + " isSuccess = " + bool
        );
    }

    /**
     * 设置吸顶屏幕亮度
     *
     * @param level 亮度级别
     */
    public void setCeilingBrightnessLevel(int level) {
        boolean bool = MegaSettings.putIntForUserSystem(
                Utils.getApp().getContentResolver(),
                MegaSettings.System.CEILING_BRIGHTNESS,
                transformBrightnessForPro(level),
                MegaUserHandle.USER_SYSTEM
        );
        LogUtils.d(TAG,
                "setPassengerBrightnessLevel ->"
                        + " value = " + level + " isSuccess = " + bool
        );
    }

    /**
     * 亮度1-255 转成1-100.
     */
    private int transformPorForBrightness(int brightness) {
        // 超出范围则显示1
        if (brightness < ACTIVE_BRIGHTNESS_MIN
                || brightness > ACTIVE_BRIGHTNESS_MAX) {
            return ACTIVE_BRIGHTNESS_MIN;
        }
        if (brightness < ACTIVE_BRIGHTNESS_LIMIT) {
            return ACTIVE_BRIGHTNESS_MIN;
        }
        int result = (int) Math.round((double) brightness / BRIGHTNESS_PERCENT);
        LogUtils.d(TAG, "ProgressResult -> " + result);
        return result;
    }

    /**
     * 100->255
     * 进度转亮度.
     */
    private int transformBrightnessForPro(int progress) {
        int result = (int) Math.round((double) progress * BRIGHTNESS_PERCENT);
        if (result <= 0) {
            result = 1;
        }
        LogUtils.d(TAG, "transformBrightnessForPro progress=" + progress + ", result -> " + result);
        return result;
    }

    /**
     * 对应屏幕 息屏/亮屏
     *
     * @param open      Boolean
     * @param displayId Int
     */
    public void controlScreenBright(boolean open, int displayId) {
        try {
            LogUtils.d(TAG, "controlScreenBright open=" + open + ", displayId=" + displayId);
            if (open) {
                MegaScreenManager.getInstance()
                        .requestScreenOn(MegaScreenManager.USAGE_VOICE_AWAKEN, displayId, new IScreenRequestListener.Stub() {
                            @Override
                            public void onResult(int code) {
                                LogUtils.d(TAG, "requestScreenOn onResult code=" + code);
                            }
                        });
            } else {
                MegaScreenManager.getInstance()
                        .requestScreenOff(MegaScreenManager.USAGE_VOICE_AWAKEN, displayId, new IScreenRequestListener.Stub() {
                            @Override
                            public void onResult(int code) {
                                LogUtils.d(TAG, "requestScreenOff onResult code=" + code);
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCeilLastAngle() {
        int curTemp = -1;
        try {
            curTemp = Settings.System.getInt(Utils.getApp().getContentResolver(), "ceiling_screen_angle");
        } catch (Settings.SettingNotFoundException e) {
            //
        }
        if (curTemp < 90) {
            curTemp = 105;
        }
        return curTemp;
    }

    public int getScreenOnOff(int displayId) {
        return MegaScreenManager.getInstance().getScreenState(displayId);
    }
}
