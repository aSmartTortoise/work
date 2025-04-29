package com.voyah.ai.basecar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.LogUtils;

/**
 * author : jie wang
 * date : 2025/3/3 21:25
 * description :
 */
public class BaseAppPresenter {

    private static final String TAG = "BaseAppPresenter";
    protected boolean mServiceConnectedFlag = false;
    protected Context mContext;

    public void initSdk() {
        mContext = ContextUtils.getAppContext();
    }

    public void openApp(String pkgName) {
        LogUtils.d(TAG, "openApp pkgName:" + pkgName);
        beforeOpenApp();
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = null;
        try {
            intent = packageManager.getLaunchIntentForPackage(pkgName);
        } catch (Exception e) {
            LogUtils.e(TAG, "openApp error:" + e);
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    public void beforeOpenApp() {
        LogUtils.d(TAG, "beforeOpenApp");
//        //收起负一屏
//        GlobalProcessingHelper.getInstance().closeNegativeScreen();
//        //收起应用中心
//        DeviceInterfaceImpl.getInstant(Utils.getApp()).getLauncherImpl().hideAllAppPanel();
    }

    /**
     * 获取信息隐藏开关状态
     * @return Int
     * 信息隐藏已开启 PRIVACY_MODE_ON = 1
     * 信息已关闭 PRIVACY_MODE_OFF = 0
     */
    public boolean isSystemInfoHidingOpen() {
        boolean result = (1 == Settings.System.getInt(
                mContext.getContentResolver(),
                ApplicationConstant.KEY_SYSTEM_INFO_HIDING, 0));
        LogUtils.i(TAG, "isSystemInfoHidingOpen result:" + result);
        return result;
    }

    public String getTimeType() {
        String timeType = Settings.System.getString(
                Utils.getApp().getContentResolver(),
                Settings.System.TIME_12_24);
        LogUtils.i(TAG, "timeType :" + timeType);
        return timeType;
    }
}
