package com.voyah.device.drawing;

import static com.voyah.vcos.BurialPointPostExtKt.EVENT_TYPE_ACTIVE_TIME;
import static com.voyah.vcos.BurialPointPostExtKt.EVENT_TYPE_DRAWING_HISTORY;
import static com.voyah.vcos.BurialPointPostExtKt.EVENT_TYPE_DRAWING_RESULT;
import static com.voyah.vcos.BurialPointPostExtKt.EVENT_TYPE_EDIT_HISTORY;
import static com.voyah.vcos.BurialPointPostExtKt.EVENT_TYPE_OPEN_APP;
import static com.voyah.vcos.BurialPointPostExtKt.EVENT_TYPE_REDRAW;
import static com.voyah.vcos.BurialPointPostExtKt.EVENT_TYPE_REMAIN_TIME;
import static com.voyah.vcos.BurialPointPostExtKt.EVENT_TYPE_SAVE_PHOTO;
import static com.voyah.vcos.BurialPointPostExtKt.EVENT_TYPE_SELECT_STYLE;
import static com.voyah.vcos.BurialPointPostExtKt.EVENT_TYPE_TO_GOODS_DETAIL;

import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;
import com.lib.common.voyah.service.IVoiceService;
import com.voyah.voice.framework.report.Report;
import com.voyah.voice.framework.report.ReportHelp;
import com.voyah.voice.framework.report.TrackOther;

/**
 * author : jie wang
 * date : 2025/3/18 11:37
 * description :
 */
public class VoiceService implements IVoiceService {


    private VoiceService() {
    }

    private static class Holder {
        private static final VoiceService INSTANCE = new VoiceService();
    }

    public static VoiceService getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void postBurialPointEvent(String eventType, String triggerMode, long time, String extraStr) {
        LogUtils.i("postBurialPointEvent eventType:" + eventType);
        TrackOther trackOther = null;
        Report report = new Report(triggerMode, time, extraStr);
        switch (eventType) {
            case EVENT_TYPE_DRAWING_HISTORY:
                ReportHelp.getInstance().clickHistory(
                        report,
                        trackOther
                );
                break;
            case EVENT_TYPE_REMAIN_TIME:
                ReportHelp.getInstance().remainTime(
                        report,
                        trackOther);
                break;
            case EVENT_TYPE_SELECT_STYLE:
                ReportHelp.getInstance().selectStyle(
                        report,
                        trackOther);
                break;
            case EVENT_TYPE_REDRAW:
                ReportHelp.getInstance().reDraw(
                        report,
                        trackOther);
                break;
            case EVENT_TYPE_TO_GOODS_DETAIL:
                ReportHelp.getInstance().buyDrawTime(
                        report,
                        trackOther);
                break;
            case EVENT_TYPE_SAVE_PHOTO:
                ReportHelp.getInstance().savePhoto(
                        report,
                        trackOther);
                break;
            case EVENT_TYPE_OPEN_APP:
                ReportHelp.getInstance().openApp(
                        report,
                        trackOther);
                break;
            case EVENT_TYPE_ACTIVE_TIME:
                ReportHelp.getInstance().activeTime(
                        report,
                        trackOther);
                break;
            case EVENT_TYPE_EDIT_HISTORY:
                ReportHelp.getInstance().editHistory(
                        report,
                        trackOther);
                break;
            case EVENT_TYPE_DRAWING_RESULT:
                ReportHelp.getInstance().drawResult(
                        report,
                        trackOther);
                break;
        }

    }

    @Override
    public int getDisplayId() {
        return 0;
    }

    @Override
    public void sendBroadcastToGallery(Context context, Intent intent) {
        context.sendBroadcast(intent);
    }

    @Override
    public String getWakeUpWord() {
        return "你好岚图";
    }

    @Override
    public Context getDisplayContext(Context context, int displayId) {
        LogUtils.d("getDisplayContext displayId:" + displayId);
        return context;
    }
}
