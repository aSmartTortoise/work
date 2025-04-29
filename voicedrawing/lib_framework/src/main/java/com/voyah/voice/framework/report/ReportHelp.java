package com.voyah.voice.framework.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.voyah.voice.framework.helper.AppHelper;

import mega.car.MegaCarPropHelper;
import mega.car.config.Ecu;
import mega.car.hardware.CarPropertyValue;
import mega.push.report.ReportManager;
import mqtt.message.EventTrackEntity;

/**
 *  埋点公共参数规范文档
 *  https://hav4xarv6k.feishu.cn/sheets/FwLIsEuxlhWIObtTi1QcejvIn0c
 */
public class ReportHelp {
    private static final String TAG = "ReportHelp";
    @SuppressLint("StaticFieldLeak")
    private static final ReportHelp REPORT_HELP = new ReportHelp();
    private static final String APP_ID = "144";

    private Context context;

    private ReportHelp() {

    }


    private String getStringValue(CarPropertyValue<?> propValue) {
        if (propValue != null && propValue.getValue() instanceof String) {
            return (String) propValue.getValue();
        }
        return "";
    }

    @SuppressLint("HardwareIds")
    public String getVin() {
        String vin = null;
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd("getprop ro.build.voice_vin", false);
        if (commandResult.result == 0) {
            vin = commandResult.successMsg;
            LogUtils.iTag(TAG, "getprop ro.build.voice_vin:" + vin);
        }
        if (!TextUtils.isEmpty(vin)) {
            return vin;
        }
        MegaCarPropHelper carPropHelper = MegaCarPropHelper.getInstance(context, null);
        vin = getStringValue(carPropHelper.getPropertyRaw(Ecu.ID_VIN));
        if (TextUtils.isEmpty(vin) || TextUtils.equals("vin-0123456789ABC", vin)) {
            Log.i(TAG, "vin is null or vin is default, load androidId");
            vin = Settings.Secure.getString(
                    AppHelper.INSTANCE.getApplication().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        Log.i(TAG, "getVin vin:" + vin);
        return vin;
    }

    public static ReportHelp getInstance() {
        return REPORT_HELP;
    }

    public void init(Context context) {
        Log.i(TAG, "init");
        this.context = context.getApplicationContext();
        ReportManager.getInstance().init(context.getApplicationContext());
    }

    private EventTrackEntity.EventTrack.Builder basicBuild(Report report) {
        EventTrackEntity.EventTrack.Builder builder = EventTrackEntity.EventTrack
                .getDefaultInstance().toBuilder();

        builder.setVn(AppUtils.getAppVersionName());
        builder.setTs(System.currentTimeMillis());
        builder.setEdes(GsonUtils.toJson(report));
        return builder;
    }

    private EventTrackEntity.EventTrack.Builder basicBuild(Report report, TrackOther trackOther) {
        EventTrackEntity.EventTrack.Builder builder = EventTrackEntity.EventTrack
                .getDefaultInstance().toBuilder();
        builder.setAppid(APP_ID);
        builder.setVn(AppUtils.getAppVersionName());
        builder.setTs(System.currentTimeMillis());
        builder.setEdes(GsonUtils.toJson(report));
        if (trackOther != null) {
            String other = GsonUtils.toJson(trackOther);
            Log.i(TAG, "basicBuild: other:" + other);
            builder.setOthers(other);
        }
        return builder;
    }

    public void clickHistory(Report report, TrackOther trackOther) {
        Log.i(TAG, "clickHistory:" + GsonUtils.toJson(report));
        new Thread(() -> {
            EventTrackEntity.EventTrack.Builder builder = basicBuild(report, trackOther);
            builder.setEid(APP_ID + "0004000");
            ReportManager.getInstance().report(builder.build());
        }).start();
    }

    public void selectStyle(Report report, TrackOther trackOther) {
        Log.i(TAG, "selectStyle:" + GsonUtils.toJson(report));
        new Thread(() -> {
            EventTrackEntity.EventTrack.Builder builder = basicBuild(report, trackOther);
            builder.setEid(APP_ID + "0006000");
            ReportManager.getInstance().report(builder.build());
        }).start();
    }

    public void savePhoto(Report report, TrackOther trackOther) {
        Log.i(TAG, "savePhoto:" + GsonUtils.toJson(report));
        new Thread(() -> {
            EventTrackEntity.EventTrack.Builder builder = basicBuild(report, trackOther);
            builder.setEid(APP_ID + "0005000");
            ReportManager.getInstance().report(builder.build());
        }).start();
    }

    public void reDraw(Report report, TrackOther trackOther) {
        Log.i(TAG, "reDraw:" + GsonUtils.toJson(report));
        new Thread(() -> {
            EventTrackEntity.EventTrack.Builder builder = basicBuild(report, trackOther);
            builder.setEid(APP_ID + "0007000");
            ReportManager.getInstance().report(builder.build());
        }).start();
    }

    public void remainTime(Report report, TrackOther trackOther) {
        Log.i(TAG, "remainTime:" + GsonUtils.toJson(report));
        new Thread(() -> {
            EventTrackEntity.EventTrack.Builder builder = basicBuild(report, trackOther);
            builder.setEid(APP_ID + "0008000");
            ReportManager.getInstance().report(builder.build());
        }).start();
    }

    public void buyDrawTime(Report report, TrackOther trackOther) {
        Log.i(TAG, "buyDrawTime:" + GsonUtils.toJson(report));
        new Thread(() -> {
            EventTrackEntity.EventTrack.Builder builder = basicBuild(report, trackOther);
            builder.setEid(APP_ID + "0009000");
            ReportManager.getInstance().report(builder.build());
        }).start();
    }

    public void openApp(Report report, TrackOther trackOther) {
        Log.i(TAG, "openApp:" + GsonUtils.toJson(report));
        new Thread(() -> {
            EventTrackEntity.EventTrack.Builder builder = basicBuild(report, trackOther);
            builder.setEid(APP_ID + "0001000");
            ReportManager.getInstance().report(builder.build());
        }).start();
    }

    public void activeTime(Report report, TrackOther trackOther) {
        Log.i(TAG, "activeTime:" + GsonUtils.toJson(report));
        new Thread(() -> {
            EventTrackEntity.EventTrack.Builder builder = basicBuild(report, trackOther);
            builder.setEid(APP_ID + "0002000");
            ReportManager.getInstance().report(builder.build());
        }).start();
    }

    public void editHistory(Report report, TrackOther trackOther) {
        Log.i(TAG, "editHistory:" + GsonUtils.toJson(report));
        new Thread(() -> {
            EventTrackEntity.EventTrack.Builder builder = basicBuild(report, trackOther);
            builder.setEid(APP_ID + "0010000");
            ReportManager.getInstance().report(builder.build());
        }).start();
    }

    public void drawResult(Report report, TrackOther trackOther) {
        Log.i(TAG, "drawResult:" + GsonUtils.toJson(report));
        new Thread(() -> {
            EventTrackEntity.EventTrack.Builder builder = basicBuild(report, trackOther);
            builder.setEid(APP_ID + "0003000");
            ReportManager.getInstance().report(builder.build());
        }).start();
    }
}
