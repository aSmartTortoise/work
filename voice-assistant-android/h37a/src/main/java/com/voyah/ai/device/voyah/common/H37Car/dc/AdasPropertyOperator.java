package com.voyah.ai.device.voyah.common.H37Car.dc;

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

import mega.car.Signal;
import mega.car.config.Adas;
import mega.car.config.Apa;
import mega.car.config.Driving;
import mega.car.config.ParamsCommon;

/**
 * @Date 2024/9/6 16:29
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class AdasPropertyOperator extends Base37Operator implements IDeviceRegister {
    private static final String TAG = AdasPropertyOperator.class.getSimpleName();

    @Override
    void init() {
        map.put(AdasSignal.ADAS_CHASSIS_STYLE, Driving.ID_DRV_CHASSIS_MODESET);
        map.put(AdasSignal.ADAS_FCW_SWITCH, Adas.ID_ADAS_FCW_SWITCH_CONFIG);
        map.put(AdasSignal.ADAS_FCW_SENSITIVITY, Adas.ID_ADAS_FCW_SENSITIVITY_CONFIG);
        map.put(AdasSignal.ADAS_AEB_SWITCH, Adas.ID_ADAS_AEB_AUTO_EMERGENCY_BRAKING_CONFIG);
        map.put(AdasSignal.ADAS_RCW_SWITCH, Adas.ID_ADAS_RCW_CONFIG);
        map.put(AdasSignal.ADAS_MEB_SWITCH, Apa.ID_APA_MEB_SW_STS);
        map.put(AdasSignal.ADAS_LDA_STATE, Adas.ID_ADAS_LANE_DEPARTURE_WARNING_CONFIG);
        map.put(AdasSignal.ADAS_LDA_WARNING_STYLE, Adas.ID_ADAS_LDW_FUNCTWARNMOD_CONFIG);
        map.put(AdasSignal.ADAS_ELK_SWITCH, Adas.ID_ADAS_EMERGENCY_LANE_KEEPING_CONFIG);
        map.put(AdasSignal.ADAS_ESA_SWITCH, Adas.ID_ADAS_EMERGENCY_STEERING_ASSIST_CONFIG);
        map.put(AdasSignal.ADAS_LCA_SWITCH, Adas.ID_ADAS_LANE_CHANGE_ASSIST_CONFIG);
        map.put(AdasSignal.ADAS_LCA_SENSITIVITY, Adas.ID_ADAS_LCA_SENSITIVITY_CONFIG);
        map.put(AdasSignal.ADAS_FCTA_SWITCH, Adas.ID_ADAS_FORWARD_CROSSING_TRAFFIC_ALERTING_CONFIG);
        map.put(AdasSignal.ADAS_RCTA_SWITCH, Adas.ID_ADAS_RCTA_CONFIG);
        map.put(AdasSignal.ADAS_DOW_SWITCH, Adas.ID_ADAS_DOOR_OPEN_WARNING_CONFIG);
        map.put(AdasSignal.ADAS_FVSR_SWITCH, Adas.ID_ADAS_FRONT_VEHICLE_START_REMINDER_CONFIG);
        map.put(AdasSignal.ADAS_TLC_SWITCH, Adas.ID_ADAS_TRAFFIC_LANE_CHANGING_CONFIG);
        map.put(AdasSignal.ADAS_BSD_SWITCH, Adas.ID_ADAS_BSD_CONFIG);
        map.put(AdasSignal.ADAS_TSR_SWITCH, Adas.ID_ADAS_TSR_SWITCH_CONFIG);
        map.put(AdasSignal.ADAS_ASSIST_DRV_STYLE, Adas.ID_ASSID_DRVG_STYLE);
        map.put(AdasSignal.ADAS_NOA_SWITCH, Adas.ID_ADAS_NAVIGATION_ON_DRIVING_ASSIST_CONFIG);
        map.put(AdasSignal.ADAS_ALCO_SWITCH, Adas.ID_ADAS_NOA_LOWSPD_ALC_CONFIG);
        map.put(AdasSignal.ADAS_LCOC_SWITCH, Adas.ID_ADAS_NOA_ALC_CONFIRM_CONFIG);
        map.put(AdasSignal.ADAS_EOL_SWITCH, Adas.ID_ADAS_NOA_OVERTAKINGBACK_ALC_CONFIG);
        map.put(AdasSignal.ADAS_ISA_SWITCH, Adas.ID_ADAS_ISA_CONFIG);
        map.put(AdasSignal.ADAS_OWA_SWITCH, Adas.ID_ADAS_OVERSPD_ALERT);
        map.put(AdasSignal.ADAS_ISLC_SWITCH, Adas.ID_ADAS_ISLC_CONFIG);

        map.put(AdasSignal.ADAS_NOA_DISPSTS, Adas.ID_ADAS_NOA_DISPSTS);
        map.put(AdasSignal.ADAS_ICA_DISPSTS, Adas.ID_ADAS_ICA_DISPSTS);
        map.put(AdasSignal.ADAS_ACTIVATE_NOA, Signal.ID_NOAACTIVEREQ);
        map.put(AdasSignal.ADAS_WARD, Signal.ID_TLCACTIVEREQ);
        map.put(AdasSignal.ADAS_CUR_WORKSHOP_TIME_INTERVAL, Signal.ID_TIMEGAPADJUSTREQ);
        map.put(AdasSignal.ADAS_CRUISESPEED, Adas.ID_ADAS_ACC_TARGETSPEED);
        map.put(AdasSignal.ADAS_APA, Apa.ID_APA_VOICE_APAONOFFREQ);
        map.put(AdasSignal.ADAS_HPP, Signal.ID_HPPVOICEONOFFREQ);
        map.put(AdasSignal.ADAS_RAD, Apa.ID_RDA_VOICE_ONOFF_REQ);
        map.put(AdasSignal.ADAS_AVM, Apa.ID_AVM_VOICE_ONOFF_REQ);
        map.put(AdasSignal.ADAS_CAR_TRANSPARENT, Apa.ID_AVM_CARTRANSPARENT_ONOFF_REQ);
    }

    @Override
    public int getBaseIntProp(String key, int area) {

        if (key.startsWith(AdasSignal.ADAS_TDA4STATE)) {
            String func = key.substring(AdasSignal.ADAS_TDA4STATE.length());
            ContentResolver cr = Utils.getApp().getContentResolver();
            LogUtils.d(TAG, "adas : func is " + func);
            return Settings.Global.getInt(cr, func, 0);
        }

        if (key.equalsIgnoreCase(AdasSignal.ADAS_LCA_SENSITIVITY)) {
            return getCommonInt(key, area) + 1; //LCASensitivity [0,1,2] -> [1,2,3]
        } else if (key.equalsIgnoreCase(AdasSignal.ADAS_AVM_VIEW)) {
            return CarServicePropUtils.getInstance().getIntProp(Apa.ID_APA_AVM_3D2DVIEWMODE);
        } else {
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
        if (key.startsWith(AdasSignal.ADAS_FUNC_SUPPORT)) {  //37A是都支持
            return true;
        }
        switch (key) {
            case AdasSignal.ADAS_SUPPORT_OPEN_APP:
            case AdasSignal.ADAS_ASSIST_DRIVE_CONFIG:
            case AdasSignal.ADAS_LCA_CONFIG:
            case AdasSignal.ADAS_FCTA_CONFIG:
            case AdasSignal.ADAS_FVSR_CONFIG:
            case AdasSignal.ADAS_NOA_CONFIG:
            case AdasSignal.ADAS_ALCO_CONFIG:
            case AdasSignal.ADAS_OWA_CONFIG:
                return true;
            case AdasSignal.ADAS_CDC_CONFIG:
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_CONTINUOUS_DAMPING_CTRL, 1) == 1;
            case AdasSignal.ADAS_NOA_SWITCH:
                return CarServicePropUtils.getInstance().getIntProp(Adas.ID_ADAS_NAVIGATION_ON_DRIVING_ASSIST_CONFIG) == 1;
            case AdasSignal.ADAS_NOA_ACTIVATE_STATE:
                int noaStart = CarServicePropUtils.getInstance().getIntProp(Adas.ID_ADAS_NOA_DISPSTS);
                int cadStart = CarServicePropUtils.getInstance().getIntProp(Adas.ID_ADAS_ICA_DISPSTS);
                return cadStart == 1 || noaStart == 2;
            default:
                return super.getBaseBooleanProp(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        if (key.equalsIgnoreCase(AdasSignal.ADAS_APA)) {
            CarServicePropUtils.getInstance().setIntProp(Apa.ID_APA_VOICE_APAONOFFREQ, value ? ParamsCommon.OnOff.ON : ParamsCommon.OnOff.OFF);
        } else if (key.equalsIgnoreCase(AdasSignal.ADAS_HPP)) {
            CarServicePropUtils.getInstance().setIntProp(Signal.ID_HPPVOICEONOFFREQ, value ? ParamsCommon.OnOff.ON : ParamsCommon.OnOff.OFF);
        } else if (key.equalsIgnoreCase(AdasSignal.ADAS_RAD)) {
            CarServicePropUtils.getInstance().setIntProp(Apa.ID_RDA_VOICE_ONOFF_REQ, value ? ParamsCommon.OnOff.ON : ParamsCommon.OnOff.OFF);
        } else if (key.equalsIgnoreCase(AdasSignal.ADAS_AVM)) {
            CarServicePropUtils.getInstance().setIntProp(Apa.ID_AVM_VOICE_ONOFF_REQ, value ? ParamsCommon.OnOff.ON : ParamsCommon.OnOff.OFF);
        } else if (key.equalsIgnoreCase(AdasSignal.ADAS_CAR_TRANSPARENT)) {
            CarServicePropUtils.getInstance().setIntProp(Apa.ID_AVM_CARTRANSPARENT_ONOFF_REQ, value ? 2 : 1);
        } else if (key.equalsIgnoreCase(AdasSignal.ADAS_AVM_VIEW)) {
            CarServicePropUtils.getInstance().setIntProp(Apa.ID_APA_AVM3D2DSW, 1);
        } else {
            super.setBaseBooleanProp(key, area, value);
        }
    }

    @Override
    public void registerDevice() {
        //  -1=不需要 1=需要   是否需要判断商城权益字段 37A 37B 56C需要判断， 56D不需要
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_NEED_MEMBERSHIP_PRIVILEGES, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //  -1=不支持 1=支持   37A 37B 56D支持激活NOA（56D是引导手动激活）  56C没有该功能，直接兜底
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_SUPPORT_NOA_ACTIVATE_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //  -1=执行 1=提示手动操作   激活NOA是否提示手动操作 37A 37B 直接执行  56D需要提示手动操作
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_NOA_ACTIVATE_HINT_HAND_MOVEMENT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "-1");
        //  寻迹倒车37A支持
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_SUPPORT_RDA_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,"1");
        //  是否是执行变道功能，还有可能是提示手动操作 37A 37B 56D是执行功能 56C是提示手动操作
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_WARD_IS_EXEC, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,"1");
        //  是否需要安全判断 37A 37B需要 56D不需要
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_NEED_SAFETY_ASSESSMENT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,"1");
    }
}
