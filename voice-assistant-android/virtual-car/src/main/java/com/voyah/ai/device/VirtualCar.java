package com.voyah.ai.device;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.voice.sdk.buriedpoint.IBuriedPointManager;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.DevicesInterface;
import com.voice.sdk.device.GalleryInterface;
import com.voice.sdk.device.IWeather;
import com.voice.sdk.device.ScheduleInterface;
import com.voice.sdk.device.ShowBoxInterface;
import com.voice.sdk.device.StockInterface;
import com.voice.sdk.device.UserCenterInterface;
import com.voice.sdk.device.appstore.AppStoreInterface;
import com.voice.sdk.device.audio.AudioRecorderInterface;
import com.voice.sdk.device.base.DialogueInterface;
import com.voice.sdk.device.base.FeedbackInterface;
import com.voice.sdk.device.base.LongAsrInterface;
import com.voice.sdk.device.base.SettingsInterface;
import com.voice.sdk.device.base.VprInterface;
import com.voice.sdk.device.carservice.dc.AdasInterface;
import com.voice.sdk.device.carservice.dc.SentryInterface;
import com.voice.sdk.device.carservice.dc.carsetting.SettingInterface;
import com.voice.sdk.device.carservice.vcar.CarServiceInterface;
import com.voice.sdk.device.carservice.vcar.CarServicePropInterface;
import com.voice.sdk.device.carservice.vcar.IVirtualDevice;
import com.voice.sdk.device.dvr.MyCameraInterface;
import com.voice.sdk.device.forbidden.VoiceCarSignalInterface;
import com.voice.sdk.device.launcher.LauncherInterface;
import com.voice.sdk.device.llm.LLMDrawingInterface;
import com.voice.sdk.device.llm.LLMTextInterface;
import com.voice.sdk.device.media.MediaCenterInterface;
import com.voice.sdk.device.media.MediaControlInterface;
import com.voice.sdk.device.media.MediaPageInterface;
import com.voice.sdk.device.navi.NaviInterface;
import com.voice.sdk.device.phone.PhoneInterface;
import com.voice.sdk.device.system.AppInterface;
import com.voice.sdk.device.system.AttributeInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.system.KeyboardInterface;
import com.voice.sdk.device.system.ScreenInterface;
import com.voice.sdk.device.system.ShareInterface;
import com.voice.sdk.device.system.SplitScreenInterface;
import com.voice.sdk.device.system.SystemInterface;
import com.voice.sdk.device.system.VolumeInterface;
import com.voice.sdk.device.tts.BeanTtsInterface;
import com.voice.sdk.device.tts.HeadSetInterface;
import com.voice.sdk.device.tts.VoiceCopyInterface;
import com.voice.sdk.device.ui.IThreadDelay;
import com.voice.sdk.device.ui.UiAbilityInterface;
import com.voice.sdk.device.ui.UiInterface;
import com.voice.sdk.device.ui.listener.IUiStateListener;
import com.voice.sdk.device.ui.listener.UICardListener;
import com.voice.sdk.device.viewcmd.ViewCmdInterface;
import com.voice.sdk.vcar.VirtualDeviceManager;
import com.voyah.ai.device.carservice.AdasInterfaceImpl;
import com.voyah.ai.device.carservice.CarServiceInterfaceImpl;
import com.voyah.ai.device.carservice.CarServicePropUtilsImpl;
import com.voyah.ai.device.manager.DialogueManager;
import com.voyah.ai.device.system.SystemInterfaceImpl;
import com.voyah.ai.device.tts.BeanTtsInterfaceImpl;

/**
 * @Date 2025/3/31 17:48
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class VirtualCar {

    private static CarServicePropInterface carServicePropUtilsImpl = new CarServicePropUtilsImpl();

    public static void init() {
        DeviceHolder.INS().setDevicesInterface(new DevicesInterface() {
            @Override
            public SystemInterface getSystem() {
                return SystemInterfaceImpl.INSTANCE;
            }

            @Override
            public NaviInterface getNavi() {
                return null;
            }

            @Override
            public AppStoreInterface getAppStore() {
                return null;
            }

            @Override
            public ViewCmdInterface getViewCmd() {
                return null;
            }

            @Override
            public CarServiceInterface getCarService() {
                return CarServiceInterfaceImpl.INSTANCE;
            }

            @Override
            public BeanTtsInterface getTts() {
                return BeanTtsInterfaceImpl.INSTANCE;
            }

            @Override
            public UiAbilityInterface getUi() {
                return new UiAbilityInterface() {
                    @Override
                    public void onChangeCardOwner(int i, @NonNull Object o, @NonNull String s, int i1, @NonNull Runnable runnable) {
                    }

                    @Override
                    public void showVoiceVpa(@NonNull String s, int i, @NonNull String s1, int i1) {

                    }

                    @Override
                    public void dismissVoiceView(int i) {

                    }

                    @Override
                    public void showWave(int i) {

                    }

                    @Override
                    public void dismissWave() {

                    }

                    @Override
                    public void showCard(int i, @NonNull Object o, int i1) {

                    }

                    @Override
                    public void dismissCard(int i) {

                    }

                    @Override
                    public void onVoiceExit() {

                    }

                    @Override
                    public void setLanguageType(@NonNull String s) {

                    }

                    @Override
                    public void setUiStateListener(@Nullable IUiStateListener iUiStateListener) {

                    }

                    @Override
                    public void addCardStateListener(@Nullable UICardListener uiCardListener) {

                    }

                    @Override
                    public void removeCardStateListener(@Nullable UICardListener uiCardListener) {

                    }
                };
            }

            @Override
            public UiInterface getUiCardInterface() {
                return null;
            }

            @Override
            public LauncherInterface getLauncher() {
                return null;
            }

            @Override
            public CarServicePropInterface getCarServiceProp() {
                return carServicePropUtilsImpl;
            }

            @Override
            public SettingInterface getSetting() {
                return null;
            }

            @Override
            public SentryInterface getSentry() {
                return null;
            }

            @Override
            public ShowBoxInterface getShowBox() {
                return null;
            }

            @Override
            public GalleryInterface getGallery() {
                return null;
            }

            @Override
            public AudioRecorderInterface getAudioRecorder() {
                return null;
            }

            @Override
            public IWeather getWeather() {
                return null;
            }

            @Override
            public MediaControlInterface getMedia() {
                return null;
            }

            @Override
            public ScheduleInterface getSchedule() {
                return null;
            }

            @Override
            public UserCenterInterface getUserCenter() {
                return null;
            }

            @Override
            public StockInterface getStock() {
                return null;
            }

            @Override
            public HeadSetInterface getHeadSet() {
                return null;
            }

            @Override
            public VoiceCopyInterface getVoiceCopy() {
                return null;
            }

            @Override
            public PhoneInterface getPhone() {
                return null;
            }

            @Override
            public VoiceCarSignalInterface getVoiceCarSignal() {
                return null;
            }

            @Override
            public LLMDrawingInterface getLLMDrawing() {
                return null;
            }

            @Override
            public LLMTextInterface getLLMText() {
                return null;
            }

            @Override
            public MediaPageInterface getMediaPage() {
                return null;
            }

            @Override
            public MediaCenterInterface getMediaCenter() {
                return null;
            }

            @Override
            public IThreadDelay getThreadDelay() {
                return null;
            }

            @Override
            public AdasInterface getAdasInterface() {
                return AdasInterfaceImpl.INSTANCE;
            }

            @Override
            public MyCameraInterface getCamera() {
                return null;
            }

            @Override
            public SettingsInterface getVoiceSettings() {
                return null;
            }

            @Override
            public DialogueInterface getDialogue() {
                return DialogueManager.INSTANCE;
            }

            @Override
            public FeedbackInterface getFeedback() {
                return null;
            }

            @Override
            public LongAsrInterface getLongAsr() {
                return null;
            }

            @Override
            public VprInterface getVpr() {
                return null;
            }

            @Override
            public IBuriedPointManager getBuriedPointManager() {
                return null;
            }

            @Override
            public IVirtualDevice getVSignalProvider() {
                return VirtualDeviceManager.getInstance().getVirtualDevice();
            }
        });
    }
}
