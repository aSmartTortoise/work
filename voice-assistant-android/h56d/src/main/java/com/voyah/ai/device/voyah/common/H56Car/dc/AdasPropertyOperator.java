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

import mega.car.config.Driving;
import mega.car.config.H56D;

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
        map.put(AdasSignal.ADAS_FCW_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_FCWONOFFSTS);
        map.put(AdasSignal.ADAS_FCW_SENSITIVITY, H56D.ADS_FunctionOnOffFeedback_ADS_FCWSENSITIVITYSET);
        map.put(AdasSignal.ADAS_AEB_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_AEBONOFFSTS);
        map.put(AdasSignal.ADAS_RCW_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_RCWONOFFSTS);
//        map.put(AdasSignal.ADAS_MEB_SWITCH, H56D.APA_control2_APA_MEB_STS);
        map.put(AdasSignal.ADAS_LDA_STATE, H56D.ADS_FunctionOnOffFeedback_ADS_LASONOFFSTS);
        map.put(AdasSignal.ADAS_LDA_WARNING_STYLE,H56D.ADS_FunctionOnOffFeedback_ADS_LASTYPE);
        map.put(AdasSignal.ADAS_ELK_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_ELKONOFFSTS);
        map.put(AdasSignal.ADAS_ESA_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_ESAONOFFSTS);
//        map.put(AdasSignal.ADAS_LCA_SWITCH, H56D.ID_ADAS_LANE_CHANGE_ASSIST_CONFIG);
//        map.put(AdasSignal.ADAS_LCA_SENSITIVITY, H56D.ID_ADAS_LCA_SENSITIVITY_CONFIG);
        map.put(AdasSignal.ADAS_FCTA_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_FCWONOFFSTS);
        map.put(AdasSignal.ADAS_RCTA_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_RCWONOFFSTS);
        map.put(AdasSignal.ADAS_DOW_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_DOWONOFFSTS);
        map.put(AdasSignal.ADAS_FVSR_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_FVSRONOFFSTS);
        map.put(AdasSignal.ADAS_TLC_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_TLCONOFFSTS);
        map.put(AdasSignal.ADAS_BSD_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_BSDONOFFSTS);
        map.put(AdasSignal.ADAS_TSR_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_TSRONOFFSTS);
        map.put(AdasSignal.ADAS_ASSIST_DRV_STYLE, H56D.ADS_FunctionOnOffFeedback_ADS_ADSSTYLESTS);
        map.put(AdasSignal.ADAS_NOA_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_NOAONOFFSTS);
//        map.put(AdasSignal.ADAS_ALCO_SWITCH, H56D.ID_ADAS_NOA_LOWSPD_ALC_CONFIG);
        map.put(AdasSignal.ADAS_LCOC_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_ALC_LANECHANGECONFIRM);
//        map.put(AdasSignal.ADAS_EOL_SWITCH, H56D.ID_ADAS_NOA_OVERTAKINGBACK_ALC_CONFIG);
        map.put(AdasSignal.ADAS_ISA_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_ISAONOFFSTS);
        map.put(AdasSignal.ADAS_OWA_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_OVERSPDWARNINGONOFFSTS);
        map.put(AdasSignal.ADAS_ISLC_SWITCH, H56D.ADS_FunctionOnOffFeedback_ADS_ISLCONOFFSTS);
        map.put(AdasSignal.ADAS_APA, H56D.IVI_AVMSet_IVI_VOICE_APAONOFFREQ);
        map.put(AdasSignal.ADAS_AVM, H56D.IVI_AVMSet_IVI_VOICE_AVMONOFFREQ);
        map.put(AdasSignal.ADAS_CAR_TRANSPARENT, H56D.IVI_AVMSet_IVI_VOICE_CARBODYTRANSPARENTONOFFREQ);

        map.put(AdasSignal.ADAS_LANE_DEPARTURE_GEAR_INQUIRY_GET,H56D.ADS_FunctionOnOffFeedback_ADS_LASSENSITIVITY);//车道偏离灵敏度设置  get
        map.put(AdasSignal.ADAS_LANE_DEPARTURE_GEAR_INQUIRY_SET,H56D.IVI_ADSSet_IVI_LASSENSITIVITYSET);//车道偏离灵敏度设置  set

        map.put(AdasSignal.ADAS_AUTO_EMERGENCY_STOP_BACK_GET,H56D.ADS_FunctionOnOffFeedback_ADS_RAEBONOFFSTS);//自动(后向)紧急制动 get
        map.put(AdasSignal.ADAS_AUTO_EMERGENCY_STOP_BACK_SET,H56D.IVI_ADSSet_IVI_RAEBONOFFSET);//自动(后向)紧急制动 set

        map.put(AdasSignal.ADAS_AUTO_EMERGENCY_STEERING_GET,H56D.ADS_FunctionOnOffFeedback_ADS_AESONOFFSTS);//自动紧急转向辅助 get
        map.put(AdasSignal.ADAS_AUTO_EMERGENCY_STEERING_SET,H56D.IVI_ADSSet_IVI_AESONOFFSET);//自动紧急转向辅助 set

        map.put(AdasSignal.ADAS_TRAFFIC_LIGHT_GET,H56D.ADS_FunctionOnOffFeedback_ADS_GLCONOFFSTS);//红绿灯提醒辅助 get
        map.put(AdasSignal.ADAS_TRAFFIC_LIGHT_SET,H56D.IVI_ADSSet_IVI_GLCONOFFSET);//红绿灯提醒辅助 set

        map.put(AdasSignal.ADAS_OVER_SPEED_TARGET_GET,H56D.ADS_FunctionOnOffFeedback_ADS_OVERSPDWARNINGONMODESTS);//超速报警提醒报警方式 get
        map.put(AdasSignal.ADAS_OVER_SPEED_TARGET_SET,H56D.IVI_ADSSet_IVI_OVERSPDWARNINGONMODESET);//超速报警提醒报警方式 set

        map.put(AdasSignal.ADAS_LIGHTS_AND_SOUND_GET,H56D.ADS_FunctionOnOffFeedback_ADS_LIGHTFLASHANDHONKINGSTS);//闪灯鸣笛提醒开关 get
        map.put(AdasSignal.ADAS_LIGHTS_AND_SOUND_SET,H56D.IVI_ADSSet_IVI_LIGHTFLASHANDHONKINGSET);//闪灯鸣笛提醒开关 set

        map.put(AdasSignal.ADAS_LIGHTS_AND_SOUND_TARGET_GET,H56D.ADS_FunctionOnOffFeedback_ADS_LIGHTFLASHANDHONKINGMODE);//闪灯鸣笛提醒方式 get
        map.put(AdasSignal.ADAS_LIGHTS_AND_SOUND_TARGET_SET,H56D.IVI_ADSSet_IVI_LIGHTFLASHANDHONKINGMODE);//闪灯鸣笛提醒方式 set

        map.put(AdasSignal.ADAS_BLIND_SPOT_MONITORING_GET,H56D.ADS_FunctionOnOffFeedback_ADS_BSDONOFFSTS);//盲区监测报警提醒方式 get
//        map.put(AdasSignal.ADAS_BLIND_SPOT_MONITORING_GET,H56D.ADS_FunctionOnOffFeedback_ADS_BSDMODESTS);//盲区监测报警提醒方式 get

        map.put(AdasSignal.ADAS_CUR_WORKSHOP_TIME_INTERVAL,H56D.ADS_DisplayReq2_ADS_TIMEGAPADJUSTMENT);//车间距调节 get
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
                return getCommonInt(key, area) + 1;
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
            case AdasSignal.ADAS_ALCO_CONFIG:
            case AdasSignal.ADAS_OWA_CONFIG:
                return false;
            case AdasSignal.ADAS_NOA_CONFIG:
                return true;
            case AdasSignal.ADAS_CDC_CONFIG:
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_CONTINUOUS_DAMPING_CTRL, 1) == 1;
            case AdasSignal.ADAS_ISA_SWITCH:
                return getCommonInt(key, area) > 1; //56C 智能限速控制 1是关闭，2是显示 3是提醒 4是控制
            case AdasSignal.ADAS_ISLC_SWITCH:
                return getCommonInt(key, area) > 1;
            case AdasSignal.ADAS_NOA_SWITCH:
                // 读 1=关 2=开
                return CarServicePropUtils.getInstance().getIntProp(H56D.ADS_FunctionOnOffFeedback_ADS_NOAONOFFSTS) == 2;
            case AdasSignal.ADAS_NOA_ACTIVATE_STATE:
                // "0x0: NOA 0x1: LCC 0x2: Error 0x3: Reserved"
                return CarServicePropUtils.getInstance().getIntProp(H56D.ADS_FunctionOnOffFeedback_ADS_NOA_LCC_SWITCH_STS) == 0;
            default:
                return super.getBaseBooleanProp(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        if (key.equalsIgnoreCase(AdasSignal.ADAS_APA)) {
            //0x1: OnOffReq_OFF
            //0x2: OnOffReq_ON
            CarServicePropUtils.getInstance().setIntProp(H56D.IVI_PASset_APAVOICEONOFFREQ, value ? 2 : 1);
        } else if (key.equalsIgnoreCase(AdasSignal.ADAS_AVM)) {
            CarServicePropUtils.getInstance().setIntProp(H56D.IVI_AVMSet_IVI_VOICE_AVMONOFFREQ, value ? 1 : 2);
        } else if (key.equalsIgnoreCase(AdasSignal.ADAS_CAR_TRANSPARENT)) {
            CarServicePropUtils.getInstance().setIntProp(H56D.IVI_AVMSet_IVI_VOICE_CARBODYTRANSPARENTONOFFREQ, value ? 1 : 2);
        } else {
            super.setBaseBooleanProp(key, area, value);
        }
    }

    @Override
    public void registerDevice() {
        //  -1=不需要 1=需要   是否需要判断商城权益字段 37A 37B 56C需要判断， 56D不需要
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_NEED_MEMBERSHIP_PRIVILEGES, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "-1");
        //  -1=不支持 1=支持   37A 37B 56D支持激活NOA（56D是引导手动激活）  56C没有该功能，直接兜底
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_SUPPORT_NOA_ACTIVATE_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //  -1=执行 1=提示手动操作   激活NOA是否提示手动操作 37A 37B 直接执行  56D需要提示手动操作
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_NOA_ACTIVATE_HINT_HAND_MOVEMENT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //  寻迹倒车37A支持
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_SUPPORT_RDA_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,"-1");
        //  变道是否是执行变道功能，还有可能是提示手动操作 37A 37B 56D是执行功能 56C是提示手动操作
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_WARD_IS_EXEC, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,"1");
        //  是否需要安全判断 37A 37B需要 56D不需要
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_NEED_SAFETY_ASSESSMENT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,"-1");
        //  车间距是否是执行调节车间距功能，还有可能是提示手动操作 37A 37B 56D是执行功能 56C是提示手动操作
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AdasSignal.ADAS_WORKSHOP_IS_EXEC, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,"1");
    }
}
