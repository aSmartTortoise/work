package com.voice.sdk.device.carservice.constants;


import androidx.annotation.IntDef;

@IntDef(value = {GearInfo.CARSET_GEAR_PARKING,
        GearInfo.CARSET_GEAR_REVERSE,
        GearInfo.CARSET_GEAR_NEUTRAL,
        GearInfo.CARSET_GEAR_DRIVING,
        GearInfo.CARSET_GEAR_SPORT})
public @interface GearInfo{
    int CARSET_GEAR_PARKING = 0; //P挡
    int CARSET_GEAR_REVERSE = 1;
    int CARSET_GEAR_NEUTRAL = 2;
    int CARSET_GEAR_DRIVING = 3;
    int CARSET_GEAR_SPORT = 4;
}
