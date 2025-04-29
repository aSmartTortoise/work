//package com.voyah.ai.voice.model;
//
//
//import com.voice.sdk.VoiceImpl;
//import com.voice.sdk.device.DeviceHolder;
//import com.voice.sdk.device.phone.PhoneInterface;
//import com.voice.sdk.device.ui.UiInterface;
//import com.voice.sdk.device.ui.listener.IUiStateListener;
//import com.voyah.ai.common.utils.LogUtils;
//import com.voyah.ai.voice.receiver.device.cv.CVFactory;
//import com.voyah.ai.voice.receiver.device.cv.CVInterface;
//
///**
// * @author:lcy 模块功能实现统一调用
// * @data:2024/1/21
// **/
//public class MultiModeFunction {
//    private static final String TAG = MultiModeFunction.class.getSimpleName();
//
//    private static MultiModeFunction multiModeFunction = new MultiModeFunction();
//
//    private CVInterface cvInterface;
//    //private DevicesInterface devicesInterface;
//    private UiInterface uiInterface;
//
//    public static MultiModeFunction getInstance() {
//        return multiModeFunction;
//    }
//
//    public void init() {
//        cvInterface = CVFactory.createCV(CVFactory.VCOS_CV1);
////        devicesInterface = DeviceFactory.createDevice(AppContext.instant, DeviceFactory.H37);
////        if (CarServicePropUtils.vehicleSimulatorJudgment()) {
////            //真车
////            devicesInterface = DeviceFactory.createDevice(AppContext.instant, DeviceFactory.H37);
////        } else {
////            //虚拟车,暂时都走真车
////            devicesInterface = DeviceFactory.createDevice(AppContext.instant, DeviceFactory.H37);
////        }
//    }
//
//    public CVInterface getCvInterface() {
//        return cvInterface;
//    }
//
//
//    public PhoneInterface getPhoneInterface() {
//        return DeviceHolder.INS().getDevices().getPhone();
//    }
//
//    public UiInterface getUiInterface() {
//        return uiInterface;
//    }
//
//
//
//
//    private IUiStateListener iUiStateListener = new IUiStateListener() {
//        @Override
//        public void uiCardClose(String sessionId) {
//            LogUtils.i(TAG, "uiCardClose");
//            DeviceHolder.INS().getDevices().getTts().shutUpOneSelf();
//            VoiceImpl.getInstance().exitSessionDialog(sessionId);
//        }
//
//        @Override
//        public void uiModelCardClose(String requestId) {
////            String currentTaskRequestId = VoiceStateRecord.getCurrentTaskRequestId();
////            LogUtils.d(TAG, "uiModelCardClose currentTaskRequestId is " + currentTaskRequestId + " ,requestId is " + requestId);
////            if (StringUtils.equals(currentTaskRequestId, requestId)) {
////                BeanTtsManager.getInstance().stopCurTts();
////                VoiceImpl.getInstance().exitRequest(requestId);
////            }
//            DeviceHolder.INS().getDevices().getTts().shutUpOneSelf();
//            VoiceImpl.getInstance().exitRequest(requestId);
//        }
//
//        @Override
//        public void uiVpaClose() {
//            LogUtils.i(TAG, "uiVpaClose");
//            DeviceHolder.INS().getDevices().getTts().shutUpOneSelf();
//            VoiceImpl.getInstance().exDialog();
//        }
//    };
//}
