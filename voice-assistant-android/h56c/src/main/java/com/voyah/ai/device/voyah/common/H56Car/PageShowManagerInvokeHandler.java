package com.voyah.ai.device.voyah.common.H56Car;

import com.blankj.utilcode.util.Utils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.appadapter.aidlimpl.IPageShowManagerImpl;
import com.voyah.cockpit.settings.IPageShowManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Date 2024/8/1 18:42
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class PageShowManagerInvokeHandler implements InvocationHandler {

    private static volatile PageShowManagerInvokeHandler instance;

    private PageShowManagerInvokeHandler() {

    }

    public static PageShowManagerInvokeHandler getInstance() {
        if (instance == null) {
            synchronized (PageShowManagerInvokeHandler.class) {
                if (instance == null) {
                    instance = new PageShowManagerInvokeHandler();
                }
            }
        }
        return instance;
    }

    private final IPageShowManager pageShowManager = IPageShowManagerImpl.getInstance(Utils.getApp());

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LogUtils.d("IPageShowManager", "代理实现，代理方法：" + method.getName());
        Thread handle = new Thread(() -> {
            if (!"toString".equalsIgnoreCase(method.getName())) {

            }
        });
        handle.start();
        handle.join(200);
        return method.invoke(pageShowManager, args);
    }
}
