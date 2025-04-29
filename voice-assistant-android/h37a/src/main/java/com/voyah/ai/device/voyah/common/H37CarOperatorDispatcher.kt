package com.voyah.ai.device.voyah.common



import com.voice.sdk.device.carservice.vcar.BaseOperatorDispatcher
import com.voyah.ai.device.voyah.common.H37Car.dc.AdasPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.AirPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.AtmospherePropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.Base37Operator
import com.voyah.ai.device.voyah.common.H37Car.dc.CarSettingPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.DmsPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.DoorControlPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.FragrancePropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.FuelportPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.HudPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.OmsPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.OtherPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.OutLightPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.ReadingLightPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.RearviewMirrorPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.RefrigeratorPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.ScreenPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.SeatPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.SteeringWheelPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.SunShadePropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.SunroofPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.SuspensionControlPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.SysCtrlPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.SystemSettingPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.TablePropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.TailGatePropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.VehicleConditionPropertyOperator
import com.voyah.ai.device.voyah.common.H37Car.dc.WindowPropertyOperator


object H37CarOperatorDispatcher : BaseOperatorDispatcher() {
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
        propertyOperatorMap["base"] = Base37Operator()
        propertyOperatorMap["doorControl"] = DoorControlPropertyOperator()
        propertyOperatorMap["Suspension"] = SuspensionControlPropertyOperator()
        propertyOperatorMap["Refrigerator"] = RefrigeratorPropertyOperator()
    }
}
