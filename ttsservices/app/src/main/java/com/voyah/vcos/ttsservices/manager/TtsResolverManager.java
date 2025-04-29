package com.voyah.vcos.ttsservices.manager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.microsoft.cognitiveservices.speech.Diagnostics;
import com.voyah.vcos.ttsservices.AppContext;
import com.voyah.vcos.ttsservices.Constant;
import com.voyah.vcos.ttsservices.copymicrosoft.CopyMcTtsResolver;
import com.voyah.vcos.ttsservices.copymicrosoft.CopyStreamTtsResolver;
import com.voyah.vcos.ttsservices.manager.stream.IStreamTtsResolver;
import com.voyah.vcos.ttsservices.microsoft.McTtsResolver;
import com.voyah.vcos.ttsservices.microsoft.StreamTtsResolver;
import com.voyah.vcos.ttsservices.utils.FileUtils;
import com.voyah.vcos.ttsservices.utils.LogUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author:lcy tts合成类管理
 * @data:2024/1/31
 **/
public class TtsResolverManager {
    private static final String TAG = "TtsResolverManager";

    private static final Object mcTtsObj = new Object();
    private static final int MC_RESOLVER_SIZE = 3;

    private int num;

    private static volatile TtsResolverManager resolverManager = null;

//    private boolean isDumpAudio;
//    private boolean isDumpLog;

    private boolean isDumpMicrosoftLog = false;

    private String playRate = "";

//    private final AtomicBoolean isInit = new AtomicBoolean(false);

    private final Queue<ITtsResolver> mcTtsResolverQueue = new LinkedBlockingDeque<>();

    private final Queue<ITtsResolver> mcTtsCopyResolverQueue = new LinkedBlockingDeque<>(); //声音复刻

    private HashMap<String, String> localAudios = new HashMap<>();

    private IStreamTtsResolver iStreamTtsResolver;
    private IStreamTtsResolver iStreamTtsCopyResolver; //声音复刻

    private boolean isDumpMicrosoft = false;


    private Context mContext;

    private TtsResolverManager() {
    }

    public static TtsResolverManager getInstance() {
        if (null == resolverManager) {
            synchronized (TtsResolverManager.class) {
                if (null == resolverManager)
                    resolverManager = new TtsResolverManager();
            }
        }
        return resolverManager;
    }

    public void init(Context context) {
        this.mContext = context;
        LogUtils.i(TAG, "tts resolver manager init ");
        //启动初始化获取当前本地cache音频数据
        getMicrosoftLogSwitch();
        readLocalAudio();
//        createMicFile();
        //创建微软sdk日志保存
        //todo:获取开关配置
        //初始化添加合成器
        addMcResolver(context);
        addStreamResolver(context);

//        Diagnostics.startConsoleLogging();
    }

    private void readLocalAudio() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.d(TAG, "readLocalAudio");
                JSONObject jsonObject = FileUtils.readJsonFromAsset(AppContext.instant, "tts.json");
                localAudios = FileUtils.stringMapToHashMap(jsonObject.toString());
                LogUtils.d(TAG, "localAudios.size:" + localAudios.size());
            }
        }).start();
    }

    public String getLocalAudioPath(String name) {
//        LogUtils.d(TAG, "getLocalAudioPath name:" + name);
        String path = "";
        if (null == localAudios || localAudios.isEmpty())
            return path;
        if (localAudios.containsKey(name)) {
//            LogUtils.d(TAG, "localAudios.get(name) " + localAudios.get(name));
            path = Constant.Path.VOICE_LOCAL_AUDIO_PATH + localAudios.get(name);
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (!file.exists())
                    path = "";
            }
        }
        LogUtils.d(TAG, "getLocalAudioPath path:" + path);
        return path;
    }

    //创建微软日志保存及音频缓存文件
//    private void createMicFile() {
//        LogUtils.i(TAG, "createMicFile");
//        FileUtils.fileCreator(Constant.Path.DIR_WRITE);
//        FileUtils.directoryCreator(Constant.Path.DIR_CACHE_WRITE);
//    }

    private void addMcResolver(Context context) {
        while (mcTtsResolverQueue.size() < MC_RESOLVER_SIZE) {
            ITtsResolver iTtsResolver = new McTtsResolver(false, false, num++);
            mcTtsResolverQueue.add(iTtsResolver);
        }
        while (mcTtsCopyResolverQueue.size() < MC_RESOLVER_SIZE) {
            ITtsResolver iTtsResolver = new CopyMcTtsResolver(false, false, num++);
            mcTtsCopyResolverQueue.add(iTtsResolver);
        }

        LogUtils.i(TAG, "resolver is add");
    }

    private void addStreamResolver(Context context) {
        if (null == iStreamTtsResolver)
            iStreamTtsResolver = new StreamTtsResolver(false, false);

        if (null == iStreamTtsCopyResolver)
            iStreamTtsCopyResolver = new CopyStreamTtsResolver(false, false);
        LogUtils.i(TAG, "streamResolver is add");
    }


    public synchronized ITtsResolver getMcResolver() {
        ITtsResolver ttsResolver = null;
        synchronized (mcTtsObj) {
            int currentSize = mcTtsResolverQueue.size();
            LogUtils.d(TAG, "getMcResolver currentSize is " + currentSize);
            if (currentSize > 0)
                ttsResolver = mcTtsResolverQueue.poll();
            else
                ttsResolver = new McTtsResolver(false, false, num++);
        }
        LogUtils.d(TAG, "getMcResolver num:" + (null != ttsResolver ? ttsResolver.getNum() : "-1"));
        return ttsResolver;
    }

    public synchronized ITtsResolver getCopyMcResolver() {
        ITtsResolver ttsResolver = null;
        synchronized (mcTtsObj) {
            int currentSize = mcTtsCopyResolverQueue.size();
            LogUtils.d(TAG, "getMcResolver currentSize is " + currentSize);
            if (currentSize > 0)
                ttsResolver = mcTtsCopyResolverQueue.poll();
            else
                ttsResolver = new CopyMcTtsResolver(false, false, num++);
        }
        LogUtils.d(TAG, "getMcResolver num:" + (null != ttsResolver ? ttsResolver.getNum() : "-1"));
        return ttsResolver;
    }

    public IStreamTtsResolver getStreamTtsResolver() {
        //todo:流式暂时单实例合成、后续有时间改为两个实例交替合成（需要添加任务顺序管理机制）
        if (null == iStreamTtsResolver)
            iStreamTtsResolver = new StreamTtsResolver(false, false);
        return iStreamTtsResolver;
    }

    public IStreamTtsResolver getCopyStreamTtsResolver() {
        //todo:流式暂时单实例合成、后续有时间改为两个实例交替合成（需要添加任务顺序管理机制）
        if (null == iStreamTtsCopyResolver)
            iStreamTtsCopyResolver = new CopyStreamTtsResolver(false, false);
        return iStreamTtsCopyResolver;
    }

    public synchronized void recycleMcResolver(ITtsResolver ttsResolver) {
        if (null == ttsResolver)
            return;
        synchronized (mcTtsObj) {
            ttsResolver.reset();
            if (ttsResolver instanceof McTtsResolver) {
                int currentSize = mcTtsResolverQueue.size();
                LogUtils.d(TAG, "recycleMcResolver currentSize is " + currentSize + " ,ttsResolver.num is " + ttsResolver.getNum());
                if (currentSize < MC_RESOLVER_SIZE)
                    mcTtsResolverQueue.offer(ttsResolver);
                else
                    ttsResolver = null;
            } else if (ttsResolver instanceof CopyMcTtsResolver) {
                int currentSize = mcTtsCopyResolverQueue.size();
                LogUtils.d(TAG, "recycle CopyResolverQueue currentSize is " + currentSize + " ,ttsResolver.num is " + ttsResolver.getNum());
                if (currentSize < MC_RESOLVER_SIZE)
                    mcTtsCopyResolverQueue.offer(ttsResolver);
                else
                    ttsResolver = null;
            }
        }
    }


    private void getConfigStatus() {
        //todo:获取当前音频保存及sdk相关日志开关配置状态(测试阶段或问题复现使用)

    }

    private void getMicrosoftLogSwitch() {
        try {
            Uri uri = Uri.parse("content://com.voyah.ai.voice.export/settings/tts_log");
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        isDumpMicrosoftLog = cursor.getInt(0) == 1;
                        LogUtils.d(TAG, "isDumpMicrosoft enable = [" + isDumpMicrosoftLog + "]");
                        break;
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isDumpMicrosoftLog() {
        LogUtils.d(TAG, "isDumpMicrosoftLog:" + isDumpMicrosoftLog);
        return isDumpMicrosoftLog;
    }

    public boolean isSaveMicrosoftMsg() {
        return isDumpMicrosoft;
    }

    public void setMicrosoftDumpSwitch(boolean isDump) {
        this.isDumpMicrosoft = isDump;
    }

    public void setPlayRate(String rate) {
        this.playRate = rate;
    }

    public String getPlayRate() {
        return playRate;
    }

}
