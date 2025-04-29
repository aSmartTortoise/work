package com.voyah.ai.virtual.data;


/**
 * @Date 2024/7/1 15:47
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class SeatInfo {

    public int seatAdjustPosition = 0;
    public int seatAdjustBack = 0;
    public int seatAdjustCushionAngle = 0;
    public int seatAdjustCushionHeight = 0;

    @Override
    public String toString() {
        return "SeatInfo{" +
                "seatAdjustPosition=" + seatAdjustPosition +
                ", seatAdjustBack=" + seatAdjustBack +
                ", seatAdjustCushionAngle=" + seatAdjustCushionAngle +
                ", seatAdjustCushionHeight=" + seatAdjustCushionHeight +
                '}';
    }
}
