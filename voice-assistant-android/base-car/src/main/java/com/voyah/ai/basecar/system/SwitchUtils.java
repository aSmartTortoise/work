package com.voyah.ai.basecar.system;

import android.content.Context;

import com.blankj.utilcode.util.Utils;
import com.mega.dvrsdk.DvrServiceProxy;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.phone.PhoneInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.common.utils.LogUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author:lcy
 * @data:2025/2/19
 **/
public class SwitchUtils {
    private static final String TAG = SwitchUtils.class.getSimpleName();

    private static final String PKG_CAMERA = "com.voyah.cockpit.camera"; //行车记录仪包名

    /**
     * 打开\关闭 应用、页面入口
     *
     * @param screenName 指定屏幕
     * @param switchTag  true:打开 false:关闭
     * @param tag        标签，标记操作对象
     * @param saveTag    标签，标记要保留不关闭的对象 String或数组类型
     * @param map        可能需要对话系统的上下文
     */
    //todo:添加saveTag参数，类型String、String[]支持单保留和多保留
    public static void switchPage(String screenName, boolean switchTag, String tag, String secondTag, Object saveTag, Object map) {
        LogUtils.d(TAG, "screenName:" + screenName + " ,switchTag:" + switchTag + " ,tag:" + tag + " ,secondTag:" + secondTag + " ,saveTag:" + saveTag);
        //关闭霸屏弹窗
        if (switchTag)
            DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(screenName), saveTag);
        //todo:各种打开关闭
        if (StringUtils.isBlank(tag))
            return;
        //------电话 DVR
        if (tag.contains("phone_app"))
            phoneSwitch(Utils.getApp(), screenName, switchTag, tag, secondTag, map);
        else if (tag.contains("dvr_app"))
            dvrSwitch(Utils.getApp(), screenName, switchTag, tag, secondTag, map);


    }

    private static void phoneSwitch(Context context, String screenName, boolean switchTag, String tag, String secondTag, Object map) {
        if (StringUtils.equals(secondTag, "phone_app")) {
            PhoneInterface phoneInterface = DeviceHolder.INS().getDevices().getPhone();
            if (switchTag)
                phoneInterface.openBtApk();
            else
                phoneInterface.closeBtApk();
        }
        //todo:后面补充电话相关其他页面打开关闭
    }

    private static void dvrSwitch(Context context, String screenName, boolean switchTag, String tag, String secondTag, Object map) {
        if (StringUtils.equals(secondTag, "dvr_app_dashCam")) {
            //行车记录仪app
            if (switchTag)
                DvrServiceProxy.get().openDvr(0);
            else
                DvrServiceProxy.get().backgroundDvr();
        } else if (StringUtils.equals(secondTag, "dvr_app_camera")) {
            //相机app
            if (switchTag)
                MegaForegroundUtils.openApp(context, PKG_CAMERA);
            else
                DeviceHolder.INS().getDevices().getLauncher().backToHome(DeviceScreenType.CENTRAL_SCREEN);
        }
        //todo:后面补充DVR相关其他页面打开关闭
    }
}
