package com.voyah.window.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.mega.nexus.os.MegaSystemProperties;
import com.voyah.cockpit.window.WindowMessageManager;
import com.voyah.cockpit.window.model.CardDirection;
import com.voyah.cockpit.window.model.CardInfo;
import com.voyah.cockpit.window.model.ChatMessage;
import com.voyah.cockpit.window.model.Contact;
import com.voyah.cockpit.window.model.BTPhoneInfo;
import com.voyah.cockpit.window.model.DomainType;
import com.voyah.cockpit.window.model.ExecuteFeedbackInfo;
import com.voyah.cockpit.window.model.LanguageType;
import com.voyah.cockpit.window.model.MultiMusicInfo;
import com.voyah.cockpit.window.model.MultimediaInfo;
import com.voyah.cockpit.window.model.PageInfo;
import com.voyah.cockpit.window.model.ScheduleInfo;
import com.voyah.cockpit.window.model.ScreenType;
import com.voyah.cockpit.window.model.StockInfo;
import com.voyah.cockpit.window.model.StreamMode;
import com.voyah.cockpit.window.model.TypeTextStyle;
import com.voyah.cockpit.window.model.UIMessage;
import com.voyah.cockpit.window.model.ViewType;
import com.voyah.cockpit.window.model.VoiceLocation;
import com.voyah.cockpit.window.model.VoiceMode;
import com.voyah.cockpit.window.model.VoiceState;
import com.voyah.cockpit.window.model.Weather;
import com.voyah.cockpit.window.util.DateUtil;
import com.voyah.voice.common.manager.MegaDisplayHelper;
import com.voyah.window.BuildConfig;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author:lcy
 * @data:2024/2/19
 **/
public class WindowBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WindowBroadcastReceiver";
    private String gossipContent = "南昌有很多好玩的地方哟，以下是一些推荐：\n" +
            "- **滕王阁**：江南三大名楼之一，因王勃的《滕王阁序》闻名，可俯瞰赣江美景，夜景也很美。\n" +
            "  ![滕王阁](https://p3-search.byteimg.com/obj/labis/f4c9467e92248f9eb014ae6fda03f871)\n" +
            "\n" +
            "- **南昌八一起义纪念馆**：国家4A级景区，是宣传人民军队诞生发展的“中国军史第一馆”，免费开放。\n" +
            "- **南昌汉代海昏侯国遗址**：我国发现面积最大、保存最好、内涵最丰富的汉代侯国都城聚落遗址。\n" +
            "- **南昌之星摩天轮**：中国第三高摩天轮，乘坐可远观南昌建筑群与赣江美景。\n" +
            "  ![南昌之星摩天轮](https://p3-search.byteimg.com/obj/labis/69b53dba084a54492a8e356b24c086ab)\n" +
            "\n" +
            "- **万寿宫历史文化街区**：集时尚购物、餐饮娱乐等为一体，能感受南昌夜生活魅力。";

    private volatile Gson mGson;

    public WindowBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.d(TAG, "onReceive: action:" + intent.getAction());
            if (!BuildConfig.DEBUG) return;
            if (TextUtils.equals(intent.getAction(), "com.voyah.window.test.query")) {
                String queryString = intent.getStringExtra("query");
                Log.i(TAG, "queryString is " + queryString);
                if (TextUtils.equals(queryString, "voiceFL")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            WindowMessageManager.getInstance().showWave(VoiceLocation.FRONT_LEFT);
                            UIMessage uiMessage = new UIMessage();
                            uiMessage.setScreenType(ScreenType.MAIN);
                            uiMessage.setVoiceState(VoiceState.VOICE_STATE_RECOGNIZE);
                            uiMessage.setTextTypewriter("我是卖报的小行家");
                            uiMessage.setTextStyle(TypeTextStyle.SECONDARY);
                            WindowMessageManager.getInstance().showVoiceVpa(uiMessage);
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "voiceFR")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            WindowMessageManager.getInstance().showWave(VoiceLocation.FRONT_RIGHT);
                            UIMessage uiMessage = new UIMessage();
                            uiMessage.setScreenType(VoiceLocation.FRONT_RIGHT);
                            uiMessage.setVoiceState(VoiceState.VOICE_STATE_LISTENING);
                            WindowMessageManager.getInstance().showVoiceVpa(uiMessage);
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "voiceRL")) {
                    WindowMessageManager.getInstance().showWave(VoiceLocation.REAR_LEFT);
                    UIMessage uiMessage = new UIMessage();
                    uiMessage.setScreenType(ScreenType.MAIN);
                    uiMessage.setVoiceState(VoiceState.VOICE_STATE_LISTENING);
                    WindowMessageManager.getInstance().showVoiceVpa(uiMessage);
                } else if (TextUtils.equals(queryString, "voiceRR")) {
                    WindowMessageManager.getInstance().showWave(VoiceLocation.REAR_RIGHT);
                    UIMessage uiMessage = new UIMessage();
                    uiMessage.setScreenType(ScreenType.MAIN);
                    uiMessage.setVoiceState(VoiceState.VOICE_STATE_LISTENING);
                    WindowMessageManager.getInstance().showVoiceVpa(uiMessage);
                } else if (TextUtils.equals(queryString, "ceilingEnableTrue")) {
                    WindowMessageManager.getInstance().setCeilingScreenEnable(true);
                } else if (TextUtils.equals(queryString, "ceilingEnableFalse")) {
                    WindowMessageManager.getInstance().setCeilingScreenEnable(true);
                } else if (TextUtils.equals(queryString, "showVoiceView")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            WindowMessageManager.getInstance().showVoiceView(
                                    VoiceMode.VOICE_MODE_OFFLINE,
                                    LanguageType.MANDARIN,
                                    VoiceLocation.REAR_RIGHT);
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                }
                else if (TextUtils.equals(queryString, "voiceModeOnline")) {
                    WindowMessageManager.getInstance().setVoiceMode(VoiceMode.VOICE_MODE_ONLINE);
                } else if (TextUtils.equals(queryString, "voiceModeOffline")) {
                    WindowMessageManager.getInstance().setVoiceMode(VoiceMode.VOICE_MODE_OFFLINE);
                } else if (TextUtils.equals(queryString, "languageGuoYu")) {
                    WindowMessageManager.getInstance().setLanguageType(LanguageType.MANDARIN);
                }
                else if (TextUtils.equals(queryString, "languageSiChuan")) {
                    WindowMessageManager.getInstance().setLanguageType(LanguageType.SICHUANESE);
                } else if (TextUtils.equals(queryString, "languageYueYu")) {
                    WindowMessageManager.getInstance().setLanguageType(LanguageType.CANTONESE);
                }

                else if (TextUtils.equals(queryString, "voiceStateListening")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            LogUtils.d("on voice listening doInBackground");
                            WindowMessageManager.getInstance().onVoiceListening();
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "voiceStateSpeaking")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            LogUtils.d("on voice speaking doInBackground");
                            WindowMessageManager.getInstance().onVoiceSpeaking();
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "voiceStateExit")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            LogUtils.d("on voice exit doInBackground");
                            WindowMessageManager.getInstance().onVoiceExit();
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "voiceStateExitThenAwake")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            LogUtils.d("on voice exit doInBackground");
                            WindowMessageManager.getInstance().onVoiceExit();
                            Thread.sleep(10L);
                            WindowMessageManager.getInstance().onVoiceAwake(
                                    VoiceMode.VOICE_MODE_OFFLINE,
                                    LanguageType.MANDARIN,
                                    VoiceLocation.REAR_RIGHT
                            );
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                }
                else if (TextUtils.equals(queryString, "expandCardContactMain")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            executeExpandContactsCardTask(ScreenType.MAIN);
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "expandCardContactPassenger")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            executeExpandContactsCardTask(ScreenType.PASSENGER);
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                }
                else if (TextUtils.equals(queryString, "expandCardWeatherDay")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            executeWeatherDayTask();
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "expandCardWeatherDayThenDismiss")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            executeWeatherDayTask();
                            Thread.sleep(500L);
                            WindowMessageManager.getInstance().dismissCard(ScreenType.ALL);
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });

                }
                else if (TextUtils.equals(queryString, "expandCardWeatherDayRange")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            executeWeatherDayRangeTask();
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "expandCardMedia")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            executeExpandMediaCardTask();
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "expandCardMusic")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            executeExpandMusicCardTask();
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "expandCardStock")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            executeExpandStockCardTask();
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "expandCardSchedule1")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            executeExpandScheduleCard1();
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "expandCardBaiKeShort")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            executeExpandBaiKeCardTask(false);
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "expandCardBaiKeLong")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            executeExpandBaiKeCardTask(true);
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "expandCardGossip")) {
                    ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
//                            WindowMessageManager.getInstance().dismissCard();
//                            Thread.sleep(10L);
                            executeExpandGossipCardTask();
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });

                } else if (TextUtils.equals(queryString, "expandCardGossip2")) {
                    ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
//                            WindowMessageManager.getInstance().dismissCard();
//                            Thread.sleep(10L);
                            executeExpandGossipCardTask2();
//                            executeExpandGossipCardTask3();
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });

                }
                else if (TextUtils.equals(queryString, "nextPage")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            boolean result = WindowMessageManager.getInstance().nextPage(
                                    1, ScreenType.MAIN);
                            LogUtils.d("nextPage doInBackground result:" + result);
                            return result;
                        }

                        @Override
                        public void onSuccess(Boolean result) {
                            LogUtils.d("nextPage onSuccess result:" + result);
                        }
                    });

                } else if (TextUtils.equals(queryString, "previousPage")) {
                    boolean result = WindowMessageManager.getInstance().nextPage(
                            -1, ScreenType.MAIN);
                    LogUtils.d("previousPage result:" + result);
                } else if (TextUtils.equals(queryString, "getCurrentPage")) {
                    PageInfo currentPage = WindowMessageManager
                            .getInstance()
                            .getCurrentPage(ScreenType.MAIN);
                    LogUtils.d("getCurrentPage currentPage:" + currentPage);
                } else if (TextUtils.equals(queryString, "expandCardAndVoiceAwake")) {
                    new Thread(() -> {
                        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {

                            @Override
                            public Boolean doInBackground() throws Throwable {
                                LogUtils.d("set voice awake...");
                                WindowMessageManager.getInstance().onVoiceAwake(
                                        VoiceMode.VOICE_MODE_OFFLINE,
                                        LanguageType.MANDARIN,
                                        VoiceLocation.FRONT_LEFT);
                                return true;
                            }

                            @Override
                            public void onSuccess(Boolean result) {

                            }
                        });
                        try {
                            Thread.sleep(10L);
                            ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                                @Override
                                public Boolean doInBackground() throws Throwable {
                                    LogUtils.d("expand card ...");
                                    executeExpandContactsCardTask(ScreenType.MAIN);
                                    return true;
                                }

                                @Override
                                public void onSuccess(Boolean result) {

                                }
                            });
                        } catch (InterruptedException e) {
                            LogUtils.w("expandCardAndVoiceAwake, e:$e");
                        }
                    }).start();

                } else if (TextUtils.equals(queryString, "dismissVoiceShowCard")) {
                    new Thread(() -> {
                        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {

                            @Override
                            public Boolean doInBackground() throws Throwable {
                                LogUtils.d("expand card ...");
                                executeExpandContactsCardTask(ScreenType.MAIN);
                                return true;
                            }

                            @Override
                            public void onSuccess(Boolean result) {

                            }
                        });
                        try {
                            Thread.sleep(0L);
                            ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                                @Override
                                public Boolean doInBackground() throws Throwable {
                                    LogUtils.d("dismiss voice view...");
                                    WindowMessageManager
                                            .getInstance()
                                            .dismissVoiceView(ScreenType.MAIN);
                                    return true;
                                }

                                @Override
                                public void onSuccess(Boolean result) {

                                }
                            });
                        } catch (InterruptedException e) {
                            LogUtils.w("expandCardAndVoiceAwake, e:$e");
                        }
                    }).start();

                } else if (TextUtils.equals(queryString, "voiceAwakeAndInputAsr")) {
                    new Thread(() -> {
                        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {

                            @Override
                            public Boolean doInBackground() throws Throwable {
                                LogUtils.d("set voice awake...");
                                WindowMessageManager.getInstance().onVoiceAwake(
                                        VoiceMode.VOICE_MODE_OFFLINE,
                                        LanguageType.MANDARIN,
                                        VoiceLocation.FRONT_LEFT);
                                return true;
                            }

                            @Override
                            public void onSuccess(Boolean result) {

                            }
                        });
                        try {
                            Thread.sleep(10L);
                            ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                                @Override
                                public Boolean doInBackground() throws Throwable {
                                    String text = "仲夏午后，乌云铺天而来，大唐岭南道桂州的治所临桂县（今桂林市）陷入一片晦暗。";
                                    WindowMessageManager.getInstance().inputTypewriterText(text, TypeTextStyle.PRIMARY);
                                    return true;
                                }

                                @Override
                                public void onSuccess(Boolean result) {

                                }
                            });
                        } catch (InterruptedException e) {
                            LogUtils.w("voice awake and input asr, error:$e");
                        }

                        try {
                            Thread.sleep(5L);
                            ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                                @Override
                                public Boolean doInBackground() throws Throwable {
                                    executeWeatherDayTask();
                                    return true;
                                }

                                @Override
                                public void onSuccess(Boolean result) {

                                }
                            });
                        } catch (InterruptedException e) {
                            LogUtils.w("voice awake and input asr, error:$e");
                        }
                    }).start();
                }

                else if (TextUtils.equals(queryString, "getCardType")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {

                        @Override
                        public Boolean doInBackground() throws Throwable {
                            LogUtils.d("get card type");
                            WindowMessageManager.getInstance().getCurrentCardType(ScreenType.MAIN);
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "getCardState")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {

                        @Override
                        public Boolean doInBackground() throws Throwable {
                            LogUtils.d("get card state ....");
                            WindowMessageManager.getInstance().getCardState(ScreenType.MAIN);
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                }

                else if (TextUtils.equals(queryString, "refreshCard")) {
                    executeRefreshContactCardTask();
                } else if (TextUtils.equals(queryString, "passengerScreenOn")) {
                    WindowMessageManager.getInstance().setScreenEnable(1, true);
                } else if (TextUtils.equals(queryString, "passengerScreenOff")) {
                    WindowMessageManager.getInstance().setScreenEnable(1, false);
                } else if (TextUtils.equals(queryString, "ceilScreenOn")) {
                    WindowMessageManager.getInstance().setScreenEnable(2, true);
                } else if (TextUtils.equals(queryString, "ceilScreenOff")) {
                    WindowMessageManager.getInstance().setScreenEnable(2, false);
                } else if (TextUtils.equals(queryString, "expandCardSchedule2")) {
                    Gson gson = new Gson();

                    CardInfo cardInfo = new CardInfo();
                    cardInfo.setDomainType(DomainType.DOMAIN_TYPE_SCHEDULE);
                    cardInfo.setScreenType(ScreenType.MAIN);

                    List<ScheduleInfo> schedules = new ArrayList<>();
                    ScheduleInfo schedule = new ScheduleInfo();
                    schedule.setItemType(ViewType.SCHEDULE_TYPE_2);
                    schedule.setEvent("今天下午三点去王家湾游泳，然后去优衣库看看有啥衣服可以试穿哈~.");
                    schedule.setTime("2024-04-26 15:00:00");
                    schedule.setLocation("王家湾摩尔城");
                    schedules.add(schedule);

                    schedule = new ScheduleInfo();
                    schedule.setItemType(ViewType.SCHEDULE_TYPE_2);
                    schedule.setEvent("今天下午三点去王家湾游泳，然后去优衣库看看有啥衣服可以试穿哈~.");
                    schedule.setTime("2024-04-26 15:00:00");
                    schedule.setLocation("王家湾摩尔城");
                    schedules.add(schedule);

                    schedule = new ScheduleInfo();
                    schedule.setItemType(ViewType.SCHEDULE_TYPE_3);
                    schedule.setEvent("今天下午三点去王家湾游泳，然后去优衣库看看有啥衣服可以试穿哈~.");
                    schedule.setTime("2024-04-26 15:00:00");
                    schedules.add(schedule);

                    cardInfo.setSchedules(schedules);
                    String cardJson = gson.toJson(cardInfo);
                    WindowMessageManager.getInstance().showCard(cardJson);
                } else if (TextUtils.equals(queryString, "expandCardSchedule3")) {
                    Gson gson = new Gson();

                    CardInfo cardInfo = new CardInfo();
                    cardInfo.setDomainType(DomainType.DOMAIN_TYPE_SCHEDULE);

                    List<ScheduleInfo> schedules = new ArrayList<>();
                    ScheduleInfo schedule = new ScheduleInfo();
                    schedule.setItemType(ViewType.SCHEDULE_TYPE_2);
                    schedule.setEvent("今天下午三点去王家湾游泳，然后去优衣库看看有啥衣服可以试穿哈~.");
                    schedule.setTime("2024-04-26 15:00:00");
                    schedule.setLocation("王家湾摩尔城");
                    schedules.add(schedule);

                    schedule = new ScheduleInfo();
                    schedule.setItemType(ViewType.SCHEDULE_TYPE_2);
                    schedule.setEvent("今天下午三点去王家湾游泳，然后去优衣库看看有啥衣服可以试穿哈~.");
                    schedule.setTime("2024-04-26 15:00:00");
                    schedule.setLocation("王家湾摩尔城");
                    schedules.add(schedule);

                    schedule = new ScheduleInfo();
                    schedule.setItemType(ViewType.SCHEDULE_TYPE_3);
                    schedule.setEvent("今天下午三点去王家湾游泳，然后去优衣库看看有啥衣服可以试穿哈~.");
                    schedule.setTime("2024-04-26 15:00:00");
                    schedules.add(schedule);


                    schedule = new ScheduleInfo();
                    schedule.setItemType(ViewType.SCHEDULE_TYPE_MORE);
                    schedules.add(schedule);

                    cardInfo.setSchedules(schedules);
                    String cardJson = gson.toJson(cardInfo);
                    WindowMessageManager.getInstance().showCard(cardJson);
                } else if (TextUtils.equals(queryString, "scrollUP")) {
                    ThreadUtils.executeByCached(new Utils.Task<Boolean>(new Utils.Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean o) {

                        }
                    }) {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            boolean result = WindowMessageManager.getInstance().scrollCardView(
                                    CardDirection.DIRECTION_UP, ScreenType.MAIN);
                            LogUtils.d("scrollUP doInBackground result:" + result);
                            return result;
                        }
                    });


                } else if (TextUtils.equals(queryString, "scrollDown")) {
                    ThreadUtils.executeByCached(new Utils.Task<Boolean>(new Utils.Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean o) {

                        }
                    }) {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            boolean result = WindowMessageManager.getInstance().scrollCardView(
                                    CardDirection.DIRECTION_DOWN, ScreenType.MAIN);
                            LogUtils.d("scrollDown doInBackground result:" + result);
                            return result;
                        }
                    });
                }
                else if (TextUtils.equals(queryString, "dismissCard")) {
                    WindowMessageManager.getInstance().dismissCard(ScreenType.MAIN);
                } else if (TextUtils.equals(queryString, "dismissVoiceView")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            WindowMessageManager.getInstance().dismissVoiceView(ScreenType.MAIN);
                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                }
                else if (TextUtils.equals(queryString, "inputTypewriterShort")) {
                    String text = "ai";
                    WindowMessageManager.getInstance().inputTypewriterText(text, TypeTextStyle.PRIMARY);
                } else if (TextUtils.equals(queryString, "inputTypewriterLong")) {
                    ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            String content = "来来来，喝完这杯，还有三杯~沧海一声笑，啦啦啦啦啦，水煮沉浮，啊啊啊啊啊啊~";
                            for (int i = 0; i < content.length(); i++) {
                                String asr = content.substring(0, i + 1);
                                try {
                                    Thread.sleep(5L);
                                    LogUtils.d("inputTypewriterLong doInBackground asr:" + asr);
                                    WindowMessageManager.getInstance().inputTypewriterText(asr, TypeTextStyle.SECONDARY);
                                    if (i == content.length() - 1) {
                                        Thread.sleep(5L);
                                        WindowMessageManager.getInstance().inputTypewriterText(asr, TypeTextStyle.PRIMARY);
                                    }
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            return true;
                        }

                        @Override
                        public void onSuccess(Boolean result) {

                        }
                    });
                } else if (TextUtils.equals(queryString, "inputTypewriterLong1")) {
                    String text = "仲夏午后，乌云铺天而来，大唐岭南道桂州的治所临桂县（今桂林市）陷入一片晦暗。";
                    WindowMessageManager.getInstance().inputTypewriterText(text, TypeTextStyle.SECONDARY);
                } else if (TextUtils.equals(queryString, "inputTypewriterLong2")) {
                    String text = "天下纷扰已久，四月初，岭南";
                    WindowMessageManager.getInstance().inputTypewriterText(text, TypeTextStyle.PRIMARY);
                } else if (TextUtils.equals(queryString, "inputTypewriterLong3")) {
                    String text = "天下纷扰已久，四月初";
                    WindowMessageManager.getInstance().inputTypewriterText(text, TypeTextStyle.SECONDARY);
                } else if (TextUtils.equals(queryString, "showExecFeedbackR")) {
                    Gson gson = new Gson();

                    ExecuteFeedbackInfo feedbackInfo = new ExecuteFeedbackInfo();
                    feedbackInfo.setLocation(VoiceLocation.FRONT_RIGHT);
                    feedbackInfo.setText("车窗给你打开了~");
                    feedbackInfo.setEnable(true);

                    String feedbackJson = gson.toJson(feedbackInfo);
                    WindowMessageManager.getInstance().showExecuteFeedbackWindow(feedbackJson);
                } else if (TextUtils.equals(queryString, "showExecFeedbackL")) {
                    Gson gson = new Gson();

                    ExecuteFeedbackInfo feedbackInfo = new ExecuteFeedbackInfo();
                    feedbackInfo.setLocation(VoiceLocation.FRONT_LEFT);
                    feedbackInfo.setText("空调给你打开了~请享受凉爽的风哦~");
                    feedbackInfo.setEnable(false);

                    String feedbackJson = gson.toJson(feedbackInfo);
                    WindowMessageManager.getInstance().showExecuteFeedbackWindow(feedbackJson);
                } else if (TextUtils.equals(queryString, "dismissFeedbackR")) {
                    WindowMessageManager.getInstance().dismissFeedbackWindow(VoiceLocation.FRONT_RIGHT);
                } else if (TextUtils.equals(queryString, "formatDate")) {
                    String[] dateStrArr = {
                            "2024-06-27 00:01:00",
                            "2024-06-27 01:10:00",
                            "2024-06-27 10:10:00",
                            "2024-06-27 12:00:00",
                            "2024-06-27 13:10:00",
                            "2024-06-27 22:10:00",
                            "2024-06-27 23:59:59"
                    };
                    for (String s : dateStrArr) {
                        long timeStamp = DateUtil.getTimeStamp(s);
                        int hour = DateUtil.getHour(timeStamp);
                        String customTimeStr = DateUtil.getCustomTimeStr(timeStamp, false);
                        LogUtils.d("hour:" + hour +" customTimeStr:" + customTimeStr);
                    }
                    String timeType = Settings.System.getString(
                            context.getContentResolver(),
                            Settings.System.TIME_12_24);
                    LogUtils.d("timeType:" + timeType);
                } else if (TextUtils.equals(queryString, "getLoop")) {
                    String originalContent = "haha@{location}，<@{date}，白天@{wx_wind_day}@{wind_level_day}级,夜间@{wx_wind_night}@{wind_level_night}级，>";

                    String regexLoop = "(<)(.*?)(>)";
                    Pattern patternLoop = Pattern.compile(regexLoop);
                    Matcher matcherLoop = patternLoop.matcher(originalContent);
                    if (matcherLoop.find()) {
                        String target = matcherLoop.group(2);
                        LogUtils.d("getLoop target:" + target);
                    } else {
                        LogUtils.d("getLoop not find ...");
                    }
                } else if (TextUtils.equals(queryString, "getDisplayId")) {
                    int displayIdFL = MegaDisplayHelper.getVoiceDisplayId(VoiceLocation.FRONT_LEFT);
                    int displayIdFR = MegaDisplayHelper.getVoiceDisplayId(VoiceLocation.FRONT_RIGHT);
                    int displayIdRL = MegaDisplayHelper.getVoiceDisplayId(VoiceLocation.REAR_LEFT);
                    LogUtils.d("onCreate displayIdFL:" + displayIdFL + " displayIdFR:" + displayIdFR +
                            " displayIdRL:" + displayIdRL);
                } else if (TextUtils.equals(queryString, "defaultUIMode")) {
                    String defaultUIMode = MegaSystemProperties.get("persist.mega.daynight.mode", "0");
                    Log.i(TAG, "onReceive: defaultUIMode:" + defaultUIMode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeWeatherDayTask() {
        Gson gson = new Gson();

        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_WEATHER);

        List<Weather> weathers = new ArrayList<>();
        Weather weather = new Weather();
        weather.setLocation("武汉市 蔡甸区");

        weather.setFormatDate("2024-11-22");
//        weather.setTempLow(5);
//        weather.setTempHigh(17);
        weather.setWeatherDesc("阴有小雨");
        weather.setWeatherDay("冰粒");
        weather.setWeatherNight("小雨");
        weather.setWindDirDay("东南风");
//        weather.setWindDirNight("东风");
        weather.setWindLevelDay("3");
//        weather.setWindLevelNight("2");
        weather.setItemType(ViewType.WEATHER_TYPE_1);

        weathers.add(weather);

        cardInfo.setWeathers(weathers);

        String cardJson = gson.toJson(cardInfo);
        WindowMessageManager.getInstance().showCard(cardJson);
    }

    private void executeWeatherDayRangeTask() {
        Gson gson = new Gson();
        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_WEATHER);

        List<Weather> weathers = new ArrayList<>();
        Weather weather = new Weather();
        weather.setLocation("武汉市 蔡甸区");
//        weather.setTempLow(5);
//        weather.setTempHigh(17);
        weather.setItemType(ViewType.WEATHER_TYPE_2);
        weathers.add(weather);

        for (int i = 0; i < 7; i++) {
            weather = new Weather();
            weather.setItemType(ViewType.WEATHER_TYPE_3);
//            weather.setTempLow(5);
//            weather.setTempHigh(17);
            weather.setTempHighDateRange(25);
            weather.setTempLowDateRange(3);
            if (i % 2 == 0) {
//                weather.setTempLow(8);
//                weather.setTempHigh(18);
                weather.setFormatDate("2024-06-28 00:00:00");
                weather.setWeatherDay("多云");
                weather.setWeatherNight("多云");

            } else if (i % 3 == 0) {
                weather.setTempLow(7);
                weather.setTempHigh(19);
                weather.setFormatDate("2024-06-29 00:00:00");
                weather.setWeatherDay("扬沙");
                weather.setWeatherNight("霾");
            } else {
                weather.setTempLow(3);
                weather.setTempHigh(19);
                weather.setFormatDate("2024-06-30 00:00:00");
                weather.setWeatherDay("小雪");
                weather.setWeatherNight("大雪");
            }

            weathers.add(weather);
        }

        cardInfo.setWeathers(weathers);

        String cardJson = gson.toJson(cardInfo);
        WindowMessageManager.getInstance().showCard(cardJson);
    }

    private void executeExpandContactsCardTask(int screenType) {
        Gson gson = new Gson();

        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_BT_PHONE);

        List<Contact> contacts = new ArrayList<>();
        Contact contact = new Contact();
        contact.setContactName("xiao fei yu" + "["+screenType+"]");
        contact.setNumber("13163268097");
        contact.setPhoneType("手机");
        contacts.add(contact);

        contact = new Contact();
        contact.setContactName("hao xiao lian");
        contact.setNumber("027-2787653");
        contact.setPhoneType("电话");
        contacts.add(contact);

        contact = new Contact();
        contact.setContactName("西门吹雪");
        contact.setNumber("027-2787653");
        contact.setPhoneType("电话");
        contacts.add(contact);

        contact = new Contact();
        contact.setContactName("叶孤城");
        contact.setNumber("027-2787653");
        contact.setPhoneType("电话");
        contacts.add(contact);

        contact = new Contact();
        contact.setContactName("陆小凤");
        contact.setNumber("027-2787653");
        contact.setPhoneType("电话");
        contacts.add(contact);

        contact = new Contact();
        contact.setContactName("楚留香");
        contact.setNumber("027-2787653");
        contact.setPhoneType("电话");
        contacts.add(contact);

        BTPhoneInfo btPhoneInfo = new BTPhoneInfo();
        btPhoneInfo.setContacts(contacts);
        cardInfo.setBtPhoneInfo(btPhoneInfo);
        cardInfo.setSessionId("蓝牙电话联系人");

        cardInfo.setScreenType(screenType);

        String cardJson = gson.toJson(cardInfo);
        WindowMessageManager.getInstance().showCard(cardJson);
    }


    private void executeExpandMediaCardTask() {
        Gson gson = new Gson();
        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_MULTIMEDIA_VIDEO);

        List<MultimediaInfo> multimediaInfos = new ArrayList<>();

        MultimediaInfo multimediaInfo = new MultimediaInfo();

        multimediaInfo.setName("少年张三丰");
        multimediaInfo.setImgUrl("https://inews.gtimg.com/om_bt/OHyQqgC_5oi4Vm0tlH49XvJzqNBHo2Zryxx5F_be5N2cIAA/1000");
        multimediaInfo.setType(MultimediaInfo.MediaType.TV_DRAMA);
        multimediaInfo.setEpisodes(30);
        multimediaInfo.setSourceType(MultimediaInfo.SourceType.TENCENT);
        multimediaInfo.setTagType(MultimediaInfo.TagType.VIP);

        multimediaInfos.add(multimediaInfo);

        for (int i = 0; i < 63; i++) {
            multimediaInfo = new MultimediaInfo();
            multimediaInfo.setName("少年派:" + i);
            multimediaInfo.setImgUrl("https://inews.gtimg.com/om_bt/OHyQqgC_5oi4Vm0tlH49XvJzqNBHo2Zryxx5F_be5N2cIAA/1000");
            multimediaInfo.setType(MultimediaInfo.MediaType.MOVIE);

            if (i % 2 == 0) {
                multimediaInfo.setSourceType(MultimediaInfo.SourceType.TENCENT);
                multimediaInfo.setTagType(MultimediaInfo.TagType.VIP);
            } else if (i % 3 == 0) {
                multimediaInfo.setTagType(MultimediaInfo.TagType.SOLE_BROADCAST);
            } else {
                multimediaInfo.setSourceType(MultimediaInfo.SourceType.IQIYI);
                multimediaInfo.setTagType(MultimediaInfo.TagType.PAYMENT);
            }

            multimediaInfos.add(multimediaInfo);
        }

        cardInfo.setMultimediaInfos(multimediaInfos);

        String cardJson = gson.toJson(cardInfo);
        WindowMessageManager.getInstance().showCard(cardJson);
    }

    private void executeExpandMusicCardTask() {
        Gson gson = new Gson();
        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_MULTIMEDIA_MUSIC);

        List<MultiMusicInfo> multiMusicInfos = new ArrayList<>();

        MultiMusicInfo multiMusicInfo = new MultiMusicInfo();

        multiMusicInfo.setName("歌曲名称歌曲名称");
        multiMusicInfo.setImgUrl("https://inews.gtimg.com/om_bt/OHyQqgC_5oi4Vm0tlH49XvJzqNBHo2Zryxx5F_be5N2cIAA/1000");
        multiMusicInfo.setArtist("歌手名称");
        multiMusicInfo.setAlbum("专辑名称");
        multiMusicInfo.setVip(true);

        multiMusicInfos.add(multiMusicInfo);

        for (int i = 0; i < 63; i++) {
            multiMusicInfo = new MultiMusicInfo();
            multiMusicInfo.setName("歌曲名称:" + i);
            multiMusicInfo.setImgUrl("https://inews.gtimg.com/om_bt/OHyQqgC_5oi4Vm0tlH49XvJzqNBHo2Zryxx5F_be5N2cIAA/1000");

            multiMusicInfo.setVip(i % 2 == 0);

            multiMusicInfos.add(multiMusicInfo);
        }

        cardInfo.setMultiMusicInfos(multiMusicInfos);

        String cardJson = gson.toJson(cardInfo);
        WindowMessageManager.getInstance().showCard(cardJson);
    }

    private void executeExpandStockCardTask() {
        Gson gson = new Gson();
        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_STOCK);

        List<StockInfo> stockInfos = new ArrayList<>();
        StockInfo stockInfo = new StockInfo();
        stockInfo.setName("岚图汽车");
        stockInfo.setCode("A9988989");
        stockInfo.setDate("2023-01-28 15:30:34");
//        stockInfo.setPrice(99999.98);
//        stockInfo.setPriceAmplitude(-999.98);
//        stockInfo.setAmplitudeRate(99.8);

        stockInfos.add(stockInfo);

        cardInfo.setStockInfos(stockInfos);
        String cardJson = gson.toJson(cardInfo);
        WindowMessageManager.getInstance().showCard(cardJson);
    }

    private void executeExpandGossipCardTask() {
        sendFirstData();

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String markdownSample = ResourceUtils.readAssets2String("markdown_sample_3.md");

        // 使用正则表达式匹配Markdown图片格式
        String regex = "!\\[[^\\]]*\\]\\([^\\)]+\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(markdownSample);

        int start = -1;
        int end = -1;
        String imageMarkdown = "";
        // 遍历所有匹配项
        if (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            imageMarkdown = markdownSample.substring(start, end);
            LogUtils.d("executeExpandGossipCardTask 找到的Markdown图片子串:" + imageMarkdown);
            LogUtils.d("executeExpandGossipCardTask 开始索引：" + start + " 结束索引：" + end);
        }

        int length = markdownSample.length();
        for (int i = 0; i < length; i++) {
            String element = String.valueOf(markdownSample.charAt(i));

            if (i == start) {
                element = imageMarkdown;
                i = end - 1;
            }


            CardInfo cardInfo = new CardInfo();
            cardInfo.setDomainType(DomainType.DOMAIN_TYPE_GOSSIP);
            cardInfo.setSessionId("debug-0123456789-mock-");
            cardInfo.setFromGPTFlag(true);

            List<ChatMessage> chatMessages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setItemType(ViewType.CHAT_TYPE_MAIN);
            chatMessage.setContent(element);
            chatMessage.setTotalLen(length);

            if (i == length - 1) {
                chatMessage.setStreamMode(StreamMode.COMPLETION);
            } else {
                chatMessage.setStreamMode(StreamMode.ON_GOING);
            }

            chatMessages.add(chatMessage);
            cardInfo.setChatMessages(chatMessages);

            String cardJson = getGson().toJson(cardInfo);
            WindowMessageManager.getInstance().showCard(cardJson);
            try {
                Thread.sleep(15L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void executeExpandGossipCardTask2() {
        String markdownSample = ResourceUtils.readAssets2String("markdown_sample_4.md");

        String regex = "([-+*]\\s+.*\\r\\s+)(!\\[[^\\]]+\\]\\([^\\)]+\\))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(markdownSample);

        // 查找并打印符合条件的子串和索引
        boolean found = false;
        while (matcher.find()) {
            found = true;
            int startIndex = matcher.start();
            int endIndex = matcher.end();
            String matchedSubstring = matcher.group();

            if (matchedSubstring.split("\r").length == 2) {
                System.out.println("匹配的子串:\n" + matchedSubstring);
                System.out.println("起始索引: " + startIndex + "，结束索引: " + endIndex);
                if (matchedSubstring.contains("\r")) {
                    System.out.println("含有\r");
                    String target = matchedSubstring.replace("\r", "^\n");
                    System.out.println("after replace:" + target);
                } else {
                    System.out.println("不含有\r");
                }
                System.out.println("无序列表项后的字符为:->" + markdownSample.charAt(endIndex - 1) + "<-");
                System.out.println("-------------");
            }

        }

        if (!found) {
            System.out.println("没有匹配到任何子串。");
        }

//        CardInfo cardInfo = new CardInfo();
//        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM);
//        cardInfo.setSessionId("debug-0123456789-mock-");
////        cardInfo.setFromGPTFlag(true);
//
//        List<ChatMessage> chatMessages = new ArrayList<>();
//        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setItemType(ViewType.CHAT_TYPE_MAIN);
//        chatMessage.setContent(markdownSample);
//
//
//        chatMessages.add(chatMessage);
//        cardInfo.setChatMessages(chatMessages);
//
//        String cardJson = getGson().toJson(cardInfo);
//        WindowMessageManager.getInstance().showCard(cardJson);

    }

    private void executeExpandGossipCardTask3() {
        String markdownSample = ResourceUtils.readAssets2String("markdown_sample_4.md");

        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM);
        cardInfo.setSessionId("debug-0123456789-mock-");
//        cardInfo.setFromGPTFlag(true);

        List<ChatMessage> chatMessages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setItemType(ViewType.CHAT_TYPE_MAIN);
        chatMessage.setContent(markdownSample);


        chatMessages.add(chatMessage);
        cardInfo.setChatMessages(chatMessages);

        String cardJson = getGson().toJson(cardInfo);
        WindowMessageManager.getInstance().showCard(cardJson);

    }

    private void sendFirstData() {
        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_GOSSIP);
        cardInfo.setSessionId("debug-0123456789-mock-");
        cardInfo.setFromGPTFlag(true);

        List<ChatMessage> chatMessages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setItemType(ViewType.CHAT_TYPE_MAIN);
        chatMessage.setContent("");

        chatMessage.setStreamMode(StreamMode.START);

        chatMessages.add(chatMessage);
        cardInfo.setChatMessages(chatMessages);

        String cardJson = getGson().toJson(cardInfo);
        WindowMessageManager.getInstance().showCard(cardJson);
    }

    private void executeExpandScheduleCard1() {
        Gson gson = new Gson();

        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_SCHEDULE);

        List<ScheduleInfo> schedules = new ArrayList<>();
        ScheduleInfo schedule = new ScheduleInfo();
        schedule.setItemType(ViewType.SCHEDULE_TYPE_1);
        schedule.setEvent("今天下午三点去王家湾游泳，然后去优衣库看看有啥衣服可以试穿哈~.");
        schedule.setTime("2024-04-26 00:00:00");
        schedule.setLocation("王家湾");
        schedules.add(schedule);

        cardInfo.setSchedules(schedules);
        String cardJson = gson.toJson(cardInfo);
        WindowMessageManager.getInstance().showCard(cardJson);
    }

    private void executeRefreshContactCardTask() {
        Gson gson = new Gson();
        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_BT_PHONE);

        List<Contact> contacts = new ArrayList<>();
        Contact contact = new Contact();
        contact.setContactName("黄飞鸿");
        contact.setNumber("13163268097");
        contact.setPhoneType("手机");
        contacts.add(contact);

        contact = new Contact();
        contact.setContactName("拂晓神剑");
        contact.setNumber("027-2787653");
        contact.setPhoneType("电话");
        contacts.add(contact);

        BTPhoneInfo btPhoneInfo = new BTPhoneInfo();
        btPhoneInfo.setContacts(contacts);
        cardInfo.setBtPhoneInfo(btPhoneInfo);

        String cardJson = gson.toJson(cardInfo);
        WindowMessageManager.getInstance().updateCardData(cardJson);
    }

    private void executeExpandBaiKeCardTask(boolean isLong) {
        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_ENCYCLOPEDIA_NOT_STREAM);
        cardInfo.setSessionId("debug-0123456789-mock-baike");


        List<ChatMessage> chatMessages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setItemType(ViewType.CHAT_TYPE_MAIN);
        String content = isLong ?
            "苹果翻译成英文是车载智能语音控制长按智能遥控钥匙的锁止设置开启锁车自动升窗功能适合用于需要稍微少一些内存的场景"
        : "苹果翻译成英文是车载智能语音控制长按智能遥控钥匙的锁止设置开启锁车自动升窗功能使用岚";
        chatMessage.setContent(content);

        chatMessage.setStreamMode(StreamMode.NOT_STREAM);

        chatMessages.add(chatMessage);
        cardInfo.setChatMessages(chatMessages);

        String cardJson = getGson().toJson(cardInfo);
        WindowMessageManager.getInstance().showCard(cardJson);
    }

    private synchronized Gson getGson() {
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson;
    }

}
