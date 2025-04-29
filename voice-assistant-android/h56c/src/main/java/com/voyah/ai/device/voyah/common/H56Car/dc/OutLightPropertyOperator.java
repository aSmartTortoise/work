package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.LightSignal;

import mega.car.config.H56C;
import mega.car.config.Lighting;

/**
 * @Date 2024/8/1 10:29
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class OutLightPropertyOperator extends Base56Operator {

    @Override
    void init() {
        map.put(LightSignal.LAMP_MODE, H56C.BCM_peps_BCM_LAMPSWITCHFDB);//近光灯  read信号
        map.put(LightSignal.LAMP_FOG, Lighting.ID_TELLTALE_FOG_LAMP);//打开关闭雾灯信号  read 信号
        map.put(LightSignal.LAMP_SWITCH, H56C.BCM_state1_BCM_LOWBEAMSTS);//近光灯状态
        map.put(LightSignal.LAMP_POSITION, H56C.BCM_state1_BCM_POSLAMPSTS);//位置灯状态
        map.put(LightSignal.LAMP_HEIGHT, H56C.GW_HCM_RLM_HCM_HCMHEIGHTSETLT);//大灯高度 read信号

        /**近光灯软开关信号   write信号
         * 0x0: No Request
         * 0x1: Passed
         * 0x2: No passed
         * 0x3: invalid
         */
        map.put(LightSignal.LOW_BEAM,H56C.IVI_BodySet3_IVI_EXTERNALLIGHTSWITCH);
        //自动灯光软开关信号   write信号
        map.put(LightSignal.LIGHT_AUTOSWITCH,H56C.IVI_BodySet3_IVI_EXTERNALLIGHTSWITCH);
        //位置灯软开关信号   write信号
        map.put(LightSignal.LIGHT_POSITIONSWITCH,H56C.IVI_BodySet3_IVI_EXTERNALLIGHTSWITCH);
        //外灯关闭软开关信号   write信号
        map.put(LightSignal.LIGHT_OFFLAMP,H56C.IVI_BodySet3_IVI_EXTERNALLIGHTSWITCH);
        //大灯高度   write信号
        map.put(LightSignal.LIGHR_HEIGHT, H56C.IVI_ACHCMCtrl_IVI_HCMHEIGHTSET);


        map.put(LightSignal.LAMP_SETFOGREAR,H56C.IVI_BodySet2_IVI_REARFOGLAMPSWITCH);//后雾灯 write 信号

        map.put(LightSignal.LIGHT_CEILINGLEFT,H56C.IVI_BD_CFSet_IVI_ATLRLREQ);//左侧航空灯

        map.put(LightSignal.LIGHT_CEILINGRIGHT,H56C.IVI_BD_CFSet_IVI_ATLRRREQ);//右侧航空灯
    }


    @Override
    public void setBaseIntProp(String key, int area, int value) {
        setCommonInt(key, area, value);
    }
}
