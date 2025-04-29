package com.voyah.ai.logic.dc;

import android.text.TextUtils;
import android.util.Log2;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.IAdas;
import com.voice.sdk.device.carservice.constants.ICarSetting;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.dc.AdasInterface;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.AdasSignal;
import com.voice.sdk.device.carservice.signal.CarSettingSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.SysSettingSignal;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TTSAnsConstant;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voice.sdk.util.ThreadPoolUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;


/**
 * @Date 2024/5/14 14:18
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class AdasControlImpl extends AbsDevices {

    private static final String TAG = AdasControlImpl.class.getSimpleName();

    private static final String PKG_ADAS = "com.voyah.cockpit.adas";
    private final AdasInterface adasInterface;
    private String direction;

    public AdasControlImpl() {
        super();
        adasInterface = DeviceHolder.INS().getDevices().getAdasInterface();
        adasInterface.registerAVMStateCallback();
    }

    @Override
    public String getDomain() {
        return "adas";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        Log2.i(TAG, "initial tts :" + str);

        String repTTs = (String) getValueInContext(map, FINAL_TTS);
        if (!StringUtils.isEmpty(repTTs)) {
            return repTTs;
        }

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        switch (key) {
            case "chassis_style":
                String chassis_style = getChassisStyleStringFromContext(map);
                str = str.replace("@{chassis_style}", chassis_style);
                break;
            case "level":
                String level = getLevelStr(map);
                str = str.replace("@{level}", level);
                break;
            case "switch_mode":
                String switchMode = getSwitchMode(map);
                str = str.replace("@{switch_mode}", switchMode);
                break;
            case "adas_view_mode": //2D 3D
                String adasViewMode = getSwitchMode(map);
                str = str.replace("@{adas_view_mode}", adasViewMode);
                break;
            case "assist_style":
                String assist_style = getAssistStyleStringFromContext(map);
                str = str.replace("@{assist_style}", assist_style);
                break;
            case "number":
            case "speed_num":
                String number = (String) getValueInContext(map, "number");
                if (key.equals("number")) {
                    str = str.replace("@{number}", number);
                } else {
                    str = str.replace("@{speed_num}", number);
                }
                break;
            case "number_level":
            case "following_distance_level":
                int number_level = getCurWorkshopTimeInterval(map);
                if (key.equals("number_level")) {
                    str = str.replace("@{number_level}", number_level + "");
                } else {
                    str = str.replace("@{following_distance_level}", number_level + "");
                }
                break;
            case "app_name":
                str = str.replace("@{app_name}", "驾驶辅助");
                break;
            default:
                Log2.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;

        }
        Log2.i(TAG, "modified tts :" + str);

        return str;
    }

    /**
     * 是否支持打开驾驶辅助应用
     */
    public boolean isSupportAdasApp(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_SUPPORT_OPEN_APP);
    }

    /**
     * 判断当前是否自定义驾驶模式
     *
     * @param map
     * @return
     */
    public boolean isCustomDrivingMode(HashMap<String, Object> map) {
        return ICarSetting.DrivingMode.CUSTOM == operator.getIntProp(CarSettingSignal.CARSET_DRIVING_MODE);
    }

    /**
     * 判断是否需要切换底盘风格
     *
     * @param map
     * @return
     */
    public boolean shouldSwitchChassisStyle(HashMap<String, Object> map) {
        return getChassisStyleCode(map) != getChassisStyle();
    }

    public int getChassisStyle() {
        return operator.getIntProp(AdasSignal.ADAS_CHASSIS_STYLE);
    }

    public void setChassisStyle(HashMap<String, Object> map) {
        operator.setIntProp(AdasSignal.ADAS_CHASSIS_STYLE, getChassisStyleCode(map));
    }

    private String getSwitchMode(HashMap<String, Object> map) {
        String switchMode = "";
        if (map.containsKey("switch_mode")) {
            switchMode = (String) getValueInContext(map, "switch_mode");
        }
        switch (switchMode) {
            case "warning_only":
                switchMode = "仅报警";
                break;
            case "warning_assist":
                switchMode = "报警与辅助";
                break;
            case "sound":
                switchMode = "声音";
                break;
            case "quake":
                switchMode = "震动";
                break;
        }
        return switchMode;
    }

    /**
     * 从参数中提取意向底盘风格
     *
     * @param map
     * @return
     */
    private int getChassisStyleCode(HashMap<String, Object> map) {
        String expectedMode = "";
        if (map.containsKey("switch_mode")) {
            expectedMode = getValueInContext(map, "switch_mode") + "";
        } else {
            //模糊调节，轮切
            int curMode = getChassisStyle();
            if (curMode == IAdas.IChassisStyle.COMFORT) {
                map.put("switch_mode", "standard");
                return IAdas.IChassisStyle.STANDARD;
            } else if (curMode == IAdas.IChassisStyle.STANDARD) {
                map.put("switch_mode", "sport");
                return IAdas.IChassisStyle.SPORT;
            } else if (curMode == IAdas.IChassisStyle.SPORT) {
                map.put("switch_mode", "comfortable");
                return IAdas.IChassisStyle.COMFORT;
            }
        }
        int epsModeCode = IAdas.IChassisStyle.STANDARD;
        switch (expectedMode) {
            case "comfortable":
                epsModeCode = IAdas.IChassisStyle.COMFORT;
                break;
            case "sport":
                epsModeCode = IAdas.IChassisStyle.SPORT;
                break;
            case "standard":
                epsModeCode = IAdas.IChassisStyle.STANDARD;
                break;
        }
        return epsModeCode;
    }

    private String getLevelStr(HashMap<String, Object> map) {
        String levelStr = "";
        if (map.containsKey("adjust_type")) {
            String adjustType = (String) getValueInContext(map, "adjust_type");
            if ("increase".equalsIgnoreCase(adjustType)) {
                map.put("level", "max");
            } else if ("decrease".equalsIgnoreCase(adjustType)) {
                map.put("level", "min");
            }
        }
        if (map.containsKey("level")) {
            String str = (String) getValueInContext(map, "level");
            switch (str) {
                case "high":
                    levelStr = "高";
                    break;
                case "max":
                    levelStr = "最高";
                    break;
                case "mid":
                    levelStr = "中";
                    break;
                case "low":
                    levelStr = "低";
                    break;
                case "min":
                    levelStr = "最低";
                    break;
            }
        }
        return levelStr;
    }

    /**
     * ----------------------- 主动安全 start------------------------------------------------------
     */

    public boolean getFcwStateSwitch(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_FCW_SWITCH);
        Log2.d(TAG, "fcw status: " + status);

        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型

        if ("H56D".equals(carType)) {
            if(status == 3 || status ==4 || status==5){//代表是低中高，此时就是打开的状态
                return true;
            }
        }else{//H56C H37A  37B
            return status == ICommon.Switch.ON;
        }
        return status == ICommon.Switch.ON;
    }

    public int getFcwSensitivityConfig(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_FCW_SENSITIVITY);
        Log2.d(TAG, "fcw Sensitivity status: " + status);
        return status;
    }

    public int getOrdFcwSensitivityConfig(HashMap<String, Object> map) {
        int status = ICommon.Level.OFF;
        if (map.containsKey("level")) {
            String str = (String) getValueInContext(map, "level");
            switch (str) {
                case "high":
                case "max":
                    status = ICommon.Level.HIGH;
                    break;
                case "mid":
                    status = ICommon.Level.MID;
                    break;
                case "low":
                case "min":
                    status = ICommon.Level.LOW;
                    break;
            }
        }
        Log2.d(TAG, "fcw order sensitivity status: " + status);
        return status;
    }

    public boolean getAebStateSwitch(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_AEB_SWITCH);
        Log2.d(TAG, "aeb status: " + status);
        return status == ICommon.Switch.ON;
    }

    public boolean getRcwStateSwitch(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_RCW_SWITCH);
        Log2.d(TAG, "rcw status: " + status);
        return status == ICommon.Switch.ON;
    }

    public boolean getMebStateSwitch(HashMap<String, Object> map) {
        int state = operator.getIntProp(AdasSignal.ADAS_MEB_SWITCH);
        Log2.d(TAG, "meb status: " + state);
        return state == ICommon.Switch.ON;
    }

    public boolean getLdaStateSwitch(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_LDA_STATE);
        Log2.d(TAG, "lda status: " + status);
        return status == IAdas.ILDA.WARNING_ONLY || status == IAdas.ILDA.WARNING_ASSIST || status == 3;
    }

    public boolean isOrdLdaConfig(HashMap<String, Object> map) {
        int status = IAdas.ILDA.OFF;
        if (map.containsKey("switch_mode")) {
            String str = (String) getValueInContext(map, "switch_mode");
            switch (str) {
                case "warning_assist":
                    status = IAdas.ILDA.WARNING_ASSIST;
                    break;
                case "warning_only":
                    status = IAdas.ILDA.WARNING_ONLY;
                    break;
            }
        }
        Log2.d(TAG, "lda order status: " + status);
        return status == operator.getIntProp(AdasSignal.ADAS_LDA_STATE);
    }

    public boolean isOrdLdwConfig(HashMap<String, Object> map) {
        int status = -1;
        if (map.containsKey("switch_mode")) {
            String str = (String) getValueInContext(map, "switch_mode");
            switch (str) {
                case "sound":
                    status = IAdas.ILDAWarningStyle.VOICE;
                    break;
                case "quake":
                    status = IAdas.ILDAWarningStyle.VIBRATION;
                    break;
            }
        }
        Log2.d(TAG, "ldw order status: " + status);
        return status == operator.getIntProp(AdasSignal.ADAS_LDA_WARNING_STYLE);
    }

    public boolean getElkStateSwitch(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_ELK_SWITCH);
        Log2.d(TAG, "elk status: " + status);
        return status == ICommon.Switch.ON;
    }

    public boolean getEsaStateSwitch(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_ESA_SWITCH);
        Log2.d(TAG, "esa status: " + status);
        return status == ICommon.Switch.ON;
    }

    public boolean isSupportLca(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_LCA_CONFIG);
    }

    public boolean getLcaStateSwitch(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_LCA_SWITCH);
        Log2.d(TAG, "lca status: " + status);
        return status == ICommon.Switch.ON;
    }

    public boolean getLcaLevel(HashMap<String, Object> map) {
        int ordLevelNum = -1;
        int curLevelNum = operator.getIntProp(AdasSignal.ADAS_LCA_SENSITIVITY);
        String ordLevelStr = getOneMapValue("level", map);
        if (!TextUtils.isEmpty(ordLevelStr)) {
            if (ordLevelStr.equals("low") || ordLevelStr.equals("min")) {
                ordLevelNum = ICommon.Level.LOW;
            } else if (ordLevelStr.equals("high") || ordLevelStr.equals("max")) {
                ordLevelNum = ICommon.Level.MID;
            }
        }
        Log2.d(TAG, "getLcaLevel: curLevelNum" + curLevelNum);
        Log2.d(TAG, "getLcaLevel: ordLevelNum" + ordLevelNum);
        return curLevelNum == ordLevelNum;
    }

    public boolean isSupportFcta(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_FCTA_CONFIG);
    }

    public boolean getFctaStateSwitch(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_FCTA_SWITCH);
        Log2.d(TAG, "fcta status: " + status);
        return status == ICommon.Switch.ON;
    }

    public boolean getRctaStateSwitch(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_RCTA_SWITCH);
        Log2.d(TAG, "rcta status: " + status);
        return status == ICommon.Switch.ON;
    }

    public int getDowStateSwitch(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_DOW_SWITCH);
        Log2.d(TAG, "dow status: " + status);
        return status;
    }

    public boolean isSupportFvsr(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_FVSR_CONFIG);
    }

    public int getFvsrStateSwitch(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_FVSR_SWITCH);
        Log2.d(TAG, "fvsr status: " + status);
        return status;
    }

    public boolean getTlcStateSwitch(HashMap<String, Object> map) {
        int status = operator.getIntProp(AdasSignal.ADAS_TLC_SWITCH);
        Log2.d(TAG, "tlc status: " + status);
        return status == ICommon.Switch.ON;
    }

    /**
     * ----------------------- 主动安全 end------------------------------------------------------
     */

    private String getChassisStyleStringFromContext(HashMap<String, Object> map) {
        String res = "";
        String chassisStyle = (String) getValueInContext(map, "switch_mode");
        if (!TextUtils.isEmpty(chassisStyle)) {
            switch (chassisStyle) {
                case "comfortable":
                    res = "舒适";
                    break;
                case "sport":
                    res = "运动";
                    break;
                case "standard":
                    res = "标准";
                    break;
                default:
            }
        }
        return res;
    }

    /**
     * ====================ADAS ZW START======================
     */
    private String getAssistStyleStringFromContext(HashMap<String, Object> map) {
        String res = "";
        String chassisStyle = (String) getValueInContext(map, "switch_mode");
        if (!TextUtils.isEmpty(chassisStyle)) {
            switch (chassisStyle) {
                case "comfortable":
                    res = "舒适优先";
                    break;
                case "efficiency":
                    res = "效率优先";
                    break;
                case "standard":
                    res = "标准选项";
                    break;
                default:
            }
        }
        return res;
    }

    public int getBSDState(HashMap<String, Object> map) {
        return operator.getIntProp(AdasSignal.ADAS_BSD_SWITCH);
    }

    public int getTSRState(HashMap<String, Object> map) {
        return operator.getIntProp(AdasSignal.ADAS_TSR_SWITCH);
    }

    /**
     * 是否支持智能行车风格调节
     */
    public boolean isSupportAssistDrive(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_ASSIST_DRIVE_CONFIG);
    }

    public int getAssidDrvgStyle(HashMap<String, Object> map) {
        return operator.getIntProp(AdasSignal.ADAS_ASSIST_DRV_STYLE);
    }

    public int expectAssidDrvgStyle(HashMap<String, Object> map) {
        int result = IAdas.AssidDrvgStyle.STANDARD;
        if (map.containsKey("switch_mode")) {
            String sm = (String) map.getOrDefault("switch_mode", "");
            switch (sm) {
                case "comfortable":
                    result = IAdas.AssidDrvgStyle.COMFORT;
                    break;
                case "efficiency":
                    result = IAdas.AssidDrvgStyle.EFFICIENCY;
                    break;
                case "standard":
                    result = IAdas.AssidDrvgStyle.STANDARD;
                    break;
            }
        }
        return result;
    }

    /**
     * 定位到智能行车风格设置项
     */
    public void openIntelligentDriving(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.SMART_DRIVE_SDS);
    }

    public void openActiveSafe(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.ACTIVE_SAFETY_PAGE);
    }

    public boolean isOpenActiveSafety(HashMap<String, Object> map) {
        return mSettingHelper.isCurrentState(SettingConstants.ACTIVE_SAFETY_PAGE);
    }

    public boolean isSupportNoa(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_NOA_CONFIG);
    }

    public int getNoaState(HashMap<String, Object> map) {
        return operator.getIntProp(AdasSignal.ADAS_NOA_SWITCH);
    }

    /**
     * ==============自动变道=============
     **/
    public boolean isSupportALCO(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_ALCO_CONFIG);
    }

    public int getAlcoState(HashMap<String, Object> map) {
        return operator.getIntProp(AdasSignal.ADAS_ALCO_SWITCH);
    }

    /**
     * 变道前确认
     */
    public int getLcocState(HashMap<String, Object> map) {
        return operator.getIntProp(AdasSignal.ADAS_LCOC_SWITCH);
    }

    /**
     * 主动驶出超车道
     */
    public int getEolState(HashMap<String, Object> map) {
        return operator.getIntProp(AdasSignal.ADAS_EOL_SWITCH);
    }

    /**
     * 智能限速提醒
     */
    public boolean getIsaState(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_ISA_SWITCH);
    }

    /**
     * =================超速报警提示====================
     */
    public boolean isSupportOWA(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_OWA_CONFIG);
    }

    public int getOwaState(HashMap<String, Object> map) {
        return operator.getIntProp(AdasSignal.ADAS_OWA_SWITCH);
    }


    public boolean getIslcState(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_ISLC_SWITCH);
    }


    public boolean isSupportFunc(HashMap<String, Object> map) {
        String name = "";
        int carServiceId = -1;
        if (map.containsKey("tab_name")) {

            //TODO 先默认都支持
            return true;
        }
        return false;
    }


/** ====================ADAS ZW END ======================*/

    /**
     * ----------------------- 公共方法 start------------------------------------------------------
     */

    public boolean isTDASupport(HashMap<String, Object> map) {
        int state = getTDA4State(map);
        return state == IAdas.TdaState.UNPAID_SUPPORTED || state == IAdas.TdaState.PAID_SUPPORTED;
    }

    public boolean isTDAPaid(HashMap<String, Object> map) {
        int state = getTDA4State(map);
        return state == IAdas.TdaState.PAID_SUPPORTED;
    }

    /**
     * @return
     */
    public int getTDA4State(HashMap<String, Object> map) {
        String func = extractFuncFromMap(map);
        return operator.getIntProp(AdasSignal.ADAS_TDA4STATE + func);
    }

    private String extractFuncFromMap(HashMap<String, Object> map) {
        String funcName = "";
        String nlu_info = (String) getValueInContext(map, DCContext.NLU_INFO);
        if (map.containsKey("tab_name")) {
            funcName = getValueInContext(map, "tab_name") + "";
        } else if (nlu_info.contains("adas_noa")) {
            funcName = "noa";
        } else if (nlu_info.contains("adas_laneChange")) {
            funcName = "tlc";
        }
        return funcName.toUpperCase();
    }

    /** ----------------------- 公共方法 end------------------------------------------------------*/

    /**
     * ----------------------- 激活NOA start----------------------------------------------------
     */

    public boolean getNavStatus(HashMap<String, Object> map) {
        return operator.getBooleanProp(CommonSignal.COMMON_IS_NAVI);
    }

    public void setOpenNOAPageUI(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.SMART_DRIVE_NOA);
    }

    public int getDriveGearPosition(HashMap<String, Object> map) {
        return operator.getIntProp(CommonSignal.COMMON_GEAR_INFO);
    }

    public boolean getActivateNOA(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_NOA_SWITCH);
    }

    public boolean isNoaActivate(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_NOA_ACTIVATE_STATE);
    }

    public void setActivateNOA(HashMap<String, Object> map) {
        adasInterface.setActivateNOA();
    }

    /**
     * ----------------------- 激活NOA end------------------------------------------------------
     */

    /**
     * ----------------------- 变道 start------------------------------------------------------
     */

    public boolean isExecute(HashMap<String, Object> map) {
        return map.containsKey("nlu_type");
    }

    public boolean isLeftward(HashMap<String, Object> map) {
        String direction = (String) getValueInContext(map, "direction");
        return direction.equals("leftward");
    }

    public int getChangeAssist(HashMap<String, Object> map) {
        return operator.getIntProp(AdasSignal.ADAS_TLC_SWITCH);
    }

    public void setOpenChangeAssistPageUI(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.SMART_DRIVE_TLC);
    }

    public boolean isNeedDetermineLaneChangeSwitch(HashMap<String, Object> map) {
        return adasInterface.isNeedDetermineLaneChangeSwitch();
    }

    public boolean isNoaOrLccOpened(HashMap<String, Object> map) {
        return adasInterface.isNoaOrLccOpened();
    }

    public void setChangeLanes(HashMap<String, Object> map) {
        adasInterface.setChangeLanes(map);
    }

    /**
     * ----------------------- 变道 end------------------------------------------------------
     */

    /**
     * ----------------------- 车间距 start--------------------------------------------------
     */

    public boolean isAccOrLccOpened(HashMap<String, Object> map) {
        return adasInterface.isAccOrLccOpened();
    }

    public boolean isLessThanLowest(HashMap<String, Object> map) {
        return adasInterface.isLessThanLowest(map);
    }

    public boolean isBeyondHighest(HashMap<String, Object> map) {
        return adasInterface.isBeyondHighest(map);
    }

    public boolean isMaxEqualTo4(HashMap<String, Object> map) {
        return adasInterface.isMaxEqualTo4();
    }

    public int getCurWorkshopTimeInterval(HashMap<String, Object> map) {
        return operator.getIntProp(AdasSignal.ADAS_CUR_WORKSHOP_TIME_INTERVAL);
    }

    public boolean isWorkshopDoesNotQualify(HashMap<String, Object> map) {
        return adasInterface.isWorkshopDoesNotQualify(map);
    }

    public void setCurWorkshopTimeInterval(HashMap<String, Object> map) {
        adasInterface.setCurWorkshopTimeInterval(map);
    }

    /**
     * ----------------------- 车间距 end----------------------------------------------------
     */

    /**
     * ----------------------- 巡航速度 start--------------------------------------------------
     */

    /**
     * 获取要设置的number值
     */
    public int getSetCruiseSpeedNumber(HashMap<String, Object> map) {
        int number = Integer.parseInt((String) getValueInContext(map, "number"));
        Log2.i(TAG, "getSetCruiseSpeedNumber number :" + number);
        return number;
    }

    /**
     * 当前车速
     */
    public int curSpeedRange(HashMap<String, Object> map) {
        return (int) operator.getFloatProp(CommonSignal.COMMON_SPEED_INFO);
    }

    /**
     * 获取当前巡航车速
     */
    public int getCruiseSpeed(HashMap<String, Object> map) {
        return operator.getIntProp(AdasSignal.ADAS_CRUISESPEED);
    }

    /**
     * 设置巡航车速
     */
    public void setCruiseSpeed(HashMap<String, Object> map) {
        String adjust_type = (String) getValueInContext(map, "adjust_type");
        int cruiseSpeed = getCruiseSpeed(map);
        if (adjust_type.equals("increase")) {
            operator.setIntProp(AdasSignal.ADAS_CRUISESPEED, Math.min(150, cruiseSpeed + 5));
        } else if (adjust_type.equals("decrease")) {
            operator.setIntProp(AdasSignal.ADAS_CRUISESPEED, Math.max(10, cruiseSpeed - 5));
        } else if (adjust_type.equals("set")) {
            if (map.containsKey("level")) {
                String level = (String) getValueInContext(map, "level");
                if (level.equals("max")) {
                    operator.setIntProp(AdasSignal.ADAS_CRUISESPEED, 150);
                } else if (level.equals("min")) {
                    operator.setIntProp(AdasSignal.ADAS_CRUISESPEED, 10);
                }
            } else if (map.containsKey("number")) {
                int setCruiseSpeedNumber = getSetCruiseSpeedNumber(map);
                operator.setIntProp(AdasSignal.ADAS_CRUISESPEED, setCruiseSpeedNumber);
            }
        }
    }

    /**
     * 速度范围是不是在 10-150之间
     */
    public boolean isCurSpeedRange(HashMap<String, Object> map) {
        int setCruiseSpeedNumber = getSetCruiseSpeedNumber(map);
        if (setCruiseSpeedNumber >= 10 && setCruiseSpeedNumber <= 150) {
            return true;
        }
        return false;
    }

    /**
     * 要设置的车速 - 当前的车速 > 60
     */
    public boolean isSpeedUp60(HashMap<String, Object> map) {
        int cruiseSpeed = getCruiseSpeed(map);
        Log2.i(TAG, "isSpeedUp60 cruiseSpeed :" + cruiseSpeed);
        if (map.containsKey("number")) {
            int setCruiseSpeedNumber = getSetCruiseSpeedNumber(map);
            if (setCruiseSpeedNumber - cruiseSpeed > 60) {
                return true;
            } else {
                return false;
            }
        }
        if (map.containsKey("level")) {
            //巡航车速调到最大
            if (150 - cruiseSpeed > 60) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当前的车速 - 要设置的车速 > 60
     */
    public boolean isSpeedDown60(HashMap<String, Object> map) {
        int cruiseSpeed = getCruiseSpeed(map);
        Log2.i(TAG, "isSpeedUp60 cruiseSpeed :" + cruiseSpeed);
        if (map.containsKey("number")) {
            int setCruiseSpeedNumber = getSetCruiseSpeedNumber(map);
            if (cruiseSpeed - setCruiseSpeedNumber > 60) {
                return true;
            } else {
                return false;
            }
        }
        if (map.containsKey("level")) {
            //巡航车速调到最小
            if (cruiseSpeed - 10 > 60) {
                return true;
            }
        }
        return false;
    }

    /**
     * 加/减速度过快安全提醒
     */
    public boolean isSpeedChangeReminder(HashMap<String, Object> map) {
        Log2.i(TAG, "isSpeedChangeReminder");
        return keyContextInMap(map, "choose_type");
    }

    /**
     * 是取消，还是确认
     */
    public boolean isCancelConfirm(HashMap<String, Object> map) {
        String chooseType = (String) getValueInContext(map, "choose_type");
        Log2.i(TAG, "isCancelConfirm chooseType is " + chooseType);
        return StringUtils.equals(chooseType, "confirm");
    }

    /**
     * ----------------------- 巡航速度 end----------------------------------------------------
     */

    /**
     * ----------------------- 自动泊车 start--------------------------------------------------
     */

    /**
     * 设置自动泊车的开关
     */
    public void setAPAState(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(AdasSignal.ADAS_APA, switchType.equals("open"));
    }

    /**
     * ----------------------- 自动泊车 end----------------------------------------------------
     */

    /**
     * ----------------------- 记忆泊车 start--------------------------------------------------
     */

    /**
     * 设置记忆泊车的开关
     */
    public void setHPPState(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(AdasSignal.ADAS_HPP, switchType.equals("open"));
    }

    /**
     * ----------------------- 记忆泊车 end----------------------------------------------------
     */

    /**
     * ----------------------- 寻迹倒车 start--------------------------------------------------
     */

    /**
     * 设置寻迹倒车的开关
     */
    public void setRADState(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(AdasSignal.ADAS_RAD, switchType.equals("open"));
    }

    /**
     * ----------------------- 寻迹倒车 end--------------------------------------------------
     */

    /**
     * ----------------------- 360环视 start----------------------------------------------------
     */

    /**
     * 设置360环视的开关
     */
    public void setAVMState(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(AdasSignal.ADAS_AVM, switchType.equals("open"));
    }

    /**
     * 360环视是否在前台
     *
     * @return
     */
    public boolean isAVMOpen(HashMap<String, Object> map) {
        int intProp = operator.getIntProp(CommonSignal.COMMON_360_STATE);
        return intProp == 1;
    }

    /**
     * ----------------------- 360环视 end----------------------------------------------------
     */

    /**
     * ----------------------- 透明底盘 start----------------------------------------------------
     */

    /**
     * 设置底盘透明的开关
     */
    public void setCarTransparentState(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        operator.setBooleanProp(AdasSignal.ADAS_CAR_TRANSPARENT, switchType.equals("open"));
    }

    /**
     * ----------------------- 透明底盘 end----------------------------------------------------
     */

    /**
     * ----------------------- 2D/3D模式 start----------------------------------------------------
     */

    /**
     * 获取当前全景影像 2D 3D 视图
     */
    public int getAvmViewMode(HashMap<String, Object> map) {
        return operator.getIntProp(AdasSignal.ADAS_AVM_VIEW);
    }

    /**
     * 当前模式 = 要设置的模式
     */
    public boolean isSameViewMode(HashMap<String, Object> map) {
        boolean isSameViewMode = false;
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        int avmViewMode = getAvmViewMode(map);
        Log2.i(TAG, "isSameViewMode avmViewMode :" + avmViewMode);
        if (avmViewMode == 0) {
            if (switch_mode.equals("3d")) {
                isSameViewMode = true;
            } else {
                isSameViewMode = false;
            }
        } else if (avmViewMode == 1) {
            if (switch_mode.equals("2d")) {
                isSameViewMode = true;
            } else {
                isSameViewMode = false;
            }
        }
        return isSameViewMode;
    }

    /**
     * 设置全景影像 2D 3D 视图模式
     */
    public void setAvmViewMode(HashMap<String, Object> map) {
        operator.setBooleanProp(AdasSignal.ADAS_AVM_VIEW, true);
    }

    /**
     * 切换全景影像视图模式
     */
    public void changeAvmViewMode(HashMap<String, Object> map) {
        int avmViewMode = getAvmViewMode(map);
        String ttsText = "2d";
        if (avmViewMode == 1) {
            ttsText = "3d";
        }
        map.put("switch_mode", ttsText);
        setAvmViewMode(map);
    }

    /**
     * ----------------------- 2D/3D模式 end----------------------------------------------------
     */

    //--------------------------------车道偏离----------------start-----------
    public boolean isGearInquiry(HashMap<String, Object> map) {
        int isLowGear = operator.getIntProp(AdasSignal.ADAS_LANE_DEPARTURE_GEAR_INQUIRY_GET);//1 是低 2是高
        String level = (String) getValueInContext(map, "level");
        Log2.i(TAG, "---isGearInquiry-------- isLowGear :" + isLowGear + "------level:" + level);
        if (level.equals("low") && isLowGear == 1) {//代表用户是想调到低档，并且当前已经是低档了
            return true;
        }
        if (level.equals("high") && isLowGear == 2) {//代表用户是想调到高档，并且当前已经是高档了
            return true;
        }
        return false;
    }

    //跳转到对应的页面
    public void jumpGearInquiry(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.ADAS_LANE_DEPARTURE);
    }


    //--------------------------------车道偏离----------------end-----------


    //--------------------------------自动(后向)紧急制动---56D低速紧急制动-------------start-----------

    public boolean isAutoEmergencyStopBackOpen(HashMap<String, Object> map) {
        int isOpenEmergency = operator.getIntProp(AdasSignal.ADAS_AUTO_EMERGENCY_STOP_BACK_GET);//0是关 1是开
        Log2.i(TAG, "---isAutoEmergencyStopBackOpen-------- isOpenEmergency :" + isOpenEmergency + "------");
        if (isOpenEmergency == 1) {//代表已经是打开的
            return true;
        }
        return false;
    }

    public boolean isAutoEmergencyStopBackClose(HashMap<String, Object> map) {
        int isOpenEmergency = operator.getIntProp(AdasSignal.ADAS_AUTO_EMERGENCY_STOP_BACK_GET);//0是关 1是开
        Log2.i(TAG, "---isAutoEmergencyStopBackClose-------- isOpenEmergency :" + isOpenEmergency + "------");
        if (isOpenEmergency == 0) {//代表已经是关闭的
            return true;
        }
        return false;
    }

    //跳转到对应的页面
    public void jumpAutoEmergencyStopBack(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.ADAS_AUTO_EMERGENCY_STOP_BACK);
    }


    //--------------------------------自动(后向)紧急制动----56D低速紧急制动------------end-----------


    //--------------------------------自动紧急转向辅助----------------start-----------
    public boolean isAutoEmergencySteeringOpen(HashMap<String, Object> map) {
        int isOpenEmergency = operator.getIntProp(AdasSignal.ADAS_AUTO_EMERGENCY_STEERING_GET);//0是关 1是开
        Log2.i(TAG, "---isAutoEmergencySteeringOpen-------- isOpenEmergency :" + isOpenEmergency + "------");
        if (isOpenEmergency == 1) {//代表已经是打开的
            return true;
        }
        return false;
    }

    public boolean isAutoEmergencySteeringClose(HashMap<String, Object> map) {
        int isOpenEmergency = operator.getIntProp(AdasSignal.ADAS_AUTO_EMERGENCY_STEERING_GET);//0是关 1是开
        Log2.i(TAG, "---isAutoEmergencySteeringClose-------- isOpenEmergency :" + isOpenEmergency + "------");
        if (isOpenEmergency == 0) {//代表已经是关闭的
            return true;
        }
        return false;
    }

    //跳转到对应的页面
    public void jumpAutoEmergencySteering(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.ADAS_EMERGENCY_STEERING);
    }


    //--------------------------------自动紧急转向辅助----------------end-----------


    //------------------------------红绿灯提醒辅助--------start-------------------------

    public boolean isTrafficLightOpen(HashMap<String, Object> map) {
        int isOpenEmergency = operator.getIntProp(AdasSignal.ADAS_TRAFFIC_LIGHT_GET);//0是关 1是开
        Log2.i(TAG, "---isTrafficLightOpen-------- isOpenEmergency :" + isOpenEmergency + "------");
        if (isOpenEmergency == 1) {//代表已经是打开的
            return true;
        }
        return false;
    }

    public boolean isTrafficLightClose(HashMap<String, Object> map) {
        int isOpenEmergency = operator.getIntProp(AdasSignal.ADAS_TRAFFIC_LIGHT_GET);//0是关 1是开
        Log2.i(TAG, "---isTrafficLightClose-------- isOpenEmergency :" + isOpenEmergency + "------");
        if (isOpenEmergency == 0) {//代表已经是关闭的
            return true;
        }
        return false;
    }

    //跳转到对应的页面
    public void jumpTrafficLight(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.ADAS_TRAFFIC_LIGHT);
    }


    //------------------------------红绿灯提醒辅助--------end-------------------------

    //-------------------------超速报警提醒报警方式--------start--------------------

    public boolean isOpenSpeedTarget(HashMap<String, Object> map){ //true 代表打开  false关闭
        int isOpen = operator.getIntProp(AdasSignal.ADAS_OWA_SWITCH);//0x0: Off 0x1: On   超速报警提醒开关
        int isTarget = operator.getIntProp(AdasSignal.ADAS_OVER_SPEED_TARGET_GET);//carservices表：0: 视觉  1: 视觉和声音  矩阵信号表： 0关闭  1视觉，2视觉和声音
        Log2.i(TAG, "---isOpenSpeedTarget-------- isOpen :" + isOpen + "------"+"--------isTarget:"+isTarget);
//        if(isOpen ==0){//防止视觉和声音的状态信号没有改变， 导致判断出错
//            return false;
//        }
        if(isTarget ==1 || isTarget ==2){
            return true;//代表是打开的
        }else {
            return false;//代表是非打开状态
        }
    }



    public boolean isOverSpeedTarget(HashMap<String, Object> map) {
        int isTarget = operator.getIntProp(AdasSignal.ADAS_OVER_SPEED_TARGET_GET);//0x0: 视觉  0x1: 视觉和声音
        Log2.i(TAG, "---isOverSpeedTarget-------- isTarget :" + isTarget + "------");
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        if (switch_mode.equals("vision") && isTarget == 0) {//代表是视觉
            map.put("switch_mode", "仅视觉");
            return true;
        }

        if (switch_mode.equals("visionAndSound") && isTarget == 1) {//代表是视觉和声音
            map.put("switch_mode", "视觉和声音");
            return true;
        }

        return false;
    }


    public void jumpOverSpeedTarget(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.ADAS_OVER_SPEED_TARGET);
    }


    //-------------------------超速报警提醒报警方式--------end--------------------


    //------------------------------闪灯鸣笛提醒--------start-------------------------

    public boolean isLightsAndSoundOpen(HashMap<String, Object> map) {
        int isOpen = operator.getIntProp(AdasSignal.ADAS_LIGHTS_AND_SOUND_GET);//0是关 1是闪灯  2是闪灯鸣笛
        Log2.i(TAG, "---isLightsAndSoundOpen-------- isOpen :" + isOpen + "------");
        if (isOpen == 1 || isOpen ==2) {//代表已经是打开的
            return true;
        }
        return false;
    }

    public boolean isLightsAndSoundClose(HashMap<String, Object> map) {
        int isClose = operator.getIntProp(AdasSignal.ADAS_LIGHTS_AND_SOUND_GET);//0是关 1是闪灯  2是闪灯鸣笛
        Log2.i(TAG, "---isLightsAndSoundClose-------- isClose :" + isClose + "------");
        if (isClose == 0) {//代表已经是关闭的
            return true;
        }
        return false;
    }


    public boolean isLightsAndSoundTarget(HashMap<String, Object> map) {
        int isTarget = operator.getIntProp(AdasSignal.ADAS_LIGHTS_AND_SOUND_GET);//0是关 1是闪灯  2是闪灯鸣笛
//        int isTarget = operator.getIntProp(AdasSignal.ADAS_LIGHTS_AND_SOUND_TARGET_GET);//0x0: 闪灯  0x1: 闪灯和鸣笛
        Log2.i(TAG, "---isOverSpeedTarget-------- isTarget :" + isTarget + "------");
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        if (switch_mode.equals("flashLight") && isTarget == 1) {//代表是闪灯
            map.put("switch_mode", "闪灯");
            return true;
        }

        if (switch_mode.equals("hornsAndFlashLight") && isTarget == 2) {//代表是闪灯和鸣笛
            map.put("switch_mode", "鸣笛与闪灯");
            return true;
        }

        return false;
    }

    //跳转到对应的页面
    public void jumpLightsAndSound(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.ADAS_LIGHTS_AND_SOUND);
    }


    //------------------------------闪灯鸣笛提醒--------end-------------------------

    //----------------------盲区监测报警方式------start---------------------

    public boolean isBlindSpotMonitoringTarget(HashMap<String, Object> map) {
        int isTarget = operator.getIntProp(AdasSignal.ADAS_BLIND_SPOT_MONITORING_GET);//0:off  1：灯光  2声音和灯光
        Log2.i(TAG, "---isBlindSpotMonitoringTarget-------- isTarget :" + isTarget + "------");
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        if (switch_mode.equals("lightAndSound") && isTarget == 0) {//代表是声音报警
            map.put("switch_mode", "灯光与声音");
            return true;
        }

        if (switch_mode.equals("light") && isTarget == 1) {//代表是仅灯光，无声音
            map.put("switch_mode", "仅灯光");
            return true;
        }

        return false;
    }

    //跳转到对应的页面
    public void jumpBlindSpotMonitoring(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.ADAS_BLIND_SPOT_MONITORING);
    }

    //是否已经打开盲区监测报警
    public boolean isOpenBlindSpotMonitoring(HashMap<String, Object> map){
        int isTarget = operator.getIntProp(AdasSignal.ADAS_BLIND_SPOT_MONITORING_GET);//0:off  1：灯光  2声音和灯光
        Log2.i(TAG, "---isOpenBlindSpotMonitoring-------- isTarget :" + isTarget + "------");
        if(isTarget ==1 || isTarget ==2){
            return true;
        }
        return false;
    }


    //----------------------盲区监测报警方式------end---------------------

    //===========================打开驾驶辅助 ADAS APP==========================
    //adas 是否打开
    public boolean isAdasOpen(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(PKG_ADAS, DeviceScreenType.CENTRAL_SCREEN);
    }

    public void openAdasApp(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getSystem().getApp().openApp(PKG_ADAS, null);

    }

    public void closeAdasApp(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getSystem().getApp().closeApp(PKG_ADAS, DeviceScreenType.CENTRAL_SCREEN);
    }

    public boolean is4WD(HashMap<String, Object> map) {
//        boolean is4WD = MegaSystemProperties.getInt(MegaProperties.CONFIG_DRIVING_TYPE, 1) == 1;
//        Log.d(TAG, "是否四驱：" + is4WD);
//        return is4WD;
        return true;
    }

    /**
     * 是否支持可调阻尼悬架
     *
     * @param map
     * @return
     */
    public boolean isSupportCDC(HashMap<String, Object> map) {
        return operator.getBooleanProp(AdasSignal.ADAS_CDC_CONFIG);
    }

    //=======================56D 需求适配===========================/

    //智能行车风格设置
    public void handleIntelligentDriving(HashMap<String, Object> map) {
        String tts = "";
        String carModel = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if ("h56c".equalsIgnoreCase(carModel)) {
            tts = TtsBeanUtils.getTtsBean(1100006).getSelectTTs();
            map.put(FINAL_TTS, tts);
            return;
        }
        if (!isFromFirstRowLeft(map)) {
            tts = TtsBeanUtils.getTtsBean(5010503).getSelectTTs();
            map.put(FINAL_TTS, tts);
            return;
        }
        if (isChildSound(map)) {
            //TODO 等声纹需求确定TTS
            tts = TtsBeanUtils.getTtsBean(1100006).getSelectTTs();
            map.put(FINAL_TTS, tts);
            return;
        }
        if (isRestrictGearsR(map)) {
            tts = TTSAnsConstant.PARK_NOT_SUPPORT;
            map.put(FINAL_TTS, tts);
            return;
        }

        int curStyle = getAssidDrvgStyle(map);
        int expectStyle = expectAssidDrvgStyle(map);
        Log2.d(TAG, "expectStyle:" + expectStyle + " curStyle:" + curStyle);
        if (curStyle == expectStyle) {
            //已经是
            tts = TtsBeanUtils.getTtsBean(5011400, getAssistStyleStringFromContext(map)).getSelectTTs();
        } else {
            mSettingHelper.exec(SettingConstants.SMART_DRIVE_SDS);
            //大屏幕操作
            tts = TtsBeanUtils.getTtsBean(5007300).getSelectTTs();
        }
        map.put(FINAL_TTS, tts);
    }


    //打开、关闭智能限速控制
    public void handleSpeedCtrl(HashMap<String, Object> map) {
        String tts = "";
        String switchMode = (String) getValueInContext(map, "switch_type");
        if (!isFromFirstRowLeft(map)) {
            tts = TtsBeanUtils.getTtsBean(5010503).getSelectTTs();
            map.put(FINAL_TTS, tts);
            return;
        }
        if (isChildSound(map)) {
            //TODO 等声纹需求确定TTS
            tts = TtsBeanUtils.getTtsBean(1100006).getSelectTTs();
            map.put(FINAL_TTS, tts);
            return;
        }
        if (isRestrictGearsR(map)) {
            tts = TTSAnsConstant.PARK_NOT_SUPPORT;
            map.put(FINAL_TTS, tts);
            return;
        }

        if (switchMode.equals("open")) {
            if (getIslcState(map)) {
                tts = TtsBeanUtils.getTtsBean(5013500).getSelectTTs();
            } else {
                mSettingHelper.exec(SettingConstants.SMART_DRIVE_ISLC);
                tts = TtsBeanUtils.getTtsBean(5007300).getSelectTTs();
            }
        } else if (switchMode.equals("close")) {
            if (getIslcState(map)) {
                mSettingHelper.exec(SettingConstants.SMART_DRIVE_ISLC);
                tts = TtsBeanUtils.getTtsBean(5007300).getSelectTTs();
            } else {
                tts = TtsBeanUtils.getTtsBean(5013600).getSelectTTs();
            }
        }
        map.put(FINAL_TTS, tts);
    }

}
