package com.voyah.vcos.ttsservices.copymicrosoft;

import android.media.AudioAttributes;
import android.text.TextUtils;
import android.util.Log;

import com.microsoft.cognitiveservices.speech.CancellationErrorCode;
import com.microsoft.cognitiveservices.speech.Connection;
import com.microsoft.cognitiveservices.speech.EmbeddedSpeechConfig;
import com.microsoft.cognitiveservices.speech.PropertyId;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisWordBoundaryEventArgs;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.voyah.vcos.ttsservices.Constant;
import com.voyah.vcos.ttsservices.buriedpoint.BuriedPointManager;
import com.voyah.vcos.ttsservices.info.PlayTTSBean;
import com.voyah.vcos.ttsservices.manager.ITtsResolverCallBack;
import com.voyah.vcos.ttsservices.manager.SpeakerManager;
import com.voyah.vcos.ttsservices.manager.near.ProximityInteractionManager;
import com.voyah.vcos.ttsservices.manager.stream.IStreamTtsResolver;
import com.voyah.vcos.ttsservices.mem.ByteArr;
import com.voyah.vcos.ttsservices.mem.ByteArrMgr;
import com.voyah.vcos.ttsservices.mem.TtsByteArrMgr;
import com.voyah.vcos.ttsservices.utils.LogUtils;
import com.voyah.vcos.ttsservices.utils.MMKVHelper;
import com.voyah.vcos.ttsservices.utils.SsmlUtils;
import com.voyah.vcos.ttsservices.utils.Util;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author:lcy 声音复刻
 * @data:2024/5/21
 **/
public class CopyStreamTtsResolver implements IStreamTtsResolver {
    private static final String TAG = "CopyStreamTtsResolver";
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

    private static final String SSML_END = "</voice></speak>";
    private SpeechSynthesizer synthesizer;
    private Connection connection;

    private ITtsResolverCallBack ttsResolverCallBack;

    private int type = AudioAttributes.CONTENT_TYPE_SPEECH;
    //    private String ttsId;
    private String mTtsId;
    private int mUsage;

    private String originTtsId;
    private boolean isParsing;
    private boolean isSynthesizing;
    private boolean isFirstParsing;
    //    private boolean retryOnTimeout = true;//发起合成时设置为false 开始合成、stop后设置为true
    private final ArrayList<SpeechSynthesisWordBoundaryEventArgs> wordBoundaries = new ArrayList<>(); //todo:合成完成、stop、新的开始时清空

    private long receivedLength = 0;
    private final Object synchronizedObj = new Object();
    private boolean stopped = false;

    private boolean isStreamBegin; //流式合成请求开始
    private boolean isStreamFirst = true; //首次发起流式合成
    private boolean isStreamEnd; //流式合成请求是否完毕

    private String appendString = ""; //拼接的流式请求tts

//    private StringBuilder appendBuild;

    private String symbols = ",，。!?！？；;、";

    //    private int streamStatus; // 0:流式合成请求开始    1:流式合成请求中   2:流式合成请求是否完毕
    private CancellationErrorCode errorCode = CancellationErrorCode.NoError;
    private SpeakingRunnable speakingRunnable;
    private ExecutorService singleThreadExecutor;
    private String ssml;
    private String textSsml;

    private boolean dumpAudio;
    private boolean dumpLog;

    //--------------复刻使用参数
    private String voiceName;
    private String profileId;
    private int voiceSex = -1;
    private String language;

    private volatile String currentOffLineSpeakerName;

    private ByteArrMgr.ByteArrObj completedByteArray = new ByteArrMgr.ByteArrObj(0);
    private byte[] defaultByte = new byte[0];

//    private DecodeRunnable decodeRunnable;

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    public CopyStreamTtsResolver(boolean isDumpAudio, boolean isDumpLog) {
        this.dumpAudio = isDumpAudio;
        this.dumpLog = isDumpLog;
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        speakingRunnable = new SpeakingRunnable();
        init();
    }


    private void init() {
        while (!initMicroTTS()) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean initMicroTTS() {
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
//        speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthEnableCompressedAudioTransmission, "true");

        //获取当前音色
        String speakerName = SpeakerManager.getInstance().getOffLineTtsSpeaker();
        LogUtils.i(TAG, "speakerName:" + speakerName);
        currentOffLineSpeakerName = speakerName;
        speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthOfflineVoice, speakerName);

        //设置微软cache缓存路径及上限个数-500个约81M
//        speechConfig.setProperty("SPEECH-SynthesisCachingPath", Constant.Path.DIR_CACHE_WRITE);
//        speechConfig.setProperty("SPEECH-SynthesisCachingMaxNumber", Constant.SpeechConfig.CACHING_MAX_NUMBER);

//        speechConfig.setProperty("SPEECH-ConnectionMaxIdleSeconds", "0");
//        speechConfig.setProperty("SPEECH-SynthesisCachingMaxNumber", "300");

        // 1.25接口变动
//        EmbeddedSpeechConfig embeddedSpeechConfig = EmbeddedSpeechConfig.fromPath(Constant.Path.OFFLINE_RES_PATH);
        //force_offline force_online parallel_buffer
        speechConfig.setProperty("SPEECH-SynthBackendSwitchingPolicy", "parallel_buffer");
//        speechConfig.setProperty("SPEECH-SynthBackendSwitchingPolicy", "force_online");
        speechConfig.setProperty("SPEECH-SynthBackendFallbackBufferTimeoutMs", Constant.SpeechConfig.BUFFER_TIMEOUT_MS_COPY);
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
//        speechConfig.setProperty("SpeechSynthesisOutputPath", SpeechSynthesisOutputPath);
        //10个约4M
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
            MMKVHelper.getInstance().setTTSId(resultId, mTtsId);
            LogUtils.i(TAG, "SynthesisStarted  mTtsId is " + mTtsId + " , resultId is " + resultId + " ,type is " + type);
            //todo:流式只在开始通知一遍-修改状态通知位置
//            if (null != ttsResolverCallBack) {
//                ttsResolverCallBack.onStatus(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_START);
//            }
            speechSynthesisEventArgs.close();
        });

        synthesizer.Synthesizing.addEventListener((o, speechSynthesisEventArgs) -> {
            String resultId = speechSynthesisEventArgs.getResult().getResultId();
            String ttsId = MMKVHelper.getInstance().getTTSId(resultId, mTtsId);
            if (!isFirstParsing) {
                isFirstParsing = true;
//                ttsResolverCallBack.onStatus(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVING);
                String backend = speechSynthesisEventArgs.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_SynthesisBackend);
                String voice = speechSynthesisEventArgs.getResult().getProperties().getProperty(PropertyId.SpeechServiceConnection_SynthOfflineVoice);
                long receivedLength = speechSynthesisEventArgs.getResult().getAudioLength();
                LogUtils.e(TAG, "Synthesizing. resultId is " + resultId + "  receivedLength is  " + receivedLength + " bytes. ttsId " + ttsId + "  Finished by: " + backend + " ,mTtsId:" + mTtsId);
                if ((!TextUtils.isEmpty(ttsId) && !TextUtils.isEmpty(mTtsId) && TextUtils.equals(ttsId, mTtsId))) {
                    BuriedPointManager.getInstance().setBuriedPointData(originTtsId, "", "", "", backend, SpeakerManager.getInstance().getOffLineTtsSpeaker());
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
            if (errorCode == CancellationErrorCode.NoError) {
                if (TextUtils.equals(ttsId, mTtsId)) {
                    ttsResolverCallBack.onStatus(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD, 1);
                    MMKVHelper.getInstance().remove(resultId);
                    mTtsId = "";
                }
            } else if (!TextUtils.isEmpty(ttsId) && errorCode != CancellationErrorCode.ConnectionFailure && errorCode != CancellationErrorCode.ServiceTimeout) {
////            todo:状态通知
//                ttsResolverCallBack.onStatus(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL);
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
            //todo:状态通知
//            SaveAudioUtils.saveByte(speechSynthesisEventArgs.getResult().getAudioData(),"/data/data/com.voyah.vcos.ttsservices/audio/aa.pcm");
            LogUtils.d(TAG, "isStreamEnd is " + isStreamEnd + " ,appendString is " + appendString);

//            isParsing = false;
            isSynthesizing = false;
            wordBoundaries.clear();
            receivedLength = 0;
            if (isStreamEnd && TextUtils.isEmpty(appendString)) {
                if (TextUtils.equals(ttsId, mTtsId)) {
//                    mTtsId = "";
//                    ttsResolverCallBack.onStatus(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED, 1);
//                    ttsResolverCallBack.onData(ttsId, completedByteArray, 1);
                    disposeBytes(defaultByte, ttsId);
//                    isParsing = false;
                    MMKVHelper.getInstance().remove(resultId);
                }
            } else
                append(null);

            speechSynthesisEventArgs.close();
        });
    }


    private void startSynthesis(String ttsId, String ttsText) {
        LogUtils.i(TAG, "ttsId is " + ttsId + " ,ttsText is " + ttsText);
        isParsing = true;
        isSynthesizing = true;
        isFirstParsing = false;
        errorCode = CancellationErrorCode.NoError;
        stopped = false;
//        readFromLocal(ttsId, ttsText, type);

        if (voiceSex == 0) {
            synthesizer.getProperties().setProperty(PropertyId.SpeechServiceConnection_SynthOfflineVoice, Constant.VoiceName.xiaoxiao);
        } else if (voiceSex == 1) {
            synthesizer.getProperties().setProperty(PropertyId.SpeechServiceConnection_SynthOfflineVoice, Constant.VoiceName.yunxi);
        } else {
            synthesizer.getProperties().setProperty(PropertyId.SpeechServiceConnection_SynthOfflineVoice, Constant.VoiceName.xiaoxiao);
        }

//        if (TextUtils.isEmpty(ttsText)) {
//            ttsResolverCallBack.onSyncError(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL, "params is wrong", 1);
//        } else {
        String ssmlPattern;
        if (TextUtils.isEmpty(profileId)) {
            ssmlPattern = SSML_PATTERN.replace("language", language);
        } else {
            ssmlPattern = SSML_PATTERN.replace("language", language)
                    .replace("profileId", profileId);
        }
        ttsText = SsmlUtils.ensureEndsWithPunctuation(ttsText);
        ttsText = SsmlUtils.replaceCopySpecialString(ttsText);
        ssml = String.format(ssmlPattern, TextUtils.htmlEncode(ttsText));
        textSsml = ttsText;
        LogUtils.i(TAG, "ssml is " + ssml);
        synthesizer.StartSpeakingSsmlAsync(ssml);
//        }
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
    public void beginStream(PlayTTSBean voiceTTSBean) {
        //预连接
        if (synthesizer != null && connection == null) {
            connection = Connection.fromSpeechSynthesizer(synthesizer);
            connection.openConnection(true);
        }

        //设置全局复刻使用变量
        LogUtils.i(TAG, "beginStream");
        //设置混合模式合成
        synthesizer.getProperties().setProperty("SPEECH-SynthBackendSwitchingPolicy", "parallel_buffer");
        appendString = "";
        mTtsId = "";
        voiceName = "";
        profileId = "";
        voiceSex = -1;
        stopped = false;
        wordBoundaries.clear();
        receivedLength = 0;
        isStreamBegin = true;
        isStreamEnd = false;
        isStreamFirst = true;
        if (null != voiceTTSBean) {
            mTtsId = voiceTTSBean.getTtsId();
            mUsage = voiceTTSBean.getUsage();
            originTtsId = voiceTTSBean.getTtsId();
            language = voiceTTSBean.getLangType();
        }
        if (null != voiceTTSBean && null != voiceTTSBean.getVoiceCopyBean()) {
            voiceName = voiceTTSBean.getVoiceCopyBean().getVoiceName();
            profileId = voiceTTSBean.getVoiceCopyBean().getProfileId();
            voiceSex = voiceTTSBean.getVoiceCopyBean().getVoiceSex();
        }
        ttsResolverCallBack.onStatus(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_START, 1);
        BuriedPointManager.getInstance().setBuriedPointData(originTtsId, "", System.currentTimeMillis() + "", "", "", "");
        if (null != voiceTTSBean)
            ProximityInteractionManager.getInstance().setNearbyTtsPosition(voiceTTSBean.getSoundLocation(), voiceTTSBean.getPackageName(),voiceTTSBean.getUsage());
    }

    @Override
    public void endStream() {
        LogUtils.i(TAG, "endStream");
        isStreamBegin = false;
        isStreamEnd = true;
    }

    @Override
    public synchronized void append(PlayTTSBean voiceTtsBean) {
//        if (null == voiceTtsBean) {
//            LogUtils.i(TAG, " append voiceTtsBean is null");
//            return;
//        }
        LogUtils.i(TAG, "append stopped is " + stopped);
        if (stopped)
            return;

        //字符串简单拼接
        if (null != voiceTtsBean && !TextUtils.isEmpty(voiceTtsBean.getTts()))
            appendString += voiceTtsBean.getTts();
        else
            appendString += "";

        //根据字符串中是否包含断句符号判断流式是否可发起第一次合成
        boolean isCanDoFirst = getLastIndex() != -1;
//        LogUtils.i(TAG, "appendString is " + appendString + " ,isCanDoFirst is " + isCanDoFirst + " ,ttsId is " + mTtsId);
        //流式请求还未发起过合成请求
        if (isStreamBegin && isStreamFirst && isCanDoFirst) {
            LogUtils.i(TAG, "start first stream " + mTtsId);
            isStreamFirst = false;

            String playTts = getPulateString();
            if (!TextUtils.isEmpty(playTts)) {
                startSynthesis(mTtsId, playTts);
                if (null != ttsResolverCallBack) {
                    ttsResolverCallBack.onStatus(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVING, 1);
                }
            }
        } else if (!isStreamFirst && !isSynthesizing && isCanDoFirst) { //合成中途
            LogUtils.i(TAG, "streaming " + mTtsId);
            String playTts = getPulateString();
            if (!TextUtils.isEmpty(playTts))
                startSynthesis(mTtsId, playTts);
        } else if (!isStreamFirst && isStreamEnd && !isSynthesizing) { //最后一帧
            LogUtils.i(TAG, "start last stream " + mTtsId);
            if (!TextUtils.isEmpty(appendString))
                startSynthesis(mTtsId, appendString);
            else
                ttsResolverCallBack.onStatus(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED, 1);
//                disposeBytes(defaultByte, mTtsId);
            appendString = "";
        } else if (isStreamEnd && isStreamFirst) {
            //垃圾一样的数据，全程没有标点
            startSynthesis(mTtsId, appendString);
            isStreamFirst = false;
            appendString = "";
        }
//        LogUtils.e(TAG, "appendString is " + appendString);
    }

    @Override
    public void stopSynthesis() {
        LogUtils.i(TAG, "stopStreamSynthesis isParsing is " + isParsing + " ,mTtsId is " + mTtsId);
        if (isParsing) {
            synthesizer.StopSpeakingAsync();
            if (isParsing || !TextUtils.isEmpty(mTtsId)) {
                ttsResolverCallBack.onStatus(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD, 1);
                stopped = true;
                mTtsId = "";
                appendString = "";
            }
            isParsing = false;
        }
    }

    @Override
    public void reset() {
//        type = -10000;
//        ttsId = "";
//        isParsing = false;
//        isFirstParsing = false;
    }

    @Override
    public void setTtsResolverCallBack(ITtsResolverCallBack ttsResolverCallBack) {
        this.ttsResolverCallBack = ttsResolverCallBack;
    }

    @Override
    public void playEnd(String ttsId, int ttsType) {
        if (TextUtils.equals(ttsId, mTtsId)) {
            mTtsId = "";
            isParsing = false;
        }
        ttsResolverCallBack.onStatus(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED, ttsType);
    }

    private void disposeBytes(byte[] bytes, String ttsId) {
//        LogUtils.i(TAG, "mTtsId is " + mTtsId + " ,ttsId is " + ttsId);
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
                    ttsResolverCallBack.onData(ttsId, obj, 1);
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

                        //todo:合成完成后断网会出现回调timeout，此时获取到的newSsml中不包含带合成的tts，只有转换后的ssml
                        String surplusTtsString = extractTextBetweenTags(newSsml);
                        LogUtils.i(TAG, "retry newSsml: " + newSsml + " ,surplusTtsString is " + surplusTtsString);

                        //todo:续合成时检查append中是否有可拼接字符串
                        int lastIndex = getLastIndex();
                        String appendStr = lastIndex != -1 ? getPulateString() : "";
                        if (!TextUtils.isEmpty(appendStr))
                            newSsml = newSsml.replace(surplusTtsString + SSML_END, surplusTtsString + appendStr + SSML_END);
                        LogUtils.i(TAG, "retry newSsml-append: " + newSsml);
                        //强制转离线合成(后续全部强制离线)
                        synthesizer.getProperties().setProperty("SPEECH-SynthBackendSwitchingPolicy", "force_offline");
                        synthesizer.StartSpeakingSsmlAsync(newSsml);
                        wordBoundaries.clear();
                        receivedLength = 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                append(null);
            }
        }
    }

    //获取满足条件的文本信息
    private String extractTextBetweenTags(String originalText) {
        String startTag = "rate='5%'>";
        String endTag = "</voice>";
        String patternString = Pattern.quote(startTag) + "(.*?)" + Pattern.quote(endTag);
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(originalText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }


    private int getLastIndex() {
        // 找到最后一个符号的位置
        int lastIndex = -1;
        for (int i = appendString.length() - 1; i >= 0; i--) {
//            LogUtils.i(TAG, "appendString.charAt(i) is " + appendString.charAt(i));
//            LogUtils.i(TAG, "contains is " + symbols.contains(String.valueOf(appendString.charAt(i))));
            if (symbols.contains(String.valueOf(appendString.charAt(i)))) {
                lastIndex = i;
                break;
            }
        }
        return lastIndex;
    }

    private String getPulateString() {
        // 找到最后一个符号的位置
        int lastIndex = getLastIndex();
        String toPlayTts = "";
        LogUtils.i(TAG, "lastIndex is " + lastIndex);
        if (lastIndex != -1) {
            // 获取从符号到开头的字符
            toPlayTts = appendString.substring(0, lastIndex + 1);
            // 修改原字符串为截取后的部分
            appendString = appendString.substring(lastIndex + 1);
            LogUtils.i(TAG, "toPlayTts is " + toPlayTts + " ,appendString is " + appendString);
        }
        return toPlayTts;
    }

}
