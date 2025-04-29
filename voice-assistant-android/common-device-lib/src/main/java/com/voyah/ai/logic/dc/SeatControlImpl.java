package com.voyah.ai.logic.dc;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.dc.SeatInterface;
import com.voice.sdk.device.carservice.dc.carsetting.SeatConstants;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.VCarSeatSignal;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.util.LogUtils;
import com.voyah.ds.common.entity.context.FlowContextKey;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatControlImpl extends AbsDevices {

    private static final String TAG = SeatControlImpl.class.getSimpleName();

    private final SeatInterface seatInterface;

    public SeatControlImpl() {
        super();
        seatInterface = DeviceHolder.INS().getDevices().getCarService().getSeatInterface();
    }

    @Override
    public String getDomain() {
        return "seat";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        int index = 0;

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        switch (key) {
            case "position":
                String pos = mergePositionToString(map);
                str = str.replace("@{position}", pos);
                break;
            case "switch_mode":
            case "seat_mr_mode":
                String switch_mode = (String) getValueInContext(map, "switch_mode");
                List<String> massageMode = getMassageMode(map);
                if (key.contains("switch_mode")) {
                    if (massageMode.contains(switch_mode)) {
                        str = str.replace("@{switch_mode}", getMassageModeName().get(switch_mode));
                    } else {
                        str = str.replace("@{switch_mode}", switch_mode);
                    }
                } else {
                    if (massageMode.contains(switch_mode)) {
                        str = str.replace("@{seat_mr_mode}", getMassageModeName().get(switch_mode));
                    } else {
                        str = str.replace("@{seat_mr_mode}", switch_mode);
                    }
                }
                break;
            case "level":
                String level = (String) getValueInContext(map, "level");
                str = str.replace("@{level}", level);
                break;
            case "seat_mem_mode":
            case "seat_mem":
                String seat_mem_mode = (String) getValueInContext(map, "seat_mem_mode");
                if (key.contains("seat_mem_mode")) {
                    str = str.replace("@{seat_mem_mode}", getSeatPositionName().get(seat_mem_mode));
                } else {
                    str = str.replace("@{seat_mem}", getSeatPositionName().get(seat_mem_mode));
                }
                break;
            case "seat_heat_num":
            case "seat_aera_num":
            case "seat_mr_num":
            case "number_level":
            case "seat_number":
                int seatNumber = 0;
                if (map.containsKey(DCContext.SEAT_NUMBER)) {
                    seatNumber = (int) getValueInContext(map, DCContext.SEAT_NUMBER);
                }
                switch (key) {
                    case "seat_heat_num":
                        if (seatNumber == 0) {
                            str = str.replace("@{seat_heat_num}", getNumberLevel(map));
                        } else {
                            str = str.replace("@{seat_heat_num}", seatNumber + "");
                        }
                        break;
                    case "seat_aera_num":
                        if (seatNumber == 0) {
                            str = str.replace("@{seat_aera_num}", getNumberLevel(map));
                        } else {
                            str = str.replace("@{seat_aera_num}", seatNumber + "");
                        }
                        break;
                    case "seat_mr_num":
                        if (seatNumber == 0) {
                            str = str.replace("@{seat_mr_num}", getNumberLevel(map));
                        } else {
                            str = str.replace("@{seat_mr_num}", seatNumber + "");
                        }
                        break;
                    case "seat_number":
                        if (seatNumber == 0) {
                            str = str.replace("@{seat_number}", getNumberLevel(map));
                        } else {
                            str = str.replace("@{seat_number}", seatNumber + "");
                        }
                        break;
                    default:
                        if (seatNumber == 0) {
                            str = str.replace("@{number_level}", getNumberLevel(map));
                        } else {
                            str = str.replace("@{number_level}", seatNumber + "");
                        }
                        break;
                }
                break;
            case "app_name":
                String appName = "儿童座椅";
                str = str.replace("@{app_name}", appName);
                break;
            case "seat_page":
                str = str.replace("@{seat_page}", getSeatPage(map));
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.e(TAG, "tts :" + start + " " + end + " " + key + " " + index);

        LogUtils.e(TAG, "tts :" + str);
        return str;
    }

    private String getNumberLevel(HashMap<String, Object> map) {
        String number_level = (String) getValueInContext(map, "number_level");
        String seatLevel = (String) getValueInContext(map, "level");
        if (seatLevel != null) {
            switch (seatLevel) {
                case "low":
                    number_level = "低";
                    break;
                case "mid":
                    number_level = "中";
                    break;
                case "high":
                    number_level = "高";
                    break;
            }
        }
        return number_level;
    }

    private String getSeatPage(HashMap<String, Object> map) {
        String str = "座椅";
        int orderPage = orderSeatPage(map);
        if (isH56DCar(map) || isH37BCar(map)) {
            switch (orderPage) {
                case 0:
                    str = "座椅通风加热";
                    break;
                case 1:
                    str = "座椅按摩";
                    break;
                case 2:
                    str = "座椅舒享";
                    break;
                case 3:
                    str = "座椅前排调节";
                    break;
                case 4:
                    str = "座椅二排调节";
                    break;
                case 5:
                    str = "座椅三排调节";
                    break;
            }
        }
        LogUtils.e(TAG, "getSeatPage :" + str);
        return str;
    }

    /** ----------------------- 座椅加热 start--------------------------------------------------*/

    /**
     * 判断座椅加热是否是全开且都为3档
     *
     * @param map 指令的座椅数参数数据
     * @return true：是全开且都为3档  false：不是全开且都为3档
     */
    public boolean isOpenAndThreeGear(HashMap<String, Object> map) {
        boolean isMax = true;
        boolean isOpen = true;
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            isOpen = (operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position) > 0) && isOpen;
            // 56c,56d均支持三排座椅加热调节，但是56c不支持挡位调节，56D支持
            isMax = (operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position) == (position > 3 ? (isH56CCar(map) ? 1 : 3) : 3)) && isMax;
        }
        LogUtils.e(TAG, "isOpen :" + isOpen + "---" + "isMax :" + isMax);
        return isOpen && isMax;
    }

    /**
     * 判断座椅加热指令位置全是关闭的
     *
     * @param map 指令的座椅数参数数据
     * @return true：是  false：不全是
     */
    public boolean isAllClose(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            if (operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断座椅加热指令位置全是打开的
     *
     * @param map 指令的座椅数参数数据
     * @return true：是  false：不全是
     */
    public boolean isAllOpen(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            if (operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断座椅加热指令位置有关闭的
     *
     * @param map 指令的座椅数参数数据
     * @return true：有关闭的  false：没有关闭的
     */
    public boolean isHasClose(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            if (operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否有中低挡位
     *
     * @param map 指令的座椅数参数数据
     * @return true：有中低挡位  false：没有中低挡位
     */
    public boolean isHasLowGear(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            int num = position > 3 ? ((isH56CCar(map) ? 1 : 3)) : 3;
            if (operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position) != num) {
                //有不是最高挡的
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否全为低档位
     *
     * @param map 指令的座椅数参数数据
     * @return true：全是低挡位  false：不全是低挡位
     */
    public boolean isHasAllLowGear(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            if (operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position) != 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断当前挡位是否都为X挡
     *
     * @param map 指令的座椅数参数数据
     * @return true：输入的挡位有效  false：输入的挡位无效
     */
    public boolean isCurNumLevel(HashMap<String, Object> map) {
        if (isNumLevelValid(map)) {
            LogUtils.e(TAG, "isCurNumLevel valid");
            List<Integer> positions = getMethodPosition(map);
            int expectNumLevel = (int) getNumber(map);
            for (Integer position : positions) {
                int intHeat = operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position);
                if (intHeat != expectNumLevel) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 将对应座椅加热的挡位调到X档,高中低/321挡。会优先执行levelToNumber方法，将高中低档，切换为321挡
     * adjust_type:set
     * number_level:1,2,3
     *
     * @param map 指令的座椅数参数数据
     */
    public void setSeatHeatNumLevel(HashMap<String, Object> map) {
        float num = getNumber(map);
        LogUtils.e(TAG, "setSeatHeatNumLevel :" + num);
        if (isNumLevelValid(map)) {
            LogUtils.e(TAG, "setSeatHeatNumLevel valid");
            map.put(DCContext.SEAT_NUMBER, (int) num);
            List<Integer> positions = getMethodPosition(map);
            for (Integer position : positions) {
                operator.setIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position, (int) num);
            }
        }
    }

    /**
     * 指定座椅加热打开，非3挡时
     * 在现有挡位的基础上+1挡
     * adjust_type:increase
     *
     * @param map 指令的座椅数参数数据
     */
    public void increaseSeatHeat(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            boolean isOpen = operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position) > 0;
            int heatLevel = operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position);
            int level = position > 3 ? ((isH56CCar(map) ? 1 : 3)) : 3;
            if (isOpen && heatLevel < level) {
                int num = Math.min(heatLevel + 1, 3);
                map.put(DCContext.SEAT_NUMBER, num);
                operator.setIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position, num);
            }
        }
    }

    /**
     * 指定座椅加热打开，非1挡时
     * 在现有挡位的基础上-1挡
     * adjust_type:decrease
     *
     * @param map 指令的座椅数参数数据
     */
    public void decreaseSeatHeat(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            boolean isOpen = operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position) > 0;
            int heatLevel = operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position);
            if (isOpen && heatLevel > 1) {
                int num = Math.max(heatLevel - 1, 1);
                if (position > 3 && isH56CCar(map)) {
                    num = 0;
                }
                map.put(DCContext.SEAT_NUMBER, num);
                operator.setIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position, num);
            }
        }
    }

    public boolean isDealThirdPosition(HashMap<String, Object> map) {
        // 只有56C和56D车型处理三排
        if (isH56DCar(map)) {
            return false;
        }
        String position = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(position)) {
            return position.equals("third_row_left") || position.equals("third_row_right");
        }
        return false;
    }

    /**
     * 指定座椅加热调到最低,将挡位调低到1档
     * adjust_type:set
     * level:min
     *
     * @param map 指令的座椅数参数数据
     */
    public void setSeatHeat2Low(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            operator.setIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position, 1);
        }
    }

    /**
     * 不管是否指令全车，打开座椅加热均会执行该方法
     * 关闭状态时,打开对应音区座椅位置的加热功能，默认打开为3挡
     * 非3挡打开状态时,直接调高对应音区座椅位置的加热功能到3挡
     * <p>
     * 执行1:打开座椅加热，不指定任何挡位
     * switch_type:open
     * <p>
     * 执行2：调高座椅加热到最高
     * adjust_type:set
     * level:max
     *
     * @param map 指令的座椅数参数数据
     */
    public void openAndThreeGear(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            operator.setIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position, 3);
        }
    }

    /**
     * 关闭座椅加热
     * switch_type:close
     *
     * @param map 指令的座椅数参数数据
     */
    public void closeSeatHeat(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            //需要不是0的时候才设置关闭
            if (operator.getIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position) != 0) {
                operator.setIntProp(VCarSeatSignal.SEAT_HEAT_CUR_LEVEL, position, 0);
            }

        }
    }

    /** ----------------------- 座椅加热 end--------------------------------------------------*/

    /**
     * ----------------------- 座椅通风 start------------------------------------------------------
     */

    /**
     * 获取执行的座椅位置
     */
    public int getPositionSize(HashMap<String, Object> map) {
        int positions = getCurPositionSize(map);
        LogUtils.e(TAG, "getPositionSize :" + positions);
        return positions;
    }

    /**
     * 指令位置座椅通风是否都关闭
     *
     * @param map 指令的座椅数参数数据
     * @return true: 全都关闭 false: 不是全都关闭
     */
    public boolean isAllCloseSeatWind(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            boolean isOpen = operator.getIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position) > 0;
            if (isOpen) {
                return false;
            }
        }
        return true;
    }

    /**
     * 指令位置座椅通风是否都打开
     *
     * @param map 指令的座椅数参数数据
     * @return true: 全都打开 false: 不是全都打开
     */
    public boolean isAllOpenSeatWind(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            boolean isOpen = operator.getIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position) > 0;
            if (!isOpen) {
                return false;
            }
        }
        return true;
    }

    /**
     * 指令位置座椅通风是否都是三档
     *
     * @param map 指令的座椅数参数数据
     * @return true: 都是三档 false: 不全是三档
     */
    public boolean isAllOpenSeatWindAndMax(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);

        for (Integer position : positions) {
            int curLevel = operator.getIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position);
            if (curLevel != 3) {
                return false;
            }
        }
        return true;
    }

    /**
     * 指令位置座椅通风是否都是一档
     *
     * @param map 指令的座椅数参数数据
     * @return true: 都是一档 false: 不全是一档
     */
    public boolean isAllOpenSeatWindAndMin(HashMap<String, Object> map) {
        boolean isAllOpenSeatWindAndMin = true;
        List<Integer> positions = getMethodPosition(map);

        for (Integer position : positions) {
            int curLevel = operator.getIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position);
            if (curLevel != 1) {
                return false;
            }
        }
        return true;
    }

    public void decreaseSeatWind(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        LogUtils.e(TAG, "decreaseSeatWind");
        for (Integer position : positions) {
            int intVent = operator.getIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position);
            if (intVent > ICommon.Level.LOW && intVent <= ICommon.Level.HIGH) {
                int num = Math.max(intVent - 1, ICommon.Level.LOW);
                map.put(DCContext.SEAT_NUMBER, num);
                operator.setIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position, num);
            }
        }
    }

    /**
     * 指定座椅通风调到最低,将挡位调低到1档
     * adjust_type:set
     * level:min
     *
     * @param map 指令的座椅数参数数据
     */
    public void setSeatWind2Low(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            operator.setIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position, 1);
        }
    }

    /**
     * 不管是否指令全车，打开座椅通风均会执行该方法
     * 关闭状态时,打开对应音区座椅位置的通风功能，默认打开为3挡
     * 非3挡打开状态时,直接调高对应音区座椅位置的通风功能到3挡
     * <p>
     * 执行1:打开座椅通风，不指定任何挡位
     * switch_type:open
     * <p>
     * 执行2：调高座椅通风到最高
     * adjust_type:set
     * level:max
     *
     * @param map 指令的座椅数参数数据
     */
    public void openSeatWindAndMax(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);

        for (Integer position : positions) {
            operator.setIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position, 3);
        }
    }

    /**
     * 关闭座椅通风
     * switch_type:close
     *
     * @param map 指令的座椅数参数数据
     */
    public void closeSeatWind(HashMap<String, Object> map) {
        LogUtils.e(TAG, "closeSeatWind");
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            operator.setIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position, 0);
        }
    }

    /**
     * 将对应座椅通风的挡位调到X档,高中低/321挡。会优先执行levelToNumber方法，将高中低档，切换为321挡
     * adjust_type:set
     * number_level:1,2,3
     *
     * @param map 指令的座椅数参数数据
     */
    public void setSeatWindNumLevel(HashMap<String, Object> map) {
        float numberLevel = getNumber(map);
        LogUtils.e(TAG, "setSeatWindNumLevel: " + numberLevel);
        if (isNumLevelValid(map)) {
            LogUtils.e(TAG, "setSeatWindNumLevel valid");
            map.put(DCContext.SEAT_NUMBER, (int) numberLevel);
            List<Integer> positions = getMethodPosition(map);
            for (Integer position : positions) {
                operator.setIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position, (int) numberLevel);
            }
        }
    }

    /**
     * 指定座椅通风打开，非3挡时
     * 在现有挡位的基础上+1挡
     * adjust_type:increase
     *
     * @param map 指令的座椅数参数数据
     */
    public void increaseSeatWind(HashMap<String, Object> map) {
        LogUtils.e(TAG, "increaseSeatWind");
        List<Integer> positions = getMethodPosition(map);

        for (Integer position : positions) {
            int curLevel = operator.getIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position);

            if (curLevel > 0 && curLevel < 3) {
                int num = Math.min(curLevel + 1, 3);
                map.put(DCContext.SEAT_NUMBER, num);
                operator.setIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position, num);
            }
        }
    }

    /**
     * 指定座椅通风打开，非1挡时
     * 在现有挡位的基础上-1挡
     * adjust_type:decrease
     *
     * @param map 指令的座椅数参数数据
     */
    public void decreaseSeatVent(HashMap<String, Object> map) {
        LogUtils.e(TAG, "decreaseSeatVent");
        List<Integer> positions = getMethodPosition(map);

        for (Integer position : positions) {
            int curLevel = operator.getIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position);
            if (curLevel > 1) {
                int num = Math.max(curLevel - 1, 1);
                map.put(DCContext.SEAT_NUMBER, num);
                operator.setIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position, num);
            }
        }
    }

    /**
     * 判断当前挡位是否都为X挡
     *
     * @param map 指令的座椅数参数数据
     * @return true：输入的挡位有效  false：输入的挡位无效
     */
    public boolean isWindCurNumLevel(HashMap<String, Object> map) {
        boolean isCurNumLevel = true;
        if (isNumLevelValid(map)) {
            LogUtils.e(TAG, "isWindCurNumLevel valid");
            List<Integer> positions = getMethodPosition(map);
            int expectLevel = (int) getNumber(map);
            for (Integer position : positions) {
                int curLevel = operator.getIntProp(VCarSeatSignal.SEAT_VENT_CUR_LEVEL, position);
                if (curLevel != expectLevel) {
                    return false;
                }
            }
        }
        return true;
    }

    /** ----------------------- 座椅通风 end------------------------------------------------------*/

    /** ----------------------- 座椅按摩 start------------------------------------------------------*/

    public boolean isSupportLevel(HashMap<String, Object> map) {
        int level = Integer.parseInt((String) getValueInContext(map, "number_level"));
        return level > 0 && level < 4;
    }

    public boolean isMassageModeMatching(HashMap<String, Object> map) {
        boolean isMassageModeMatching = false;
        String massageMode = (String) getValueInContext(map, "switch_mode");
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            String strProp = operator.getStringProp(VCarSeatSignal.MASSAGE_MODE, position);
            if (massageMode.equalsIgnoreCase(strProp)) {
                isMassageModeMatching = true;
            }
        }
        return isMassageModeMatching;
    }

    public void openedSet3LevelAndSetMode(HashMap<String, Object> map) {
        closeSet3Level(map);
        if (map.containsKey("switch_mode")) {
            setSeatMassageMode(map);
        } else {
            setChangeNext(map);
        }
    }

    public void allSet3LevelAndSetMode(HashMap<String, Object> map) {
        //按摩座椅的设置为3挡
        openSeatMassageAndSet3Level(map);
        //设置到指定按摩模式
        setSeatMassageMode(map);
    }

    public void setSeatMassageMode(HashMap<String, Object> map) {
        String massageMode = (String) getValueInContext(map, "switch_mode");
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            operator.setStringProp(VCarSeatSignal.MASSAGE_MODE, position, massageMode);
        }
    }

    public void setChangeNext(HashMap<String, Object> map) {
        List<Integer> executePositions = getMethodPosition(map);
        for (Integer position : executePositions) {
            if (operator.getIntProp(VCarSeatSignal.MASSAGE_CUR_LEVEL, position) > 0) {
                String strProp = operator.getStringProp(VCarSeatSignal.MASSAGE_MODE, position);
                String mode = seatInterface.getNextMassageMode(position,strProp);
                operator.setStringProp(VCarSeatSignal.MASSAGE_MODE, position, mode);
                if (executePositions.size() == 1) {
                    map.put("switch_mode", getMassageModeName().get(mode));
                } else {
                    map.put("switch_mode", "下一个");
                }
            }
        }
    }

    public boolean isSupportMassageMode(HashMap<String, Object> map) {
        String massageMode = (String) getValueInContext(map, "switch_mode");
        List<Integer> positions = getMethodPosition(map);
        return seatInterface.isSupportMassageMode(positions, massageMode);
    }

    public boolean isSeatMassageModeNewSpeakType(HashMap<String, Object> map) {
        return seatInterface.isSeatMassageModeNewSpeakType();
    }

    public List<String> getMassageMode(HashMap<String, Object> map) {
        return Arrays.asList(seatInterface.getSeatMassageSupportMode());
    }

    public Map<String, String> getMassageModeName() {
        return seatInterface.getModeName();
    }

    public boolean isOpenSeatMassage(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            if (operator.getIntProp(VCarSeatSignal.MASSAGE_CUR_LEVEL, position) == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isCloseSeatMassage(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            if (operator.getIntProp(VCarSeatSignal.MASSAGE_CUR_LEVEL, position) > 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isSeatMassageTargetLevel(HashMap<String, Object> map) {
        boolean isTargetLevel = true;
        int target_level = Integer.parseInt((String) getValueInContext(map, "target_level"));
        if (target_level == -1) {
            target_level = Integer.parseInt((String) getValueInContext(map, "number_level"));
        }
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            if (operator.getIntProp(VCarSeatSignal.MASSAGE_CUR_LEVEL, position) != target_level) {
                isTargetLevel = false;
            }
        }
        return isTargetLevel;
    }

    public void setSeatMassageStatus(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        if (switchType.equals("open")) {
            openSeatMassageAndSet3Level(map);
        } else if (switchType.equals("close")) {
            closeSeatMassage(map);
        }
    }

    public void openSeatMassageAndSet3Level(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            operator.setIntProp(VCarSeatSignal.MASSAGE_CUR_SWITCH, position, 2);
            operator.setIntProp(VCarSeatSignal.MASSAGE_CUR_LEVEL, position, 3);
        }
    }

    public void closeSeatMassage(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            operator.setIntProp(VCarSeatSignal.MASSAGE_CUR_SWITCH, position, 1);
            operator.setIntProp(VCarSeatSignal.MASSAGE_CUR_LEVEL, position, 0);
        }
    }

    public void setSeatMassageAdjust(HashMap<String, Object> map) {
        String adjustType = (String) getValueInContext(map, "adjust_type");
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            int intMassage = operator.getIntProp(VCarSeatSignal.MASSAGE_CUR_LEVEL, position);
            int setLevel = intMassage;
            if (adjustType.equals("increase")) {
                //调高一挡
                setLevel = Math.min(intMassage + 1, 3);
                //作为tts回复使用
                map.put(DCContext.SEAT_NUMBER, setLevel);
            } else if (adjustType.equals("decrease")) {
                //调低一挡
                setLevel = Math.max(intMassage - 1, 1);
                //作为tts回复使用
                map.put(DCContext.SEAT_NUMBER, setLevel);
            } else if (adjustType.equals("set")) {
                String number_level = (String) getValueInContext(map, "number_level");
                if (number_level != null) {
                    //指定number_level
                    setLevel = Integer.parseInt(number_level);
                } else {
                    String level = (String) getValueInContext(map, "level");
                    if (level != null) {
                        if (level.equals("max")) {
                            //最高
                            setLevel = 3;
                        } else if (level.equals("min")) {
                            //最低
                            setLevel = 1;
                        }
                    }
                }
            }
            operator.setIntProp(VCarSeatSignal.MASSAGE_CUR_SWITCH, position, 2);
            operator.setIntProp(VCarSeatSignal.MASSAGE_CUR_LEVEL, position, setLevel);
        }
    }

    public void openedSet1Level(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            int level = operator.getIntProp(VCarSeatSignal.MASSAGE_CUR_LEVEL, position);
            if (level > 0) {
                operator.setIntProp(VCarSeatSignal.MASSAGE_CUR_LEVEL, position, Math.max(level - 1, 1));
            }
        }
    }

    public void closeSet3Level(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            if (operator.getIntProp(VCarSeatSignal.MASSAGE_CUR_LEVEL, position) == 0) {
                operator.setIntProp(VCarSeatSignal.MASSAGE_CUR_SWITCH, position, 2);
                operator.setIntProp(VCarSeatSignal.MASSAGE_CUR_LEVEL, position, 3);
            }
        }
    }

    /** ----------------------- 座椅按摩 end------------------------------------------------------*/

    /** ----------------------- 座椅位置调节/靠背位置调节/坐垫位置调节 start-----------------------------*/

    /**
     * 是否是停止移动的二次交互
     */
    public boolean isStopMove(HashMap<String, Object> map) {
        if (map.containsKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE) && map.containsKey("choose_type")) {
            String scene = (String) getValueInContext(map,
                    FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE);
            return scene.equals("State.DC_OP_STOP_CONFIRM");
        } else {
            return false;
        }
    }

    /**
     * 座椅停止移动
     */
    public void seatStopMove(HashMap<String, Object> map) throws JSONException {
        List<Integer> positions = getMethodPosition(map);
        operator.setBooleanProp(VCarSeatSignal.SEAT_ADJUST_STOP_MOVE, positions.get(0), true);
    }

    /**
     * 座椅移动提醒
     */
    public boolean isSeatMovementReminder(HashMap<String, Object> map) {
        LogUtils.i(TAG, "isSeatMovementReminder");
        return keyContextInMap(map, "choose_type");
    }

    /**
     * 是取消，还是确认
     */
    public boolean isCancelConfirm(HashMap<String, Object> map) {
        String chooseType = (String) getValueInContext(map, "choose_type");
        LogUtils.i(TAG, "isCancelConfirm chooseType is " + chooseType);
        return StringUtils.equals(chooseType, "confirm");
    }

    /**
     * 处理位置 == 声源位置
     *
     * @param map 指令的座椅数参数数据
     */
    public boolean isPositionEqualSoundPositions(HashMap<String, Object> map) {
        ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
        ArrayList<Integer> positions = (ArrayList<Integer>) getPosition(map);
        return curSoundPositions.get(0) == positions.get(0);
    }

    /**
     * 是否在范围内
     *
     * @return true:在范围内   false:不在范围内
     */
    public boolean isInScope(HashMap<String, Object> map) {
        String direction = (String) getValueInContext(map, "direction");
        String adjust_type = (String) getValueInContext(map, "adjust_type");
        String nlu_info = (String) getValueInContext(map, DCContext.NLU_INFO);
        LogUtils.d(TAG, "isInScope direction : " + direction + " adjust_type : " + adjust_type + " nlu_info : " + nlu_info);
        List<Integer> positions = getMethodPosition(map);
        String seat_type = VCarSeatSignal.SEAT_ADJUST_POSITION;
        if (nlu_info.contains("seat_place") && (direction.equals("forward") || direction.equals("backward"))) {
            seat_type = VCarSeatSignal.SEAT_ADJUST_POSITION;
        } else if (nlu_info.contains("seat_backrest")) {
            seat_type = VCarSeatSignal.SEAT_ADJUST_BACK;
        } else if (nlu_info.contains("seat_cushion_height") || nlu_info.contains("seat_cushion_place") ||
                (nlu_info.contains("seat_place") && (direction.equals("upward") || direction.equals("downward")))) {
            seat_type = VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT;
        }
        int curSeatNumber = operator.getIntProp(seat_type, positions.get(0));
        /*if (seat_type.equalsIgnoreCase(VCarSignal.SEAT_ADJUST_CUSHION_HEIGHT)) {
            curSeatNumber = curSeatNumber & 0xff;
        }*/ //移到底层了，上层保持统一
        LogUtils.d(TAG, "isInScope seat_type : " + seat_type + " curSeatNumber : " + curSeatNumber);
        if (seat_type.equals(VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT)) {
            if (map.containsKey("direction")) {
                if (direction.equals("downward") || direction.equals("forward")) {
                    return curSeatNumber >= 6 && curSeatNumber <= 100;
                } else if (direction.equals("upward") || direction.equals("backward")) {
                    return curSeatNumber >= 0 && curSeatNumber <= 94;
                }
            }
        } else if (seat_type.equals(VCarSeatSignal.SEAT_ADJUST_POSITION)) {
            if (map.containsKey("direction")) {
                if (direction.equals("downward") || direction.equals("backward")) {
                    return curSeatNumber >= 6 && curSeatNumber <= 100;
                } else if (direction.equals("upward") || direction.equals("forward")) {
                    return curSeatNumber >= 0 && curSeatNumber <= 94;
                }
            }
        } else {
            if (map.containsKey("direction")) {
                if (direction.equals("forward")) {
                    return curSeatNumber >= 6 && curSeatNumber <= 100;
                } else if (direction.equals("backward")) {
                    return curSeatNumber >= 0 && curSeatNumber <= 55;
                }
            }
        }

        if (map.containsKey("adjust_type")) {
            switch (adjust_type) {
                case "decrease":
                    return curSeatNumber >= 6 && curSeatNumber <= 100;
                case "increase":
                    return curSeatNumber >= 0 && curSeatNumber <= 94;
                case "set":
                    String level = (String) getValueInContext(map, "level");
                    if (level.equals("max")) {
                        return curSeatNumber >= 0 && curSeatNumber <= 94;
                    } else if (level.equals("min")) {
                        return curSeatNumber >= 6 && curSeatNumber <= 100;
                    }
                    break;
            }
        }
        return false;
    }

    /**
     * 执行调节 座椅/座椅靠背/坐垫
     */
    public void seatPositionAdjust(HashMap<String, Object> map) throws JSONException {
        String direction = (String) getValueInContext(map, "direction");
        String adjust_type = (String) getValueInContext(map, "adjust_type");
        String nlu_info = (String) getValueInContext(map, DCContext.NLU_INFO);
        String level = (String) getValueInContext(map, "level");
        LogUtils.d(TAG, "seatPositionAdjust direction : " + direction + " adjust_type : " + adjust_type + " nlu_info : " + nlu_info + " level : " + level);
        List<Integer> positions = getMethodPosition(map);
        //ArrayList<Integer> analysisPosition = analysisPosition(positions);
        //座椅调节位置调节:Comforts.ID_SEAT_POSITION = 50331687
        //座椅靠背调节位置调节:Comforts.ID_SEAT_BACK = 50331688
        //座椅坐垫调节位置调节:Comforts.ID_SEAT_CUSHION = 50331689
        String seat_type = VCarSeatSignal.SEAT_ADJUST_POSITION;
        int set_number = -1;
        //调节的单位(座椅调节位置调节一次调节7%，靠背是3%，坐垫是13%)
        int set_unit = -1;
        if (nlu_info.contains("seat_place") && (direction.equals("forward") || direction.equals("backward"))) {
            seat_type = VCarSeatSignal.SEAT_ADJUST_POSITION;
            set_unit = 7;
        } else if (nlu_info.contains("seat_backrest")) {
            seat_type = VCarSeatSignal.SEAT_ADJUST_BACK;
            set_unit = 4;
        } else if (nlu_info.contains("seat_cushion_height") || nlu_info.contains("seat_cushion_place") ||
                (nlu_info.contains("seat_place") && (direction.equals("upward") || direction.equals("downward")))) {
            seat_type = VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT;
            set_unit = 13;
        }
        int curSeatNumber = operator.getIntProp(seat_type, positions.get(0));
        LogUtils.d(TAG, "seatPositionAdjust seat_type : " + seat_type + " set_unit : " + set_unit + " curSeatNumber : " + curSeatNumber);
        boolean backOrCushion = VCarSeatSignal.SEAT_ADJUST_BACK.equalsIgnoreCase(seat_type)
                || VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT.equalsIgnoreCase(seat_type);

        if (map.containsKey("direction")) {
            if (backOrCushion) {
                if (direction.equals("forward")) {
                    set_number = Math.max(0, curSeatNumber - set_unit);
                } else if (direction.equals("backward")) {
                    set_number = Math.min(100, curSeatNumber + set_unit);
                } else if (direction.equals("upward")) {
                    set_number = Math.min(100, curSeatNumber + set_unit);
                } else if (direction.equals("downward")) {
                    set_number = Math.max(0, curSeatNumber - set_unit);
                }
            } else {
                switch (direction) {
                    case "forward":
                        set_number = Math.min(100, curSeatNumber + set_unit);
                        break;
                    case "backward":
                        set_number = Math.max(0, curSeatNumber - set_unit);
                        break;
                    case "upward":
                        set_number = Math.min(100, curSeatNumber + set_unit);
                        break;
                    case "downward":
                        set_number = Math.max(0, curSeatNumber - set_unit);
                        break;
                }
            }
        }
        if (map.containsKey("adjust_type")) {
            if (adjust_type.equals("increase")) {
                set_number = Math.min(100, curSeatNumber + set_unit);
            } else if (adjust_type.equals("decrease")) {
                set_number = Math.max(0, curSeatNumber - set_unit);
            }
        }
        if (map.containsKey("level")) {
            //level != null 就代表是需要调节到max或者min
            if (level.equals("max")) {
                if (map.containsKey("direction")) {
                    if (VCarSeatSignal.SEAT_ADJUST_CUSHION_HEIGHT.equals(seat_type)) {
                        if (direction.equals("backward") || direction.equals("upward")) {
                            set_number = 100;
                        } else if (direction.equals("forward") || direction.equals("downward")) {
                            set_number = 0;
                        }
                    } else if (VCarSeatSignal.SEAT_ADJUST_POSITION.equals(seat_type)) {
                        if (direction.equals("backward") || direction.equals("upward")) {
                            set_number = 0;
                        } else if (direction.equals("forward") || direction.equals("downward")) {
                            set_number = 100;
                        }
                    } else {
                        if (direction.equals("backward")) {
                            set_number = 57;
                        } else if (direction.equals("forward")) {
                            set_number = 0;
                        }
                    }
                } else {
                    if (map.containsKey("adjust_type")) {
                        set_number = 100;
                    }
                }
            } else if (level.equals("min")) {
                set_number = 0;
            }
        }
        LogUtils.d(TAG, "seatPositionAdjust set_number : " + set_number);
        //暂时只有座椅坐垫高度调节有最高，最低的调节
        operator.setIntProp(seat_type, positions.get(0), set_number);
    }

    /**
     * 指定座位是否已经占座
     *
     * @param map 指令的座椅数参数数据，此场景只涉及指令全车
     * @return true：有  false：没有
     */
    public boolean isUnUsedThisPosition(HashMap<String, Object> map) {
        String direction = (String) getValueInContext(map, "direction");
        List<Integer> positions = getMethodPosition(map);
        Integer integer = positions.get(0);
        if (direction.equals("backward")) {
            //如果操作是往后，需要判断前排位置指定的后排是否有占座（主驾 -> 后左 ; 副驾 -> 后右），有占座需要二次交互
            integer = integer + 2;
        }
        if (integer == 0) {
            //主驾默认不判断占位
            return true;
        }
        return operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, integer);
    }

    /** ----------------------- 座椅位置调节/靠背位置调节/坐垫位置调节 end-----------------------------*/

    /** ----------------------- 座椅记忆位置/激活位置 start----------------------------------------*/

    public boolean isSpecifyPosition(HashMap<String, Object> map) {
        if (!map.containsKey("seat_mem_mode")) {
            map.put("seat_position", 0);
            return false;
        }
        return true;
    }

    public boolean isSupportRearSideSeatMemory(HashMap<String, Object> map) {
        return seatInterface.isSupportRearSideSeatMemory();
    }

    public boolean isSpeak37TssText(HashMap<String, Object> map) {
        return seatInterface.isSpeak37TssText();
    }

    public boolean isSpeak56CTssText(HashMap<String, Object> map) {
        return seatInterface.isSpeak56CTssText();
    }

    public boolean isSpeak56DTssText(HashMap<String, Object> map) {
        return seatInterface.isSpeak56DTssText();
    }

    public void setPositionStopMove(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        operator.setIntProp(VCarSeatSignal.SEAT_POSITION_STOP_MOVE, positions.get(0), 0);
    }

    public void setFirstRowLeftPosition(HashMap<String, Object> map) {
        seatInterface.setFirstRowLeftPosition(map);
    }

    public void setFirstRowRightPosition(HashMap<String, Object> map) {
        seatInterface.setFirstRowRightPosition(map);
    }

    public void setRearSidePosition(HashMap<String, Object> map) {
        seatInterface.setRearSidePosition(map);
    }

    public void openSeatSettingUI(HashMap<String, Object> map) {
        ArrayList<Integer> positions = (ArrayList<Integer>) getPosition(map);
        operator.setBooleanProp(VCarSeatSignal.SEAT_SAVE_PAGE_SATE, positions.get(0), true);
    }

    public boolean isDriverSupportPosition(HashMap<String, Object> map) {
        boolean isDriverSupportPosition = false;
        String seat_mem_mode = (String) getValueInContext(map, "seat_mem_mode");
        String[] modes = seatInterface.getDriverSupportPosition();
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equals(seat_mem_mode)) {
                map.put("seat_position", i);
                isDriverSupportPosition = true;
            }
        }
        return isDriverSupportPosition;
    }

    public boolean isPassengerSupportPosition(HashMap<String, Object> map) {
        boolean isPassengerSupportPosition = false;
        String seat_mem_mode = (String) getValueInContext(map, "seat_mem_mode");
        String[] modes = seatInterface.getPassengerSupportPosition();
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equals(seat_mem_mode)) {
                map.put("seat_position", i);
                isPassengerSupportPosition = true;
            }
        }
        return isPassengerSupportPosition;
    }

    public boolean isRearSideSupportPosition(HashMap<String, Object> map) {
        boolean isRearSideSupportPosition = false;
        String seat_mem_mode = (String) getValueInContext(map, "seat_mem_mode");
        String[] modes = seatInterface.getRearSideSupportPosition();
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equals(seat_mem_mode)) {
                map.put("seat_position", i);
                isRearSideSupportPosition = true;
            }
        }
        return isRearSideSupportPosition;
    }

    public void setRearviewMirrorPosition(HashMap<String, Object> map) {
        int seat_position = (int) getValueInContext(map, "seat_position");
        operator.setIntProp(VCarSeatSignal.SEAT_MIRROR_SAVE, 0, seat_position);
    }

    public void saveSeatPosition(HashMap<String, Object> map) {
        int seat_position = (int) getValueInContext(map, "seat_position");
        List<Integer> positions = getMethodPosition(map);
        operator.setIntProp(VCarSeatSignal.SEAT_MEMORY_SAVE, positions.get(0), seat_position);
    }

    public boolean isSavePosition(HashMap<String, Object> map) {
        if ((isH56CCar(map) || isH56DCar(map))) {
            return true;
        }
        boolean isSavePosition = true;
        int seat_position = (int) getValueInContext(map, "seat_position");
        List<Integer> positions = getMethodPosition(map);
        String info = operator.getStringProp(VCarSeatSignal.SEAT_MEMORY_READ, positions.get(0));
        if (info != null && !info.isEmpty()) {
            LogUtils.e(SeatControlImpl.TAG, "isSavePosition json: " + info);
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(info).getAsJsonArray();
            if (array != null && array.size() >= 3) {
                JsonObject asJsonObject = array.get(seat_position).getAsJsonObject();
                int seatHorizontalPos = asJsonObject.get("seatAdjustPosition").getAsInt();
                int seatBacPos = asJsonObject.get("seatAdjustBack").getAsInt();
                int seatCushionAnglePos = asJsonObject.get("seatAdjustCushionAngle").getAsInt();
                int seatHeightPos = asJsonObject.get("seatAdjustCushionHeight").getAsInt();
                if ((seatHorizontalPos == 0 && seatBacPos == 0 && seatCushionAnglePos == 0 && seatHeightPos == 0)
                        || (seatHorizontalPos == 255 && seatBacPos == 255 && seatCushionAnglePos == 255 && seatHeightPos == 255)
                        || (seatHorizontalPos == -1 && seatBacPos == -1 && seatCushionAnglePos == -1 && seatHeightPos == -1)) {
                    isSavePosition = false;
                }
            }
        }
        return isSavePosition;
    }

    public void setSeatPosition(HashMap<String, Object> map) {
        int seat_position = (int) getValueInContext(map, "seat_position");
        List<Integer> positions = getMethodPosition(map);
        operator.setIntProp(VCarSeatSignal.SEAT_MEMORY_RECOVERY, positions.get(0), seat_position);
        //座椅位置调节，同步需要调整后视镜位置
        if (0 == positions.get(0)) {
            operator.setIntProp(VCarSeatSignal.SEAT_MIRROR_RECOVERY, positions.get(0), seat_position);
        }
    }

    public Map<String, String> getSeatPositionName() {
        return new HashMap<String, String>() {
            {
                put("driving_position", "驾驶位置");
                put("resting_position", "休息位置");
                put("other_position", "其他位置");
                put("watch_movies_position", "观影位置");
                put("usual_position", "常用位置");
                put("position_one", "位置1");
                put("position_two", "位置2");
                put("position_three", "位置3");
                put("habit_one", "习惯1");
                put("habit_two", "习惯2");
                put("habit_three", "习惯3");
                put("posture_one", "坐姿1");
                put("posture_two", "坐姿2");
                put("posture_three", "坐姿3");
            }
        };
    }

    /** ----------------------- 座椅记忆位置/激活位置 end----------------------------------------*/

    /**
     * ----------------------- 座椅复位 start--------------------------------------------------
     */

    public boolean curSoundPositionsIsReset(HashMap<String, Object> map) {
        int curSoundPosition = ((ArrayList<Integer>) curSoundPositions(map)).get(0);
        int position = ((ArrayList<Integer>) getPosition(map)).get(0);
        LogUtils.e(TAG, "curSoundPositionsIsReset curSoundPosition :" + curSoundPosition + "----position :" + position);
        if (curSoundPosition == position) {
            if (curSoundPosition != 1) {
                return true;
            }
        }
        return false;
    }

    public boolean isOnlyFirstRowRight(HashMap<String, Object> map) {
        ArrayList<Integer> positions = (ArrayList<Integer>) getPosition(map);
        if (positions.size() == 1) {
            if (positions.get(0) == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean isSupportReset(HashMap<String, Object> map) {
        ArrayList<Integer> positions = (ArrayList<Integer>) getPosition(map);
        for (Integer position : positions) {
            if (position > 3 || position < 1) {
                return false;
            }
        }
        return true;
    }

    public boolean isResetState(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        int value = operator.getIntProp(VCarSeatSignal.SEAT_ONEKEY_RESET, positions.get(0));
        return value == 2;
    }

    /**
     * 座椅复位（56支持副驾和二排）
     */
    public void resetSeat(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        for (Integer position : positions) {
            operator.setBooleanProp(VCarSeatSignal.SEAT_ONEKEY_RESET, position, true);
        }
    }

    /** ----------------------- 座椅复位 end--------------------------------------------------*/

    /** ----------------------- 儿童座椅 start--------------------------------------------------*/

    /**
     * 获取儿童座椅连接的数量，最大为两个儿童座椅
     *
     * @param map
     * @return
     */
    public int getChildSeatStatus(HashMap<String, Object> map) {
        LogUtils.e(TAG, "getChildSeatStatus");
        return operator.getIntProp(VCarSeatSignal.BABY_SEAT_COUNT, 0);
    }

    /**
     * 判断儿童座椅状态是否可正常执行
     *
     * @param map 指令的座椅数参数数据
     * @return true: 儿童座椅处于可正常执行状态 false: 处于不可正常执行状态
     */
    public boolean checkChildSeatStatus(HashMap<String, Object> map) {
        LogUtils.d(TAG, "checkChildSeatStatus");
        return operator.getBooleanProp(VCarSeatSignal.BABY_SEAT_STATE);
    }

    /**
     * 实际使用需要先检查儿童座椅状态是否正常,判断座椅加热是否关闭
     *
     * @param map 指令的座椅数参数数据
     * @return true: 儿童座椅加热关闭 false: 儿童座椅加热打开
     */
    public boolean isChildSeatHeatClose(HashMap<String, Object> map) {
        if (checkChildSeatStatus(map)) {
            return !operator.getBooleanProp(VCarSeatSignal.BABY_SEAT_HEAT_SWITCH, 0);
        }
        return true;
    }

    /**
     * 实际使用需要先检查儿童座椅状态是否正常,判断座椅通风是否关闭
     *
     * @param map 指令的座椅数参数数据
     * @return true: 儿童座椅通风关闭 false: 儿童座椅通风打开
     */
    public boolean isChildSeatWindClose(HashMap<String, Object> map) {
        if (checkChildSeatStatus(map)) {
            return !operator.getBooleanProp(VCarSeatSignal.BABY_SEAT_VENT_SWITCH, 0);
        }
        return true;
    }

    /**
     * 打开儿童座椅加热
     * switch_type:open
     *
     * @param map 指令的座椅数参数数据
     */
    public void openChildSeatHeat(HashMap<String, Object> map) {
        LogUtils.d(TAG, "openChildSeatHeat");
        operator.setBooleanProp(VCarSeatSignal.BABY_SEAT_HEAT_SWITCH, 0, true);
    }

    /**
     * 关闭儿童座椅加热
     * switch_type:close
     *
     * @param map 指令的座椅数参数数据
     */
    public void closeChildSeatHeat(HashMap<String, Object> map) {
        LogUtils.d(TAG, "closeChildSeatHeat");
        operator.setBooleanProp(VCarSeatSignal.BABY_SEAT_HEAT_SWITCH, 0, false);
    }

    /**
     * 打开儿童座椅通风
     * switch_type:open
     *
     * @param map 指令的座椅数参数数据
     */
    public void openChildSeatWind(HashMap<String, Object> map) {
        LogUtils.d(TAG, "openChildSeatWind");
        operator.setBooleanProp(VCarSeatSignal.BABY_SEAT_VENT_SWITCH, 0, true);
    }

    /**
     * 关闭儿童座椅通风
     * switch_type:close
     *
     * @param map 指令的座椅数参数数据
     */
    public void closeChildSeatWind(HashMap<String, Object> map) {
        LogUtils.d(TAG, "closeChildSeatWind");
        operator.setBooleanProp(VCarSeatSignal.BABY_SEAT_VENT_SWITCH, 0, false);
    }

    /** ----------------------- 儿童座椅 end--------------------------------------------------*/

    /**
     * ----------------------- 一键舒躺 & 一键零重力 start--------------------------------------------------
     */

    public void setSeatComfortablyStop(HashMap<String, Object> map) {
        operator.setIntProp(VCarSeatSignal.SEAT_COMFORTABLY_STOP_MOVE, getMethodPosition(map).get(0), 0);
    }

    public boolean isHasCurMode(HashMap<String, Object> map) {
        String nlu_info = (String) getValueInContext(map, DCContext.NLU_INFO);
        return seatInterface.isHasZeroGravityConfig(nlu_info);
    }

    public boolean isSupportCurPosition(HashMap<String, Object> map) {
        return seatInterface.isZeroGravitySupportPosition(getMethodPosition(map));
    }

    public boolean isAdjustSecondSidePosition(HashMap<String, Object> map) {
        return seatInterface.isAdjustSecondSidePosition(getMethodPosition(map));
    }

    public boolean isInCurModePosition(HashMap<String, Object> map) {
        int position = getMethodPosition(map).get(0);
        //0x0：初始值 0x1：座椅自动调整中 0x2：当前在零重力位置 0x3：当前在舒躺位置 0x4~0x7：预留
        int state = operator.getIntProp(VCarSeatSignal.SEAT_ZERO_GRAVITY, position);
        String nlu_info = (String) getValueInContext(map, DCContext.NLU_INFO);
        return seatInterface.isCurSeatPosition(state, nlu_info);
    }

    public boolean isHintRisk(HashMap<String, Object> map) {
        return keyContextInMap(map, "choose_type");
    }

    public void setSeatMode(HashMap<String, Object> map) {
        int position = getMethodPosition(map).get(0);
        operator.setBooleanProp(VCarSeatSignal.SEAT_ZERO_GRAVITY, position, true);
    }

    /** ----------------------- 一键舒躺 & 一键零重力 end--------------------------------------------------*/

    /**
     * ----------------------- 三排座椅折叠/展开 start--------------------------------------------------
     */

    public void setSeatFoldStopAdjust(HashMap<String, Object> map) {
        for (int position : getMethodPosition(map)) {
            operator.setIntProp(VCarSeatSignal.SEAT_FOLD_STOP_ADJUST, position, 0);
        }
    }

    public boolean isSupportSeatFold(HashMap<String, Object> map) {
        return seatInterface.isSupportSeatFold(getMethodPosition(map));
    }

    public boolean isCanSeatFold(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        return seatInterface.isCanSeatFold(operator, positions);
    }

    public boolean isBackPosFold(HashMap<String, Object> map) {
        return seatInterface.isBackPosFold(operator, getMethodPosition(map));
    }

    public boolean isBackPosOpend(HashMap<String, Object> map) {
        return seatInterface.isBackPosOpend(operator, getMethodPosition(map));
    }

    public boolean isSupportAdjustThirdSide(HashMap<String, Object> map) {
        return operator.getBooleanProp(VCarSeatSignal.SEAT_SUPPORT_ADJUST_THIRD_SIDE);
    }

    public boolean isSupportStopAdjustSeatFold(HashMap<String, Object> map) {
        return seatInterface.isSupportStopAdjustSeatFold();
    }

    public void setSeatFold(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        List<Integer> positions = getMethodPosition(map);
        for (int position : positions) {
            operator.setBooleanProp(VCarSeatSignal.SEAT_FOLD, position, switch_type.equals("open"));
        }
    }

    public boolean isThirdSide(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        return positions.size() > 1;
    }

    public boolean isThirdSideLift(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        return positions.get(0) == 4;
    }

    public boolean isSecondSide(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        return positions.size() > 1;
    }

    public boolean isSecondSideLift(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        return positions.get(0) == 2;
    }

    /**
     * ----------------------- 三排座椅折叠/展开 end--------------------------------------------------
     */

    public boolean isSupportSeatWelcome(HashMap<String, Object> map) {
        String positions = getOneMapValue("positions", map);
        if ((isH56CCar(map) || isH56DCar(map))) {
            if (!TextUtils.isEmpty(positions)) {
                return positions.equals("first_row_left") || positions.equals("second_side");
            }
        } else {
            if (!TextUtils.isEmpty(positions)) {
                return positions.equals("first_row_left");
            }
        }
        return true;
    }

    public boolean isDealFirstRowLeft(HashMap<String, Object> map) {
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            return positions.equals("first_row_left");
        }
        return true;
    }

    public boolean getSeatWelcomeStateSwitch(HashMap<String, Object> map) {
        return operator.getBooleanProp(VCarSeatSignal.SEAT_WELCOME_MODE_SWITCH, isDealFirstRowLeft(map) ? 0 : 1);
    }

    public void setSeatWelcomeStateSwitch(HashMap<String, Object> map) {
        boolean switchState = false;
        if (map.containsKey("switch_type")) {
            String str = (String) getValueInContext(map, "switch_type");
            if (str.equals("open")) {
                switchState = true;
            } else if (str.equals("close")) {
                switchState = false;
            }
        }
        operator.setBooleanProp(VCarSeatSignal.SEAT_WELCOME_MODE_SWITCH, isDealFirstRowLeft(map) ? 0 : 1, switchState);
    }

    /** ----------------------- 迎宾模式 end--------------------------------------------------*/

    /**
     * ----------------------- 通用函数 start--------------------------------------------------
     */

    private String mergePositionToString(HashMap<String, Object> map) {
        ArrayList<Integer> positionList = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        String position = getOneMapValue(DCContext.TTS_PLACEHOLDER, map);
        if (!TextUtils.isEmpty(position)) {
            LogUtils.d(TAG, "mergePositionToString position: 111" + position);
            return position;
        }
        if (positionList.size() == 1) {
            switch (positionList.get(0)) {
                case 0:
                    return "主驾";
                case 2:
                    return "左后";
                case 1:
                    return "副驾";
                case 3:
                    return "右后";
                case 4:
                    return "三排左";
                case 5:
                    return "三排右";
            }
        }
        String inValidPosition = getOneMapValue("invalid_position", map);
        if (!TextUtils.isEmpty(inValidPosition)) {
            LogUtils.d(TAG, "inValidPosition: " + inValidPosition);
            return inValidPosition;
        }
        //表示是两个位置。
        //0表示没有，1表示有
        int[] arr = new int[6];
        int config = operator.getIntProp(CommonSignal.COMMON_EQUIPMENT_LEVEL);
        if ((isH56CCar(map) && config == 4) || isH56DCar(map)) {
            for (int i = 0; i < positionList.size(); i++) {
                arr[positionList.get(i)] = 1;
            }
            if (arr[0] == 1 && arr[1] == 1 && arr[2] == 1 && arr[3] == 1 && arr[4] == 1 && arr[5] == 1) {
                return "全车";
            }
            if (arr[0] == 1 && arr[1] == 1) {
                getValueInContext(map, "position");
                return "前排";
            }
            if (arr[2] == 1 && arr[3] == 1) {
                return  "二排";
            }
            if (arr[4] == 1 && arr[5] == 1) {
                return "三排";
            }
            if (arr[0] == 1 && arr[2] == 1 && arr[4] == 1) {
                return "左侧";
            }
            if (arr[1] == 1 && arr[3] == 1 && arr[5] == 1) {
                return "右侧";
            }
        } else {
            for (int i = 0; i < positionList.size(); i++) {
                arr[positionList.get(i)] = 1;
            }
            if (arr[0] == 1 && arr[1] == 1 && arr[2] == 1 && arr[3] == 1) {
                return "全车";
            }
            if (arr[0] == 1 && arr[1] == 1) {
                getValueInContext(map, "position");
                return "前排";
            }
            if (arr[2] == 1 && arr[3] == 1) {
                return (isH56CCar(map) || isH56DCar(map)) ? "二排" : "后排";
            }
            if (arr[0] == 1 && arr[2] == 1) {
                return "左侧";
            }
            if (arr[1] == 1 && arr[3] == 1) {
                return "右侧";
            }
        }
        //出错了。
        return "全车";
    }

    /**
     * 指令位置是否为全车座椅（座椅只支持全车，前排，后排，三排这些说法）
     *
     * @param map 指令的座椅数参数数据
     * @return true：是全车座椅  false：不是全车座椅
     */
    public boolean isAllSeat(HashMap<String, Object> map) {
        String position = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(position)) {
            if (position.equals("left_side") || position.equals("right_side")) {
                // 56新增左侧/右侧的说法，这里需要优先处理下
                map.put("deal_side_position", position);
                return true;
            }
        }
        List<Integer> positions = getMethodPosition(map);
        return positions.size() == seatInterface.getAllSeatSize();
    }

    /**
     * 指令位置是否为多位置（座椅只支持全车，前排，后排，三排这些说法）
     *
     * @param map 指令的座椅数参数数据
     * @return true：多个座椅  false：单个座椅
     */
    public boolean isMultipleSeat(HashMap<String, Object> map) {
        return getCurPositionSize(map) > 1;
    }

    /**
     * 打开位置里面是否有后排座椅(因为车型问题，这里不换函数名了，对应的是是否有不支持的座位)
     *
     * @param map 指令的座椅数参数数据
     * @return true：有不支持的座位  false：没有不支持的座位
     */
    public boolean isHasRearSeat(HashMap<String, Object> map) {
        String nlu_info = (String) getValueInContext(map, DCContext.NLU_INFO);
        if (isH56DCar(map) && !nlu_info.contains("massage")) {
            // 56D全车型支持三排座椅加热通风
            LogUtils.d(TAG, "H56D deal seat or wind");
            return false;
        }
        int config = operator.getIntProp(CommonSignal.COMMON_EQUIPMENT_LEVEL);
        List<Integer> positions = getMethodPosition(map);
        boolean isHasRearSeat = false;
        if (isH37ACar(map)) {
            for (Integer position : positions) {
                if (position > 1) {
                    isHasRearSeat = true;
                    map.put("invalid_position", "后排");
                    break;
                }
            }
            LogUtils.e(TAG, "isH37ACar isHasRearSeat :" + isHasRearSeat);
            return isHasRearSeat;
        }
        if (isH37BCar(map)) {
            if (nlu_info.contains("massage")) {
                for (Integer position : positions) {
                    if (position > 1) {
                        isHasRearSeat = true;
                        map.put("invalid_position", "后排");
                        break;
                    }
                }
            } else {
                for (Integer position : positions) {
                    if (position > 3) {
                        isHasRearSeat = true;
                        map.put("invalid_position", "三排");
                        break;
                    }
                }
            }
            LogUtils.e(TAG, "isH37BCar isHasRearSeat :" + isHasRearSeat);
            return isHasRearSeat;
        }
        if ((isH56CCar(map) || isH56DCar(map)) && config == 2) {
            if (nlu_info.contains("massage")) {
                for (Integer position : positions) {
                    if (position < 2 || position > 3) {
                        isHasRearSeat = true;
                        map.put("invalid_position", position < 2 ? "前排" : "三排");
                        break;
                    }
                }
            } else {
                for (Integer position : positions) {
                    if (position > 3) {
                        isHasRearSeat = true;
                        map.put("invalid_position", "三排");
                        break;
                    }
                }
            }
            LogUtils.e(TAG, "isH56CCar2 isHasRearSeat :" + isHasRearSeat);
            return isHasRearSeat;
        }
        if ((isH56CCar(map) || isH56DCar(map)) && config == 3) {
            for (Integer position : positions) {
                if (position > 3) {
                    isHasRearSeat = true;
                    map.put("invalid_position", "三排");
                    break;
                }
            }
            LogUtils.e(TAG, "isH56CCar3 isHasRearSeat :" + isHasRearSeat);
            return isHasRearSeat;
        }
        // N4车型三排支持加热
        if ((isH56CCar(map) || isH56DCar(map)) && config == 4) {
            if (isSeatHeatType(map)) {
                return false;
            }
            for (Integer position : positions) {
                if (position > 3) {
                    isHasRearSeat = true;
                    map.put("invalid_position", "三排");
                    break;
                }
            }
            LogUtils.e(TAG, "isH56CCar4 isHasRearSeat :" + isHasRearSeat);
            return isHasRearSeat;
        }
        return false;
    }

    /**
     * 去除其中的后排座椅(车型适配，这里处理去掉无效座椅，方法名先不修改)
     *
     * @param map 指令的座椅数参数数据
     */
    public void removeRearSeat(HashMap<String, Object> map) {
        LogUtils.e(TAG, "removeRearSeat");
        String nlu_info = (String) getValueInContext(map, DCContext.NLU_INFO);
        if (isH56DCar(map) && !nlu_info.contains("massage")) {
            // 56D全车型支持三排座椅加热通风
            LogUtils.d(TAG, "H56D deal seat or wind");
            return;
        }
        String isSidePosition = getOneMapValue("deal_side_position", map);
        int config = operator.getIntProp(CommonSignal.COMMON_EQUIPMENT_LEVEL);
        if (!TextUtils.isEmpty(isSidePosition)) {
            if (isSidePosition.equals("left_side")) {
                if (isH37ACar(map)) {
                    map.put("change", "{0}");
                    positionChange(map);
                    return;
                }
                if (isH37BCar(map)) {
                    map.put("change", "{0,2}");
                    positionChange(map);
                    map.put(DCContext.TTS_PLACEHOLDER, "左侧");
                }
                if ((isH56CCar(map) || isH56DCar(map)) && config == 2) {
                    map.put("change", "{0,2}");
                    positionChange(map);
                    map.put(DCContext.TTS_PLACEHOLDER, "左侧");
                }
                if ((isH56CCar(map) || isH56DCar(map)) && config == 3) {
                    map.put("change", "{0,2}");
                    positionChange(map);
                    map.put(DCContext.TTS_PLACEHOLDER, "左侧");
                }
                if ((isH56CCar(map) || isH56DCar(map)) && config == 4) {
                    if (isSeatHeatType(map)) {
                        map.put("change", "{0,2,4}");
                    } else {
                        map.put("change", "{0,2}");
                    }
                    positionChange(map);
                    map.put(DCContext.TTS_PLACEHOLDER, "左侧");
                }
            } else {
                if (isH37ACar(map)) {
                    map.put("change", "{1}");
                    positionChange(map);
                    return;
                }
                if (isH37BCar(map)) {
                    map.put("change", "{1,3}");
                    positionChange(map);
                    map.put(DCContext.TTS_PLACEHOLDER, "右侧");
                }
                if ((isH56CCar(map) || isH56DCar(map)) && config == 2) {
                    map.put("change", "{1,3}");
                    positionChange(map);
                    map.put(DCContext.TTS_PLACEHOLDER, "右侧");
                }
                if ((isH56CCar(map) || isH56DCar(map)) && config == 3) {
                    map.put("change", "{1,3}");
                    positionChange(map);
                    map.put(DCContext.TTS_PLACEHOLDER, "右侧");
                }
                if ((isH56CCar(map) || isH56DCar(map)) && config == 4) {
                    if (isSeatHeatType(map)) {
                        map.put("change", "{1,3,5}");
                    } else {
                        map.put("change", "{1,3}");
                    }
                    positionChange(map);
                    map.put(DCContext.TTS_PLACEHOLDER, "右侧");
                }
            }
            return;
        }
        if (isH37ACar(map)) {
            map.put("change", "{0,1}");
            positionChange(map);
            map.put(DCContext.TTS_PLACEHOLDER, "前排");
            return;
        }
        if (isH37BCar(map) && nlu_info.contains("massage")) {
            map.put("change", "{0,1}");
            positionChange(map);
            map.put(DCContext.TTS_PLACEHOLDER, "前排");
            return;
        }
        if (isH37BCar(map)) {
            map.put("change", "{0,1,2,3}");
            positionChange(map);
            map.put(DCContext.TTS_PLACEHOLDER, "全车");
            return;
        }
        if (isH56CCar(map) && config == 2 && nlu_info.contains("massage")) {
            map.put("change", "{2,3}");
            positionChange(map);
            map.put(DCContext.TTS_PLACEHOLDER, "后排");
        }
        if ((isH56CCar(map) || isH56DCar(map)) && config == 2) {
            if (TextUtils.isEmpty(isSidePosition)) {
                map.put("change", "{0,1,2,3}");
                positionChange(map);
                map.put(DCContext.TTS_PLACEHOLDER, "全车");
            }
        }
        if ((isH56CCar(map) || isH56DCar(map)) && config == 3) {
            if (TextUtils.isEmpty(isSidePosition)) {
                map.put("change", "{0,1,2,3}");
                positionChange(map);
                map.put(DCContext.TTS_PLACEHOLDER, "全车");
            }
        }
        if ((isH56CCar(map) || isH56DCar(map)) && config == 4) {
            if (TextUtils.isEmpty(isSidePosition)) {
                if (isSeatHeatType(map)) {
                    map.put("change", "{0,1,2,3,4,5}");
                } else {
                    map.put("change", "{0,1,2,3}");
                }
                positionChange(map);
                map.put(DCContext.TTS_PLACEHOLDER, "全车");
            }
        }
    }

    /**
     * 是否有未占位座椅
     * 占位座椅情况，仅在用户指令全车时，才做判断
     * <p>
     * 方法名字有错误。
     *
     * @param map 指令的座椅数参数数据，此场景只涉及指令全车
     * @return true：有  false：没有
     */
    public boolean isHasUnUsedSeat(HashMap<String, Object> map) {
        String adjustType = getOneMapValue("adjust_type", map);
        float num = getNumber(map);
        if (!TextUtils.isEmpty(adjustType) && adjustType.equals("set") && num == 0.0f) {
            return false;
        }
        String isSidePosition = getOneMapValue("deal_side_position", map);
        if (!TextUtils.isEmpty(isSidePosition)) {
            if (isSidePosition.contains("side")) {
                return false;
            }
        }
        String nlu_info = (String) getValueInContext(map, DCContext.NLU_INFO);
        if (isH56DCar(map) && !nlu_info.contains("massage")) {
            // 56D全车型支持三排座椅加热通风
            LogUtils.d(TAG, "H56D deal seat or wind");
            return !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1)
                    || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2)
                    || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3)
                    || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 4)
                    || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 5);
        }
        int config = operator.getIntProp(CommonSignal.COMMON_EQUIPMENT_LEVEL);
        if (isH37ACar(map)) {
            return !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1);//判断副驾
        }
        if (isH37BCar(map) && nlu_info.contains("massage")) {
            return !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1);//判断副驾
        }
        if (isH37BCar(map)) {
            return !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1) ||
                    !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2)
                    || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3);//判断副驾，二排左，二排右
        }
        if (isH56CCar(map) && config == 2 && nlu_info.contains("massage")) {
            return !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2)
                    || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3);//判断二排左，二排右
        }
        if ((isH56CCar(map) || isH56DCar(map)) && config == 2) {
            return !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1) ||
                    !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2)
                    || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3);//判断副驾，二排左，二排右
        }
        if ((isH56CCar(map) || isH56DCar(map)) && config == 3) {
            return !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1)
                    || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2)
                    || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3);
        }
        if ((isH56CCar(map) || isH56DCar(map)) && config == 4) {
            if (isSeatHeatType(map)) {
                return !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1)
                        || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2)
                        || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3)
                        || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 4)
                        || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 5);
            }
            return !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1)
                    || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2)
                    || !operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3);
        }
        return false;
    }

    /**
     * 去掉未使用的座椅
     *
     * @param map 指令的座椅数参数数据，此场景只涉及指令全车
     */
    public void removeUnusedSeat(HashMap<String, Object> map) {
        LogUtils.d(TAG, "removeUnusedSeat");
        int config = operator.getIntProp(CommonSignal.COMMON_EQUIPMENT_LEVEL);
        String str = "{0,1}";
        if (isH37ACar(map)) {
            boolean copilotStatus = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1);
            if (!copilotStatus) {
                str = "{0}";
            }
        }

        String nlu_info = (String) getValueInContext(map, DCContext.NLU_INFO);
        if (isH37BCar(map) && nlu_info.contains("massage")) {
            boolean copilotStatus = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1);
            if (!copilotStatus) {
                str = "{0}";
            }
        }
        if (isH37BCar(map)) {
            boolean copilotStatus1 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1);
            boolean copilotStatus2 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2);
            boolean copilotStatus3 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3);
            String dealStr = "{0";
            if (copilotStatus1) {
                dealStr = dealStr + ",1";
            }
            if (copilotStatus2) {
                dealStr = dealStr + ",2";
            }
            if (copilotStatus3) {
                dealStr = dealStr + ",3";
            }
            str = dealStr + "}";
        }
        if (isH56CCar(map) && config == 2 && nlu_info.contains("massage")) {
            str = "{2,3}";
            map.put("change", str);
            positionChange(map);
            // N2车，单独回复
            map.put(DCContext.TTS_PLACEHOLDER, "二排");
            return;
        }
        if (isH56CCar(map) && config == 2) {
            boolean copilotStatus1 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1);
            boolean copilotStatus2 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2);
            boolean copilotStatus3 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3);
            String dealStr = "{0";
            if (copilotStatus1) {
                dealStr = dealStr + ",1";
            }
            if (copilotStatus2) {
                dealStr = dealStr + ",2";
            }
            if (copilotStatus3) {
                dealStr = dealStr + ",3";
            }
            str = dealStr + "}";
        }
        if (isH56CCar(map) && config == 3) {
            boolean copilotStatus1 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1);
            boolean copilotStatus2 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2);
            boolean copilotStatus3 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3);
            String dealStr = "{0";
            if (copilotStatus1) {
                dealStr = dealStr + ",1";
            }
            if (copilotStatus2) {
                dealStr = dealStr + ",2";
            }
            if (copilotStatus3) {
                dealStr = dealStr + ",3";
            }
            str = dealStr + "}";
        }
        if (isH56CCar(map) && config == 4 && nlu_info.contains("heat")) {
            boolean copilotStatus1 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1);
            boolean copilotStatus2 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2);
            boolean copilotStatus3 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3);
            boolean copilotStatus4 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 4);
            boolean copilotStatus5 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 5);
            String dealStr = "{0";
            if (copilotStatus1) {
                dealStr = dealStr + ",1";
            }
            if (copilotStatus2) {
                dealStr = dealStr + ",2";
            }
            if (copilotStatus3) {
                dealStr = dealStr + ",3";
            }
            if (isSeatHeatType(map)) {
                if (copilotStatus4) {
                    dealStr = dealStr + ",4";
                }
                if (copilotStatus5) {
                    dealStr = dealStr + ",5";
                }
            }
            str = dealStr + "}";
        } else if (isH56CCar(map) && config == 4 && (nlu_info.contains("wind") || nlu_info.contains("massage"))) {
            boolean copilotStatus1 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1);
            boolean copilotStatus2 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2);
            boolean copilotStatus3 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3);
            String dealStr = "{0";
            if (copilotStatus1) {
                dealStr = dealStr + ",1";
            }
            if (copilotStatus2) {
                dealStr = dealStr + ",2";
            }
            if (copilotStatus3) {
                dealStr = dealStr + ",3";
            }
            str = dealStr + "}";
        }
        if (isH56DCar(map) && !nlu_info.contains("massage")) {
            boolean copilotStatus1 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1);
            boolean copilotStatus2 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2);
            boolean copilotStatus3 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3);
            boolean copilotStatus4 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 4);
            boolean copilotStatus5 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 5);
            String dealStr = "{0";
            if (copilotStatus1) {
                dealStr = dealStr + ",1";
            }
            if (copilotStatus2) {
                dealStr = dealStr + ",2";
            }
            if (copilotStatus3) {
                dealStr = dealStr + ",3";
            }
            if (isSeatHeatType(map)) {
                if (copilotStatus4) {
                    dealStr = dealStr + ",4";
                }
                if (copilotStatus5) {
                    dealStr = dealStr + ",5";
                }
            }
            str = dealStr + "}";
        }
        if (isH56DCar(map) && nlu_info.contains("massage")) {
            boolean copilotStatus1 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 1);
            boolean copilotStatus2 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 2);
            boolean copilotStatus3 = operator.getBooleanProp(VCarSeatSignal.SEAT_OCCUPIED_STATUS, 3);
            String dealStr = "{0";
            if (copilotStatus1) {
                dealStr = dealStr + ",1";
            }
            if (copilotStatus2) {
                dealStr = dealStr + ",2";
            }
            if (copilotStatus3) {
                dealStr = dealStr + ",3";
            }
            str = dealStr + "}";
        }
        LogUtils.d(TAG, "removeUnusedSeat str: " + str);
        map.put("change", str);
        positionChange(map);
        map.put(DCContext.TTS_PLACEHOLDER, "已占位");
    }

    private boolean isSeatHeatType(HashMap<String, Object> map) {
        String type = getOneMapValue("seat_type", map);
        if (!TextUtils.isEmpty(type)) {
            return type.equals("heat");
        }
        return false;
    }

    /**
     * 对{0,1,2,3}的位置转化为VehicleArea位置信息
     *
     * @param positions 当前分支里的位置信息。
     * @return 返回只需要执行的VehicleArea位置信息。
     */
    private ArrayList<Integer> analysisPosition(List<Integer> positions) {
        //跟具体车型相关的，下沉到各自实现中 TODO 修改调用该方法的位置
        return new ArrayList<>(positions);
    }

    /**
     * 获取处理位置的数据
     *
     * @return 位置数据
     */
    public Object getPosition(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        return positions;
    }

    /**
     * 获取处理位置的数据size
     *
     * @return 数据的size
     */
    public int getPositionCount(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        return positions.size();
    }

    /**
     * 是否驾驶状态中
     *
     * @return true:驾驶中   false:未驾驶
     */
    public boolean isDriving(HashMap<String, Object> map) {
        return operator.getBooleanProp(VCarSeatSignal.CAR_STATE_DRIVING, 0);
    }

    public void levelToNumber(HashMap<String, Object> map) {
        String level = (String) getValueInContext(map, "level");
        if (level == null) {
            return;
        }
        switch (level) {
            case "low":
                map.put("number_level", "1");
                break;
            case "mid":
                map.put("number_level", "2");
                break;
            case "high":
                map.put("number_level", "3");
                break;
        }
    }

    /**
     * 获取执行的座位表数据
     */
    public List<Integer> getExecutePositions(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        return analysisPosition(positions);
    }

    public boolean is56DSeatPage(HashMap<String, Object> map) {
        return isH56DCar(map);
    }

    public boolean is56DSeatPageOpen(HashMap<String, Object> map) {
        boolean isOpenSeatApp = operator.getBooleanProp(VCarSeatSignal.SEAT_PAGE_SATE, getOrderScreenType(map));
        String switchType = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(switchType)) {
            if (switchType.equals("close")) {
                return isOpenSeatApp;
            }
        }
        switch (orderSeatPage(map)) {
            case 0:
                return operator.getBooleanProp(VCarSeatSignal.SEAT_PAGE_VENTANDHEAT, getOrderScreenType(map));
            case 1:
                return operator.getBooleanProp(VCarSeatSignal.SEAT_PAGE_MASSAGE, getOrderScreenType(map));
            case 2:
                return operator.getBooleanProp(VCarSeatSignal.SEAT_PAGE_COMFORT_ENJOY, getOrderScreenType(map));
            case 3:
                return operator.getBooleanProp(VCarSeatSignal.SEAT_PAGE_FRONT_MEM_ADJUST, getOrderScreenType(map));
            case 4:
                return operator.getBooleanProp(VCarSeatSignal.SEAT_PAGE_2ROW_MEM_ADJUST, getOrderScreenType(map));
            case 5:
                return operator.getBooleanProp(VCarSeatSignal.SEAT_PAGE_3ROW_MEM_ADJUST, getOrderScreenType(map));
            default:
                return isOpenSeatApp;
        }
    }

    public void open56DSeatPage(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(getOrderScreenType(map) == 0 ? DeviceScreenType.CENTRAL_SCREEN : DeviceScreenType.PASSENGER_SCREEN, "SeatPage");
        switch (orderSeatPage(map)) {
            case 0:
                operator.setStringProp(VCarSeatSignal.SEAT_PAGE_SATE, SeatConstants.ACTION_OPEN_VENTILATION_HEAT + getOrdDisplayId(map));
                break;
            case 1:
                operator.setStringProp(VCarSeatSignal.SEAT_PAGE_SATE, SeatConstants.ACTION_OPEN_MASSAGE + getOrdDisplayId(map));
                break;
            case 2:
                operator.setStringProp(VCarSeatSignal.SEAT_PAGE_SATE, SeatConstants.ACTION_OPEN_COMFORT_ENJOY + getOrdDisplayId(map));
                break;
            case 3:
                operator.setStringProp(VCarSeatSignal.SEAT_PAGE_SATE, SeatConstants.ACTION_OPEN_FIRST_ADJUST + getOrdDisplayId(map));
                break;
            case 4:
                operator.setStringProp(VCarSeatSignal.SEAT_PAGE_SATE, SeatConstants.ACTION_OPEN_SECOND_ADJUST + getOrdDisplayId(map));
                break;
            case 5:
                operator.setStringProp(VCarSeatSignal.SEAT_PAGE_SATE, SeatConstants.ACTION_OPEN_THIRD_ADJUST + getOrdDisplayId(map));
                break;
        }
    }

    private int orderSeatPage(HashMap<String, Object> map) {
        int orderSeat = 0;
        String uiName = getOneMapValue("ui_name", map);
        if (!TextUtils.isEmpty(uiName)) {
            switch (uiName) {
                case "ventilation_heat":
                case "heat":
                case "ventilation":
                    orderSeat = 0;
                    break;
                case "massage":
                    orderSeat = 1;
                    break;
                case "comfort":
                    orderSeat = 2;
                    break;
                case "first_adjust":
                    orderSeat = 3;
                    break;
                case "second_adjust":
                    orderSeat = 4;
                    break;
                case "third_adjust":
                    orderSeat = 5;
                    break;
                case "adjust":
                    String position = getOneMapValue("positions", map);
                    if (!TextUtils.isEmpty(position)) {
                        switch (position) {
                            case "front_side":
                                orderSeat = 3;
                                break;
                            case "second_side":
                            case "rear_side":
                                orderSeat = 4;
                                break;
                            case "third_side":
                                orderSeat = 5;
                                break;
                            default:
                                orderSeat = 3;
                                break;
                        }
                    } else {
                        orderSeat = 3;
                    }
                    break;
            }
        }
        LogUtils.d(TAG, "orderSeatPage : " + orderSeat);
        return orderSeat;
    }

    public boolean isSupportOpenSeatPage(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isSupportOpenSeatPage");
        if ((isH56CCar(map) || isH56DCar(map))) {
            return getOrderScreenType(map) != -1;
        }
        return true;
    }

    /**
     * 执行1：打开座椅舒适界面
     * switch_type:open
     * ui_name:comfort
     * <p>
     * 执行2：打开座椅界面，未指定时，打开舒适界面
     * switch_type:open
     *
     * @param map 指令的座椅数参数数据
     */
    public void openComfort(HashMap<String, Object> map) {
        LogUtils.d(TAG, "openComfort");
        if (isH56DCar(map)) {
            open56DSeatPage(map);
        } else {
            DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(getOrderScreenType(map) == 0 ? DeviceScreenType.CENTRAL_SCREEN : DeviceScreenType.PASSENGER_SCREEN, "SeatPage");
            operator.setStringProp(VCarSeatSignal.SEAT_PAGE_SATE, (isH56DCar(map) ? SeatConstants.ACTION_OPEN_COMFORT_ENJOY : SeatConstants.ACTION_OPEN_COMFORT) + getOrdDisplayId(map));
        }
    }

    /**
     * 打开座椅调节界面
     * switch_type:open
     * ui_name:adjust
     *
     * @param map 指令的座椅数参数数据
     */
    public void openAdjust(HashMap<String, Object> map) {
        LogUtils.d(TAG, "openAdjust");
        if (isH56DCar(map)) {
            open56DSeatPage(map);
        } else {
            DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(getOrderScreenType(map) == 0 ? DeviceScreenType.CENTRAL_SCREEN : DeviceScreenType.PASSENGER_SCREEN, "SeatPage");
            operator.setStringProp(VCarSeatSignal.SEAT_PAGE_SATE, SeatConstants.ACTION_OPEN_ADJUST + getOrdDisplayId(map));
        }
    }

    public boolean isSeatPageOpen(HashMap<String, Object> map) {
        LogUtils.d(TAG, "isSeatPageOpen");
        if (isH56DCar(map)) {
            return is56DSeatPageOpen(map);
        }
        return operator.getBooleanProp(VCarSeatSignal.SEAT_PAGE_SATE, getOrderScreenType(map));
    }

    public void closeSeatPage(HashMap<String, Object> map) {
        LogUtils.d(TAG, "closeSeatPage");
        operator.setStringProp(VCarSeatSignal.SEAT_PAGE_SATE, "ACTION_CLOSE_SEAT_PANEL" + getOrdDisplayId(map));
    }

    private String getOrdDisplayId(HashMap<String, Object> map) {
        if ((isH56CCar(map) || isH56DCar(map))) {
            return getOrderScreenType(map) == 1 ? SeatConstants.DISPLAY_ID2 : SeatConstants.DISPLAY_ID;
        }
        if (isH37ACar(map) || isH37BCar(map)) {
            return "";
        }
        return SeatConstants.DISPLAY_ID;
    }

    private int getOrderScreenType(HashMap<String, Object> map) {
        if (isH37ACar(map) || isH37BCar(map)) {
            return 0;
        }
        // 传递displayId 0,1
        int displayId = 0;
        ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
        int curPosition = curSoundPositions.get(0);
        // 冲突说法以position为基准，处理前排/二排/三排这些说法，需要先判断声源位置信息,对这几个页面做特殊处理
        String position = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(position)) {
            switch (position) {
                case "first_row_left":
                    displayId = 0;
                    break;
                case "first_row_right":
                    displayId = 1;
                    break;
                case "front_side":
                case "second_side":
                case "rear_side":
                case "third_side":
                    if (isH56DCar(map)) {
                        switch (curPosition) {
                            case 0:
                                displayId = 0;
                                break;
                            case 1:
                                displayId = 1;
                                break;
                            default:
                                displayId = -1;
                        }
                    }
                    break;
                default:
                    displayId = -1;
                    break;
            }
        } else {
            String screenName = getOneMapValue("screen_name", map);
            if (!TextUtils.isEmpty(screenName)) {
                switch (screenName) {
                    case "entertainment_screen":
                        switch (curPosition) {
                            case 0:
                            case 1:
                                displayId = 1;
                                break;
                            default:
                                displayId = -1;
                        }
                        break;
                    case "screen":
                        switch (curPosition) {
                            case 0:
                                displayId = 0;
                                break;
                            case 1:
                                displayId = 1;
                                break;
                            default:
                                displayId = -1;
                        }
                        break;
                    case "central_screen":
                        displayId = 0;
                        break;
                    case "passenger_screen":
                        displayId = 1;
                        break;
                    case "ceil_screen":
                        displayId = -1;
                        break;
                    default:
                        displayId = -1;
                }
            } else {
                switch (curPosition) {
                    case 0:
                        displayId = 0;
                        break;
                    case 1:
                        displayId = 1;
                        break;
                    default:
                        displayId = -1;
                }
            }
        }
        LogUtils.d(TAG, "getScreenSwitchState displayId: " + displayId);
        return displayId;
    }

    /**
     * 判断输入的X档，是否有效。X表示1.2.3.低.中.高，其中1=低挡.2=中挡.3=高挡
     *
     * @param map 指令的座椅数参数数据
     * @return true：输入的挡位有效  false：输入的挡位无效
     */
    public boolean isNumLevelValid(HashMap<String, Object> map) {
        float num = getNumber(map);
        return num >= 1 && num <= 3;
    }

    public float getNumber(HashMap<String, Object> map) {
        float num = 0.0f;
        if (map.containsKey("number")) {
            String str = (String) getValueInContext(map, "number");
            if (str.endsWith("%")) {
                num = Float.parseFloat(str.substring(0, str.length() - 1)) / 100;
            } else if (str.contains("/")) {
                String[] parts = str.split("/");
                if (parts.length == 2) {
                    int numerator = Integer.parseInt(parts[0]);
                    int denominator = Integer.parseInt(parts[1]);
                    num = (float) numerator / denominator;
                }
            } else {
                num = Float.parseFloat(str);
            }
        }
        if (map.containsKey("number_level")) {
            String str = (String) getValueInContext(map, "number_level");
            if (str.endsWith("%")) {
                num = Float.parseFloat(str.substring(0, str.length() - 1)) / 100;
            } else if (str.contains("/")) {
                String[] parts = str.split("/");
                if (parts.length == 2) {
                    int numerator = Integer.parseInt(parts[0]);
                    int denominator = Integer.parseInt(parts[1]);
                    num = (float) numerator / denominator;
                }
            } else {
                num = Float.parseFloat(str);
            }
        }
        if (num > 0 && num <= 1) {
            num = 1;
        } else if (num > 1 && num <= 2) {
            num = 2;
        } else if (num > 2 && num <= 3) {
            num = 3;
        }
        LogUtils.d(TAG, "getNumber: " + num);
        return num;
    }

    /** ----------------------- 通用函数 end--------------------------------------------------*/
}
