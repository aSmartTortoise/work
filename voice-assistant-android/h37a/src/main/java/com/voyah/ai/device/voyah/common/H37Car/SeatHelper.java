package com.voyah.ai.device.voyah.common.H37Car;

import android.provider.Settings;
import android.util.Pair;

import com.blankj.utilcode.util.MapUtils;
import com.blankj.utilcode.util.Utils;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.VCarSeatSignal;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.cockpit.babyseat.aidlimpl.ChildSeatManagerServiceImpl;
import com.voyah.cockpit.child.seat.ChildSeatBean;
import com.voyah.cockpit.seat.SeatServiceImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

import mega.car.Signal;
import mega.car.VehicleArea;
import mega.car.config.Cabin;
import mega.car.config.Comforts;
import mega.car.config.ParamsCommon;
import mega.car.hardware.CarPropertyValue;
import mega.config.MegaDataStorageConfig;

/**
 * @Date 2024/6/28 14:29
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class SeatHelper {

    public static void saveSeatPosition(int area, int value) {
        CarPropUtils.getInstance().setIntProp(Comforts.ID_SEAT_MEMORY, area, value);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37B")) {
            if (area == VehicleArea.FRONT_LEFT && value == 1) {
                sendDriverSeatPosition();
                sendDriverSeatRearMirrorPosition();
            }
        }
    }

    private static void sendDriverSeatPosition() {
        String driverSeatPosition = CarPropUtils.getInstance().getPropertyRaw(Comforts.ID_SEAT_MEMORY_POS, VehicleArea.FRONT_LEFT).getValue().toString();
        MegaDataStorageConfig.putString("left_seat_message", driverSeatPosition);
        MegaDataStorageConfig.getContentResolver().notifyChange(Settings.System.getUriFor("left_seat_message"), null);
    }

    private static void sendDriverSeatRearMirrorPosition() {
        int carValueLeftH = CarPropUtils.getInstance().getIntProp(Cabin.ID_REAR_VIEW_MIRROR_H_POS, VehicleArea.OUTSIDE_LEFT);
        int carValueRightH = CarPropUtils.getInstance().getIntProp(Cabin.ID_REAR_VIEW_MIRROR_H_POS, VehicleArea.OUTSIDE_RIGHT);
        int carValueLeftV = CarPropUtils.getInstance().getIntProp(Cabin.ID_REAR_VIEW_MIRROR_V_POS, VehicleArea.OUTSIDE_LEFT);
        int carValueRightV = CarPropUtils.getInstance().getIntProp(Cabin.ID_REAR_VIEW_MIRROR_V_POS, VehicleArea.OUTSIDE_RIGHT);
        JSONArray array = new JSONArray();
        JSONObject driveLocation = new JSONObject(); //驾驶位置
        // 休息位置字段无效，占位值
        JSONObject restLocation = new JSONObject();
        // 其他位置无效，占位值
        JSONObject otherLocation = new JSONObject();
        JSONObject obj = new JSONObject();
        try {
            // 驾驶位置的左右后视镜X,Y的有效值
            driveLocation.put("LeftMirrorHorizontalPos", carValueLeftH);
            driveLocation.put("LeftMirrorVerticalPos", carValueLeftV);
            driveLocation.put("RightMirrorHorizontalPos", carValueRightH);
            driveLocation.put("RightMirrorVerticalPos", carValueRightV);

            restLocation.put("LeftMirrorHorizontalPos", 255);
            restLocation.put("LeftMirrorVerticalPos", 255);
            restLocation.put("RightMirrorHorizontalPos", 255);
            restLocation.put("RightMirrorVerticalPos", 255);

            otherLocation.put("LeftMirrorHorizontalPos", 255);
            otherLocation.put("LeftMirrorVerticalPos", 255);
            otherLocation.put("RightMirrorHorizontalPos", 255);
            otherLocation.put("RightMirrorVerticalPos", 255);
            array.put(driveLocation);
            array.put(restLocation);
            array.put(otherLocation);
            //  LocationValue字段，这三组数据，通过 Cabin.ID_MIRROR_SYNC_CLOUDDATA发给BCM，
            //  底层会将现存的三个位置的左右后视镜XY值，替换成此JSON里的值，只有“驾驶位置”是有效的
            // 另外两组“休息”，“其他”是无用的，但是不能发有效字段（0——100），可以发FF占位
            obj.put("LocationValue", array);
            // LocationId字段，个人中心会解出来，通过 Cabin.ID_MIRROR_REQ_ANGLE_READ_REQ 发送到BCM，调用位置
            obj.put("LocationId", 1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        MegaDataStorageConfig.putString("seat_rearview_mirrors_message", obj.toString());
        MegaDataStorageConfig.getContentResolver().notifyChange(Settings.System.getUriFor("seat_rearview_mirrors_message"), null);
    }

    public static void setSeatPosition(int area, int value) {
        CarPropUtils.getInstance().setIntProp(Comforts.ID_SEAT_MEMORY_READ, area, value);
    }

    public static void setSeatMirrorSave(int area, int value) {
        CarPropUtils.getInstance().setIntProp(Cabin.ID_MIRROR_REQ_ANGLE_SAVE_REQ, area, value);
    }

    public static void setSeatMirrorRecovery(int area, int value) {
        CarPropUtils.getInstance().setIntProp(Cabin.ID_MIRROR_REQ_ANGLE_READ_REQ, area, value);
    }

    public static void setSeatMessageLevel(int area, int value) {
        CarPropUtils.getInstance().setIntProp(Comforts.ID_SEAT_MASSAGE, area, value);
    }

    public static int getMassageMode(String mode) {
        if (mode.contains("wave")) {
            return Signal.ParamsSeatMassageMode.SEATMASSAGEPROG1;
        } else if (mode.contains("rolling")) {
            return Signal.ParamsSeatMassageMode.SEATMASSAGEPROG2;
        } else if (mode.contains("snake")) {
            return Signal.ParamsSeatMassageMode.SEATMASSAGEPROG4;
        } else if (mode.contains("backside")) {
            return Signal.ParamsSeatMassageMode.SEATMASSAGEPROG5;
        } else if (mode.contains("waist")) {
            return Signal.ParamsSeatMassageMode.SEATMASSAGEPROG3;
        }
        return Signal.ParamsSeatMassageMode.SEATMASSAGEPROG1;
    }

    public static String getMassageModeString(int mode) {
        switch (mode) {
            case Signal.ParamsSeatMassageMode.SEATMASSAGEPROG1:
                return "wave";
            case Signal.ParamsSeatMassageMode.SEATMASSAGEPROG2:
                return "rolling";
            case Signal.ParamsSeatMassageMode.SEATMASSAGEPROG3:
                return "waist";
            case Signal.ParamsSeatMassageMode.SEATMASSAGEPROG4:
                return "snake";
            case Signal.ParamsSeatMassageMode.SEATMASSAGEPROG5:
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
                    new CarPropertyValue<>(Signal.ID_SEAT_TARGET_POSITION_SET,
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
        CarPropertyValue seatOccupyStatus = CarPropUtils.getInstance().getPropertyRaw(Signal.ID_SEAT_OCCUPY_STATUS);
        if (seatOccupyStatus != null && seatOccupyStatus.getValue() instanceof Integer[]) {

            Integer[] occupyStatusValue = (Integer[]) seatOccupyStatus.getValue();
            if (area == VehicleArea.FRONT_RIGHT) {
                return occupyStatusValue[0] != Signal.ParamsSeatSensorStatus.SEATSTS_UNOCCUPIED;
            } else if (area == VehicleArea.REAR_LEFT) {
                return occupyStatusValue[1] != Signal.ParamsSeatSensorStatus.SEATSTS_UNOCCUPIED;
            } else if (area == VehicleArea.REAR_RIGHT) {
                return occupyStatusValue[3] != Signal.ParamsSeatSensorStatus.SEATSTS_UNOCCUPIED;
            }
        }
        return false;
    }

    public static void stopMoving(int area_37){
        JSONObject object = new JSONObject();
        try {
            Set<Map.Entry<String, String>> Set = getSeatReqMap().entrySet();
            for (Map.Entry<String, String> entry : Set) {
                object.put(entry.getValue(), 254);
            }
            CarPropertyValue<String> valueOpen =
                    new CarPropertyValue<>(Signal.ID_SEAT_TARGET_POSITION_SET,
                            area_37,
                            object.toString());
            CarPropUtils.getInstance().setRawProp(valueOpen);
        } catch (JSONException e) {
            LogUtils.e("Seat", "stop seat moving failed");
        }
    }

    public static String adaptSeatInfoString (String json) {
        return json.replaceAll("SeatHorizontalPos", "seatAdjustPosition")
                .replaceAll("SeatBacPos", "seatAdjustBack")
                .replaceAll("SeatHeightPos", "seatAdjustCushionHeight")
                .replaceAll("SeatCushionAnglePos", "seatAdjustCushionAngle");
    }

    /**
     * 获取主驾/二排座椅迎宾状态，兼容37
     *
     * @param type 主驾0/二排1
     * @return
     */
    public static boolean isSeatWelcomeOpen(int type) {
        return ParamsCommon.OnOffInvalid.ON
            == CarPropUtils.getInstance().getIntProp(Comforts.ID_SEAT_CTRL_WELCOME_MODE_SETTING);
       }

    public static void setSeatWelcomeOpen(int type, boolean value) {
        int state = value ? 1 : 0;
        CarPropUtils.getInstance().setIntProp(Comforts.ID_SEAT_CTRL_WELCOME_MODE_SETTING, state);
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

    public static int getZeroGravityConfig() {
        return MegaSystemProperties.getInt(MegaProperties.CONFIG_ZEROGRAVITY_SEAT, -1);
    }

    public static int getZeroGravityState() {
        return CarPropUtils.getInstance().getIntProp(Signal.ID_ZERO_SEAT_REQ);
    }

    public static void setZeroGravityState() {
        CarPropUtils.getInstance().setIntProp(Signal.ID_ZERO_SEAT_REQ,Signal.OnOffReq.ON);
    }

    public static boolean getSeatUserState(int area) {
        CarPropertyValue seatOccupyStatus = CarPropUtils.getInstance().getPropertyRaw(Signal.ID_SEAT_OCCUPY_STATUS);
        if (seatOccupyStatus != null && seatOccupyStatus.getValue() instanceof Integer[]) {
            Integer[] occupyStatusValue = (Integer[]) seatOccupyStatus.getValue();
            if (area == PositionSignal.SECOND_ROW_LEFT) {
                return occupyStatusValue[1] != Signal.ParamsSeatSensorStatus.SEATSTS_UNOCCUPIED;
            } else if (area ==  PositionSignal.SECOND_ROW_RIGHT) {
                return occupyStatusValue[3] != Signal.ParamsSeatSensorStatus.SEATSTS_UNOCCUPIED;
            } else {
                return occupyStatusValue[2] != Signal.ParamsSeatSensorStatus.SEATSTS_UNOCCUPIED;
            }
        }
        return false;
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
        LogUtils.d("SeatHelper", "connectDeviceSize: " + connectDeviceSize + "powerConnectStatus: " + powerConnectStatus);
        return connectDeviceSize > 0 && powerConnectStatus;
    }

    public static int getSeatCurFoldPosition(int area) {
        return CarPropUtils.getInstance().getIntProp(Comforts.ID_SEAT_BACK, area);
    }

    public static void setSeatFold(int area, boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_SEAT_FOLD_EXPAND, area, value ? Signal.SeatFoldExpandReq.FOLD : Signal.SeatFoldExpandReq.EXPAND);
    }
}
