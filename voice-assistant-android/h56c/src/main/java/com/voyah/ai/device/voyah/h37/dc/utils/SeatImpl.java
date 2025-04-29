package com.voyah.ai.device.voyah.h37.dc.utils;

import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.dc.SeatInterface;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.VCarSeatSignal;
import com.voice.sdk.device.carservice.vcar.BaseOperator;
import com.voice.sdk.device.carservice.vcar.IPropertyOperator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatImpl implements SeatInterface {

    @Override
    public int getAllSeatSize() {
        return 6;
    }

    @Override
    public String[] getSeatMassageSupportMode() {
        return new String[]{"wave", "rolling"};
    }

    @Override
    public String getNextMassageMode(int position, String curMode) {
        String nextMode = "wave";
        if (curMode.equalsIgnoreCase("wave")) {
            nextMode = "rolling";
        }
        return nextMode;
    }

    @Override
    public Map<String, String> getModeName() {
        return new HashMap<String, String>() {{
            put("wave", "波浪");
            put("rolling", "滚动");
        }};
    }

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

    @Override
    public boolean isSeatMassageModeNewSpeakType() {
        return false;
    }

    @Override
    public boolean isSupportMassageMode(List<Integer> positions, String massageMode) {
        List<String> list = Arrays.asList(getSeatMassageSupportMode());
        return list.contains(massageMode);
    }

    @Override
    public String[] getDriverSupportPosition() {
        return new String[]{"driving_position", "resting_position", "other_position"};
    }

    @Override
    public String[] getRearSideSupportPosition() {
        return new String[]{"position_one", "position_two", "position_three"};
    }

    @Override
    public String[] getPassengerSupportPosition() {
        return new String[]{"usual_position", "resting_position", "other_position"};
    }

    @Override
    public boolean isSupportStopAdjustSeatFold() {
        return false;
    }

    @Override
    public void setFirstRowLeftPosition(HashMap<String, Object> map) {
        map.put("seat_mem_mode", "driving_position");
    }

    @Override
    public void setFirstRowRightPosition(HashMap<String, Object> map) {
        map.put("seat_mem_mode", "usual_position");
    }

    @Override
    public void setRearSidePosition(HashMap<String, Object> map) {
        map.put("seat_mem_mode", "position_one");
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
        return true;
    }

    @Override
    public boolean isSpeak56DTssText() {
        return false;
    }
}
