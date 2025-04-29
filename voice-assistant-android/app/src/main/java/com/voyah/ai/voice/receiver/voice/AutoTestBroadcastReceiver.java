package com.voyah.ai.voice.receiver.voice;


import static androidx.core.content.ContextCompat.getSystemService;
import static com.voice.sdk.constant.ConfigsConstant.WAKEUP_LOCATION_MAP;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log2;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.VoiceConfigManager;
import com.voice.sdk.VoiceEnvPlugin;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.system.WakeupDirection;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.flowchart.FlowChartAgent;
import com.voyah.ai.sdk.bean.DhDirection;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.voice.R;
import com.voyah.ai.voice.sdk.api.component.parameter.command.WakeupCommandParameters;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.nlu.NluInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/2/19
 **/
public class AutoTestBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = AutoTestBroadcastReceiver.class.getSimpleName();
    private static final String AUDIO_INSTRUCTION = "audioInstruction:";
    private static final String TEXT_INSTRUCTION = "textInstruction:";

    private static final String WAKE_UP = "wakeUp:";
    private static final String EX_DIALOG = "exDialog";
    private static final String TTS = "tts:";
    private static final String GRAPH_TEST = "graphTest:";
    private Map<String, String> keyValueMapToGraph = new HashMap<>();
    private Map<String, Object> keyValueMap = new HashMap<>();

    public AutoTestBroadcastReceiver() {
    }

    /**
     * 自动化测试使用
     * 音频指令:
     * AudioInstruction:标识符(读取音频数据作为指令传输)
     * adb shell am broadcast -a com.voyah.ai.autotest.query --es query "AudioInstruction:origin-6db.wav" -f 0x01000000
     * <p>
     * 文本指令:
     * textInstruction:标识符(读取文本数据作为指令传输)
     * text:文本(String)
     * location:声源位置(int)
     * isOnline:离\在线(boolean)
     * adb shell am broadcast -a com.voyah.ai.autotest.query --es query "textInstruction:text=打开电话:location=1:isOnline=true" -f 0x01000000
     * textInstruction后可追加自定义参数,保证格式正确即可
     * <p>
     * 唤醒指令
     * wakeUp:标识符
     * location:声源位置(int)
     * adb shell am broadcast -a com.voyah.ai.autotest.query --es query "wakeUp:location=0" -f 0x01000000
     *
     * <p>
     * 退出语音交互指令
     * exDialog:标识符
     * adb shell am broadcast -a com.voyah.ai.autotest.query --es query "exDialog" -f 0x01000000
     *
     * <p>
     * 1.5发送通知消息
     * msgCenter:1.5发送通知广播
     * adb shell am broadcast -a com.voyah.ai.autotest.query --es query "msgCenter" -f 0x01000000
     *
     * <p>
     * tts播报
     * ttsString:播报内容(广播不支持英文播报)
     * adb shell am broadcast -a com.voyah.ai.autotest.query --es query "tts:ttsString" -f 0x01000000
     *
     * <p>
     * 流程图执行指令
     * graphTest：
     * locations：声源位置信息
     * 需要在代码里添加对应的nlu结果。因为nlu中有&符号，在广播中会识别成别的指令，无法下发下来。
     * adb shell am broadcast -a com.voyah.ai.autotest.query --es query "graphTest:locations=first_row_left" -f 0x01000000
     *
     * <p>
     * 模拟语音状态(开启交互和退出交互)
     * I: 模拟开启交互，带参数awake true， 参数direction 0:主驾 1:副驾 2:后左 3:后右, 不传direction时默认为0
     * II:模拟退出交互，带参数awake false, 不需要携带参数direction
     * adb shell am broadcast -a com.voyah.ai.autotest.awake -f 0x01000000 --ez awake true --ei direction 0
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (StringUtils.equals(VoiceConfigManager.getInstance().getVoiceEnv(), VoiceEnvPlugin.ENV_PROD_NAME)) {
            LogUtils.d(TAG, "production environment rejects the operation");
            return;
        }
        try {
            Log2.d(TAG, "onReceive: action:" + intent.getAction());
            if (TextUtils.equals(intent.getAction(), "com.voyah.ai.autotest.query")) {
                String queryString = intent.getStringExtra("query");
                LogUtils.i(TAG, "queryString is " + queryString);
                String trim = queryString.substring(queryString.indexOf(":") + 1).trim();
                LogUtils.i(TAG, "trim is " + trim);
                if (StringUtils.isBlank(trim))
                    return;

                if (StringUtils.contains(queryString, AUDIO_INSTRUCTION)) {
                    //音频文件
                    VoiceImpl.getInstance().sendAudio(trim);
                } else if (StringUtils.contains(queryString, TEXT_INSTRUCTION)) {
                    keyValueMap.clear();
                    //参数提取
                    getParams(trim);
                    String queryText = keyValueMap.containsKey("text") ? (String) keyValueMap.get("text") : "";
                    int location = keyValueMap.containsKey("location") ? (int) keyValueMap.get("location") : 0;
                    boolean isOnline = keyValueMap.containsKey("isOnline") ? (boolean) keyValueMap.get("isOnline") : true;
                    VoiceImpl.getInstance().queryTest(queryText, location, isOnline);
                } else if (StringUtils.contains(queryString, WAKE_UP)) {
                    getParams(trim);
                    int location = keyValueMap.containsKey("location") ? (int) keyValueMap.get("location") : 0;
                    VoiceImpl.getInstance().wakeUp(location, WakeupCommandParameters.WAKEUP_DEFAULT);
                } else if (StringUtils.contains(queryString, EX_DIALOG)) {
                    VoiceImpl.getInstance().exDialog();
                } else if (StringUtils.contains(queryString, "msgCenter")) {
                    sendMessageToSystemBar();
                } else if (StringUtils.contains(queryString, TTS)) {
                    if (!StringUtils.isBlank(trim)) {
                        DeviceHolder.INS().getDevices().getTts().stopCurTts();
                        DeviceHolder.INS().getDevices().getTts().speak(trim);
                    }
                } else if (StringUtils.contains(queryString, GRAPH_TEST)) {
                    getParams(trim);
                    Map<String, Object> map = new HashMap<>();
                    FlowChartAgent flowChartAgent = new FlowChartAgent();

                    NluInfo nluInfo = new NluInfo();
                    //nlu结果
                    nluInfo.nluStr = "air#switch@switch_type=&open";
                    map.put(FlowContextKey.FC_NLU_RESULT, nluInfo);
                    map.put(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION, keyValueMapToGraph.get("locations"));

                    flowChartAgent.executeAgent(map, null);
                }
//                else if (StringUtils.contains(queryString, "phone")) {
//                    VoiceCarSignalManager.getInstance().aaa(Integer.parseInt(trim));
//                } else if (StringUtils.contains(queryString, "str")) {
//                    VoiceCarSignalManager.getInstance().bb(Integer.parseInt(trim));
//                }
            } else if (TextUtils.equals(intent.getAction(), "com.voyah.ai.autotest.awake")) {
                boolean awake = intent.getBooleanExtra("awake", false);
                int direction = intent.getIntExtra("direction", DhDirection.FRONT_LEFT);
                if (awake) {
                    String awakenLocation = "first_row_left";
                    for (Map.Entry<String, WakeupDirection> entry : WAKEUP_LOCATION_MAP.entrySet()) {
                        if (entry.getValue().getDirection() == direction) {
                            awakenLocation = entry.getKey();
                        }
                    }
                    DeviceHolder.INS().getDevices().getDialogue().onVoiceStateCallback(LifeState.AWAKE, awakenLocation);
                } else {
                    DeviceHolder.INS().getDevices().getDialogue().onVoiceStateCallback(LifeState.ASLEEP);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getParams(String input) {
        String[] pairs = input.split(":");
        keyValueMapToGraph.clear();
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                // 去除空格
                key = key.trim();
                value = value.trim();
                keyValueMapToGraph.put(key, value);
                if (StringUtils.equals(key, "text")) {
                    keyValueMap.put(key, value);
                } else if (StringUtils.equals(key, "location")) {
                    keyValueMap.put(key, Integer.parseInt(value));
                } else if (StringUtils.equals(key, "isOnline")) {
                    keyValueMap.put(key, Boolean.parseBoolean(value));
                }
            }
        }
    }

    private void sendMessageToSystemBar() {
        // 创建通知渠道（Android 8.0+ 需要）
        String CHANNEL_ID = "your_channel_id";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(Utils.getApp(), NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        // 构建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.getApp(), CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_voice_launcher)
                .setContentTitle("语音测试通知")
                .setContentText("语音测试通知，无需关注")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // 显示通知
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Utils.getApp());
        notificationManager.notify(123456, builder.build());
    }
}
