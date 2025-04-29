package com.voice.sdk.device;


import com.voice.sdk.buriedpoint.IBuriedPointManager;
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

public interface DevicesInterface {
    SystemInterface getSystem();

    NaviInterface getNavi();

    AppStoreInterface getAppStore();

    ViewCmdInterface getViewCmd();

    CarServiceInterface getCarService();

    BeanTtsInterface getTts();

    UiAbilityInterface getUi();

    UiInterface getUiCardInterface();

    LauncherInterface getLauncher();

    CarServicePropInterface getCarServiceProp();

    SettingInterface getSetting();  //车辆设置的接口

    SentryInterface getSentry();

    ShowBoxInterface getShowBox();

    GalleryInterface getGallery();

    AudioRecorderInterface getAudioRecorder();

    IWeather getWeather();

    MediaControlInterface getMedia();

    ScheduleInterface getSchedule();

    UserCenterInterface getUserCenter();

    StockInterface getStock();

    HeadSetInterface getHeadSet();

    VoiceCopyInterface getVoiceCopy();

    PhoneInterface getPhone();

    VoiceCarSignalInterface getVoiceCarSignal();

    LLMDrawingInterface getLLMDrawing();

    LLMTextInterface getLLMText();

    MediaPageInterface getMediaPage();

    MediaCenterInterface getMediaCenter();

    IThreadDelay getThreadDelay();

    AdasInterface getAdasInterface();

    MyCameraInterface getCamera();

    SettingsInterface getVoiceSettings();

    DialogueInterface getDialogue();

    FeedbackInterface getFeedback();

    LongAsrInterface getLongAsr();

    AIInterface getAI();

    VprInterface getVpr();

    IBuriedPointManager getBuriedPointManager();

    IVirtualDevice getVSignalProvider();

}
