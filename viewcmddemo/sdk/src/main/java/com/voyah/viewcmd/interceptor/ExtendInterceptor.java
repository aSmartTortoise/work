package com.voyah.viewcmd.interceptor;

import android.graphics.Point;
import android.view.View;

import com.voyah.viewcmd.Response;
import com.voyah.viewcmd.ViewCmdBean;

import java.util.List;
import java.util.Map;

/**
 * 扩展型拦截器，需要上传控件信息
 */
public class ExtendInterceptor implements IInterceptor<ViewCmdBean> {
    @Override
    public Map<ViewCmdBean, View> bind(View view, String text) {
        return null;
    }

    @Override
    public Map<String, Integer> bind() {
        return null;
    }

    @Override
    public boolean onTriggered(String text, int resId) {
        return false;
    }

    @Override
    public Response onTriggered(View view, String text, int resId) {
        return null;
    }

    @Override
    public List<String> globalBind() {
        return null;
    }

    @Override
    public Response onGlobalTriggered(String text) {
        return null;
    }

    @Override
    public List<String> kwsBind() {
        return null;
    }

    @Override
    public Response onKwsTriggered(String text) {
        return null;
    }

    @Override
    public boolean onLocateAdj(View view, Point point) {
        return false;
    }
}
