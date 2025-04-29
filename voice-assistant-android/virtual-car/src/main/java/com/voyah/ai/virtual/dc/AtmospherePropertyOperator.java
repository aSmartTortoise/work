package com.voyah.ai.virtual.dc;

import com.voice.sdk.device.carservice.constants.IAtmosphere;
import com.voice.sdk.device.carservice.signal.AtmosphereSignal;
import com.voyah.ai.common.utils.BiDirectionalMap;

/**
 * @Date 2024/9/13 14:56
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class AtmospherePropertyOperator extends BaseVirtualPropertyOperator {

    private final BiDirectionalMap<Integer, String> modeMap = new BiDirectionalMap<>();

    private final BiDirectionalMap<Integer, String> sinColorMap = new BiDirectionalMap<>();

    private final BiDirectionalMap<Integer, String> multiColorMap = new BiDirectionalMap<>();

    public AtmospherePropertyOperator()  {
        initModeMap();
        initColorMap();
    }

    private void initModeMap() {
        modeMap.put(IAtmosphere.ModeDB.staticSingleColor, "StaticSingleColor");
        modeMap.put(IAtmosphere.ModeDB.staticMultiColor, "StaticMultiColor");
        modeMap.put(IAtmosphere.ModeDB.dynamicSingleColor, "DynamicSingleColor");
        modeMap.put(IAtmosphere.ModeDB.dynamicMultiColor, "DynamicMultiColor");
    }

    private void initColorMap() {
        //单色
        sinColorMap.put(IAtmosphere.SingleColor.red, "红色");
        sinColorMap.put(IAtmosphere.SingleColor.orange, "橙色");
        sinColorMap.put(IAtmosphere.SingleColor.yellow, "黄色");
        sinColorMap.put(IAtmosphere.SingleColor.green, "绿色");
        sinColorMap.put(IAtmosphere.SingleColor.purple, "紫色");
        sinColorMap.put(IAtmosphere.SingleColor.pink, "粉色");
        sinColorMap.put(IAtmosphere.SingleColor.blue, "蓝色");
        sinColorMap.put(IAtmosphere.SingleColor.cyan, "青色");

        //多色
        multiColorMap.put(IAtmosphere.MultiColor.cold, "冷色");
        multiColorMap.put(IAtmosphere.MultiColor.warm, "暖色");
        multiColorMap.put(IAtmosphere.MultiColor.neutral, "中性色");
        multiColorMap.put(IAtmosphere.MultiColor.combined, "组合色");
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case AtmosphereSignal.ATMO_ACTION_MODE:
                String modeDB = (String) getValue(AtmosphereSignal.ATMO_ACTION_MODE);
                return modeMap.getReverse(modeDB);
            case AtmosphereSignal.ATMO_LAST_STATIC_MODE:
                String lastStatic = (String) getValue(AtmosphereSignal.ATMO_LAST_STATIC_MODE);
                return modeMap.getReverse(lastStatic);
            case AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE:
                String lastDynamic = (String) getValue(AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE);
                return modeMap.getReverse(lastDynamic);
            case AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR:
            case AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR:
                String singleColor = (String) getValue(key);
                return sinColorMap.getReverse(singleColor);
            case AtmosphereSignal.ATMO_STATIC_MULTI_COLOR:
            case AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR:
                String multiColor = (String) getValue(key);
                return multiColorMap.getReverse(multiColor);
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case AtmosphereSignal.ATMO_ACTION_MODE:
                String modeDB = modeMap.getForward(value);
                setValue(AtmosphereSignal.ATMO_ACTION_MODE, modeDB);
                if (modeDB.startsWith("Static")) {
                    setValue(AtmosphereSignal.ATMO_LAST_STATIC_MODE, modeDB);
                } else if (modeDB.startsWith("Dynamic")) {
                    setValue(AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE, modeDB);
                }
                break;

            case AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR:
            case AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR:
                String singleColor = sinColorMap.getForward(value);
                setValue(key, singleColor);
                break;
            case AtmosphereSignal.ATMO_STATIC_MULTI_COLOR:
            case AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR:
                String multiColor = multiColorMap.getForward(value);
                setValue(key, multiColor);
                break;
            default:
                super.setBaseIntProp(key, area, value);
                break;
        }
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        if (AtmosphereSignal.ATMO_IS_STATIC.equals(key)) {
            return getBaseStringProp(AtmosphereSignal.ATMO_ACTION_MODE, area).startsWith("static");
        } else {
            return super.getBaseBooleanProp(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        super.setBaseBooleanProp(key, area, value);
    }
}
