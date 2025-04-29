package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.DoorSignal;

import mega.car.config.H56D;

@CarDevices(carType = CarType.H37_CAR)
public class DoorControlPropertyOperator extends Base56Operator  {

    @Override
    void init() {
        map.put(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_LEFT, H56D.PSD_L_PSD_L_PSDENADBLESTA);//左侧滑移门电动开关是否开启
        map.put(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_RIGHT, H56D.PSD_R_PSD_R_PSDENADBLESTA);//右侧滑移门电动开关是否开启


        map.put(DoorSignal.DOOR_SWITCH_LEFT, H56D.IVI_BodySet2_IVI_RLDOORCTL_CMD);//打开/关闭/暂停  左侧滑移门信号
        map.put(DoorSignal.DOOR_SWITCH_RIGHT, H56D.IVI_BodySet2_IVI_RRDOORCTL_CMD);//打开/关闭/暂停  右侧滑移门信号

        map.put(DoorSignal.DOOR_IS_OPEN_LEFT, H56D.PSD_L_PSD_L_RLDOOR_ST);//左侧滑移门是否已开启  已关闭
        map.put(DoorSignal.DOOR_IS_OPEN_RIGHT, H56D.PSD_R_PSD_R_RRDOOR_ST);//右侧滑移门是否已开启  已关闭

        map.put(DoorSignal.DOOR_OPENING_LEFT, H56D.PSD_L_PSD_L_DOORWORKST_RSP);//左侧是否是开启中  关闭中
        map.put(DoorSignal.DOOR_OPENING_RIGHT, H56D.PSD_R_PSD_R_DOORWORKST_RSP);//右侧是否是开启中  关闭中

        map.put(DoorSignal.DOOR_ELECTRIC_SWITCH, H56D.IVI_BodySet3_IVI_PSDENADBLESET);//电动手动 滑移门切换开关

        map.put(DoorSignal.DOOR_ELECTRIC_SUCTION_GET, H56D.OIB_BCM_State3_BCM_DOORSUCTIONRELEASESETSTS);//电吸门开关 read
        map.put(DoorSignal.DOOR_ELECTRIC_SUCTION_SET, H56D.IVI_BodySet4_IVI_DOORSUCTIONRELEASESET);//电吸门开关 write

    }


}
