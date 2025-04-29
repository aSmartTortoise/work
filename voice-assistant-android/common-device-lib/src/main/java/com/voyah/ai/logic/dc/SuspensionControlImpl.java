package com.voyah.ai.logic.dc;


import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.signal.SuspensionSignal;

import com.voice.sdk.util.LogUtils;

import java.util.HashMap;


public class SuspensionControlImpl extends AbsDevices {

    private static final String TAG = SuspensionControlImpl.class.getSimpleName();


    @Override
    public String getDomain() {
        return "Suspension";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        LogUtils.i(TAG, "tts :" + str);
        return str;
    }

    /**
     * @param map 判断是不是N2 N3车型
     * @return
     */
    public boolean isHasCeilingScreen(HashMap<String, Object> map) {
        int mEquipmentLevelConfig = operator.getIntProp(SuspensionSignal.SUSPENSION_ISN2N3);
        LogUtils.i(TAG, "isHasCeilingScreen mEquipmentLevelConfig :" + mEquipmentLevelConfig);
        return mEquipmentLevelConfig == 3 || mEquipmentLevelConfig == 2;
    }

    /**
     * 悬架是否可调节
     */
    public boolean isAdjustable(HashMap<String, Object> map) {
        boolean isOk = true;//默认可调节
        try {
            Thread.sleep(1000); // 暂停1000毫秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 重置中断状态
        }

//        int adjustable = carPropHelper.getIntProp(H56C.ASC_state1_ASC_SUSTEMPUNADJUSTABLEREASON);

        int adjustable = operator.getIntProp(SuspensionSignal.SUSPENSION_ADJUSTABLE);
        LogUtils.i(TAG, "isAdjustable  ====:" + adjustable);
        if (adjustable != 0) {//0 是可调节，否则不可调节
            isOk = false;
        }
        LogUtils.i(TAG, "isAdjustable  isOK===" + isOk);
        return isOk;
    }

    /**
     * 悬架是否是郊游模式的判断  如果是郊游模式，则不可以调节
     */

    public boolean isOutingMode(HashMap<String, Object> map) {
        boolean isOuting = false;//默认不是郊游模式
//        int drivingMode = carPropHelper.getIntProp(Driving.ID_DRV_MODE);
        int drivingMode = operator.getIntProp(SuspensionSignal.SUSPENSION_OUTINGMODE);
        if (drivingMode == 5) {//代表是郊游模式
            isOuting = true;
        }
        return isOuting;
    }

    /**
     * 执行悬架手动调节  调高悬架  恢复悬架 上升悬架
     */
    public void raiseSuspension(HashMap<String, Object> map) {
        LogUtils.i(TAG, "raiseSuspension ----- :");
//        carPropHelper.setIntProp(H56C.IVI_chassisSet_IVI_MANUALEASYOUTSW, 1);
        operator.setIntProp(SuspensionSignal.SUSPENSION_RISING_FALLING, 1);
    }

    /**
     * 执行悬架手动调节  调低悬架  下降悬架
     */
    public void declineSuspension(HashMap<String, Object> map) {
        LogUtils.i(TAG, "raiseSuspension ----- :");
//        carPropHelper.setIntProp(H56C.IVI_chassisSet_IVI_MANUALEASYOUTSW, 2);
        operator.setIntProp(SuspensionSignal.SUSPENSION_RISING_FALLING, 2);
    }

    /**
     * 判断当前是否是走 便捷上下车自动调节
     */
    public boolean isBoardingCar(HashMap<String, Object> map) {
        LogUtils.d("----isBoardingCar------便捷上下车自动调节----", "===start===============");
        boolean isBoardCar = false;
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        if (switch_mode == null) {
            return isBoardCar;
        }
        if (switch_mode.contains("convenient_boarding")) {
            isBoardCar = true;
        }
        return isBoardCar;
    }

    /**
     * 判断当前是否是走  高速悬架自适应调节
     */
    public boolean isHighSpeedCar(HashMap<String, Object> map) {
        LogUtils.d("----isHighSpeedCar------高速悬架自适应调节----", "===start===============");
        boolean isHighCar = false;
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        if (switch_mode == null) {
            return isHighCar;
        }
        if (switch_mode.contains("auto_highway")) {
            isHighCar = true;
        }
        return isHighCar;
    }

    /**
     * 判断当前是否是走  悬架高度随车速自动调节
     */
    public boolean isHeightSpeedCar(HashMap<String, Object> map) {
        LogUtils.d("----isHeightSpeedCar------悬架高度随车速自动调节----", "===start===============");
        boolean isHeightCar = false;
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        if (switch_mode == null) {
            return isHeightCar;
        }
        if (switch_mode.contains("auto_speed")) {
            isHeightCar = true;
        }
        return isHeightCar;
    }

    /**
     * 判断当前是否是走  便捷载物
     */
    public boolean isLoadAdjust(HashMap<String, Object> map) {
        LogUtils.d("----isLoadAdjust------便捷载物----", "===start===============");
        boolean isLoadCar = false;
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        if (switch_mode == null) {
            return isLoadCar;
        }
        if (switch_mode.contains("convenient_loading")) {
            isLoadCar = true;
        }
        return isLoadCar;
    }

    /**
     * 判断当前是否是走  悬架维修
     */
    public boolean isMaintenanceAdjust(HashMap<String, Object> map) {
        LogUtils.d("----isLoadAdjust------悬架维修----", "===start===============");
        boolean isMaintenanceCar = false;
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        if (switch_mode == null) {
            return isMaintenanceCar;
        }
        if (switch_mode.contains("repair")) {
            isMaintenanceCar = true;
        }
        return isMaintenanceCar;
    }


    //---------------------------------------------------------

    /**
     * @param map 判断 便捷上下车自动调节  是否打开
     * @return
     */
    public boolean boardingCar(HashMap<String, Object> map) {
        boolean unopen = false;
        LogUtils.d("----boardingCar------判断 便捷上下车自动调节----", "===start===============");
//        int state = carPropHelper.getIntProp(H56C.ASC_state1_ASC_AUTOEASYENTRYFB);
        int state = operator.getIntProp(SuspensionSignal.SUSPENSION_BOARDINGCAR);
        if (state == 1) {
            unopen = true;
        }
        return unopen;
    }

    /**
     * 判断高速悬架自适应调节是否打开
     */
    public boolean highSpeed(HashMap<String, Object> map) {
        boolean unopen = false;
        LogUtils.d("----highSpeed------判断 便捷上下车自动调节----", "===start===============");
//        int state = carPropHelper.getIntProp(Driving.ID_DRV_HIGHWAY_MODE);
        int state = operator.getIntProp(SuspensionSignal.SUSPENSION_HIGHSPEED);
        if (state == 1) {
            unopen = true;
        }
        return unopen;

    }


    /**
     * 判断悬架高度随车速自动调节是否打开
     */
    public boolean heightSpeedAdjust(HashMap<String, Object> map) {
        boolean unopen = false;

//        int state = carPropHelper.getIntProp(H56C.ASC_state1_ASC_SPEDAJUSTSETFB);
        int state = operator.getIntProp(SuspensionSignal.SUSPENSION_HEIGHTSPEEDADJUST);
        if (state == 1) {
            unopen = true;
        }
        return unopen;

    }

    /**
     * 判断便捷载物是否打开
     */
    public boolean loadAdjust(HashMap<String, Object> map) {
        boolean unopen = false;

//        int state = carPropHelper.getIntProp(H56C.ASC_state1_ASC_EASYPACKFB);
        int state = operator.getIntProp(SuspensionSignal.SUSPENSION_LOADADJUST);
        if (state == 1) {//1是打开，0是关闭
            unopen = true;
        }
        return unopen;

    }

    /**
     * 判断悬架维修模式是否打开
     */
    public boolean maintenanceAdjust(HashMap<String, Object> map) {
        boolean unopen = false;

//        int state = carPropHelper.getIntProp(Driving.ID_DRV_SUSPENSION_MAINTAIN);
        int state = operator.getIntProp(SuspensionSignal.SUSPENSION_MAINTENANCEADJUST);
        if (state == 1) {//1是打开，0是关闭
            unopen = true;
        }
        return unopen;
    }

    //-------------------------执行打开的逻辑-------------------

    /**
     * 执行打开便捷上下车自动调节  二次弹窗
     *
     * @param map
     */
    public void doingOpenBoardingCar(HashMap<String, Object> map) {
        LogUtils.d("-showSuspensionEasyBoardConfirm", "===start===============");
        int start = -1;
//        try {
//            start = iPageShowManager.showSuspensionEasyBoardConfirm();
//        } catch (RemoteException e) {
//            LogUtils.d("-showSuspensionEasyBoardConfirm e:"+ e.getMessage(),"===============");
//        }
        mSettingHelper.exec(SettingConstants.SUSPENSION_BOARDINGCAR);//便捷上下车二次弹窗
        LogUtils.d("-showSuspensionEasyBoardConfirm----" + start, "===end===============");
    }

    /**
     * 执行关闭便捷上下车自动调节
     */
    public void doingCloseBoardingCar(HashMap<String, Object> map) {
        LogUtils.d("-doingCloseBoardingCar", "===start===============");
//        carPropHelper.setIntProp(H56C.IVI_chassisSet_IVI_AUTOEASYENTRYSET, 1);//2是打开  1是关闭
        operator.setIntProp(SuspensionSignal.SUSPENSION_DOINGCLOSEBOARDINGCAR, 1);//2是打开  1是关闭
        LogUtils.d("-doingCloseBoardingCar----", "===end===============");
    }


    /**
     * 执行打开高速悬架自适应调节
     */
    public void doingOpenHighSpeedCar(HashMap<String, Object> map) {
        LogUtils.d("-doingOpenHighSpeedCar", "===start===============");
//        carPropHelper.setIntProp(Driving.ID_DRV_HIGHWAY_MODE, 2);//2是打开 1是关闭
        operator.setIntProp(SuspensionSignal.SUSPENSION_HIGHSPEED, 2);//2是打开 1是关闭
        LogUtils.d("-doingOpenHighSpeedCar----", "===end===============");
    }

    /**
     * 执行关闭高速悬架自适应调节
     */
    public void doingCloseHighSpeedCar(HashMap<String, Object> map) {
        LogUtils.d("-doingCloseHighSpeedCar", "===start===============");
//        carPropHelper.setIntProp(Driving.ID_DRV_HIGHWAY_MODE, 1);//2是打开 1是关闭
        operator.setIntProp(SuspensionSignal.SUSPENSION_HIGHSPEED, 1);//2是打开 1是关闭
        LogUtils.d("-doingCloseHighSpeedCar----", "===end===============");
    }

    /**
     * 执行打开悬架高度随车速自动调节
     */
    public void doingOpenHeightSpeedCar(HashMap<String, Object> map) {
        LogUtils.d("-doingOpenHeightSpeedCar", "===start===============");
//        carPropHelper.setIntProp(H56C.IVI_chassisSet2_IVI_ASC_SPEDAJUSTSET, 2);//2是打开 1是关闭
        operator.setIntProp(SuspensionSignal.SUSPENSION_DOINGHEIGHTSPEDCAR, 2);//2是打开 1是关闭
        LogUtils.d("-doingOpenHeightSpeedCar----", "===end===============");
    }

    /**
     * 执行关闭悬架高度随车速自动调节
     */
    public void doingCloseHeightSpeedCar(HashMap<String, Object> map) {
        LogUtils.d("-doingCloseHeightSpeedCar", "===start===============");
//        carPropHelper.setIntProp(H56C.IVI_chassisSet2_IVI_ASC_SPEDAJUSTSET, 1);//2是打开 1是关闭
        operator.setIntProp(SuspensionSignal.SUSPENSION_DOINGHEIGHTSPEDCAR, 1);//2是打开 1是关闭
        LogUtils.d("-doingCloseHeightSpeedCar----", "===end===============");
    }

    /**
     * 执行打开便捷载物
     */
    public void doingOpenLoadAdjust(HashMap<String, Object> map) {
        LogUtils.d("-doingOpenLoadAdjust", "===start===============");
//        carPropHelper.setIntProp(H56C.IVI_chassisSet_IVI_EASYPACKSET, 2);//2是打开 1是关闭
        int start = -1;
//        try {
//            start = iPageShowManager.showSuspensionEasyLoadConfirm();
//        } catch (RemoteException e) {
//            LogUtils.e("doingOpenLoadAdjust e:" + e.getMessage(),"---content-------");
//        }
        mSettingHelper.exec(SettingConstants.SUSPENSION_LOADADJUST);//便捷载物
    }

    /**
     * 执行关闭便捷载物
     */
    public void doingCloseLoadAdjust(HashMap<String, Object> map) {
//        carPropHelper.setIntProp(H56C.IVI_chassisSet_IVI_EASYPACKSET, 1);//2是打开 1是关闭
        operator.setIntProp(SuspensionSignal.SUSPENSION_DOINGLOADADJUST, 1);//2是打开 1是关闭
    }

    /**
     * 执行打开悬架维修模式
     */
    public void doingOpenMaintenanceAdjust(HashMap<String, Object> map) {
//        carPropHelper.setIntProp(Driving.ID_DRV_SUSPENSION_MAINTAIN, 2);//2是开  1是关
    }

    /**
     * 执行关闭悬架维修模式
     */
    public void doingCloseMaintenanceAdjust(HashMap<String, Object> map) {
//        carPropHelper.setIntProp(Driving.ID_DRV_SUSPENSION_MAINTAIN, 1);//2是开 1是关
    }
}
