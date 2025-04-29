package com.voyah.ai.virtual.dc;

import com.google.gson.Gson;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.virtual.data.SeatInfo;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.VCarSeatSignal;

/**
 * @Date 2024/6/25 15:04
 * @Author 8327821
 * @Email *
 * @Description .
 * <p>
 * <p>
 * VCarPropertyOperator
 **/
public class SeatPropertyOperator extends BaseVirtualPropertyOperator {

    private static final String TAG = SeatPropertyOperator.class.getSimpleName();


    @Override
    public void setBaseIntProp(String key, int area, int value) {
        String virtualKey = getVirtualKey(key, area);

        if (VCarSeatSignal.SEAT_MEMORY_SAVE.equalsIgnoreCase(key)) {
            processSeatSave(area, value);
            return;
        } else if (VCarSeatSignal.SEAT_MEMORY_RECOVERY.equalsIgnoreCase(key)) {
            processRecovery(area, value, true);
            return;
        }
        if (VCarSeatSignal.SEAT_ADJUST_POSITION.equalsIgnoreCase(key)
                || VCarSeatSignal.SEAT_ADJUST_BACK.equalsIgnoreCase(key)
                || VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT.equalsIgnoreCase(key)
                || VCarSeatSignal.SEAT_ADJUST_CUSHION_ANGLE.equalsIgnoreCase(key)) {
            if (value == 255) {
                return; //255代表不作调整
            }
        }
        setValue(virtualKey, value);
    }

    @Override
    public void setBaseFloatProp(String key, int area, float value) {
        String virtualKey = getVirtualKey(key, area);
        setValue(virtualKey, value);
    }

    @Override
    public String getBaseStringProp(String key, int area) {

        if (VCarSeatSignal.SEAT_MEMORY_READ.equalsIgnoreCase(key)) {
            return processRead(area);
        }
        String virtualKey = getVirtualKey(key, area);
        return (String) iVirtualDevice.getData(virtualKey);
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        if (VCarSeatSignal.CAR_STATE_DRIVING.equalsIgnoreCase(key)) {
            boolean isDriving = true;
            //获取当前挡位
            String gearInfo = (String) getValue(CommonSignal.COMMON_GEAR_INFO);
            if ("P".equalsIgnoreCase(gearInfo) || "N".equalsIgnoreCase(gearInfo)) {
                isDriving = false;
            } else {
                //获取当前车速
                float speed = (Float) getValue(CommonSignal.COMMON_SPEED_INFO);
                if (speed <= 3F) {
                    isDriving = false;
                }
            }
            return isDriving;
        } else {
            return super.getBaseBooleanProp(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        if (VCarSeatSignal.SEAT_ONEKEY_RESET.equalsIgnoreCase(key) && value) {
            processReset(area);
        } else {
            super.setBaseBooleanProp(key, area, value);
        }
    }


    /**
     * @param area  主驾？副驾？
     * @param value 要保存到哪个槽位
     */
    private void processSeatSave(int area, int value) {

        String key = getVirtualKey(VCarSeatSignal.SEAT_MEMORY_SAVE, area);
        //read
        String jsonStr = (String) iVirtualDevice.getData(key);

        //convert
        Gson gson = new Gson();
        SeatInfo[] array = gson.fromJson(jsonStr, SeatInfo[].class);

        //modify
        if (value >= 0 && value < array.length) {
            SeatInfo info = new SeatInfo();
            info.seatAdjustPosition = (Integer) iVirtualDevice.getData(getVirtualKey(VCarSeatSignal.SEAT_ADJUST_POSITION, area));
            info.seatAdjustBack = (Integer) iVirtualDevice.getData(getVirtualKey(VCarSeatSignal.SEAT_ADJUST_BACK, area));
            info.seatAdjustCushionHeight = (Integer) iVirtualDevice.getData(getVirtualKey(VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT, area));
            info.seatAdjustCushionAngle = (Integer) iVirtualDevice.getData(getVirtualKey(VCarSeatSignal.SEAT_ADJUST_CUSHION_ANGLE, area));
            array[value] = info;

            //write back
            setValue(key, gson.toJson(array));
        } else {
            throw new IndexOutOfBoundsException("length is" + array.length + ",but try access index" + value);
        }
    }


    private String processRead(int area) {
        String key = getVirtualKey(VCarSeatSignal.SEAT_MEMORY_SAVE, area);
        return (String) iVirtualDevice.getData(key);
    }

    /**
     * @param area       主驾副驾？
     * @param value      第几个槽位？
     * @param isRecovery 只读还是需要恢复
     */
    private void processRecovery(int area, int value, boolean isRecovery) {
        String key = getVirtualKey(VCarSeatSignal.SEAT_MEMORY_SAVE, area);
        String jsonStr = (String) iVirtualDevice.getData(key);
        if (jsonStr != null && !jsonStr.isEmpty()) {
            Gson gson = new Gson();
            SeatInfo[] array = gson.fromJson(jsonStr, SeatInfo[].class);

            SeatInfo target = array[value]; //需要恢复的数据
            LogUtils.d(TAG, "recovery with:" + target.toString());
            setValue(getVirtualKey(VCarSeatSignal.SEAT_ADJUST_POSITION, area), target.seatAdjustPosition);
            setValue(getVirtualKey(VCarSeatSignal.SEAT_ADJUST_BACK, area), target.seatAdjustBack);
            setValue(getVirtualKey(VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT, area), target.seatAdjustCushionHeight);
            setValue(getVirtualKey(VCarSeatSignal.SEAT_ADJUST_CUSHION_ANGLE, area), target.seatAdjustCushionAngle);
        }
    }

    private void processReset(int area) {
        setValue(getVirtualKey(VCarSeatSignal.SEAT_ADJUST_POSITION, area), 50);
        setValue(getVirtualKey(VCarSeatSignal.SEAT_ADJUST_BACK, area), 50);
        setValue(getVirtualKey(VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT, area), 50);
        setValue(getVirtualKey(VCarSeatSignal.SEAT_ADJUST_CUSHION_ANGLE, area), 50);
    }

}
