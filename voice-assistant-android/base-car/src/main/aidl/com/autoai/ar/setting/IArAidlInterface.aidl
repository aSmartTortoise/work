package com.autoai.ar.setting;

import com.autoai.ar.setting.IArAidlCallback;

interface IArAidlInterface {
    void set(int type,int value);
    int get(int type);
    String getStr(int type);
    void registerCallback(IArAidlCallback callback);
    void unregisterCallback(IArAidlCallback callback);
}