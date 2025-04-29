package com.voyah.vcos.virtualdevice.param.intent;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseControl {
    protected List<BaseParams> list = new ArrayList<>();
    public void exe(){
        for (int i = 0; i < list.size(); i++) {
            BaseParams curParams  = list.get(i);
            curParams.exe();
        }
    }
    protected abstract void init();
}
