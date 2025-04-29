package com.voyah.ai.basecar.navi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.mega.map.IRemoteAssistantInterface;
import com.mega.map.IRemoteAssistantListener;
import com.mega.map.assistant.data.ActionCallback;
import com.mega.map.assistant.data.ActionParams;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviInterface;
import com.voice.sdk.device.navi.NaviMapInterface;
import com.voice.sdk.device.navi.NaviScreenInterface;
import com.voice.sdk.device.navi.NaviSettingInterface;
import com.voice.sdk.device.navi.NaviStatusInterface;
import com.voice.sdk.device.navi.NaviTeamInterface;
import com.voice.sdk.device.navi.NaviViaPointInterface;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.NaviStashInterface;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.device.navi.bean.UploadPoi;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractNaviInterfaceImpl implements NaviInterface {
    private static final String TAG = "AbstractNaviInterfaceImpl";

    public static final int DEFAULT_WAIT_TIME = 3000;

    public static final int LONG_WAIT_TIME = 20000;

    private IRemoteAssistantInterface remoteAssistantInterface = null;
    private final Map<String, CompletableFuture<ActionCallback>> completableFutureMap = new ConcurrentHashMap<>();

    protected final NaviStatusInterfaceImpl naviStatusInterface = new NaviStatusInterfaceImpl(this);

    protected final NaviPoiInterfaceImpl naviPoiInterface = new NaviPoiInterfaceImpl(this);

    protected final NaviTeamInterfaceImpl naviTeamInterface = new NaviTeamInterfaceImpl(this);

    protected final NaviMapInterfaceImpl naviMapInterface = new NaviMapInterfaceImpl(this);

    protected final NaviViaPointInterfaceImpl naviViaPointInterface = new NaviViaPointInterfaceImpl(this);

    protected final NaviSettingInterfaceImpl naviSettingInterface = new NaviSettingInterfaceImpl(this);

    protected final NaviScreenInterfaceImpl naviScreenInterface = new NaviScreenInterfaceImpl();

    protected final NaviStashInterfaceImpl naviStashInterface = new NaviStashInterfaceImpl();

    private final List<Poi> lastPoiList = new ArrayList<>();

    private final Thread checkBinderThread = new Thread(() -> {
        while (true) {
            SystemClock.sleep(2000);
            if (isServiceReady()) {
                uploadPoiList();
                SystemClock.sleep(5000);
            } else {
                init();
            }
        }
    });


    public boolean isSamePoi(List<Poi> list1, List<Poi> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if (StringUtils.compare(list1.get(i).getName(), list2.get(i).getName()) != 0) {
                return false;
            }
        }
        return true;
    }

    private void uploadPoiList() {
        NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getHistoryPoiList(0);
        if (naviResponse != null && naviResponse.isSuccess() && naviResponse.getData() != null && !naviResponse.getData().isEmpty()) {
            List<Poi> poiList = naviResponse.getData();
            poiList.sort((o1, o2) -> StringUtils.compare(o1.getName(), o2.getName()));
            if (isSamePoi(lastPoiList, poiList)) {
                LogUtils.i(TAG, "same poi list no need upload");
            } else {
                lastPoiList.clear();
                lastPoiList.addAll(poiList);
                List<UploadPoi> listUpload = new ArrayList<>();
                for (int i = 0; i < Math.min(lastPoiList.size(), 20); i++) {
                    Poi poi = lastPoiList.get(i);
                    listUpload.add(new UploadPoi(poi.getName(), poi.getLat(), poi.getLon()));
                }
                listUpload.add(new UploadPoi("补能", 0, 0));
                try {
                    JSONObject jsonObject = getJsonObject(listUpload);
                    LogUtils.i(TAG, "jsonObject:" + jsonObject);
                    DeviceHolder.INS().getDevices().getFeedback().uploadPersonalEntity("com.voyah.ai.voice", jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private static @NonNull JSONObject getJsonObject(List<UploadPoi> listUpload) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("function", "naviPoiHistory");
        JSONArray jsonArray = new JSONArray();
        int i = 0;
        for (UploadPoi uploadPoi : listUpload) {
            JSONObject curJsonObject = new JSONObject();
            curJsonObject.put("name", uploadPoi.getName());
            curJsonObject.put("lat", uploadPoi.getLat());
            curJsonObject.put("lon", uploadPoi.getLon());
            jsonArray.put(i, curJsonObject);
            i++;
        }
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }


    private final IRemoteAssistantListener remoteAssistantListener = new IRemoteAssistantListener.Stub() {
        @Override
        public void onGetNaviInfoResult(ActionCallback actionCallback) {
            LogUtils.i(TAG, "getMapRequest:sessionId = " + (actionCallback != null ? actionCallback.getSessionId() : null));
            LogUtils.i(TAG, "getMapRequest:" + GsonUtils.toJson(actionCallback));
            if (actionCallback != null) {
                String sessionId = actionCallback.getSessionId();
                if (completableFutureMap.containsKey(sessionId)) {
                    LogUtils.i(TAG, "need callback:" + sessionId);
                    CompletableFuture<ActionCallback> completableFuture = completableFutureMap.get(sessionId);
                    completableFutureMap.remove(sessionId);
                    if (completableFuture != null) {
                        LogUtils.i(TAG, "need put response");
                        completableFuture.complete(actionCallback);
                    }
                }
            }
        }

        @Override
        public void onGetSceneWordResult(ActionCallback actionCallback) {

        }

        @Override
        public void onGetNaviStatusReport(String s) {
            naviStatusInterface.parseNaviStatusReport(s);
        }
    };

    private final IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            LogUtils.e(TAG, "binder died remoteAssistantInterface:" + remoteAssistantInterface);
            if (remoteAssistantInterface != null) {
                remoteAssistantInterface.asBinder().unlinkToDeath(deathRecipient, 0);
                remoteAssistantInterface = null;
            }
            init();
        }
    };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.i(TAG, "onServiceConnected:" + name);
            remoteAssistantInterface = IRemoteAssistantInterface.Stub.asInterface(service);
            try {
                remoteAssistantInterface.addRemoteAssistantListener(remoteAssistantListener);
                try {
                    if (remoteAssistantInterface != null) {
                        IBinder iBinder = remoteAssistantInterface.asBinder();
                        iBinder.linkToDeath(deathRecipient, 0);
                    } else {
                        LogUtils.e(TAG, "linkToDeath error, mRemoteAssistantInterface null");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.e(TAG, "onServiceDisconnected:" + name);
        }
    };


    @Override
    public void init() {
        LogUtils.i(TAG, "bindService");
        if (!isServiceReady()) {
            int count = 0;
            Intent intent = new Intent();
            intent.setPackage("com.mega.map");
            intent.setAction("com.mega.map.assistant.AssistantService");
            while (count < 3) {
                boolean result = ContextUtils.getAppContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
                LogUtils.i(TAG, "bindService:" + result + ",count:" + count);
                if (result) {
                    break;
                } else {
                    count++;
                    SystemClock.sleep(2000);
                }
            }
        }
        if (!checkBinderThread.isAlive()) {
            LogUtils.i(TAG, "checkBinderThread start");
            checkBinderThread.start();
        }
    }

    @Override
    public boolean isServiceReady() {
        boolean isServiceReady = (remoteAssistantInterface != null && remoteAssistantInterface.asBinder() != null && remoteAssistantInterface.asBinder().isBinderAlive());
        LogUtils.i(TAG, "isServiceReady:" + isServiceReady + ",remoteAssistantInterface:" + remoteAssistantInterface);
        return isServiceReady;
    }

    protected void sendRequest(ActionParams params) {
        sendRequest(params, false);
    }

    protected ActionCallback sendRequest(ActionParams params, boolean needResponse) {
        return sendRequest(params, needResponse, DEFAULT_WAIT_TIME);
    }

    protected ActionCallback sendRequest(ActionParams params, boolean needResponse, int maxWaitTime) {
        params.setSessionId(params.getSessionId() + "_" + Utils.generateRandomString(8));
        LogUtils.d(TAG, "sendMapRequest:" + params + ",needResponse:" + needResponse + ",maxWaitTime:" + maxWaitTime);
        if (remoteAssistantInterface != null) {
            try {
                CompletableFuture<ActionCallback> completableFuture = null;
                if (needResponse) {
                    completableFuture = new CompletableFuture<>();
                    completableFutureMap.put(params.getSessionId(), completableFuture);
                    params.setSessionId(params.getSessionId());
                }
                remoteAssistantInterface.optOperationNaviInfo(params);
                if (needResponse) {
                    ActionCallback actionCallback = completableFuture.get(maxWaitTime, TimeUnit.MILLISECONDS);
                    LogUtils.d(TAG, "waited  for response:" + actionCallback);
                    return actionCallback;
                }
                return null;

            } catch (TimeoutException e) {
                LogUtils.i(TAG, "time out exception:" + params.getSessionId());
                completableFutureMap.remove(params.getSessionId());
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                LogUtils.e(TAG, "RemoteException:" + e);
                e.printStackTrace();
                init();
                return null;
            }
        } else {
            LogUtils.e(TAG, "remoteAssistantInterface is null");
            init();
            return null;
        }
    }

    @Override
    public NaviStatusInterface getNaviStatus() {
        return naviStatusInterface;
    }


    @Override
    public NaviMapInterface getNaviMap() {
        return naviMapInterface;
    }

    @Override
    public NaviTeamInterface getNaviTeam() {
        return naviTeamInterface;
    }

    @Override
    public NaviSettingInterface getNaviSetting() {
        return naviSettingInterface;
    }

    @Override
    public NaviPoiInterfaceImpl getNaviPoi() {
        return naviPoiInterface;
    }

    @Override
    public NaviViaPointInterface getNaviViaPoint() {
        return naviViaPointInterface;
    }

    @Override
    public NaviScreenInterface getNaviScreen() {
        return naviScreenInterface;
    }

    @Override
    public NaviStashInterface getNaviStash() {
        return naviStashInterface;
    }
}
