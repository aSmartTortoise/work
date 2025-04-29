package com.voyah.ai.basecar.recorder.mem;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.ArrayMap;

@TargetApi(Build.VERSION_CODES.KITKAT)
public final class AudioByteArrMgr {

    public int LEN_PER;

    private ArrayMap<Integer, ByteArrMgr> byteArrMgrArrayMap = new ArrayMap<>();

    private final Object bytArrayLock = new Object();

    private AudioByteArrMgr() {

    }

    private static class InnerHolder {
        private static final AudioByteArrMgr sInstance = new AudioByteArrMgr();
    }

    public static AudioByteArrMgr getInstance() {
        return InnerHolder.sInstance;
    }

    public void init(int type, int length) {
        LEN_PER = length;
        synchronized (bytArrayLock) {
            if (byteArrMgrArrayMap.containsKey(type)) {
                return;
            }
            int amount = 10240;
            ByteArrMgr byteArrMgr = new ByteArrMgr(LEN_PER, amount);
            byteArrMgrArrayMap.put(type, byteArrMgr);
        }
    }

    public ByteArrMgr.ByteArrObj getByteObj(int type) {
        synchronized (bytArrayLock) {
            if (!byteArrMgrArrayMap.containsKey(type)) {
                return new ByteArrMgr.ByteArrObj(LEN_PER);
            }
            return byteArrMgrArrayMap.get(type).getByteArrObj();
        }
    }
}
