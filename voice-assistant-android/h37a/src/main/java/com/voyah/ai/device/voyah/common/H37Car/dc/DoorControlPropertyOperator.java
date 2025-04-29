package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.VCarSeatSignal;
import com.voice.sdk.device.carservice.signal.DoorSignal;

import mega.car.Signal;


@CarDevices(carType = CarType.H37_CAR)
public class DoorControlPropertyOperator extends Base37Operator  {

    @Override
    void init() {

        map.put(DoorSignal.DOOR_ELECTRIC_SUCTION, Signal.ID_DOOR_SUCTION_RELEASE_SETTING);//电吸门开关 read_write

    }


}
