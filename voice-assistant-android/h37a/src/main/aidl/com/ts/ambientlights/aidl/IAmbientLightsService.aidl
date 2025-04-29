package com.ts.ambientlights.aidl;

import com.ts.ambientlights.aidl.ILightsSettingListener;

interface IAmbientLightsService {
    boolean Set_dynamicLightsStatus_ARS(boolean var1);

    boolean Set_lightsStatus_ARS(boolean var1);

    boolean Set_lightsBrightness_ARS(int var1);

    boolean Set_lightsColour_ARS(int var1);

    boolean Set_drivingModeStatus_ARS(boolean var1);

    int Get_lightsBrightness_ARS();

    int Get_lightsColour_ARS();

    boolean Get_lightsStatus_ARS();

    int Get_lightsMode_ARS();

    boolean Set_voiceAssistantFeedbackStatus_ARS(boolean var1);

    boolean Set_voiceSpeakStatus_ARS(boolean var1);

    boolean Set_colourSelectType_ARS(int var1);

    int Get_colourSelectType_ARS();

    boolean registerCallback(ILightsSettingListener var1);

    boolean unregisterCallback(ILightsSettingListener var1);

    boolean Set_gameModeStatus_ARS(boolean var1);

    boolean Set_gameMode_ARS(int var1);

    int Get_gameMode_ARS();

    boolean Set_dynamicRhythmReq(boolean var1);

    boolean Set_lightsMode_ARS(int var1);

    boolean Set_themeColor_ARS(int var1);

    int Get_themeColor_ARS();

    boolean Set_musicRhythm_ARS(int var1);

    int Get_musicRhythm_ARS();

    int Get_StaticOrDynamic_ARS();

    int Get_StaticMode_ARS();

    int Get_DynamicMode_ARS();

    boolean Set_SceneLightStatus_ARS(int var1);

    int Get_SceneLightStatus_ARS();

    boolean Set_ClothLinkageStatus_ARS(boolean var1);

    int Get_ClothLinkageStatus_ARS();

    boolean Set_ClothLinkagePriority_ARS(int var1);

    int Get_ClothLinkagePriority_ARS();

    int Get_lightsBrightnessDynamic_ARS();

    boolean Set_lightsBrightnessDynamic_ARS(int var1);

    int Get_staticMultipleColor_ARS();

    boolean Set_staticMultipleColor_ARS(int var1);
}