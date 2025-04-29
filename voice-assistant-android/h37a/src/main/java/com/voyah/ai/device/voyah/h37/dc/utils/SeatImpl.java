package com.voyah.ai.device.voyah.h37.dc.utils;

import com.voice.sdk.device.carservice.dc.SeatInterface;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.VCarSeatSignal;
import com.voice.sdk.device.carservice.vcar.IPropertyOperator;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatImpl implements SeatInterface {
    @Override
    public String[] getSeatMassageSupportMode() {
        return new String[]{"wave", "rolling", "snake", "backside", "waist"};
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
        return new String[]{"usual_position", "resting_position", "watch_movies_position"};
    }

    @Override
    public int getAllSeatSize() {
        return 4;
    }

    @Override
    public String getNextMassageMode(int position, String curMode) {
        String nextMode = "wave";
        if (curMode.equalsIgnoreCase("wave")) {
            nextMode = "rolling";
        } else if (curMode.equalsIgnoreCase("rolling")) {
            nextMode = "snake";
        } else if (curMode.equalsIgnoreCase("waist")) {
            nextMode = "wave";
        } else if (curMode.equalsIgnoreCase("snake")) {
            nextMode = "backside";
        } else if (curMode.equalsIgnoreCase("backside")) {
            nextMode = "waist";
        }
        return nextMode;
    }

    @Override
    public Map<String, String> getModeName() {
        return new HashMap<String, String>() {{
            put("wave", "波浪");
            put("rolling", "滚动");
            put("waist", "腰部");
            put("snake", "蛇形");
            put("backside", "背部");
        }};
    }

    @Override
    public boolean isHasZeroGravityConfig(String nluInfo) {
        // 37A全座椅不支持座椅零重力，37B副驾支持座椅零重力
        String carType = CarServicePropUtils.getInstance().getCarType();
        return !carType.equalsIgnoreCase("H37A");
    }

    @Override
    public boolean isZeroGravitySupportPosition(List<Integer> positions) {
        // 37A全座椅不支持座椅零重力，37B副驾支持座椅零重力
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return false;
        }
        boolean isSupportCurPosition = false;
        for (Integer position : positions) {
            if (position == 1) {
                isSupportCurPosition = true;
            }
        }
        return isSupportCurPosition;
    }

    @Override
    public boolean isAdjustSecondSidePosition(List<Integer> positions) {
        // 37B不需要判断，直接往下走
        return false;
    }

    @Override
    public boolean isCurSeatPosition(int state, String nluinfo) {
        // TODO: 2025/3/28 37B暂时无法判断是不是已经在零重力位置，先默认放回false
        return false;
    }

    @Override
    public boolean isSupportSeatFold(List<Integer> positions) {
        // 37A不支持座椅折叠，37B支持后排座椅折叠
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            return false;
        }
        for (Integer position : positions) {
            if (position < 2 || position > 3) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCanSeatFold(IPropertyOperator operator, List<Integer> positions) {
        boolean usedSecondRowLeftPosition = operator.getBooleanProp(VCarSeatSignal.SEAT_USER_STATE, PositionSignal.SECOND_ROW_LEFT);
        boolean usedSecondRowMidPosition = operator.getBooleanProp(VCarSeatSignal.SEAT_USER_STATE, 4);
        boolean usedSecondRowRightPosition = operator.getBooleanProp(VCarSeatSignal.SEAT_USER_STATE, PositionSignal.SECOND_ROW_RIGHT);
        //处理位置=1就是单独操作三排左和三排右，>1就是操作三排
        if (positions.size() == 1) {
            int curPosition = positions.get(0);
            // 4=三排左 5=三排右
            if (curPosition == 4) {
                //如果是折叠三排左，需要三排左和三排中都没被占位
                return !usedSecondRowLeftPosition && !usedSecondRowMidPosition;
            } else {
                //如果是折叠三排右，需要三排右和三排中都没被占位
                return !usedSecondRowRightPosition && !usedSecondRowMidPosition;
            }
        } else {
            //如果是折叠三排，需要三排右和三排中和三排右都没被占位
            return !usedSecondRowLeftPosition && !usedSecondRowMidPosition && !usedSecondRowRightPosition;
        }
    }

    @Override
    public boolean isBackPosFold(IPropertyOperator operator, List<Integer> positions) {
        int secondRowLeftBackPosition = operator.getIntProp(VCarSeatSignal.SEAT_CUR_FOLD_POSITION,PositionSignal.SECOND_ROW_LEFT);
        int secondRowRightBackPosition = operator.getIntProp(VCarSeatSignal.SEAT_CUR_FOLD_POSITION,PositionSignal.SECOND_ROW_RIGHT);
        if (positions.size() == 1) {
            int curPosition = positions.get(0);
            if (curPosition == 2) {
                return secondRowLeftBackPosition < 6;
            } else {
                return secondRowRightBackPosition < 6;
            }
        } else {
            return secondRowLeftBackPosition < 6 && secondRowRightBackPosition < 6;
        }
    }

    @Override
    public boolean isBackPosOpend(IPropertyOperator operator, List<Integer> positions) {
        int secondRowLeftBackPosition = operator.getIntProp(VCarSeatSignal.SEAT_CUR_FOLD_POSITION,PositionSignal.SECOND_ROW_LEFT);
        int secondRowRightBackPosition = operator.getIntProp(VCarSeatSignal.SEAT_CUR_FOLD_POSITION,PositionSignal.SECOND_ROW_RIGHT);
        if (positions.size() == 1) {
            int curPosition = positions.get(0);
            if (curPosition == 4) {
                return secondRowLeftBackPosition > 79;
            } else {
                return secondRowRightBackPosition > 79;
            }
        } else {
            return secondRowLeftBackPosition > 79 && secondRowRightBackPosition > 79;
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
        // 37不支持二排座椅记忆
    }

    @Override
    public boolean isSupportRearSideSeatMemory() {
        return false;
    }

    @Override
    public boolean isSpeak37TssText() {
        return true;
    }

    @Override
    public boolean isSpeak56CTssText() {
        return false;
    }

    @Override
    public boolean isSpeak56DTssText() {
        return false;
    }
}
