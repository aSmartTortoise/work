package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.signal.TableSignal;


/**
 * @Date 2024/10/18 14:46
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class TablePropertyOperator extends Base56Operator {
    @Override
    public boolean getBooleanProp(String key) {
        if (TableSignal.TABLE_CONFIG.equals(key)) {
            return MegaSystemProperties.getInt(MegaProperties.CONFIG_ELECTRIC_DESK, -1) == 1;
        } else {
            return super.getBooleanProp(key);
        }
    }

    @Override
    public void setBooleanProp(String key, boolean value) {
        if (TableSignal.TABLE_CMD.equals(key)) {
//            CarPropUtils.getInstance().setIntProp(Signal.ID_ELEC_TALBD_CMD, Signal.EnumELECTBLBDCMD.EXTEND);
        } else {
            super.setBooleanProp(key, value);
        }
    }

    @Override
    void init() {

    }
}
