package com.voyah.ai.basecar.media.bean;

import android.os.UserHandle;

import com.voyah.ai.basecar.helper.MegaDisplayHelper;

public enum UserHandleInfo {
    user_null(null,-1),
    central_screen(MegaDisplayHelper.getUserHandleByDisplayId(MegaDisplayHelper.getMainScreenDisplayId()),MegaDisplayHelper.getMainScreenDisplayId()),
    passenger_screen(MegaDisplayHelper.getUserHandleByDisplayId(MegaDisplayHelper.getPassengerScreenDisplayId()),MegaDisplayHelper.getPassengerScreenDisplayId()),
    ceil_screen(MegaDisplayHelper.getUserHandleByDisplayId(MegaDisplayHelper.getCeilingScreenDisplayId()),MegaDisplayHelper.getCeilingScreenDisplayId());

    private final UserHandle userHandle;

    private final int displayId;

    UserHandleInfo(UserHandle userHandle,int displayId){
        this.userHandle = userHandle;
        this.displayId = displayId;
    }

    public UserHandle getUserHandle() {
        return userHandle;
    }

    public int getDisplayId() {
        return displayId;
    }
}
