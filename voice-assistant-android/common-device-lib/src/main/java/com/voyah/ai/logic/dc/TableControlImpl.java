package com.voyah.ai.logic.dc;

import com.voice.sdk.device.carservice.signal.TableSignal;
import com.voice.sdk.util.LogUtils;

import java.util.HashMap;

public class TableControlImpl extends AbsDevices {

    private static final String TAG = TableControlImpl.class.getSimpleName();

    public TableControlImpl() {
        super();
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        return str;
    }

    /**
     * 获取副驾电动桌板的配置字
     */
    public boolean getTableConfig(HashMap<String, Object> map) {
        //获取当前配置字
        boolean config = operator.getBooleanProp(TableSignal.TABLE_CONFIG);
        LogUtils.i(TAG, "getTableConfig : config-" + config);
        return config;
    }

    /**
     * 设置副驾电动桌板的开关状态（只有打开，没有关闭）
     */
    public void setTableStatus(HashMap<String, Object> map) {
        LogUtils.i(TAG, "setTableStatus");
        operator.setBooleanProp(TableSignal.TABLE_CMD, true);
    }

    @Override
    public String getDomain() {
        return "Table";
    }
}
