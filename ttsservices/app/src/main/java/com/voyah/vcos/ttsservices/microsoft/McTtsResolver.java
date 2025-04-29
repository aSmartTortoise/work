package com.voyah.vcos.ttsservices.microsoft;

import android.media.AudioAttributes;
import android.os.Handler;
import android.os.HandlerThread;
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
import com.voyah.vcos.ttsservices.BinderList;
import com.voyah.vcos.ttsservices.Constant;
import com.voyah.vcos.ttsservices.buriedpoint.BuriedPointManager;
import com.voyah.vcos.ttsservices.info.PlayTTSBean;
import com.voyah.vcos.ttsservices.manager.AudioAheadProcessor;
import com.voyah.vcos.ttsservices.manager.ITtsResolver;
import com.voyah.vcos.ttsservices.manager.ITtsResolverCallBack;
import com.voyah.vcos.ttsservices.manager.SpeakerManager;
import com.voyah.vcos.ttsservices.manager.TtsResolverManager;
import com.voyah.vcos.ttsservices.manager.near.ProximityInteractionManager;
import com.voyah.vcos.ttsservices.mem.ByteArrMgr;
import com.voyah.vcos.ttsservices.utils.FileUtils;
import com.voyah.vcos.ttsservices.utils.SsmlUtils;
import com.voyah.vcos.ttsservices.utils.LogUtils;
import com.voyah.vcos.ttsservices.utils.MMKVHelper;
import com.voyah.vcos.ttsservices.utils.SaveAudioUtils;
import com.voyah.vcos.ttsservices.utils.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author:lcy
 * @data:2024/1/30
 **/
//todo:添加微软日志保存
public class McTtsResolver implements ITtsResolver {
    private String TAG = "McTtsResolver";
    private static final String SSML_PATTERN = "<speak xmlns='http://www.w3.org/2001/10/synthesis' xmlns:mstts='http://www.w3.org/2001/mstts' xmlns:emo='http://www.w3.org/2009/10/emotionml' version='1.0' xml:lang='language'><voice name='speaker' leadingsilence-exact=\"0ms\" style='emotionStyle' rate='playRate%%'>%s</voice></speak>";

    private volatile String currentOffLineSpeakerName;

    private SpeechSynthesizer synthesizer;

    //    private SpeechSynthesisResult result;
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
    private final Object synchronizedObj = new Object();


    private boolean stopped = false;
    private CancellationErrorCode errorCode = CancellationErrorCode.NoError;
    private SpeakingRunnable speakingRunnable;
    private ExecutorService singleThreadExecutor;
    private String ssml;
    private DecodeRunnable decodeRunnable;

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    private Handler checkHandler;
    private HandlerThread checkHandlerThread;

    private String tts; //debug模式，保存到本地音频名

    private long receivedLength = 0;

    //    private ByteArrMgr.ByteArrObj completedByteArray = new ByteArrMgr.ByteArrObj(0);
    private byte[] defaultByte = new byte[0];

    private AudioAheadProcessor audioAheadProcessor;


    public McTtsResolver(boolean isDumpAudio, boolean isDumpLog, int num) {
        this.num = num;
        TAG = TAG + "-" + num;
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        speakingRunnable = new SpeakingRunnable();
        initAudioAheadProcess(TAG);
        init(num);
        initHandler();
    }

    private void initAudioAheadProcess(String tag) {
        audioAheadProcessor = new AudioAheadProcessor(tag);
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
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(Constant.McAccount.SPEECH_SUBSCRIPTION_KEY_S, Constant.McAccount.SERVICE_REGION_S);
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

//         In SDK version 1.34.0 and later, the default setting for compression format in transmission on Linux and Android changes to false.
//         So please add this line to enable compression format in transmission if needed, especially while using pcm output format;
//        修改传输格式为mp3
//        speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthEnableCompressedAudioTransmission, "true");

        //保存微软日志
//        speechConfig.setProperty(PropertyId.Speech_LogFilename, Constant.Path.DIR_WRITE);

        //获取当前音色
        String speakerName = SpeakerManager.getInstance().getOffLineTtsSpeaker();
        LogUtils.i(TAG, "speakerName:" + speakerName);
        currentOffLineSpeakerName = speakerName;
        speechConfig.setProperty(PropertyId.SpeechServiceConnection_SynthOfflineVoice, speakerName);
        //设置微软cache缓存路径及上限个数-500个约81M
        speechConfig.setProperty("SPEECH-SynthesisCachingPath", Constant.Path.BASE_PATH);
        speechConfig.setProperty("SPEECH-SynthesisCachingMaxNumber", Constant.SpeechConfig.CACHING_MAX_NUMBER);
        //定义缓存最大内存
//        speechConfig.setProperty("SPEECH-SynthesisCachingMaxSizeOfOneItemInBytes", String.valueOf(500 * 1024));
        // You can set a longer expired time for pre-generated caches as you can review the audio manually.
        speechConfig.setProperty("SPEECH-SynthesisCachingExpiredDays", "365");
        //微软日志打印
//        Diagnostics.startConsoleLogging();


//        speechConfig.setProperty("SPEECH-ConnectionMaxIdleSeconds", "0");
//        speechConfig.setProperty("SPEECH-SynthesisCachingMaxNumber", "300");

        // 1.25接口变动
//        EmbeddedSpeechConfig embeddedSpeechConfig = EmbeddedSpeechConfig.fromPath(Constant.Path.OFFLINE_RES_PATH);
        //force_offline force_online parallel_buffer
        speechConfig.setProperty("SPEECH-SynthBackendSwitchingPolicy", "parallel_buffer");
        speechConfig.setProperty("SPEECH-SynthBackendFallbackBufferTimeoutMs", Constant.SpeechConfig.BUFFER_TIMEOUT_MS);
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
        if (TtsResolverManager.getInstance().isDumpMicrosoftLog()) {
            FileUtils.fileChecker(Constant.Path.SDK_LOG_PATH);
            checkSdkLogSize();
            speechConfig.setProperty(PropertyId.Speech_LogFilename, Constant.Path.SDK_LOG_PATH);
        }
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
//            checkSdkLogSize();
            onStatusCallBack(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_START, 0);
            BuriedPointManager.getInstance().setBuriedPointData(originTtsId, "", System.currentTimeMillis() + "", "", "", SpeakerManager.getInstance().getOffLineTtsSpeaker());
            speechSynthesisEventArgs.close();
        });

        synthesizer.Synthesizing.addEventListener((o, speechSynthesisEventArgs) -> {
            String resultId = speechSynthesisEventArgs.getResult().getResultId();
            String ttsId = MMKVHelper.getInstance().getTTSId(resultId, mTtsId);
            if (!isFirstParsing) {
                isFirstParsing = true;
                String backend = speechSynthesisEventArgs.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_SynthesisBackend);
                long receivedLength = speechSynthesisEventArgs.getResult().getAudioLength();
                LogUtils.e(TAG, "Synthesizing. resultId is " + resultId + "  receivedLength is  " + receivedLength + " bytes. ttsId " + ttsId + "  Finished by: " + backend + " ,mTtsId:" + mTtsId);
                audioAheadProcessor.startTask(ttsId);
                //*存在旧任务stop之后无法立即停止，然后实例继续合成新的时，状态在同一个对象中回调
                if ((!TextUtils.isEmpty(ttsId) && !TextUtils.isEmpty(mTtsId) && TextUtils.equals(ttsId, mTtsId))) {
                    BuriedPointManager.getInstance().setBuriedPointData(originTtsId, "", "", "", backend, SpeakerManager.getInstance().getOffLineTtsSpeaker());
                    onStatusCallBack(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVING, 0);
                }
            }
            //todo:音频数据下发
            byte[] audioByte = speechSynthesisEventArgs.getResult().getAudioData();
            receivedLength += audioByte.length;
//            SaveAudioUtils.saveByte(audioByte,"/data/data/com.voyah.ai.voice/micAudio/");
//            ttsResolverCallBack.onData(ttsId, audioByte);
            if (TextUtils.equals(ttsId, mTtsId))
                audioAheadProcessor.processAudioDataDelay(audioByte, ttsId, resultId, mUsage);
//                disposeBytes(audioByte, ttsId, resultId);
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
                audioAheadProcessor.stopTask();
                onStatusCallBack(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD, 0);
                MMKVHelper.getInstance().remove(resultId);
                mTtsId = "";
                if (decodeRunnable != null)
                    decodeRunnable.isParsing = false;
                checkSdkLogSize();
            } else if (!TextUtils.isEmpty(ttsId) && errorCode != CancellationErrorCode.ConnectionFailure && errorCode != CancellationErrorCode.ServiceTimeout) {
//            todo:状态通知
                mTtsId = "";
                onStatusCallBack(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL, 0);
                audioAheadProcessor.stopTask();
                MMKVHelper.getInstance().remove(resultId);
                checkSdkLogSize();
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
            String firstByteLatencyMs = speechSynthesisEventArgs.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_SynthesisFirstByteLatencyMs) + " ms";
            String underRunTimeMs = speechSynthesisEventArgs.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_SynthesisUnderrunTimeMs);
            String backend = speechSynthesisEventArgs.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_SynthesisBackend);
            LogUtils.i(TAG, "SynthesisCompleted  resultId:" + resultId + " ,ttsId:" + ttsId + " ,firstByteLatencyMs:" + firstByteLatencyMs + " ,underRunTimeMs:" + underRunTimeMs + " ,backend:" + backend);
            //todo:状态通知
            if (TtsResolverManager.getInstance().isSaveMicrosoftMsg()) {
                SaveAudioUtils.saveByte(speechSynthesisEventArgs.getResult().getAudioData(), Constant.Path.BASE_PATH + "audio/" + tts);
            }
//            stopped = true;
            //基于待播放音频写入线程修改优先级会导致多线程状态无法同步，在接收到completed状态回调时不直接返回状态，
            // 改为再写入一帧长度为0的数据作为结束标识，然后在audioTrack中返回实际播报完成结果(底层没有实际播报完成的状态回调，经测试write与播报进度基本一致)
            if (!TextUtils.isEmpty(mTtsId) && !TextUtils.isEmpty(ttsId) && TextUtils.equals(ttsId, mTtsId))
                audioAheadProcessor.processAudioDataDelay(defaultByte, ttsId, resultId, mUsage);
            audioAheadProcessor.markAudioComplete(ttsId, resultId, mUsage);
//                onData(ttsId, completedByteArray, 0);
//                onStatusCallBack(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED, 0);
            checkSdkLogSize();
            MMKVHelper.getInstance().remove(resultId);
            wordBoundaries.clear();
            receivedLength = 0;
//            mTtsId = "";
            speechSynthesisEventArgs.close();
        });
    }

    @Override
    public void startSynthesis(PlayTTSBean playTTSBean) {
        LogUtils.i(TAG, "ttsId is " + playTTSBean.getTtsId() + " ,ttsText is " + playTTSBean.getTts());
        isParsing = true;
        isFirstParsing = false;
        errorCode = CancellationErrorCode.NoError;
        stopped = false;
        wordBoundaries.clear();
        receivedLength = 0;
        String ttsText = playTTSBean.getTts();
        String styleType = playTTSBean.getEmotion();
        String language = playTTSBean.getLangType();
        this.mTtsId = playTTSBean.getTtsId();
        this.mUsage = playTTSBean.getUsage();
        this.originTtsId = playTTSBean.getOriginTtsId();
        String currentOffLineSpeaker = SpeakerManager.getInstance().getOffLineTtsSpeaker();
        LogUtils.d(TAG, "currentOffLineSpeaker:" + currentOffLineSpeaker + " ,speakerName:" + currentOffLineSpeakerName);
        //设置离线音色
        if (!TextUtils.equals(currentOffLineSpeaker, currentOffLineSpeakerName)) {
            synthesizer.getProperties().setProperty(PropertyId.SpeechServiceConnection_SynthOfflineVoice, currentOffLineSpeaker);
            currentOffLineSpeakerName = currentOffLineSpeaker;
            Log.i(TAG, "startTTS: set mic offline speaker is " + currentOffLineSpeakerName);
        }
        if (TextUtils.isEmpty(ttsText)) {
            onSyncError(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL, "params is wrong", 0);
        } else {
            String speakerName = SpeakerManager.getInstance().getOnLineTtsSpeaker();
            LogUtils.i(TAG, "speakerName:" + speakerName);
            synthesizer.getProperties().setProperty("SPEECH-SynthBackendSwitchingPolicy", "parallel_buffer");
            LogUtils.e(TAG, "packageName is " + playTTSBean.getPackageName());
            synthesizer.getProperties().setProperty("SPEECH-SynthDisableCaching", String.valueOf(TextUtils.equals(playTTSBean.getPackageName(), BinderList.MAP)));
            String style = SsmlUtils.switchStyle(styleType, speakerName);
            String rate = TtsResolverManager.getInstance().getPlayRate();
            String ssmlPattern = SSML_PATTERN.replace("language", language)
                    .replace("emotionStyle", style)
                    .replace("playRate", TextUtils.isEmpty(rate) ? "5" : rate)
                    .replace("speaker", speakerName);

            tts = System.currentTimeMillis() + "_" + ttsText + "_" + speakerName + "_" + style + "_5%%.pcm";
            ttsText = SsmlUtils.replaceSpecialString(ttsText);

            //ssml md5
            ssml = String.format(ssmlPattern, TextUtils.htmlEncode(ttsText));
            String localPath = TtsResolverManager.getInstance().getLocalAudioPath(ssml);
            if (TextUtils.isEmpty(localPath)) {
                LogUtils.i(TAG, "ssml is:" + ssml);
                boolean isNearPlaySet = ProximityInteractionManager.getInstance().setNearbyTtsPosition(playTTSBean.getSoundLocation(), playTTSBean.getPackageName(), playTTSBean.getUsage());
                LogUtils.d(TAG, "isNearPlaySet:" + isNearPlaySet);
                //todo:
                playTTSBean.setNearPlay(isNearPlaySet);
                synthesizer.StartSpeakingSsmlAsync(ssml);
            } else {
                LogUtils.i(TAG, "readFromLocal localPath:" + localPath);
                readFromLocal(playTTSBean, localPath, speakerName, ssml);
            }
        }
    }

    private void readFromLocal(PlayTTSBean playTTSBean, String localPath, String speakerName, String ssml) {
//        LogUtils.d(TAG, "readFromLocal localPath:" + localPath);
        //添加路径+文件名+后缀
        BuriedPointManager.getInstance().setBuriedPointData(originTtsId, "", System.currentTimeMillis() + "", "", "", SpeakerManager.getInstance().getOffLineTtsSpeaker());
        boolean isNearPlaySet = ProximityInteractionManager.getInstance().setNearbyTtsPosition(playTTSBean.getSoundLocation(), playTTSBean.getPackageName(), playTTSBean.getUsage());
        LogUtils.d(TAG, "isNearPlaySet:" + isNearPlaySet);
        playTTSBean.setNearPlay(isNearPlaySet);
        decodeRunnable = new DecodeRunnable(playTTSBean.getTtsId(), localPath, speakerName, ssml);
        executorService.execute(decodeRunnable);
    }

    private class DecodeRunnable implements Runnable {
        volatile String ttsId;
        volatile boolean isParsing = false;
        volatile String path;
        volatile String speakerName;

        volatile String ssml;


        void stop() {
            LogUtils.d(TAG, "DecodeRunnable stop");
            isParsing = false;
            ttsId = null;
            speakerName = "";
            ssml = "";
            mTtsId = "";
        }

        DecodeRunnable(String ttsId, String path, String speakerName, String ssml) {
            this.ttsId = ttsId;
            this.path = path;
            this.speakerName = speakerName;
            this.ssml = ssml;
        }

        @Override
        public void run() {
            String currentId = ttsId;
            FileInputStream pcmFile = null;
            onStatusCallBack(currentId, ITtsResolverCallBack.ResolverStatus.RESOLVER_START, 0);
            try {
                LogUtils.i(TAG, path);
                pcmFile = new FileInputStream(new File(path));
                isParsing = true;
                BuriedPointManager.getInstance().setBuriedPointData(originTtsId, "", "", "", "cache", speakerName);
                byte[] putBuf = new byte[pcmFile.available()];
                int readLength = pcmFile.read(putBuf);
                LogUtils.d(TAG, "DecodeRunnable putBuf.length:" + putBuf.length + " ,currentId:" + currentId + " ,mTtsId:" + mTtsId + " ,readLength:" + readLength);

                if (putBuf.length == 0) {
                    synthesizer.StartSpeakingSsmlAsync(ssml);
                } else {
                    if (TextUtils.equals(currentId, mTtsId)) {
                        audioAheadProcessor.startTask(currentId);
//                        disposeBytes(putBuf, currentId, "");
                        audioAheadProcessor.processAudioData(putBuf, currentId, "", mUsage);
                        pcmFile.close();
                        audioAheadProcessor.processAudioData(defaultByte, currentId, "", mUsage);
//                        disposeBytes(defaultByte, ttsId, "");
                    } else {
                        LogUtils.d(TAG, "id changed,abandon this task currentId:" + currentId + " ,mTtsId:" + mTtsId);
                    }
//                    stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(TAG, "parsing failure --- " + type, e);
                onStatusCallBack(currentId, ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL, 0);
                stop();
            } finally {
                try {
                    if (null != pcmFile) {
                        pcmFile.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void synthesis() {
        //todo:1.检查下当前系统设置的音色与合成器保存音色是否一致 2.拼接ssml(语种、情感) 3.发起合成
        isFirstParsing = false;
    }

    @Override
    public void stopSynthesis() {
        if (null != decodeRunnable && decodeRunnable.isParsing) {
            LogUtils.d(TAG, "stopSynthesis decodeRunnable");
            onStatusCallBack(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD, 0);
            decodeRunnable.stop();
        } else if (isParsing) {
            LogUtils.d(TAG, "stopSynthesis synthesizer");
            stopped = true;
            synthesizer.StopSpeakingAsync();
            onStatusCallBack(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD, 0);
            mTtsId = "";
        } else {
            LogUtils.d(TAG, "stopSynthesis default");
            onStatusCallBack(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD, 0);
        }
        audioAheadProcessor.stopTask();
    }

    @Override
    public void reset() {
//        ttsId = "";
        isParsing = false;
        isFirstParsing = false;
    }

    @Override
    public void setTtsResolverCallBack(ITtsResolverCallBack ttsResolverCallBack) {
        this.ttsResolverCallBack = ttsResolverCallBack;
        audioAheadProcessor.setTtsResolverCallBack(ttsResolverCallBack);

    }

    @Override
    public int getNum() {
        return num;
    }

    @Override
    public void playEnd(String ttsId, int ttsType) {
        LogUtils.d(TAG, "playEnd ttsId:" + ttsId + " ,ttsType:" + ttsType);
        if (TextUtils.isEmpty(mTtsId) || !TextUtils.equals(ttsId, mTtsId))
            return;
        if ((TextUtils.isEmpty(ttsId) || TextUtils.isEmpty(mTtsId)) && TextUtils.equals(ttsId, mTtsId))
            mTtsId = "";
        if (decodeRunnable != null) {
            decodeRunnable.stop();
        }
        audioAheadProcessor.playEnd();
        onStatusCallBack(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED, ttsType);
    }

//    private void disposeBytes(byte[] bytes, String ttsId, String resultId) {
////        LogUtils.e(TAG, "disposeBytes bytes.length:" + bytes.length + " ,ttsId:" + ttsId + " ,mTtsId:" + mTtsId);
//        if (stopped || !TextUtils.equals(mTtsId, ttsId)) {
//            LogUtils.d(TAG, "disposeBytes stopped:" + stopped + " ,mTtsId:" + mTtsId + " ,ttsId:" + ttsId);
//            return;
//        }
//        if (bytes.length == 0) {
//            LogUtils.d(TAG, " last buffer");
//            onData(ttsId, completedByteArray, 0, resultId);
//        } else {
//            int count = bytes.length / ByteArr.LEN_PER + (bytes.length % ByteArr.LEN_PER == 0 ? 0 : 1);
//            if (count >= 60)
//                LogUtils.d(TAG, "disposeBytes count:" + count);
//            for (int i = 0; i < count; i++) {
//                ByteArrMgr.ByteArrObj obj = TtsByteArrMgr.getInstance().getByteObj(mUsage);
//                if (obj.len > 0) {
//                    obj.reset();
//                }
//                int len = bytes.length - i * ByteArr.LEN_PER;
//                if (len > ByteArr.LEN_PER) {
//                    len = ByteArr.LEN_PER;
//                }
//                System.arraycopy(bytes, i * ByteArr.LEN_PER, obj.arr, 0, len);
//                obj.len = len;
//                if (!stopped && TextUtils.equals(mTtsId, ttsId))
//                    onData(ttsId, obj, 0, resultId);
//                else
//                    LogUtils.d(TAG, "disposeBytes stopped:" + stopped + " ,mTtsId:" + mTtsId + " ,ttsId:" + ttsId
//                            + ", count:" + count + " ,i:" + i);
//            }
//        }
//    }

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
                        isFirstParsing = false;
                        synthesizer.StartSpeakingSsmlAsync(newSsml).get();
                        wordBoundaries.clear();
                        receivedLength = 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onSyncError(mTtsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL, "wordBoundaries error", 0);
            }
        }
    }

    private void checkSdkLogSize() {
        if (checkHandler == null || !TtsResolverManager.getInstance().isDumpMicrosoftLog()) {
            return;
        }
        checkHandler.post(new Runnable() {
            @Override
            public void run() {
                File file = new File(Constant.Path.SDK_LOG_PATH);
                long fileSize = file.length();
                BufferedWriter bwMSG11 = null;
                if (file.exists() && fileSize >= Constant.TTS_SDK_LOG_SIZE) {
                    LogUtils.i(TAG, "dumpLog: ttsLogSize is " + fileSize);
                    try {
                        bwMSG11 = new BufferedWriter(new FileWriter(Constant.Path.SDK_LOG_PATH));
                        bwMSG11.write("");//清空
                        bwMSG11.flush();
                        bwMSG11.close();
                        bwMSG11 = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (null != bwMSG11)
                                bwMSG11.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }


    private void onStatusCallBack(String ttsId, int playStatus, int ttsType) {
        if (null != ttsResolverCallBack)
            ttsResolverCallBack.onStatus(ttsId, playStatus, ttsType);
        else
            LogUtils.d(TAG, "onStatusCallBack ttsResolverCallBack is null");
    }

    private void onSyncError(String ttsId, int errorCode, String msg, int ttsType) {
//        ttsResolverCallBack.onSyncError(ttsId, errorCode, msg, ttsType);
        onStatusCallBack(ttsId, errorCode, ttsType);
    }

    private void onData(String ttsId, ByteArrMgr.ByteArrObj arrObject, int ttsType) {
        ttsResolverCallBack.onData(ttsId, arrObject, ttsType);
    }
}
