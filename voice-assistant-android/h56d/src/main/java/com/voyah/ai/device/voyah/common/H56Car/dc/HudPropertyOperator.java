package com.voyah.ai.device.voyah.common.H56Car.dc;

import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.signal.HudSignal;
import com.voice.sdk.device.carservice.signal.ScreenSignal;
import com.voyah.ai.basecar.utils.HudUtils;
import com.voyah.ai.device.voyah.common.H56Car.ScreenHelper;

/**
 * @Date 2024/7/29 16:39
 * @Author 8327821
 * @Email *
 * @Description HUD 模块比较少依赖car-service
 * 主要中间件为HudUtils
 **/
@CarDevices(carType = CarType.H37_CAR)
public class HudPropertyOperator extends Base56Operator {
    @Override
    void init() {
        HudUtils.getInstance();
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case HudSignal.HUD_SUPPORT:
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_HUD, 0);
            case HudSignal.HUD_MODE:
                return HudUtils.getInstance().getHudCurMode();
            case HudSignal.HUD_MANUAL_LIGHT:
                return HudUtils.getInstance().getLightNum();
            case HudSignal.HUD_MANUAL_HEIGHT:
                return HudUtils.getInstance().getHeight();
            case HudSignal.HUD_SWITCH:
                return HudUtils.getInstance().getArHudSwitch();
            case HudSignal.HUD_SNOW_MODE_SWITCH:
                return HudUtils.getInstance().getSnowMode();
            case HudSignal.HUD_AUTO_LIGHT:
                return HudUtils.getInstance().getLightMode();
            case HudSignal.HUD_AUTO_HEIGHT:
                return HudUtils.getInstance().getHeightAuto();
            case HudSignal.HUD_PRIVACY_MODE:
                try {
                    return Settings.System.getInt(Utils.getApp().getContentResolver(), "privacySecurity");
                }  catch (Settings.SettingNotFoundException e) {
                    return -1;
                }
            case HudSignal.HUD_TEMPERATURE:
                return HudUtils.getInstance().getTemperatureState();
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case HudSignal.HUD_MODE:
                HudUtils.getInstance().setHudMode(value);
                return;
            case HudSignal.HUD_MANUAL_LIGHT:
                HudUtils.getInstance().setLightNum(value);
                return;
            case HudSignal.HUD_MANUAL_HEIGHT:
                HudUtils.getInstance().setHeight(value);
                return;
            case HudSignal.HUD_SWITCH:
                HudUtils.getInstance().setArHudSwitch(value);
                return;
            case HudSignal.HUD_SNOW_MODE_SWITCH:
                HudUtils.getInstance().setSnowMode(value);
                return;
            case HudSignal.HUD_AUTO_LIGHT:
                HudUtils.getInstance().setLightAutoMode(value);
                return;
            case HudSignal.HUD_AUTO_HEIGHT:
                HudUtils.getInstance().setHeightAuto(value);
                return;
            case HudSignal.HUD_HEIGHT_UPLOAD:
                HudUtils.getInstance().uploadHeightAuto(value);
                return;
            default:
                super.setBaseIntProp(key, area, value);
        }
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        switch (key) {
            case HudSignal.HUD_MODE_JAD:
                return HudUtils.getInstance().getJADStatus() == ICommon.Switch.ON;
            case HudSignal.HUD_MODE_TLC:
                return HudUtils.getInstance().getTLCStatus() == ICommon.Switch.ON;
            case HudSignal.HUD_MODE_CTD:
                return HudUtils.getInstance().getCTDStatus() == ICommon.Switch.ON;
            case HudSignal.HUD_MODE_MSD:
                return HudUtils.getInstance().getMSDStatus() == ICommon.Switch.ON;
            default:
                return super.getBaseBooleanProp(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        switch (key) {
            case HudSignal.HUD_MODE_JAD:
                HudUtils.getInstance().setJADStatus(value);
                break;
            case HudSignal.HUD_MODE_TLC:
                HudUtils.getInstance().setTLCStatus(value);
                break;
            case HudSignal.HUD_MODE_CTD:
                HudUtils.getInstance().setCTDStatus(value);
                break;
            case HudSignal.HUD_MODE_MSD:
                HudUtils.getInstance().setMSDStatus(value);
                break;
            default:
                setCommonBoolean(key, area, value);
                break;
        }
    }


    /**
     * 1代表支持。0代表不支持。-代表没处理。（当前没处理表示支持）
     *
     * @param key
     * @return
     */
    @Override
    public String isSupport(String key) {
        switch (key) {
            case HudSignal.HUD_SWITCH:
            case HudSignal.HUD_MODE:
            case HudSignal.HUD_SNOW_MODE_SWITCH:
            case HudSignal.HUD_AUTO_LIGHT:
            case HudSignal.HUD_MANUAL_LIGHT:
            case HudSignal.HUD_AUTO_HEIGHT:
            case HudSignal.HUD_MANUAL_HEIGHT:
            case HudSignal.HUD_PRIVACY_MODE:
            case HudSignal.HUD_TEMPERATURE:
                return "1";
            default:
                return "-";
        }
    }

}
