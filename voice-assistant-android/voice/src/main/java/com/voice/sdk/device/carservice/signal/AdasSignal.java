package com.voice.sdk.device.carservice.signal;

/**
 * @Date 2024/9/6 16:27
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class AdasSignal {

    public static final String ADAS_SUPPORT_OPEN_APP = "adas_SupportApp"; //adas信号
    public static final String ADAS_CDC_CONFIG = "adas_CdcConfig"; //连续可调阻尼悬架
    public static final String ADAS_CHASSIS_STYLE = "adas_ChassisStyle"; //底盘风格(1->comfort,2->standard,3->sport)
    public static final String ADAS_FCW_SWITCH = "adas_FcwSwitch"; //前向碰撞预警开关[0,1]
    public static final String ADAS_FCW_SENSITIVITY = "adas_FcwSensitivity"; //前向碰撞预警灵敏度(1->low,2->mid,3->high)
    public static final String ADAS_AEB_SWITCH = "adas_AebSwitch"; //自动紧急制动[0,1]
    public static final String ADAS_RCW_SWITCH = "adas_RcwSwitch"; //后向碰撞预警[0,1]
    public static final String ADAS_MEB_SWITCH = "adas_MebSwitch"; //低速紧急制动[0,1]

    public static final String ADAS_LDA_STATE = "adas_LdaState"; //车道偏离辅助(0->off,1->warning_only,2->warning_assist)
    public static final String ADAS_LDA_WARNING_STYLE = "adas_LdaWarningStyle"; //车道偏离辅助报警方式（0->声音、1->震动）
    public static final String ADAS_ELK_SWITCH = "adas_ElkSwitch"; //紧急车道保持[0,1]
    public static final String ADAS_ESA_SWITCH = "adas_EsaSwitch"; //紧急转向辅助开关[0,1]
    public static final String ADAS_LCA_CONFIG = "adas_LcaConfig"; //变道辅助预警配置
    public static final String ADAS_LCA_SWITCH = "adas_LcaSwitch"; //变道辅助预警、并线辅助预警[0,1]
    public static final String ADAS_LCA_SENSITIVITY = "adas_LcaSensitivity"; //变道辅助预警灵敏度(1->low,2->high)
    public static final String ADAS_FCTA_CONFIG = "adas_FctaConfig"; //前方交通穿行预警配置
    public static final String ADAS_FCTA_SWITCH = "adas_FctaSwitch"; //前方交通穿行开关[0,1]
    public static final String ADAS_RCTA_SWITCH = "adas_RctaSwitch"; //后方交通穿行开关[0,1]
    public static final String ADAS_DOW_SWITCH = "adas_DowSwitch"; //开门预警[0,1]
    public static final String ADAS_FVSR_CONFIG = "adas_FvsrConfig"; //前车起步提醒配置
    public static final String ADAS_FVSR_SWITCH = "adas_FvsrSwitch"; //前车起步提醒[0,1]
    public static final String ADAS_TLC_SWITCH = "adas_TlcSwitch"; //触发式变道辅助[0,1]
    public static final String ADAS_BSD_SWITCH = "adas_BsdSwitch"; //盲区监测预警[0,1]
    public static final String ADAS_TSR_SWITCH = "adas_TsrSwitch"; //交通标志识别[0,1]
    public static final String ADAS_ASSIST_DRIVE_CONFIG = "adas_AssistDriveConfig"; //智能行车风格配置 （37支持，56不支持）
    public static final String ADAS_ASSIST_DRV_STYLE = "adas_AssistDriveStyle"; //智能行车风格（0->standard,1->comfort,2->efficiency）
    public static final String ADAS_NOA_CONFIG = "adas_NoaConfig"; //NOA配置
    public static final String ADAS_NOA_SWITCH = "adas_NoaSwitch"; //NOA开关[0,1]
    public static final String ADAS_ALCO_CONFIG = "adas_AlcoConfig"; //自动变道配置
    public static final String ADAS_ALCO_SWITCH = "adas_AlcoSwitch"; //自动变道开关[0,1]
    public static final String ADAS_LCOC_SWITCH = "adas_LcocSwitch"; //变道前确认[0,1]
    public static final String ADAS_EOL_SWITCH = "adas_EolSwitch"; //主动驶出超车道[0,1]
    public static final String ADAS_ISA_SWITCH = "adas_IsaSwitch"; //智能限速提醒[0,1]
    public static final String ADAS_OWA_CONFIG = "adas_OwaConfig"; //超速报警配置
    public static final String ADAS_OWA_SWITCH = "adas_OwaSwitch"; //超速报警提示[0,1]
    public static final String ADAS_ISLC_SWITCH = "adas_IslcSwitch"; //智能限速控制[0,1]
    public static final String ADAS_TDA4STATE = "adas_Tda4State"; //智驾包权益状态，后面拼上对应的功能简写比如 adas_Tda4StateNOA
    public static final String ADAS_NOA_DISPSTS = "adas_NoaDispsts"; //NOA的状态
    public static final String ADAS_ICA_DISPSTS = "adas_IcaDispsts"; //ICA的状态
    public static final String ADAS_ACTIVATE_NOA = "adas_ActivateNoa"; //激活NOA
    public static final String ADAS_WARD = "adas_Ward"; //执行变道
    public static final String ADAS_WARD_IS_EXEC = "adas_WardIsExec"; //是否是执行变道功能，还有可能是提示手动操作
    public static final String ADAS_CUR_WORKSHOP_TIME_INTERVAL = "adas_CurWorkshopTimeInterval"; //获取当前跟车距离挡位
    public static final String ADAS_WORKSHOP_IS_EXEC = "adas_WorkshopIsExec"; //车间距是否是功能执行
    public static final String ADAS_CRUISESPEED = "adas_CruiseSpeed"; //巡航车速
    public static final String ADAS_APA = "adas_Apa"; //自动泊车
    public static final String ADAS_HPP = "adas_Hpp"; //记忆泊车
    public static final String ADAS_RAD = "adas_Rad"; //寻迹倒车
    public static final String ADAS_AVM = "adas_Avm"; //360全景影像
    public static final String ADAS_CAR_TRANSPARENT = "adas_CarTransparent"; //透明底盘
    public static final String ADAS_AVM_VIEW = "adas_AvmView"; //360 2D/3D 视图
    public static final String ADAS_SUPPORT_NOA_ACTIVATE_CONFIG = "adas_SupportNoaActivateConfig";
    public static final String ADAS_FUNC_SUPPORT = "adas_FuncSupport"; //支持的功能列表 默认支持，各车型维护一个黑名单，命中兜底回复
    public static final String ADAS_SUPPORT_RDA_CONFIG = "adas_SupportRdaConfig"; //是否支持操作寻迹倒车
    public static final String ADAS_NEED_MEMBERSHIP_PRIVILEGES = "adas_NeedMembershipPrivileges"; //是否需要判断商城权益字段
    public static final String ADAS_NOA_ACTIVATE_STATE = "adas_NoaActivateState"; //激活NOA的状态
    public static final String ADAS_NOA_ACTIVATE_HINT_HAND_MOVEMENT = "adas_NoaActivateHintHandMovement"; //激活NOA是否提示手动
    public static final String ADAS_LANE_DEPARTURE_GEAR_INQUIRY_GET = "adas_isGearInquiryGet";//车道偏离灵敏度挡位 get
    public static final String ADAS_LANE_DEPARTURE_GEAR_INQUIRY_SET = "adas_isGearInquirySet";//车道偏离灵敏度挡位 set
    public static final String ADAS_AUTO_EMERGENCY_STOP_BACK_GET = "adas_AutoEmergencyStopBackGet";//自动(后向)紧急制动 get
    public static final String ADAS_AUTO_EMERGENCY_STOP_BACK_SET = "adas_AutoEmergencyStopBackSet";//自动(后向)紧急制动 set
    public static final String ADAS_AUTO_EMERGENCY_STEERING_GET = "adas_AutoEmergencySteeringGet";//自动紧急转向辅助 get
    public static final String ADAS_AUTO_EMERGENCY_STEERING_SET = "adas_AutoEmergencySteeringSet";//自动紧急转向辅助 set
    public static final String ADAS_TRAFFIC_LIGHT_GET = "adas_TrafficLightGet";//红绿灯提醒辅助 get
    public static final String ADAS_TRAFFIC_LIGHT_SET = "adas_TrafficLightSet";//红绿灯提醒辅助 set
    public static final String ADAS_OVER_SPEED_TARGET_GET = "adas_OverSpeedTargetGet";//超速报警提醒报警方式 get
    public static final String ADAS_OVER_SPEED_TARGET_SET = "adas_OverSpeedTargetSet";//超速报警提醒报警方式 set
    public static final String ADAS_LIGHTS_AND_SOUND_GET = "adas_LightsAndSoundGet";//闪灯鸣笛提醒 get
    public static final String ADAS_LIGHTS_AND_SOUND_SET = "adas_LightsAndSoundSet";//闪灯鸣笛提醒 set
    public static final String ADAS_LIGHTS_AND_SOUND_TARGET_GET = "adas_LightsAndSoundTargetGet";//闪灯鸣笛提醒方式 get
    public static final String ADAS_LIGHTS_AND_SOUND_TARGET_SET = "adas_LightsAndSoundTargetSet";//闪灯鸣笛提醒方式 set
    public static final String ADAS_BLIND_SPOT_MONITORING_GET = "adas_BlindSpotMonitoringGet";//盲区监测报警方式 get
    public static final String ADAS_BLIND_SPOT_MONITORING_SET = "adas_BlindSpotMonitoringSet";//盲区监测报警方式 set
    public static final String ADAS_NEED_SAFETY_ASSESSMENT = "adas_NeedSafetyAssessment"; //是否需要安全判断
}
