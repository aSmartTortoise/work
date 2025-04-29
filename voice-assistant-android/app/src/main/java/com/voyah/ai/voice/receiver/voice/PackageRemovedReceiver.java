package com.voyah.ai.voice.receiver.voice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.voice.sdk.device.DeviceHolder;
import com.voyah.ai.common.utils.LogUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author:lcy
 * @data:2025/3/13
 **/
public class PackageRemovedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d("PackageRemovedReceiver", "intent:" + intent.getAction() + " ," + intent.getPackage());
        if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            // 获取被卸载应用的包名
            String packageName = intent.getData().getSchemeSpecificPart();
            if (StringUtils.equals(packageName, "com.thunder.carplay")) {
                LogUtils.d("PackageRemovedReceiver", "LeiKtv: " + packageName);
                // 判断是卸载还是更新
                boolean isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
                LogUtils.d("PackageRemovedReceiver", "isReplacing:" + isReplacing);
                if (!isReplacing) {
                    DeviceHolder.INS().getDevices().getVoiceCarSignal().onLeiKtvReceiveStatus(false);
                    LogUtils.d("PackageRemovedReceiver", "LeiKtv uninstall: " + packageName);
                }
            }
        }
    }
}
