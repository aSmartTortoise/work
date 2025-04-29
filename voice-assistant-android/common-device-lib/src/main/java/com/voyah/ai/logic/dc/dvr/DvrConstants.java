package com.voyah.ai.logic.dc.dvr;

/**
 * @author:lcy
 * @data:2025/3/8
 **/
public class DvrConstants {
    public static final int CAMERA_STATUS_NO_INIT = -1;
    public static final int CAMERA_STATUS_OPENING = 0;
    public static final int CAMERA_STATUS_DISCONNECTED = 2;
    public static final int CAMERA_STATUS_RECORDING = 3;
    public static final int CAMERA_STATUS_STOP = 4;
    public static final int CAMERA_STATUS_EXCEPTION = 5;
    public static final String RECORD_AUDIO_IS_ENABLE = "recordAudioIsEnable";
    public static final String RECORD_VIDEO_RESOLUTION_IS_1080 = "recordVideoResolutionIs1080";
    public static final int RECORD_VIDEO_RESOLUTION_720 = 0;
    public static final int RECORD_VIDEO_RESOLUTION_1080 = 1;
    public static final String RECORD_VIDEO_FILE_PATH = "recordVideoFilePath";
    public static final String CAR_INFO_ON_OFF = "carInfoOnOff";
    public static final String DVR_CAMERA_STATUS = "dvrCameraStatus";
    public static final int VIEW_DEFAULT = -1;
    public static final int VIEW_BROWSE = 1;
    public static final int VIDEO_PREVIEW = 2;
    public static final int VIDEO_SETTING = 3;
    public static final int SELECT_DVR_DRIVE = 0;
    public static final int SELECT_DVR_CRASH = 1;
    public static final int SELECT_DVR_GUARD = 2;
    public static final int SELECT_DVR_COLLECT = 3;

    public DvrConstants() {
    }
}
