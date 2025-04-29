package com.voyah.ai.logic.dc;



import android.util.Log2;

import java.util.HashMap;

public class ElectricTailControlImpl extends AbsDevices {

    private static final String TAG = ElectricTailControlImpl.class.getSimpleName();

    public ElectricTailControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "ElectricTail";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        Log2.i(TAG, "tts :" + str);
        return str;
    }
}
