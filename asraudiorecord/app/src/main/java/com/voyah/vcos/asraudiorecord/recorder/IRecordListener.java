package com.voyah.vcos.asraudiorecord.recorder;

public interface IRecordListener {
    void recordStart();

    void recordData(byte[] data,int len);

    void recordStop();

}
