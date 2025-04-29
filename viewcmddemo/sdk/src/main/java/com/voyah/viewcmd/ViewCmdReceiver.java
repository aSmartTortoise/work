package com.voyah.viewcmd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class ViewCmdReceiver extends BroadcastReceiver {
    private static final String TAG = ViewCmdReceiver.class.getSimpleName();

    /**
     * 使用广播模拟可见即可说响应
     * adb shell am broadcast -a com.voyah.ai.action.VIEW_CMD_MOCK -e text "语音唤醒" -e prompt "Switch"
     * <p>
     * adb shell am broadcast -a com.voyah.ai.action.VIEW_CMD_MOCK --es text "语音唤醒" --ei direction 0
     */
    public static final String ACTION_VIEW_CMD_TEXT = "com.voyah.ai.action.VIEW_CMD_MOCK";

    /**
     * 语音服务端主动触发扫描
     */
    public static final String ACTION_TRIGGER_CHANGED = "com.voyah.ai.action.TRIGGER_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.equals(intent.getAction(), ACTION_VIEW_CMD_TEXT)) {
            String prompt = intent.getStringExtra("prompt");
            String text = intent.getStringExtra("text");
            int direction = intent.getIntExtra("direction", -1);
            String type = intent.getStringExtra("type");
            int displayId = intent.getIntExtra("displayId", -1);
            if (type == null) {
                type = ViewCmdType.TYPE_NORMAL;
            }
            VaLog.d(TAG, "onReceive: ACTION_VIEW_CMD_TEXT, prompt:" + prompt + ",word:" + text +
                    ",type:" + type + ",direction:" + direction + ", displayId:" + displayId);
            Response response = VoiceViewCmdManager.getInstance().onUIWordTriggered(prompt, text, type, direction, displayId);
            VaLog.d(TAG, "response:" + response);
            try {
                if (response != null) {
                    com.voyah.ai.sdk.manager.TTSManager.speakImt(response.text);
                }
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            }
        } else if (TextUtils.equals(intent.getAction(), ACTION_TRIGGER_CHANGED)) {
            int displayId = intent.getIntExtra("displayId", -1);
            VaLog.d(TAG, "onReceive: ACTION_TRIGGER_CHANGED, displayId:" + displayId);
            VoiceViewCmdManager.getInstance().triggerNotifyUiChangeById(displayId);
        }
    }

}
