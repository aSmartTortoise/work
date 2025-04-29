package com.voyah.ai.common.helper;


import com.blankj.utilcode.util.LogUtils;

import java.util.Map;

public class LogHelper {
    private static final String TAG = "tag=[VRService]";
    public static void toString(String tag, Map<String,String> map){
        LogTool logTool = new LogTool(tag);

        for(String key:map.keySet()){
            logTool.add(key,map.get(key));
        }
        LogUtils.i(TAG,logTool.toString());
    }
}
