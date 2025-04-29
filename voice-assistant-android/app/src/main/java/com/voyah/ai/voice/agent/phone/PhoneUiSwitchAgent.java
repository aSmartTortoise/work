package com.voyah.ai.voice.agent.phone;


import static com.voyah.ai.logic.dc.AbsDevices.THIRD_SCREEN;

import com.example.filter_annotation.ClassAgent;
import com.voice.sdk.constant.PhoneConstants;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.phone.PhoneInterface;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voyah.ai.common.utils.LogUtils;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.generic.Constant;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.context.FlowContextKey;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author:lcy 电话app及界面相关
 * @data:2024/2/26
 **/
@ClassAgent
public class PhoneUiSwitchAgent extends BaseAgentX {
    private static final String TAG = PhoneUiSwitchAgent.class.getSimpleName();

    private PhoneInterface phoneInterface;

    @Override
    public String AgentName() {
        return "phone_ui#switch";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        LogUtils.i(TAG, "---------PhoneUiSwitchAgent----------");
        phoneInterface = DeviceHolder.INS().getDevices().getPhone();
        boolean isSwitchOpen = StringUtils.equals(getParamKey(paramsMap, Constant.SWITCH_TYPE, 0), "open");
        String uiName = getParamKey(paramsMap, Constant.UI_NAME, 0);
        String screenName = getParamKey(paramsMap, Constant.SCREEN_NAME, 0);
        String position = getParamKey(paramsMap, Constant.POSITION, 0);
        String soundLocation = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION, flowContext);
        boolean isBtOpened = phoneInterface.isBtApkOpen();
        LogUtils.i(TAG, "uiName is " + uiName + " ,screenName:" + screenName + " ,position:" + position + " ,soundLocation:" + soundLocation + ",isSwitchOpen is " + isSwitchOpen);
        //1.5新增R挡、泊车等场景限制
        if (DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation()) {
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, TTSAnsConstant.PHONE_R_FORBIDDEN);
        }
        TTSBean ttsBean = null;
        boolean is56 = StringUtils.contains(DeviceHolder.INS().getDevices().getCarServiceProp().getCarType(), "H56");
        if (StringUtils.equals(uiName, PhoneConstants.PHONE)) {
            //todo:56车型需要判断指定的屏幕是否支持
            //蓝牙电话仅支持中控屏操作
            //一.打开操作
            //中控屏已打开蓝牙电话：
            // 1>.声源位置 主驾声源：回复1100001        其他声源：回复1100029
            // 2>.指定屏幕 主驾屏幕：回复1100029        其他屏幕：回复1100034
            //中控屏未打开蓝牙电话：
            // 1>.声源位置 主驾声源：回复1100002 执行打开 其他声源：回复1100030 执行打开
            // 2>.指定屏幕 主驾屏幕：回复1100030 执行打开  其他屏幕：回复1100034
            //二.关闭操作
            //中控屏已打开蓝牙电话：
            // 1>.声源位置 主驾声源：回复1100003  执行关闭 其他声源：回复1100031  执行关闭
            // 2>.指定屏幕 主驾屏幕：回复1100031  执行关闭 其他屏幕：回复1100035
            //中控屏未打开蓝牙电话：
            // 1>.声源位置 主驾声源：回复1100004          其他声源：回复1100004
            // 2>.指定屏幕 主驾屏幕：回复1100032          其他屏幕：回复1100035
            boolean isAssignScreen = isAssignScreen(screenName, position);
            String assignScreen = getAssignScreen(screenName, position, soundLocation);
            boolean isCentralScreen = isCentralScreen(assignScreen);
            LogUtils.d(TAG, "isAssignScreen:" + isAssignScreen + " ,assignScreen:" + assignScreen + " ,isCentralScreen:" + isCentralScreen);
            if (isSwitchOpen) {
                if (isBtOpened) {
                    LogUtils.i(TAG, "phone is already opened ");
                    if (is56) {
                        //中控屏
                        if (isCentralScreen) {
                            //指定屏幕
                            if (isAssignScreen) {
                                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_ALREADY_SCREEN[0], TTSAnsConstant.PHONE_OPEN_ALREADY_SCREEN[1]);
                                PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                        new String[]{getScreenSaverReplaceStr(assignScreen), "蓝牙电话"});
                            } else {
                                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_ALREADY[0], TTSAnsConstant.PHONE_OPEN_ALREADY[1]);
                                PhoneUtils.getReplaceTtsBean(ttsBean, "@{app_name}", "蓝牙电话");
                            }
                        } else {
                            //是否指定吸顶屏且吸顶屏不存在
                            if (isAssignCeilScreenAndHaveCeilScreen(assignScreen)) {
                                if (!isOnlySoundLocation(screenName, position)) {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.CAR_CONFIGURATION_SUPPORT[0], TTSAnsConstant.CAR_CONFIGURATION_SUPPORT[1]);
                                } else {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.CENTRAL_SCREEN_PHONE_ALREADY_OPEN[0], TTSAnsConstant.CENTRAL_SCREEN_PHONE_ALREADY_OPEN[1]);
                                    PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                            new String[]{getScreenSaverReplaceStr(FuncConstants.VALUE_SCREEN_CENTRAL), "蓝牙电话"});
                                }
                            } else {
                                //是否指定了屏幕
                                if (!StringUtils.isBlank(screenName) || !StringUtils.isBlank(position)) {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_MOT_SUPPORT[0], TTSAnsConstant.PHONE_OPEN_MOT_SUPPORT[1]);
                                    PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                            new String[]{getScreenSaverReplaceStr(assignScreen), "蓝牙电话"});
                                } else {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.CENTRAL_SCREEN_PHONE_ALREADY_OPEN[0], TTSAnsConstant.CENTRAL_SCREEN_PHONE_ALREADY_OPEN[1]);
                                    PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                            new String[]{getScreenSaverReplaceStr(FuncConstants.VALUE_SCREEN_CENTRAL), "蓝牙电话"});
                                }
                            }
                        }
                    } else {
                        ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_ALREADY[0], TTSAnsConstant.PHONE_OPEN_ALREADY[1]);
                        PhoneUtils.getReplaceTtsBean(ttsBean, "@{app_name}", "蓝牙电话");
                    }
                } else {
                    if (is56) {
                        if (isCentralScreen) {
                            if (isAssignScreen) {
                                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_SCREEN[0], TTSAnsConstant.PHONE_OPEN_SCREEN[1]);
                                PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                        new String[]{getScreenSaverReplaceStr(assignScreen), "蓝牙电话"});
                            } else {
                                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN[0], TTSAnsConstant.PHONE_OPEN[1]);
                                PhoneUtils.getReplaceTtsBean(ttsBean, "@{app_name}", "蓝牙电话");
                            }
                            DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", true, "phone_app", "phone_app", "", null);
                        } else {
                            //是否指定吸顶屏且吸顶屏不存在
                            if (isAssignCeilScreenAndHaveCeilScreen(assignScreen)) {
                                if (!isOnlySoundLocation(screenName, position)) {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.CAR_CONFIGURATION_SUPPORT[0], TTSAnsConstant.CAR_CONFIGURATION_SUPPORT[1]);
                                } else {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_SCREEN[0], TTSAnsConstant.PHONE_OPEN_SCREEN[1]);
                                    PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                            new String[]{getScreenSaverReplaceStr(FuncConstants.VALUE_SCREEN_CENTRAL), "蓝牙电话"});
                                    DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", true, "phone_app", "phone_app", "", null);
                                }
                            } else {
                                //是否指定了屏幕
                                if (!StringUtils.isBlank(screenName) || !StringUtils.isBlank(position)) {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_MOT_SUPPORT[0], TTSAnsConstant.PHONE_OPEN_MOT_SUPPORT[1]);
                                    PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                            new String[]{getScreenSaverReplaceStr(assignScreen), "蓝牙电话"});
                                } else {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_SCREEN[0], TTSAnsConstant.PHONE_OPEN_SCREEN[1]);
                                    PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                            new String[]{getScreenSaverReplaceStr(FuncConstants.VALUE_SCREEN_CENTRAL), "蓝牙电话"});
                                    DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", true, "phone_app", "phone_app", "", null);
                                }
                            }
                        }
                    } else {
                        DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", true, "phone_app", "phone_app", "", null);
                        LogUtils.i(TAG, "to open phone");
                        ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN[0], TTSAnsConstant.PHONE_OPEN[1]);
                        PhoneUtils.getReplaceTtsBean(ttsBean, "@{app_name}", "蓝牙电话");
                    }
                }
            } else {
                if (isBtOpened) {
                    if (is56) {
                        if (isCentralScreen) {
                            if (isAssignScreen) {
                                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_UI_CLOSE_SCREEN[0], TTSAnsConstant.PHONE_UI_CLOSE_SCREEN[1]);
                                PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                        new String[]{getScreenSaverReplaceStr(assignScreen), "蓝牙电话"});
                            } else {
                                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_UI_CLOSE[0], TTSAnsConstant.PHONE_UI_CLOSE[1]);
                                PhoneUtils.getReplaceTtsBean(ttsBean, "@{app_name}", "蓝牙电话");
                            }
//                            phoneInterface.closeBtApk();
                            DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", false, "phone_app", "phone_app", "", null);

                        } else {
                            //是否指定吸顶屏且吸顶屏不存在
                            if (isAssignCeilScreenAndHaveCeilScreen(assignScreen)) {
                                if (!isOnlySoundLocation(screenName, position)) {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.CAR_CONFIGURATION_SUPPORT[0], TTSAnsConstant.CAR_CONFIGURATION_SUPPORT[1]);
                                } else {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_UI_CLOSE_SCREEN[0], TTSAnsConstant.PHONE_UI_CLOSE_SCREEN[1]);
                                    PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                            new String[]{getScreenSaverReplaceStr(FuncConstants.VALUE_SCREEN_CENTRAL), "蓝牙电话"});
                                    DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", false, "phone_app", "phone_app", "", null);
                                }
                            } else {
                                //是否指定了屏幕
                                if (!StringUtils.isBlank(screenName) || !StringUtils.isBlank(position)) {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_MOT_SUPPORT_CLOSE[0], TTSAnsConstant.PHONE_OPEN_MOT_SUPPORT_CLOSE[1]);
                                    PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                            new String[]{getScreenSaverReplaceStr(assignScreen), "蓝牙电话"});
                                } else {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_UI_CLOSE_SCREEN[0], TTSAnsConstant.PHONE_UI_CLOSE_SCREEN[1]);
                                    PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                            new String[]{getScreenSaverReplaceStr(FuncConstants.VALUE_SCREEN_CENTRAL), "蓝牙电话"});
                                    DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", false, "phone_app", "phone_app", "", null);
                                }
                            }
                        }
                    } else {
                        DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", false, "phone_app", "phone_app", "", null);
                        LogUtils.i(TAG, "to close phone");
                        ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_UI_CLOSE[0], TTSAnsConstant.PHONE_UI_CLOSE[1]);
                        PhoneUtils.getReplaceTtsBean(ttsBean, "@{app_name}", "蓝牙电话");
                    }
                } else {
                    if (is56) {
                        if (isCentralScreen) {
                            if (isAssignScreen) {
                                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_UI_CLOSE_SCREEN_CLOSE[0], TTSAnsConstant.PHONE_UI_CLOSE_SCREEN_CLOSE[1]);
                                PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                        new String[]{getScreenSaverReplaceStr(assignScreen), "蓝牙电话"});
                            } else {
                                ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CLOSE_ALREADY[0], TTSAnsConstant.PHONE_CLOSE_ALREADY[1]);
                                PhoneUtils.getReplaceTtsBean(ttsBean, "@{app_name}", "蓝牙电话");
                            }
                            DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", false, "phone_app", "phone_app", "", null);
                        } else {
                            //是否指定吸顶屏且吸顶屏不存在
                            if (isAssignCeilScreenAndHaveCeilScreen(assignScreen)) {
                                if (!isOnlySoundLocation(screenName, position)) {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.CAR_CONFIGURATION_SUPPORT[0], TTSAnsConstant.CAR_CONFIGURATION_SUPPORT[1]);
                                } else {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_UI_CLOSE_SCREEN_CLOSE[0], TTSAnsConstant.PHONE_UI_CLOSE_SCREEN_CLOSE[1]);
                                    PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                            new String[]{getScreenSaverReplaceStr(FuncConstants.VALUE_SCREEN_CENTRAL), "蓝牙电话"});
                                }
                            } else {
                                //是否指定了屏幕
                                if (!StringUtils.isBlank(screenName) || !StringUtils.isBlank(position)) {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_MOT_SUPPORT_CLOSE[0], TTSAnsConstant.PHONE_OPEN_MOT_SUPPORT_CLOSE[1]);
                                    PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                            new String[]{getScreenSaverReplaceStr(assignScreen), "蓝牙电话"});
                                } else {
                                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_UI_CLOSE_SCREEN_CLOSE[0], TTSAnsConstant.PHONE_UI_CLOSE_SCREEN_CLOSE[1]);
                                    PhoneUtils.getReplacesTtsBean(ttsBean, new String[]{"@{screen_name}", "@{app_name}"},
                                            new String[]{getScreenSaverReplaceStr(FuncConstants.VALUE_SCREEN_CENTRAL), "蓝牙电话"});
                                }
                            }
                        }
                    } else {
                        LogUtils.i(TAG, "to close phone");
                        ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CLOSE_ALREADY[0], TTSAnsConstant.PHONE_CLOSE_ALREADY[1]);
                        PhoneUtils.getReplaceTtsBean(ttsBean, "@{app_name}", "蓝牙电话");
                    }
                }
            }


            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean);
        } else {
            if (isSwitchOpen) {
                //1.蓝牙是否连接
                boolean isBtConnect = phoneInterface.isBtConnect();
                if (!isBtConnect) {
                    LogUtils.i(TAG, "bt is not connect , open btphone ");
                    DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", true, "phone_app", "phone_app", "", null);
                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CONNECT_BT[0], TTSAnsConstant.PHONE_CONNECT_BT[1]);
                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
                }

                //2.正在同步通讯录数据
                boolean isSyncContacting = phoneInterface.isSyncContacting();
                if (isSyncContacting) {
                    LogUtils.i(TAG, "contact is syncContacting...");
                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_SYNCING[0], TTSAnsConstant.PHONE_SYNCING[1]);
                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
                }
                //3.未同步通讯录
                boolean isSyncContacted = phoneInterface.isSyncContacted();
                if (!isSyncContacted) {
                    LogUtils.i(TAG, "contact is not sync open btphone ");
                    DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", true, "phone_app", "phone_app", "", null);
                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_SYNC_BOOK[0], TTSAnsConstant.PHONE_SYNC_BOOK[1]);
                    return new ClientAgentResponse(Constant.PhoneAgentResponseCode.BT_STATE_NOT_SATISFY, flowContext, ttsBean);
                }

                //4.未打开通讯录or通话记录页面(通讯录界面和通话记录页面接口上只有一个)
//                if (!isBtOpened) {
                if (StringUtils.equals(uiName, PhoneConstants.ADDRESS_BOOK)) {
                    phoneInterface.setBluetoothPhoneTab(1);
                    LogUtils.i(TAG, "open  contact ");
                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_ADDRESS_BOOK[0], TTSAnsConstant.PHONE_OPEN_ADDRESS_BOOK[1]);
                } else if (StringUtils.equals(uiName, PhoneConstants.RECENT) || StringUtils.equals(uiName, PhoneConstants.MISSED) || StringUtils.equals(uiName, PhoneConstants.ANSWER)
                        || StringUtils.equals(uiName, PhoneConstants.INCOMING) || StringUtils.equals(uiName, PhoneConstants.OUTING)) {
                    phoneInterface.setBluetoothPhoneTab(0);
                    LogUtils.i(TAG, "open  recent_call ");
                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_OPEN_CALL_LOG[0], TTSAnsConstant.PHONE_OPEN_CALL_LOG[1]);
                }
                return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean);

            } else {
                if (isBtOpened) {
                    DeviceHolder.INS().getDevices().getSystem().getApp().switchPage("", false, "phone_app", "phone_app", "", null);
                    LogUtils.i(TAG, " close btphone");
                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_UI_CLOSE[0], TTSAnsConstant.PHONE_UI_CLOSE[1]);

                } else {
                    LogUtils.i(TAG, "btphone is closed");
                    ttsBean = PhoneUtils.getTtsBean(TTSAnsConstant.PHONE_CLOSE_ALREADY[0], TTSAnsConstant.PHONE_CLOSE_ALREADY[1]);
                }
                PhoneUtils.getReplaceTtsBean(ttsBean, "@{app_name}", "蓝牙电话");
            }
            return new ClientAgentResponse(Constant.PhoneAgentResponseCode.SUCCESS, flowContext, ttsBean);
        }
    }


    private boolean isCentralScreen(String assignScreen) {
        return StringUtils.equals(assignScreen, FuncConstants.VALUE_SCREEN_CENTRAL);
    }

    private boolean isAssignScreen(String screenName, String position) {
        return !StringUtils.isBlank(screenName) || !StringUtils.isBlank(position);
    }

    private String getScreenSaverReplaceStr(String assignScreen) {
        if (StringUtils.equals(assignScreen, FuncConstants.VALUE_SCREEN_CENTRAL))
            return TTSAnsConstant.VALUE_SCREEN_CENTRAL;
        else if (StringUtils.equals(assignScreen, FuncConstants.VALUE_SCREEN_PASSENGER))
            return TTSAnsConstant.VALUE_SCREEN_PASSENGER;
        else if (StringUtils.equals(assignScreen, FuncConstants.VALUE_SCREEN_CEIL))
            return TTSAnsConstant.VALUE_SCREEN_CEIL;
        else
            return TTSAnsConstant.VALUE_SCREEN_CENTRAL;
    }

    private String getAssignScreen(String screenName, String position, String soundLocation) {
//        String screenName = map.containsKey("screen_name") ? (String) getValueInContext(map, "screen_name") : "";
//        String position = map.containsKey("positions") ? (String) getValueInContext(map, "positions") : "";
//        String soundLocation = getSoundLocation(map);
        String screen = SCREEN_LIST.get(0);
        if (StringUtils.isBlank(screenName) && StringUtils.isBlank(position)) {
            screen = soundLocation;
        } else if (StringUtils.isBlank(screenName)) {
            //指定屏幕为空
            if (StringUtils.isBlank(position)) {
                //没有指定屏幕和位置-根据声源位置
                screen = soundLocation;
            } else if (!StringUtils.isBlank(position)) {
                //没有指定屏幕有指定位置-根据指定位置
                screen = position;
            }
        } else {
            if (StringUtils.equals(screenName, SCREEN)) {
                //指定了屏幕，但是是非单一屏幕
                screen = position;
            } else if (StringUtils.equals(screenName, ENTERTAINMENT_SCREEN)) {
                //指定屏幕且为娱乐屏
                if (!StringUtils.isBlank(position))
                    screen = position;
                else
                    screen = soundLocation;
            } else {
                screen = screenName;
            }
        }

        String assignScreenName = assignScreenToFuncName(screen);
        LogUtils.d(TAG, "getAssignScreen screenName:" + screenName + " ,position:" + position + " ,soundLocation:" + soundLocation + " ,screen:" + screen + " ,assignScreenName:" + assignScreenName);
        return assignScreenName;
    }

    private String assignScreenToFuncName(String screen) {
        String assignScreenName = FuncConstants.VALUE_SCREEN_CENTRAL;
        if (LOCATION_LIST.contains(screen)) {
            if (StringUtils.equals(screen, LOCATION_LIST.get(0)))
                assignScreenName = FuncConstants.VALUE_SCREEN_CENTRAL;
            else if (StringUtils.equals(screen, LOCATION_LIST.get(1)))
                assignScreenName = FuncConstants.VALUE_SCREEN_PASSENGER;
            else
                assignScreenName = FuncConstants.VALUE_SCREEN_CEIL;
        } else {
            if (FIRST_SCREEN.contains(screen))
                assignScreenName = FuncConstants.VALUE_SCREEN_CENTRAL;
            else if (THIRD_SCREEN.contains(screen))
                assignScreenName = FuncConstants.VALUE_SCREEN_CEIL;
            else
                assignScreenName = FuncConstants.VALUE_SCREEN_PASSENGER;

        }
        return assignScreenName;
    }

    //指定吸顶屏且吸顶屏不存在
    private boolean isAssignCeilScreenAndHaveCeilScreen(String screenName) {
        boolean isCeilScreen = StringUtils.equals(screenName, FuncConstants.VALUE_SCREEN_CEIL);
        boolean hasCeilScreen = DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportScreen(DeviceScreenType.CEIL_SCREEN);
        LogUtils.d(TAG, "isAssignCeilScreenAndHaveCeilScreen hasCeilScreen:" + hasCeilScreen + " ,isCeilScreen:" + isCeilScreen + " ,screenName:" + screenName);
        return isCeilScreen && !hasCeilScreen;
    }

    private boolean isOnlySoundLocation(String screenName, String position) {
        return StringUtils.isBlank(screenName) && StringUtils.isBlank(position);
    }


}
