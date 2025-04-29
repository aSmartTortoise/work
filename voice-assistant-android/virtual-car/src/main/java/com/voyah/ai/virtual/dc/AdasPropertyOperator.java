package com.voyah.ai.virtual.dc;

import com.voice.sdk.device.carservice.signal.AdasSignal;
import com.voyah.ai.common.utils.BiDirectionalMap;

/**
 * @Date 2024/9/9 14:56
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class AdasPropertyOperator extends BaseVirtualPropertyOperator {

    private final BiDirectionalMap<Integer, String> chassisStyle = new BiDirectionalMap<>();
    private final BiDirectionalMap<Integer, String> ldaMap = new BiDirectionalMap<>(); //车道偏离辅助
    private final BiDirectionalMap<Integer, String> ldaWarningMap = new BiDirectionalMap<>(); //车道偏离辅助预警方式
    private final BiDirectionalMap<Integer, String> lcaSensitivity = new BiDirectionalMap<>(); //变道辅助预警灵敏度
    private final BiDirectionalMap<Integer, String> drvStyleMap = new BiDirectionalMap<>(); //智能行车风格
    private final BiDirectionalMap<Integer, String> tda4StateMap = new BiDirectionalMap<>(); //智驾包状态



    public AdasPropertyOperator() {
        chassisStyle.put(1, "comfort");
        chassisStyle.put(2, "standard");
        chassisStyle.put(3, "sport");
        ldaMap.put(0, "off");
        ldaMap.put(1, "warning_only");
        ldaMap.put(2, "warning_assist");
        ldaWarningMap.put(0, "voice");
        ldaWarningMap.put(1, "vibration");
        lcaSensitivity.put(1, "low");
        lcaSensitivity.put(2, "high");
        drvStyleMap.put(0, "standard");
        drvStyleMap.put(1, "comfort");
        drvStyleMap.put(2, "efficiency");
        tda4StateMap.put(0, "unpaid_unsupported");
        tda4StateMap.put(1, "unpaid_supported");
        tda4StateMap.put(2, "paid_unsupported");
        tda4StateMap.put(3, "paid_supported");
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (key.startsWith(AdasSignal.ADAS_TDA4STATE)) {
            String tda4State = (String) getValue(key);
            return tda4StateMap.getReverse(tda4State.toLowerCase());
        }
        switch (key) {
            case AdasSignal.ADAS_CHASSIS_STYLE:
                String style = (String) getValue(key);
                return chassisStyle.getReverse(style.toLowerCase());
            case AdasSignal.ADAS_LDA_STATE:
                String state = (String) getValue(key);
                return ldaMap.getReverse(state.toLowerCase());
            case AdasSignal.ADAS_LDA_WARNING_STYLE:
                String warningStyle = (String) getValue(key);
                return ldaWarningMap.getReverse(warningStyle.toLowerCase());
            case AdasSignal.ADAS_LCA_SENSITIVITY:
                String sense = (String) getValue(key);
                return lcaSensitivity.getReverse(sense.toLowerCase());
            case AdasSignal.ADAS_ASSIST_DRV_STYLE:
                String drvStyle = (String) getValue(key);
                return drvStyleMap.getReverse(drvStyle.toLowerCase());
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (key.startsWith(AdasSignal.ADAS_TDA4STATE)) {
            String tda4State = tda4StateMap.getForward(value);
            setValue(key, tda4State);
            return;
        }
        switch (key) {
            case AdasSignal.ADAS_CHASSIS_STYLE:
                String style = chassisStyle.getForward(value);
                setValue(key, style);
            case AdasSignal.ADAS_LDA_STATE:
                String state = ldaMap.getForward(value);
                setValue(key, state);
            case AdasSignal.ADAS_LDA_WARNING_STYLE:
                String warningStyle = ldaWarningMap.getForward(value);
                setValue(key, warningStyle);
            case AdasSignal.ADAS_LCA_SENSITIVITY:
                String sense = lcaSensitivity.getForward(value);
                setValue(key, sense);
            case AdasSignal.ADAS_ASSIST_DRV_STYLE:
                String drvStyle = drvStyleMap.getForward(value);
                setValue(key, drvStyle);
            default:
                super.setBaseIntProp(key, area, value);
        }
    }
}
