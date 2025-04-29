package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.ReadingLightSignal;
import com.voyah.ai.device.voyah.common.H56Car.ReadingLightHelper;

import mega.car.config.H56C;
import mega.car.config.Lighting;

/**
 * @Date 2024/8/1 14:14
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class ReadingLightPropertyOperator extends Base56Operator {
    @Override
    void init() {
        map.put(ReadingLightSignal.READING_LIGHT_SWITCH, Lighting.ID_CABIN_LIGHT_ON_OFF);
        map.put(ReadingLightSignal.DOME_LIGHT_SWITCH, H56C.IVI_BodySet1_IVI_ITLTOPLAMPREQ);

        map.put(ReadingLightSignal.INLIGHT_THIRDROWLHREADLIGHTREQ,H56C.IVI_BodySet3_IVI_THIRDROWLHREADLIGHTREQ);//H56c H56D 专用  第三排左侧 阅读write信号

        map.put(ReadingLightSignal.INLIGHT_THIRDROWRHREADLIGHTREQ,H56C.IVI_BodySet3_IVI_THIRDROWRHREADLIGHTREQ);//H56c H56D 专用  第三排右侧 阅读write信号

        map.put(ReadingLightSignal.INLIGHT_FLREADLIGHTSTS,H56C.GW_LIN_BODY_ITL_FLREADLIGHTSTS);//主驾阅读灯read信号

        map.put(ReadingLightSignal.INLIGHT_FRREADLIGHTSTS,H56C.GW_LIN_BODY_ITL_FRREADLIGHTSTS);//副驾阅读灯read信号

        map.put(ReadingLightSignal.INLIGHT_RLREADLIGHTSTS,H56C.GW_LIN_BODY_ITL_RLREADLIGHTSTS);//二排左阅读灯read信号

        map.put(ReadingLightSignal.INLIGHT_RRREADLIGHTSTS,H56C.GW_LIN_BODY_ITL_RRREADLIGHTSTS);//二排右阅读灯raad信号

        map.put(ReadingLightSignal.INLIGHT_THIRDROWLHREADLIGHTSTS,H56C.GW_LIN_BODY_ITL_THIRDROWLHREADLIGHTSTS);//三排左阅读灯read信号

        map.put(ReadingLightSignal.INLIGHT_THIRDROWRHREADLIGHTSTS,H56C.GW_LIN_BODY_ITL_THIRDROWRHREADLIGHTSTS);//三排右阅读灯raad信号

    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (ReadingLightSignal.READING_LIGHT_SWITCH.equals(key)) {
            return ReadingLightHelper.getReadingLightStatus(area);
        } else {
            return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {// 1是关闭  2是打开
        int area_37 = getRealArea(area);
        if (ReadingLightSignal.READING_LIGHT_SWITCH.equals(key)) {
            ReadingLightHelper.setReadingLightState(area_37, value);
        } else {
            setCommonInt(key, area, value);
        }
    }
}
