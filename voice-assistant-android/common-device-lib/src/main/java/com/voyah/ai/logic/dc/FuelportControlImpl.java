package com.voyah.ai.logic.dc;

import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.FuelPortSignal;

import java.util.HashMap;


public class FuelportControlImpl extends AbsDevices {

    public FuelportControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "Fuelport";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        return str;
    }

    public boolean getFuelportConfig(HashMap<String, Object> map) {
        // 0=纯电 1=混动
        return operator.getIntProp(CommonSignal.COMMON_POWER_MODER) == 0;
    }

    public boolean isHasRiskOfPressureSurge(HashMap<String, Object> map) {
        return getFuelSystemCode() == 1;
    }

    public boolean isFailureOfPressureRelief(HashMap<String, Object> map) {
        return getFuelSystemCode() == 2;
    }

    public int getFuelSystemCode() {
        //0x0：初始值 0x1：满油状态无法泄压 0x2: 泄压超时 0x3：无效
        return operator.getIntProp(FuelPortSignal.FUEL_PRESSURE_RELIEFSTA);
    }

    public int getDriveGearPosition(HashMap<String, Object> map) {
        //P挡:0 R挡:1 N挡:2 D挡:3 S挡:4
        return operator.getIntProp(CommonSignal.COMMON_GEAR_INFO);
    }

    public boolean getFuelportStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(FuelPortSignal.FUEL_PORT_SWITCH);
    }

    public void setFuelportStatus(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(FuelPortSignal.FUEL_PORT_SWITCH, switchType.equals("open"));
    }
}
