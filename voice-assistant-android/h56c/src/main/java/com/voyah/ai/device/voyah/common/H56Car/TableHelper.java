package com.voyah.ai.device.voyah.common.H56Car;


import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.config.Ecu;

public class TableHelper {

    public static int getTableConfig() {
        return CarPropUtils.getInstance().getIntProp(Ecu.ID_CONFIG_ELECTRIC_DESK);
    }

    public static void setTableState() {
        CarPropUtils.getInstance().setIntProp(1677721855, 1);
    }
}
