package com.voyah.viewcmd.interceptor;

import java.util.Map;

/**
 * 直接注册拦截器
 */
public interface IDirectRegisterInterceptor {
    /**
     * 应用自己追加的命令和ID, 自定义ID值不限制
     *
     * @return
     */
    Map<String, Integer> bind();

    /**
     * 应用只拦截动作
     *
     * @param text  可见即可说命令
     * @param resId 应用自定义的id
     * @return true: resId已处理， false:resId未处理
     */
    boolean onTriggered(String text, int resId);
}
