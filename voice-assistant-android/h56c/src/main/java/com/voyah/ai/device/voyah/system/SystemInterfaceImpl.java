package com.voyah.ai.device.voyah.system;

import com.voice.sdk.device.system.AppInterface;
import com.voice.sdk.device.system.AttributeInterface;
import com.voice.sdk.device.system.KeyboardInterface;
import com.voice.sdk.device.system.ScreenInterface;
import com.voice.sdk.device.system.ShareInterface;
import com.voice.sdk.device.system.SplitScreenInterface;
import com.voice.sdk.device.system.SystemInterface;
import com.voice.sdk.device.system.UiInterface;
import com.voice.sdk.device.system.VolumeInterface;
import com.voyah.ai.basecar.system.CommonAppInterfaceImpl;
import com.voyah.ai.basecar.system.CommonAttributeInterfaceImpl;
import com.voyah.ai.basecar.system.CommonMultiScreenInterfaceImpl;
import com.voyah.ai.basecar.system.CommonVolumeInterfaceImpl;
import com.voyah.ai.basecar.system.CommonUiInterfaceImpl;
import com.voyah.ai.basecar.system.CommonSplitScreenImpl;
import com.voyah.ai.basecar.system.CommonKeyboardImpl;
import com.voyah.ai.device.voyah.h37.dc.utils.ShareUtils;

public class SystemInterfaceImpl implements SystemInterface {

    private SystemInterfaceImpl() {

    }

    private static final SystemInterfaceImpl instance = new SystemInterfaceImpl();

    private final UiInterface uiInterface = new CommonUiInterfaceImpl();

    private final VolumeInterface volumeInterface = new CommonVolumeInterfaceImpl();

    private final ScreenInterface screenInterface = new CommonMultiScreenInterfaceImpl();

    private final AppInterface appInterface = new CommonAppInterfaceImpl();

    private final AttributeInterface attributeInterface = new CommonAttributeInterfaceImpl();

    private final SplitScreenInterface splitScreenInterface = new CommonSplitScreenImpl();

    private final KeyboardInterface keyboardInterface = new CommonKeyboardImpl();

    public static SystemInterfaceImpl getInstance() {
        return instance;
    }


    @Override
    public VolumeInterface getVolume() {
        return volumeInterface;
    }

    @Override
    public UiInterface getUi() {
        return uiInterface;
    }

    @Override
    public AppInterface getApp() {
        return appInterface;
    }

    @Override
    public ScreenInterface getScreen() {
        return screenInterface;
    }

    @Override
    public SplitScreenInterface getSplitScreen() {
        return splitScreenInterface;
    }

    @Override
    public AttributeInterface getAttribute() {
        return attributeInterface;
    }

    @Override
    public KeyboardInterface getKeyboard() {
        return keyboardInterface;
    }

    @Override
    public ShareInterface getShare() {
        return ShareUtils.getInstance();
    }
}
