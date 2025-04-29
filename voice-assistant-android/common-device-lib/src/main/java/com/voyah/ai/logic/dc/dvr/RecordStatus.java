package com.voyah.ai.logic.dc.dvr;

/**
 * @author:lcy
 * @data:2024/8/7
 **/
public interface RecordStatus {
    int IDLE = 0; //停止状态
    int START_RECORDING = 1; //开始中
    int STARTED = 2; //已开始
    int STOP_RECORDING = 3; //停止中
    int PAUSE_RECORDING = 4; //已暂停
    int RESUME_RECORDING = 5; //已恢复
}
