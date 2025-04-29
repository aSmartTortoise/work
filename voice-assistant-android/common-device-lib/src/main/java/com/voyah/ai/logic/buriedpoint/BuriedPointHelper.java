package com.voyah.ai.logic.buriedpoint;

import android.os.Build;

import com.google.gson.Gson;
import com.voice.sdk.buriedpoint.bean.BuriedPointData;
import com.voice.sdk.buriedpoint.bean.VadTime;
import com.voice.sdk.context.ParamsGather;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.logic.buriedpoint.helper.FunctionToIdHelper;
import com.voyah.ai.sdk.bean.DhDialect;
import com.voyah.ai.sdk.bean.DhSwitch;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.nlu.NluInfo;
import com.voyah.ds.common.entity.status.StreamMode;
import com.voyah.ds.common.entity.wakeup.WakeupType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 埋点里有3个todo 没处理。
 */
public class BuriedPointHelper {
    private static final String TAG = "BuriedPointHelper";

    boolean isCar;

    private static final String APP_ID = "301";

    private Gson gson = new Gson();

    private BuriedPointHelper() {

    }

    public static BuriedPointHelper getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final BuriedPointHelper instance = new BuriedPointHelper();
    }

    public void init(String path) {

        FunctionToIdHelper.getInstance().init(path);
        LogUtils.d(TAG, "BURIED_POINT,init start");
        DeviceHolder.INS().getDevices().getBuriedPointManager().init(path);
        LogUtils.d(TAG, "BURIED_POINT,init end");
        isCar = DeviceHolder.INS().getDevices().getCarServiceProp().vehicleSimulatorJudgment();

    }


    /**
     * 设置通用的埋点数据
     */
    private void setCommonData(BuriedPointData buriedPointData) {
        //设置vin
        buriedPointData.setVin(ParamsGather.vin);
        //todo 设置用户唯一标识。（个人中心的user_id）


        //用户登陆了，
        if (DeviceHolder.INS().getDevices().getUserCenter().isLogin()) {
//            UserInfo userInfo = ;
//            //并且存在用户信息
//            if (userInfo != null) {
            buriedPointData.setUser_id(DeviceHolder.INS().getDevices().getUserCenter().getUserId());
//            } else {
////                    LogUtils.d(TAG, "埋点，不存在用户信息");
//            }
        } else {
//                LogUtils.d(TAG, "埋点，用户没登陆");
        }

        //设置车型
        String str = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        LogUtils.d(TAG, "BURIED_POINT, vehicle type：" + str);
        buriedPointData.setVehicle_type(str);
        //设置系统版本
        buriedPointData.setSystem_version(Build.DISPLAY);
        //设置语音版本
        buriedPointData.setApp_version(DeviceHolder.INS().getDevices().getSystem().getApp().getAppVersionName());

        //设置位置信息
        buriedPointData.setLocation(DeviceHolder.INS().getDevices().getBuriedPointManager().getLocation(isCar));
        //设置时间
        buriedPointData.setTime(System.currentTimeMillis() + "");
        //获取网络信息
        boolean isConnect = DeviceHolder.INS().getDevices().getBuriedPointManager().isConnected(isCar);
        buriedPointData.setInternet(isConnect == true ? "online" : "offline");
        //设置语音设置项
        HashMap<String, String> sets = new HashMap<>();
        //todo 连续对话开关设置
        sets.put("session", DeviceHolder.INS().getDevices().getVoiceSettings().isEnableSwitch(DhSwitch.ContinuousDialogue) == true ? "session_on" : "session_off");
        DhDialect dhDialect = DeviceHolder.INS().getDevices().getVoiceSettings().getCurrentDialect();
        String asr = dhDialect.asr;
        String hitAsr;
        switch (asr) {
            case DhDialect.ID_CANTONESE:
                hitAsr = "cantonese";
                break;
            case DhDialect.ID_SICHUAN:
                hitAsr = "sichuan";
                break;
            case DhDialect.ID_OFFICIAL_1:
                hitAsr = "mandarin";
                break;
            default:
                hitAsr = "error";
                break;
        }
        //方言识别设置
        sets.put("localism", hitAsr);
        buriedPointData.setSets(sets);
        //设置语音环境
//        buriedPointData.setSpeech_env("ds-" + VoiceConfigManager.getInstance().getVoiceEnv());

        //声纹信息
//        HashMap<String, String> voicePrintHashMap = new HashMap<>();
//        voicePrintHashMap.put("switch", );
//        voicePrintHashMap.put("gender", "");
//        voicePrintHashMap.put("age", "");
//        statisticalData.setVoiceprint(voicePrintHashMap);
        //todo
//        buriedPointData.setVoiceprint_switch(SettingsManager.get().isEnableSwitch(DhSwitch.VoicePrintRecognize) == true ? "on" : "off");
//        statisticalData.setGender();
//        statisticalData.setAge();


    }

    private void setDsData(Map<String, Object> flowContext, BuriedPointData buriedPointData, TTSBean ttsBean, String agentName) {
        //方位，声源位置。
        String soundLocation = (String) flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION);
        String resPosition;
        switch (soundLocation) {
            case "first_row_left":
                resPosition = "driver";
                break;
            case "first_row_right":
                resPosition = "copilot";
                break;
            case "second_row_left":
                resPosition = "second_left";
                break;
            case "second_row_right":
                resPosition = "second_right";
                break;
            case "third_row_left":
                resPosition = "third_left";
                break;
            case "third_row_right":
                resPosition = "third_right";
                break;
            default:
                resPosition = "error";
                break;
        }
        //设置生源位置
        buriedPointData.setVoice_zone(resPosition);
//        //声纹开关
//        buriedPointData.setVoiceprint_switch("");
//        //声纹性别
//        buriedPointData.setVoiceprint_gender("");
//        //声纹年龄
//        buriedPointData.setVoiceprint_age("");
        //设置唤醒类型
        int wakeUpType = (int) flowContext.get(FlowContextKey.FC_WAKE_UP_TYPE);
        String resWakeUpType = "";
        switch (wakeUpType) {
            case WakeupType.WP_MAJOR:
                resWakeUpType = "major";
                break;
            case WakeupType.WP_NATURE:
                resWakeUpType = "natural";
                //自然唤醒也会上屏幕，major是唤醒的意思。
                buriedPointData.setAsr_type("major");
                //自然唤醒需要传wake_word
                buriedPointData.setWakeup_word((String) flowContext.get(FlowContextKey.FC_WHOLE_QUERY));
                break;
            case WakeupType.WP_ALLTIME:
                resWakeUpType = "all_time";
                break;
            case WakeupType.WP_VISIBLE:
                resWakeUpType = "visible";
                break;
            case WakeupType.WP_VOICE_KEY:
                resWakeUpType = "voice_key";
                break;
            case WakeupType.NO_WP:
                resWakeUpType = "";
                break;
            default:
                resWakeUpType = "major";
                break;

        }

        buriedPointData.setWakeup_type(resWakeUpType);
        //kws文本
        Object isViewCommand = flowContext.get(FlowContextKey.FC_USE_VIEW_COMMAND_PATH);
        LogUtils.d(TAG, "BURIED_POINT, isViewCommand:" + isViewCommand + "  is in if(isViewCommand != null && isViewCommand instanceof Boolean):" + (isViewCommand != null && isViewCommand instanceof Boolean));
        //可见即可说
        if (isViewCommand != null && isViewCommand instanceof Boolean) {
            boolean isRealViewCommand = (boolean) isViewCommand;

            if (!isRealViewCommand) {
                buriedPointData.setNlu_source("nlu_gpt");
            }
            //asr识别类型。
            String asrType;
            if (isRealViewCommand) {
                asrType = "visible";
            } else {
                asrType = "major";
            }
            buriedPointData.setAsr_type(asrType);
            //语义来源
            buriedPointData.setNlu_source(isRealViewCommand ? "kws" : "nlu");
        } else {
            if (!agentName.equals("wakeUp")) {
                buriedPointData.setAsr_type("major");
                buriedPointData.setNlu_source("nlu_gpt");
            } else {
                buriedPointData.setNlu_source("");
            }
        }


        //多轮id
        buriedPointData.setSession_id((String) flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SESSION_ID));
        LogUtils.d(TAG, "BURIED_POINT,session_id is:" + (String) flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SESSION_ID));
        //queryID`
        String requestId = (String) flowContext.get(FlowContextKey.FC_REQ_ID);
        String relRequestId = "";
        if (requestId != null && !requestId.isEmpty()) {
            LogUtils.d(TAG, "BURIED_POINT,request_id is:" + requestId);
            if (requestId.contains("coop-")) {
                relRequestId = requestId.substring(5);
            } else {
                relRequestId = requestId;
            }
        } else {
            LogUtils.d(TAG, "BURIED_POINT, request_id is null");
        }
        buriedPointData.setQuery_id(relRequestId);
        //task_id
        buriedPointData.setMult_id(flowContext.get(FlowContextKey.FC_QUERY_ID) + "");
        //交互轮数
        buriedPointData.setInteraction_count(flowContext.get(FlowContextKey.SC_MULTI_INTERACTION_COUNT) + "");
//        //人声开始时间
        VadTime curVadTime = DeviceHolder.INS().getDevices().getBuriedPointManager().getVadTime(relRequestId, soundLocation);
        if (curVadTime != null) {
            buriedPointData.setTimestamp_vad_start(curVadTime.getVadStartTime() + "");
//        //人声结束时间
            buriedPointData.setTimestamp_vad_end(curVadTime.getVadEndTime() + "");
        }
        //Asr首字上屏时间

        buriedPointData.setTimestamp_asr(asrFirstOnScreenTime.get(relRequestId));
//        //执行语义时间
        buriedPointData.setTimestamp_nlu((flowContext.get(FlowContextKey.FC_NLU_EX_COST_TIME) == null) ? "" : ((Long) flowContext.get(FlowContextKey.FC_NLU_EX_COST_TIME) + ""));
        //asr文本
        buriedPointData.setAsr_word((String) flowContext.get(FlowContextKey.FC_WHOLE_QUERY));
        //离线nlu还是在线nlu
        boolean isOnline = (boolean) flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_IS_ONLINE);
        buriedPointData.setNlu_online_state(isOnline ? "online" : "offline");
        NluInfo nluInfo = (NluInfo) flowContext.get(FlowContextKey.FC_NLU_RESULT);
        LogUtils.d(TAG, "nlu数据是否位Null" + ((nluInfo == null) ? "是null的" : "不是null的"));
        if (nluInfo != null) {
            LogUtils.d(TAG, "埋点数据，看nlu下发的什么:" + nluInfo.nluStr);
            String nlu = nluInfo.nluStr;
            String[] nluArray = getNluParamsString(nlu);
            buriedPointData.setNlu_params(nluArray[1]);
            //todo 需要一次映射
            String funcTionId = FunctionToIdHelper.getInstance().getFunctionId(nluArray[0]);
            LogUtils.i(TAG, nluArray[0] + " " + funcTionId);
            buriedPointData.setNlu_content(funcTionId);
        }

        //语义执行状态 默认走normal_end，waken_end后续看逻辑添加
        buriedPointData.setNlu_status("normal_end");
//        //NLG上屏文案,在后面设置了
//        statisticalData.setNlg_screen();
//        //NLG播报文案，在后面设置了
//        statisticalData.setNlg_speak();
//        //NLG生成类


        if (Boolean.TRUE.equals(flowContext.get(FlowContextKey.FC_IS_CLASS_LABEL_FROM_CPSP))) {
            buriedPointData.setNlg_type("cpsp");
        } else if (Boolean.TRUE.equals(flowContext.get(FlowContextKey.FC_IS_CLASS_LABEL_FROM_GPT))) {
            buriedPointData.setNlg_type("gpt");
        } else {
            //
            if (ttsBean == null || ttsBean.getSelectTTs() == null || ttsBean.getSelectTTs().isEmpty()) {
                buriedPointData.setNlg_type("");
            } else {
                buriedPointData.setNlg_type("system");
            }
        }

        //全时免唤醒判断
        boolean isAllTimeWakeUp = (boolean) flowContext.get(FlowContextKey.FC_IS_NO_WAKEUP_TIME);
        if (isAllTimeWakeUp) {
            buriedPointData.setWakeup_word((String) flowContext.get(FlowContextKey.FC_WHOLE_QUERY));
            buriedPointData.setNlu_source("kws");
            buriedPointData.setAsr_type("");
            buriedPointData.setAsr_word("");
        }
        //设置
        buriedPointData.setNlu_classify_level1(flowContext.get(FlowContextKey.FC_WHOLE_QUERY_CLASS_LABEL) == null ? "" : (String) flowContext.get(FlowContextKey.FC_WHOLE_QUERY_CLASS_LABEL));
        buriedPointData.setNlu_classify_level2(flowContext.get(FlowContextKey.FC_QUERY_CLASS_LABEL) == null ? "" : (flowContext.get(FlowContextKey.FC_QUERY_CLASS_LABEL) + "|"));


        //ttsid
        if (ttsBean != null) {
            buriedPointData.setTts_id(ttsBean.getId());
        }
        //null埋点数据提交
        //1.识别文本为null
        if (buriedPointData.getAsr_word() == null || buriedPointData.getAsr_word().isEmpty()) {
            //  buriedPointData.setNull_data("1");
        }
        //2.延时轮退出

        //3.二次交互多轮超时退出
        //4.微场景主动交互场景
        //5.外部信号触发的退出交互或语音退出（如手动点击列表外部、导航二次交互场景页面变动）
        //6.主唤醒（无KWSword）

        //null埋点数据提交
        buriedPointData.setNull_query(flowContext.get(FlowContextKey.FC_NULL_QUERY_HINT) + "");
        //设置环境FlowContextKey.FC_ENV
        buriedPointData.setSpeech_env(flowContext.get(FlowContextKey.FC_ENV) + "");
        //track_id
        buriedPointData.setTrack_id(flowContext.get("ctx-" + flowContext.get(FlowContextKey.SC_DIALOG_ID)) + "");

    }


    private StringBuilder stringBuilderNLGTts = new StringBuilder();
    private StringBuilder stringBuilderNLGAsr = new StringBuilder();

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    /**
     * 上传埋点数据
     */
    public void upLoading(Map<String, Object> flowContext, String agentName, TTSBean ttsBean) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runAble(flowContext, agentName, ttsBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void runAble(Map<String, Object> flowContext, String agentName, TTSBean ttsBean) {
        String requestId = (String) flowContext.get(FlowContextKey.FC_REQ_ID) + "," + (int) flowContext.get(FlowContextKey.FC_QUERY_ID);
        BuriedPointData buriedPointData = DeviceHolder.INS().getDevices().getBuriedPointManager().createBuriedPointBeanToRequestId(requestId);
        //设置通用的数据。
        setCommonData(buriedPointData);
        //把所有数据中的ds数据进行填充
        LogUtils.d(TAG, "ttsIdBean context is ：" + ((ttsBean == null) ? "null" : ttsBean.toString()));
        setDsData(flowContext, buriedPointData, ttsBean, agentName);
        //把mega数据进行填充
//        setAllMegaData(statisticalData, flowContext, agentName);
        //把埋点数据进行填充
//        setStatisticalDataToBuriedPointData(statisticalData);
        //检查网络
        boolean isConnect = DeviceHolder.INS().getDevices().getBuriedPointManager().isConnected(isCar);
        //发送埋点数据
        Object isStreamMode = flowContext.get(FlowContextKey.FC_IS_STREAM_MODE);
        boolean resSend = true;
        if (isStreamMode != null && isStreamMode instanceof Boolean) {
            boolean isRealStreamMode = (boolean) isStreamMode;
            if (isRealStreamMode) {
                int streamMode = (int) flowContext.get(FlowContextKey.FC_STREAM_MODE);
//            public static final int STREAM_MODE_START = 0;
//            public static final int STREAM_MODE_IN = 1;
//            public static final int STREAM_MODE_END = 2;
                switch (streamMode) {
                    case StreamMode.STREAM_MODE_START:
                        resSend = false;
                        if (stringBuilderNLGTts == null) {
                            stringBuilderNLGTts = new StringBuilder();
                        }
                        if (stringBuilderNLGAsr == null) {
                            stringBuilderNLGAsr = new StringBuilder();
                        }
                        String ttsStart = (String) flowContext.get(FlowContextKey.FC_TTS_TEXT);
                        if (ttsStart != null && !ttsStart.isEmpty()) {
                            stringBuilderNLGTts.append(ttsStart);
                        }
                        String asrStart = (String) flowContext.get(FlowContextKey.FC_NO_TASK_TEXT);
                        if (asrStart != null && !asrStart.isEmpty()) {
                            stringBuilderNLGAsr.append(asrStart);
                        }
                        break;
                    case StreamMode.STREAM_MODE_IN:
                        resSend = false;
                        String ttsIn = (String) flowContext.get(FlowContextKey.FC_TTS_TEXT);
                        if (ttsIn != null && !ttsIn.isEmpty()) {
                            stringBuilderNLGTts.append(ttsIn);
                        }
                        String asrIn = (String) flowContext.get(FlowContextKey.FC_NO_TASK_TEXT);
                        if (asrIn != null && !asrIn.isEmpty()) {
                            stringBuilderNLGAsr.append(asrIn);
                        }
                        break;
                    case StreamMode.STREAM_MODE_END:
                        resSend = true;
                        String ttsEnd = (String) flowContext.get(FlowContextKey.FC_TTS_TEXT);
                        if (ttsEnd != null && !ttsEnd.isEmpty()) {
                            stringBuilderNLGTts.append(ttsEnd);
                        }
                        String asrEnd = (String) flowContext.get(FlowContextKey.FC_NO_TASK_TEXT);
                        if (asrEnd != null && !asrEnd.isEmpty()) {
                            stringBuilderNLGAsr.append(asrEnd);
                        }
                        //设置Nlg的参数，删除当前的内容。
                        buriedPointData.setNlg_speak(stringBuilderNLGTts.toString());
                        buriedPointData.setNlg_screen(stringBuilderNLGAsr.toString());
                        stringBuilderNLGTts = null;
                        stringBuilderNLGAsr = null;
                        buriedPointData.setTimestamp_nlu(flowContext.get(FlowContextKey.FC_NLU_EX_COST_TIME) + "");
                        break;
                }
            } else {
                resSend = true;
            }
        }

        if (resSend) {
            LogUtils.d(TAG, "BURIED_POINT, need to send message");
        } else {
            LogUtils.d(TAG, "BURIED_POINT, not need to upload message");
        }

        LogUtils.d(TAG, "agentName:" + agentName);
        DeviceHolder.INS().getDevices().getBuriedPointManager().upLoading(isConnect, requestId, agentName.equals("wakeUp") ? "wake_up" : "agent", resSend, (String) flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION));
    }

//    /**
//     * 把所有的mega数据传入到MegaData
//     */
//    private void setAllMegaData(StatisticalData statisticalData, Map<String, Object> flowContext, String agentName) {
//        setMegaData(MegaBean.Key.appid, statisticalData.getApp_id());
//        setMegaData(MegaBean.Key.appvn, statisticalData.getVin());
//        //这里存放的是埋点数据
////        setMegaData(MegaBean.Key.edes,);
//        //单独设置，根据是唤醒Agent还是识别Agent进行设置。
//        String eid;
//        if (agentName.equals("wakeUp")) {
//            eid = "3010001";
//        } else {
//            eid = "3010002";
//        }
//        setMegaData(MegaBean.Key.eid, eid);
//        //设置session_id和query_id进行拼接，
//        String others = flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SESSION_ID) + "," + flowContext.get(FlowContextKey.FC_REQ_ID);
//        LogUtils.d(TAG, "数据埋点，others的传输内容：" + others);
//        setMegaData(MegaBean.Key.others, others);
//        //设置的是人声开始时间。
//        LogUtils.d(TAG, "开始进行时间设置：");
//        long asrTime = System.currentTimeMillis();
//        setMegaData(MegaBean.Key.ts, Long.valueOf(asrTime));
//
//        LogUtils.d(TAG, "数据埋点，设置mega数据：" + buriedPointManager.getMegaBean().toString());
//    }
//
//    private void setStatisticalDataToBuriedPointData(StatisticalData statisticalData) {
//        LogUtils.d(TAG, "全局的数据：" + (((statisticalData == null) ? "statisticalData为null" : statisticalData.toString())));
//        //设置vin
//        buriedPointData.setVin(statisticalData.getVin());
//        //设置用户唯一标识。
//        buriedPointData.setUser_id(statisticalData.getUser_id());
//        //设置车型
//        buriedPointData.setVehicle_type(statisticalData.getVehicle_type());
//        //设置系统版本
//        buriedPointData.setSystem_version(statisticalData.getSystem_version());
//        //设置语音版本
//        buriedPointData.setApp_version(statisticalData.getApp_version());
//
//        buriedPointData.setLocation(statisticalData.getLocation());
//        //设置时间
//        buriedPointData.setTime(statisticalData.getTime());
//
//        buriedPointData.setInternet(statisticalData.getInternet());
//
//        buriedPointData.setSets(statisticalData.getSets());
//        //声纹开关
//        buriedPointData.setSwitch_type(statisticalData.getSwitch_type());
//        //声纹性别
//        buriedPointData.setGender(statisticalData.getGender());
//        //声纹年纪
//        buriedPointData.setAge(statisticalData.getAge());
//        //声源信息
//        buriedPointData.setVoice_zone(statisticalData.getVoice_zone());
//        //kws文本
//        buriedPointData.setWakeup_word(statisticalData.getWakeup_word());
//        //asr识别类型。
//        buriedPointData.setAsr_type(statisticalData.getAsr_type());
//        //多轮id
//        buriedPointData.setSession_id(statisticalData.getSession_id());
//        //queryID
//        buriedPointData.setQuery_id(statisticalData.getQuery_id());
//        //交互轮数
//        buriedPointData.setInteraction_count(statisticalData.getInteraction_count());
//        //执行语义时间
//        buriedPointData.setTimestamp_nlu(statisticalData.getTimestamp_nlu());
//        //asr文本
//        buriedPointData.setAsr_word(statisticalData.getAsr_word());
//        //离线nlu还是在线nlu
//        buriedPointData.setNlu_online_state(statisticalData.getNlu_online_state());
//        buriedPointData.setNlu_params(statisticalData.getNlu_params());
//        buriedPointData.setNlu_content(statisticalData.getNlu_content());
//        //语义来源
//        buriedPointData.setNlu_source(statisticalData.getNlu_source());
//        //语义执行状态
//        buriedPointData.setNlu_status(statisticalData.getNlu_status());
//        //NLG上屏文案
//        buriedPointData.setNlg_screen(statisticalData.getNlg_screen());
//        //NLG播报文案
//        buriedPointData.setNlg_speak(statisticalData.getNlg_speak());
//        //NLG生成类
//        buriedPointData.setNlg_type(statisticalData.getNlg_type());
//        LogUtils.d(TAG, "数据埋点，设置所有埋点数据：" + buriedPointData.toString());
//    }
//
//    /**
//     * 获取当前埋点的所有信息。
//     *
//     * @return
//     */
//    public BuriedPointData getBuriedPointData() {
//        return buriedPointData;
//    }

    //    public void setAsrFirstUpperScreenTime(String requestId,String ){
//
//    }
    //=============统计asr手字上屏时间===============
    private Map<String, String> asrFirstOnScreenTime = new HashMap<>();

    public void setAsrFirstOnScreenTime(String requestId, String time) {
//        LogUtils.d(TAG, "埋点，asr首字上屏时间统计：当前的requestId为：" + requestId);
        if (!asrFirstOnScreenTime.containsKey(requestId)) {
            asrFirstOnScreenTime.put(requestId, time);
        }
    }

    private String[] getNluParamsString(String nlu) {
        char targetChar = '@';
        int index = nlu.indexOf(targetChar);
        String[] res = new String[2];
        if (index != -1) {
            res[0] = nlu.substring(0, index);
            res[1] = nlu.substring(index);
        } else {
//            System.out.println("目标字符未出现在字符串中");
        }
        return res;
    }


}
