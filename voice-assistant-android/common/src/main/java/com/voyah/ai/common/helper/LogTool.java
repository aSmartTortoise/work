package com.voyah.ai.common.helper;


import com.voyah.ai.common.ParamsGather;

import java.util.List;

public class LogTool {
    String vin;
    String requestId;
    String sessionId;
    StringBuilder stringBuilder = new StringBuilder();

    public LogTool(String tag){
        vin = ParamsGather.vin;
        //vin码
        stringBuilder.append(" vin=["+vin+"]");
        //requestID
        stringBuilder.append(" requestId=["+ ParamsGather.requestId+"]");
        stringBuilder.append(" ("+tag+") ");
    }

    public LogTool add(String key, String value){
        stringBuilder.append(key+"="+value+" ");
        return this;
    }
    public LogTool add(String key, int value){
        stringBuilder.append(key+"="+value+" ");

        //VirtualDeviceManager.getInstance().
        return this;
    }
    public LogTool add(String key, float value){
        stringBuilder.append(key+"="+value+" ");
        return this;
    }
    public LogTool add(String key, boolean value){
        stringBuilder.append(key+"="+value+" ");
        return this;
    }

    public LogTool addJson(String key, JsonStructure jsonStructure){
        stringBuilder.append(key+"="+jsonStructure.toString()+" ");
        return this;
    }


    public String toString(){
        return stringBuilder.toString();
    }


    public static class JsonStructure{
        StringBuilder stringBuilder = new StringBuilder();
        public JsonStructure(){
            stringBuilder.append("{");
        }
        public JsonStructure addJsonParam(String key,String value){
            if(stringBuilder.length() == 1){
                stringBuilder.append("\""+key+"\""+":\""+value+"\"");
            }else{
                stringBuilder.append(",\""+key+"\""+":\""+value+"\"");
            }

            return this;
        }
        public JsonStructure addJsonParam(String key,int value){
            if(stringBuilder.length() == 1){
                stringBuilder.append("\""+key+"\""+":\""+value+"\"");
            }else{
                stringBuilder.append(",\""+key+"\""+":\""+value+"\"");
            }
            return this;
        }
        public JsonStructure addJsonParam(String key,float value){
            if(stringBuilder.length() == 1){
                stringBuilder.append("\""+key+"\""+":\""+value+"\"");
            }else{
                stringBuilder.append(",\""+key+"\""+":\""+value+"\"");
            }
            return this;
        }
        public JsonStructure addJsonParam(String key,boolean value){
            if(stringBuilder.length() == 1){
                stringBuilder.append("\""+key+"\""+":\""+value+"\"");
            }else{
                stringBuilder.append(",\""+key+"\""+":\""+value+"\"");
            }
            return this;
        }

        public JsonStructure addJsonParam(String key, Object value) {
            if (value instanceof String) {
                return addJsonParam(key, (String) value);
            } else {
                if (stringBuilder.length() == 1) {
                    stringBuilder.append("\"").append(key).append("\"").append(":").append(value);
                } else {
                    stringBuilder.append(",\"" + key + "\"" + ":" + value);
                }
                return this;
            }
        }
        public JsonStructure addJsonParam(String key,List<Integer> value){
            StringBuilder mStringBuilder = new StringBuilder();
            mStringBuilder.append("[");
            mStringBuilder.append("\""+value.get(0)+"\"");
            for (int i = 1; i < value.size(); i++) {
                mStringBuilder.append(",\""+value.get(i)+"\"");
            }
            mStringBuilder.append("]");
            stringBuilder.append(",\""+key+"\":"+mStringBuilder.toString());
            return this;
        }

        public String toString(){
            stringBuilder.append("}");
            return stringBuilder.toString();
        }
    }
}
