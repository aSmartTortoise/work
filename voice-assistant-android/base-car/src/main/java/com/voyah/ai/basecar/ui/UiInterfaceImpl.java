package com.voyah.ai.basecar.ui;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaScreenManager;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.constant.UiConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.system.ScreenInterface;
import com.voice.sdk.device.ui.IScreenStateChangeListener;
import com.voice.sdk.device.ui.ScreenStateHelper;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.device.ui.UiAbilityInterface;
import com.voice.sdk.device.ui.UiInterface;
import com.voice.sdk.device.ui.listener.IUiStateListener;
import com.voice.sdk.device.ui.listener.UICardListener;
import com.voyah.ai.basecar.manager.SettingsManager;
import com.voice.sdk.model.CPEntity;
import com.voice.sdk.record.VoiceStateRecordManager;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.sdk.bean.DhDialect;
import com.voyah.cockpit.window.WindowMessageManager;
import com.voyah.cockpit.window.model.APIResult;
import com.voyah.cockpit.window.model.BTPhoneInfo;
import com.voyah.cockpit.window.model.CardDirection;
import com.voyah.cockpit.window.model.CardInfo;
import com.voyah.cockpit.window.model.ChatMessage;
import com.voyah.cockpit.window.model.Contact;
import com.voyah.cockpit.window.model.DomainType;
import com.voyah.cockpit.window.model.LanguageType;
import com.voyah.cockpit.window.model.MultiMusicInfo;
import com.voyah.cockpit.window.model.MultimediaInfo;
import com.voyah.cockpit.window.model.PageInfo;
import com.voyah.cockpit.window.model.ScreenType;
import com.voyah.cockpit.window.model.StreamMode;
import com.voyah.cockpit.window.model.UIMessage;
import com.voyah.cockpit.window.model.ViewType;
import com.voyah.cockpit.window.model.VoiceMode;
import com.voyah.cockpit.window.model.Weather;
import com.voyah.cockpit.window.model.WindowAction;
import com.voyah.cockpit.window.model.WindowMessage;
import com.voyah.cockpit.window.model.WindowType;
import com.voyah.ds.common.entity.IData;
import com.voyah.ds.common.entity.domains.call.ContactNumberInfo;
import com.voyah.ds.common.entity.domains.weather.MultiDaysWeather;
import com.voyah.ds.common.entity.domains.weather.OneDayWeather;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author:lcy
 * @data:2024/3/8
 **/
public class UiInterfaceImpl implements UiInterface, UiAbilityInterface {
    private static final String TAG = UiInterfaceImpl.class.getSimpleName();

    private static final UiInterfaceImpl uiImpl = new UiInterfaceImpl();

    private static final int CHANGE_STATE_LISTENING = 0; //非信息类卡片TTS播报完500ms改为聆听态
    private static final int CHANGE_INFORMATION_STATE_LISTENING = 1; //信息类卡片TTS播报完5s改为聆听态
    private static final int CHANGE_INFORMATION_STATE = 2; //信息类卡片TTS播报完5s关闭卡片
    private static final long DELAY_SEND_TIME = 3000;

    private boolean isGestureInformation = false;


    private IUiStateListener iUiStateListener;

    private List<UICardListener> mUIStateListeners;

    //UI服务
    private WindowMessageManager windowMessageManager;

    private Handler handler;
    private HandlerThread handlerThread;

    private final IScreenStateChangeListener listener = new IScreenStateChangeListener() {
        @Override
        public boolean isPersistent() {
            return true;
        }

        @Override
        public void onScreenStateChanged(int displayId, int newState) {
            if (MegaScreenManager.SCREENID_CEILING == displayId || MegaScreenManager.SCREENID_PASSENGER == displayId) {
                LogUtils.d(TAG, "onScreenStateChanged displayId:" + displayId + " newState:" + newState);
                int screen = 0;
                if (MegaScreenManager.SCREENID_CEILING == displayId) {
                    screen = 2;
                } else if (MegaScreenManager.SCREENID_PASSENGER == displayId) {
                    screen = 1;
                }

                if (newState == MegaScreenManager.STATE_ON) {
                    windowMessageManager.setScreenEnable(screen, true);
                    UIMgr.INSTANCE.onScreenStateChange(screen, true);
                } else if (newState == MegaScreenManager.STATE_OFF) {
                    UIMgr.INSTANCE.onScreenStateChange(screen, false);
                    windowMessageManager.setScreenEnable(screen, false);
                }
            }
        }
    };


    public static UiInterfaceImpl getInstance() {
        return uiImpl;
    }

    @Override
    public void init() {
        initHandler();
        windowMessageManager = WindowMessageManager.getInstance();
        windowMessageManager.setMessageCallback(new WindowMessageManager.WindowMessageCallback() {
            private final Set<String> llmDomains = new HashSet<String>() {
                {
                    add(DomainType.DOMAIN_TYPE_FAQ);
                    add(DomainType.DOMAIN_TYPE_GOSSIP);
                    add(DomainType.DOMAIN_TYPE_CAR_ENCYCLOPEDIA);
                    add(DomainType.DOMAIN_TYPE_ENCYCLOPEDIA);
                }
            };
            @Override
            public void onServiceBind() {
                //绑定UI服务的时候设置当前状态，后续客户端状态有修改，都会回调给UI，剩下的UI接口不需要再传递这两个参数
                int privacySecurityStatus = DeviceHolder.INS().getDevices().getLauncher().getPrivacySecurity();
                windowMessageManager.setVoiceMode(privacySecurityStatus == 1 ? VoiceMode.VOICE_MODE_OFFLINE : VoiceMode.VOICE_MODE_ONLINE);
                String langType = SettingsManager.get().getCurrentDialect().asr;
                setLanguageType(langType);

                handleScreenStates();
            }

            @Override
            public void onBinderDied() {
                ScreenStateHelper.INSTANCE.removeListener(ScreenStateHelper.SCREENID_CEILING, listener);
            }

            @Override
            public void onServiceDisconnected() {

            }

            @Override
            public void onReceiveWindowMessage(WindowMessage msg) {
                if (msg != null) {
                    switch (msg.getName()) {
                        case WindowType.WINDOW_TYPE_VPA_TYPEWRITER_CARD:
                            String msgAction = msg.getAction();
                            int screenType = msg.getScreenType();
                            Log.i(TAG, "onReceiveWindowMessage: screenType:" + screenType);
                            handleVTCWindowMessage(msgAction);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onReceiveVoyahWindowMessage(@NonNull String msgJson) {
                LogUtils.d(TAG, "onReceiveVoyahWindowMessage msgJson:" + msgJson);
                Gson gson = new Gson();
                CardInfo cardInfo = gson.fromJson(msgJson, CardInfo.class);
                int itemType = cardInfo.getItemType();
                int position = cardInfo.getPosition();
                String reqId = cardInfo.getSessionId();
                String domainType = cardInfo.getDomainType();
                String scene = cardInfo.getAction();
                int screenType = cardInfo.getScreenType();
                LogUtils.d(TAG, "onReceiveVoyahWindowMessage itemType:" + itemType + " ,position is "
                        + position + " ,scene is " + scene + " screenType:" + screenType);

                switch (scene) {
                    case "State.CALL_OUT_CONFIRM":
                        VoiceImpl.getInstance().confirm("确定");
                        break;
                    case WindowAction.WINDOW_ACTION_ITEM_CLICK:
                    case "State.CALL_CONTACT_NUMBER_OPTIONS":
                        handleItemClick(itemType, position, domainType, screenType);
                        break;
                    case WindowAction.WINDOW_ACTION_COLLAPSE_CARD:
                        handleCardCollapse(cardInfo, domainType);
                        break;
                    case WindowAction.WINDOW_ACTION_INTERACTION_IN_CARD:
                        //卡片中发生交互，通过可见指令或者触控
                        UIMgr.INSTANCE.onInteractionInCard(reqId, llmDomains.contains(domainType));
                        break;
                }

            }

            //滑动结果回调
            @Override
            public void onCardScroll(@NonNull String cardType, int direction, boolean canScroll) {
                LogUtils.i(TAG, "cardType is " + cardType + " ,direction is " + direction + " , canScroll is " + canScroll);
            }
        });
        windowMessageManager.init(Utils.getApp());

    }

    private void initHandler() {
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case CHANGE_INFORMATION_STATE:
                        closeCard();
                        LogUtils.d(TAG, "uiChange closeCard");
                        break;
                }
            }
        };
    }

    @Override
    public void showCard(int type, Object data, int location) {
        Gson gson = new Gson();
        CardInfo cardInfo = new CardInfo();
        if (type == UiConstant.CardType.PHONE_CARD) {
            String scene = (String) ((HashMap<String, Object>) data).get("scene");
            String sessionId = (String) ((HashMap<String, Object>) data).get("sessionId");
            String requestId = (String) ((HashMap<String, Object>) data).get("requestId");
            List list = (List) ((HashMap<String, Object>) data).get("numberList");
            LogUtils.i(TAG, "scene is " + scene + " ,list.size is " + list.size());
            DeviceHolder.INS().getDevices().getPhone().setCurrentInfoList(list);
            List<Contact> contacts = new ArrayList<>();
            int count = 0;
            for (Object item : list) {
//                //联系人列表
//                if (data instanceof ContactInfo) {
//                    List<ContactNumberInfo> contactNumberInfoList = ((ContactInfo) data).getNumberInfoList();
//                    for (ContactNumberInfo contactNumberInfo : contactNumberInfoList) {
//                        Contact contact = new Contact();
//                        contact.setContactName(((ContactInfo) data).getName());
//                        contact.setNumber(contactNumberInfo.getNumber());
//                        contact.setPhoneType(contactNumberInfo.getType());
//                        contacts.add(contact);
//                    }
//                } else
                if (item instanceof ContactNumberInfo) {

                    if (count >= 32) {
                        LogUtils.d(TAG, "showCard displayed number exceed...");
                        break;
                    }
                    //号码列表
                    Contact contact = new Contact();
                    contact.setContactName(((ContactNumberInfo) item).getName());
                    contact.setNumber(((ContactNumberInfo) item).getNumber());
                    contact.setPhoneType(((ContactNumberInfo) item).getType());
                    contact.setItemType(ViewType.BT_PHONE_TYPE);
                    contacts.add(contact);
                    count++;
                }
            }
            BTPhoneInfo btPhoneInfo = new BTPhoneInfo();
            btPhoneInfo.setContacts(contacts);
            cardInfo.setDomainType(DomainType.DOMAIN_TYPE_BT_PHONE);
            cardInfo.setAction(scene);
            cardInfo.setSessionId(sessionId);
            cardInfo.setRequestId(requestId);
            cardInfo.setBtPhoneInfo(btPhoneInfo);
        } else if (type == UiConstant.CardType.MEDIA_CARD) {
            String scene = (String) ((HashMap<String, Object>) data).get("scene");
            String sessionId = (String) ((HashMap<String, Object>) data).get("sessionId");
            String requestId = (String) ((HashMap<String, Object>) data).get("requestId");
            List<MultimediaInfo> list = (List<MultimediaInfo>) ((HashMap<String, Object>) data).get("videoList");
            cardInfo.setDomainType(DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO);
            cardInfo.setAction(scene);
            cardInfo.setSessionId(sessionId);
            cardInfo.setRequestId(requestId);
            cardInfo.setMultimediaInfos(list);
        } else if (type == UiConstant.CardType.MUSIC_CARD) {
            String scene = (String) ((HashMap<String, Object>) data).get("scene");
            String sessionId = (String) ((HashMap<String, Object>) data).get("sessionId");
            String requestId = (String) ((HashMap<String, Object>) data).get("requestId");
            List<MultiMusicInfo> list = (List<MultiMusicInfo>) ((HashMap<String, Object>) data).get("musicList");
            cardInfo.setDomainType(DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC);
            cardInfo.setAction(scene);
            cardInfo.setSessionId(sessionId);
            cardInfo.setRequestId(requestId);
            cardInfo.setMultiMusicInfos(list);
        } else if (type == UiConstant.CardType.WEATHER_CARD) {
            if (data instanceof CPEntity) {
                CPEntity cpEntity = (CPEntity) data;
                IData cpData = cpEntity.getData();
                if (cpData instanceof OneDayWeather) {
                    OneDayWeather oneDayWeather = (OneDayWeather) cpData;
                    cardInfo.setDomainType(DomainType.DOMAIN_TYPE_WEATHER);
                    String requestId = cpEntity.getRequestId();
                    cardInfo.setSessionId(requestId);
                    cardInfo.setRequestId(requestId);

                    List<Weather> weathers = new ArrayList<>();
                    Weather weather = new Weather();
                    weather.setLocation(oneDayWeather.location);
                    weather.setFormatDate(oneDayWeather.date);
                    weather.setTempLow(oneDayWeather.tempLow);
                    weather.setTempHigh(oneDayWeather.tempHigh);
                    weather.setItemType(ViewType.WEATHER_TYPE_1);
                    weather.setWeatherDay(oneDayWeather.weatherDay);
                    weather.setWeatherNight(oneDayWeather.weatherNight);
                    weather.setWindDirDay(oneDayWeather.windDirDay);
                    weather.setWindDirNight(oneDayWeather.windDirNight);
                    weather.setWindLevelDay(oneDayWeather.windLevelDay);
                    weather.setWindLevelNight(oneDayWeather.windLevelNight);
                    weathers.add(weather);
                    cardInfo.setWeathers(weathers);
                } else if (cpData instanceof MultiDaysWeather) {
                    MultiDaysWeather multiDaysWeather = (MultiDaysWeather) cpData;
                    cardInfo.setDomainType(DomainType.DOMAIN_TYPE_WEATHER);
                    String requestId = cpEntity.getRequestId();
                    cardInfo.setSessionId(requestId);
                    cardInfo.setRequestId(requestId);

                    List<MultiDaysWeather.DayWeather> weathersList = multiDaysWeather.dayWeathersList;
                    if (weathersList == null || weathersList.isEmpty()) {
                        LogUtils.w(TAG, "showCard, data range weather query response invalid...");
                        return;
                    }

                    List<Weather> weathers = new ArrayList<>();
                    Weather weather = new Weather();
                    weather.setLocation(multiDaysWeather.location);
                    weather.setTempLow(multiDaysWeather.tempLow);
                    weather.setTempHigh(multiDaysWeather.tempHigh);
                    weather.setItemType(ViewType.WEATHER_TYPE_2);
                    weathers.add(weather);

                    for (MultiDaysWeather.DayWeather dayWeather : weathersList) {
                        weather = new Weather();
                        weather.setFormatDate(dayWeather.date);
                        weather.setTempLow(dayWeather.tempLow);
                        weather.setTempHigh(dayWeather.tempHigh);
                        weather.setTempHighDateRange(multiDaysWeather.temp15High);
                        weather.setTempLowDateRange(multiDaysWeather.temp15Low);
                        weather.setItemType(ViewType.WEATHER_TYPE_3);
                        weather.setWeatherDay(dayWeather.weatherDay);
                        weather.setWeatherNight(dayWeather.weatherNight);
                        weathers.add(weather);
                    }
                    cardInfo.setWeathers(weathers);
                }
            }

        } else if (type == UiConstant.CardType.SCHEDULE_CARD) {
            cardInfo = (CardInfo) data;
        } else if (type == UiConstant.CardType.STOCK_CARD) {
            cardInfo = (CardInfo) data;
        } else if (type == UiConstant.CardType.LLM_CARD) {
            cardInfo = (CardInfo) data;
        }

        cardInfo.setScreenType(location);
        String cardJson = gson.toJson(cardInfo);
        LogUtils.d(TAG, "showCard, cardJson:" + cardJson);
        WindowMessageManager.getInstance().showCard(cardJson);
    }

    @Override
    public void closeCard() {
        LogUtils.d(TAG, "dismissCard.");
        WindowMessageManager.getInstance().dismissCard(ScreenType.ALL);
    }

    @Override
    public void setTypeWriterText(String text, int typeTextStyle) {

    }

    /**
     * @param direction 0:下滑(上一页) 1:上滑(下一页)
     * @return
     */
    public int scrollCard(int direction, int screenType) {
        int scrollRe = 0;
        boolean isTerminus = WindowMessageManager
                .getInstance()
                .scrollCardView(direction, screenType);
        LogUtils.i(TAG, "scrollCard direction is " + direction + " ,isTerminus is " + isTerminus);
        if (!isTerminus) {
            if (direction == CardDirection.DIRECTION_UP)
                scrollRe = 2;
            else if (direction == CardDirection.DIRECTION_DOWN)
                scrollRe = 1;
        }
        //1：上一页到顶 2：下一页到底
        return scrollRe;
    }

    /**
     * 翻页
     *
     * @param direction 负数向上翻页 正数向下翻页
     * @return 1:到顶 2:到底
     */
    public int scrollPage(int direction, int screenType) {
        int scrollRe = 0;
        // todo 成远 需要指定屏幕
        boolean isTerminus = WindowMessageManager.getInstance().nextPage(direction, screenType);
        LogUtils.i(TAG, "scrollPage direction is " + direction + " ,isTerminus is " + isTerminus);
        if (!isTerminus && direction < 0)
            scrollRe = 1;
        else if (!isTerminus && direction > 0)
            scrollRe = 2;
        return scrollRe;
    }

    //跳转指定页面
    @Override
    public void scrollAssignPage(int indexPage, int screenType) {
        LogUtils.i(TAG, "scrollAssignPage indexPage is " + indexPage);
        WindowMessageManager.getInstance().setCurrentPage(indexPage, screenType);
    }

    @Override
    public int getCurrentPage(int screenType) {
        int currentPage = -1;
        PageInfo pageInfo = WindowMessageManager.getInstance().getCurrentPage(screenType);
        if (null != pageInfo)
            currentPage = pageInfo.getPosition() + 1;
        return currentPage;
    }

    public int getMaxItemCount(int screenType) {
        int maxItemCount = -1;
        PageInfo pageInfo = WindowMessageManager.getInstance().getCurrentPage(screenType);
        if (null != pageInfo)
            maxItemCount = pageInfo.getMaxItemCount();
        return maxItemCount;
    }

    @Override
    public void setVoiceMode(int voiceMode) {
        LogUtils.d(TAG, "setVoiceMode voiceMode:" + voiceMode);
        WindowMessageManager.getInstance().setVoiceMode(voiceMode);
    }

    @Override
    public void setLanguageType(int languageType) {
        LogUtils.d(TAG, "setLanguageType languageType:" + languageType);
        WindowMessageManager.getInstance().setLanguageType(languageType);
    }

    @Override
    public void setLanguageType(String languageType) {
        LogUtils.d(TAG, "setLanguageType languageType:" + languageType);
        int lagType = LanguageType.MANDARIN;
        if (StringUtils.equals(languageType, DhDialect.ID_CANTONESE))
            lagType = LanguageType.CANTONESE;
        else if (StringUtils.equals(languageType, DhDialect.ID_SICHUAN))
            lagType = LanguageType.SICHUANESE;
        setLanguageType(lagType);
    }

    //信息类卡片交互时延时关闭卡片处理
    @Override
    public void delayInformationCardCountDown(int type) {
        isGestureInformation = 1 == type;
        boolean isCardShowAndRegisterViewCmd = isCardShowAndRegisterViewCmd(0);
        boolean isInTtsPlay = VoiceStateRecordManager.getInstance().isTtsPlay();
        LogUtils.d(TAG, "delayInformationCardCountDown:isCardShowAndRegisterViewCmd:" + isCardShowAndRegisterViewCmd + " ,isInTtsPlay:" + isInTtsPlay + " ,type:" + type);
        if (isCardShowAndRegisterViewCmd && !isInTtsPlay)
            startInformationCardCountDown(10000);
    }

    /**
     * 返回结果
     * // 返回值为APIResult.CARD_REGISTER_VIEW_CMD表示卡片展开且注册了可见
     * // 返回值为APIResult.CARD_NOT_REGISTER_VIEW_CMD表示卡片展开未注册可见
     * // 返回值为APIResult.CARD_COLLAPSED表示卡片未展开
     * // 返回值为APIResult.NO_RESOURCE表示VoiceUI的逻辑出错。
     * // 返回值为APIResult.INIT表示VoiceUI线程切换出现了异常
     * // 返回值为APIResult.ERROR表示VoiceUI处理过程中发生了线程打断异常
     *
     * @param displayId 指定屏幕位置 目前默认传0
     * @return
     */
    @SuppressLint("WrongConstant")
    @Override
    public boolean isCardShowAndRegisterViewCmd(int displayId) {
//        int state = WindowMessageManager
//                .getInstance()
//                .getCardViewRegisterViewCmdState(0, ScreenType.MAIN);
//        LogUtils.d(TAG, "isCardShowAndRegisterViewCmd state:" + state);
//        return state == APIResult.CARD_REGISTER_VIEW_CMD;
        return false;
    }

    @Override
    public boolean isInFormationCardShow() {
        return false;
    }

    public int getCardState(int screenType) {
        int cardState = WindowMessageManager.getInstance().getCardState(screenType);
        LogUtils.d(TAG, "getCardState cardState:" + cardState);
        return cardState;
    }


    //模型卡片
    public void startInformationCardCountDown(long delayTime) {
        if (null != handler) {
            LogUtils.i(TAG, "InformationCountDow card startCountDown delayTime is " + delayTime);
            handler.removeMessages(CHANGE_INFORMATION_STATE);
            handler.sendEmptyMessageDelayed(CHANGE_INFORMATION_STATE, delayTime);
        } else {
            LogUtils.d(TAG, "handler is null");
        }
    }

    @Override
    public void removeCountDown() {
        if (null != handler) {
            handler.removeMessages(CHANGE_STATE_LISTENING);
//            handler.removeMessages(CHANGE_INFORMATION_STATE_LISTENING);
        } else {
            LogUtils.d(TAG, "handler is null");
        }
    }

    @Override
    public void removeInformationCountDown() {
        LogUtils.d(TAG, "InformationCountDow removeCountDown");
        if (null != handler) {
            handler.removeMessages(CHANGE_INFORMATION_STATE_LISTENING);
        } else {
            LogUtils.d(TAG, "handler is null");
        }
    }

    @Override
    public void removeInformationCardCountDown() {
        LogUtils.d(TAG, "removeInformationCardCountDown removeCountDown");
        if (null != handler) {
            handler.removeMessages(CHANGE_INFORMATION_STATE);
        } else {
            LogUtils.d(TAG, "handler is null");
        }
    }


    @Override
    public void setUiStateListener(IUiStateListener listener) {
        this.iUiStateListener = listener;
    }

    @Override
    public void addCardStateListener(UICardListener listener) {
        if (mUIStateListeners == null) {
            mUIStateListeners = new ArrayList<>();
        }

        if (!mUIStateListeners.contains(listener)) {
            mUIStateListeners.add(listener);
        }
    }

    @Override
    public void removeCardStateListener(UICardListener listener) {
        if (mUIStateListeners != null) {
            mUIStateListeners.remove(listener);
        }
    }

    /**
     * 处理 voice_ui 应用发送过来的vap-typewriter-card window的消息
     *
     * @param msgAction
     */
    private void handleVTCWindowMessage(String msgAction) {
        switch (msgAction) {
            //遗弃
            case WindowAction.WINDOW_ACTION_COLLAPSE_CARD:
                iUiStateListener.uiCardClose("");
                LogUtils.d(TAG, "handleVTCWindowMessage: collapse window.");
                break;
            case WindowAction.WINDOW_ACTION_DISMISS:
                LogUtils.d(TAG, "handleVTCWindowMessage: dismiss window.");
                UIMgr.INSTANCE.forceExitAll("voice ui window dismiss");
                iUiStateListener.uiVpaClose();
                break;
        }
    }

    private void handleItemClick(int itemType, int position, String domainType, int screenType) {
        switch (domainType) {
            case DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO:
                if (itemType == ViewType.MEDIA_TYPE) {
                    if (mUIStateListeners != null) {
                        for (UICardListener listener : mUIStateListeners) {
                            listener.onCardItemClick(position, itemType, screenType);
                        }
                    }
                }
                break;
            case DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC:
                if (itemType == ViewType.MUSIC_TYPE) {
                    if (mUIStateListeners != null) {
                        for (UICardListener listener : mUIStateListeners) {
                            listener.onCardItemClick(position, itemType, screenType);
                        }
                    }
                }
                break;
            case DomainType.DOMAIN_TYPE_SCHEDULE:
                if (itemType == ViewType.SCHEDULE_TYPE_MORE) {
                    if (mUIStateListeners != null) {
                        for (UICardListener listener : mUIStateListeners) {
                            listener.onCardItemClick(position, itemType, screenType);
                        }
                    }
                }
            case DomainType.DOMAIN_TYPE_BT_PHONE:
                if (itemType == ViewType.BT_PHONE_TYPE) {
                    if (mUIStateListeners != null) {
                        for (UICardListener listener : mUIStateListeners) {
                            listener.onCardItemClick(position, itemType, screenType);
                        }
                    }
                }
                break;
        }
    }

    private void handleCardCollapse(CardInfo cardInfo, String domainType) {
        //UI通知卡片折叠，VPA需要退出执行态
        UIMgr.INSTANCE.forceExitAct("ui: card collapse", 0);
        switch (domainType) {
            case DomainType.DOMAIN_TYPE_GOSSIP:
            case DomainType.DOMAIN_TYPE_FAQ:
            case DomainType.DOMAIN_TYPE_CAR_ENCYCLOPEDIA:
            case DomainType.DOMAIN_TYPE_ENCYCLOPEDIA:
                String requestId = cardInfo.getSessionId();
                LogUtils.d(TAG, "isChatCard handleCardCollapse requestId:"
                        + requestId);
                if (!TextUtils.isEmpty(requestId)) {
                    if (iUiStateListener != null) {
                        iUiStateListener.uiModelCardClose(requestId);
                    }

                }
                break;
            case DomainType.DOMAIN_TYPE_FAQ_NOT_STREAM:
            case DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM:
            case DomainType.DOMAIN_TYPE_BT_PHONE:
            case DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO:
            case DomainType.DOMAIN_TYPE_WEATHER:
            case DomainType.DOMAIN_TYPE_STOCK:
            case DomainType.DOMAIN_TYPE_SCHEDULE:
            case DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC:
                String sessionId = cardInfo.getSessionId();
                LogUtils.d(TAG, "other card onReceiveVoyahWindowMessage sessionId:"
                        + sessionId);
                if (iUiStateListener != null) {
                    iUiStateListener.uiCardClose(sessionId);
                }
                break;
        }
    }

    /************************************ UI状态管理 START************************************/

    private void handleScreenStates() {
        ScreenInterface screen = DeviceHolder.INS().getDevices().getSystem().getScreen();
        //通知UI 吸顶屏的状态
        boolean ceilScreenState = screen.isScreenOn(ScreenStateHelper.SCREENID_CEILING);
        //吸顶屏配置
        boolean hasCeilingScreen = screen.isSupportScreen(DeviceScreenType.CEIL_SCREEN);
        LogUtils.d(TAG, "ceiling screen state:" + ceilScreenState + ",hasCeilingScreen:" + hasCeilingScreen);
        windowMessageManager.setCeilingScreenEnable(hasCeilingScreen && ceilScreenState);
        UIMgr.INSTANCE.onScreenStateChange(2, hasCeilingScreen && ceilScreenState);
        ScreenStateHelper.INSTANCE.addListener(ScreenStateHelper.SCREENID_CEILING, listener);
        ScreenStateHelper.INSTANCE.addListener(ScreenStateHelper.SCREENID_PASSENGER, listener);
    }

    @Override
    public void onChangeCardOwner(int cardType, @NonNull Object cardData, @NonNull String business, int location, @NonNull Runnable callback) {
        if (UiConstant.CardType.LLM_CARD == cardType) {
            List<ChatMessage> messages = ((CardInfo) cardData).getChatMessages();
            ChatMessage message = messages.get(messages.size() - 1); //获取最新一帧
            if (StreamMode.START == message.getStreamMode() || StreamMode.NOT_STREAM == message.getStreamMode()) {
                callback.run();
            }
        } else {
            callback.run();
        }
    }

    @Override
    public void showVoiceVpa(@NonNull String text, int textStyle, @NonNull String voiceState, int location) {
        UIMessage uiMessage = new UIMessage();
        uiMessage.setTextTypewriter(text);
        uiMessage.setTextStyle(textStyle);
        uiMessage.setVoiceState(voiceState);
        uiMessage.setScreenType(location);
        windowMessageManager.showVoiceVpa(uiMessage);
    }

    @Override
    public void dismissVoiceView(int location) {
        LogUtils.d("UIState", "dismissVoiceView location:" + location);
        windowMessageManager.dismissVoiceView(location);
    }

    @Override
    public void showWave(int location) {
        windowMessageManager.showWave(location);
    }

    @Override
    public void dismissWave() {
        windowMessageManager.dismissWave();
    }

    @Override
    public void dismissCard(int location) {
        windowMessageManager.dismissCard(location);
    }

    @Override
    public void onVoiceExit() {
        windowMessageManager.onVoiceExit();
    }

    /************************************ UI状态管理 END ************************************/
}
