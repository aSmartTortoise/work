package com.voice.sdk.device.base;

import com.voyah.ai.sdk.IVprCallback;
import com.voyah.ai.sdk.bean.UserVprInfo;

import java.util.List;

public interface VprInterface {

    String startRegisterVpr(String vprId, int direction, IVprCallback callback);

    void stopRegisterVpr(String id, int errCode);

    int deleteUserVpr(String id);

    int saveUserVprInfo(UserVprInfo vp);

    List<UserVprInfo> getRegisteredVprList();

    void startRecordingVpr(String id, String text);

    void stopRecognizeVpr(String id, String text, int errCode);

    int getMaxSupportedVprNum();

    UserVprInfo getUserVprInfo(String id);
}
