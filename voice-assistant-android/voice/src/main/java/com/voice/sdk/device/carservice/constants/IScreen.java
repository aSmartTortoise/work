package com.voice.sdk.device.carservice.constants;

/**
 * @Date 2024/7/26 15:43
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public interface IScreen {

    /**
     * 参照37定义
     * BT = between
     * DRIVER===BT_DRV_MIDDLE===MIDDLE===BT_PASS_MIDDLE===PASSENGER
     */

    public interface IScreenPos {

        int DRIVER = 1; //驾驶位
        int MIDDLE = 2; //中间
        int PASSENGER = 3; //乘客位
        int BT_DRV_MIDDLE = 4; //
        int BT_PASS_MIDDLE = 5; //
    }
}
