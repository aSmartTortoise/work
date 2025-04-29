package com.voyah.vcos.ttsservices.mem;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.ArrayMap;

import com.voyah.vcos.ttsservices.Constant;

@TargetApi(Build.VERSION_CODES.KITKAT)
public final class TtsByteArrMgr {

    public static final int LEN_PER = 1024;

    private ArrayMap<Integer, ByteArrMgr> byteArrMgrArrayMap = new ArrayMap<>();

    private final Object bytArrayLock = new Object();

    private TtsByteArrMgr() {

    }

    private static class InnerHolder {
        private static final TtsByteArrMgr sInstance = new TtsByteArrMgr();
    }

    public static TtsByteArrMgr getInstance() {
        return InnerHolder.sInstance;
    }

    public void init(int type) {
        synchronized (bytArrayLock) {
            if (byteArrMgrArrayMap.containsKey(type)) {
                return;
            }
            int amount = (type == Constant.Usage.VOICE_USAGE ? 10240 : 2048);
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
