package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/8/2 9:55
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class WindowControlParams extends BaseParams {
    @Override
    public void exe() {
        mContentProviderHelper.addParams("window_ChildLock_2", ParamsType.BOOLEAN, "", "", false, "左后儿童锁");
        mContentProviderHelper.addParams("window_ChildLock_3", ParamsType.BOOLEAN, "", "", false, "右后儿童锁");

        mContentProviderHelper.addParams("window_Window", ParamsType.INT, "127", "0", 50, "车窗开度，100全开，0全关，127代表需要学习");
        mContentProviderHelper.addParams("window_Window_1", ParamsType.INT, "127", "0", 50, "车窗开度，100全开，0全关，127代表需要学习");
        mContentProviderHelper.addParams("window_Window_2", ParamsType.INT, "127", "0", 50, "车窗开度，100全开，0全关，127代表需要学习");
        mContentProviderHelper.addParams("window_Window_3", ParamsType.INT, "127", "0", 50, "车窗开度，100全开，0全关，127代表需要学习");

    }
}
