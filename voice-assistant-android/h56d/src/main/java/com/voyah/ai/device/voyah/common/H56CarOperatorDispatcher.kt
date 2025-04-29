package com.voyah.ai.device.voyah.common



import com.voice.sdk.device.carservice.vcar.BaseOperatorDispatcher
import com.voyah.ai.device.voyah.common.H56Car.dc.AdasPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.AirPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.AtmospherePropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.Base56Operator
import com.voyah.ai.device.voyah.common.H56Car.dc.CarSettingPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.DmsPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.DoorControlPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.FragrancePropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.FuelportPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.HudPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.OmsPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.OtherPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.OutLightPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.ReadingLightPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.RearviewMirrorPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.RefrigeratorPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.ScreenPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.SeatPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.SteeringWheelPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.SunShadePropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.SunroofPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.SuspensionControlPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.SysCtrlPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.SystemSettingPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.TablePropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.TailGatePropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.VehicleConditionPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.WindowPropertyOperator
import com.voyah.ai.device.voyah.common.H56Car.dc.WindowSunshadePropertyOperator


object H56CarOperatorDispatcher : BaseOperatorDispatcher() {
    override fun init() {
        propertyOperatorMap["table"] = TablePropertyOperator()
        propertyOperatorMap["steeringwheel"] = SteeringWheelPropertyOperator()
        propertyOperatorMap["other"] = OtherPropertyOperator()
        propertyOperatorMap["fragrance"] = FragrancePropertyOperator()
        propertyOperatorMap["fuelport"] = FuelportPropertyOperator()
        propertyOperatorMap["carsetting"] = CarSettingPropertyOperator()
        propertyOperatorMap["dms"] = DmsPropertyOperator()
        propertyOperatorMap["outlight"] = OutLightPropertyOperator()
        propertyOperatorMap["tailgate"] = TailGatePropertyOperator()
        propertyOperatorMap["rearviewmirror"] = RearviewMirrorPropertyOperator()
        propertyOperatorMap["vehiclecondition"] = VehicleConditionPropertyOperator()
        propertyOperatorMap["window"] = WindowPropertyOperator()
        propertyOperatorMap["air"] = AirPropertyOperator()
        propertyOperatorMap["oms"] = OmsPropertyOperator()
        propertyOperatorMap["seat"] = SeatPropertyOperator()
        propertyOperatorMap["systemsetting"] = SystemSettingPropertyOperator()
        propertyOperatorMap["readinglight"] = ReadingLightPropertyOperator()
        propertyOperatorMap["screen"] = ScreenPropertyOperator()
        propertyOperatorMap["sunshade"] = SunShadePropertyOperator()
        propertyOperatorMap["adas"] = AdasPropertyOperator()
        propertyOperatorMap["atmosphere"] = AtmospherePropertyOperator()
        propertyOperatorMap["hud"] = HudPropertyOperator()
        propertyOperatorMap["sunroof"] = SunroofPropertyOperator()
        propertyOperatorMap["sysctrl"] = SysCtrlPropertyOperator()
        propertyOperatorMap["base"] = Base56Operator()
        propertyOperatorMap["RemoteControl"] = Base56Operator()
        propertyOperatorMap["Refrigerator"] = RefrigeratorPropertyOperator()
        propertyOperatorMap["Suspension"] = SuspensionControlPropertyOperator()
        propertyOperatorMap["doorControl"] = DoorControlPropertyOperator()
        propertyOperatorMap["windowSunshade"] = WindowSunshadePropertyOperator()
    }
}
