package com.voyah.ai.logic.dc.manager;

import android.content.Context;

import com.voice.sdk.device.carservice.dc.Devices;
import com.voyah.ai.logic.dc.AdasControlImpl;
import com.voyah.ai.logic.dc.AirControlImpl;
import com.voyah.ai.logic.dc.AtmosphereControlImpl;
import com.voyah.ai.logic.dc.CarSettingControlImpl;
import com.voyah.ai.logic.dc.ChargeportControlImpl;
import com.voyah.ai.logic.dc.DmsControlImpl;
import com.voyah.ai.logic.dc.DoorControlImpl;
import com.voyah.ai.logic.dc.ElectricTailControlImpl;
import com.voyah.ai.logic.dc.FragranceControlImpl;
import com.voyah.ai.logic.dc.FuelportControlImpl;
import com.voyah.ai.logic.dc.HudControlImpl;
import com.voyah.ai.logic.dc.InLightControlImpl;
import com.voyah.ai.logic.dc.LlmControlImpl;
import com.voyah.ai.logic.dc.OmsControlImp;
import com.voyah.ai.logic.dc.OtherControlControlImpl;
import com.voyah.ai.logic.dc.OutLightControlImpl;
import com.voyah.ai.logic.dc.ReadingLightControlImpl;
import com.voyah.ai.logic.dc.RearviewMirrorControlImpl;
import com.voyah.ai.logic.dc.RefrigeratorControlImpl;
import com.voyah.ai.logic.dc.RemoteControlControlImpl;
import com.voyah.ai.logic.dc.ScreenControlImpl;
import com.voyah.ai.logic.dc.SeatControlImpl;
import com.voyah.ai.logic.dc.SteeringWheelControlImpl;
import com.voyah.ai.logic.dc.SunShadeControlImpl;
import com.voyah.ai.logic.dc.SunroofControlImpl;
import com.voyah.ai.logic.dc.SuspensionControlImpl;
import com.voyah.ai.logic.dc.SystemControlControlImpl;
import com.voyah.ai.logic.dc.SystemSettingControlImpl;
import com.voyah.ai.logic.dc.TableControlImpl;
import com.voyah.ai.logic.dc.TailGateControlImpl;
import com.voyah.ai.logic.dc.VehicleConditionControlImpl;
import com.voyah.ai.logic.dc.WindowControlImpl;
import com.voyah.ai.logic.dc.WindowSunshadeControlImpl;
import com.voyah.ai.logic.dc.dvr.CmsControlImpl;
import com.voyah.ai.logic.dc.dvr.DvrControlImpl;

import java.util.Map;
import java.util.TreeMap;

public class DevicesIntentManager extends BaseIntent{

    public DevicesIntentManager() {
        init();
    }

    protected void init() {
        map.put("atmosphere",new AtmosphereControlImpl());
        map.put("door",new DoorControlImpl());
        map.put("outLight",new OutLightControlImpl());
        map.put("remoteControl",new RemoteControlControlImpl());
        map.put("fuelport",new FuelportControlImpl());
        map.put("systemControl",new SystemControlControlImpl());
        map.put("screen",new ScreenControlImpl());
        map.put("air",new AirControlImpl());
        map.put("dms",new DmsControlImpl());
        map.put("oms",new OmsControlImp());
        map.put("cms",new CmsControlImpl());
        map.put("electricTail",new ElectricTailControlImpl());
        map.put("sunShade",new SunShadeControlImpl());
        map.put("suspension",new SuspensionControlImpl());
        map.put("otherControl",new OtherControlControlImpl());
        map.put("table",new TableControlImpl());
        map.put("dvr",new DvrControlImpl());
        map.put("steeringWheel",new SteeringWheelControlImpl());
        map.put("chargeport",new ChargeportControlImpl());
        map.put("vehicleCondition",new VehicleConditionControlImpl());
        map.put("rearviewMirror",new RearviewMirrorControlImpl());
        map.put("carSetting",new CarSettingControlImpl());
        map.put("systemSetting",new SystemSettingControlImpl());
        map.put("seat",new SeatControlImpl());
        map.put("fragrance",new FragranceControlImpl());
        map.put("inLight",new InLightControlImpl());
        map.put("hud",new HudControlImpl());
        map.put("sunroof",new SunroofControlImpl());
        map.put("adas",new AdasControlImpl());
        map.put("window",new WindowControlImpl());
        map.put("readingLight",new ReadingLightControlImpl());
        map.put("refrigerator",new RefrigeratorControlImpl());
        map.put("tailGate",new TailGateControlImpl());
        map.put("Llm",new LlmControlImpl());
        map.put("windowSunshade",new WindowSunshadeControlImpl());
    }
}
