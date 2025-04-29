package com.ts.ambientlights.aidl;

interface ILightsSettingListener {
    void onLightsStatusChanged(boolean var1);

    void onBrightnessSetting(int var1);

    void onColorNumberSetting(int var1);

    void onLightsModeChanged(int var1);

    void onColorSelectType(int var1);

    void onGameModeChanged(int var1);

    void onThemeColorChanged(int var1);

    void onMusicRhythmChanged(int var1);

    void onClothLinkageStatusChanged(boolean var1);

    void onClothLinkagePriorityChanged(int var1);

    void onSceneLightStatusChanged(int var1);

    void onMusicRhythmBrightnessSetting(int var1);

    void onStaticMultiColorChanged(int var1);
}