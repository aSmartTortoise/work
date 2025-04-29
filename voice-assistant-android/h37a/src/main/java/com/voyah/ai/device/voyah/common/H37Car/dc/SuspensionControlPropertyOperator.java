package com.voyah.ai.device.voyah.common.H37Car.dc;



import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.signal.SuspensionSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;

import mega.car.config.Driving;

@CarDevices(carType = CarType.H37_CAR)
public class SuspensionControlPropertyOperator extends Base37Operator implements IDeviceRegister {
    private static final String TAG = "SuspensionControlPropertyOperator";
    @Override
    void init() {



    }

    @Override
    public int getBaseIntProp(String key, int area) {

        return super.getBaseIntProp(key, area);

    }


    @Override
    public void registerDevice() {
        LogUtils.i(TAG, "悬架注册");


        String isSupportValue;
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equals("H56C") || carType.equals("H56D")) {
            isSupportValue = "1";
        } else {
            isSupportValue = "-1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SuspensionSignal.SUSPENSION_ISSUPPORT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, isSupportValue);

    }
}
