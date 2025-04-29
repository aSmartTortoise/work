package com.voice.sdk.device.carservice.dc;

import java.util.ArrayList;

public interface ReadingLightInterface {

    ArrayList<Integer> getAllPositions();

    boolean isAllCarTTS(ArrayList<Integer> positions);
}
