package com.voyah.ai.device.voyah.h37.dc.utils;

import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.dc.SeatInterface;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.VCarSeatSignal;
import com.voice.sdk.device.carservice.vcar.BaseOperator;
import com.voice.sdk.device.carservice.vcar.IPropertyOperator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatImpl implements SeatInterface {

    @Override
    public int getAllSeatSize() {
        return 6;
    }

    // 座椅一键零重力 & 一键舒躺

    @Override
    public boolean isHasZeroGravityConfig(String nluInfo) {
        //无零重力座椅 = 有座椅舒躺
        // 0=无重力座椅 2=有重力座椅
        int mZeroGravityConfig = MegaSystemProperties.getInt(MegaProperties.CONFIG_ZEROGRAVITY_SEAT, -1);
        return nluInfo.contains("seat_comfortably") ? mZeroGravityConfig == 0 : mZeroGravityConfig == 2;
    }

    @Override
    public boolean isZeroGravitySupportPosition(List<Integer> positions) {
        boolean isSupportCurPosition = true;
        for (Integer position : positions) {
            if (position < 2 || position > 3) {
                isSupportCurPosition = false;
            }
        }
        return isSupportCurPosition;
    }

    @Override
    public boolean isAdjustSecondSidePosition(List<Integer> positions) {
        boolean isHasSecondSideSeat = false;
        for (Integer position : positions) {
            if (position > 1 && position < 4) {
                isHasSecondSideSeat = true;
            }
        }
        return isHasSecondSideSeat && positions.size() == 2;
    }

    @Override
    public boolean isCurSeatPosition(int state, String nluinfo) {
        return nluinfo.contains("seat_comfortably") ? state == 3 : state == 2;
    }


    // 座椅折叠 & 展开

    @Override
    public boolean isSupportSeatFold(List<Integer> positions) {
        boolean isSupportSeatFold = true;
        for (Integer position : positions) {
            if (position < 4) {
                isSupportSeatFold = false;
            }
        }
        return isSupportSeatFold;
    }

    @Override
    public boolean isCanSeatFold(IPropertyOperator operator, List<Integer> positions) {
        boolean usedThirdRowLeftPosition = operator.getBooleanProp(VCarSeatSignal.SEAT_USER_STATE, PositionSignal.THIRD_ROW_LEFT);
        boolean usedThirdRowMidPosition = operator.getBooleanProp(VCarSeatSignal.SEAT_USER_STATE, 6);
        boolean usedThirdRowRightPosition = operator.getBooleanProp(VCarSeatSignal.SEAT_USER_STATE, PositionSignal.THIRD_ROW_RIGHT);
        //处理位置=1就是单独操作三排左和三排右，>1就是操作三排
        if (positions.size() == 1) {
            int curPosition = positions.get(0);
            // 4=三排左 5=三排右
            if (curPosition == 4) {
                //如果是折叠三排左，需要三排左和三排中都没被占位
                return !usedThirdRowLeftPosition && !usedThirdRowMidPosition;
            } else {
                //如果是折叠三排右，需要三排右和三排中都没被占位
                return !usedThirdRowRightPosition && !usedThirdRowMidPosition;
            }
        } else {
            //如果是折叠三排，需要三排右和三排中和三排右都没被占位
            return !usedThirdRowLeftPosition && !usedThirdRowMidPosition && !usedThirdRowRightPosition;
        }
    }

    @Override
    public boolean isSupportStopAdjustSeatFold() {
        return true;
    }

    @Override
    public boolean isBackPosFold(IPropertyOperator operator, List<Integer> positions) {
        int thirdRowLeftBackPosition = operator.getIntProp(VCarSeatSignal.SEAT_CUR_FOLD_POSITION, PositionSignal.THIRD_ROW_LEFT);
        int thirdRowRightBackPosition = operator.getIntProp(VCarSeatSignal.SEAT_CUR_FOLD_POSITION, PositionSignal.THIRD_ROW_RIGHT);
        if (positions.size() == 1) {
            int curPosition = positions.get(0);
            if (curPosition == 4) {
                return thirdRowLeftBackPosition < 6;
            } else {
                return thirdRowRightBackPosition < 6;
            }
        } else {
            return thirdRowLeftBackPosition < 6 && thirdRowRightBackPosition < 6;
        }
    }

    @Override
    public boolean isBackPosOpend(IPropertyOperator operator, List<Integer> positions) {
        int thirdRowLeftBackPosition = operator.getIntProp(VCarSeatSignal.SEAT_CUR_FOLD_POSITION, PositionSignal.THIRD_ROW_LEFT);
        int thirdRowRightBackPosition = operator.getIntProp(VCarSeatSignal.SEAT_CUR_FOLD_POSITION, PositionSignal.THIRD_ROW_RIGHT);
        if (positions.size() == 1) {
            int curPosition = positions.get(0);
            if (curPosition == 4) {
                return thirdRowLeftBackPosition > 79;
            } else {
                return thirdRowRightBackPosition > 79;
            }
        } else {
            return thirdRowLeftBackPosition > 79 && thirdRowRightBackPosition > 79;
        }
    }


    // 座椅按摩

    @Override
    public boolean isSeatMassageModeNewSpeakType() {
        return true;
    }

    @Override
    public boolean isSupportMassageMode(List<Integer> positions, String massageMode) {
        boolean isSupportMassageMode = false;
        for (Integer position : positions) {
            if (position < 2) {
                // 前排
                isSupportMassageMode = getFrontSideSupportMassageModeName().containsKey(massageMode);
            } else {
                int config = MegaSystemProperties.getInt(MegaProperties.CONFIG_EQUIPMENT_LEVEL, -1);
                if (config == 4) {
                    isSupportMassageMode = getRearSideSupportMassageModeName().containsKey(massageMode);
                } else {
                    isSupportMassageMode = getFrontSideSupportMassageModeName().containsKey(massageMode);
                }
            }
        }
        return isSupportMassageMode;
    }

    @Override
    public String getNextMassageMode(int position, String curMode) {
        String nextMode = "body_relaxation";
        if (curMode.equalsIgnoreCase("body_relaxation")) {
            nextMode = "body_circulation";
        } else if (curMode.equalsIgnoreCase("body_circulation")) {
            nextMode = "body_stretch";
        } else if (curMode.equalsIgnoreCase("body_stretch")) {
            nextMode = "shoulder_stretch";
        } else if (curMode.equalsIgnoreCase("shoulder_stretch")) {
            nextMode = "backside_activation";
        } else if (curMode.equalsIgnoreCase("backside_activation")) {
            if (position < 2) {
                nextMode = "body_relaxation";
            } else {
                int config = MegaSystemProperties.getInt(MegaProperties.CONFIG_EQUIPMENT_LEVEL, -1);
                if (config == 4) {
                    nextMode = "waist_relaxation";
                } else {
                    nextMode = "body_relaxation";
                }
            }
        } else if (curMode.equalsIgnoreCase("waist_relaxation")) {
            nextMode = "waist_stretch";
        } else if (curMode.equalsIgnoreCase("waist_stretch")) {
            nextMode = "footrest_circulation";
        } else if (curMode.equalsIgnoreCase("footrest_circulation")) {
            nextMode = "body_relaxation";
        }
        return nextMode;
    }

    @Override
    public Map<String, String> getModeName() {
        return getRearSideSupportMassageModeName();
    }

    @Override
    public String[] getSeatMassageSupportMode() {
        return getRearSideSupportMassageMode();
    }

    public String[] getFrontSideSupportMassageMode() {
        return new String[]{"body_relaxation", "body_circulation", "body_stretch", "shoulder_stretch", "backside_activation"};
    }

    public String[] getRearSideSupportMassageMode() {
        return new String[]{"body_relaxation", "body_circulation", "body_stretch", "shoulder_stretch", "backside_activation", "waist_relaxation", "waist_stretch", "footrest_circulation"};
    }

    public  Map<String, String> getFrontSideSupportMassageModeName() {
        return new HashMap<String, String>() {{
            put("body_relaxation", "全身放松");
            put("body_circulation", "全身循环");
            put("body_stretch", "全身舒展");
            put("shoulder_stretch", "肩部舒展");
            put("backside_activation", "背部激活");
        }};
    }

    public  Map<String, String> getRearSideSupportMassageModeName() {
        return new HashMap<String, String>() {{
            put("body_relaxation", "全身放松");
            put("body_circulation", "全身循环");
            put("body_stretch", "全身舒展");
            put("shoulder_stretch", "肩部舒展");
            put("backside_activation", "背部激活");
            put("waist_relaxation", "腰部放松");
            put("waist_stretch", "腰臀拉伸");
            put("footrest_circulation", "腿托循环");
        }};
    }


    // 座椅记忆保存&激活

    @Override
    public String[] getDriverSupportPosition() {
        return new String[]{"habit_one", "habit_two", "habit_three"};
    }

    @Override
    public String[] getRearSideSupportPosition() {
        return new String[]{"posture_one", "posture_two", "posture_three"};
    }

    @Override
    public String[] getPassengerSupportPosition() {
        return new String[]{"posture_one", "posture_two", "posture_three"};
    }

    @Override
    public void setFirstRowLeftPosition(HashMap<String, Object> map) {
        map.put("seat_mem_mode", "habit_one");
    }

    @Override
    public void setFirstRowRightPosition(HashMap<String, Object> map) {
        map.put("seat_mem_mode", "posture_one");
    }

    @Override
    public void setRearSidePosition(HashMap<String, Object> map) {
        map.put("seat_mem_mode", "posture_one");
    }

    @Override
    public boolean isSupportRearSideSeatMemory() {
        return true;
    }

    @Override
    public boolean isSpeak37TssText() {
        return false;
    }

    @Override
    public boolean isSpeak56CTssText() {
        return false;
    }

    @Override
    public boolean isSpeak56DTssText() {
        return true;
    }
}
