package com.voyah.vcos.virtualdevice.param.intent;


import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/6/20 10:19
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class SeatControlParams extends BaseParams{
    @Override
    public void exe() {

        mContentProviderHelper.addParams("seatHeatLevel", ParamsType.INT, 3, 0, 1, "座椅加热挡位" );
        mContentProviderHelper.addParams("seatHeatLevel_1", ParamsType.INT, 3, 0, 1, "座椅加热挡位" );

        mContentProviderHelper.addParams("seatVentLevel", ParamsType.INT, 3, 0, 1, "座椅通风挡位" );
        mContentProviderHelper.addParams("seatVentLevel_1", ParamsType.INT, 3, 0, 1, "座椅通风挡位" );


        mContentProviderHelper.addParams("seatMassageLevel", ParamsType.INT, 3, 0, 1, "座椅按摩挡位" );
        mContentProviderHelper.addParams("seatMassageMode", ParamsType.STRING, "", "", "wave", "座椅按摩模式" );
        mContentProviderHelper.addParams("seatMassageLevel_1", ParamsType.INT, 3, 0, 1, "座椅按摩挡位" );
        mContentProviderHelper.addParams("seatMassageMode_1", ParamsType.STRING, "", "", "wave", "座椅按摩模式" );
        //主驾
        mContentProviderHelper.addParams("seatAdjustPosition", ParamsType.INT, 100, 0, 50, "座椅前后调节" );
        mContentProviderHelper.addParams("seatAdjustBack", ParamsType.INT, 100, 0, 50, "座椅靠背调节" );
        mContentProviderHelper.addParams("seatAdjustCushionHeight", ParamsType.INT, 100, 0, 50, "座椅高度调节" );
        mContentProviderHelper.addParams("seatAdjustCushionAngle", ParamsType.INT, 100, 0, 50, "坐垫角度调节" );
        //副驾
        mContentProviderHelper.addParams("seatAdjustPosition_1", ParamsType.INT, 100, 0, 50, "座椅前后调节" );
        mContentProviderHelper.addParams("seatAdjustBack_1", ParamsType.INT, 100, 0, 50, "座椅靠背调节" );
        mContentProviderHelper.addParams("seatAdjustCushionHeight_1", ParamsType.INT, 100, 0, 50, "座椅高度调节" );
        mContentProviderHelper.addParams("seatAdjustCushionAngle_1", ParamsType.INT, 100, 0, 50, "坐垫角度调节" );

        mContentProviderHelper.addParams("seatOccupiedStatus", ParamsType.BOOLEAN, "", "", true, "座位上是否有人" );
        mContentProviderHelper.addParams("seatOccupiedStatus_1", ParamsType.BOOLEAN, "", "", false, "座位上是否有人" );
        mContentProviderHelper.addParams("seatOccupiedStatus_2", ParamsType.BOOLEAN, "", "", false, "座位上是否有人" );
        mContentProviderHelper.addParams("seatOccupiedStatus_3", ParamsType.BOOLEAN, "", "", false, "座位上是否有人" );

        mContentProviderHelper.addParams("seatMemorySave", ParamsType.STRING, "", "", "[{\"seatAdjustPosition\":50,\"seatAdjustBack\":50,\"seatAdjustCushionAngle\":50,\"seatAdjustCushionHeight\":50},{\"seatAdjustPosition\":60,\"seatAdjustBack\":60,\"seatAdjustCushionAngle\":60,\"seatAdjustCushionHeight\":60},{\"seatAdjustPosition\":66,\"seatAdjustBack\":66,\"seatAdjustCushionAngle\":66,\"seatAdjustCushionHeight\":66}]", "座椅数据json" );
        mContentProviderHelper.addParams("seatMemorySave_1", ParamsType.STRING, "", "", "[{\"seatAdjustPosition\":50,\"seatAdjustBack\":50,\"seatAdjustCushionAngle\":50,\"seatAdjustCushionHeight\":50},{\"seatAdjustPosition\":60,\"seatAdjustBack\":60,\"seatAdjustCushionAngle\":60,\"seatAdjustCushionHeight\":60},{\"seatAdjustPosition\":66,\"seatAdjustBack\":66,\"seatAdjustCushionAngle\":66,\"seatAdjustCushionHeight\":66}]", "座椅数据json" );

        mContentProviderHelper.addParams("seatWelcomeModeSwitch", ParamsType.BOOLEAN, "", "", false, "座椅迎宾模式开关" );
        mContentProviderHelper.addParams("babySeatCount", ParamsType.INT , 2, 0, 0, "儿童座椅连接数" );
        mContentProviderHelper.addParams("babySeatHeatSwitch", ParamsType.BOOLEAN, "", "", false, "儿童座椅加热开关" );
        mContentProviderHelper.addParams("babySeatVentSwitch", ParamsType.BOOLEAN, "", "", false, "儿童座椅通风开关" );

        mContentProviderHelper.addParams("carStateDriving", ParamsType.BOOLEAN, "", "", false, "抽象的信号，是否在驾驶中，实车捆绑档位和车速，这里抽象出来，和挡位车速无关" );

    }
}
