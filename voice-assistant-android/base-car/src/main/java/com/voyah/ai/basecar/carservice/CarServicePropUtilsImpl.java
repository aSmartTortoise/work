package com.voyah.ai.basecar.carservice;

import androidx.annotation.NonNull;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.vcar.CarPropertyEventCallback;
import com.voice.sdk.device.carservice.vcar.CarPropertyValue;
import com.voice.sdk.device.carservice.vcar.CarServicePropInterface;

import java.util.HashSet;
import java.util.Set;

import mega.car.CarPropertyManager;
import mega.car.SourceID;

public class CarServicePropUtilsImpl implements CarServicePropInterface {
    @NonNull
    @Override
    public String getVinCode() {
        return CarServicePropUtils.getInstance().getVinCode();
    }

    @NonNull
    @Override
    public String getCarType() {
        return CarServicePropUtils.getInstance().getCarType();
    }

    @Override
    public boolean isH37A() {
        return CarServicePropUtils.getInstance().isH37A();
    }

    @Override
    public boolean isH56C() {
        return CarServicePropUtils.getInstance().isH56C();
    }

    @Override
    public boolean isH56D() {
        return CarServicePropUtils.getInstance().isH56D();
    }

    @Override
    public boolean isVCOS15() {
        return isH56D();
    }

    @Override
    public boolean isH37B() {
        return CarServicePropUtils.getInstance().isH37B();
    }

    @NonNull
    @Override
    public void setIntProp(int propid, int status) {
        CarServicePropUtils.getInstance().setIntProp(propid, status);
    }

    @NonNull
    @Override
    public void setIntProp(int propid, int area, int status) {
        CarServicePropUtils.getInstance().setIntProp(propid, area, status);
    }

    @Override
    public int getIntProp(int propid) {
        return CarServicePropUtils.getInstance().getIntProp(propid);
    }

    @Override
    public int getIntProp(int propId, int area) {
        return CarServicePropUtils.getInstance().getIntProp(propId, area);
    }

    @NonNull
    @Override
    public void setFloatProp(int propId, float status) {
        CarServicePropUtils.getInstance().setFloatProp(propId, status);
    }

    @NonNull
    @Override
    public void setFloatProp(int propId, int area, float status) {
        CarServicePropUtils.getInstance().setFloatProp(propId, area, status);
    }

    @Override
    public float getFloatProp(int propId) {
        return CarServicePropUtils.getInstance().getFloatProp(propId);
    }

    @Override
    public float getFloatProp(int propId, int area) {
        return CarServicePropUtils.getInstance().getFloatProp(propId, area);
    }

    @NonNull
    @Override
    public void setRawProp(int propId, int area, int status, int sourceId) {
        CarServicePropUtils.getInstance().setRawProp(propId, area, status, sourceId);
    }


    @NonNull
    @Override
    public CarPropertyValue getPropertyRaw(int propId) {
        mega.car.hardware.CarPropertyValue carPropertyValue = CarServicePropUtils.getInstance().getPropertyRaw(propId);

//        mega.car.hardware.CarPropertyValue value = new mega.car.hardware.CarPropertyValue(propid, area, status);
//        value.setExtension(sourceId);
        CarPropertyValue carPropertyValue1 = new CarPropertyValue(carPropertyValue.getPropertyId(), carPropertyValue.getAreaId(), carPropertyValue.getStatus());
        carPropertyValue1.setExtension(new SourceID(SourceID.SOURCEID_VOICE));
        return carPropertyValue1;
    }

    @NonNull
    @Override
    public CarPropertyValue getPropertyRaw(int propId, int areaId) {
        mega.car.hardware.CarPropertyValue carPropertyValue = CarServicePropUtils.getInstance().getPropertyRaw(propId, areaId);

        CarPropertyValue carPropertyValue1 = new CarPropertyValue(carPropertyValue.getPropertyId(), carPropertyValue.getAreaId(), carPropertyValue.getStatus());

        return carPropertyValue1;
    }

    @Override
    public boolean vehicleSimulatorJudgment() {
        return CarServicePropUtils.vehicleSimulatorJudgment();
    }

    @NonNull
    @Override
    public void registerCallback(@NonNull CarPropertyEventCallback cb, @NonNull Set ids) {


        CarPropertyManager.CarPropertyEventCallback carPropertyEventCallback1 = new CarPropertyManager.CarPropertyEventCallback() {
            @Override
            public void onChangeEvent(mega.car.hardware.CarPropertyValue carPropertyValue) {
                CarPropertyValue carPropertyValue1 = new CarPropertyValue(carPropertyValue.getPropertyId(), carPropertyValue.getAreaId(), carPropertyValue.getValue());
                cb.onChangeEvent(carPropertyValue1);
            }

            @Override
            public void onErrorEvent(int i, int i1) {
                cb.onErrorEvent(i, i1);
            }
        };
        CarServicePropUtils.getInstance().registerCallback(carPropertyEventCallback1, ids);
    }

    @NonNull
    @Override
    public void unregisterCallback(@NonNull CarPropertyEventCallback changeCallback, @NonNull HashSet hashSet) {
        CarPropertyManager.CarPropertyEventCallback carPropertyEventCallback1 = new CarPropertyManager.CarPropertyEventCallback() {
            @Override
            public void onChangeEvent(mega.car.hardware.CarPropertyValue carPropertyValue) {
                CarPropertyValue carPropertyValue1 = new CarPropertyValue(carPropertyValue.getPropertyId(), carPropertyValue.getAreaId(), carPropertyValue.getStatus());
                changeCallback.onChangeEvent(carPropertyValue1);
            }

            @Override
            public void onErrorEvent(int i, int i1) {
                changeCallback.onErrorEvent(i, i1);
            }
        };
        CarServicePropUtils.getInstance().unregisterCallback(carPropertyEventCallback1, hashSet);
    }

    @Override
    public int getCarModeConfig() {
        return CarServicePropUtils.getInstance().getCarModeConfig();
    }

    @Override
    public int getDrvInfoGearPosition() {
        return DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher().getOperatorByDomain("sysctrl").getIntProp(CommonSignal.COMMON_GEAR_INFO);
    }
}
