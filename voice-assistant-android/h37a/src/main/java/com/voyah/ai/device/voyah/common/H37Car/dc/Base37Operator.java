package com.voyah.ai.device.voyah.common.H37Car.dc;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.RemoteException;
import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.signal.CarSettingSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.ScreenSignal;
import com.voice.sdk.device.carservice.vcar.BaseOperator;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.basecar.manager.SettingsManager;
import com.voyah.ai.basecar.system.CommonSplitScreenImpl;
import com.voyah.ai.basecar.system.MegaForegroundUtils;
import com.voyah.ai.basecar.utils.SettingUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.device.voyah.common.H37Car.DrivingHelper;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.device.voyah.common.H37Car.ScreenHelper;
import com.voyah.ai.device.voyah.h37.utils.SystemUiUtils;
import com.voyah.cockpit.appadapter.aidlimpl.IFunctionManagerImpl;
import com.voyah.cockpit.appadapter.aidlimpl.SystemUIInterfaceImp;

import java.util.HashMap;

import mega.car.Signal;
import mega.car.VehicleArea;
import mega.car.config.Apa;
import mega.car.config.Driving;
import mega.car.config.ElecPower;
import mega.car.config.ParamsCommon;

/**
 * @Date 2024/7/17 14:41
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class Base37Operator extends BaseOperator {
    private static final String TAG = "VirtualCar";
    protected static final int INVALID = -1;
    protected static final HashMap<String, Integer> map = new HashMap<>(64);

    static {
        //一些通用的虚拟信号，放在基类处理
        map.put(CommonSignal.COMMON_SPEED_INFO, Driving.ID_DRV_INFO_SPEED_INFO);
        map.put(CommonSignal.COMMON_GEAR_INFO, Driving.ID_DRV_INFO_GEAR_POSITION);
        map.put(CarSettingSignal.CARSET_DRIVING_MODE, Driving.ID_DRV_MODE); //驾驶模式
        map.put(CommonSignal.COMMON_REMAIN_POWER, ElecPower.ID_HV_PERCENT);//获取当前电量

    }

    public Base37Operator() {
        init();
    }

    void init() {
//        SystemUiUtils.getInstance().binderSystemUi(Utils.getApp());
    }

    public int getBaseIntProp(String key, int area) {
        LogUtils.d(TAG, "KEY: " + key);
        switch (key) {
            case CarSettingSignal.CARSET_DRIVING_MODE:
                return DrivingHelper.getVDrivingMode();
            case CommonSignal.COMMON_INFO_HIDING:
                return Settings.System.getInt(Utils.getApp().getContentResolver(), "system_info_hiding", 0);
            case CommonSignal.COMMON_SPLIT_SWITCH:
                return Settings.Global.getInt(Utils.getApp().getContentResolver(), "application.layering", 1);
            case CommonSignal.COMMON_PRIVACY_PROTECTION:
                return Settings.System.getInt(Utils.getApp().getContentResolver(), "privacySecurity", 0);
            case CommonSignal.COMMON_POWER_MODER:
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_POWER_MODE, -1);
            case CommonSignal.COMMON_360_STATE:
                return CarPropUtils.getInstance().getIntProp(Apa.ID_APA_PAS_FUNCMODE);
            case CommonSignal.COMMON_SUPPORT_REFRIGERATOR:
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_CAR_REFRIGERATOR, 0);
            case CommonSignal.COMMON_SUPPORT_FAST:
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_FAST_CHARGE, 2);
            case CommonSignal.COMMON_EQUIPMENT_LEVEL:
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_EQUIPMENT_LEVEL, -1);
            case CommonSignal.COMMON_SUPPORT_HEADREST:
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_HEADREST_SOUND, -1);
            case CommonSignal.COMMON_HEADREST_STATE:
                return Settings.System.getInt(Utils.getApp().getContentResolver(), "audio_headrest_mode", 1);
            default:
                return getCommonInt(key, area);
        }
    }

    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case CarSettingSignal.CARSET_DRIVING_MODE:
                DrivingHelper.setDrivingMode(value);
                break;
            case CommonSignal.COMMON_INFO_HIDING:
                Settings.System.putInt(Utils.getApp().getContentResolver(), "system_info_hiding", value);
                break;
            case CommonSignal.COMMON_SPLIT_SWITCH:
                Settings.Global.putInt(Utils.getApp().getContentResolver(), "application.layering", value);
                Utils.getApp().getContentResolver().notifyChange(Settings.System.getUriFor("application.layering"), null);
                DeviceHolder.INS().getDevices().getSystem().getSplitScreen().updateSplitSwitch(ICommon.Switch.ON == value);
                break;
            case CommonSignal.COMMON_PRIVACY_PROTECTION:
                if (CarServicePropUtils.getInstance().isH37A()) {
                    try {
                        IFunctionManagerImpl.getInstance(Utils.getApp()).setPrivacyMode(value == 1);
                    } catch (RemoteException ignored) {
                    }
                } else {
                    SettingUtils.getInstance().exec(value == 1 ? SettingConstants.NEW_ACTION_OPENPRIVACYPROTECTION : SettingConstants.NEW_ACTION_CLOSEPRIVACYPROTECTION);
                }
                break;
            case CommonSignal.COMMON_CAR_HEALTH:
                Intent carHealthIntent = new Intent();
                carHealthIntent.setComponent(new ComponentName("com.voyah.vehiclehealth", "com.voyah.vehiclehealth.feature.main.MainActivity"));
                carHealthIntent.putExtra("route_path", value);
                startActivity(carHealthIntent);
                break;
            case CommonSignal.COMMON_LLM_MODE:
                SettingsManager.get().setAiModelPreference(value);
                break;
            default:
                setCommonInt(key, area, value);
                break;
        }
    }

    @Override
    public float getBaseFloatProp(String key, int area) {
        return getCommonFloat(key, area);
    }

    @Override
    public void setBaseFloatProp(String key, int area, float value) {
        setCommonFloat(key, area, value);
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        switch (key) {
            case CommonSignal.COMMON_IS_NAVI:
                return DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation();
            case CommonSignal.COMMON_HAS_BT_CONNECT:
                try {
                    return IFunctionManagerImpl.getInstance(Utils.getApp()).getBtEarphonesState() == 0;
                } catch (RemoteException e) {
                    return false;
                }
            case CommonSignal.COMMON_CEIL_CONFIG:
                return SysCtrlHelper.INSTANCE.getCeilingConfig();
            case CommonSignal.COMMON_CEIL_OPEN:
                return SysCtrlHelper.INSTANCE.isCeilingOpen();
            case ScreenSignal.SCREEN_MOVE_STATE:
                return ScreenHelper.isMoving();
            case CommonSignal.COMMON_SETTING_STATE:
                return MegaForegroundUtils.isForegroundApp("com.voyah.cockpit.vehiclesettings");
            case CommonSignal.COMMON_HAS_REMOTE_APP:
                // 遥控器app 37A 37B不支持 56C 56D支持
                return false;
            default:
                return getCommonBoolean(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        if (key.equalsIgnoreCase(CommonSignal.COMMON_PARK_APP_STATE)) {
            if (value) {
                CarPropUtils.getInstance().setIntProp(Apa.ID_AVM_VOICE_ONOFF_REQ, ParamsCommon.OnOff.ON);
            } else {
                CarPropUtils.getInstance().setIntProp(Signal.ID_APA_PASQUITSW, ParamsCommon.Press.PRESSED);
            }
        } else {
            setCommonBoolean(key, area, value);
        }
    }

    /**
     * @param virtualKey 虚拟车平台定义的key
     * @return 37 car-service 定义的key
     */
    protected int getRealKey(String virtualKey) {
        return map.getOrDefault(virtualKey, -1);
    }

    protected int getRealArea(int area) {
        int area_37;
        switch (area) {
            case PositionSignal.FIRST_ROW_LEFT:
                area_37 = VehicleArea.FRONT_LEFT;
                break;
            case PositionSignal.FIRST_ROW_RIGHT:
                area_37 = VehicleArea.ROW_1_RIGHT;
                break;
            case PositionSignal.SECOND_ROW_LEFT:
                area_37 = VehicleArea.ROW_2_LEFT;
                break;
            case PositionSignal.SECOND_ROW_RIGHT:
                area_37 = VehicleArea.ROW_2_RIGHT;
                break;
            case PositionSignal.THIRD_ROW_LEFT:
                area_37 = VehicleArea.ROW_3_LEFT;
                break;
            case PositionSignal.THIRD_ROW_RIGHT:
                area_37 = VehicleArea.ROW_3_RIGHT;
                break;
            case PositionSignal.ALL:
                area_37 = VehicleArea.ALL;
                break;
            case PositionSignal.FIRST_ROW:
                area_37 = VehicleArea.FRONT_ROW;
                break;
            case PositionSignal.AREA_NONE:
            default:
                area_37 = VehicleArea.NONE;
        }
        return area_37;
    }

    protected int getCommonInt(String key, int area) {
        int key_37 = getRealKey(key);
        int area_37 = getRealArea(area);
        return CarPropUtils.getInstance().getIntProp(key_37, area_37);
    }

    protected void setCommonInt(String key, int area, int value) {
        int key_37 = getRealKey(key);
        int area_37 = getRealArea(area);
        CarPropUtils.getInstance().setIntProp(key_37, area_37, value);
    }

    protected float getCommonFloat(String key, int area) {
        int key_37 = getRealKey(key);
        int area_37 = getRealArea(area);
        return CarPropUtils.getInstance().getFloatProp(key_37, area_37);
    }

    protected void setCommonFloat(String key, int area, float value) {
        int key_37 = getRealKey(key);
        int area_37 = getRealArea(area);
        CarPropUtils.getInstance().setFloatProp(key_37, area_37, value);
    }

    /**
     * 通用的将1转化为true,0转化为false
     * @param key
     * @param area
     * @return
     */
    protected boolean getCommonBoolean(String key, int area) {
        int key_37 = getRealKey(key);
        int area_37 = getRealArea(area);
        return ParamsCommon.OnOffInvalid.ON
                == CarPropUtils.getInstance().getIntProp(key_37, area_37);
    }

    protected void setCommonBoolean(String key, int area, boolean value) {
        int key_37 = getRealKey(key);
        int area_37 = getRealArea(area);
        //默认使用1代表开，0代表关，如果不满足这个规则，请在switch case补充
        int intOnOff = value ? ParamsCommon.OnOffInvalid.ON : ParamsCommon.OnOffInvalid.OFF;
        CarPropUtils.getInstance().setIntProp(key_37, area_37, intOnOff);
    }
}
