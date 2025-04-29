package com.voice.sdk.device.carservice.dc;

import com.voice.sdk.device.carservice.vcar.IPropertyOperator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SeatInterface {

    String[] getSeatMassageSupportMode();

    String[] getDriverSupportPosition();

    String[] getRearSideSupportPosition();

    String[] getPassengerSupportPosition();

    int getAllSeatSize();

    String getNextMassageMode(int position, String curMode);

    Map<String, String> getModeName();

    boolean isHasZeroGravityConfig(String nluInfo);

    boolean isZeroGravitySupportPosition(List<Integer> positions);

    boolean isAdjustSecondSidePosition(List<Integer> positions);

    boolean isCurSeatPosition(int state,String nluinfo);

    boolean isSupportSeatFold(List<Integer> positions);

    boolean isCanSeatFold(IPropertyOperator operator, List<Integer> positions);

    boolean isBackPosFold(IPropertyOperator operator, List<Integer> positions);

    boolean isBackPosOpend(IPropertyOperator operator, List<Integer> positions);

    boolean isSeatMassageModeNewSpeakType();

    boolean isSupportMassageMode(List<Integer> positions, String massageMode);

    boolean isSupportStopAdjustSeatFold();

    void setFirstRowLeftPosition(HashMap<String, Object> map);

    void setFirstRowRightPosition(HashMap<String, Object> map);

    void setRearSidePosition(HashMap<String, Object> map);

    boolean isSupportRearSideSeatMemory();

    boolean isSpeak37TssText();

    boolean isSpeak56CTssText();

    boolean isSpeak56DTssText();
}
