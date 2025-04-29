package com.voyah.ai.logic.dc.manager;

import com.voice.sdk.device.carservice.dc.Devices;

import java.util.Map;
import java.util.TreeMap;

public abstract class BaseIntent {
    //domain , IntentManager
    protected Map<String, Devices> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    protected abstract void init();

    public Devices getDevices(String device){
        return map.get(device);
    }

    public Map<String, Devices> getAllDevices(){
        return map;
    }
}
