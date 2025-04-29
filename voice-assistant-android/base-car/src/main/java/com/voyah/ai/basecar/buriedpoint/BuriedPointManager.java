package com.voyah.ai.basecar.buriedpoint;

import android.content.Context;
import android.util.Log;

import com.tencent.mmkv.MMKV;
import com.voice.sdk.buriedpoint.IBuriedPointManager;
import com.voice.sdk.buriedpoint.bean.BuriedPointData;
import com.voice.sdk.buriedpoint.bean.VadTime;
import com.voyah.ai.basecar.buriedpoint.bean.MegaBean;
import com.voyah.ai.common.ParamsGather;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mega.push.report.ReportManager;
import mqtt.message.EventTrackEntity;

/**
 * public static final String VIN = "vin";//有了
 * public static final String USER_ID = "user_id";//
 * public static final String VEHICLE_TYPE = "vehicle_type";
 * public static final String SYSTEM_VERSION = "system_version";
 * public static final String APP_VERSION = "app_version";
 * public static final String LOCATION = "location";
 * public static final String TIME = "time";
 * public static final String INTERNET = "internet";
 * public static final String SETS = "sets";//todo
 * public static final String VOICE_ZONE = "voice_zone";
 * public static final String VOICEPRINT = "voiceprint";
 * public static final String WAKEUP_TYPE = "wakeup_type";
 * public static final String WAKEUP_WORD = "wakeup_word";
 * public static final String ASR_TYPE = "asr_type";
 * public static final String SESSION_ID = "session_id";
 * public static final String QUERY_ID = "query_id";
 * public static final String INTERACTION_COUNT = "interaction_count";
 * public static final String TIMESTAMP_VAD_START = "timestamp_vad_start";
 * public static final String TIMESTAMP_VAD_END = "timestamp_vad_end";
 * public static final String TIMESTAMP_ASR = "timestamp_asr";
 * public static final String TIMESTAMP_NLU = "timestamp_nlu";
 * public static final String ASR_WORD = "asr_word";
 * public static final String OFFLINE_NLU = "offline_nlu";
 * public static final String NLU_SOURCE = "nlu_source";
 * public static final String NLU_STATUS = "nlu_status";
 * public static final String NLG_SCREEN = "nlg_screen";
 * public static final String NLG_SPEAK = "nlg_speak";
 * public static final String NLG_TYPE = "nlg_type";
 * public static final String TTS_ID = "tts_id";
 */

public class BuriedPointManager {
    private static final String TAG = "BuriedPointManager";


    private BuriedPointManager() {

    }

    public static BuriedPointManager getInstance() {
        return Holder.instance;
    }


    private static class Holder {
        private static final BuriedPointManager instance = new BuriedPointManager();
    }

    //    BuriedPointData buriedPointData;
    MegaBean megaBean;
    ReportManager mReportManager = ReportManager.getInstance();
    ExecutorService CachedThreadPool = null;
    String customPath;
    MMKV mmkv;

    String SET_KEY = "setKey";
    Set<String> keySet;

    public static final String WAKE_UP = "wake_up";
    public static final String AGENT = "agent";
    public volatile Boolean isRun = false;

    private Map<String, BuriedPointData> buriedPointDataMap = new HashMap<>();


//    public BuriedPointData getBuriedPointData() {
//        return buriedPointData;
//    }


    public void init(Context context) {
        //初始化数组
        for (int i = 0; i < vadRequestIdMap.length; i++) {
            vadRequestIdMap[i] = new LinkedHashMap<>();
            requestIdVadMap[i] = new LinkedHashMap<>();
            ListArray[i] = new LinkedList<>();
        }
        mReportManager.init(context);
        CachedThreadPool = Executors.newCachedThreadPool();
        customPath = MMKV.initialize(context, "/data/data/com.voyah.ai.voice/voicecache");
        mmkv = MMKV.defaultMMKV();
        keySet = mmkv.decodeStringSet(SET_KEY);
        if (keySet == null) {
            keySet = new LinkedHashSet<>();
            mmkv.encode(SET_KEY, keySet);
        }
    }

    /**
     * 清除埋点数据，初始化固定不变的数据
     */
//    public void cleanBuriedPointData() {
//        LogUtils.e(TAG, "数据埋点，清null埋点数据的bean对象");
//        buriedPointData = new BuriedPointData();
//        megaBean = new MegaBean();
//
//    }


//    /**
//     * 设置客户端的埋点信息。
//     *
//     * @param key   BuriedPointData.Key
//     * @param value key对应的value
//     */
//    public void setBuriedPointData(String key, String value) {
//        switch (key) {
//            case BuriedPointData.Key.VIN:
//                //vin直接获取
//                buriedPointData.setVin(value);
//                break;
//            default:
//                Class<?> clazz = buriedPointData.getClass();
//                try {
//                    key = Utils.upperFirstLatter(key);
//                    Method method = clazz.getMethod("set" + key, String.class);
//                    method.invoke(buriedPointData, value);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    LogUtils.e(TAG, "埋点设置数据key写错了：" + key);
//                }
//                break;
//
//        }
//    }

//    /**
//     * 设置mega上报需要的参数
//     *
//     * @param key
//     * @param value
//     * @return
//     */
//    public BuriedPointManager setMegaData(String key, Object value) {
//        switch (key) {
//            case MegaBean.Key.ts:
//                megaBean.setTs((Long) value);
//                break;
//            default:
//                Class<?> clazz = megaBean.getClass();
//                try {
//                    key = Utils.upperFirstLatter(key);
//                    Method method = clazz.getMethod("set" + key, String.class);
//                    method.invoke(megaBean, value);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    LogUtils.e(TAG, "埋点设置数据key写错了：" + key);
//                }
//                break;
//
//        }
//        return this;
//    }

//    public MegaBean getMegaBean(){
//        return megaBean;
//    }
    public BuriedPointData createBuriedPointBeanToRequestId(String requestId) {
        if (buriedPointDataMap.containsKey(requestId)) {
            LogUtils.e(TAG, "客户端埋点，出现重复的数据：requestId是" + requestId);
            return buriedPointDataMap.get(requestId);
        }
        buriedPointDataMap.put(requestId, new BuriedPointData());
        return buriedPointDataMap.get(requestId);
    }

    public BuriedPointData getBuriedPointBeanByRequestId(String requestId) {

        return buriedPointDataMap.get(requestId);
    }


    //==================Vad的埋点上传===================
//    public Stack<VadTime> vadTimeStack = new Stack<>();
    private Map<VadTime, String>[] vadRequestIdMap = new HashMap[6];
    private Map<String, VadTime>[] requestIdVadMap = new HashMap[6];

    private LinkedList<VadTime>[] ListArray = new LinkedList[6];

    public void saveVadStartTime(Long startTime, String position) {
        int index = getStackPosition(position);
        LogUtils.d(TAG, "埋点,当前转换的position:" + index + " ");
        VadTime vadTime = new VadTime();
        vadTime.setVadStartTime(startTime);

        if (ListArray[index] == null) {
            ListArray[index] = new LinkedList<>();
            LogUtils.d(TAG, "埋点,当前listArray是null的：" + index + " ");
        }
        ListArray[index].addFirst(vadTime);
        LogUtils.d(TAG, "================埋点,vad开始时间=============");
    }

    private int getStackPosition(String position) {
        LogUtils.d(TAG, "埋点，position：" + position);
        int realPosition;
        position = (position == null) ? "" : position;
        switch (position) {
            case "":
                realPosition = 0;
                break;
            default:
                boolean isNumber = Utils.isNumeric(position);
                if (isNumber) {
                    realPosition = Integer.parseInt(position);
                    if (realPosition >= 0 && realPosition <= 5) {
                    } else {
                        realPosition = 0;
                    }
                } else {
                    LogUtils.d(TAG, "当前传下来的position不是int,默认给到主驾:" + position);
                    realPosition = 0;
                }
                break;

        }
        return realPosition;
    }

    public synchronized void saveVadTimeToRequestId(String requestId, String position) {

        LogUtils.d(TAG, "埋点，准备绑定request_id" + position);
        int index = positionStringToInt(position);
        if (!requestIdVadMap[index].containsKey(requestId)) {
            if (!ListArray[index].isEmpty()) {
                VadTime vadTime = ListArray[index].peek();
                vadRequestIdMap[index].put(vadTime, requestId);
                requestIdVadMap[index].put(requestId, vadTime);
                LogUtils.d(TAG, "埋点,vad绑定request_id，保存");
            } else {
                LogUtils.e(TAG, "埋点,当前栈顶为null");
            }
        } else {
            LogUtils.d(TAG, "埋点,存在对应的requestId：" + requestId);

        }
    }

    public VadTime getVadTime(String requestId, String soundLocation) {
        int index = positionStringToInt(soundLocation);
        VadTime res = requestIdVadMap[index].get(requestId);
        return res;
    }

    private int positionStringToInt(String position) {
        int realPosition;
        switch (position) {
            case "first_row_left"://主驾/左前/一排左
                realPosition = 0;
                break;
            case "first_row_right"://副驾/右前/一排右
                realPosition = 1;
                break;
            case "second_row_left"://二排左/左后/后排左
                realPosition = 2;
                break;
            case "second_row_right"://二排右/右后/后排右
                realPosition = 3;
                break;
            case "third_row_left"://三排左
                realPosition = 4;
                break;
            case "third_row_right"://三排右
                realPosition = 5;
                break;
            default:
                realPosition = 0;
                break;
        }
        return realPosition;
    }

    public void saveVadEndTime(Long startTime, String position) {
        try {
            int index = getStackPosition(position);
            LogUtils.d(TAG, "saveVadEndTime index is :" + index);
            if (index < 0 || index > 5) {
                index = 0;
            }
            if (ListArray[index] == null) {
                LogUtils.d(TAG, "saveVadEndTime List is null,error");
                ListArray[index] = new LinkedList<>();
            }

            if (ListArray[index].isEmpty()) {
                LogUtils.d(TAG, "埋点,时间信息是null的，没经过asrAgent，队列里就是null的");
                return;
            }
            VadTime vadTime = ListArray[index].peek();
            //当前vad时间是否被requestId接收。
            if (vadRequestIdMap[index].containsKey(vadTime)) {
                vadTime.setVadEndTime(startTime);
            } else {
                //当前存在的问题，是是否要区分位置。
                LogUtils.d(TAG, "埋点,当前Vad时间没被requestId接收,所以移除掉当前vad,个数：" + ListArray[index].size());
                ListArray[index].clear();
                vadRequestIdMap[index].clear();
                requestIdVadMap[index].clear();
            }
            LogUtils.d(TAG, "埋点，当前存储的数据量：" + ListArray[index].size());
            //删除多余的数据
            if (requestIdVadMap[index].size() > 10) {
                LogUtils.d(TAG, "埋点,清null vadTime的map里的数据，防止内存泄漏");
                while (ListArray[index].size() > 3) {
                    VadTime curVadTime = ListArray[index].getLast();
                    String requestId = vadRequestIdMap[index].get(curVadTime);
                    ListArray[index].remove(curVadTime);
                    requestIdVadMap[index].remove(requestId);
                    vadRequestIdMap[index].remove(curVadTime);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void upLoading(boolean isConnect, String requestId, String mode, boolean isSend, String position) {
        if (!isSend) return;
        //获取埋点数据
        BuriedPointData buriedPointData = buriedPointDataMap.remove(requestId);
//        LogUtils.d(TAG, "埋点，获取的数据是：" + buriedPointData + "requestId是：" + requestId + "对象里的request_id:" + buriedPointData.getQuery_id());
        //设置Vad的数据,一定有
        int index = positionStringToInt(position);
        VadTime vadTime = requestIdVadMap[index].get(index);
        if (vadTime != null) {
            buriedPointData.setTimestamp_vad_start(vadTime.getVadStartTime() + "");
            buriedPointData.setTimestamp_vad_end(vadTime.getVadEndTime() + "");
        }
        //设置mega数据
        MegaBean megaBean = new MegaBean();
        megaBean.setAppid("301");
        if (mode.equals(WAKE_UP)) {
            megaBean.setEid("3010012");
        } else {
            megaBean.setEid("3010022");
        }//73
        megaBean.setTs(System.currentTimeMillis());
        megaBean.setAppvn(buriedPointData.getApp_version() == "null" ? "" : buriedPointData.getApp_version());
        //对不需要的埋点数据进行清除
//        buriedPointData.setSystem_version(null);
//        buriedPointData.setVin(null);
//        buriedPointData.setApp_version(null);
//        buriedPointData.setUser_id(null);
//        buriedPointData.setTime(null);
//        buriedPointData.setVehicle_type(null);
        buriedPointData.setNlu_version("nlu1.0-20241025");
        megaBean.setOthers("");
        megaBean.setEdes(GsonUtils.toJson(buriedPointData));

        //把埋点数据生成对应的json数据,通过json数据生成对应的
//        String res = GsonUtils.toJson(buriedPointData);
        LogUtils.d(TAG, "BURIED_POINT,megaBean context is :" + GsonUtils.toJson(megaBean));
//        megaBean.setTs(System.currentTimeMillis());

        LogUtils.i(TAG, "cache size is :" + keySet.size());
        if (!isConnect) {
            //没连接网络需要存储数据
            String str = GsonUtils.toJson(megaBean);
            keySet.add(str);
            LogUtils.i(TAG, "is offline , cache megaBean data.");
            synchronized (keySet) {
                if (keySet.size() >= 1000) {
                    LogUtils.e(TAG, "cache data > 1000 ，clear data .cur data is ：" + str);
                    keySet.clear();
                }
                keySet.add(str);
                mmkv.encode(SET_KEY, keySet);
                keySet.notifyAll();
            }

        } else {
            //当前的数据优先上传。
            LogUtils.e(TAG, "create new buried point data EventTrack class");
            EventTrackEntity.EventTrack.Builder builder = EventTrackEntity.EventTrack.getDefaultInstance().toBuilder();
            builder.setAppid(megaBean.getAppid());
            builder.setEid(megaBean.getEid());
            //others和edes的数据内容进行了交换
            builder.setEdes(megaBean.getEdes());
            builder.setOthers("");
            builder.setTs(megaBean.getTs());
            builder.setVn(megaBean.getAppvn());

            if (ParamsGather.type.equals("H56C")) {
                builder.setVcos("H56C OTA2.0");
            } else if (ParamsGather.type.equals("H37A")) {
                builder.setVcos("H37A OTA1.0");
            }


            CachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //上传当前数据数据。
                        LogUtils.e(TAG, "online data, up load start");
                        mReportManager.report(builder.build());
                        LogUtils.e(TAG, "online data, up load end");
                        synchronized (keySet) {
                            if (keySet.size() > 0 && isRun == false) {
                                isRun = true;
                                thread.start();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    //用于上传埋点缓存的线程
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    String value;
                    EventTrackEntity.EventTrack.Builder builderIn = EventTrackEntity.EventTrack.getDefaultInstance().toBuilder();
                    synchronized (keySet) {
                        if (keySet.isEmpty()) {
                            try {
                                keySet.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        //拿到缓存的数据
                        Iterator<String> iterator = keySet.iterator();
                        value = iterator.next();
                        MegaBean megaBean = GsonUtils.fromJson(value, MegaBean.class);

                        builderIn.setAppid(megaBean.getAppid());
                        builderIn.setEid(megaBean.getEid());
                        //others和edes的数据内容进行了交换
                        builderIn.setEdes(megaBean.getEdes());
                        builderIn.setOthers("");
                        builderIn.setTs(megaBean.getTs());
                        builderIn.setVn(megaBean.getAppvn());

                        iterator.remove();
                        mmkv.encode(SET_KEY, keySet);
                        keySet.notifyAll();
                    }
                    //todo 耗时操作，网络传输。
                    mReportManager.report(builderIn.build());
                    Log.e(TAG, "The child thread sends output ： " + value);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    });

}
