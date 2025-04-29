package com.voice.sdk;


import static com.voyah.ai.voice.sdk.kernel.api.callback.StateCallbackCode.EVENT_INIT_FAILED;
import static com.voyah.ai.voice.sdk.kernel.api.callback.StateCallbackCode.EVENT_INIT_SUCCESS;
import static com.voyah.ai.voice.sdk.kernel.api.callback.StateCallbackCode.EVENT_VA_STATE;
import static com.voyah.ai.voice.sdk.kernel.api.callback.StateCallbackCode.EVENT_VOICE_EVENT;

import com.voice.sdk.util.ThreadPoolUtils;
import com.voyah.ai.common.ParamsGather;
import com.voyah.ai.common.utils.FileUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.RecordDataUtils;
import com.voyah.ai.voice.nlu.offline.NluOfflineSdk;
import com.voyah.ai.voice.platform.soa.api.channel.IAudioSteam;
import com.voyah.ai.voice.platform.soa.api.channel.IPullAudioSteam;
import com.voyah.ai.voice.platform.soa.api.parameter.DebugConfig;
import com.voyah.ai.voice.platform.soa.api.parameter.EnvDataConfig;
import com.voyah.ai.voice.platform.soa.api.parameter.FeatureConfig;
import com.voyah.ai.voice.platform.soa.api.plugin.IEnvPlugin;
import com.voyah.ai.voice.sdk.api.IVoiceAssistantInterface;
import com.voyah.ai.voice.sdk.api.VoiceAssistantKitsImpl;
import com.voyah.ai.voice.sdk.api.callback.IVoiceAssistantCallback;
import com.voyah.ai.voice.sdk.api.channel.IAgentXManager;
import com.voyah.ai.voice.sdk.api.component.VoiceAssistantComponent;
import com.voyah.ai.voice.sdk.api.component.parameter.command.ExitRequestParameters;
import com.voyah.ai.voice.sdk.api.component.parameter.command.ExitSessionParameters;
import com.voyah.ai.voice.sdk.api.component.parameter.command.QueryCommandParameters;
import com.voyah.ai.voice.sdk.api.component.parameter.command.WakeupCommandParameters;
import com.voyah.ai.voice.sdk.api.component.parameter.config.DebugConfigParameters;
import com.voyah.ai.voice.sdk.api.component.parameter.config.EnvDataConfigParameters;
import com.voyah.ai.voice.sdk.api.component.parameter.config.FeatureConfigParameters;
import com.voyah.ai.voice.sdk.api.component.parameter.config.SceneConfigParameters;
import com.voyah.ai.voice.sdk.api.component.parameter.config.ViewCommandConfigParameters;
import com.voyah.ai.voice.sdk.api.parameter.VoiceAssistantConfig;
import com.voyah.ai.voice.sdk.api.task.AgentX;
import com.voyah.ai.voice.sdk.kernel.api.callback.StateCallbackCode;
import com.voyah.ds.dialog.entity.ExternalScenario;
import com.voyah.ds.dialog.entity.ExternalSignal;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author:lcy
 * @data:2024/1/21
 **/
public class VoiceImpl implements IVoiceImpl {
    private static final String TAG = VoiceImpl.class.getSimpleName();
    private static VoiceImpl mVoiceImpl;
    private IVoiceAssistantInterface voiceAssistant = null;

    private IVoAIVoiceSDKInitCallback mInitCallback;

    private IAgentXManager agentManager;
    private IAudioSteam audioSteam;

    private IPullAudioSteam pullStream;

    private String mVinCode;
    private boolean isCar;

    //todo:大模式开关临时状态值
    private boolean modelDebug;

    private boolean isInitSuccess;

    private String mVehicleType;
    private int mConfigurationType;

    private final int[] integers = new int[]{509, 761, 967, 1157, 1338, 1520, 1723, 1905, 2066, 2244, 2396, 2557, 2715, 2864, 3018, 3175, 3341, 3514, 3750};

    private boolean[] status = new boolean[6];
    private boolean[] statusDefault = new boolean[]{true, true, true, true, true, true};

    public VCarDataCallback vCarDataCallback;

    private IEnvPlugin voiceEnvPlugin;

    public VoiceImpl() {
        VoiceConfigManager.getInstance().init();
    }

    public static VoiceImpl getInstance() {
        if (null == mVoiceImpl) {
            synchronized (VoiceImpl.class) {
                if (null == mVoiceImpl) {
                    mVoiceImpl = new VoiceImpl();
                }
            }
        }
        return mVoiceImpl;
    }

    @Override
    public void init(String vinCode, boolean isCar, String vehicleType, int configurationType, IVoAIVoiceSDKInitCallback initCallback) {
        LogUtils.i(TAG, "vinCode is " + vinCode + " ,isCar is " + isCar + " ,vehicleType is " + vehicleType + " ,configurationType is" + configurationType);
        this.mVinCode = vinCode;
        this.isCar = isCar;
        this.mInitCallback = initCallback;
        this.mVehicleType = vehicleType;
        this.mConfigurationType = configurationType;
        getiVoiceAssistantInterface();
        NluOfflineSdk nluOfflineSdk = new NluOfflineSdk();
    }

    public void setVoiceEnvPlugin(IEnvPlugin voiceEnvPlugin) {
        this.voiceEnvPlugin = voiceEnvPlugin;
    }

    private void getiVoiceAssistantInterface() {
        LogUtils.d(TAG, "getiVoiceAssistantInterface micChannelsDefault:" + Constant.channels.micChannelsDefault + " ,RefChannelsDefault:" + Constant.channels.RefChannelsDefault);
        VoiceAssistantConfig parameters = new VoiceAssistantConfig();
        parameters.setMicChannels(Constant.channels.micChannelsDefault);
        parameters.setRefChannels(Constant.channels.RefChannelsDefault);
        parameters.setDeviceId(mVinCode);
        parameters.setEnvPlugin(this.voiceEnvPlugin == null ? new VoiceEnvPlugin() : this.voiceEnvPlugin);
        parameters.setThirdParty(false); //默认用自研
        parameters.setActivePath(Constant.path.dirWrite);
        parameters.setDeviceType(mVehicleType);
        parameters.setDeviceConfiguration(mConfigurationType);
        //todo:添加一个临时的配置文件(环境也添加)
        boolean isSaveAudio = VoiceConfigManager.getInstance().getSaveAudioSwitch();
        LogUtils.i(TAG, "isSaveAudio is " + isSaveAudio);
        if (isSaveAudio)
            parameters.setRecordPath(Constant.path.dirWrite + "audio/");

        if (isCar) {
            String resPath = Constant.path.carLoadRes;
            LogUtils.i(TAG, "resPath is " + resPath);
            parameters.setResPath(resPath);
        } else {
            //虚拟车
            parameters.setResValid(false);
        }

        VoiceAssistantKitsImpl.getInstance().initVoAI(parameters, new IVoiceAssistantCallback() {
            @Override
            public void broadcastMessage(StateCallbackCode stateCallbackCode) {
                ThreadPoolUtils.INSTANCE.threadExecute(() -> {
                    String detail = stateCallbackCode.getDetail();
                    int what = stateCallbackCode.getEvent();
                    if (what != EVENT_VOICE_EVENT)
                        LogUtils.d(TAG, "msg.what is " + what + " ,detail is " + detail);
                    switch (what) {
                        case EVENT_INIT_SUCCESS:
                            voiceAssistant = VoiceAssistantKitsImpl.getInstance();
                            agentManager = voiceAssistant.getAgentManager();
                            audioSteam = voiceAssistant.createAudioSteam();
                            isInitSuccess = true;
                            mInitCallback.onSUCCESS();
                            break;
                        case EVENT_INIT_FAILED:
                            mInitCallback.onFAILED();
                            break;
                        case EVENT_VA_STATE:
                            //非连续对话结束时退出语音交互场景
                            if (StringUtils.equals(detail, StateCallbackCode.VA_STATE_IDLE)) {
                                mInitCallback.onExit();
                            }
                            break;
                        case EVENT_VOICE_EVENT:
                            if (StringUtils.isBlank(detail))
                                return;
                            Map<String, Object> msgMap = stateCallbackCode.getMessage();
                            mInitCallback.onVoiceStatus(detail, msgMap);
                            break;
                    }
                });
            }
        });
    }

    @Override
    public void registerAgent(AgentX agent) {
        if (null == agentManager)
            return;
        agentManager.addAgentX(agent);
    }

    @Override
    public void addAgents(List<AgentX> agentXList) {
        if (null == agentManager || agentXList.isEmpty())
            return;
        for (AgentX agent : agentXList) {
            agentManager.addAgentX(agent);
        }
    }

    @Override
    public void unRegisterAgent(AgentX agent) {
        if (null == agentManager)
            return;
        agentManager.removeAgentX(agent);
    }

    //退出语音
    @Override
    public void exDialog() {
        if (null == voiceAssistant)
            return;
        LogUtils.d(TAG, "exDialog");
        VoiceAssistantComponent.Command.ExitDialog exitDialog = new VoiceAssistantComponent.Command.ExitDialog();
        voiceAssistant.setComponent(exitDialog);

    }

    //退出语音二次交互
    @Override
    public void exitSessionDialog(String sessionId) {
        if (null == voiceAssistant || StringUtils.isBlank(sessionId))
            return;
        LogUtils.d(TAG, "exitSessionDialog" + "    sessionId is " + sessionId);
        cleanSceneReport();
        ExitSessionParameters exitSessionParameters = new ExitSessionParameters(sessionId);
        VoiceAssistantComponent.Command.ExitSession exitSession = new VoiceAssistantComponent.Command.ExitSession(exitSessionParameters);
        voiceAssistant.setComponent(exitSession);
    }


    //退出指定任务(大模型卡片关闭场景使用)
    @Override
    public void exitRequest(String requestId) {
        LogUtils.d(TAG, "exitRequest requestId is " + requestId);
        if (null == voiceAssistant || StringUtils.isBlank(requestId))
            return;
        ExitRequestParameters exitRequestParameters = new ExitRequestParameters(requestId);
        VoiceAssistantComponent.Command.ExitRequest exitRequest = new VoiceAssistantComponent.Command.ExitRequest(exitRequestParameters);
        voiceAssistant.setComponent(exitRequest);
    }


    /**
     * @param sceneState 场景值-参考 https://hav4xarv6k.feishu.cn/docx/IGhMd7vBRoamzhxG5RlcSXLynuh
     * @param params     场景随参
     */
    @Override
    public void sceneReport(String id, String sceneState, String sceneStateTts, Map<String, Object> params) {
        if (null == voiceAssistant)
            return;
        //场景上报
        ExternalScenario externalScenario = new ExternalScenario();
        externalScenario.id = id;
        externalScenario.state = sceneState;
        externalScenario.tts = sceneStateTts;
        externalScenario.paramMap = params;
        VoiceAssistantComponent.Config.Scene scene = new VoiceAssistantComponent.Config.Scene(new SceneConfigParameters(externalScenario));
        voiceAssistant.setComponent(scene);
    }

    /**
     * 场景上报(三方使用)
     *
     * @param sceneState     场景值
     * @param sceneStateTts  场景TTS文本，多轮连续询问时使用，非强多轮可传空
     * @param params         三方上传数据
     * @param wakeUpLocation 唤醒位 *默认为0（三方暂时默认使用0）
     * @param isWakeUp       是否唤醒，*默认为false
     */
    public void thirdSceneReport(String sceneState, String sceneStateTts, Map<String, Object> params, int wakeUpLocation, boolean isWakeUp) {
        LogUtils.d(TAG, "thirdSceneReport sceneState:" + sceneState + " ,wakeUpLocation:" + wakeUpLocation + " ,isWakeUp:" + isWakeUp);
        if (null == voiceAssistant)
            return;
        //场景上报
        ExternalSignal externalSignal = new ExternalSignal();
        externalSignal.state = sceneState;
        externalSignal.tts = sceneStateTts;
        externalSignal.paramMap = params;
        externalSignal.isThirdParty = true;
        SceneConfigParameters sceneConfigParameters = new SceneConfigParameters(externalSignal);
        sceneConfigParameters.setLocation(wakeUpLocation);
        sceneConfigParameters.setWakeup(isWakeUp);
        VoiceAssistantComponent.Config.Scene scene = new VoiceAssistantComponent.Config.Scene(sceneConfigParameters);
        voiceAssistant.setComponent(scene);
    }


    /**
     * 清空场景上报
     */
    @Override
    public void cleanSceneReport() {
        if (null == voiceAssistant)
            return;
        VoiceAssistantComponent.Config.Scene scene = new VoiceAssistantComponent.Config.Scene(new SceneConfigParameters(new ExternalScenario()));
        voiceAssistant.setComponent(scene);
    }

    @Override
    public void sendAudio(byte[] bytes) {
        //todo:开启新的线程传底层音频
        if (null == audioSteam || VoiceConfigManager.getInstance().getDebugWriteAudioSwitch())
            return;
        try {
            audioSteam.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //本地测试相应时长使用(1麦克+0参考、关闭原始音频流传输)
    public void sendAudio(String audioName) {
        //todo:开启新的线程传底层音频
        if (null == audioSteam)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtils.i(TAG, "sendAudio test start");
                    byte[] buffer = new byte[2560];
                    FileInputStream fileInputStream = new FileInputStream(Constant.path.dirWrite + audioName);
                    if (audioName.contains("audio10")) {
                        byte[] buffer1 = new byte[3200];
                        while ((fileInputStream.read(buffer1)) != -1) {
                            if (StringUtils.equals(mVehicleType,"H37B")){
                                audioSteam.write(buffer1);
                            }else {
                                byte[] bytes = RecordDataUtils.transferRecordDataFor2560(buffer1, buffer);
                                audioSteam.write(bytes);
                            }
                            //todo:调试阶段使用
                            Thread.sleep(10);
                        }
                    } else {
                        while ((fileInputStream.read(buffer)) != -1) {
                            audioSteam.write(buffer);
                            //todo:调试阶段使用
                            Thread.sleep(10);
                        }
                    }
                    LogUtils.i(TAG, "sendAudio test end");
                    fileInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void wakeUp(int location, int wakeupType) {
        if (null == voiceAssistant)
            return;
        LogUtils.i(TAG, "wakeUp location is " + location);
        WakeupCommandParameters wakeupCommandParameters = new WakeupCommandParameters();
        wakeupCommandParameters.setLocation(location);
        wakeupCommandParameters.setWakeupType(wakeupType);
        VoiceAssistantComponent.Command.Wakeup wakeup = new VoiceAssistantComponent.Command.Wakeup(wakeupCommandParameters);
        voiceAssistant.setComponent(wakeup);
    }

    public void selectIndex(int index) {
        int location = 0;
        if (Location.LOCATION_MAP.containsKey(ParamsGather.location)) {
            location = Location.LOCATION_MAP.get(ParamsGather.location);
        }
        if (index >= 0) {
            queryTest("第" + (index + 1) + "个", location, true);
        }
        LogUtils.i(TAG, " index is " + index + " ,location is " + location);
    }

    public void confirm(String confirm) {
        int location = 0;
        if (Location.LOCATION_MAP.containsKey(ParamsGather.location)) {
            location = Location.LOCATION_MAP.get(ParamsGather.location);
        }
        if (!StringUtils.isBlank(confirm))
            queryTest(confirm, location, true);
        LogUtils.i(TAG, " confirm is " + confirm + " ,location is " + location);
    }

    public void queryTest(String queryText, int location, boolean isOnline) {
        if (null == voiceAssistant)
            return;
        LogUtils.i(TAG, queryText);
        QueryCommandParameters queryCommandParameters = new QueryCommandParameters(queryText);
        queryCommandParameters.setLocation(location);
        VoiceAssistantComponent.Command.Query query = new VoiceAssistantComponent.Command.Query(queryCommandParameters);
        voiceAssistant.setComponent(query);
    }

    public void uploadPersonalEntity(String key, Object data) {
        if (null == voiceAssistant)
            return;
        EnvDataConfig envDataConfig = new EnvDataConfig();
        envDataConfig.addEnvData(key, data);
        EnvDataConfigParameters envDataConfigParameters = new EnvDataConfigParameters(envDataConfig);
        VoiceAssistantComponent.Config.EnvData envData = new VoiceAssistantComponent.Config.EnvData(envDataConfigParameters);
        voiceAssistant.setComponent(envData);
    }

    public void uploadTtsPlayStatus(int[] ints) {
//        LogUtils.d(TAG, "uploadTtsPlayStatus:" + Arrays.toString(ints));
        if (null == voiceAssistant)
            return;
        EnvDataConfig envDataConfig = new EnvDataConfig();
        envDataConfig.addEnvData(EnvDataConfig.ENV_KEY_TTS_STATUS, arrayTransition(ints));
        EnvDataConfigParameters envDataConfigParameters = new EnvDataConfigParameters(envDataConfig);
        VoiceAssistantComponent.Config.EnvData envData = new VoiceAssistantComponent.Config.EnvData(envDataConfigParameters);
        voiceAssistant.setComponent(envData);
    }

    //大模型调试使用
    public void setLanguageModelDebug(boolean isLanguageModelDebug) {
        if (null == voiceAssistant)
            return;
        modelDebug = isLanguageModelDebug;
        DebugConfig debugConfig = new DebugConfig();
        debugConfig.setLargeLanguageModelDebug(isLanguageModelDebug);
        DebugConfigParameters debugConfigParameters = new DebugConfigParameters(debugConfig);
        VoiceAssistantComponent.Config.Debug debug = new VoiceAssistantComponent.Config.Debug(debugConfigParameters);
        voiceAssistant.setComponent(debug);
    }

    /**
     * 音频保存控制开关
     *
     * @param enableRecord  是否保存音频数据
     * @param dumpAudioPath 音频数据保存路径
     */
    public boolean enableRecordAudio(boolean enableRecord, boolean isDumpWakeupLike, String dumpAudioPath) {
        LogUtils.d(TAG, "enableRecordAudio enableRecord:" + enableRecord + " ,isDumpWakeupLike:" + isDumpWakeupLike + " ,dumpAudioPath:" + dumpAudioPath);
        if (null == voiceAssistant)
            return false;
        if (enableRecord && StringUtils.isBlank(dumpAudioPath))
            return false;
        DebugConfig debugConfig = new DebugConfig();
        debugConfig.setDumpAudio(enableRecord);
        debugConfig.setDumpAudioPath(dumpAudioPath);
        debugConfig.setDumpWakeupLike(isDumpWakeupLike);
        DebugConfigParameters debugConfigParameters = new DebugConfigParameters(debugConfig);
        VoiceAssistantComponent.Config.Debug debug = new VoiceAssistantComponent.Config.Debug(debugConfigParameters);
        voiceAssistant.setComponent(debug);
        return true;
    }

    //todo:获取大模式调试开关状态 - 临时
    public boolean getModelDebug() {
        return modelDebug;
    }


    //语音唤醒开关
    public void enableWakeup(boolean enableWakeup) {
        if (null == voiceAssistant)
            return;
        LogUtils.i(TAG, "enableWakeup is " + enableWakeup);
        FeatureConfig featureConfig = new FeatureConfig();
        featureConfig.addEnableWakeup(enableWakeup);
        FeatureConfigParameters featureConfigParameters = new FeatureConfigParameters(featureConfig);
        voiceAssistant.setComponent(new VoiceAssistantComponent.Config.Feature(featureConfigParameters));
    }

    //语音禁用(禁用场景使用)
    public void enableVoice(boolean enableVoice) {
        if (null == voiceAssistant)
            return;
        LogUtils.i(TAG, "enableVoice is " + enableVoice);
        FeatureConfig featureConfig = new FeatureConfig();
        featureConfig.addEnableVoice(enableVoice);
        FeatureConfigParameters featureConfigParameters = new FeatureConfigParameters(featureConfig);
        voiceAssistant.setComponent(new VoiceAssistantComponent.Config.Feature(featureConfigParameters));
    }

    //todo:全时免唤醒开关
    public void enableAllTime(boolean enableAllTime) {
        if (null == voiceAssistant)
            return;
        LogUtils.i(TAG, "enableAllTime is " + enableAllTime);
        FeatureConfig featureConfig = new FeatureConfig();
        featureConfig.addEnableAllTimeKeyword(enableAllTime);
        //20250113 多音区自由对话与全时合并，多音区自由对话无需独立的开关
        featureConfig.addEnableMultiVPA(enableAllTime);
        FeatureConfigParameters featureConfigParameters = new FeatureConfigParameters(featureConfig);
        voiceAssistant.setComponent(new VoiceAssistantComponent.Config.Feature(featureConfigParameters));
    }

    //todo:自然唤醒开关
    public void enableWeakWakeup(boolean enableWeakWakeup) {
        if (null == voiceAssistant)
            return;
        LogUtils.i(TAG, "enableWeakWakeup is " + enableWeakWakeup);
        FeatureConfig featureConfig = new FeatureConfig();
        featureConfig.addEnableWeakWakeup(enableWeakWakeup);
        FeatureConfigParameters featureConfigParameters = new FeatureConfigParameters(featureConfig);
        voiceAssistant.setComponent(new VoiceAssistantComponent.Config.Feature(featureConfigParameters));
    }

    //todo:可收音区设置
    public void setRegionConfig(int position) {
        if (null == voiceAssistant)
            return;
        LogUtils.i(TAG, "setRegionConfig is " + position);
        FeatureConfig featureConfig = new FeatureConfig();
        featureConfig.addRegionConfig(position);
        FeatureConfigParameters featureConfigParameters = new FeatureConfigParameters(featureConfig);
        voiceAssistant.setComponent(new VoiceAssistantComponent.Config.Feature(featureConfigParameters));
    }

    @Override
    public boolean getVoiceSdkInitStatus() {
        return isInitSuccess;
    }

    //连续对话
    public void enableContinueSession(boolean enableContinueSession) {
        if (null == voiceAssistant)
            return;
        LogUtils.i(TAG, "enableContinueSession is " + enableContinueSession);
        FeatureConfig featureConfig = new FeatureConfig();
        featureConfig.addEnableContinueSession(enableContinueSession);
        FeatureConfigParameters featureConfigParameters = new FeatureConfigParameters(featureConfig);
        voiceAssistant.setComponent(new VoiceAssistantComponent.Config.Feature(featureConfigParameters));
    }

    //20250113多音区自由对话与全时合并，多音区自由对话无需独立的开关
    public void enableMultiVPA(boolean enableMultiVPA) {
        if (null == voiceAssistant)
            return;
        LogUtils.i(TAG, "enableMultiVPA is " + enableMultiVPA);
        FeatureConfig featureConfig = new FeatureConfig();
        featureConfig.addEnableMultiVPA(enableMultiVPA);
        FeatureConfigParameters featureConfigParameters = new FeatureConfigParameters(featureConfig);
        voiceAssistant.setComponent(new VoiceAssistantComponent.Config.Feature(featureConfigParameters));
    }


    //todo:就近播报
    public void enableNearbyTTS(boolean enableNearbyTTS) {
        //todo：客户端处理,无需上报
    }

    //todo:方言识别设置,仅处理识别
    public void setDialect(String language) {
        if (null == voiceAssistant)
            return;
        LogUtils.i(TAG, "language is " + language);
        FeatureConfig featureConfig = new FeatureConfig();
        if ("official_1".equalsIgnoreCase(language)) {
            language = "mandarin";
        }
        featureConfig.addLanguageConfig(language);
        FeatureConfigParameters featureConfigParameters = new FeatureConfigParameters(featureConfig);
        voiceAssistant.setComponent(new VoiceAssistantComponent.Config.Feature(featureConfigParameters));
    }

    //todo:个性音色
    public void setPersonalizedTimbre() {
        //
    }

    //todo:声音复刻
    public void enableVoicePrint(boolean enable) {
        //
    }

    //设置大模型供应商源
    public void setLlmModel(String llmModel) {
        if (null == voiceAssistant)
            return;
        LogUtils.i(TAG, "llmModel is " + llmModel);
        FeatureConfig featureConfig = new FeatureConfig();
        featureConfig.addLlmMode(llmModel);
        FeatureConfigParameters featureConfigParameters = new FeatureConfigParameters(featureConfig);
        voiceAssistant.setComponent(new VoiceAssistantComponent.Config.Feature(featureConfigParameters));
    }

    //隐私模式
    public void enablePrivacyMode(boolean enablePrivacyMode) {
        if (null == voiceAssistant)
            return;
        LogUtils.i(TAG, "enablePrivacyMode is " + enablePrivacyMode);
        FeatureConfig featureConfig = new FeatureConfig();
        featureConfig.addEnablePrivacyMode(enablePrivacyMode);
        FeatureConfigParameters featureConfigParameters = new FeatureConfigParameters(featureConfig);
        voiceAssistant.setComponent(new VoiceAssistantComponent.Config.Feature(featureConfigParameters));
    }

    public void pullAudioStream(int voiceArea, IAudioSteam stream) {
        if (voiceAssistant != null) {
            LogUtils.d("LongAsManager", "pull start with voiceArea:" + voiceArea);
            pullStream = voiceAssistant.createPullAudioSteam();
            pullStream.start(voiceArea, stream);
        }
    }

    public void stopPull(int voiceArea) {
        if (voiceAssistant == null) {
            return;
        }
        if (pullStream != null) {
            LogUtils.d("LongAsManager", "pull stop");
            pullStream.stopDumpAudio(voiceArea);
        }
    }


    public void setVoiceCreateConfig(boolean enableWakeup, boolean enableContinueSession, boolean enableAllTime, boolean enableNatureWakeUp, boolean enableMultiVPA, boolean enablePrivacyMode, int regionConfig, String asr) {
        if (null == voiceAssistant) {
            return;
        }
        FeatureConfig featureConfig = new FeatureConfig();
        featureConfig.addEnableWakeup(enableWakeup);
        featureConfig.addEnableContinueSession(enableContinueSession);
        featureConfig.addEnableAllTimeKeyword(enableAllTime);
        featureConfig.addEnableWeakWakeup(enableNatureWakeUp);
        featureConfig.addEnableMultiVPA(enableMultiVPA);
        featureConfig.addEnablePrivacyMode(enablePrivacyMode);
        featureConfig.addRegionConfig(regionConfig);
        if ("official_1".equalsIgnoreCase(asr)) {
            asr = "mandarin";
        }
        featureConfig.addLanguageConfig(asr);
        FeatureConfigParameters featureConfigParameters = new FeatureConfigParameters(featureConfig);
        voiceAssistant.setComponent(new VoiceAssistantComponent.Config.Feature(featureConfigParameters));
    }

    /**
     * 可见热词上传
     *
     * @param data json字符串
     */
    public void uploadViewCommand(String data) {
        if (null == voiceAssistant) {
            return;
        }
        LogUtils.d(TAG, "uploadViewCommand() called with: data = " + data);
        ViewCommandConfigParameters parameters = new ViewCommandConfigParameters(data);
        VoiceAssistantComponent.Config.ViewCommand viewCmdConfig = new VoiceAssistantComponent.Config.ViewCommand(parameters);
        voiceAssistant.setComponent(viewCmdConfig);
    }

    /**
     * 场景免唤醒词注册
     *
     * @param sceneWordList 场景免唤醒词
     */
    public void addAndRemoveSceneWord(List<String> sceneWordList) {
        if (null == voiceAssistant || null == sceneWordList) {
            return;
        }
        LogUtils.d(TAG, "addAndRemoveSceneWord");
        ViewCommandConfigParameters viewCommandConfigParameters = new ViewCommandConfigParameters(sceneWordList);
        VoiceAssistantComponent.Config.ViewCommand viewCommand = new VoiceAssistantComponent.Config.ViewCommand(viewCommandConfigParameters);
        voiceAssistant.setComponent(viewCommand);
    }

    public void reportData(Map<String, String> map) {
        if (null == voiceAssistant) {
            return;
        }
        LogUtils.i(TAG, "reportData:" + GsonUtils.toJson(map));
        voiceAssistant.reportDeviceData(map);
    }


    interface Location {
        Map<String, Integer> LOCATION_MAP = new HashMap() {
            {
                put("front_left", 0);
                put("front_right", 1);
                put("rear_left", 2);
                put("rear_right", 3);

            }
        };
    }

    public void registerVCarDataCallback(VCarDataCallback vCarDataCallback) {
        this.vCarDataCallback = vCarDataCallback;
    }

    private boolean[] arrayTransition(int[] ints) {
        if (1 == ints[0])
            status = statusDefault;
        else {
            for (int i = 0; i < ints.length; i++) {
                status[i] = 0 != ints[i];
            }
        }
        LogUtils.d(TAG, "arrayTransition:" + Arrays.toString(status));
        return status;
    }
}
