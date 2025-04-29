package com.voyah.ai.device.voyah.common.H56Car;

import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.VehicleArea;
import mega.car.config.H56D;
import mega.car.config.Lighting;

public class ReadingLightHelper {

    private static final String TAG = "ReadingLightHelper";

    public static int getReadingLightStatus(int area) {
        int porpId;
        if (area == 0) {
            porpId = H56D.GW_LIN_BODY_ITL_FLREADLIGHTSTS;
        } else if (area == 1) {
            porpId = H56D.GW_LIN_BODY_ITL_FRREADLIGHTSTS;
        } else if (area == 2) {
            porpId = H56D.GW_LIN_BODY_ITL_RLREADLIGHTSTS;
        } else if (area == 3) {
            porpId = H56D.GW_LIN_BODY_ITL_RRREADLIGHTSTS;
        } else if (area == 4) {
            porpId = H56D.GW_LIN_BODY_ITL_THIRDROWLHREADLIGHTSTS;
        } else {
            porpId = H56D.GW_LIN_BODY_ITL_THIRDROWRHREADLIGHTSTS;
        }
        return CarPropUtils.getInstance().getIntProp(porpId);
    }

    public static void setReadingLightState(int area, int Value) {
        if (area == VehicleArea.ROW_3_LEFT) {
            CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet3_IVI_THIRDROWLHREADLIGHTREQ, Value);
        } else if (area == VehicleArea.ROW_3_RIGHT) {
            CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet3_IVI_THIRDROWRHREADLIGHTREQ, Value);
        } else {
            CarPropUtils.getInstance().setIntProp(Lighting.ID_CABIN_LIGHT_ON_OFF, area, Value);
        }
    }

}
