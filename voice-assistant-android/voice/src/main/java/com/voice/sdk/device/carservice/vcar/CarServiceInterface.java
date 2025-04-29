package com.voice.sdk.device.carservice.vcar;

import com.voice.sdk.device.carservice.dc.AtmoInterface;
import com.voice.sdk.device.carservice.dc.ReadingLightInterface;
import com.voice.sdk.device.carservice.dc.RemoteInterface;
import com.voice.sdk.device.carservice.dc.SeatInterface;
import com.voice.sdk.device.carservice.dc.SystemControlInterface;
import com.voice.sdk.device.carservice.dc.SystemSettingInterface;
import com.voice.sdk.device.system.SceneModeInterface;

/**
 * @Date 2024/12/24 16:27
 * @Author 8327821
 * @Email *
 * @Description car service 汇总接口接口
 **/
public interface CarServiceInterface {

    IOperatorDispatcher getOperatorDispatcher();

    AtmoInterface getAtmo();

    SystemSettingInterface getSystemSetting();

    ReadingLightInterface getReadingLight();

    SeatInterface getSeatInterface();

    RemoteInterface getRemoteInterface();

    SceneModeInterface getSceneModeInterface();

    SystemControlInterface getSystemControlInterface();

}
