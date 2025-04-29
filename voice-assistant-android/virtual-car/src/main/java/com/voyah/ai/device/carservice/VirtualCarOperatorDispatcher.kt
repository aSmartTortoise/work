package com.voyah.ai.device.carservice



import com.voice.sdk.device.carservice.vcar.BaseOperatorDispatcher
import com.voyah.ai.virtual.dc.AdasPropertyOperator
import com.voyah.ai.virtual.dc.AirPropertyOperator
import com.voyah.ai.virtual.dc.AtmospherePropertyOperator
import com.voyah.ai.virtual.dc.BaseVirtualPropertyOperator
import com.voyah.ai.virtual.dc.CarSettingPropertyOperator
import com.voyah.ai.virtual.dc.DmsPropertyOperator
import com.voyah.ai.virtual.dc.FragrancePropertyOperator
import com.voyah.ai.virtual.dc.HudPropertyOperator
import com.voyah.ai.virtual.dc.OtherPropertyOperator
import com.voyah.ai.virtual.dc.OutLightPropertyOperator
import com.voyah.ai.virtual.dc.ScreenPropertyOperator
import com.voyah.ai.virtual.dc.SeatPropertyOperator
import com.voyah.ai.virtual.dc.SteeringWheelPropertyOperator
import com.voyah.ai.virtual.dc.SystemSettingPropertyOperator
import com.voyah.ai.virtual.dc.TailGatePropertyOperator


object VirtualCarOperatorDispatcher : BaseOperatorDispatcher() {
    override fun init() {
        propertyOperatorMap["table"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["steeringwheel"] = SteeringWheelPropertyOperator()
        propertyOperatorMap["other"] = OtherPropertyOperator()
        propertyOperatorMap["fragrance"] = FragrancePropertyOperator()
        propertyOperatorMap["fuelport"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["carsetting"] = CarSettingPropertyOperator()
        propertyOperatorMap["dms"] = DmsPropertyOperator()
        propertyOperatorMap["outlight"] = OutLightPropertyOperator()
        propertyOperatorMap["tailgate"] = TailGatePropertyOperator()
        propertyOperatorMap["rearviewmirror"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["vehiclecondition"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["window"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["air"] = AirPropertyOperator()
        propertyOperatorMap["oms"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["seat"] = SeatPropertyOperator()
        propertyOperatorMap["systemsetting"] = SystemSettingPropertyOperator()
        propertyOperatorMap["readinglight"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["screen"] = ScreenPropertyOperator()
        propertyOperatorMap["sunshade"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["adas"] = AdasPropertyOperator()
        propertyOperatorMap["atmosphere"] = AtmospherePropertyOperator()
        propertyOperatorMap["hud"] = HudPropertyOperator()
        propertyOperatorMap["sunroof"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["sysctrl"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["base"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["RemoteControl"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["Refrigerator"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["Suspension"] = BaseVirtualPropertyOperator()
        propertyOperatorMap["doorControl"] = BaseVirtualPropertyOperator()
    }
}
