package com.voyah.ai.logic.dc;



import java.util.HashMap;

public class ChargeportControlImpl extends AbsDevices {

    private static final String TAG = ChargeportControlImpl.class.getSimpleName();

    public ChargeportControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "chargePort";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        return str;
    }
}
