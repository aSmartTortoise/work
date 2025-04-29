package com.voyah.ai.logic.dc.dvr;

/**
 * @author:lcy
 * @data:2025/3/8
 **/
public interface Constant {
    String COMMON_TAG = "COMMON";
    String CAMERA_TAG = "VOYAH_CAMERA";
    String GALLERY_TAG = "VOYAH_GALLERY";
    String GESTURE_TAG = "VOYAH_GESTURE";
    String GESTURE_SERVICE_PACKAGE = "com.voyah.cockpit.gesture";
    String GESTURE_SERVICE_CLASS = "com.voyah.cockpit.gesture.service.GestureService";
    String CAMERA_SERVICE_PACKAGE = "com.voyah.cockpit.camera";
    String CAMERA_SERVICE_CLASS = "com.voyah.cockpit.camera.service.CameraService";
    String GALLERY_SERVICE_PACKAGE = "com.voyah.cockpit.gallery";
    String GALLERY_SERVICE_CLASS = "com.voyah.cockpit.gallery.service.GalleryService";

    public interface H56C_DisplayIds {
        int SCREEN_MAIN = 0;
        int SCREEN_TOP = 3;
        int SCREEN_SECONDARY = 5;
    }

    public interface SourceType {
        int GESTURE = 0;
        int VOICE = 1;
        int KEY = 2;
        int ELSE = 3;
    }

    public interface GalleryType {
        int LOCAL = 0;
        int CLOUD = 1;
        int USB = 2;
        int TRANSFER = 3;
        int CONNECT = 4;
        int GALLERY_TYPE_DEFAULT = 5;
        int GALLERY_TYPE_TRAVEL = 6;
        int GALLERY_TYPE_STORE = 7;
        int GALLERY_TYPE_AI_ALBUM = 8;
    }

    public interface GestureForAVValue {
        int STATIC_OK = 5;
        int STATIC_FIST = 6;
    }

    public @interface CaptureType {
        int TAKE_PHOTO = 0;
        int RECORD = 1;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public interface CameraScene {
        int TAKE_PHOTO = 0;
        int RECORD = 1;
    }

    public @interface CameraType {
        int DMS = 0;
        int OMS = 1;
        int TOF = 2;
        int DVR = 3;
        int CMS = 4; //电子后视镜
    }

    public interface SwitchMode {
        String FULL_SCREEN = "full_screen"; //全屏
        String SMALL_WINDOW = "small_window"; //小屏
    }
}
