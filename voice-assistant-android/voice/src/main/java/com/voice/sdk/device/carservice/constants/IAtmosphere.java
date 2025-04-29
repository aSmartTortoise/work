package com.voice.sdk.device.carservice.constants;

/**
 * @Date 2024/9/6 13:36
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public interface IAtmosphere {

    //NLU 使用
    public interface Mode {
        int MODE_STATIC = 0;
        int MODE_DYNAMIC = 1;
        int MODE_DRIVE = 2;
        int MODE_GAME = 3;
        int MODE_DJ = 4;
        int MODE_THEME = 5;
        int MODE_SINGLE_COLOR = 6;
        int MODE_BREATHE = 10;
        int MODE_FLOW = 11;
        int MODE_TIME = 12;
    }

    //控制器、虚拟车使用
    interface ModeDB {
        int staticSingleColor = 0;
        int staticMultiColor = 9;
        int dynamicSingleColor = 6;
        int dynamicMultiColor = 1;
    }

    interface SingleColor {
        int red = 46;
        int orange = 59;
        int yellow = 72;
        int green = 87;
        int purple = 9;
        int pink = 22;
        int blue = 128;
        int cyan = 102;
    }

    interface MultiColor {
        int cold = 1;
        int warm = 3;
        int neutral = 2;
        int combined = 5;
    }
}
