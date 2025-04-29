package com.voyah.ai.sdk.manager;

import static com.voyah.ai.sdk.DhSpeechSDK.assistantConnector;

import android.util.Log;

import com.voyah.ai.sdk.IAgentCallback;
import com.voyah.ai.sdk.IAsrListener;
import com.voyah.ai.sdk.SceneIntent;
import com.voyah.ai.sdk.bean.DhDirection;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.bean.ShortCommand;
import com.voyah.ai.sdk.listener.IHintResultListener;
import com.voyah.ai.sdk.listener.IRmsDbListener;
import com.voyah.ai.sdk.listener.IVAResultListener;
import com.voyah.ai.sdk.listener.IVAStateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 对话接口管理
 */
public class DialogueManager {

    private static final String TAG = DialogueManager.class.getSimpleName();

    /**
     * 开启对话
     */
    public static void startDialogue(@DhDirection int direction) {
        Log.d(TAG, "startDialogue()");
        if (assistantConnector != null) {
            assistantConnector.startDialogue(direction);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 结束对话
     */
    public static void stopDialogue() {
        Log.d(TAG, "stopDialogue()");
        if (assistantConnector != null) {
            assistantConnector.stopDialogue();
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 设置对话结果监听器
     *
     * @param listener 监听器
     */
    public static void setVAResultListener(IVAResultListener listener) {
        Log.d(TAG, "setVAResultListener() called with: listener = [" + listener + "]");
        if (assistantConnector != null) {
            assistantConnector.setVAResultListener(listener);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 设置对话状态监听器
     *
     * @param listener 监听器
     */
    public static void setVAStateListener(IVAStateListener listener) {
        Log.d(TAG, "setVAStateListener() called with: listener = [" + listener + "]");
        if (assistantConnector != null) {
            assistantConnector.setVAStateListener(listener);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 订阅技能
     *
     * @param domainList 技能列表
     */
    public static void subscribeDomains(List<String> domainList) {
        Log.d(TAG, "subscribeDomains() called with: domainList = [" + domainList + "]");
        if (assistantConnector != null) {
            List<String> arrList = new ArrayList<>(domainList);//防止外部调用Arrays.asList()生产的List的add、remove方法时报异常
            assistantConnector.subscribeDomains(arrList);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 取消订阅技能
     *
     * @param domainList 技能列表
     */
    public static void unsubscribeDomains(List<String> domainList) {
        Log.d(TAG, "unsubscribeDomains() called with: domainList = [" + domainList + "]");
        if (assistantConnector != null) {
            assistantConnector.subscribeDomains(new ArrayList<>());
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 注册自定义快捷指令
     *
     * @param lstOfCommand 指令集
     */
    public static void addShortCommands(List<ShortCommand> lstOfCommand) {
        Log.d(TAG, "addShortCommands() called with: lstOfCommand = [" + lstOfCommand + "]");
        if (assistantConnector != null) {
            assistantConnector.setShortCommands(lstOfCommand);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 反注册快捷命令
     */
    public static void removeShortCommands() {
        Log.d(TAG, "removeShortCommands() called");
        if (assistantConnector != null) {
            assistantConnector.setShortCommands(new ArrayList<>());
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 可见即可说UI文本集变化回调，通知语音进行可见即可说的注册操作
     *
     * @param json
     */
    public static void onViewContentChange(String json) {
        Log.d(TAG, "onViewContentChange() called with: json = [" + json + "]");
        if (assistantConnector != null) {
            assistantConnector.onViewContentChange(json);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * asrEvent埋点
     *
     * @param skill  技能
     * @param scene  场景
     * @param intent 意图
     * @param tts    播报
     */
    public static void trackAsrEvent(String skill, String scene, String intent, String tts) {
        Log.d(TAG, "trackAsrEvent() called with: skill = [" + skill + "], scene = [" + scene + "], intent = [" + intent + "], tts = [" + tts + "]");
        if (assistantConnector != null) {
            assistantConnector.trackAsrEvent(skill, scene, intent, tts);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 获取唤醒方位
     *
     * @return
     */
    public static int getWakeupDirection() {
        if (assistantConnector != null) {
            return assistantConnector.getWakeupDirection();
        } else {
            return -1;
        }
    }

    /**
     * 获取hint提示语
     *
     * @param num      hint条数
     * @param listener 监听器
     */
    public static void getHintResult(int num, IHintResultListener listener) {
        if (assistantConnector != null) {
            assistantConnector.getHintResult(num, listener);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 获取语音状态
     *
     * @return
     */
    @LifeState
    public static String getLifeState() {
        if (assistantConnector != null) {
            assistantConnector.getLifeState();
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return LifeState.UNKNOWN;
    }

    /**
     * 当前语音是否处于交互状态
     */
    public static boolean isInteractionState() {
        if (assistantConnector != null) {
            String state = assistantConnector.getLifeState();
            return !LifeState.UNKNOWN.equals(state) && !LifeState.READY.equals(state) && !LifeState.ASLEEP.equals(state);
        } else {
            return false;
        }
    }

    /**
     * 当前语音是否处于播报状态
     */
    public static boolean isSpeakingState() {
        if (assistantConnector != null) {
            String state = assistantConnector.getLifeState();
            return LifeState.SPEAKING.equals(state);
        } else {
            return false;
        }
    }

    /**
     * 上传个性化热词，动态实体：POI名称，微场景名称，联系人等
     *
     * @param json
     */
    public static void uploadPersonalEntity(String json) {
        Log.d(TAG, "uploadPersonalEntity() called with: json = [" + json + "]");
        if (assistantConnector != null) {
            assistantConnector.uploadPersonalEntity(json);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 注册输入人声音量大小回调
     *
     * @param listener 监听器
     */
    public static void registerRmsDbListener(IRmsDbListener listener) {
        Log.d(TAG, "registerRmsDbListener() called with: listener = [" + listener + "]");
        if (assistantConnector != null) {
            assistantConnector.setRmsDbListener(listener);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 反注册注册输入人声音量大小回调
     *
     * @param listener 监听器
     */
    public static void unregisterRmsDbListener(IRmsDbListener listener) {
        Log.d(TAG, "unregisterRmsDbListener() called with: listener = [" + listener + "]");
        if (assistantConnector != null) {
            assistantConnector.setRmsDbListener(null);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    public static int registerAgentX(IAgentCallback callback) {
        if (assistantConnector != null) {
            return assistantConnector.registerAgentX(callback);
        } else {
            Log.e(TAG, "registerAgentX please call initialize first");
            return -2;
        }
    }

    public static void registerAsrListener(IAsrListener asrListener) {
        if (assistantConnector != null) {
            assistantConnector.registerAsrListener(asrListener);
        } else {
            Log.e(TAG, "registerAsrListener please call initialize first");
        }
    }

    public static void unregisterAsrListener(IAsrListener asrListener) {
        if (assistantConnector != null) {
            assistantConnector.unregisterAsrListener(asrListener);
        } else {
            Log.e(TAG, "unregisterAsrListener please call initialize first");
        }
    }

    /**
     * 是否正在显示空调/座椅/ALL-APP等特殊view
     */
    public static boolean isShowingTopCoverView() {
        return isShowingTopCoverView(0);
    }

    /**
     * 是否正在显示空调/座椅/ALL-APP等特殊view
     */
    public static boolean isShowingTopCoverView(int displayId) {
        if (assistantConnector != null) {
            return assistantConnector.isShowingTopCoverView(displayId);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return false;
    }

    public static void setTopCoverViewShowing(int displayId, boolean isShow) {
        if (assistantConnector != null) {
            assistantConnector.setTopCoverViewShowing(displayId, isShow);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 获取语音交互Vpa所在的displayId
     *
     * @return -1:无效值
     */
    public static int getCurVpaDisplayId() {
        if (assistantConnector != null) {
            return assistantConnector.getCurVpaDisplayId();
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return -1;
    }

    /**
     * 主动交互：触发场景意图
     *
     * @param intent 场景意图
     */
    public static int triggerSceneIntent(SceneIntent intent) {
        Log.d(TAG, "triggerSceneIntent() called with: intent = [" + intent.state + "]");
        if (assistantConnector != null) {
            return assistantConnector.triggerSceneIntent(intent);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return -1;
    }
}
