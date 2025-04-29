package com.voyah.ai.voice.provider;


import static com.voice.sdk.constant.ConfigsConstant.AUTHORITY_EXPORT;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_AI_MODEL_PREFERENCE;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_ALL_NETWORK;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_CONTINUOUS_DIALOGUE;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_DIALECT;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_FREE_WAKEUP;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_MIC_MASK;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_MULTI_ZONE_DIALOGUE;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_MUSIC_PREFERENCE;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_NEARBY_TTS;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_NEWS_PUSH;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_NEWS_PUSH_CONFIG_TIME;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_ONESHOT;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_TTS_LOG;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_VIDEO_PREFERENCE;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_VOICE_PRINT_RECOGNIZE;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_VPR_USER;
import static com.voice.sdk.constant.ConfigsConstant.SP_KEY_WAKEUP;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.base.SettingsInterface;
import com.voyah.ai.sdk.bean.DhDialect;
import com.voyah.ai.sdk.bean.DhSwitch;

/**
 * 语音对外提供的数据provider
 */
public class ExportProvider extends ContentProvider {

    private static final int SETTINGS_CODE = 1; // 语音设置
    private static final int WAKEUP_DIRECTION_CODE = 2; // 唤醒方位
    private static final int SPLIT_SCREEN_STATUS_CODE = 3; // 分屏状态
    private static final int NLU_VIEW_CMD_CODE = 4; // 语义可见结果


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY_EXPORT, "settings/*", SETTINGS_CODE);
        sUriMatcher.addURI(AUTHORITY_EXPORT, "wakeup_direction", WAKEUP_DIRECTION_CODE);
        sUriMatcher.addURI(AUTHORITY_EXPORT, "split_screen_status", SPLIT_SCREEN_STATUS_CODE);
        sUriMatcher.addURI(AUTHORITY_EXPORT, "nlu_view_cmd", NLU_VIEW_CMD_CODE);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SettingsInterface settingsManager = DeviceHolder.INS().getDevices().getVoiceSettings();
        if (sUriMatcher.match(uri) == SETTINGS_CODE) {
            String setting = uri.getPathSegments().get(1);
            LogUtils.d("setting:" + setting);
            String[] tableCursor = new String[]{setting};
            MatrixCursor matrixCursor = new MatrixCursor(tableCursor);
            switch (setting) {
                case SP_KEY_WAKEUP:
                    boolean wakeup = settingsManager.isEnableSwitch(DhSwitch.MainWakeup);
                    matrixCursor.addRow(new Object[]{wakeup ? 1 : 0});
                    return matrixCursor;
                case SP_KEY_FREE_WAKEUP:
                    boolean freeWakeup = settingsManager.isEnableSwitch(DhSwitch.FreeWakeup);
                    matrixCursor.addRow(new Object[]{freeWakeup ? 1 : 0});
                    return matrixCursor;
                case SP_KEY_ONESHOT:
                    boolean oneshot = settingsManager.isEnableSwitch(DhSwitch.Oneshot);
                    matrixCursor.addRow(new Object[]{oneshot ? 1 : 0});
                    return matrixCursor;
                case SP_KEY_MIC_MASK:
                    int micMask = settingsManager.getUserVoiceMicMask();
                    matrixCursor.addRow(new Object[]{micMask});
                    return matrixCursor;
                case SP_KEY_CONTINUOUS_DIALOGUE:
                    boolean continuousDialogue = settingsManager.isEnableSwitch(DhSwitch.ContinuousDialogue);
                    matrixCursor.addRow(new Object[]{continuousDialogue ? 1 : 0});
                    return matrixCursor;
                case SP_KEY_MULTI_ZONE_DIALOGUE:
                    boolean multiZoneDialogue = settingsManager.isEnableSwitch(DhSwitch.MultiZoneDialogue);
                    matrixCursor.addRow(new Object[]{multiZoneDialogue ? 1 : 0});
                    return matrixCursor;
                case SP_KEY_NEARBY_TTS:
                    boolean nearbyTTS = settingsManager.isEnableSwitch(DhSwitch.NearbyTTS);
                    matrixCursor.addRow(new Object[]{nearbyTTS ? 1 : 0});
                    return matrixCursor;
                case SP_KEY_VOICE_PRINT_RECOGNIZE:
                    boolean vpr = settingsManager.isEnableSwitch(DhSwitch.VoicePrintRecognize);
                    matrixCursor.addRow(new Object[]{vpr ? 1 : 0});
                    return matrixCursor;
                case SP_KEY_VPR_USER:
                    matrixCursor.addRow(new Object[]{1});
                    return matrixCursor;
                case SP_KEY_MUSIC_PREFERENCE:
                    int musicPreference = settingsManager.getMusicPreference();
                    matrixCursor.addRow(new Object[]{musicPreference});
                    return matrixCursor;
                case SP_KEY_VIDEO_PREFERENCE:
                    int videoPreference = settingsManager.getVideoPreference();
                    matrixCursor.addRow(new Object[]{videoPreference});
                    return matrixCursor;
                case SP_KEY_DIALECT:
                    DhDialect dialect = settingsManager.getCurrentDialect();
                    matrixCursor.addRow(new Object[]{dialect.id});
                    return matrixCursor;
                case SP_KEY_ALL_NETWORK:
                    boolean allNetwork = settingsManager.isAllNetworkEnabled();
                    matrixCursor.addRow(new Object[]{allNetwork ? 1 : 0});
                    return matrixCursor;
                case SP_KEY_TTS_LOG:
                    boolean ttsLog = settingsManager.isTtsLogDumpEnabled();
                    matrixCursor.addRow(new Object[]{ttsLog ? 1 : 0});
                    return matrixCursor;
                case SP_KEY_NEWS_PUSH:
                    boolean newsPush = settingsManager.isEnableSwitch(DhSwitch.NewsPush);
                    matrixCursor.addRow(new Object[]{newsPush ? 1 : 0});
                    return matrixCursor;
                case SP_KEY_NEWS_PUSH_CONFIG_TIME:
                    String time = settingsManager.getNewsPushConfigTime();
                    matrixCursor.addRow(new Object[]{time});
                    return matrixCursor;
                case SP_KEY_AI_MODEL_PREFERENCE:
                    int aiModelPreference = settingsManager.getAiModelPreference();
                    matrixCursor.addRow(new Object[]{aiModelPreference});
                    return matrixCursor;
            }
        } else if (sUriMatcher.match(uri) == WAKEUP_DIRECTION_CODE) {
            String direction = uri.getPathSegments().get(0);
            if ("wakeup_direction".equals(direction)) {
                return buildWakeupDirectionCursor();
            }
        } else if (sUriMatcher.match(uri) == SPLIT_SCREEN_STATUS_CODE) {
            String splitStatus = uri.getPathSegments().get(0);
            if ("split_screen_status".equals(splitStatus)) {
                return buildSplitScreenStatusCursor();
            }
        } else if (sUriMatcher.match(uri) == NLU_VIEW_CMD_CODE) {
            String nluViewCmd = uri.getPathSegments().get(0);
            if ("nlu_view_cmd".equals(nluViewCmd)) {
                return buildNluViewCmdCursor();
            }
        }
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentvalues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private MatrixCursor buildWakeupDirectionCursor() {
        int direction = DeviceHolder.INS().getDevices().getDialogue().getDirection();
        String[] tableCursor = new String[]{"wakeup_direction"};
        MatrixCursor matrixCursor = new MatrixCursor(tableCursor);
        matrixCursor.addRow(new Object[]{direction});
        return matrixCursor;
    }

    private MatrixCursor buildSplitScreenStatusCursor() {
        int state = DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSplitScreening() ? 1 : 0;
        String[] tableCursor = new String[]{"state"};
        MatrixCursor matrixCursor = new MatrixCursor(tableCursor);
        matrixCursor.addRow(new Object[]{state});
        return matrixCursor;
    }

    private MatrixCursor buildNluViewCmdCursor() {
        int state = DeviceHolder.INS().getDevices().getViewCmd().isSupportNluViewCmd() ? 1 : 0;
        String[] tableCursor = new String[]{"type"};
        MatrixCursor matrixCursor = new MatrixCursor(tableCursor);
        matrixCursor.addRow(new Object[]{state});
        return matrixCursor;
    }
}