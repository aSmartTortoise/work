package com.voyah.ai.voice.agent.navi;

import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.Domain;
import com.voice.sdk.device.carservice.constants.GearInfo;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.DeviceUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ds.common.entity.context.FlowContextKey;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNaviAgent extends BaseAgentX {

    private CountDownLatch countDownLatch = null;

    private static final String TAG = "AbstractNaviAgent";

    abstract public boolean isNeedNaviInFront();

    abstract public boolean isNeedNavigationStarted();

    public ClientAgentResponse clientAgentResponse = null;

    public boolean isNeedNavigationLogin() {
        return false;
    }

    boolean isSyncAgent() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    abstract public ClientAgentResponse executeSyncNaviAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap);

    public ClientAgentResponse executeAsyncNaviAgent(Map<String, Object> flowContext) {
        LogUtils.i(TAG, "executeAsyncNaviAgent:" + clientAgentResponse);
        countDownLatch = new CountDownLatch(1);
        try {
            LogUtils.i(TAG, "countDownLatch await");
            boolean ret = countDownLatch.await(3000, TimeUnit.SECONDS);
            if (!ret) {
                LogUtils.i(TAG, "countDownLatch error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.i(TAG, "countDownLatch end:" + GsonUtils.toJson(clientAgentResponse));
        if (clientAgentResponse == null) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
        } else {
            return clientAgentResponse;
        }
    }

    @SuppressWarnings("unused")
    public void asyncNaviAgentDone() {
        LogUtils.i(TAG, "asyncNaviAgentDone");
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public boolean isNeedShowToast() {
        return true;
    }

    public boolean isSupportRGear() {
        return false;
    }

    public boolean isNaviChooseAgent() {
        return "navi#choose".equals(getAgentName());
    }


    @Override
    public ClientAgentResponse executeAgent(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.CENTRAL_SCREEN, "");
        Object nluInfo = flowContext.get(FlowContextKey.FC_NLU_RESULT);
        Object state = flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_SCENARIO_STATE);
        Object sessionId = flowContext.get(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SESSION_ID);
        if (sessionId != null) {
            DeviceHolder.INS().getDevices().getNavi().getNaviStash().setSessionId((String) sessionId);
        }
        LogUtils.i(TAG, "sessionId:" + sessionId + ",state:" + state + ",nlu:" + GsonUtils.toJson(nluInfo) + ",paramsMap:" + GsonUtils.toJson(paramsMap));
        if (DeviceUtils.isVoyahDevice()) {
            if (!DeviceHolder.INS().getDevices().getNavi().isServiceReady()) {
                DeviceHolder.INS().getDevices().getNavi().init();
                return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
            }
            if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isActivation()) {
                DeviceHolder.INS().getDevices().getNavi().openNaviApp();
                return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3016000));
            }
            if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isAgreeDisclaimer()) {
                DeviceHolder.INS().getDevices().getNavi().openNaviApp();
                return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3014800));
            }
            if (isNeedNaviInFront()) {
                if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInFront()) {
                    LogUtils.i(TAG, "return because navi not in front");
                    DeviceHolder.INS().getDevices().getNavi().openNaviApp();
                }
            }
            if (isNeedNavigationStarted()) {
                if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                    LogUtils.i(TAG, "return because navi not start navigation");
                    return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004600));
                }
            }
            if (isNeedNavigationLogin()) {
                if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isLogin()) {
                    LogUtils.i(TAG, "return because navi not login");
                    DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviLoginPage(true);
                    return new ClientAgentResponse(NaviResponseCode.UN_LOGIN.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                }
            }
            if (!isSupportRGear() && DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation()) {

                    return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, "出于安全考虑，当前场景不支持此操作。");
            }
            if (isNeedShowToast()) {
                if (DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
                    String screenName = getParamKey(paramsMap, FuncConstants.KEY_SCREEN_NAME, 0);
                    if (TextUtils.isEmpty(screenName)) {
                        int location = BaseAgentX.getAwakenLocation(flowContext);
                        if (location != 0) {
                            DeviceHolder.INS().getDevices().getSystem().getUi().showSystemToast(DeviceScreenType.fromValue(getScreenFromSound(location)), "地图仅支持在中控屏执行");
                        }
                    }
                }
            }
        }
        if (isSyncAgent()) {
            return executeSyncNaviAgent(flowContext, paramsMap);
        } else {
            return executeAsyncNaviAgent(flowContext);
        }
    }

    public String getScreenFromSound(int location) {
        String screenName;
        switch (location) {
            case 0:
                screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                break;
            case 1:
                screenName = FuncConstants.VALUE_SCREEN_PASSENGER;
                break;
            default:
                screenName = FuncConstants.VALUE_SCREEN_CEIL;
                break;
        }
        return screenName;
    }

}
