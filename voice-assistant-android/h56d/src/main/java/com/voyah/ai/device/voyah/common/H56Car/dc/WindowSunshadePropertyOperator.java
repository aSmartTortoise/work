package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.WindowSunshadeSignal;


import mega.car.config.H56D;

@CarDevices(carType = CarType.H37_CAR)
public class WindowSunshadePropertyOperator extends Base56Operator {


    @Override
    void init() {
        map.put(WindowSunshadeSignal.WINDOWSUNSHADE_GET_LEFT, H56D.GW_DCU_state_DCU_RLWNDSUNSHADEPOSSTS_0X27E);//查询左侧车窗遮阳帘的状态
        map.put(WindowSunshadeSignal.WINDOWSUNSHADE_GET_RIGHT, H56D.GW_DCU_state_DCU_RRWNDSUNSHADEPOSSTS_0X27E);//查询右侧车窗遮阳帘的状态


        map.put(WindowSunshadeSignal.WINDOWSUNSHADE_SET_LEFT, H56D.IVI_BD_CFSet_IVI_RLWINSUNSHADECMD);//设置左侧车窗遮阳帘的状态
        map.put(WindowSunshadeSignal.WINDOWSUNSHADE_SET_RIGHT, H56D.IVI_BD_CFSet_IVI_RRWINSUNSHADECMD);//设置右侧车窗遮阳帘的状态


    }

}
