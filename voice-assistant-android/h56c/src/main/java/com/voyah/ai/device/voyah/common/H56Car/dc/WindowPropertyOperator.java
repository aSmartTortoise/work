package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.WindowSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.device.voyah.common.H56Car.WindowHelper;


import mega.car.config.EntryLocks;
import mega.car.config.H56C;

/**
 * @Date 2024/7/22 14:30
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class WindowPropertyOperator extends Base56Operator {
    @Override
    void init() {
        map.put(WindowSignal.WINDOW_CHILD_LOCK, EntryLocks.ID_LOCKS_CHILD_SEC_DOOR_LOCK);//儿童锁
        //H56C.DCU_FL2_DCU_FL_FLWINDOWHORIZONTALSTS
        map.put(WindowSignal.WINDOW_WINDOW,H56C.DCU_FR_DCU_FR_FRVENTILATIONFB);//车窗（多位置），127代表需要手动学习

//        map.put(WindowSignal.WINDOW_CHILD_LOCK, EntryLocks.ID_LOCKS_CHILD_SEC_DOOR_LOCK);
//
//
//        //主驾车窗  get
        map.put(WindowSignal.WINDOW_DRIVER, H56C.DCU_FL2_DCU_FL_FLWINDOWHORIZONTALSTS);
//        //副驾车窗 get
        map.put(WindowSignal.WINDOW_PASSENGER, H56C.DCU_FR_DCU_FR_FRWINDOWHORIZONTALSTS);
//        //左后车窗 get
        map.put(WindowSignal.WINDOW_LEFTREAR, H56C.DCU_RL_DCU_RL_RLWINDOWHORIZONTALSTS);
//        //右后车窗 get
        map.put(WindowSignal.WINDOW_RIGHTREAR, H56C.DCU_RR_DCU_RR_RRWINDOWHORIZONTALSTS);

        map.put(WindowSignal.WINDOW_CHILDLOCKLEFT,H56C.DCU_RL_DCU_RL_RLWINDOWLOCKSTS);//左侧后排车窗禁用信号

        map.put(WindowSignal.WINDOW_CHILDLOCKRIGHT,H56C.DCU_RR_DCU_RR_RRWINDOWLOCKSTS);//右侧后排车窗禁用信号


        map.put(WindowSignal.WINDOW_SETDRIVER,H56C.IVI_BodySet4_IVI_FLWINDOWHORIZONTALREQ);//设置主驾车窗开合度
        map.put(WindowSignal.WINDOW_SETPASSENGER,H56C.IVI_BodySet4_IVI_FRWINDOWHORIZONTALREQ);//设置副驾车窗开合度
        map.put(WindowSignal.WINDOW_SETLEFTREAR,H56C.IVI_BodySet4_IVI_RLWINDOWHORIZONTALREQ);//设置左后车窗开合度
        map.put(WindowSignal.WINDOW_SETRIGHTREAR,H56C.IVI_BodySet4_IVI_RRWINDOWHORIZONTALREQ);//设置右后车窗开合度
    }



    @Override
    public int getBaseIntProp(String key, int area) {
        //通用逻辑
        int key_37 = getRealKey(key);
        int area_37 = getRealArea(area);
        if (key_37 != -1) {
            return CarPropUtils.getInstance().getIntProp(key_37, area_37);
        }
        return INVALID;
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (key.equals(WindowSignal.WINDOW_WINDOW)) {
            WindowHelper.setWindow(area, value);
        } else {
            super.setBaseIntProp(key, area, value);
        }
    }



    @Override
    public boolean getBaseBooleanProp(String key, int area) {

        if (key.equals(WindowSignal.WINDOW_CHILD_LOCK)) {
            return WindowHelper.getChildLockStatus(area);
        }
        return getCommonBoolean(key, area);
    }

    @Override
    public void setBaseStringProp(String key, int area, String value) {
        if (key.equals(WindowSignal.WINDOW_WINDOW)) {
            WindowHelper.setWindow(area,value);
        } else {
            super.setBaseStringProp(key, area, value);
        }
    }

}
