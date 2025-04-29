package com.voyah.ai.device;

import android.content.Context;

import com.example.aidlvehicleactivationmodule.manager.CarActivationManager;
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
import com.voice.sdk.device.base.AIInterface;
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
import com.voice.sdk.device.base.DialogueInterface;
import com.voice.sdk.device.base.FeedbackInterface;
import com.voice.sdk.device.base.LongAsrInterface;
import com.voice.sdk.device.base.SettingsInterface;
import com.voice.sdk.device.base.VprInterface;
import com.voice.sdk.device.media.MediaControlInterface;
import com.voice.sdk.device.media.MediaPageInterface;
import com.voice.sdk.device.media.MediaCenterInterface;
import com.voice.sdk.device.navi.NaviInterface;
import com.voice.sdk.device.phone.PhoneInterface;
import com.voice.sdk.device.system.SystemInterface;
import com.voice.sdk.device.tts.BeanTtsInterface;
import com.voice.sdk.device.tts.HeadSetInterface;
import com.voice.sdk.device.tts.VoiceCopyInterface;
import com.voice.sdk.device.ui.IThreadDelay;
import com.voice.sdk.device.ui.UiAbilityInterface;
import com.voice.sdk.device.ui.UiInterface;
import com.voice.sdk.device.viewcmd.ViewCmdInterface;
import com.voyah.ai.basecar.buriedpoint.BuriedPointManagerImp;
import com.voyah.ai.basecar.carservice.CarServicePropUtilsImpl;
import com.voyah.ai.basecar.manager.AIManager;
import com.voyah.ai.basecar.manager.DialogueManager;
import com.voyah.ai.basecar.manager.FeedbackManager;
import com.voyah.ai.basecar.manager.LongAsrManager;
import com.voyah.ai.basecar.manager.SettingsManager;
import com.voyah.ai.basecar.manager.VprManager;
import com.voyah.ai.basecar.voicecopy.VoiceReproductionManager;
import com.voyah.ai.basecar.BaseGalleryPresenter;
import com.voyah.ai.basecar.ScheduleInterfaceImpl;
import com.voyah.ai.basecar.StockInterfaceImpl;
import com.voyah.ai.basecar.UserCenterInterfaceImpl;
import com.voyah.ai.basecar.WeatherPresenterImpl;
import com.voyah.ai.basecar.appstore.CommonAppStoreInterfaceImpl;
import com.voyah.ai.basecar.llm.LLMTextInterfaceImpl;
import com.voyah.ai.basecar.media.MediaInterfaceImpl;
import com.voyah.ai.basecar.media.MediaPageInterfaceImpl;
import com.voyah.ai.basecar.navi.NaviInterfaceImpl;
import com.voyah.ai.basecar.phone.PhoneInterfaceImpl;
import com.voyah.ai.basecar.recorder.AudioRecorderManager;
import com.voyah.ai.basecar.showbox.ShowBoxInterfaceImpl;
import com.voyah.ai.basecar.tts.BeanTtsManager;
import com.voyah.ai.basecar.ui.UiInterfaceImpl;
import com.voyah.ai.basecar.utils.BeanDumpManager;
import com.voyah.ai.basecar.utils.SentryUtils;
import com.voyah.ai.basecar.viewcmd.ViewCmdInterfaceImpl;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.device.base.recorder.forbidden.VoiceCarSignalManager;
import com.voyah.ai.device.voyah.CarServiceInterfaceImpl;
import com.voyah.ai.device.voyah.common.media.MediaCenterInterfaceImpl;
import com.voyah.ai.device.voyah.h37.dc.utils.AdasImpl;
import com.voyah.ai.device.voyah.h37.dc.utils.H56CSettingInterfaceImpl;
import com.voyah.ai.device.voyah.h37.dc.utils.MegaDataStorageConfigUtils;
import com.voyah.ai.device.voyah.h37.dvr.MyCameraManager;
import com.voyah.ai.device.voyah.h37.launcher.LauncherManager;
import com.voyah.ai.device.voyah.h37.tts.HeadSetObserver;
import com.voyah.ai.device.voyah.system.SystemInterfaceImpl;

public class RealCar {
    private static final String TAG = "RealCar";

    private static ViewCmdInterface viewCmdInterface;

    private static LauncherInterface launcherInterface;

    private static CarServicePropInterface carServiceProp;

    private static final AppStoreInterface appStoreInterface = new CommonAppStoreInterfaceImpl();

    private static IBuriedPointManager buriedPointManager = new BuriedPointManagerImp();


    public static void init(Context context) {
        LogUtils.i(TAG, "h56 real car init");
        DeviceHolder.INS().setDevicesInterface(new DevicesInterface() {

            public CarServicePropInterface getCarServiceProp() {
                if (carServiceProp == null) {
                    carServiceProp = new CarServicePropUtilsImpl();
                }
                return carServiceProp;
            }

            @Override
            public CarServiceInterface getCarService() {
                return CarServiceInterfaceImpl.INSTANCE;
            }

            @Override
            public AudioRecorderInterface getAudioRecorder() {
                return AudioRecorderManager.getInstance();
            }

            @Override
            public UiInterface getUiCardInterface() {
                return UiInterfaceImpl.getInstance();
            }

            @Override
            public UiAbilityInterface getUi() {
                return UiInterfaceImpl.getInstance();
            }

            @Override
            public BeanTtsInterface getTts() {
                return BeanTtsManager.getInstance();
            }

            @Override
            public NaviInterface getNavi() {
                return NaviInterfaceImpl.getInstance();
            }

            @Override
            public AppStoreInterface getAppStore() {
                return appStoreInterface;
            }

            @Override
            public SystemInterface getSystem() {
                return SystemInterfaceImpl.getInstance();
            }

            @Override
            public ViewCmdInterface getViewCmd() {
                if (viewCmdInterface == null) {
                    viewCmdInterface = new ViewCmdInterfaceImpl();
                }
                return viewCmdInterface;
            }

            public LauncherInterface getLauncher() {
                if (launcherInterface == null) {
                    launcherInterface = LauncherManager.getInstance();
                }
                return launcherInterface;
            }

            @Override
            public SettingInterface getSetting() {
                return H56CSettingInterfaceImpl.INSTANCE;
                //TODO 设置适配完之后放开
                //return SettingUtils.getInstance();
            }

            @Override
            public SentryInterface getSentry() {
                return SentryUtils.getInstance();
            }

            @Override
            public ShowBoxInterface getShowBox() {
                return ShowBoxInterfaceImpl.getInstance();
            }

            @Override
            public GalleryInterface getGallery() {
                return GalleryInterfaceImpl.getInstance();
            }

            @Override
            public VoiceCarSignalInterface getVoiceCarSignal() {
                return VoiceCarSignalManager.getInstance();

            }

            @Override
            public IWeather getWeather() {
                return WeatherPresenterImpl.getInstance();
            }

            @Override
            public MediaControlInterface getMedia() {
                return MediaInterfaceImpl.getInstance();
            }

            @Override
            public ScheduleInterface getSchedule() {
                return ScheduleInterfaceImpl.getInstance();
            }

            @Override
            public UserCenterInterface getUserCenter() {
                return UserCenterInterfaceImpl.getInstance();
            }

            @Override
            public StockInterface getStock() {
                return StockInterfaceImpl.getInstance();
            }

            @Override
            public HeadSetInterface getHeadSet() {
                return HeadSetObserver.INSTANCE;
            }

            @Override
            public VoiceCopyInterface getVoiceCopy() {
                return VoiceReproductionManager.INSTANCE;
            }


            @Override
            public LLMDrawingInterface getLLMDrawing() {
                return LLMDrawingInterfaceImpl.getInstance();
            }

            @Override
            public MediaCenterInterface getMediaCenter() {
                return MediaCenterInterfaceImpl.getInstance();
            }

            @Override
            public MyCameraInterface getCamera() {
                return MyCameraManager.getInstance();
            }

            public PhoneInterface getPhone() {
                return PhoneInterfaceImpl.getInstance();
            }

            @Override
            public LLMTextInterface getLLMText() {
                return LLMTextInterfaceImpl.getInstance();
            }

            @Override
            public MediaPageInterface getMediaPage() {
                return MediaPageInterfaceImpl.getInstance();
            }

            @Override
            public IThreadDelay getThreadDelay() {
                return BeanDumpManager.getInstance();
            }

            @Override
            public AdasInterface getAdasInterface() {
                return AdasImpl.getInstance();
            }

            @Override
            public SettingsInterface getVoiceSettings() {
                return SettingsManager.get();
            }

            @Override
            public DialogueInterface getDialogue() {
                return DialogueManager.get();
            }

            @Override
            public FeedbackInterface getFeedback() {
                return FeedbackManager.get();
            }

            @Override
            public LongAsrInterface getLongAsr() {
                return LongAsrManager.get();
            }

            @Override
            public AIInterface getAI() {
                return AIManager.get();
            }

            @Override
            public VprInterface getVpr() {
                return VprManager.get();
            }

            @Override
            public IBuriedPointManager getBuriedPointManager() {
                return buriedPointManager;
            }

            @Override
            public IVirtualDevice getVSignalProvider() {
                return null;
            }


        });
        MegaDataStorageConfigUtils.init(ContextUtils.getAppContext());
        CarActivationManager.getInstance().init(ContextUtils.getAppContext());
        DeviceHolder.INS().getDevices().getUiCardInterface().init();
        DeviceHolder.INS().getDevices().getVoiceCarSignal().init();
        DeviceHolder.INS().getDevices().getNavi().init();
        DeviceHolder.INS().getDevices().getAppStore().init();
        DeviceHolder.INS().getDevices().getShowBox().init();
        DeviceHolder.INS().getDevices().getGallery().init();
        DeviceHolder.INS().getDevices().getWeather().init();
        DeviceHolder.INS().getDevices().getSchedule().init();
        DeviceHolder.INS().getDevices().getUserCenter().init();
        DeviceHolder.INS().getDevices().getLLMDrawing().init();
        DeviceHolder.INS().getDevices().getPhone().init();
        DeviceHolder.INS().getDevices().getCamera().init();
        DeviceHolder.INS().getDevices().getMedia().init();
    }
}
