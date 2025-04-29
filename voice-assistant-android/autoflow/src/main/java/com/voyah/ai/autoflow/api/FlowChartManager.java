package com.voyah.ai.autoflow.api;

import android.content.Context;
import android.os.Environment;

import com.voyah.ai.autoflow.api.utils.Utils;

import java.io.File;

public class FlowChartManager {
    private boolean isInit = false;
    private FlowChartManager() {

    }

    public static FlowChartManager getInstance() {
        return Holder.Instance;
    }

    private static class Holder {
        private static final FlowChartManager Instance = new FlowChartManager();
    }

    //配置文件根目录
    public static final String ROOT_FILE = Environment.getExternalStorageDirectory() + File.separator + "Configuration";
    //excle表的目录
    public static final String GRAPH_PATH = ROOT_FILE + File.separator + "flow_chart/";
    public static final String SOUCE_PATH = ROOT_FILE + File.separator + "resource/";
    public void init(Context context){
        //初始化工具类
        if(isInit) {
            return;
        }
        Utils.getInstance().init(context);
        Utils.getInstance().copyAssetsToSD("flow_chart", GRAPH_PATH);
        Utils.getInstance().copyAssetsToSD("resource", SOUCE_PATH);
        isInit = true;
    }
    public String getGraphPath(){
        return GRAPH_PATH;
    }
}
