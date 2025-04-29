package com.voyah.ai.logic.dc;

import android.os.RemoteException;
import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.UserCenterInterface;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.OtherSignal;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.sdk.bean.DhDialect;
import com.voyah.ai.sdk.bean.DhSwitch;

import java.util.ArrayList;
import java.util.HashMap;

public class OtherControlControlImpl extends AbsDevices {

    private static final String TAG = OtherControlControlImpl.class.getSimpleName();

    private static final String[] complianceAppName =
            new String[]{"智能选择", "网易云音乐", "qq音乐", "腾讯视频", "爱奇艺"};

    private static final String TIMBRE_MODE = "timbre_mode";

    private static final String DIALECT_MODE = "dialect_mode";


    public OtherControlControlImpl() {
        super();
    }


    @Override
    public String getDomain() {
        return "other";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        LogUtils.e(TAG, "initial tts :" + str);
        int index = 0;

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        switch (key) {
            case "position":
            case "audio_zone":
                if (key.equals("position")) {
                    str = str.replace("@{position}", getOrdVoiceAreaStr(map));
                } else {
                    str = str.replace("@{audio_zone}", getOrdVoiceAreaStr(map));
                }
                break;
            case "tonet":
                str = str.replace("@{tonet}", getTimbreModeStr(map));
                break;
            case "dialect":
                str = str.replace("@{dialect}", getLanguageModeStr(map));
                break;
            case "mode":
            case "video_source":
            case "media_source":
                if (key.equals("mode")) {
                    str = str.replace("@{mode}", getMediaStr(map));
                } else if (key.equals("video_source")) {
                    str = str.replace("@{video_source}", getMediaStr(map));
                } else {
                    str = str.replace("@{media_source}", getMediaStr(map));
                }
                break;
            case "tab_name":
                str = str.replace("@{tab_name}", getTabName(map));
                break;
            case "app_name":
                str = str.replace("@{app_name}", "设置应用");
                break;
            case "switch_mode":
                String switch_mode = getLlmOrderMode(map) == 0 ? "逍遥座舱大模型" : "deepseek";
                str = str.replace("@{switch_mode}", switch_mode);
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.e(TAG, "modified tts :" + str);

        return str;
    }

    private String getTabName(HashMap<String, Object> map) {
        String str = "";
        String pageName = (String) getValueInContext(map, "tab_name");
        if (!TextUtils.isEmpty(pageName)) {
            switch (pageName) {
                case "intelligent_gesture":
                    str = "智能手势";
                    break;
            }
        }
        return str;
    }

    private String getOrdVoiceAreaStr(HashMap<String, Object> map) {
        String str = "";
        if (map.containsKey("positions")) {
            str = (String) getValueInContext(map, "positions");
            switch (str) {
                case "first_row_left":
                    str = "主驾";
                    break;
                case "first_row_right":
                    str = "副驾";
                    break;
                case "front_side":
                    str = "前排";
                    break;
                case "rear_side":
                case "second_side":
                    str = "后排";
                    break;
                case "rear_side_left":
                    str = "左后";
                    break;
                case "rear_side_right":
                    str = "右后";
                    break;
                case "second_row_left":
                    str = "二排左";
                    break;
                case "second_row_right":
                    str = "二排右";
                    break;
                case "left_side":
                    str = "左侧";
                    break;
                case "right_side":
                    str = "右侧";
                    break;
                case "total_car":
                    str = "全部";
                    break;
                default:
            }
        }
        if (map.containsKey("switch_type")) {
            String switchType = (String) getValueInContext(map, "switch_type");
            if (switchType.equals("close")) {
                if (str.equals("前排")) {
                    str = "副驾";
                } else if (str.equals("左侧")) {
                    str = "左后";
                }
            }
        }
        LogUtils.d(TAG, "getOrdVoiceAreaStr: " + str);
        return str;
    }

    private String getMediaStr(HashMap<String, Object> map) {
        String str = "智能选择";
        if (map.containsKey("app_name")) {
            str = (String) getValueInContext(map, "app_name");
            if (str.equals("爱奇艺")) {
                str = "爱奇艺视频";
            }
        }
        LogUtils.d(TAG, "getMediaStr: " + str);
        return str;
    }

    private String getTimbreModeStr(HashMap<String, Object> map) {
        String str = getOneMapValue(TIMBRE_MODE, map);
        if (!TextUtils.isEmpty(str)) {
            LogUtils.d(TAG, "tonetStr: " + str);
        } else {
            str = getTimbreMode(map);
            switch (str) {
                case DhDialect.ID_OFFICIAL_1:
                    str = "晓晓";
                    break;
                case DhDialect.ID_OFFICIAL_2:
                    str = "晓甄";
                    break;
                case DhDialect.ID_OFFICIAL_3:
                    str = "晓晨";
                    break;
                case DhDialect.ID_OFFICIAL_4:
                    str = "云希";
                    break;
                case DhDialect.ID_OFFICIAL_5:
                    str = "云逸";
                    break;
                default:
                    break;
            }
        }
        return str;
    }

    private String getLanguageModeStr(HashMap<String, Object> map) {
        String str = getOneMapValue(DIALECT_MODE, map);
        if (!TextUtils.isEmpty(str)) {
            LogUtils.d(TAG, "tonetStr: " + str);
        } else {
            str = getLanguageMode(map);
            switch (str) {
                case DhDialect.ID_OFFICIAL_1:
                    str = "普通话";
                    break;
                case DhDialect.ID_SICHUAN:
                    str = "四川话";
                    break;
                case DhDialect.ID_CANTONESE:
                    str = "广东话";
                    break;
                default:
                    break;
            }
        }
        return str;
    }

    /**
     * ----------------------- 分屏开关 start------------------------------------------------------
     */

    /**
     * 获取分屏开关状态
     *
     * @param map 上下文数据
     * @return 开关状态。0：关闭 1：打开
     */
    public int getSplitSwitchState(HashMap<String, Object> map) {
        int value = 0;
        value = operator.getIntProp(CommonSignal.COMMON_SPLIT_SWITCH);
        LogUtils.d(TAG, "getSplitSwitchState: " + value);
        return value;
    }

    public void setSplitSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setSplitSwitchState");
        String str = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(str)) {
            operator.setIntProp(CommonSignal.COMMON_SPLIT_SWITCH, "open".equals(str) ? 1 : 0);
            if ("close".equals(str)) {
                if (DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSplitScreening()) {
                    LogUtils.d(TAG, "dismiss split");
                    DeviceHolder.INS().getDevices().getSystem().getSplitScreen().sendDismissSplitBroadcast(false);
                } else {
                    LogUtils.d(TAG, "not need dismiss split");
                }
            }
        }
    }

    /** ----------------------- 分屏开关 end------------------------------------------------------*/

    /**
     * ----------------------- 语音设置 start------------------------------------------------------
     */

    public boolean getVoiceWakeUpSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getVoiceWakeUpSwitchState");
        return operator.getBooleanProp(OtherSignal.OTHER_WAKEUP_SWITCH);
    }

    public void setVoiceWakeUpSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setVoiceWakeUpSwitchState");
        String str = "";
        if (map.containsKey("switch_type")) {
            str = (String) getValueInContext(map, "switch_type");
        }
        operator.setBooleanProp(OtherSignal.OTHER_WAKEUP_SWITCH, str.equals("open"));
    }

    public void showConfirmOffWakeup(HashMap<String, Object> map) {
        if (mSettingHelper != null) {
            mSettingHelper.exec(SettingConstants.WAKE_UP_OFF);
        }
    }

    public boolean getWakeUpFreeSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getWakeUpFreeSwitchState");
        return DeviceHolder.INS().getDevices().getVoiceSettings().isEnableSwitch(DhSwitch.FreeWakeup);
    }

    public void setWakeUpFreeSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setWakeUpFreeSwitchState");
        String str = "";
        if (map.containsKey("switch_type")) {
            str = (String) getValueInContext(map, "switch_type");
        }
        DeviceHolder.INS().getDevices().getVoiceSettings().enableSwitch(DhSwitch.FreeWakeup, str.equals("open"));
    }

    public boolean getWakeUpNatureSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getWakeUpNatureSwitchState");
        return DeviceHolder.INS().getDevices().getVoiceSettings().isEnableSwitch(DhSwitch.Oneshot);
    }

    public void setWakeUpNatureSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setWakeUpNatureSwitchState");
        String str = "";
        if (map.containsKey("switch_type")) {
            str = (String) getValueInContext(map, "switch_type");
        }
        DeviceHolder.INS().getDevices().getVoiceSettings().enableSwitch(DhSwitch.Oneshot, str.equals("open"));
    }

    public boolean getContinueDialogueSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getContinueDialogueSwitchState");
        return operator.getBooleanProp(OtherSignal.OTHER_CONTINUOUS_DIALOGUE);
    }

    public void setContinueDialogueSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setContinueDialogueSwitchState");
        String str = "";
        if (map.containsKey("switch_type")) {
            str = (String) getValueInContext(map, "switch_type");
        }
        operator.setBooleanProp(OtherSignal.OTHER_CONTINUOUS_DIALOGUE, "open".equals(str));
    }

    public boolean getPolyphonicRegionSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getPolyphonicRegionSwitchState");
        return operator.getBooleanProp(OtherSignal.OTHER_MULTI_ZONE_DIALOGUE);
    }

    public void setPolyphonicRegionSwitchState(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setPolyphonicRegionSwitchState");
        String str = "";
        if (map.containsKey("switch_type")) {
            str = (String) getValueInContext(map, "switch_type");
        }
        operator.setBooleanProp(OtherSignal.OTHER_MULTI_ZONE_DIALOGUE, "open".equals(str));
    }

    public boolean isValidPosition(HashMap<String, Object> map) {
        String pos = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(pos)) {
            return !pos.contains("third");
        }
        return false;
    }

    public boolean isFirstRowLeftPosition(HashMap<String, Object> map) {
        if (map.containsKey("positions")) {
            return getValueInContext(map, "positions").equals("first_row_left");
        }
        return false;
    }

    public boolean hasFirstRowLeftPosition(HashMap<String, Object> map) {
        if (map.containsKey("positions")) {
            return getValueInContext(map, "positions").equals("front_side")
                    || getValueInContext(map, "positions").equals("left_side");
        }
        return false;
    }

    public boolean getVoiceArea(HashMap<String, Object> map) {
        ArrayList<Integer> curArea = getCurVoiceArea(map);
        ArrayList<Integer> ordArea = getOrdVoiceArea(map);
        if (ordArea.size() != curArea.size()) {
            return false;
        }
        int curFrontLeft = curArea.get(0);
        int curFrontRight = curArea.get(1);
        int curRearLeft = curArea.get(2);
        int curRearRight = curArea.get(3);
        int ordFrontLeft = ordArea.get(0);
        int ordFrontRight = ordArea.get(1);
        int ordRearLeft = ordArea.get(2);
        int ordRearRight = ordArea.get(3);
        if (map.containsKey("positions")) {
            String str = (String) getValueInContext(map, "positions");
            switch (str) {
                case "first_row_left":
                    return curFrontLeft == ordFrontLeft;
                case "first_row_right":
                case "front_side":
                    return curFrontRight == ordFrontRight;
                case "rear_side":
                case "second_side":
                    return curRearLeft == ordRearLeft && curRearRight == ordRearRight;
                case "rear_side_left":
                case "left_side":
                case "second_row_left":
                    return curRearLeft == ordRearLeft;
                case "rear_side_right":
                case "second_row_right":
                    return curRearRight == ordRearRight;
                case "right_side":
                    return curFrontRight == ordFrontRight && curRearRight == ordRearRight;
                case "total_car":
                    return curFrontRight == ordFrontRight && curRearRight == ordRearRight && curRearLeft == ordRearLeft;
            }
        }
        return false;
    }

    public void setVoiceArea(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setVoiceArea");
        operator.setIntProp(OtherSignal.OTHER_VOICE_AREA, getOrdVoiceAreaConfig(map));
    }

    private ArrayList<Integer> getOrdVoiceArea(HashMap<String, Object> map) {
        ArrayList<Integer> position = getCurVoiceArea(map);
        if (map.containsKey("positions")) {
            String str = (String) getValueInContext(map, "positions");
            if (map.containsKey("switch_type")) {
                String switch_type = (String) getValueInContext(map, "switch_type");
                int value = "open".equals(switch_type) ? 1 : 0;
                switch (str) {
                    case "first_row_left":
                        position.set(0, 1);
                        break;
                    case "first_row_right":
                    case "front_side":
                        position.set(1, value);
                        break;
                    case "rear_side":
                    case "second_side":
                        position.set(2, value);
                        position.set(3, value);
                        break;
                    case "right_side":
                        position.set(1, value);
                        position.set(3, value);
                        break;
                    case "rear_side_left":
                    case "left_side":
                    case "second_row_left":
                        position.set(2, value);
                        break;
                    case "rear_side_right":
                    case "second_row_right":
                        position.set(3, value);
                        break;
                    case "total_car":
                        position.set(1, value);
                        position.set(2, value);
                        position.set(3, value);
                        break;
                }
            }
        }
        LogUtils.d(TAG, "getOrdVoiceArea: " + position);
        return position;
    }

    private ArrayList<Integer> getCurVoiceArea(HashMap<String, Object> map) {
        int curVoiceArea = operator.getIntProp(OtherSignal.OTHER_VOICE_AREA);
        ArrayList<Integer> position = new ArrayList<>();
        position.add(1);
        position.add(0);
        position.add(0);
        position.add(0);
        position.set(1, curVoiceArea >> 1 & 1);
        position.set(2, curVoiceArea >> 2 & 1);
        position.set(3, curVoiceArea >> 3 & 1);
        LogUtils.d(TAG, "getCurVoiceArea: " + position);
        return position;
    }

    private int getOrdVoiceAreaConfig(HashMap<String, Object> map) {
        int voiceArea = 0;
        ArrayList<Integer> list = getOrdVoiceArea(map);
        int frontLeft = list.get(0);
        int frontRight = list.get(1);
        int rearLeft = list.get(2);
        int rearRight = list.get(3);
        voiceArea = frontLeft + frontRight * 2 + rearLeft * 4 + rearRight * 8;
        LogUtils.d(TAG, "getOrdVoiceAreaConfig: " + voiceArea);
        return voiceArea;
    }

    public String getLanguageMode(HashMap<String, Object> map) {
        DhDialect CurTimbreMode = DeviceHolder.INS().getDevices().getVoiceSettings().getCurrentDialect();
        LogUtils.d(TAG, "getLanguageMode: " + CurTimbreMode.toString());
        return CurTimbreMode.wakeup;
    }

    public String getOrderLanguageMode(HashMap<String, Object> map) {
        String str = getOneMapValue("switch_mode", map);
        String languageMode = "";
        if (!TextUtils.isEmpty(str)) {
            switch (str) {
                case "普通话":
                    languageMode = DhDialect.ID_OFFICIAL_1;
                    break;
                case "广东话":
                    languageMode = DhDialect.ID_CANTONESE;
                    break;
                case "四川话":
                    languageMode = DhDialect.ID_SICHUAN;
                    break;
                default:
                    break;
            }
        }
        LogUtils.d(TAG, "getOrderLanguageMode: " + languageMode);
        return languageMode;
    }

    public boolean isOrdLanguageMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isOrdLanguageMode");
        if (getLanguageMode(map) == null || getOrderLanguageMode(map) == null) {
            LogUtils.d(TAG, "isOrdLanguageMode error");
            return true;
        }
        return getLanguageMode(map).equals(getOrderLanguageMode(map));
    }

    public void setLanguageMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setLanguageMode");
        DhDialect currentDialect = DeviceHolder.INS().getDevices().getVoiceSettings().getCurrentDialect();
        String curLanguageMode = getLanguageMode(map);
        if (!map.containsKey("switch_mode")) {
            switch (curLanguageMode) {
                case DhDialect.ID_OFFICIAL_1:
                    currentDialect.asr = DhDialect.ID_SICHUAN;
                    currentDialect.wakeup = DhDialect.ID_SICHUAN;
                    DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                    map.put(DIALECT_MODE, "四川话");
                    break;
                case DhDialect.ID_SICHUAN:
                    currentDialect.asr = DhDialect.ID_CANTONESE;
                    currentDialect.wakeup = DhDialect.ID_CANTONESE;
                    DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                    map.put(DIALECT_MODE, "广东话");
                    break;
                case DhDialect.ID_CANTONESE:
                    currentDialect.asr = DhDialect.ID_OFFICIAL_1;
                    currentDialect.wakeup = DhDialect.ID_OFFICIAL_1;
                    DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                    map.put(DIALECT_MODE, "普通话");
                    break;
                default:
                    break;

            }
            return;
        }
        String ordTimbreMOde = getOrderLanguageMode(map);
        switch (ordTimbreMOde) {
            case DhDialect.ID_CANTONESE:
                currentDialect.asr = DhDialect.ID_CANTONESE;
                currentDialect.wakeup = DhDialect.ID_CANTONESE;
                DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                map.put(DIALECT_MODE, "广东话");
                break;
            case DhDialect.ID_SICHUAN:
                currentDialect.asr = DhDialect.ID_SICHUAN;
                currentDialect.wakeup = DhDialect.ID_SICHUAN;
                DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                map.put(DIALECT_MODE, "四川话");
                break;
            case DhDialect.ID_OFFICIAL_1:
                currentDialect.asr = DhDialect.ID_OFFICIAL_1;
                currentDialect.wakeup = DhDialect.ID_OFFICIAL_1;
                DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                map.put(DIALECT_MODE, "普通话");
                break;
            default:
                break;
        }
    }

    public boolean isOrderToPuTongHuaMode(HashMap<String, Object> map) {
        if (!map.containsKey("switch_mode")) {
            return false;
        }
        String str = (String) getValueInContext(map, "switch_mode");
        return str.equals("普通话");
    }

    public void setTimbreMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setTimbreMode");
        DhDialect currentDialect = DeviceHolder.INS().getDevices().getVoiceSettings().getCurrentDialect();
        String curTimbreMOde = getTimbreMode(map);
        if (!map.containsKey("switch_mode")) {
            switch (curTimbreMOde) {
                case DhDialect.ID_OFFICIAL_1:
                    currentDialect.tts = DhDialect.ID_OFFICIAL_2;
                    currentDialect.id = DhDialect.ID_OFFICIAL_2;
                    DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                    map.put(TIMBRE_MODE, "晓甄");
                    break;
                case DhDialect.ID_OFFICIAL_2:
                    currentDialect.tts = DhDialect.ID_OFFICIAL_3;
                    currentDialect.id = DhDialect.ID_OFFICIAL_3;
                    DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                    map.put(TIMBRE_MODE, "晓晨");
                    break;
                case DhDialect.ID_OFFICIAL_3:
                    currentDialect.tts = DhDialect.ID_OFFICIAL_4;
                    currentDialect.id = DhDialect.ID_OFFICIAL_4;
                    DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                    map.put(TIMBRE_MODE, "云希");
                    break;
                case DhDialect.ID_OFFICIAL_4:
                    currentDialect.tts = DhDialect.ID_OFFICIAL_5;
                    currentDialect.id = DhDialect.ID_OFFICIAL_5;
                    DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                    map.put(TIMBRE_MODE, "云逸");
                    break;
                case DhDialect.ID_OFFICIAL_5:
                    currentDialect.tts = DhDialect.ID_OFFICIAL_1;
                    currentDialect.id = DhDialect.ID_OFFICIAL_1;
                    DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                    map.put(TIMBRE_MODE, "晓晓");
                    break;
                default:
                    break;
            }
            return;
        }
        String ordTimbreMOde = getOrdTimbreMode(map);
        switch (ordTimbreMOde) {
            case DhDialect.ID_OFFICIAL_1:
                currentDialect.tts = DhDialect.ID_OFFICIAL_1;
                currentDialect.id = DhDialect.ID_OFFICIAL_1;
                DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                map.put(TIMBRE_MODE, "晓晓");
                break;
            case DhDialect.ID_OFFICIAL_2:
                currentDialect.tts = DhDialect.ID_OFFICIAL_2;
                currentDialect.id = DhDialect.ID_OFFICIAL_2;
                DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                map.put(TIMBRE_MODE, "晓甄");
                break;
            case DhDialect.ID_OFFICIAL_3:
                currentDialect.tts = DhDialect.ID_OFFICIAL_3;
                currentDialect.id = DhDialect.ID_OFFICIAL_3;
                DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                map.put(TIMBRE_MODE, "晓晨");
                break;
            case DhDialect.ID_OFFICIAL_4:
                currentDialect.tts = DhDialect.ID_OFFICIAL_4;
                currentDialect.id = DhDialect.ID_OFFICIAL_4;
                DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                map.put(TIMBRE_MODE, "云希");
                break;
            case DhDialect.ID_OFFICIAL_5:
                currentDialect.tts = DhDialect.ID_OFFICIAL_5;
                currentDialect.id = DhDialect.ID_OFFICIAL_5;
                DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(currentDialect);
                map.put(TIMBRE_MODE, "云逸");
                break;
            default:
                break;
        }
    }

    public String getTimbreMode(HashMap<String, Object> map) {
        DhDialect CurTimbreMode = DeviceHolder.INS().getDevices().getVoiceSettings().getCurrentDialect();
        LogUtils.d(TAG, "getTimbreMode: " + CurTimbreMode.toString());
        return CurTimbreMode.tts;
    }

    public String getOrdTimbreMode(HashMap<String, Object> map) {
        String str = getOneMapValue("switch_mode", map);
        String timbreMode = "";
        if (!TextUtils.isEmpty(str)) {
            switch (str) {
                case "晓晓":
                    timbreMode = DhDialect.ID_OFFICIAL_1;
                    break;
                case "晓甄":
                    timbreMode = DhDialect.ID_OFFICIAL_2;
                    break;
                case "晓晨":
                    timbreMode = DhDialect.ID_OFFICIAL_3;
                    break;
                case "云希":
                    timbreMode = DhDialect.ID_OFFICIAL_4;
                    break;
                case "云逸":
                    timbreMode = DhDialect.ID_OFFICIAL_5;
                    break;
                default:
                    break;
            }
        }
        LogUtils.d(TAG, "getOrdTimbreMode: " + timbreMode);
        return timbreMode;
    }

    public boolean isOrdTimbreMode(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isOrdTimbreMode");
        if (getTimbreMode(map) == null || getOrdTimbreMode(map) == null) {
            LogUtils.d(TAG, "isOrdTimbreMode error");
            return true;
        }
        return getTimbreMode(map).equals(getOrdTimbreMode(map));
    }

    public boolean isSupportNearBy(HashMap<String, Object> map) {
        return !isH37ACar(map);
    }

    public boolean isSupportCloseInBroadcast(HashMap<String, Object> map) {
        int mHeadrestAudioMode = operator.getIntProp(CommonSignal.COMMON_HEADREST_STATE);
        LogUtils.d(TAG, "isHeadrestSoundOpend mHeadrestAudioMode :" + mHeadrestAudioMode);
        int mHeadrestSoundConfig = operator.getIntProp(CommonSignal.COMMON_SUPPORT_HEADREST);
        LogUtils.d(TAG, "isHeadrestSoundConfig mHeadrestSoundConfig :" + mHeadrestSoundConfig);
        return mHeadrestSoundConfig == 0 || mHeadrestAudioMode <= 1;
    }

    public boolean getCloseInBroadcastStateSwitch(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getCloseInBroadcastStateSwitch");
        return DeviceHolder.INS().getDevices().getVoiceSettings().isEnableSwitch(DhSwitch.NearbyTTS);
    }

    public void setCloseInBroadcastStateSwitch(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setCloseInBroadcastStateSwitch");
        String str = "";
        if (map.containsKey("switch_type")) {
            str = (String) getValueInContext(map, "switch_type");
        }
        DeviceHolder.INS().getDevices().getVoiceSettings().enableSwitch(DhSwitch.NearbyTTS, str.equals("open"));
    }

    public boolean getLoginState(HashMap<String, Object> map) {
        UserCenterInterface userCenterInterface = DeviceHolder.INS().getDevices().getUserCenter();
        if (userCenterInterface != null) {
            boolean isLogin = userCenterInterface.isLogin();
            LogUtils.d(TAG, "getLoginState: " + isLogin);
            return isLogin;
        }
        return false;
    }

    public int getVoiceCopyNum(HashMap<String, Object> map) {
        // todo
        return 0;
    }

    public void openQrCodePage(HashMap<String, Object> map) {
        // todo
    }

    public boolean getVoiceCopyDialogState(HashMap<String, Object> map) {
        // todo
        return false;
    }

    public void setVoiceCopyDialogState(HashMap<String, Object> map) {
        // todo
    }

    public boolean getMediaCategory(HashMap<String, Object> map) {
        LogUtils.d(TAG, "getMediaCategory");
        if (map.containsKey("switch_mode") && map.containsKey("media_type")) {
            String mediaType = getOneMapValue("media_type", map);
            if (mediaType.equals("music")) {
                int curMediaType = operator.getIntProp(OtherSignal.OTHER_MUSIC_PREFERENCE);
                LogUtils.d(TAG, "curMusicMediaType: " + curMediaType);
                return curMediaType == getOrdMusicConfig(map);
            } else if (mediaType.equals("video")) {
                int curMediaType = operator.getIntProp(OtherSignal.OTHER_VIDEO_PREFERENCE);
                LogUtils.d(TAG, "curVideoMediaType: " + curMediaType);
                return curMediaType == getOrdVideoConfig(map);
            }
        }
        if (getOrdMusicConfig(map) != -1) {
            int curMediaType = operator.getIntProp(OtherSignal.OTHER_MUSIC_PREFERENCE);
            LogUtils.d(TAG, "curMusicMediaType: " + curMediaType);
            return curMediaType == getOrdMusicConfig(map);
        } else if (getOrdVideoConfig(map) != -1) {
            int curMediaType = operator.getIntProp(OtherSignal.OTHER_VIDEO_PREFERENCE);
            LogUtils.d(TAG, "curVideoMediaType: " + curMediaType);
            return curMediaType == getOrdVideoConfig(map);
        }
        return false;
    }

    public void setMediaCategory(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setMediaCategory");
        if (map.containsKey("switch_mode") && map.containsKey("media_type")) {
            String mediaType = getOneMapValue("media_type", map);
            if (mediaType.equals("music")) {
                LogUtils.d(TAG, "setMusicMediaCategory intelligent_recommend");
                operator.setIntProp(OtherSignal.OTHER_MUSIC_PREFERENCE, getOrdMusicConfig(map));
                return;
            } else if (mediaType.equals("video")) {
                LogUtils.d(TAG, "setVideoMediaCategory intelligent_recommend");
                operator.setIntProp(OtherSignal.OTHER_VIDEO_PREFERENCE, getOrdVideoConfig(map));
                return;
            }
        }
        if (getOrdMusicConfig(map) != -1) {
            LogUtils.d(TAG, "setMusicMediaCategory");
            operator.setIntProp(OtherSignal.OTHER_MUSIC_PREFERENCE, getOrdMusicConfig(map));
        } else if (getOrdVideoConfig(map) != -1) {
            LogUtils.d(TAG, "setVideoMediaCategory");
            operator.setIntProp(OtherSignal.OTHER_VIDEO_PREFERENCE, getOrdVideoConfig(map));
        }
    }

    private int getOrdMusicConfig(HashMap<String, Object> map) {
        int config = -1;
        if (map.containsKey("switch_mode") && map.containsKey("media_type")) {
            String str = (String) getValueInContext(map, "switch_mode");
            String mediaType = getOneMapValue("media_type", map);
            if (mediaType.equals("music")) {
                if (str.equals("intelligent_recommend")) {
                    config = 0;
                }
            }
        }
        if (map.containsKey("app_name")) {
            String str = (String) getValueInContext(map, "app_name");
            if (str.equals("网易云音乐")) {
                config = 2;
            } else if (str.equals("qq音乐")) {
                config = 1;
            }
        }
        LogUtils.d(TAG, "getMusicConfig: " + config);
        return config;
    }

    private int getOrdVideoConfig(HashMap<String, Object> map) {
        int config = -1;
        if (map.containsKey("switch_mode") && map.containsKey("media_type")) {
            String str = (String) getValueInContext(map, "switch_mode");
            String mediaType = getOneMapValue("media_type", map);
            if (mediaType.equals("video")) {
                if (str.equals("intelligent_recommend")) {
                    config = 0;
                }
            }
        }
        if (map.containsKey("app_name")) {
            String str = (String) getValueInContext(map, "app_name");
            if (str.equals("爱奇艺")) {
                config = 2;
            } else if (str.equals("腾讯视频")) {
                config = 1;
            }
        }
        LogUtils.d(TAG, "getOrdVideoConfig: " + config);
        return config;
    }

    public boolean getIsMusicType(HashMap<String, Object> map) {
        if (map.containsKey("media_type")) {
            String mediaType = getOneMapValue("media_type", map);
            return mediaType.equals("music");
        }
        return false;
    }

    public boolean isAppCompliance(HashMap<String, Object> map) {
        if (map.containsKey("switch_mode")) {
            String switchMode = getOneMapValue("switch_mode", map);
            if (!TextUtils.isEmpty(switchMode)) {
                return switchMode.equals("intelligent_recommend");
            }
        }
        if (map.containsKey("app_name")) {
            String appName = getOneMapValue("app_name", map);
            boolean isAppCompliance = false;
            for (String str : complianceAppName) {
                if (str.equals(appName)) {
                    isAppCompliance = true;
                    break;
                }
            }
            return isAppCompliance;
        }
        return false;
    }

    public boolean isSupportCarHealth(HashMap<String, Object> map) {
        return isH37ACar(map) || isH37BCar(map);
    }

    public boolean isSupportNewsPushOpen(HashMap<String, Object> map) {
        return isH56DCar(map);
    }

    public boolean isNewsPushOpen(HashMap<String, Object> map) {
        return operator.getBooleanProp(OtherSignal.OTHER_NEWS_SWITCH);
    }

    public void setNewsPushOpen(HashMap<String, Object> map) {
        String switchType = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(switchType)) {
            operator.setBooleanProp(OtherSignal.OTHER_NEWS_SWITCH, switchType.equals("open"));
        }
    }

    public void setLlmMode(HashMap<String, Object> map) {
        operator.setIntProp(CommonSignal.COMMON_LLM_MODE, getLlmOrderMode(map));
    }

    public int getLlmOrderMode(HashMap<String, Object> map) {
        int orderMode = 0;
        String switchMode = getOneMapValue("switch_mode", map);
        if (!TextUtils.isEmpty(switchMode)) {
            orderMode = (switchMode.equals("XiaoYao") || switchMode.equals("lantu")) ? 0 : 1;
        }
        LogUtils.d(TAG, "getLlmOrderMode: " + orderMode);
        return orderMode;
    }

    /** ----------------------- 语音设置 end------------------------------------------------------*/


    /** ----------------------- 手势 start------------------------------------------------------*/

    /**
     * 获取静态手势开关状态
     */
    public int getStaticGestureStatus(HashMap<String, Object> map) {
        int state = operator.getIntProp(OtherSignal.OTHER_STATIC_GESTURE_SWITCH);
        LogUtils.i(TAG, "getStaticGestureStatus : state-" + state);
        return state;
    }

    /**
     * 设置静态手势开关状态
     */
    public void setStaticGestureStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setIntProp(OtherSignal.OTHER_STATIC_GESTURE_SWITCH,
                "open".equals(switch_type) ? ICommon.Switch.ON : ICommon.Switch.OFF);
        LogUtils.i(TAG, "setStaticGestureStatus : switch_type-" + switch_type);
    }

    /**
     * 获取动态手势开关状态
     */
    public int getDynamicGestureStatus(HashMap<String, Object> map) {
        int state = operator.getIntProp(OtherSignal.OTHER_DYNAMIC_GESTURE_SWITCH);
        LogUtils.i(TAG, "getDynamicGestureStatus : state-" + state);
        return state;
    }

    /**
     * 设置动态手势开关状态
     */
    public void setDynamicGestureStatus(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        operator.setIntProp(OtherSignal.OTHER_DYNAMIC_GESTURE_SWITCH,
                "open".equals(switch_type) ? ICommon.Switch.ON : ICommon.Switch.OFF);
        LogUtils.i(TAG, "setDynamicGestureStatus : switch_type-" + switch_type);
    }

    /**
     * 打开智能手势设置页面
     */
    public boolean getGesturePageStatus(HashMap<String, Object> map) {
        return mSettingHelper.isCurrentState(SettingConstants.SMART_GESTURE);
    }

    /**
     * 关闭智能手势设置页面
     */
    public void setGesturePageCloseStatus(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getLauncher().backToHome(DeviceScreenType.CENTRAL_SCREEN);
    }

    /**
     * 获取隐私保护开关状态
     *
     * @return Int
     * 已开启 1
     * 已关闭 0
     */
    public int getPrivacySecuritySwitchState(HashMap<String, Object> map) {
        int status = operator.getIntProp(CommonSignal.COMMON_PRIVACY_PROTECTION);
        LogUtils.d(TAG, "getPrivacySecuritySwitchState: " + status);
        return status;
    }

    public boolean isH56CorH56D(HashMap<String, Object> map){//是否是两个遮阳帘，是否是H56C  H56D
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H56C".equals(carType) || "H56D".equals(carType)) {
            return true;
        }else{//H37A H37B
            return false;
        }
    }

    /** ----------------------- 手势 end--------------------------------------------------------*/
}
