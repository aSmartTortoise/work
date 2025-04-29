package com.voyah.vcos.ttsservices.copymicrosoft;

import android.media.AudioAttributes;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.microsoft.cognitiveservices.speech.CancellationErrorCode;
import com.microsoft.cognitiveservices.speech.Connection;
import com.microsoft.cognitiveservices.speech.EmbeddedSpeechConfig;
import com.microsoft.cognitiveservices.speech.PropertyId;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisWordBoundaryEventArgs;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.voyah.vcos.ttsservices.BinderList;
import com.voyah.vcos.ttsservices.Constant;
import com.voyah.vcos.ttsservices.buriedpoint.BuriedPointManager;
import com.voyah.vcos.ttsservices.info.PlayTTSBean;
import com.voyah.vcos.ttsservices.manager.ITtsResolver;
import com.voyah.vcos.ttsservices.manager.ITtsResolverCallBack;
import com.voyah.vcos.ttsservices.manager.SpeakerManager;
import com.voyah.vcos.ttsservices.manager.TtsResolverManager;
import com.voyah.vcos.ttsservices.manager.near.ProximityInteractionManager;
import com.voyah.vcos.ttsservices.mem.ByteArr;
import com.voyah.vcos.ttsservices.mem.ByteArrMgr;
import com.voyah.vcos.ttsservices.mem.TtsByteArrMgr;
import com.voyah.vcos.ttsservices.utils.LogUtils;
import com.voyah.vcos.ttsservices.utils.MMKVHelper;
import com.voyah.vcos.ttsservices.utils.SaveAudioUtils;
import com.voyah.vcos.ttsservices.utils.SsmlUtils;
import com.voyah.vcos.ttsservices.utils.Util;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author:lcy 声音复刻
 * @data:2024/1/30
 **/

public class CopyMcTtsResolver implements ITtsResolver {
    private String TAG = "CopyMcTtsResolver";
    private static final String SSML_PATTERN = "<speak version=\"1.0\" xml:lang=\"language\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xmlns:mstts=\"http://www.w3.org/2001/mstts\">\n" +
            "    <voice name=\"PhoenixLatestNeural\" leadingsilence-exact=\"0ms\">\n" +
            "        <mstts:ttsembedding speakerProfileId=\"profileId\"/>\n" +
            "        <mstts:express-as style=\"Prompt\">\n" +
            "            <lang xml:lang=\"language\">\n" +
            "                %s\n" +
            "            </lang>\n" +
            "        </mstts:express-as>\n" +
            "    </voice>\n" +
            "</speak>";


    private SpeechSynthesizer synthesizer;
    private Connection connection;

    private ITtsResolverCallBack ttsResolverCallBack;

    private int type = AudioAttributes.CONTENT_TYPE_SPEECH;

    private int num;
    private String mTtsId;
    private int mUsage;
    private String originTtsId;
    private boolean isParsing;
    private boolean isFirstParsing;

    private final ArrayList<SpeechSynthesisWordBoundaryEventArgs> wordBoundaries = new ArrayList<>();
    private long receivedLength = 0;
    private final Object synchronizedObj = new Object();

    private final long TTS_SDK_LOG_SIZE = 50 * 1024 * 1024;

    private boolean stopped = false;
    private CancellationErrorCode errorCode = CancellationErrorCode.NoError;
    private SpeakingRunnable speakingRunnable;
    private ExecutorService singleThreadExecutor;
    private String ssml;

//    private DecodeRunnable decodeRunnable;

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    private Handler checkHandler;
    private HandlerThread checkHandlerThread;
    private ByteArrMgr.ByteArrObj completedByteArray = new ByteArrMgr.ByteArrObj(0);
    private byte[] defaultByte = new byte[0];


    public CopyMcTtsResolver(boolean isDumpAudio, boolean isDumpLog, int num) {
        this.num = num;
        TAG = TAG + "-" + num;
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        speakingRunnable = new SpeakingRunnable();
        init(num);
        initHandler();
    }

    private void initHandler() {
        checkHandlerThread = new HandlerThread("checkMicSdkLogThread");
        checkHandlerThread.start();
        checkHandler = new Handler(checkHandlerThread.getLooper());
    }

    @Override
    public void init(int num) {
        LogUtils.i(TAG, "init num:" + num);
        while (!initMicroTTS()) {
            try {
                Thread.sleep(1500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean initMicroTTS() {
        LogUtils.i(TAG, "initMicroTTS");
        //todo:初始化微软TTS-判断当前环境 S P
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(Constant.McAccount.SPEECH_SUBSCRIPTION_KEY_S_COPY, Constant.McAccount.SERVICE_REGION_S_COPY);
        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Raw24Khz16BitMonoPcm);
        speechConfig.setProperty("SpeechSynthesis_KeepConnectionAfterStopping", "true");
        //混合模式
        speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthBackend, Constant.SpeechConfig.HYBRID);
        //离线模型加载路径
        String offLineResPath;
        if (!Util.vehicleSimulatorJudgment())
            offLineResPath = Constant.Path.PHONE_OFFLINE_RES_PATH;
        else
            offLineResPath = Constant.Path.CAR_OFFLINE_RES_PATH;
        EmbeddedSpeechConfig embeddedSpeechConfig = EmbeddedSpeechConfig.fromPath(offLineResPath);
        speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthOfflineDataPath, offLineResPath);
        speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthModelKey, Constant.McAccount.DECRYPTION_KEY);
        //针对网络质量差 在线转离线的情况下，提高了interval阈值，提前转离线，以减少卡顿 （interval默认为1000）
        speechConfig.setProperty("SpeechSynthesis_FrameTimeoutInterval", Constant.SpeechConfig.INTERVAL);
        //设置在线解码节流以降低峰值使用率。
//        speechConfig.setProperty("SPEECH-SynthThrottleDecoding", "true");
        //设置遥测端点
        speechConfig.setProperty("EmbeddedSpeech-TelemetryRegion", "china");
        //实时离在线切换
        speechConfig.setProperty("AutoOnlineOfflineSwitch", "Yes");

        // In SDK version 1.34.0 and later, the default setting for compression format in transmission on Linux and Android changes to false.
        // So please add this line to enable compression format in transmission if needed, especially while using pcm output format;
        //修改传输格式为mp3
//        speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthEnableCompressedAudioTransmission, "true");

        //保存微软日志
//        speechConfig.setProperty(PropertyId.Speech_LogFilename, Constant.Path.DIR_WRITE);

        //获取当前音色
//        String speakerName = SpeakerManager.getInstance().getTtsSpeaker();
//        LogUtils.i(TAG, "speakerName:" + speakerName);
//        currentOffLineSpeakerName = speakerName;
//        speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthOfflineVoice, speakerName);
        //设置微软cache缓存路径及上限个数-500个约81M
        speechConfig.setProperty("SPEECH-SynthesisCachingPath", Constant.Path.BASE_PATH);
        speechConfig.setProperty("SPEECH-SynthesisCachingMaxNumber", Constant.SpeechConfig.CACHING_MAX_NUMBER);
        // You can set a longer expired time for pre-generated caches as you can review the audio manually.
        //定义缓存最大内存
//        speechConfig.setProperty("SPEECH-SynthesisCachingMaxSizeOfOneItemInBytes", String.valueOf(1024 * 500));
        speechConfig.setProperty("SPEECH-SynthesisCachingExpiredDays", "365");
        //微软日志打印
//        Diagnostics.startConsoleLogging();


//        speechConfig.setProperty("SPEECH-ConnectionMaxIdleSeconds", "0");
//        speechConfig.setProperty("SPEECH-SynthesisCachingMaxNumber", "300");

        // 1.25接口变动
//        EmbeddedSpeechConfig embeddedSpeechConfig = EmbeddedSpeechConfig.fromPath(Constant.Path.OFFLINE_RES_PATH);
        //force_offline force_online parallel_buffer
        speechConfig.setProperty("SPEECH-SynthBackendSwitchingPolicy", "parallel_buffer");
        speechConfig.setProperty("SPEECH-SynthBackendFallbackBufferTimeoutMs", Constant.SpeechConfig.BUFFER_TIMEOUT_MS_COPY + "0");
        speechConfig.setProperty("SPEECH-SynthBackendFallbackBufferLengthMs", Constant.SpeechConfig.BUFFER_LENGTH_MS);
        synthesizer = new SpeechSynthesizer(speechConfig, null);
        connection = Connection.fromSpeechSynthesizer(synthesizer);
//        connection.openConnection(true);

//        SpeechConfig speechConfig = SpeechConfig.fromSubscription(Constant.McAccount.SPEECH_SUBSCRIPTION_KEY_S, Constant.McAccount.SERVICE_REGION_S);
//        // Use 24k Hz format for higher quality.
//        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Raw24Khz16BitMonoPcm);
//        // Set voice name.
//        speechConfig.setSpeechSynthesisVoiceName("en-US-AvaNeural");
//

        //todo:调试阶段暂不配置
//        //保存合成的音频数据(调试阶段使用)
//        speechConfig.setProperty(PropertyId.Speech_LogFilename, "/data/data/com.voyah.vcos.ttsservices/sdklog" + "/micSDKLog.log");
//        //todo:微软保存合成音频数据接口无效果
//        String SpeechSynthesisOutputPath = "/data/data/com.voyah.vcos.ttsservices/save/";
//        speechConfig.setProperty("SpeechSynthesisOutputPath", Constant.Path.DIR_WRITE + "save/");
////        10个约4M
//        speechConfig.setProperty("NumOfSavedFile", "30");


        synthesizer = new SpeechSynthesizer(speechConfig, null);
        connection = Connection.fromSpeechSynthesizer(synthesizer);
        //添加微软连接及合成监听
        setSynthesisListener(connection, synthesizer);
        LogUtils.i(TAG, "init finish");
        return true;
    }

    private void setSynthesisListener(Connection connection, SpeechSynthesizer synthesizer) {
//        Locale current = getResources().getConfiguration().locale;
        connection.connected.addEventListener((o, connectionEventArgs) -> {
            LogUtils.i(TAG, "connected type is " + type);
        });

        connection.disconnected.addEventListener((o, connectionEventArgs) -> {
            LogUtils.i(TAG, "disconnected type is " + type);
        });

        synthesizer.SynthesisStarted.addEventListener((o, speechSynthesisEventArgs) -> {
            String resultId = speechSynthesisEventArgs.getResult().getResultId();
            LogUtils.i(TAG, "SynthesisStarted  mTtsId is " + mTtsId + " , resultId is " + resultId + " ,type is " + type);
            MMKVHelper.getInstance().setTTSId(resultId, mTtsId);
            //todo:状态通知
            if (null != ttsResolverCallBack) {
                ttsResolverCallBack.onStatus(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_START, 0);
                BuriedPointManager.getInstance().setBuriedPointData(originTtsId, "", System.currentTimeMillis() + "", "", "", "");
            }
            speechSynthesisEventArgs.close();
        });

        synthesizer.Synthesizing.addEventListener((o, speechSynthesisEventArgs) -> {
            String resultId = speechSynthesisEventArgs.getResult().getResultId();
            String ttsId = MMKVHelper.getInstance().getTTSId(resultId, mTtsId);
            if (!isFirstParsing) {
                isFirstParsing = true;
                String backend = speechSynthesisEventArgs.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_SynthesisBackend);
//                String voice = speechSynthesisEventArgs.getResult().getProperties().getProperty(PropertyId.SpeechServiceConnection_SynthOfflineVoice);
                long receivedLength = speechSynthesisEventArgs.getResult().getAudioLength();
                LogUtils.e(TAG, "Synthesizing. resultId is " + resultId + "  receivedLength is  " + receivedLength + " bytes. ttsId " + ttsId + "  Finished by: " + backend + " ,mTtsId:" + mTtsId);
                //*存在旧任务stop之后无法立即停止，然后实例继续合成新的时，状态在同一个对象中回调
                if ((!TextUtils.isEmpty(ttsId) && !TextUtils.isEmpty(mTtsId) && TextUtils.equals(ttsId, mTtsId))) {
                    BuriedPointManager.getInstance().setBuriedPointData(originTtsId, "", "", "", backend, SpeakerManager.getInstance().getOffLineTtsSpeaker());
                    if (null != ttsResolverCallBack)
                        ttsResolverCallBack.onStatus(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVING, 0);
                }

            }
            //todo:音频数据下发
            byte[] audioByte = speechSynthesisEventArgs.getResult().getAudioData();
            receivedLength += audioByte.length;
//            SaveAudioUtils.saveByte(audioByte,"/data/data/com.voyah.ai.voice/micAudio/");
//            ttsResolverCallBack.onData(ttsId, audioByte);
            if (TextUtils.equals(ttsId, mTtsId))
                disposeBytes(audioByte, ttsId);
            speechSynthesisEventArgs.close();
        });

        synthesizer.SynthesisCanceled.addEventListener((o, speechSynthesisEventArgs) -> {
            String resultId = speechSynthesisEventArgs.getResult().getResultId();
            String ttsId = MMKVHelper.getInstance().getTTSId(resultId, mTtsId);
            String cancellationDetails = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisEventArgs.getResult()).toString();
            errorCode = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisEventArgs.getResult()).getErrorCode();
            LogUtils.e(TAG, "Error synthesizing. ttsId is " + ttsId + " ,mTtsId:" + mTtsId + " ,errorCode is " + errorCode + " ,Result ID: " + resultId +
                    ". Error detail: " + System.lineSeparator() + cancellationDetails + "  type : " + type);
            //*存在旧任务stop之后无法立即停止，然后实例继续合成新的时，状态在同一个对象中回调
            if (TextUtils.isEmpty(ttsId) || TextUtils.isEmpty(mTtsId) || !TextUtils.equals(ttsId, mTtsId))
                return;
            mTtsId = "";
            if (errorCode == CancellationErrorCode.NoError) {
                if (null != ttsResolverCallBack)
                    ttsResolverCallBack.onStatus(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD, 0);
                MMKVHelper.getInstance().remove(resultId);
//                checkSdkLogSize();
            } else if (!TextUtils.isEmpty(ttsId) && errorCode != CancellationErrorCode.ConnectionFailure && errorCode != CancellationErrorCode.ServiceTimeout) {
//            todo:状态通知
                mTtsId = "";
                if (null != ttsResolverCallBack)
                    ttsResolverCallBack.onStatus(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL, 0);
                MMKVHelper.getInstance().remove(resultId);
//                checkSdkLogSize();
            }

            if (!TextUtils.isEmpty(ttsId) && (errorCode == CancellationErrorCode.ConnectionFailure || errorCode == CancellationErrorCode.ServiceTimeout)) {
                //在线合成失败，转离线合成
                stopped = false;
                speakingRunnable.setRawSsml(ssml);
                singleThreadExecutor.execute(speakingRunnable);
            }
            speechSynthesisEventArgs.close();
        });

        synthesizer.WordBoundary.addEventListener((o, e) -> {
            wordBoundaries.add(e);
//            LogUtils.i(TAG, String.format("Word boundary. Text offset %d, length %d; audio offset %d ms.\n",
//                    e.getTextOffset(),
//                    e.getWordLength(),
//                    e.getAudioOffset() / 10000));
        });

        synthesizer.SynthesisCompleted.addEventListener((o, speechSynthesisEventArgs) -> {
            String resultId = speechSynthesisEventArgs.getResult().getResultId();
            String ttsId = MMKVHelper.getInstance().getTTSId(resultId, mTtsId);
            LogUtils.i(TAG, "SynthesisCompleted  resultId:" + resultId + " ,ttsId:" + ttsId);
            //微软耗时打印
            LogUtils.d(TAG, "First byte latency: " + speechSynthesisEventArgs.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_SynthesisFirstByteLatencyMs));
            LogUtils.d(TAG, "Server latency" + speechSynthesisEventArgs.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_SynthesisServiceLatencyMs));
            LogUtils.d(TAG, "Network latency" + speechSynthesisEventArgs.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_SynthesisNetworkLatencyMs));
            LogUtils.d(TAG, "Connection latency" + speechSynthesisEventArgs.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_SynthesisConnectionLatencyMs));
            if (TtsResolverManager.getInstance().isSaveMicrosoftMsg()) {
                SaveAudioUtils.saveByte(speechSynthesisEventArgs.getResult().getAudioData(), "/data/data/com.voyah.vcos.ttsservices/files/audio/copy" + System.currentTimeMillis() + ".pcm");
            }
//            stopped = true;
            if (!TextUtils.isEmpty(mTtsId) && !TextUtils.isEmpty(ttsId) && TextUtils.equals(ttsId, mTtsId)) {
                if (null != ttsResolverCallBack)
                    disposeBytes(defaultByte, ttsId);
//                    ttsResolverCallBack.onData(ttsId, completedByteArray, 0);
//                    ttsResolverCallBack.onStatus(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED, 0);
            }
//            if (TextUtils.equals(ttsId, mTtsId)) {
//                mTtsId = "";
//            }
//            checkSdkLogSize();
            MMKVHelper.getInstance().remove(resultId);
            wordBoundaries.clear();
            receivedLength = 0;
//            mTtsId = "";
            speechSynthesisEventArgs.close();
        });
    }

    @Override
    public void startSynthesis(PlayTTSBean playTTSBean) {

        //预连接
        if (synthesizer != null && connection == null) {
            LogUtils.d(TAG, "to preLink");
            connection = Connection.fromSpeechSynthesizer(synthesizer);
            connection.openConnection(true);
        }

        LogUtils.i(TAG, "ttsId is " + playTTSBean.getTtsId() + " ,ttsText is " + playTTSBean.getTts());
        isParsing = true;
        isFirstParsing = false;
        errorCode = CancellationErrorCode.NoError;
        stopped = false;
        wordBoundaries.clear();
        receivedLength = 0;
        String ttsText = playTTSBean.getTts();
        String language = playTTSBean.getLangType();
        String voiceName = "";
        String profileId = "";
        int voiceSex = -1;

        PlayTTSBean.VoiceCopyBean voiceCopyBean = playTTSBean.getVoiceCopyBean();
        if (null != voiceCopyBean) {
            voiceName = voiceCopyBean.getVoiceName();
            profileId = voiceCopyBean.getProfileId();
            voiceSex = voiceCopyBean.getVoiceSex();
        }

        this.mTtsId = playTTSBean.getTtsId();
        this.mUsage = playTTSBean.getUsage();
        this.originTtsId = playTTSBean.getOriginTtsId();
//        readFromLocal(ttsId, ttsText, type);


        //设置离线音色
        if (voiceSex == 0) {
            synthesizer.getProperties().setProperty(PropertyId.SpeechServiceConnection_SynthOfflineVoice, Constant.VoiceName.xiaoxiao);
        } else if (voiceSex == 1) {
            synthesizer.getProperties().setProperty(PropertyId.SpeechServiceConnection_SynthOfflineVoice, Constant.VoiceName.yunxi);
        } else {
            synthesizer.getProperties().setProperty(PropertyId.SpeechServiceConnection_SynthOfflineVoice, Constant.VoiceName.xiaoxiao);
        }

        if (TextUtils.isEmpty(ttsText)) {
            LogUtils.d(TAG, "params is wrong");
            ttsResolverCallBack.onStatus(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL, 0);
        } else {
            synthesizer.getProperties().setProperty("SPEECH-SynthBackendSwitchingPolicy", "parallel_buffer");
            synthesizer.getProperties().setProperty("SPEECH-SynthDisableCaching", String.valueOf(TextUtils.equals(playTTSBean.getPackageName(), BinderList.MAP)));
            String ssmlPattern;
            if (TextUtils.isEmpty(profileId)) {
                ssmlPattern = SSML_PATTERN.replace("language", language);
            } else {
                ssmlPattern = SSML_PATTERN.replace("language", language)
                        .replace("profileId", profileId);
            }

            ttsText = SsmlUtils.replaceCopySpecialString(ttsText);
            ssml = String.format(ssmlPattern, TextUtils.htmlEncode(SsmlUtils.ensureEndsWithPunctuation(ttsText)));

            LogUtils.i(TAG, "ssml is " + ssml);
            //todo:设置就近交互
            ProximityInteractionManager.getInstance().setNearbyTtsPosition(playTTSBean.getSoundLocation(), playTTSBean.getPackageName(), playTTSBean.getUsage());
            synthesizer.StartSpeakingSsmlAsync(ssml);
        }
    }

//    private void readFromLocal(String ttsId, String ttsText, int type) {
//        //添加路径+文件名+后缀
//        decodeRunnable = new DecodeRunnable(ttsId, "path", type);
//        executorService.execute(decodeRunnable);
//    }

//    private class DecodeRunnable implements Runnable {
//        volatile String ttsId;
//        volatile boolean isParsing = false;
//        String path;
//        int type;
//
//        void stop() {
//            isParsing = false;
//            ttsId = null;
////            ttsResolverCallback.onStatus(ttsId, RESOLVER_FAIL);
//        }
//
//        DecodeRunnable(String ttsId, String path, int type) {
//            this.ttsId = ttsId;
//            this.path = path;
//            this.type = type;
//        }
//
//        @Override
//        public void run() {
//            FileInputStream pcmFile = null;
////            onParsing(ttsId);
//            try {
//                LogUtils.i(TAG, path);
//                pcmFile = new FileInputStream(new File(path));
//                isParsing = true;
//
//                byte[] putBuf = new byte[pcmFile.available()];
//                pcmFile.read(putBuf);
////                disposeBytes(putBuf, ttsId);
//                ttsResolverCallBack.onData(ttsId, putBuf);
//                pcmFile.close();
//
////                disposeCompleted(ttsId);
////                onSuccess(ttsId);
//                stop();
//            } catch (Exception e) {
//                e.printStackTrace();
//                LogUtils.e(TAG, "解析失败 --- " + type, e);
////                onFail(ttsId);
//                stop();
//            } finally {
//                try {
//                    if (null != pcmFile) {
//                        pcmFile.close();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }

    private void synthesis() {
        //todo:1.检查下当前系统设置的音色与合成器保存音色是否一致 2.拼接ssml(语种、情感) 3.发起合成
        isFirstParsing = false;
    }

    @Override
    public void stopSynthesis() {
        if (isParsing) {
            stopped = true;
            synthesizer.StopSpeakingAsync();
            ttsResolverCallBack.onStatus(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD, 0);
            mTtsId = "";
        }
    }

    @Override
    public void reset() {
//        ttsId = "";
        isParsing = false;
        isFirstParsing = false;
        wordBoundaries.clear();
        receivedLength = 0;
    }

    @Override
    public void setTtsResolverCallBack(ITtsResolverCallBack ttsResolverCallBack) {
        this.ttsResolverCallBack = ttsResolverCallBack;
    }

    @Override
    public int getNum() {
        return num;
    }

    @Override
    public void playEnd(String ttsId, int ttsType) {
        if (TextUtils.equals(ttsId, mTtsId)) {
            mTtsId = "";
        }
        ttsResolverCallBack.onStatus(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED, 0);
    }

    private void disposeBytes(byte[] bytes, String ttsId) {
        if (stopped)
            return;
        if (bytes.length == 0)
            ttsResolverCallBack.onData(ttsId, completedByteArray, 1);
        else {
            int count = bytes.length / ByteArr.LEN_PER + (bytes.length % ByteArr.LEN_PER == 0 ? 0 : 1);
            for (int i = 0; i < count; i++) {
                ByteArrMgr.ByteArrObj obj = TtsByteArrMgr.getInstance().getByteObj(mUsage);
                if (obj.len > 0) {
                    obj.reset();
                }
                int len = bytes.length - i * ByteArr.LEN_PER;
                if (len > ByteArr.LEN_PER) {
                    len = ByteArr.LEN_PER;
                }
                System.arraycopy(bytes, i * ByteArr.LEN_PER, obj.arr, 0, len);
                obj.len = len;
                if (!stopped)
                    ttsResolverCallBack.onData(ttsId, obj, 0);
            }
        }
    }

    //在线转离线重试
    class SpeakingRunnable implements Runnable {
        private String rawSsml;

        public void setRawSsml(String ssml) {
            this.rawSsml = ssml;
        }

        @Override
        public void run() {
            try {
                synchronized (synchronizedObj) {
                    stopped = false;
                }
                LogUtils.i(TAG, "stopped is " + stopped);
//                if (!stopped && retryOnTimeout) {
                if (!stopped) {
                    if ((errorCode == CancellationErrorCode.ConnectionFailure || errorCode == CancellationErrorCode.ServiceTimeout)) {
                        long duration = receivedLength / 48; // milliseconds for 24kHz
                        long textStart = wordBoundaries.get(0).getTextOffset();
                        long textEnd = wordBoundaries.get(0).getTextOffset();
                        LogUtils.d(TAG, "SpeakingRunnable duration:" + duration + " ,textStart:" + textStart + " ,textEnd:" + textEnd + " ,receivedLength:" + receivedLength);
                        for (SpeechSynthesisWordBoundaryEventArgs e : wordBoundaries) {
                            long currentWordOffset = e.getAudioOffset() / 10 / 1000;
                            long currentWordDuration = e.getDuration() / 10 / 1000;
                            if (currentWordOffset + currentWordDuration > duration) {
                                if (e.getText().matches("^[\\u4e00-\\u9fa5]+$")) { // if Chinese
                                    if (currentWordOffset > duration) {
                                        textEnd = e.getTextOffset();
                                    } else {
                                        textEnd = e.getTextOffset() + Math.round((double) (duration - currentWordOffset) / currentWordDuration * e.getWordLength());
                                    }
                                } else { // if is not Chinese
                                    textEnd = e.getTextOffset();
                                }
                                break;
                            }
                            textEnd = e.getTextOffset() + e.getWordLength();
                        }

                        String newSsml = ssml.substring(0, (int) textStart) + ssml.substring((int) textEnd);
                        LogUtils.d(TAG, "SpeakingRunnable newSsml:" + newSsml);
                        synthesizer.getProperties().setProperty("SPEECH-SynthBackendSwitchingPolicy", "force_offline");
                        synthesizer.StartSpeakingSsmlAsync(newSsml).get();
                        wordBoundaries.clear();
                        receivedLength = 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ttsResolverCallBack.onStatus(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL, 0);
//                ttsResolverCallBack.onSyncError(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL, "wordBoundaries error", 0);
            }
        }
    }

//    private void checkSdkLogSize() {
//        if (checkHandler == null) {
//            return;
//        }
//        checkHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                File file = new File(Constant.Path.DIR_WRITE);
//                long fileSize = file.length();
//                BufferedWriter bwMSG11 = null;
//                if (file.exists() && fileSize >= TTS_SDK_LOG_SIZE) {
//                    LogUtils.i(TAG, "dumpLog: ttsLogSize is " + fileSize);
//                    try {
//                        bwMSG11 = new BufferedWriter(new FileWriter(Constant.Path.DIR_WRITE));
//                        bwMSG11.write("");//清空
//                        bwMSG11.flush();
//                        bwMSG11.close();
//                        bwMSG11 = null;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        try {
//                            if (null != bwMSG11)
//                                bwMSG11.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        });
//    }

}
