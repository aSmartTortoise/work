package com.voyah.ai.basecar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import com.voice.sdk.device.IDomainPresenter;
import com.voyah.ai.basecar.system.MegaForegroundUtils;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.common.utils.LogUtils;

import java.util.Random;

/**
 * author : jie wang
 * date : 2024/4/10 19:30
 * description : 业务功能Presenter的抽象类
 */
abstract public class BaseDomainPresenter implements IDomainPresenter {

    private static final String TAG = "BaseDomainPresenter";

    protected Context mContext;

    protected boolean mServiceConnectedFlag = false;

    @Override
    public void init(Context context) {
        mContext = context;
    }

    @Override
    public boolean isAppForeground(String pkg) {
        return MegaForegroundUtils.isForegroundApp(mContext, pkg);
    }

    @Override
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

    @Override
    public void closeApp(String pkgName) {

    }

    @Override
    public void backToLauncher() {
        LogUtils.d(TAG, "backToLauncher");
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = null;
        try {
            intent = packageManager.getLaunchIntentForPackage(ApplicationConstant.PKG_LAUNCHER);
        } catch (Exception e) {
            LogUtils.e(TAG, "backToLauncher error:" + e);
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    public String getTTSText(@StringRes int strRes, Object... textArr) {
        return mContext.getString(strRes, textArr);
    }

    public String[] getStringArrayResource(@ArrayRes int resourceId) {
        return mContext.getResources().getStringArray(resourceId);
    }

    public String getRandomReply(String... replies) {
        if (replies.length == 0) {
            return "";
        } else {
            int i = new Random().nextInt(replies.length);
            return replies[i];
        }
    }

    /**
     * 获取信息隐藏开关状态
     * @return Int
     * 信息隐藏已开启 PRIVACY_MODE_ON = 1
     * 信息已关闭 PRIVACY_MODE_OFF = 0
     */
    public boolean isSystemInfoHidingOpen() {
        return 1 == Settings.System.getInt(
                mContext.getContentResolver(),
                ApplicationConstant.KEY_SYSTEM_INFO_HIDING, 0);
    }

    public void beforeOpenApp() {
        LogUtils.d(TAG, "beforeOpenApp");
        //收起负一屏
//        GlobalProcessingHelper.getInstance().closeNegativeScreen();
        //收起应用中心
//        DeviceInterfaceImpl.getInstant(Utils.getApp()).getLauncherImpl().hideAllAppPanel();
    }
}
