package com.voyah.ai.device.voyah.common.H56Car;

import android.util.Pair;

import com.blankj.utilcode.util.MapUtils;
import com.blankj.utilcode.util.Utils;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.VCarSeatSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.babyseat.aidlimpl.ChildSeatManagerServiceImpl;
import com.voyah.cockpit.child.seat.ChildSeatBean;
import com.voyah.cockpit.seat.SeatServiceImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;


import mega.car.VehicleArea;
import mega.car.config.Climate;
import mega.car.config.Comforts;
import mega.car.config.H56C;
import mega.car.config.ParamsCommon;
import mega.car.hardware.CarPropertyValue;

/**
 * @Date 2024/6/28 14:29
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class SeatHelper {
    private static final String TAG = SeatHelper.class.getSimpleName();

    public static void saveSeatPosition(int area, int value) {
        int key;
        switch (area) {
            case VehicleArea.FRONT_LEFT:
                key = Comforts.ID_SEAT_MEMORY;
                break;
            case VehicleArea.FRONT_RIGHT:
                key = H56C.IVI_SCUCtrl2_IVI_PSMSETMEMORYREQ;
                break;
            case VehicleArea.REAR_LEFT:
                key = H56C.IVI_BD_CFSet_IVI_2LSEATSETMEMORYREQ;
                break;
            case VehicleArea.REAR_RIGHT:
                key = H56C.IVI_BD_CFSet_IVI_2RSEATSETMEMORYREQ;
                break;
            default:
                key = -1;
                break;
        }
        CarPropUtils.getInstance().setIntProp(key, value);
    }

    public static void setSeatPosition(int area, int value) {
        int key;
        switch (area) {
            case VehicleArea.FRONT_LEFT:
                key = Comforts.ID_SEAT_MEMORY_READ;
                break;
            case VehicleArea.FRONT_RIGHT:
                key = H56C.IVI_SCUCtrl2_IVI_PSMGETMEMORYREQ;
                break;
            case VehicleArea.REAR_LEFT:
                key = H56C.IVI_BD_CFSet_IVI_2LSEATGETMEMORYREQ;
                break;
            case VehicleArea.REAR_RIGHT:
                key = H56C.IVI_BD_CFSet_IVI_2RSEATGETMEMORYREQ;
                break;
            default:
                key = -1;
                break;
        }
        CarPropUtils.getInstance().setIntProp(key, value);
    }

    public static void setSeatMirrorSave(int area, int value) {
        CarPropUtils.getInstance().setIntProp(H56C.IVI_SCUCtrl_IVI_DSMSETMEMORYREQ, value);
    }

    public static void setSeatMirrorRecovery(int area, int value) {
        CarPropUtils.getInstance().setIntProp(H56C.IVI_SCUCtrl_IVI_DSMGETMEMORYREQ, value);
    }

    public static void setMassageSwitch(int area, int value) {
        int key;
        switch (area) {
            case PositionSignal.FIRST_ROW_LEFT:
                key = H56C.IVI_SCUCtrl2_IVI_FLSEATMASSONOFFSET;
                break;
            case PositionSignal.FIRST_ROW_RIGHT:
                key = H56C.IVI_SCUCtrl2_IVI_FRSEATMASSONOFFSET;
                break;
            case PositionSignal.SECOND_ROW_LEFT:
                key = H56C.IVI_SCUCtrl_IVI_RLSEATMASSONOFFSET;
                break;
            case PositionSignal.SECOND_ROW_RIGHT:
                key = H56C.IVI_SCUCtrl_IVI_RRSEATMASSONOFFSET;
                break;
            default:
                key = -1;
                break;
        }
        CarPropUtils.getInstance().setIntProp(key, value);
    }

    public static int getMassageMode(String mode) {
        if (mode.contains("wave")) {
            return ParamsCommon.ParamsSeatMassageMode.SEATMASSAGEPROG1;
        } else if (mode.contains("rolling")) {
            return ParamsCommon.ParamsSeatMassageMode.SEATMASSAGEPROG2;
        } else if (mode.contains("snake")) {
            return ParamsCommon.ParamsSeatMassageMode.SEATMASSAGEPROG4;
        } else if (mode.contains("backside")) {
            return ParamsCommon.ParamsSeatMassageMode.SEATMASSAGEPROG5;
        } else if (mode.contains("waist")) {
            return ParamsCommon.ParamsSeatMassageMode.SEATMASSAGEPROG3;
        }
        return 1;
    }

    public static String getMassageModeString(int mode) {
        switch (mode) {
            case ParamsCommon.ParamsSeatMassageMode.SEATMASSAGEPROG1:
                return "wave";
            case ParamsCommon.ParamsSeatMassageMode.SEATMASSAGEPROG2:
                return "rolling";
            case ParamsCommon.ParamsSeatMassageMode.SEATMASSAGEPROG3:
                return "waist";
            case ParamsCommon.ParamsSeatMassageMode.SEATMASSAGEPROG4:
                return "snake";
            case ParamsCommon.ParamsSeatMassageMode.SEATMASSAGEPROG5:
                return "backside";
            default:
                return "wave";
        }
    }

    public static void adjustSeat(String signal, int area, int value) {
        JSONObject object = new JSONObject();
        Set<String> stringSet = getSeatReqMap().keySet();
        try {
            for (String str : stringSet) {
                if (str.equalsIgnoreCase(signal)) {
                    object.put(getSeatReqMap().get(str), value);
                } else {
                    object.put(getSeatReqMap().get(str), 255);
                }
            }
            CarPropertyValue<String> valueOpen =
                    new CarPropertyValue<>(Comforts.ID_SEAT_MEMORY,
                            area,
                            object.toString());
            CarPropUtils.getInstance().setRawProp(valueOpen);
        } catch (Exception e) {
            //
        }

    }

    private static Map<String, String> getSeatReqMap() {
        return MapUtils.newLinkedHashMap(
                new Pair<>(VCarSeatSignal.SEAT_ADJUST_POSITION, "SeatHorizontalPos"),
                new Pair<>(VCarSeatSignal.SEAT_ADJUST_BACK, "SeatBacPos"),
                new Pair<>(VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT, "SeatHeightPosPos"),
                new Pair<>(VCarSeatSignal.SEAT_ADJUST_CUSHION_ANGLE, "SeatCushionAnglePosPos")
        );
    }

    public static boolean isOccupied(int area) {
        int rightSTS1 = CarPropUtils.getInstance().getIntProp(H56C.ACU_state_ACU_PASSENGERSTS);
        int leftSTS2 = CarPropUtils.getInstance().getIntProp(H56C.ACU_state_ACU_SECONDLEFTSTS);
        int rightSTS2 = CarPropUtils.getInstance().getIntProp(H56C.ACU_state_ACU_SECONDRIGHTSTS);
        int leftSTS3 = CarPropUtils.getInstance().getIntProp(H56C.BCM_state3_BCM_3NDLEFTSTS);
        int rightSTS3 = CarPropUtils.getInstance().getIntProp(H56C.BCM_state3_BCM_3NDRIGHTSTS);
        if (area == VehicleArea.FRONT_RIGHT) {
            return rightSTS1 == 1;
        } else if (area == VehicleArea.ROW_2_LEFT) {
            return leftSTS2 == 1;
        } else if (area == VehicleArea.ROW_2_RIGHT) {
            return rightSTS2 == 1;
        } else if (area == VehicleArea.ROW_3_LEFT) {
            return leftSTS3 == 1;
        } else if (area == VehicleArea.ROW_3_RIGHT) {
            return rightSTS3 == 1;
        } else if (area == VehicleArea.ALL) {
            return rightSTS1 == 1 && leftSTS2 == 1 && rightSTS2 == 1 && leftSTS3 == 1 && rightSTS3 == 1;
        }
        return true;
    }

    /**
     * 获取主驾/二排座椅迎宾状态，兼容37
     *
     * @param type 主驾0/二排1
     * @return
     */
    public static boolean isSeatWelcomeOpen(int type) {
        if (type == 0) {
            return ParamsCommon.OnOffInvalid.ON
                    == CarPropUtils.getInstance().getIntProp(Comforts.ID_SEAT_CTRL_WELCOME_MODE_SETTING, VehicleArea.FRONT_LEFT);
        }
        return ParamsCommon.OnOffInvalid.ON == CarPropUtils.getInstance().getIntProp(Comforts.ID_SEAT_CTRL_WELCOME_MODE_SETTING, VehicleArea.REAR_LEFT)
                && ParamsCommon.OnOffInvalid.ON == CarPropUtils.getInstance().getIntProp(Comforts.ID_SEAT_CTRL_WELCOME_MODE_SETTING, VehicleArea.REAR_RIGHT);

    }

    /**
     * 儿童座椅是否可用状态
     *
     * @return
     */
    public static boolean isBabySeatAvailable() {
        int connectDeviceSize = ChildSeatManagerServiceImpl.getInstance(Utils.getApp()).getConnectDeviceSize();
        List<ChildSeatBean> childSeatBeanList = ChildSeatManagerServiceImpl.getInstance(Utils.getApp()).getConnectDevice();
        if (childSeatBeanList == null || childSeatBeanList.size() < 1) {
            return false;
        }
        boolean powerConnectStatus = !childSeatBeanList.get(0).getPowerStatus();
        LogUtils.d(TAG, "connectDeviceSize: " + connectDeviceSize + "powerConnectStatus: " + powerConnectStatus);
        return connectDeviceSize > 0 && powerConnectStatus;
    }

    public static void setSeatWelcomeOpen(int type, boolean value) {
        int state = value ? 1 : 2;
        if (type == 0) {
            CarPropUtils.getInstance().setIntProp(Comforts.ID_SEAT_CTRL_WELCOME_MODE_SETTING, VehicleArea.FRONT_LEFT, state);
        } else {
            CarPropUtils.getInstance().setIntProp(Comforts.ID_SEAT_CTRL_WELCOME_MODE_SETTING, VehicleArea.REAR_LEFT, state);
            CarPropUtils.getInstance().setIntProp(Comforts.ID_SEAT_CTRL_WELCOME_MODE_SETTING, VehicleArea.REAR_RIGHT, state);
        }
    }

    //座椅调节的停止移动
    public static void seatAdjustStopMove(int area_37) {
        JSONObject object = new JSONObject();
        try {
            Set<Map.Entry<String, String>> Set = getSeatReqMap().entrySet();
            for (Map.Entry<String, String> entry : Set) {
                object.put(entry.getValue(), 254);
            }
            CarPropertyValue<String> valueOpen =
                    new CarPropertyValue<>(Comforts.ID_SEAT_MEMORY,
                            area_37,
                            object.toString());
            CarPropUtils.getInstance().setRawProp(valueOpen);
        } catch (JSONException e) {
            LogUtils.e("Seat", "stop seat moving failed");
        }
    }

    //座椅一键舒躺/一键零重力的停止移动
    public static void seatComfortablyStopMove(int area) {
        int key;
        switch (area) {
            case PositionSignal.SECOND_ROW_LEFT:
                key = H56C.IVI_SCUCtrl_IVI_2LSEATPAUSE;
                break;
            case PositionSignal.SECOND_ROW_RIGHT:
                key = H56C.IVI_SCUCtrl_IVI_2RSEATPAUSE;
                break;
            default:
                key = -1;
                break;
        }
        CarPropUtils.getInstance().setIntProp(key, ParamsCommon.OnOff.ON);
    }

    //座椅位置激活的停止移动
    public static void seatPositionStopMove(int area) {
        int key;
        switch (area) {
            case PositionSignal.FIRST_ROW_LEFT:
                key = Comforts.ID_SEAT_MEMORY_READ;
                break;
            case PositionSignal.FIRST_ROW_RIGHT:
                key = H56C.IVI_SCUCtrl2_IVI_PSMGETMEMORYREQ;
                break;
            case PositionSignal.SECOND_ROW_LEFT:
                key = H56C.IVI_BD_CFSet_IVI_2LSEATGETMEMORYREQ;
                break;
            case PositionSignal.SECOND_ROW_RIGHT:
                key = H56C.IVI_BD_CFSet_IVI_2RSEATGETMEMORYREQ;
                break;
            default:
                key = -1;
                break;
        }
        CarPropUtils.getInstance().setIntProp(key, 7);
    }

    public static String adaptSeatInfoString(String json) {
        return json.replaceAll("SeatHorizontalPos", "seatAdjustPosition")
                .replaceAll("SeatBacPos", "seatAdjustBack")
                .replaceAll("SeatHeightPos", "seatAdjustCushionHeight")
                .replaceAll("SeatCushionAnglePos", "seatAdjustCushionAngle");
    }

    public static void setSeatState(int type, int area, int value) {
        int writePropId = -1;
        if (Comforts.ID_SEAT_MASSAGE == type) {
            switch (area) {
                case VehicleArea.FRONT_LEFT:
                    writePropId = H56C.IVI_SCUCtrl2_IVI_FLSEATMASSONOFFSET;
                    break;
                case VehicleArea.FRONT_RIGHT:
                    writePropId = H56C.IVI_SCUCtrl2_IVI_FRSEATMASSONOFFSET;
                    break;
                case VehicleArea.REAR_LEFT:
                    writePropId = H56C.IVI_SCUCtrl_IVI_RLSEATMASSONOFFSET;
                    break;
                case VehicleArea.REAR_RIGHT:
                    writePropId = H56C.IVI_SCUCtrl_IVI_RRSEATMASSONOFFSET;
                    break;
            }
        } else if (Climate.ID_SEAT_VENT == type) {
            switch (area) {
                case VehicleArea.FRONT_LEFT:
                    writePropId = H56C.IVI_SCUCtrl_IVI_DRSEATVENTONOFFSET;
                    break;
                case VehicleArea.FRONT_RIGHT:
                    writePropId = H56C.IVI_SCUCtrl_IVI_PASSSEATVENTONOFFSET;
                    break;
                case VehicleArea.REAR_LEFT:
                    writePropId = H56C.IVI_SCUCtrl_IVI_RLSEATVENTONOFFSET;
                    break;
                case VehicleArea.REAR_RIGHT:
                    writePropId = H56C.IVI_SCUCtrl_IVI_RRSEATVENTONOFFSET;
                    break;
            }
        } else if (Climate.ID_SEAT_HEAT == type) {
            switch (area) {
                case VehicleArea.FRONT_LEFT:
                    writePropId = H56C.IVI_SCUCtrl_IVI_DRSEATHEATONOFFSET;
                    break;
                case VehicleArea.FRONT_RIGHT:
                    writePropId = H56C.IVI_SCUCtrl_IVI_PASSSEATHEATONOFFSET;
                    break;
                case VehicleArea.REAR_LEFT:
                    writePropId = H56C.IVI_SCUCtrl_IVI_RLSEATHEATONOFFSET;
                    break;
                case VehicleArea.REAR_RIGHT:
                    writePropId = H56C.IVI_SCUCtrl_IVI_RRSEATHEATONOFFSET;
                    break;
                case VehicleArea.ROW_3_LEFT:
                    writePropId = H56C.IVI_SCUCtrl2_IVI_3LSEATHEATONOFFSET;
                    break;
                case VehicleArea.ROW_3_RIGHT:
                    writePropId = H56C.IVI_SCUCtrl2_IVI_3RSEATHEATONOFFSET;
                    break;
            }
        }
        LogUtils.d("SeatHelper", "getWritePropId is writePropId:" + writePropId + "    propId:" + type);
        // 此处只是56C的信号会用到，需要先打开状态，也就是设置2
        CarServicePropUtils.getInstance().setIntProp(writePropId, value > 0 ? 2 : 1);
    }

    public static int getZeroGravityConfig() {
        return MegaSystemProperties.getInt(MegaProperties.CONFIG_ZEROGRAVITY_SEAT, -1);
    }

    public static int getZeroGravityState(int area) {
        if (area == PositionSignal.SECOND_ROW_LEFT) {
            return CarPropUtils.getInstance().getIntProp(H56C.SCU_RL_SCU_RL_2LSEATZEROGSTA);
        } else {
            return CarPropUtils.getInstance().getIntProp(H56C.SCU_RR_SCU_RR_2RSEATZEROGSTA);
        }
    }

    public static void setZeroGravityState(int area) {
        if (area == PositionSignal.SECOND_ROW_LEFT) {
            CarPropUtils.getInstance().setIntProp(H56C.SCU_RL_SCU_RL_2LSEATZEROGSTA,1);
        } else {
            CarPropUtils.getInstance().setIntProp(H56C.SCU_RR_SCU_RR_2RSEATZEROGSTA,1);
        }
    }

    public static void setSeatFold(int area, boolean value) {
        if (area == 4) {
            if (value) {
                //三排左折叠
                CarPropUtils.getInstance().setIntProp(H56C.IVI_SCUCtrl2_IVI_3LSEATFOLDSET, 1);
            } else {
                //三排左展开
                CarPropUtils.getInstance().setIntProp(H56C.IVI_SCUCtrl2_IVI_3LSEATUNFOLDSET, 1);
            }
        } else {
            if (value) {
                //三排右折叠
                CarPropUtils.getInstance().setIntProp(H56C.IVI_SCUCtrl2_IVI_3RSEATFOLDSET, 1);
            } else {
                //三排右展开
                CarPropUtils.getInstance().setIntProp(H56C.IVI_SCUCtrl2_IVI_3RSEATUNFOLDSET, 1);
            }
        }
    }

    public static void setSeatReset(int area) {
        CarPropUtils.getInstance().setIntProp(Comforts.ID_SEAT_ONEKEY_RESET, area, ParamsCommon.OnOff.ON);
    }

    public static void showSeatSaveDialog(int area) {
        if (area == 0) {
            // 打开主驾座椅记忆位置保存弹窗
            SeatServiceImpl.getInstance(Utils.getApp()).exec("ACTION_OPEN_DRIVER_SAVE_DIALOG");
        } else if (area == 1) {
            // 打开副驾座椅记忆位置保存弹窗
            SeatServiceImpl.getInstance(Utils.getApp()).exec("ACTION_OPEN_PASS_SAVE_DIALOG");
        }
    }

    public static boolean getSeatUserState(int area) {
        if (area == PositionSignal.THIRD_ROW_LEFT) {
            return CarPropUtils.getInstance().getIntProp(H56C.BCM_state3_BCM_3NDLEFTSTS) == 1;
        } else if (area == PositionSignal.THIRD_ROW_RIGHT) {
            return CarPropUtils.getInstance().getIntProp(H56C.BCM_state3_BCM_3NDRIGHTSTS) == 1;
        } else {
            return CarPropUtils.getInstance().getIntProp(H56C.BCM_state3_BCM_3NDMIDDLESEATBELTSTS) == 1;
        }
    }

    public static int getSeatCurFoldPosition(int area) {
        if (area == PositionSignal.THIRD_ROW_LEFT) {
            return CarPropUtils.getInstance().getIntProp(H56C.SCU_TR_SCU_TR_3LSEATBACKPOS);
        } else {
            return CarPropUtils.getInstance().getIntProp(H56C.SCU_TR_SCU_TR_3RSEATBACKPOS);
        }
    }
}
