package com.voyah.ai.basecar.navi;

import com.mega.map.assistant.data.ActionCallback;
import com.mega.map.assistant.data.ActionParams;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviTeamInterface;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ds.common.tool.GsonUtils;

import java.util.HashMap;
import java.util.Map;

public class NaviTeamInterfaceImpl implements NaviTeamInterface {

    private static final String TAG = "NaviTeamInterfaceImpl";

    private final AbstractNaviInterfaceImpl abstractNaviInterface;


    public NaviTeamInterfaceImpl(AbstractNaviInterfaceImpl abstractNaviInterface) {
        this.abstractNaviInterface = abstractNaviInterface;
    }


    @Override
    public NaviResponse<Integer> getTeamInfo() {
        LogUtils.i(TAG, "getTeamInfo");
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("reqCarTeamStatus");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        params.setParams(GsonUtils.toJson((map)));
        ActionCallback actionCallback = this.abstractNaviInterface.sendRequest(params, true);
        NaviResponse<Integer> naviResponse = new NaviResponse<>(actionCallback != null ? actionCallback.getResult() : null);
        if (naviResponse.isSuccess() && naviResponse.getJsonObject() != null) {
            int teamInfo = naviResponse.getJsonObject().optInt("result");
            naviResponse.setData(teamInfo);
        }
        return naviResponse;
    }

    @Override
    public void createTeam() {
        LogUtils.i(TAG, "createTeam");
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optCarTeam");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("type", 3);
        params.setParams(GsonUtils.toJson(map));
        this.abstractNaviInterface.sendRequest(params);
    }

    @Override
    public void joinTeam() {
        LogUtils.i(TAG, "joinTeam");
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optCarTeam");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("type", 2);
        params.setParams(GsonUtils.toJson(map));
        this.abstractNaviInterface.sendRequest(params);
    }

    @Override
    public void viewTeam() {
        LogUtils.i(TAG, "viewTeam");
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optCarTeam");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("type", 1);
        params.setParams(GsonUtils.toJson(map));
        this.abstractNaviInterface.sendRequest(params);
    }

    @Override
    public void disbandTeam() {
        LogUtils.i(TAG, "disbandTeam");
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optCarTeam");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("type", 0);
        params.setParams(GsonUtils.toJson(map));
        this.abstractNaviInterface.sendRequest(params);
    }

    @Override
    public void exitTeam() {
        LogUtils.i(TAG, "exitTeam");
        Map<String, Object> map = new HashMap<>();
        ActionParams params = new ActionParams();
        params.setSessionId(NaviConstants.SESSION_COMMON_OPT);
        params.setActionType("optCarTeam");
        map.put("actionType", params.getActionType());
        map.put("protocol", 2);
        map.put("type", 0);
        params.setParams(GsonUtils.toJson(map));
        this.abstractNaviInterface.sendRequest(params);
    }

}
