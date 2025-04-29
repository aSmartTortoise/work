package com.voyah.ai.basecar.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.R;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.base.FeedbackInterface;
import com.voice.sdk.device.viewcmd.ViewCmdCache;
import com.voice.sdk.device.viewcmd.ViewCmdInterface;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.common.utils.SystemUtils;
import com.voice.sdk.device.viewcmd.SplitScreenId;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FeedbackManager implements FeedbackInterface {
    private final ViewCmdInterface viewCmdInterface;
    private final List<String> widgetExcludeTexts = Arrays.asList("去公司", "回家", "洗车模式", "小憩模式");
    private final Map<String, String> viewCmdVerMap = new ConcurrentHashMap<>();

    public void uploadViewCmd(List<String> list, ViewCmdCache globalViewCmdCache, ViewCmdCache kwsViewCmdCache) {
        List<String> hotInfoList = new ArrayList<>(list);
        if (globalViewCmdCache != null) {
            ViewCmdCache.Cache globalCache = globalViewCmdCache.getCache(ViewCmdCache.GLOBAL_ID);
            if (globalCache != null) {
                hotInfoList.addAll(globalCache.list);
            }
        }

        if (DialogueManager.get().isInteractionState()) {
            JSONObject backObj = new JSONObject();
            backObj.put("text", Utils.getApp().getString(R.string.viewcmd_back));
            if (!hotInfoList.contains(backObj.toString())) {
                hotInfoList.add(backObj.toString());
            }
        }

        JSONObject object = new JSONObject();
        object.put("viewCmd.hotInfo", hotInfoList);

        List<String> kwsInfoList = new ArrayList<>();
        if (kwsViewCmdCache != null) {
            ViewCmdCache.Cache kwsCache = kwsViewCmdCache.getCache(ViewCmdCache.KWS_ID);
            if (kwsCache != null) {
                kwsInfoList.addAll(kwsCache.list);
            }
        }
        object.put("viewCmd.kwsInfo", kwsInfoList);

        VoiceImpl.getInstance().uploadViewCommand(object.toString());
    }

    public void uploadPersonalEntity(String pkgName, String json) {
        if (StringUtils.isBlank(json)) {
            LogUtils.e("uploadPersonalEntity json is empty");
            return;
        }
        LogUtils.e("uploadPersonalEntity:" + json);
        JSONObject object = JSON.parseObject(json);
        if (object.containsKey("function")) {
            String function = (String) object.getString("function");
            Object data = object.get("data");
            LogUtils.i("uploadPersonalEntity data:" + data);
            VoiceImpl.getInstance().uploadPersonalEntity(function, data);
        } else {
            LogUtils.e("uploadPersonalEntity function is empty");
        }
    }

    public void onViewContentChange(String pkg, String json) {
        JSONObject object = JSON.parseObject(json);
        JSONArray uiContent = object.getJSONArray("uiContent");
        JSONArray globalUiContent = object.getJSONArray("globalUiContent");
        JSONArray kwsUiContent = object.getJSONArray("kwsUiContent");
        String version = object.getString("version");
        if (version == null) {
            version = "1.0";
        }
        viewCmdVerMap.put(pkg, version);
        String pkgName = pkg;
        if (pkgName.contains("&")) {
            String[] split = pkgName.split("&");
            pkgName = split[0];
        }
        int mainDisplayId = DeviceHolder.INS().getDevices().getSystem().getScreen().getMainScreenDisplayId();
        int displayId = object.getIntValue("displayId");
        if (displayId == -1) {
            displayId = SystemUtils.getDisplayIdByPkg(pkgName);
            if (displayId == -1) {
                LogUtils.w("onViewContentChange can't get valid displayId");
                displayId = mainDisplayId;
            }
        }
        int splitScreenId = object.getIntValue("splitScreenId");
        if (splitScreenId == 0 || displayId != mainDisplayId) {
            splitScreenId = SplitScreenId.FULL_SCREEN;
        }
        if (uiContent != null) {
            List<String> globalList = new ArrayList<>();
            List<String> globalToNormalList = null;
            if (globalUiContent != null) {
                globalList = globalUiContent.toJavaList(String.class);
                if (displayId == mainDisplayId) {
                    viewCmdInterface.setGlobalViewCmd(pkg, globalList);
                } else if (globalList.size() > 0) {
                    globalToNormalList = globalList;
                }
            }
            List<String> kwsList = new ArrayList<>();
            if (kwsUiContent != null) {
                kwsList = kwsUiContent.toJavaList(String.class);
                viewCmdInterface.setKwsViewCmd(pkg, kwsList);
            }
            List<String> list = uiContent.toJavaList(String.class);
            if (globalToNormalList != null) {
                list.addAll(globalToNormalList);
            }
            boolean isFocusable = object.getBooleanValue("isFocusable");
            boolean register = object.getBooleanValue("register");
            if (ApplicationConstant.PKG_LAUNCHER.equals(pkgName)) {
                if (isFocusable && register) {
                    List<String> widgetList = viewCmdInterface.getAccessibleAbility().getWidgetUiText(displayId, widgetExcludeTexts);
                    LogUtils.d("widgetList:" + widgetList);
                    if (widgetList != null && widgetList.size() > 0) {
                        for (String text : widgetList) {
                            JSONObject widgetObj = new JSONObject();
                            widgetObj.put("text", text);
                            widgetObj.put("prompt", "Widget");
                            list.add(widgetObj.toString());
                        }
                    }
                }
            }
            if (register) {
                viewCmdInterface.addViewCommands(pkg, displayId, splitScreenId, list);
            } else {
                viewCmdInterface.removeViewCommands(pkg, displayId, splitScreenId);
            }

            if (list.size() == 0 && globalList.size() == 0 && kwsList.size() == 0) {
                // 给栈顶应用触发一次扫描
                if (!register) {
                    viewCmdInterface.triggerViewCmdScan(pkg, displayId, isFocusable);
                }
            }
        }
    }

    public String getViewCmdVersion(String pkg) {
        return viewCmdVerMap.getOrDefault(pkg, "1.0");
    }

    private static class Holder {
        private static final FeedbackManager _INSTANCE = new FeedbackManager();
    }

    private FeedbackManager() {
        super();
        viewCmdInterface = DeviceHolder.INS().getDevices().getViewCmd();
    }

    public static FeedbackManager get() {
        return FeedbackManager.Holder._INSTANCE;
    }
}
