package com.voyah.ai.device.voyah.common.H37Car;

import com.voice.sdk.device.carservice.constants.ITailGate;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.Signal;
import mega.car.VehicleArea;
import mega.car.config.EntryLocks;

/**
 * @Date 2024/8/12 15:01
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class TailGateHelper {

    public static int getTailGateState() {
        return CarPropUtils.getInstance().getIntProp(EntryLocks.ID_DOOR, VehicleArea.OUTSIDE_REAR);
    }

    public static void setTailGateState(int state) {
        int mappedValue = Signal.OpenCloseStopInvalid.INVALID;
        switch (state) {
            case ITailGate.TAILGATE_FULLYOPENED:
                mappedValue = Signal.OpenCloseStopInvalid.OPEN;
                break;
            case ITailGate.TAILGATE_FULLYCLOSED:
                mappedValue = Signal.OpenCloseStopInvalid.CLOSE;
                break;
            case ITailGate.TAILGATE_STOPPED:
                mappedValue = Signal.OpenCloseStopInvalid.STOP;
                break;
        }
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_DOOR, VehicleArea.OUTSIDE_REAR, mappedValue);
    }
}
