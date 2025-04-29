package com.voyah.ai.logic.buriedpoint.helper;

import com.google.gson.reflect.TypeToken;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.logic.util.FileUtils;
import com.voyah.ds.common.tool.GsonUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class FunctionToIdHelper {
    private static final String TAG = "FunctionToIdHelper";
    private Map<String, String> functionToMap;

    private FunctionToIdHelper() {

    }

    public static FunctionToIdHelper getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final FunctionToIdHelper instance = new FunctionToIdHelper();
    }

    public void init(String path){
        String filePath = path + "/resource/function_to_id.json";


        String jsonText = FileUtils.readTxtFile(filePath);

        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        functionToMap = GsonUtils.fromJson(jsonText, type);
        LogUtils.d(TAG,"function_guidance的数量是：" + (functionToMap == null ? "没加载映射表" : functionToMap.size()));
    }
    public String getFunctionId(String functionName){
        LogUtils.i(TAG,"函数id映射表数据个数："+functionToMap.size());
        return functionToMap.get(functionName);
    }
}
