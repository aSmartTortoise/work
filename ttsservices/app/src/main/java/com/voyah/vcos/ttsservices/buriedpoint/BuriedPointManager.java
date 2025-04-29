package com.voyah.vcos.ttsservices.buriedpoint;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;
import com.voyah.vcos.ttsservices.AppContext;
import com.voyah.vcos.ttsservices.buriedpoint.bean.MegaBean;
import com.voyah.vcos.ttsservices.buriedpoint.bean.TTSBuriedPointBean;
import com.voyah.vcos.ttsservices.info.PlayTTSBean;
import com.voyah.vcos.ttsservices.info.TtsBean;
import com.voyah.vcos.ttsservices.utils.AppUtils;
import com.voyah.vcos.ttsservices.utils.LogUtils;
import com.voyah.vcos.ttsservices.utils.MMKVHelper;
import com.voyah.vcos.ttsservices.utils.NetWorkUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mega.push.report.ReportManager;
import mqtt.message.EventTrackEntity;

/**
 * 使用方式：
 * 1.先调用初始化方法。init()
 * 2.调用createBuriedPointBean()方法创建埋点对象，每个ttsid对应一个埋点兑现。
 * 3.设置埋点数据。getTtsBuriedPointBean(),通过这个方法去获取对应ttsid的埋点对象设置数据。
 * 4.找到所有的需要上传的埋点地方，进行数据上传。
 */
public class BuriedPointManager {
    private static final String TAG = "BuriedPointManager";

    private static BuriedPointManager pointManager;

    ReportManager mReportManager;
    ExecutorService CachedThreadPool = null;
    //    String customPath;
    MMKV mmkv;

    String SET_KEY = "setKey";
    Set<String> keySet;
    private Gson gson = new Gson();
    //tts的埋点对象。一个ttsId对应一个埋点bean
    Map<String, TTSBuriedPointBean> mBuriedPointMap = new HashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(3);


    public static BuriedPointManager getInstance() {
        if (null == pointManager) {
            synchronized (BuriedPointManager.class) {
                if (null == pointManager)
                    pointManager = new BuriedPointManager();
            }
        }
        return pointManager;
    }


    public void init(Context context) {
        mReportManager = ReportManager.getInstance();
        mReportManager.init(context);
        CachedThreadPool = Executors.newCachedThreadPool();

        mmkv = MMKVHelper.getInstance().getmKV();
        keySet = mmkv.decodeStringSet(SET_KEY);
        if (keySet == null) {
            keySet = new LinkedHashSet<>();
            mmkv.encode(SET_KEY, keySet);
        }
    }

    public void buriedPointBean(Object ttsBean) {
        if (null == mReportManager)
            return;
//        LogUtils.e(TAG, "createBuriedPointBean " + new Gson().toJson(ttsBean));
//        String ttsId = "";
        if (ttsBean instanceof TtsBean) {
            TtsBean bean = (TtsBean) ttsBean;
            createBuriedPointBean(TextUtils.isEmpty(bean.getOriginTtsId()) ? bean.getTtsId() : bean.getOriginTtsId());
            TTSBuriedPointBean ttsBuriedPointBean = mBuriedPointMap.get(TextUtils.isEmpty(bean.getOriginTtsId()) ? bean.getTtsId() : bean.getOriginTtsId());
//            LogUtils.e(TAG, "1buriedPointBean ttsBuriedPointBean is " + ttsBuriedPointBean + " ," + (ttsBuriedPointBean == null));
            if (null != ttsBuriedPointBean) {
//                ttsId = bean.getOriginTtsId();
                ttsBuriedPointBean.setTts_id(bean.getOriginTtsId(), bean.getTtsId());
                ttsBuriedPointBean.setTts_text(bean.getTts());
                ttsBuriedPointBean.setTts_use(bean.getPackageName());
            }

        } else if (ttsBean instanceof PlayTTSBean) {
            PlayTTSBean bean = (PlayTTSBean) ttsBean;
            createBuriedPointBean(TextUtils.isEmpty(bean.getOriginTtsId()) ? bean.getTtsId() : bean.getOriginTtsId());
            TTSBuriedPointBean ttsBuriedPointBean = mBuriedPointMap.get(TextUtils.isEmpty(bean.getOriginTtsId()) ? bean.getTtsId() : bean.getOriginTtsId());
//            LogUtils.e(TAG, "2buriedPointBean ttsBuriedPointBean is " + ttsBuriedPointBean + " ," + (ttsBuriedPointBean == null));
            if (null != ttsBuriedPointBean) {
//                ttsId = bean.getOriginTtsId();
                ttsBuriedPointBean.setTts_id(bean.getOriginTtsId(), bean.getTtsId());
                ttsBuriedPointBean.setTts_text(bean.getTts());
                ttsBuriedPointBean.setTts_use(bean.getPackageName());
            }
        }
//        upLoading(true, ttsId);

    }

    /**
     * 根据ttsid创建埋点数据，
     *
     * @param originTtsId
     */
    public void createBuriedPointBean(String originTtsId) {
        if (null == mReportManager)
            return;
        if (!mBuriedPointMap.containsKey(originTtsId)) {
//            LogUtils.e(TAG, "originTtsId id ：" + originTtsId);
            mBuriedPointMap.put(originTtsId, new TTSBuriedPointBean());
        }
    }


    /**
     * 根据ttsid获取对应的埋点数据。
     *
     * @param ttsid
     * @return
     */
    public TTSBuriedPointBean getTtsBuriedPointBean(String ttsid) {
        return mBuriedPointMap.get(ttsid);
    }

    public void setBuriedPointData(String ttsId, String tts_status, String tts_time_start, String tts_time_end, String tts_line, String tts_speaker) {
        if (null == mReportManager)
            return;
//        LogUtils.e(TAG, "setBuriedPointData ttsId:" + ttsId);
        TTSBuriedPointBean ttsBuriedPointBean = mBuriedPointMap.get(ttsId);
        if (null != ttsBuriedPointBean) {
//            LogUtils.d(TAG, "setBuriedPointData ttsId:" + ttsId);
            if (!TextUtils.isEmpty(ttsId) && StringUtils.isEmpty(ttsBuriedPointBean.getTts_id()))
                ttsBuriedPointBean.setTts_id(ttsId, ttsId);
            if (!TextUtils.isEmpty(tts_status) && StringUtils.isEmpty(ttsBuriedPointBean.getTts_status()))
                ttsBuriedPointBean.setTts_status(tts_status);
            if (!TextUtils.isEmpty(tts_time_start) && StringUtils.isEmpty(ttsBuriedPointBean.getTts_time_start()))
                ttsBuriedPointBean.setTts_time_start(tts_time_start);
            if (!TextUtils.isEmpty(tts_time_end) && StringUtils.isEmpty(ttsBuriedPointBean.getTts_time_end()))
                ttsBuriedPointBean.setTts_time_end(tts_time_end);
            if (!TextUtils.isEmpty(tts_line) && StringUtils.isEmpty(ttsBuriedPointBean.getTts_line()))
                ttsBuriedPointBean.setTts_line(tts_line);
            if (!TextUtils.isEmpty(tts_speaker) && StringUtils.isEmpty(ttsBuriedPointBean.getTts_speaker()))
                ttsBuriedPointBean.setTts_speaker(tts_speaker);

            LogUtils.d(TAG, new Gson().toJson(ttsBuriedPointBean));
        } else {
            LogUtils.d(TAG, "ttsBuriedPointBean is null");
        }
    }

    public void lastSetBuriedPointData(String ttsId, String tts_status, String tts_time_start, String tts_time_end, String tts_line, String tts_speaker) {
        setBuriedPointData(ttsId, tts_status, tts_time_start, tts_time_end, tts_line, tts_speaker);
        upLoading(ttsId);
    }


    public volatile Boolean isRun = false;

    /**
     * 根据网络情况，上传埋点数据，并清除掉上传的map。
     * <p>
     * * @param ttsid     tts的id
     */
    public void upLoading(String ttsid) {
        LogUtils.d(TAG, "upLoading ttsid:" + ttsid);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runAble(ttsid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void runAble(String ttsid) {
        boolean isConnect = NetWorkUtils.isConnected(AppContext.instant, true);
        LogUtils.d(TAG, "upLoading ttsid:" + ttsid);
        if (null == mReportManager)
            return;
        TTSBuriedPointBean ttsBuriedPointBean = mBuriedPointMap.remove(ttsid);
        if (null == ttsBuriedPointBean)
            return;
        //把埋点数据生成对应的json数据,通过json数据生成对应的
//        String res = GsonUtils.toJson(buriedPointData);
        LogUtils.d(TAG, "upLoading message:" + ttsBuriedPointBean.toString());
        MegaBean megaBean = new MegaBean();
        megaBean.setAppid("301");
        megaBean.setEid("3010032");
        megaBean.setTs(System.currentTimeMillis());
        //todo 语音版本号
        megaBean.setAppvn(AppUtils.getAppVersionName(AppContext.instant));
        megaBean.setOthers("");
        megaBean.setEdes(gson.toJson(ttsBuriedPointBean));
        LogUtils.d(TAG, "upLoading，mega message:" + megaBean);
//        megaBean.setTs(System.currentTimeMillis());
        if (!isConnect) {
            //没连接网络需要存储数据
            String str = gson.toJson(megaBean);
            keySet.add(str);
            LogUtils.i(TAG, "cache data：" + str);
            synchronized (keySet) {
                if (keySet.size() > 1000) {
                    LogUtils.e(TAG, "cache data more than 1000：" + str);
                    keySet.clear();
                }
                keySet.add(str);
                mmkv.encode(SET_KEY, keySet);
                keySet.notifyAll();
            }

        } else {
            //当前的数据优先上传。
            LogUtils.e(TAG, "EventTrack ");
            EventTrackEntity.EventTrack.Builder builder = EventTrackEntity.EventTrack.getDefaultInstance().toBuilder();
            builder.setAppid(megaBean.getAppid());
            builder.setEid(megaBean.getEid());
            //others和edes的数据内容进行了交换
            builder.setEdes(megaBean.getEdes());
            builder.setOthers("");
            builder.setTs(megaBean.getTs());
            builder.setVn(megaBean.getAppvn());
            CachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //上传当前数据数据。
                        LogUtils.e(TAG, "start upload");
                        mReportManager.report(builder.build());
                        LogUtils.e(TAG, "end upload");
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
                        MegaBean megaBean = gson.fromJson(value, MegaBean.class);
                        builderIn.setAppid(megaBean.getAppid());
                        builderIn.setEid(megaBean.getEid());
                        //others和edes的数据内容进行了交换
                        builderIn.setEdes(megaBean.getEdes());
                        builderIn.setOthers("");
                        builderIn.setTs(megaBean.getTs());
                        builderIn.setVn(megaBean.getAppvn());

                        iterator.remove();
                        Log.e(TAG, "上传后，数据更新");
                        mmkv.encode(SET_KEY, keySet);
                        keySet.notifyAll();
                    }
                    Log.e(TAG, "准备上传埋点数据");
                    //todo 耗时操作，网络传输。
                    mReportManager.report(builderIn.build());
                    Log.e(TAG, "子线程发送数据" + value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

}
