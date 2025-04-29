package com.voyah.ai.device.voyah.common.H56Car;

import android.os.RemoteException;

import com.voice.sdk.device.carservice.constants.ITailGate;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.cockpit.systemui.ISystemUIInterface;


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
        // 37 读 全关=0 全开=1 开启中=2 关闭中=3 暂停中=4 扣锁中=6
        // 56 读 全开=0 全关=1 开启中=2 关闭中=3 暂停中=4 扣锁中=6
        int value = CarPropUtils.getInstance().getIntProp(EntryLocks.ID_DOOR, VehicleArea.OUTSIDE_REAR);
        if (value == 0) {
            return 1;
        }
        if (value == 1) {
            return 0;
        }
        return value;
    }

    public static void setTailGateState(int state) {
        // 37 写 全关=2 全开=1 暂停=3
        // 56 写 全关=1 全开=2 暂停=3
        int mappedValue = 0;
        switch (state) {
            case ITailGate.TAILGATE_FULLYOPENED:
                mappedValue = 2;
                break;
            case ITailGate.TAILGATE_FULLYCLOSED:
                mappedValue = 1;
                break;
            case ITailGate.TAILGATE_STOPPED:
                mappedValue = 3;
                break;
        }
        CarPropUtils.getInstance().setIntProp(EntryLocks.ID_DOOR, VehicleArea.OUTSIDE_REAR, mappedValue);
    }

    public static void showUnoperableDiglog() {
        try {
            ISystemUIInterface.Stub.getDefaultImpl().showAntiPlayDialog();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
