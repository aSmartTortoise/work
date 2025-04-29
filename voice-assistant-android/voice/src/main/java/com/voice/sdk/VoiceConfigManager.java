package com.voice.sdk;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.ISysSetting;
import com.voyah.ai.common.utils.FileUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.soa.api.plugin.IEnvPlugin;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class VoiceConfigManager {
    private static final String TAG = VoiceConfigManager.class.getSimpleName();


    public static final int ENV_PRO = 1;
    public static final int ENV_PRE_PRO = 2;
    public static final int ENV_TEST = 3;
    public static final int ENV_DEV = 4;
    public static final int ENV_MOCK = 5;


    public static int getEnv() {
        try {
            int env = DeviceHolder.INS().getDevices().getCarService().getSystemSetting()
                    .getInt(ISysSetting.SystemSettingType.SETTING_GLOBAL, "com.voyah.engineeringmode.enviswitch", ENV_PRO);
            LogUtils.i(TAG, "environment from eng == " + env);
            return env;
        } catch (Exception var2) {
            LogUtils.e(TAG, "getIntProp:" + var2.getMessage());
            return 1;
        }
    }


    private boolean isSaveAudio = false; //是否保存音频
    private boolean isDebugWriteAudio = false; //自动化灌音频测试使用
    private String env = "test"; //默认指定环境
    private boolean isEngineerPreEnvEnabled;

    private static class configHolder {
        private static final VoiceConfigManager INSTANCE = new VoiceConfigManager();
    }

    public static VoiceConfigManager getInstance() {
        return configHolder.INSTANCE;
    }

    public void init() {
        if (isEngineerPreEnvEnabled) {
            env = "pre";
        } else {
            int envInt = getEnv();
            switch (envInt) {
                case ENV_TEST:
                    env = "test";
                    break;
                case ENV_DEV:
                    env = "dev";
                    break;
                case ENV_PRO:
                    env = "prod";
                    break;
                case ENV_PRE_PRO:
                default:
                    env = "pre";
                    break;
            }
        }
        try {
            String configPath = Constant.path.carLoadRes + "config.txt";
            String config = FileUtils.readTxtFile(configPath);
            LogUtils.i(TAG, "config is " + config);
            if (StringUtils.isBlank(config))
                return;
            JSONObject jsonObject = GsonUtils.parseToJSONObject(config);
            isSaveAudio = jsonObject.getBoolean("saveAudio");
            isDebugWriteAudio = jsonObject.getBoolean("isDebugWriteAudio");
            env = jsonObject.getString("env");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getSaveAudioSwitch() {
        return isSaveAudio;
    }

    public boolean getDebugWriteAudioSwitch() {
        return isDebugWriteAudio;
    }

    public String getVoiceEnv() {
        if (IEnvPlugin.ENV_PROD_NAME.equals(env)) {
            env = IEnvPlugin.ENV_PRE_NAME;
        }
        return env;
    }

    public void setPreEnvEnabled(boolean isPreEnvEnabled) {
        this.isEngineerPreEnvEnabled = isPreEnvEnabled;
    }

}
