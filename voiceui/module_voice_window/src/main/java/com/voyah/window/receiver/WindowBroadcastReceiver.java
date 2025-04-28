package com.voyah.window.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.voyah.cockpit.window.WindowMessageManager;
import com.voyah.cockpit.window.model.CardDirection;
import com.voyah.cockpit.window.model.CardInfo;
import com.voyah.cockpit.window.model.Contact;
import com.voyah.cockpit.window.model.BTPhoneInfo;
import com.voyah.cockpit.window.model.DomainType;
import com.voyah.cockpit.window.model.ExecuteFeedbackInfo;
import com.voyah.cockpit.window.model.MultimediaInfo;
import com.voyah.cockpit.window.model.ScheduleInfo;
import com.voyah.cockpit.window.model.TypeTextStyle;
import com.voyah.cockpit.window.model.ViewType;
import com.voyah.cockpit.window.model.VoiceLocation;
import com.voyah.cockpit.window.model.VoiceMode;
import com.voyah.cockpit.window.model.Weather;
import com.voyah.window.R;


import java.util.ArrayList;
import java.util.List;

/**
 * @author:lcy
 * @data:2024/2/19
 **/
public class WindowBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WindowBroadcastReceiver";

    public WindowBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.d(TAG, "onReceive: action:" + intent.getAction());
            if (TextUtils.equals(intent.getAction(), "com.voyah.window.test.query")) {
                String queryString = intent.getStringExtra("query");
                Log.i(TAG, "queryString is " + queryString);
                if (TextUtils.equals(queryString, "voiceFL")) {
                    WindowMessageManager.getInstance().onVoiceAwake(VoiceMode.VOICE_MODE_OFFLINE,
                            VoiceLocation.FRONT_LEFT);
                } else if (TextUtils.equals(queryString, "voiceFR")) {
                    WindowMessageManager.getInstance().onVoiceAwake(VoiceMode.VOICE_MODE_OFFLINE,
                            VoiceLocation.FRONT_RIGHT);
                }
                else if (TextUtils.equals(queryString, "voiceRL")) {
                    WindowMessageManager.getInstance().onVoiceAwake(VoiceMode.VOICE_MODE_OFFLINE,
                            VoiceLocation.REAR_LEFT);
                } else if (TextUtils.equals(queryString, "voiceRR")) {
                    WindowMessageManager.getInstance().onVoiceAwake(VoiceMode.VOICE_MODE_OFFLINE,
                            VoiceLocation.REAR_RIGHT);
                }

                else if (TextUtils.equals(queryString, "setVoiceStateListening")) {
                    WindowMessageManager.getInstance().onVoiceListening();
                } else if (TextUtils.equals(queryString, "setVoiceStateSpeaking")) {
                    WindowMessageManager.getInstance().onVoiceSpeaking();
                } else if (TextUtils.equals(queryString, "setVoiceStateExit")) {
                    WindowMessageManager.getInstance().onVoiceExit();
                } else if (TextUtils.equals(queryString, "expandCardContact")) {
                    executeExpandContactsCardTask();
                } else if (TextUtils.equals(queryString, "updateCardWeather2")) {
                    Gson gson = new Gson();

                    CardInfo cardInfo = new CardInfo();
                    cardInfo.setDomainType(DomainType.DOMAIN_TYPE_WEATHER);

                    List<Weather> weathers = new ArrayList<>();
                    Weather weather = new Weather();
                    weather.setLocation("武汉市 蔡甸区");
                    weather.setTempLow(5);
                    weather.setTempHigh(17);
                    weather.setItemType(ViewType.WEATHER_TYPE_2);
                    weathers.add(weather);


                    weather = new Weather();
                    weather.setFormatDate("2024-04-11 00:00:00");
                    weather.setTempLow(5);
                    weather.setTempHigh(17);
                    weather.setWeatherDesc("阴有小雨");
                    weather.setWeatherIcon(R.drawable.icon_weather_cloudy_l);
                    weather.setItemType(ViewType.WEATHER_TYPE_3);
                    weathers.add(weather);

                    weather = new Weather();
                    weather.setFormatDate("2024-04-12 00:00:00");
                    weather.setTempLow(5);
                    weather.setTempHigh(17);
                    weather.setWeatherDesc("小雨");
                    weather.setWeatherIcon(R.drawable.icon_weather_cloudy_l);
                    weather.setItemType(ViewType.WEATHER_TYPE_3);
                    weathers.add(weather);

                    cardInfo.setWeathers(weathers);

                    String cardJson = gson.toJson(cardInfo);
                    WindowMessageManager.getInstance().showCard(cardJson);
                } else if (TextUtils.equals(queryString, "updateCardWeather1")) {
                    executeWeatherDayTask();
                } else if (TextUtils.equals(queryString, "expandCardMedia")) {
                    executeExpandMediaCardTask();
                }

                else if (TextUtils.equals(queryString, "refreshCard")) {
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
                } else if (TextUtils.equals(queryString, "expandSchedule1")) {
                    Gson gson = new Gson();

                    CardInfo cardInfo = new CardInfo();
                    cardInfo.setDomainType(DomainType.DOMAIN_TYPE_SCHEDULE);

                    List<ScheduleInfo> schedules = new ArrayList<>();
                    ScheduleInfo schedule = new ScheduleInfo();
                    schedule.setItemType(ViewType.SCHEDULE_TYPE_1);
                    schedule.setEvent("今天下午三点去王家湾游泳，然后去优衣库看看有啥衣服可以试穿哈~.");
                    schedule.setTime("2024-04-26 15:00:00");
                    schedule.setLocation("王家湾");
                    schedules.add(schedule);

                    cardInfo.setSchedules(schedules);
                    String cardJson = gson.toJson(cardInfo);
                    WindowMessageManager.getInstance().showCard(cardJson);
                }  else if (TextUtils.equals(queryString, "expandSchedule2")) {
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

                    cardInfo.setSchedules(schedules);
                    String cardJson = gson.toJson(cardInfo);
                    WindowMessageManager.getInstance().showCard(cardJson);
                } else if (TextUtils.equals(queryString, "expandSchedule3")) {
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
                                    CardDirection.DIRECTION_UP);
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
                                    CardDirection.DIRECTION_DOWN);
                            LogUtils.d("scrollDown doInBackground result:" + result);
                            return result;
                        }
                    });
                }
                else if (TextUtils.equals(queryString, "collapseCard")) {
                    WindowMessageManager.getInstance().dismissCard();
                } else if (TextUtils.equals(queryString, "inputTypewriterLong")) {
                    String text = "来来来，喝完这杯，还有三杯~沧海一声笑，啦啦啦啦啦，水煮沉浮，啊啊啊啊啊啊~";
                    WindowMessageManager.getInstance().inputTypewriterText(text, TypeTextStyle.PRIMARY);
                } else if (TextUtils.equals(queryString, "inputTypewriterLong1")) {
                    String text = "仲夏午后，乌云铺天而来，大唐岭南道桂州的治所临桂县（今桂林市）陷入一片晦暗。";
                    WindowMessageManager.getInstance().inputTypewriterText(text, TypeTextStyle.PRIMARY);
                } else if (TextUtils.equals(queryString, "inputTypewriterLong2")) {
                    String text = "天下纷扰已久，四月初，岭南";
                    WindowMessageManager.getInstance().inputTypewriterText(text, TypeTextStyle.PRIMARY);
                } else if (TextUtils.equals(queryString, "inputTypewriterShort")) {
                    String text = "aiya";
                    WindowMessageManager.getInstance().inputTypewriterText(text, TypeTextStyle.PRIMARY);
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeWeatherDayTask() {
        Gson gson = new Gson();

        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_WEATHER);

        List<Weather> weathers = new ArrayList<>();
        Weather weather = new Weather();
        weather.setLocation("武汉市 蔡甸区");

        weather.setFormatDate("2024-04-11 00:00:00");
        weather.setTempLow(5);
        weather.setTempHigh(17);
        weather.setWeatherDesc("阴有小雨");
        weather.setWeatherIcon(R.drawable.icon_weather_cloudy_l);
        weather.setItemType(ViewType.WEATHER_TYPE_1);

        weather.setWeatherDay("多云");
        weather.setWeatherNight("少云");
        weathers.add(weather);

        cardInfo.setWeathers(weathers);

        String cardJson = gson.toJson(cardInfo);
        WindowMessageManager.getInstance().showCard(cardJson);
    }

    private void executeExpandContactsCardTask() {
        Gson gson = new Gson();

        CardInfo cardInfo = new CardInfo();
        cardInfo.setDomainType(DomainType.DOMAIN_TYPE_BT_PHONE);

        List<Contact> contacts = new ArrayList<>();
        Contact contact = new Contact();
        contact.setContactName("xiao fei yu");
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

        for (int i = 0; i < 8; i++) {
            multimediaInfo = new MultimediaInfo();
            multimediaInfo.setName("少年派:" + i);
            multimediaInfo.setImgUrl("https://inews.gtimg.com/om_bt/OHyQqgC_5oi4Vm0tlH49XvJzqNBHo2Zryxx5F_be5N2cIAA/1000");
            multimediaInfo.setType(MultimediaInfo.MediaType.MOVIE);
            multimediaInfo.setSourceType(MultimediaInfo.SourceType.TENCENT);
            if (i % 2 == 0) {
                multimediaInfo.setTagType(MultimediaInfo.TagType.VIP);
            } else if (i % 3 == 0) {
                multimediaInfo.setTagType(MultimediaInfo.TagType.SOLE_BROADCAST);
            } else {
                multimediaInfo.setTagType(MultimediaInfo.TagType.PAYMENT);
            }

            multimediaInfos.add(multimediaInfo);
        }



        cardInfo.setMultimediaInfos(multimediaInfos);

        String cardJson = gson.toJson(cardInfo);
        WindowMessageManager.getInstance().showCard(cardJson);

    }

}
