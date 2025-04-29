package com.voice.sdk.device.phone;


import com.voice.sdk.device.phone.bean.CallLogInfo;
import com.voyah.ds.common.entity.domains.call.ContactInfo;
import com.voyah.ds.common.entity.domains.call.ContactNumberInfo;

import java.util.List;

/**
 * @author:lcy
 * @data:2024/1/21
 **/
public interface PhoneInterface {

    void init();

    /**
     * 打开apk
     **/
    int openBtApk();

    /**
     * 关闭apk
     **/
    int closeBtApk();

    /**
     * 接听电话
     **/
    int answerCall();

    /**
     * 挂断电话
     **/
    int disconnectCall();

    /**
     * 拨打电话
     **/
    int placeCall(String number);

    /**
     * 得到蓝牙连接状态
     **/
    int getBluetoothConnectState();

    //    /**
//     * 得到蓝牙电话状态
//     **/
    int getBluetoothCallState();

    /**
     * 同步通讯录
     **/
    int syncContact();

    /**
     * 打开蓝牙连接开关
     */
    void openBlueTooth();

    /**
     * 打开蓝牙设置界面
     **/
    int openBluetoothSettings();

    /**
     * 切换蓝牙电话界面
     **/
    int setBluetoothPhoneTab(int tab);

    /**
     * 切换蓝牙电话界面
     **/
    int switchBluetoothPhoneTab();

    /**
     * 回拨
     **/
    String getLastIncomingNumber();

    /**
     * 重拨
     **/
    String getLastOutgoingNumber();

    /**
     * 通讯录
     **/
    List<ContactInfo> getContactInfoList();

    /**
     * 最近通话
     **/
    List<CallLogInfo> getCallLogInfoList();

    /**
     * 自定义-通讯录数据是否已同步
     */
    boolean isSyncContacted();

    /**
     * 自定义-语音是否正在同步通讯录数据
     */
    boolean isSyncContacting();

    /**
     * 自定义-蓝牙电话是否已打开
     */
    boolean isBtApkOpen();

    /**
     * 自定义-蓝牙是否连接
     */
    boolean isBtConnect();

    /**
     * 来电状态
     */
    boolean isIncoming();

    /**
     * 去电状态
     */
    boolean isOutgoing();

    void uploadContact();

    void setCurrentInfoList(List<ContactNumberInfo> infos);

    boolean isYellowPageContainsName(List<ContactInfo> yellowPageList, String name);

    List<ContactInfo> searchListByName(String name, List<ContactInfo> list);

    List<ContactNumberInfo> selectByName(String name, List<ContactNumberInfo> list);

    List<ContactNumberInfo> getContactToNumberList(List<ContactInfo> contactInfoList);

    String getNameByIndex(boolean isMinus, int index, List<ContactNumberInfo> numberInfoList);

    String getNumberByIndex(boolean isMinus, int index, List<ContactNumberInfo> numberInfoList);

    List<ContactInfo> searchListByNumber(String number_front, String number_end, String number, List<ContactInfo> list);

    List<ContactInfo> searchListByNameAndNumber(String name, String number_front, String number_end, String number, List<ContactInfo> list);

    List<ContactNumberInfo> selectByNumber(String number_front, String number_end, String number, List<ContactNumberInfo> contactNumberInfoList);

    List<ContactNumberInfo> selectByNameAndNumber(String name, String number_front, String number_end, String number, List<ContactNumberInfo> list);


}
