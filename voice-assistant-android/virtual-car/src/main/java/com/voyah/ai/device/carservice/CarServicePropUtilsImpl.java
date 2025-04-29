package com.voyah.ai.device.carservice;

import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.vcar.CarPropertyEventCallback;
import com.voice.sdk.device.carservice.vcar.CarPropertyValue;
import com.voice.sdk.device.carservice.vcar.CarServicePropInterface;
import com.voice.sdk.device.carservice.vcar.IPropertyOperator;
import com.voyah.ai.virtual.dc.BaseVirtualPropertyOperator;

import java.util.HashSet;
import java.util.Set;

public class CarServicePropUtilsImpl implements CarServicePropInterface {

    private IPropertyOperator operator = new BaseVirtualPropertyOperator();
    @Override
    public String getVinCode() {
        return "";
    }

    @Override
    public String getCarType() {
        return "h56c";
        //return operator.getStringProp(CommonSignal.COMMON_CAR_TYPE);
    }

    @Override
    public boolean isH37A() {
        return "h37a".equalsIgnoreCase(getCarType());
    }

    @Override
    public boolean isH56C() {
        return "h356c".equalsIgnoreCase(getCarType());
    }

    @Override
    public boolean isH56D() {
        return "h56d".equalsIgnoreCase(getCarType());
    }

    @Override
    public boolean isH37B() {
        return "h37b".equalsIgnoreCase(getCarType());
    }

    @Override
    public void setIntProp(int propid, int status) {

    }

    @Override
    public void setIntProp(int propid, int area, int status) {
    }

    @Override
    public int getIntProp(int propid) {
        return -1;
    }

    @Override
    public int getIntProp(int propId, int area) {
        return -1;
    }

    @Override
    public void setFloatProp(int propId, float status) {
    }

    @Override
    public void setFloatProp(int propId, int area, float status) {
    }

    @Override
    public float getFloatProp(int propId) {
        return -1;
    }

    @Override
    public float getFloatProp(int propId, int area) {
        return -1;
    }

    @Override
    public void setRawProp(int propId, int area, int status, int sourceId) {
    }


    @Override
    public CarPropertyValue getPropertyRaw(int propId) {
        return new CarPropertyValue(-1, -1);
    }

    @Override
    public CarPropertyValue getPropertyRaw(int propId, int areaId) {
        return new CarPropertyValue(-1, -1);
    }

    @Override
    public boolean vehicleSimulatorJudgment() {
        return false;
    }

    @Override
    public void registerCallback(CarPropertyEventCallback cb,  Set ids) {
    }

    @Override
    public void unregisterCallback(CarPropertyEventCallback changeCallback, HashSet hashSet) {
    }

    @Override
    public int getCarModeConfig() {
        return operator.getIntProp(CommonSignal.COMMON_EQUIPMENT_LEVEL);
    }

    @Override
    public int getDrvInfoGearPosition() {
        return operator.getIntProp(CommonSignal.COMMON_GEAR_INFO);
    }
}
