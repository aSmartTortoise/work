package com.voyah.ai.device.voyah.common.H56Car;

import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.voyah.ai.basecar.carservice.CarPropUtils;


/**
 * @Date 2024/7/26 16:02
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class ScreenHelper {

    //读写不同信号，所以对写单独处理了
    public static void setScreenPos(int pos) {
        CarPropUtils.getInstance().setIntProp(1677721813, pos);
    }

    /**
     * 滑移屏移动状态
     * @return
     */
    public static boolean isMoving() {
        int moveState = CarPropUtils.getInstance().getIntProp(1677721818);
        return moveState == 1 || moveState == 2;
    }

    /**
     * 滑移屏移动过程中被障碍物阻塞
     * @return
     */
    public static boolean isAbnormalState() {
        int moveSts = CarPropUtils.getInstance().getIntProp(1677721923);
        return moveSts == 7;
    }

    /**
     * 下电复位 get
     * @return
     */
    public static boolean getPowerOffResetState() {
        return CarPropUtils.getInstance().getIntProp(1677721815)
                == 1;
    }

    /**
     * 下电复位 set
     * 和get同一个信号，但参数不同
     */
    public static void setPowerOffState(boolean onOff) {
        CarPropUtils.getInstance().setIntProp(1677721815,
                onOff ? 2 : 1);
    }

    /**===============================屏幕清洁模式==========================*/
    public static boolean getCleanMode() {
        return Settings.System.getInt(Utils.getApp().getContentResolver(), "cleanModeStatus", 0) == 1;
    }

    /**
     * 不支持set，走兜底打开界面
     */
    public static void setCleanMode() {

    }
}
