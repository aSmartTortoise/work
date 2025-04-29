package com.voyah.ai.device.voyah.h37.dc.utils;

import com.voice.sdk.device.carservice.dc.ReadingLightInterface;

import java.util.ArrayList;

public class ReadingLightImpl implements ReadingLightInterface {

    @Override
    public ArrayList<Integer> getAllPositions() {
        ArrayList<Integer> positionList = new ArrayList<>();
        positionList.add(0);
        positionList.add(1);
        positionList.add(2);
        positionList.add(3);
        positionList.add(4);
        positionList.add(5);
        return positionList;
    }

    @Override
    public boolean isAllCarTTS(ArrayList<Integer> positions) {
        return positions.size() > 4;
    }

}
