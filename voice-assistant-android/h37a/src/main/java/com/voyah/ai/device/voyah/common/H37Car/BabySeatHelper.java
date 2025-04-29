
package com.voyah.ai.device.voyah.common.H37Car;

import com.blankj.utilcode.util.Utils;
import com.voyah.cockpit.babyseat.aidlimpl.ChildSeatManagerServiceImpl;
import com.voyah.cockpit.child.seat.ChildSeatBean;

import java.util.List;

/**
 * @Date 2024/6/28 14:34
 * @Author 8327821
 * @Email *
 * @Description 儿童座椅中间件
 **/
public class BabySeatHelper {

    public static int getDeviceCount() {
        return ChildSeatManagerServiceImpl.getInstance(Utils.getApp()).getConnectDeviceSize();
    }

    public static int getBabySeatHeat() {
        String mac = getBabySeatMac();
        if (!mac.isEmpty()) {
            return ChildSeatManagerServiceImpl.getInstance(Utils.getApp()).getSeatHeat(mac);
        }
        return -1;
    }

    public static int getBabySeatFan() {
        String mac = getBabySeatMac();
        if (!mac.isEmpty()) {
            return ChildSeatManagerServiceImpl.getInstance(Utils.getApp()).getSeatFan(mac);
        }
        return -1;
    }

    public static void setBabySeatHeat(boolean open) {
        String mac = getBabySeatMac();
        if (!mac.isEmpty()) {
            if (open) {
                ChildSeatManagerServiceImpl.getInstance(Utils.getApp()).openSeatHeat(mac);
            } else {
                ChildSeatManagerServiceImpl.getInstance(Utils.getApp()).closeSeatHeat(mac);
            }
        }
    }

    public static void setBabySeatFan(boolean open) {
        String mac = getBabySeatMac();
        if (!mac.isEmpty()) {
            if (open) {
                ChildSeatManagerServiceImpl.getInstance(Utils.getApp()).openSeatFan(mac);
            } else {
                ChildSeatManagerServiceImpl.getInstance(Utils.getApp()).closeSeatFan(mac);
            }
        }
    }

    private static String getBabySeatMac() {
        List<ChildSeatBean> list = ChildSeatManagerServiceImpl.getInstance(Utils.getApp()).getConnectDevice();
        if (list != null && !list.isEmpty()) {
            ChildSeatBean first = list.get(0);
            return first.getMac();
        }
        return "";
    }
}
