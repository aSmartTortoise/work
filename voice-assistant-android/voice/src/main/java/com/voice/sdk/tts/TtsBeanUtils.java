package com.voice.sdk.tts;

import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.Locale;

public class TtsBeanUtils {
    public static TTSBean getTtsBean(int ttsId, String... args) {
        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(String.valueOf(ttsId), 2);
        if (ttsBean != null) {
            String tts = ttsBean.getSelectTTs();
            LogUtils.i("TTSBeanX", "getTtsBean:" + ttsId + ",tts:" + tts);
            tts = tts.replaceAll("@\\{(.*?)\\}", "%s");
            LogUtils.i("TTSBeanX", "tts replaced:" + tts);
            if (args != null && args.length > 0) {
                for (String a : args) {
                    LogUtils.i("TTSBeanX", "args:" + a);
                }
                tts = String.format(Locale.getDefault(), tts, (Object[]) args);
            }
            ttsBean.setSelectTTs(tts);
            LogUtils.i("TTSBeanX", "setSelectTTs:" + tts);
        } else {
            ttsBean = new TTSBean();
            LogUtils.e("TTSBeanX", "ttsId is null:" + ttsId);
        }
        return ttsBean;
    }

    public static TTSBean getTtsBean(TTSBean ttsBean, String target, String replacement) {
        String tts = ttsBean.getSelectTTs();
        tts = tts.replace(target, replacement);
        ttsBean.setSelectTTs(tts);
        return ttsBean;
    }

    public static TTSBean getTtsBeanByRegex(String ttsId, String... args) {
        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(ttsId, 2);
        String tts = ttsBean.getSelectTTs();
        LogUtils.i("TTSBeanX", "getTtsBeanByRegex, ttsId:" + ttsId + ",tts:" + tts);
        tts = tts.replaceAll("@\\{(.*?)\\}", "%s");
        LogUtils.i("TTSBeanX", "getTtsBeanByRegex, tts replaced:" + tts);
        if (args != null && args.length > 0) {
            for (String a : args) {
                LogUtils.i("TTSBeanX", "getTtsBeanByRegex, args:" + a);
            }
            tts = String.format(Locale.getDefault(), tts, (Object[]) args);
        }
        ttsBean.setSelectTTs(tts);
        return ttsBean;
    }


    public static TTSBean makeOpenSuccessTts(String originScreen, String targetScreen, int location, boolean alreadyOpen, String appName) {
        if (TextUtils.isEmpty(originScreen)) {
            if (location == 0 || !DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
                if (alreadyOpen) {
                    return TtsBeanUtils.getTtsBean(1100001, appName);
                } else {
                    return TtsBeanUtils.getTtsBean(1100002, appName);
                }
            } else {
                if (alreadyOpen) {
                    return TtsBeanUtils.getTtsBean(1100029, DeviceScreenType.fromValue(targetScreen).getChName(), appName);
                } else {
                    return TtsBeanUtils.getTtsBean(1100030, DeviceScreenType.fromValue(targetScreen).getChName(), appName);
                }
            }
        } else {
            if (alreadyOpen) {
                return TtsBeanUtils.getTtsBean(1100029, DeviceScreenType.fromValue(targetScreen).getChName(), appName);
            } else {
                return TtsBeanUtils.getTtsBean(1100030, DeviceScreenType.fromValue(targetScreen).getChName(), appName);

            }
        }
    }

    public static TTSBean makeCloseSuccessTts(String originScreen, String targetScreen, int location, boolean alreadyClose, String appName) {
        if (TextUtils.isEmpty(originScreen)) {
            if (location == 0 || !DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
                if (alreadyClose) {
                    return TtsBeanUtils.getTtsBean(1100004, appName);
                } else {
                    return TtsBeanUtils.getTtsBean(1100003, appName);
                }
            } else {
                if (alreadyClose) {
                    return TtsBeanUtils.getTtsBean(1100032, DeviceScreenType.fromValue(targetScreen).getChName(), appName);
                } else {
                    return TtsBeanUtils.getTtsBean(1100031, DeviceScreenType.fromValue(targetScreen).getChName(), appName);
                }
            }
        } else {
            if (alreadyClose) {
                return TtsBeanUtils.getTtsBean(1100032, DeviceScreenType.fromValue(targetScreen).getChName(), appName);
            } else {
                return TtsBeanUtils.getTtsBean(1100031, DeviceScreenType.fromValue(targetScreen).getChName(), appName);

            }
        }
    }

}
