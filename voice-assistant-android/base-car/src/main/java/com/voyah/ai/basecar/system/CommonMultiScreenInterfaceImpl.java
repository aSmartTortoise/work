package com.voyah.ai.basecar.system;


import android.content.Context;
import android.os.UserHandle;

import com.mega.nexus.os.MegaScreenManager;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.Domain;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.vcar.IPropertyOperator;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.system.ScreenInterface;
import com.voice.sdk.device.ui.IScreenStateChangeListener;
import com.voice.sdk.device.ui.ScreenStateHelper;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.cockpit.window.model.ScreenType;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CommonMultiScreenInterfaceImpl extends BaseScreenInterfaceImpl implements ScreenInterface {

    public CommonMultiScreenInterfaceImpl() {
        super();
    }

    @Override
    public int getMainScreenDisplayId() {
        return MegaDisplayHelper.getMainScreenDisplayId();
    }

    @Override
    public int getPassengerScreenDisplayId() {
        return MegaDisplayHelper.getPassengerScreenDisplayId();
    }

    @Override
    public int getCeilingScreenDisplayId() {
        return MegaDisplayHelper.getCeilingScreenDisplayId();
    }

    @Override
    public int getCurVpaDisplayId() {
        return MegaDisplayHelper.getVoiceDisplayId();
    }

    @Override
    public int getVoiceDisplayId(int direction) {
        return MegaDisplayHelper.getVoiceDisplayId(direction);
    }

    @Override
    public int getScreenType(String position) {
        int screenType = ScreenType.MAIN;
        switch (position){
            case "first_row_left"://主驾
                screenType = ScreenType.MAIN;
                break;
            case "first_row_right"://副驾
                screenType = ScreenType.PASSENGER;
                break;
            case "rear_side"://后排
            case "rear_side_left"://左后
            case "rear_side_mid"://后排中
            case "rear_side_right"://右后
            case "second_side"://二排
            case "second_row_right"://二排右
            case "second_row_left"://二排左
            case "second_row_mid"://二排中
            case "third_side"://三排
            case "third_row_left"://三排左
            case "third_row_right"://三排右
            case "third_row_mid"://三排中
                screenType = ScreenType.CEILING;
                break;
        }
        return screenType;
    }

    @Override
    public boolean isSupportMultiScreen() {
        return true;
    }

    @Override
    public boolean isSupportScreen(DeviceScreenType deviceScreenType) {
        if (deviceScreenType == null) {
            deviceScreenType = DeviceScreenType.CENTRAL_SCREEN;
        }
        if (deviceScreenType == DeviceScreenType.CENTRAL_SCREEN) {
            return true;
        }
        if (deviceScreenType == DeviceScreenType.PASSENGER_SCREEN) {
            return true;
        }
        if (deviceScreenType == DeviceScreenType.CEIL_SCREEN) {
            return DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher()
                    .getOperatorByDomain(Domain.SYS_CTRL.getDomain()).getBooleanProp(CommonSignal.COMMON_CEIL_CONFIG);
        }
        return false;
    }

    @Override
    public boolean isCeilScreenOpen() {
        return DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher()
                .getOperatorByDomain(Domain.SYS_CTRL.getDomain()).getBooleanProp(CommonSignal.COMMON_CEIL_OPEN);
    }

    @Override
    public void openCeilScreen(int maxWaitTime) {

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        ScreenStateHelper.INSTANCE.addListener(ScreenStateHelper.SCREENID_CEILING, new IScreenStateChangeListener() {
            @Override
            public void onScreenStateChanged(int screenId, int state) {
                if (MegaScreenManager.SCREENID_CEILING == screenId && MegaScreenManager.STATE_ON == state) {
                    future.complete(true);
                }
            }

            @Override
            public boolean isPersistent() {
                return false;
            }
        });

        try {
            if (maxWaitTime < 0) {
                future.complete(true);
            } else {
                future.get(maxWaitTime, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        IPropertyOperator operator = DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher()
                .getOperatorByDomain(Domain.SYS_CTRL.getDomain());
        int lastAngle = operator.getIntProp(CommonSignal.COMMON_LAST_ANGLE);
        operator.setIntProp(CommonSignal.COMMON_CEIL_ANGLE, lastAngle);
    }

    @Override
    public void onCeilOpen(Runnable runnable) {
        ScreenStateHelper.INSTANCE.addListener(ScreenStateHelper.SCREENID_CEILING, new IScreenStateChangeListener() {
            @Override
            public void onScreenStateChanged(int screenId, int state) {
                if (MegaScreenManager.SCREENID_CEILING == screenId && MegaScreenManager.STATE_ON == state) {
                    runnable.run();
                }
            }

            @Override
            public boolean isPersistent() {
                return false;
            }
        });
        IPropertyOperator operator = DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher()
                .getOperatorByDomain(Domain.SYS_CTRL.getDomain());
        int lastAngle = operator.getIntProp(CommonSignal.COMMON_LAST_ANGLE);
        operator.setIntProp(CommonSignal.COMMON_CEIL_ANGLE, lastAngle);
    }

    @Override
    public int getDisplayId(DeviceScreenType deviceScreenType) {
        return MegaDisplayHelper.getDisplayId(deviceScreenType);
    }

    @Override
    public int getDisplayId(int screenType) {
        return MegaDisplayHelper.getDisplayId(screenType);
    }

    @Override
    public UserHandle getUserHandle(DeviceScreenType deviceScreenType) {
        return MegaDisplayHelper.getUserHandle(deviceScreenType);
    }

    @Override
    public Context getScreenContext(DeviceScreenType deviceScreenType) {
        return MegaDisplayHelper.getScreenContext(deviceScreenType);
    }
}
