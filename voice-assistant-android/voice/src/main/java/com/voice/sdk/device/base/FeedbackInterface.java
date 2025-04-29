package com.voice.sdk.device.base;

import com.voice.sdk.device.viewcmd.ViewCmdCache;

import java.util.List;

public interface FeedbackInterface {
    void uploadViewCmd(List<String> list, ViewCmdCache globalViewCmdCache, ViewCmdCache kwsViewCmdCache);

    void uploadPersonalEntity(String pkgName, String json);

    void onViewContentChange(String pkg, String json);
}
