package com.voyah.viewcmd;

import com.voyah.viewcmd.interceptor.IDirectRegisterInterceptor;
import com.voyah.viewcmd.interceptor.IInterceptor;

import java.util.Map;

class ViewAttrs {
    int displayId;
    boolean isTopCover;
    boolean isSticky;
    boolean isDirect;
    IInterceptor<?> interceptor;
    IDirectRegisterInterceptor directInterceptor;
    Map<String, Object> directDataMap;

    ViewAttrs(int displayId, boolean isTopCover, boolean isSticky, boolean isDirect) {
        this.displayId = displayId;
        this.isTopCover = isTopCover;
        this.isSticky = isSticky;
        this.isDirect = isDirect;
    }

    @Override
    public String toString() {
        return "ViewAttrs{" +
                "displayId=" + displayId +
                ", isTopCover=" + isTopCover +
                ", isSticky=" + isSticky +
                ", isDirect=" + isDirect +
                '}';
    }
}