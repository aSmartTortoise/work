package com.voyah.ai.device.voyah.common.H56Car.dc;



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
import mega.car.config.H56C;
@CarDevices(carType = CarType.H37_CAR)
public class SuspensionControlPropertyOperator extends Base56Operator implements IDeviceRegister {
    private static final String TAG = "SuspensionControlPropertyOperator";
    @Override
    void init() {


        map.put(SuspensionSignal.SUSPENSION_ADJUSTABLE,H56C.ASC_state1_ASC_SUSTEMPUNADJUSTABLEREASON);//悬架是否可以调节

        map.put(SuspensionSignal.SUSPENSION_OUTINGMODE, Driving.ID_DRV_MODE);//郊游模式判断

        map.put(SuspensionSignal.SUSPENSION_RISING_FALLING,H56C.IVI_chassisSet_IVI_MANUALEASYOUTSW);//手动调节悬架  上升 下降

        map.put(SuspensionSignal.SUSPENSION_BOARDINGCAR,H56C.ASC_state1_ASC_AUTOEASYENTRYFB);//便捷上下车是否打开

        map.put(SuspensionSignal.SUSPENSION_HIGHSPEED,Driving.ID_DRV_HIGHWAY_MODE);//高速悬架自适应调节是否打开   打开关闭 高速悬架自适应调节

        map.put(SuspensionSignal.SUSPENSION_HEIGHTSPEEDADJUST,H56C.ASC_state1_ASC_SPEDAJUSTSETFB);//悬架高度随车速自动调节是否打开

        map.put(SuspensionSignal.SUSPENSION_LOADADJUST,H56C.ASC_state1_ASC_EASYPACKFB);//便捷载物是否打开

        map.put(SuspensionSignal.SUSPENSION_MAINTENANCEADJUST,Driving.ID_DRV_SUSPENSION_MAINTAIN);//悬架维修模式是否打开

        map.put(SuspensionSignal.SUSPENSION_DOINGCLOSEBOARDINGCAR,H56C.IVI_chassisSet_IVI_AUTOEASYENTRYSET);//打开 关闭 便捷上下车自动调节

        map.put(SuspensionSignal.SUSPENSION_DOINGHEIGHTSPEDCAR,H56C.IVI_chassisSet2_IVI_ASC_SPEDAJUSTSET);//打开关闭 悬架高度随车速自动调节


        map.put(SuspensionSignal.SUSPENSION_DOINGLOADADJUST,H56C.IVI_chassisSet_IVI_EASYPACKSET);//打开关闭 便捷载物

    }

    @Override
    public int getBaseIntProp(String key, int area) {

        switch (key) {
            case SuspensionSignal.SUSPENSION_ISN2N3://判断是否是N2 N3车型
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_EQUIPMENT_LEVEL, -1);
            default:
                return super.getBaseIntProp(key, area);
        }
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
