package com.voice.sdk.device.system;

import java.util.HashMap;

public interface ShareInterface {

    boolean getDlnaState(String targetScreen, boolean onOff);

    boolean openDlnaState(String targetScreen);

    int openShareApp();

    int closeDlna();

    int getConnectState();

    boolean isMirrorOpened();

    int isDlnaVideoPlaying();

    int startDlnaVideo();

    int pauseDlnaVideo();

    boolean isDlnaOpened();

    boolean isDlnaMainActivity();

    boolean isFileTransferOpened();

    int openFileTransfer();

    int openScreenMirroring();

    int closeScreenMirroring();

    int openScreenMirroringFullScreen();

    int openScreenMirroringSmallWindow();

    boolean isNewPlan();
}
