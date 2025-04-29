package com.voice.sdk.tts;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.R;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.Locale;
import java.util.Random;

public class TtsReplyUtils {
    private static final String REPLAY_SPLIT = "&";


    public static String getReplayByRandom(String replay) {
        String response = replay;
        if (replay.contains(REPLAY_SPLIT)) {
            String[] split = replay.split(REPLAY_SPLIT);
            int index = (int) (System.currentTimeMillis() % split.length);
            response = split[index];
        }
        return response;
    }

    public static String getNotSupportReplay() {
        return getReplayByRandom("当前应用还不支持这个操作&当前应用还不支持这样控制");
    }

    public static TTSBean getTtsBean(String ttsId) {
        return getTtsBean(ttsId, 2);
    }

    public static TTSBean getTtsBean(String ttsId, int version) {
        return TTSIDConvertHelper.getInstance().getTTSBean(ttsId, version);
    }

    public static TTSBean getTtsBean(String ttsId, String target, String replacement) {
        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(ttsId, 2);
        String tts = ttsBean.getSelectTTs();
        tts = tts.replace(target, replacement);
        ttsBean.setSelectTTs(tts);
        return ttsBean;
    }

    public static TTSBean getTtsBean(String ttsId, String target1, String replacement1, String target2, String replacement2) {
        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(ttsId, 2);
        String tts = ttsBean.getSelectTTs();
        tts = tts.replace(target1, replacement1);
        tts = tts.replace(target2, replacement2);
        ttsBean.setSelectTTs(tts);
        return ttsBean;
    }

    public static TTSBean getTtsBean(String ttsId, String target1, String replacement1, String target2, String replacement2, String target3, String replacement3) {
        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(ttsId, 2);
        if (ttsBean != null) {
            String tts = ttsBean.getSelectTTs();
            tts = tts.replace(target1, replacement1);
            tts = tts.replace(target2, replacement2);
            tts = tts.replace(target3, replacement3);
            ttsBean.setSelectTTs(tts);
        }
        return ttsBean;
    }

    public static TTSBean getTtsBean(int ttsId, String... args) {
        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(String.valueOf(ttsId), 2);
        if (ttsBean != null) {
            String tts = ttsBean.getSelectTTs();
            LogUtils.i("TTSBeanX", "getTtsBean:" + ttsId + ",tts:" + tts);
            tts = tts.replaceAll("@\\{(.*?)\\}", "%s");
            LogUtils.i("TTSBeanX", "tts replaced:" + tts);
            if (args != null && args.length > 0) {
                for (String a : args) {
                    LogUtils.i("TTSBeanX", "args:" + a);
                }
                tts = String.format(Locale.getDefault(), tts, (Object) args);
            }
            ttsBean.setSelectTTs(tts);
            LogUtils.i("TTSBeanX", "setSelectTTs:" + tts);
        } else {
            ttsBean = new TTSBean();
            LogUtils.e("TTSBeanX", "ttsId is null:" + ttsId);
        }
        return ttsBean;
    }

    public static TTSBean getTtsBeanByRegex(String ttsId, String... args) {
        TTSBean ttsBean = TTSIDConvertHelper.getInstance().getTTSBean(ttsId, 2);
        String tts = ttsBean.getSelectTTs();
        LogUtils.i("TTSBeanX", "getTtsBeanByRegex, ttsId:" + ttsId + ",tts:" + tts);
        tts = tts.replaceAll("@\\{(.*?)\\}", "%s");
        LogUtils.i("TTSBeanX", "getTtsBeanByRegex, tts replaced:" + tts);
        if (args != null && args.length > 0) {
            for (String a : args) {
                LogUtils.i("TTSBeanX", "getTtsBeanByRegex, args:" + a);
            }
            tts = String.format(Locale.getDefault(), tts, (Object[]) args);
        }
        ttsBean.setSelectTTs(tts);
        return ttsBean;
    }

    public static TTSBean getTtsBean(TTSBean ttsBean, String target, String replacement) {
        String tts = ttsBean.getSelectTTs();
        tts = tts.replace(target, replacement);
        ttsBean.setSelectTTs(tts);
        return ttsBean;
    }

    public static TTSBean getTtsBeanText(String tts) {
        TTSBean ttsBean = new TTSBean();
        ttsBean.setSelectTTs(tts);
        return ttsBean;
    }

    /**
     * 获取可见即可说回复语
     */
    public static String getViewCmdReply() {
        String[] replies = Utils.getApp().getResources().getStringArray(R.array.view_cmd_reply);
        int i = new Random().nextInt(replies.length);
        return replies[i];
    }
}
