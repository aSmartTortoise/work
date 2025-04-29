package com.voice.sdk;

import com.voice.sdk.device.DeviceHolder;

import org.apache.commons.lang3.StringUtils;

/**
 * @author:lcy
 * @data:2024/3/21
 **/
public class Constant {
    public interface path {
//        String zipRes = "/storage/emulated/0/res.zip";
//        String deviceRes = "/data/data/com.voyah.ai.voice/";
//        String loadRes = "/data/data/com.voyah.ai.voice/res/";

        //-------------------手机自动化资源文件
        String zipRes = "/storage/emulated/0/res.zip";
        String phoneDeviceRes = "/data/data/com.voyah.ai.voice/";
        String phoneLoadRes = "/data/data/com.voyah.ai.voice/res/";


        //-------------------实车资源文件加载路径
        String carLoadRes = "/system/third_party/voice/res/";

        //--------------------音频文件等本地保存路径
        String dirWrite = "/data/data/com.voyah.ai.voice/files/";
    }

    public interface channels {
        int micChannelsDefault = 4;
        int RefChannelsDefault = StringUtils.equals(DeviceHolder.INS().getDevices().getCarServiceProp().getCarType(), "H37B") ? 6 : 4;
//        int RefChannelsH37B = 6;
    }
}
