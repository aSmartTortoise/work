package com.voyah.ai.device.voyah.common.H56Car.dc;

import android.content.ContentResolver;
import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.signal.AdasSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;

import mega.car.config.Adas;
import mega.car.config.Driving;
import mega.car.config.H56C;

/**
 * @Date 2024/9/6 16:29
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class AdasPropertyOperator extends Base56Operator implements IDeviceRegister {
    private static final String TAG = AdasPropertyOperator.class.getSimpleName();

    @Override
    void init() {
        map.put(AdasSignal.ADAS_CHASSIS_STYLE, Driving.ID_DRV_CHASSIS_MODESET);
        map.put(AdasSignal.ADAS_FCW_SWITCH, Adas.ID_ADAS_FCW_SWITCH_CONFIG);
        map.put(AdasSignal.ADAS_FCW_SENSITIVITY, Adas.ID_ADAS_FCW_SENSITIVITY_CONFIG);
        map.put(AdasSignal.ADAS_AEB_SWITCH, Adas.ID_ADAS_AEB_AUTO_EMERGENCY_BRAKING_CONFIG);
        map.put(AdasSignal.ADAS_RCW_SWITCH, Adas.ID_ADAS_RCW_CONFIG);
        map.put(AdasSignal.ADAS_MEB_SWITCH, H56C.APA_control2_APA_MEB_SW_STS);
        map.put(AdasSignal.ADAS_LDA_STATE, Adas.ID_ADAS_LANE_DEPARTURE_WARNING_CONFIG);
        map.put(AdasSignal.ADAS_LDA_WARNING_STYLE, Adas.ID_ADAS_LDW_FUNCTWARNMOD_CONFIG);
        map.put(AdasSignal.ADAS_ELK_SWITCH, Adas.ID_ADAS_EMERGENCY_LANE_KEEPING_CONFIG);
        map.put(AdasSignal.ADAS_ESA_SWITCH, Adas.ID_ADAS_EMERGENCY_STEERING_ASSIST_CONFIG);
//        map.put(AdasSignal.ADAS_LCA_SWITCH, Adas.ID_ADAS_LANE_CHANGE_ASSIST_CONFIG);
//        map.put(AdasSignal.ADAS_LCA_SENSITIVITY, Adas.ID_ADAS_LCA_SENSITIVITY_CONFIG);
//        map.put(AdasSignal.ADAS_FCTA_SWITCH, Adas.ID_ADAS_FORWARD_CROSSING_TRAFFIC_ALERTING_CONFIG);
        map.put(AdasSignal.ADAS_RCTA_SWITCH, Adas.ID_ADAS_RCTA_CONFIG);
        map.put(AdasSignal.ADAS_DOW_SWITCH, Adas.ID_ADAS_DOOR_OPEN_WARNING_CONFIG);
//        map.put(AdasSignal.ADAS_FVSR_SWITCH, Adas.ID_ADAS_FRONT_VEHICLE_START_REMINDER_CONFIG);
        map.put(AdasSignal.ADAS_TLC_SWITCH, Adas.ID_ADAS_TRAFFIC_LANE_CHANGING_CONFIG);
        map.put(AdasSignal.ADAS_BSD_SWITCH, Adas.ID_ADAS_BSD_CONFIG);
        map.put(AdasSignal.ADAS_TSR_SWITCH, Adas.ID_ADAS_TSR_SWITCH_CONFIG);
//        map.put(AdasSignal.ADAS_ASSIST_DRV_STYLE, Adas.ID_ASSID_DRVG_STYLE);
//        map.put(AdasSignal.ADAS_NOA_SWITCH, Adas.ID_ADAS_NAVIGATION_ON_DRIVING_ASSIST_CONFIG);
//        map.put(AdasSignal.ADAS_ALCO_SWITCH, Adas.ID_ADAS_NOA_LOWSPD_ALC_CONFIG);
//        map.put(AdasSignal.ADAS_LCOC_SWITCH, Adas.ID_ADAS_NOA_ALC_CONFIRM_CONFIG);
//        map.put(AdasSignal.ADAS_EOL_SWITCH, Adas.ID_ADAS_NOA_OVERTAKINGBACK_ALC_CONFIG);
        map.put(AdasSignal.ADAS_ISA_SWITCH, Adas.ID_ADAS_ISA_CONFIG);
//        map.put(AdasSignal.ADAS_OWA_SWITCH, Adas.ID_ADAS_OVERSPD_ALERT);
        map.put(AdasSignal.ADAS_ISLC_SWITCH, Adas.ID_ADAS_ISA_CONFIG);
        map.put(AdasSignal.ADAS_APA, H56C.IVI_AVMSet_IVI_VOICE_APAONOFFREQ);
        map.put(AdasSignal.ADAS_AVM, H56C.IVI_AVMSet_IVI_VOICE_AVMONOFFREQ);
        map.put(AdasSignal.ADAS_CAR_TRANSPARENT, H56C.IVI_AVMSet_IVI_VOICE_CARBODYTRANSPARENTONOFFREQ);


        //以下为56C和37上不同的信号，即上面注释的部分
//        map.put(AdasSignal.ADAS_MEB_SWITCH, Apa.ID_APA_MEB_SW_STS);
//        map.put(AdasSignal.ADAS_LCA_SWITCH, Adas.ID_ADAS_LANE_CHANGE_ASSIST_CONFIG);
//        map.put(AdasSignal.ADAS_LCA_SENSITIVITY, Adas.ID_ADAS_LCA_SENSITIVITY_CONFIG);
//        map.put(AdasSignal.ADAS_FCTA_SWITCH, Adas.ID_ADAS_FORWARD_CROSSING_TRAFFIC_ALERTING_CONFIG);
//        map.put(AdasSignal.ADAS_FVSR_SWITCH, Adas.ID_ADAS_FRONT_VEHICLE_START_REMINDER_CONFIG);
//        map.put(AdasSignal.ADAS_ASSIST_DRV_STYLE, Adas.ID_ASSID_DRVG_STYLE);
//        map.put(AdasSignal.ADAS_NOA_SWITCH, Adas.ID_ADAS_NAVIGATION_ON_DRIVING_ASSIST_CONFIG);
//        map.put(AdasSignal.ADAS_ALCO_SWITCH, Adas.ID_ADAS_NOA_LOWSPD_ALC_CONFIG);
//        map.put(AdasSignal.ADAS_LCOC_SWITCH, Adas.ID_ADAS_NOA_ALC_CONFIRM_CONFIG);
//        map.put(AdasSignal.ADAS_EOL_SWITCH, Adas.ID_ADAS_NOA_OVERTAKINGBACK_ALC_CONFIG);
//        map.put(AdasSignal.ADAS_OWA_SWITCH, Adas.ID_ADAS_OVERSPD_ALERT);
//        map.put(AdasSignal.ADAS_ISLC_SWITCH, Adas.ID_ADAS_ISLC_CONFIG);

    }

    @Override
    public int getBaseIntProp(String key, int area) {

        if (key.startsWith(AdasSignal.ADAS_TDA4STATE)) {
            String func = key.substring(AdasSignal.ADAS_TDA4STATE.length());
            ContentResolver cr = Utils.getApp().getContentResolver();
            LogUtils.d(TAG, "adas : func is " + func);
            return Settings.Global.getInt(cr, func, 0);
        }

        switch (key) {
            case AdasSignal.ADAS_LCA_SENSITIVITY: //LCASensitivity [0,1,2] -> [1,2,3]
            case AdasSignal.ADAS_FCW_SWITCH:
            case AdasSignal.ADAS_AEB_SWITCH:
                return getCommonInt(key, area) + 1;
            case AdasSignal.ADAS_TLC_SWITCH:
            case AdasSignal.ADAS_LDA_STATE:
            case AdasSignal.ADAS_LDA_WARNING_STYLE:
                return getCommonInt(key, area) - 1;
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case AdasSignal.ADAS_LCA_SENSITIVITY: //LCASensitivity [0,1,2] -> [1,2,3]
                setCommonInt(key, area, value - 1);
            default:
                super.setBaseIntProp(key, area, value);
        }
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        switch (key) {
            case AdasSignal.ADAS_SUPPORT_OPEN_APP:
            case AdasSignal.ADAS_LCA_CONFIG:
            case AdasSignal.ADAS_FCTA_CONFIG:
            case AdasSignal.ADAS_FVSR_CONFIG:
            case AdasSignal.ADAS_NOA_CONFIG:
            case AdasSignal.ADAS_ALCO_CONFIG:
            case AdasSignal.ADAS_OWA_CONFIG:
                return false;
            case AdasSignal.ADAS_CDC_CONFIG:
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_CONTINUOUS_DAMPING_CTRL, 1) == 1;
            case AdasSignal.ADAS_ISA_SWITCH:
                return getCommonInt(key, area) > 1; //56C 智能限速控制 1是关闭，2是显示 3是提醒 4是控制
            case AdasSignal.ADAS_ISLC_SWITCH:
                return getCommonInt(key, area) > 1;
            default:
                return super.getBaseBooleanProp(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        if (key.equalsIgnoreCase(AdasSignal.ADAS_APA)) {
            CarServicePropUtils.getInstance().setIntProp(H56C.IVI_AVMSet_IVI_VOICE_APAONOFFREQ, value ? 1 : 2);
        } else if (key.equalsIgnoreCase(AdasSignal.ADAS_AVM)) {
            CarServicePropUtils.getInstance().setIntProp(H56C.IVI_AVMSet_IVI_VOICE_AVMONOFFREQ, value ? 1 : 2);
        } else if (key.equalsIgnoreCase(AdasSignal.ADAS_CAR_TRANSPARENT)) {
            CarServicePropUtils.getInstance().setIntProp(H56C.IVI_AVMSet_IVI_VOICE_CARBODYTRANSPARENTONOFFREQ, value ? 1 : 2);
        } else {
            super.setBaseBooleanProp(key, area, value);
        }
    }

    @Override
    public void registerDevice() {
        //  -1=不需要 1=需要   是否需要判断商城权益字段 37A 37B 56C需要判断， 56D不需要
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_NEED_MEMBERSHIP_PRIVILEGES, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //  -1=不支持 1=支持   暂时只有37A支持激活NOA
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_SUPPORT_NOA_ACTIVATE_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "-1");
        //  寻迹倒车37A支持
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_SUPPORT_RDA_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,"-1");
    }
}
