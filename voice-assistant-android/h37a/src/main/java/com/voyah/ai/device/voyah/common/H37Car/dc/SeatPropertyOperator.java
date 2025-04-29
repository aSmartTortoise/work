package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.blankj.utilcode.util.Utils;
import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.signal.CarSettingSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.VCarSeatSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.device.voyah.common.H37Car.BabySeatHelper;
import com.voyah.ai.device.voyah.common.H37Car.DrivingHelper;
import com.voyah.ai.device.voyah.common.H37Car.SeatHelper;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.cockpit.babyseat.aidlimpl.ChildSeatManagerServiceImpl;
import com.voyah.cockpit.seat.SeatServiceImpl;

import mega.car.Signal;
import mega.car.config.Cabin;
import mega.car.config.Climate;
import mega.car.config.Comforts;
import mega.car.config.Driving;
import mega.car.config.ParamsCommon;

/**
 * @Date 2024/6/25 13:57
 * @Author 8327821
 * @Email *
 * @Description 37实现
 *
 * @see CarPropUtils
 **/
@CarDevices(carType = CarType.H37_CAR)
public class SeatPropertyOperator extends Base37Operator implements IDeviceRegister {

    private static final String TAG = SeatPropertyOperator.class.getSimpleName();


    @Override
    void init() {
        ChildSeatManagerServiceImpl.getInstance(Utils.getApp()).getConnectDevice();
        map.put(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, Climate.ID_SEAT_HEAT);

        map.put(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, Climate.ID_SEAT_VENT);

        map.put(VCarSeatSignal.MASSAGE_MODE, Comforts.ID_SEAT_MASSAGE_MODE);
        map.put(VCarSeatSignal.MASSAGE_CUR_LEVEL, Comforts.ID_SEAT_MASSAGE);
        //座椅调节
        map.put(VCarSeatSignal.SEAT_ADJUST_POSITION, Comforts.ID_SEAT_POSITION);
        map.put(VCarSeatSignal.SEAT_ADJUST_BACK, Comforts.ID_SEAT_BACK);
        map.put(VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT, Comforts.ID_SEAT_CUSHION);
        map.put(VCarSeatSignal.SEAT_ADJUST_CUSHION_ANGLE, Comforts.ID_SEAT_CUSHION);
        map.put(VCarSeatSignal.SEAT_OCCUPIED_STATUS, Signal.ID_SEAT_OCCUPY_STATUS);

        map.put(VCarSeatSignal.SEAT_WELCOME_MODE_SWITCH, Comforts.ID_SEAT_CTRL_WELCOME_MODE_SETTING);

        map.put(VCarSeatSignal.SEAT_MIRROR_SAVE, Cabin.ID_MIRROR_REQ_ANGLE_SAVE_REQ);
        map.put(VCarSeatSignal.SEAT_MIRROR_RECOVERY, Cabin.ID_MIRROR_REQ_ANGLE_READ_REQ);
        map.put(VCarSeatSignal.SEAT_MEMORY_SAVE, Comforts.ID_SEAT_MEMORY);
        map.put(VCarSeatSignal.SEAT_MEMORY_READ, Comforts.ID_SEAT_MEMORY_POS);
        map.put(VCarSeatSignal.SEAT_MEMORY_RECOVERY, Comforts.ID_SEAT_MEMORY_READ);
        map.put(VCarSeatSignal.SEAT_ONEKEY_RESET, Comforts.ID_SEAT_ONEKEY_RESET);
        map.put(VCarSeatSignal.CAR_STATE_DRIVING, Driving.ID_DRV_INFO_GEAR_POSITION); //虽然写了这个映射，实际CAR_STATE_DRIVING 对应档位+车速两个信号
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        int key_37 = getRealKey(key);
        int area_37 = getRealArea(area);

        if (key_37 != -1) {
            //说明是car-service的信号
            int value = CarPropUtils.getInstance().getIntProp(key_37, area_37);
            switch (key) {
                case VCarSeatSignal.MASSAGE_CUR_LEVEL:
                    return value - 1; //按摩使用234 代替123挡，写的时候加一，读的时候减一

                case VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT: //（角度<<8|高度）；0~100 %
                    return value & 0xff;

                case VCarSeatSignal.SEAT_ADJUST_CUSHION_ANGLE:
                    return (value & 0xff00) >> 8;
                default:
                    return value;
            }
        } else {
            //可能来自其他中间件
            switch (key) {
                case VCarSeatSignal.BABY_SEAT_COUNT:
                    return BabySeatHelper.getDeviceCount();
                case VCarSeatSignal.SEAT_ZERO_GRAVITY:
                    return SeatHelper.getZeroGravityState();
                case VCarSeatSignal.SEAT_ZERO_GRAVITY_CONFIG:
                    return SeatHelper.getZeroGravityConfig();
                case VCarSeatSignal.SEAT_CUR_FOLD_POSITION:
                    return SeatHelper.getSeatCurFoldPosition(area_37);
                case CommonSignal.COMMON_EQUIPMENT_LEVEL:
                    return MegaSystemProperties.getInt(MegaProperties.CONFIG_EQUIPMENT_LEVEL, -1);
                default:
                    return super.getBaseIntProp(key, area);
            }
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        int key_37 = getRealKey(key);
        int area_37 = getRealArea(area);
        switch (key) {
            case VCarSeatSignal.MASSAGE_CUR_LEVEL: //按摩使用234 代替123挡，写的时候加一，读的时候减一
                SeatHelper.setSeatMessageLevel(area_37, value + 1);
                return;
            case VCarSeatSignal.SEAT_MEMORY_SAVE: //保存槽位，业务上处理需要+1
                SeatHelper.saveSeatPosition(area_37, value + 1);
                return;
            case VCarSeatSignal.SEAT_MEMORY_RECOVERY: //保存槽位，业务上处理需要+1
                SeatHelper.setSeatPosition(area_37, value + 1);
                return;
            case VCarSeatSignal.SEAT_MIRROR_SAVE:
                SeatHelper.setSeatMirrorSave(area_37, value + 1);
                return;
            case VCarSeatSignal.SEAT_MIRROR_RECOVERY:
                SeatHelper.setSeatMirrorRecovery(area_37, value + 1);
                return;
            case VCarSeatSignal.SEAT_ADJUST_POSITION:
            case VCarSeatSignal.SEAT_ADJUST_BACK:
            case VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT:
            case VCarSeatSignal.SEAT_ADJUST_CUSHION_ANGLE:
                SeatHelper.adjustSeat(key, area_37, value);
                return;
            case VCarSeatSignal.SEAT_COMFORTABLY_STOP_MOVE:
                SeatHelper.stopMoving(area_37);
                return;
            default:
        }
        CarPropUtils.getInstance().setIntProp(key_37, area_37, value);
    }

    @Override
    public float getBaseFloatProp(String key, int area) {
        return 0;
    }

    @Override
    public void setBaseFloatProp(String key, int area, float value) {

    }

    @Override
    public String getBaseStringProp(String key, int area) {
        int key_37 = getRealKey(key);
        int area_37 = getRealArea(area);
        switch (key) {
            case VCarSeatSignal.MASSAGE_MODE:
                int value = CarPropUtils.getInstance().getIntProp(key_37, area_37);
                return SeatHelper.getMassageModeString(value);

            case VCarSeatSignal.SEAT_MEMORY_READ:
                String originJson = (String) CarPropUtils.getInstance().getPropertyRaw(key_37, area_37).getValue();
                if (originJson != null && !originJson.isEmpty()) {
                    return SeatHelper.adaptSeatInfoString(originJson);
                }
                return "";
        }
        //TODO string 不存在统一包装
        return "";
    }

    @Override
    public void setBaseStringProp(String key, int area, String value) {
        int key_37 = getRealKey(key);
        int area_37 = getRealArea(area);
        switch (key) {
            case VCarSeatSignal.MASSAGE_MODE:
                int mode = SeatHelper.getMassageMode(value);
                CarPropUtils.getInstance().setIntProp(key_37, area_37, mode);
                break;
            case VCarSeatSignal.SEAT_PAGE_SATE:
                SeatServiceImpl.getInstance(Utils.getApp()).exec(value);
                break;
        }
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        int area_37 = getRealArea(area);
        switch (key) {
            case VCarSeatSignal.SEAT_OCCUPIED_STATUS:
                return SeatHelper.isOccupied(area_37);
            case VCarSeatSignal.CAR_STATE_DRIVING:
                return DrivingHelper.isDriving();
            case VCarSeatSignal.BABY_SEAT_HEAT_SWITCH:
                return BabySeatHelper.getBabySeatHeat() == 1;
            case VCarSeatSignal.BABY_SEAT_VENT_SWITCH:
                return BabySeatHelper.getBabySeatFan() == 1;
            case VCarSeatSignal.SEAT_WELCOME_MODE_SWITCH:
                return SeatHelper.isSeatWelcomeOpen(area);
            case VCarSeatSignal.SEAT_PAGE_SATE:
                return SeatServiceImpl.getInstance(Utils.getApp())
                        .isCurrentState("ACTION_GET_SEAT_PANEL_STATE");
            case VCarSeatSignal.SEAT_USER_STATE:
                return SeatHelper.getSeatUserState(area);
            case VCarSeatSignal.BABY_SEAT_STATE:
                return SeatHelper.isBabySeatAvailable();
            case VCarSeatSignal.SEAT_SUPPORT_ADJUST_THIRD_SIDE:
                //37支持的二排，不支持三排
                return false;
        }
        int key_37 = getRealKey(key);
        return ParamsCommon.OnOffInvalid.ON
                == CarPropUtils.getInstance().getIntProp(key_37, area_37);

    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        int area_37 = getRealArea(area);
        switch (key) {
            case VCarSeatSignal.BABY_SEAT_HEAT_SWITCH:
                BabySeatHelper.setBabySeatHeat(value);
                return;
            case VCarSeatSignal.BABY_SEAT_VENT_SWITCH:
                BabySeatHelper.setBabySeatFan(value);
                return;
            case VCarSeatSignal.SEAT_ADJUST_STOP_MOVE:
                SeatHelper.stopMoving(area_37);
                return;
            case VCarSeatSignal.SEAT_WELCOME_MODE_SWITCH:
                //暂时只处理了主驾
                SeatHelper.setSeatWelcomeOpen(area, value);
                return;
            case VCarSeatSignal.SEAT_ONEKEY_RESET:
                SeatHelper.setSeatReset(area_37);
                return;
            case VCarSeatSignal.SEAT_SAVE_PAGE_SATE:
                SeatHelper.showSeatSaveDialog(area);
                return;
            case VCarSeatSignal.SEAT_ZERO_GRAVITY:
                SeatHelper.setZeroGravityState();
                return;
            case VCarSeatSignal.SEAT_FOLD:
                SeatHelper.setSeatFold(area_37,value);
        }

        int key_37 = getRealKey(key);
        //默认使用1代表开，0代表关，如果不满足这个规则，请在switch case补充
        int intOnOff = value ? ParamsCommon.OnOffInvalid.ON : ParamsCommon.OnOffInvalid.OFF;
        CarPropUtils.getInstance().setIntProp(key_37, area_37, intOnOff);
    }

    /**
     * 1代表支持。0代表不支持。-代表没处理。（当前没处理表示支持）
     *
     * @param key
     * @return
     */
    @Override
    public String isSupport(String key) {
        switch (key) {
            case VCarSeatSignal.SEAT_HEAT_CUR_LEVEL:
            case VCarSeatSignal.SEAT_VENT_CUR_LEVEL:
            case VCarSeatSignal.BABY_SEAT_HEAT_SWITCH:
            case VCarSeatSignal.BABY_SEAT_VENT_SWITCH:
            case VCarSeatSignal.BABY_SEAT_COUNT:
            case VCarSeatSignal.SEAT_WELCOME_MODE_SWITCH:
                return "1";
            default:
                return "-";
        }
    }

    @Override
    public void registerDevice() {
        String seatFoldConfig;
        String seatSupportManualadJustBack;
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            seatFoldConfig = "-1";
            seatSupportManualadJustBack = "-1";
        } else {
            seatFoldConfig = "1";
            seatSupportManualadJustBack = "1";
        }
        //1=文案需要按照37A来兜底回复
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(VCarSeatSignal.SEAT_MESSAGE_SUPPORT_TEXT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //是否支持语音调节座椅位置
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(VCarSeatSignal.SEAT_SUPPORT_ADJUST_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,"1");
        //是否只有副驾支持复位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(VCarSeatSignal.SEAT_ONEKEY_RESET_OLAY_FIRST_ROW_RIGHT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,"1");
        // 座椅折叠展开，37A不支持座椅折叠展开 37B支持 56C支持 56D支持
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(VCarSeatSignal.SEAT_FOLD_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,seatFoldConfig);
        // 是否支持二排座椅手动调节，37A是不支持  37B是支持
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(VCarSeatSignal.SEAT_SUPPORT_MANUAL_ADJUST_BACK, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,seatSupportManualadJustBack);
    }
}
