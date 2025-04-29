package com.voyah.viewcmd.interceptor;

import android.graphics.Point;
import android.view.View;

import com.voyah.viewcmd.Response;

import java.util.List;
import java.util.Map;

public interface IInterceptor<T> {
    /**
     * 资源id绑定相应可见即可说命令
     *
     * @param view 控件
     * @param text 控件文本
     * @return
     */
    Map<T, View> bind(View view, String text);

    /**
     * 应用自己追加的命令和ID, 自定义ID值范围0-99
     *
     * @return
     */
    Map<String, Integer> bind();

    /**
     * 应用只拦截动作
     *
     * @param text  可见即可说命令
     * @param resId 应用自定义的id/手势id/控件资源id
     * @return true: resId已处理， false:resId未处理
     */
    boolean onTriggered(String text, int resId);

    /**
     * 应用完全拦截某个动作的响应（动作+动效+播报）
     *
     * @param view  执行动作的view
     * @param text  可见即可说命令
     * @param resId 应用自定义的id/手势id/控件资源id
     * @return 非null代表拦截， null代表不拦载
     */
    Response onTriggered(View view, String text, int resId);

    /**
     * 全局可见注册 (全局命令不受屏和音区方位限制均可响应)
     */
    List<String> globalBind();

    /**
     * 全局可见拦截响应 (全局命令不受屏和音区方位限制均可响应)
     *
     * @param text 可见即可说命令
     * @return 非null代表拦截， null代表不拦载
     */
    Response onGlobalTriggered(String text);

    /**
     * 免唤醒可见注册 (kws命令不受语音状态和屏限制均可响应)
     */
    List<String> kwsBind();

    /**
     * 免唤醒可见拦截响应 (kws命令不受语音状态和屏限制均可响应)
     *
     * @param text 可见即可说命令
     * @return 非null代表拦截， null代表不拦载
     */
    Response onKwsTriggered(String text);

    /**
     * 响应座标校正
     *
     * @param view  控件
     * @param point 点击的原始座标
     * @return true: resId已处理， false:resId未处理
     */
    boolean onLocateAdj(View view, Point point);
}
