package com.voyah.ai.basecar.viewcmd.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.collection.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.tts.BeanTtsInterface;
import com.voice.sdk.device.viewcmd.AccessibleAbilityInterface;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voyah.ai.basecar.R;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.viewcmd.ViewCmdStrategy;
import com.voyah.ai.basecar.viewcmd.accessibility.app.BaseApp;
import com.voyah.ai.basecar.viewcmd.accessibility.app.BiliApp;
import com.voyah.ai.basecar.viewcmd.accessibility.app.KtvApp;
import com.voyah.ai.basecar.viewcmd.accessibility.app.VoHiCoiApp;
import com.voice.sdk.device.viewcmd.SplitScreenId;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.sdk.bean.DhDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 无障碍服务能力
 */
public class AccessibleAbility implements AccessibleAbilityInterface {
    public static final String TAG = AccessibleAbility.class.getSimpleName();

    private VoiceAccessibilityService mService;
    private List<String> mScanViewIdtList = new ArrayList<>(); //扫描的viewId集
    private List<String> mScanTextList = new ArrayList<>(); //扫描的字符串集
    private ViewCmdAppAdapter mViewCmdAppAdapter;
    private List<String> mTempAppList;
    private AccessibilityNodeInfo mFocusNode;
    private AccessibilityNodeInfo mWidgetNode;
    // 屏幕宽高
    private final int mScreenWidth = ScreenUtils.getScreenWidth();
    private final int mScreenHeight = ScreenUtils.getScreenHeight();

    @Override
    public void init() {
        initCallback();
        initViewCmdAdapter();
    }

    private void initCallback() {
        VoiceAccessibilityService.mCallback = new VoiceAccessibilityService.AccessCallback() {
            @Override
            public void setAccessibilityService(VoiceAccessibilityService service) {
                mService = service;
                setInterestedAppList();
                getWidgetAccessibilityNode();
            }

            @Override
            public void handAccessibilityEvent(int displayId) {
                AccessibleAbility.this.handAccessibilityEvent(displayId);
            }
        };
    }

    private void getWidgetAccessibilityNode() {
        ThreadUtils.getCpuPool().execute(() -> {
            int mainDisplayId = MegaDisplayHelper.getMainScreenDisplayId();
            if (ApplicationConstant.PKG_LAUNCHER.equals(CommonSystemUtils.getTopPackageName(mainDisplayId))) {
                mWidgetNode = getWidgetAccessibilityNodeInfo(mainDisplayId);
                if (mWidgetNode != null) {
                    DeviceHolder.INS().getDevices().getViewCmd().triggerViewCmdScan(Utils.getApp().getPackageName(), mainDisplayId, false);
                }
            }
        });
    }

    private void initViewCmdAdapter() {
        // 添加无障碍要进行可即可说的特殊处理应用
        ArrayMap<String, BaseApp> appMap = new ArrayMap<>();
        appMap.put(KtvApp.PACKAGE_NAME, new KtvApp());
        appMap.put(BiliApp.PACKAGE_NAME, new BiliApp());
        appMap.put(VoHiCoiApp.PACKAGE_NAME, new VoHiCoiApp());
        mViewCmdAppAdapter = new ViewCmdAppAdapter(appMap);
    }

    /**
     * 触发无障碍扫描
     */
    @Override
    public void handAccessibilityEvent(int displayId) {
        LogUtils.d("handAccessibilityEvent,displayId=" + displayId);

        if (mService == null) {
            LogUtils.e("mService is null ,so return");
            return;
        }
        ThreadUtils.getCpuPool().execute(() -> mFocusNode = scanUiTextLocked(displayId));
    }

    @Override
    public boolean isAccessibilityApp(String pkgName) {
        if (mTempAppList == null) {
            String json = ResourceUtils.readAssets2String("accessibility/cn.json");
            mTempAppList = JSON.parseArray(json, String.class);
        }
        return mTempAppList.contains(pkgName);
    }

    /**
     * 模拟点击某个文字
     *
     * @param displayId 屏幕id
     * @param text      文字
     */
    private AccessResponse performViewClickByText(int displayId, String text) {
        LogUtils.d("performViewClickByText() called with: displayId:" + displayId + ", text:" + text);
        if (mService == null) {
            return AccessResponse.SERVER_ERR;
        }
        if (mFocusNode == null) {
            return AccessResponse.NO_RESULT;
        }
        List<AccessibilityNodeInfo> nodeInfos = mService.findAll(mFocusNode, AbstractTF.newText(text));
        if (nodeInfos == null || nodeInfos.size() == 0) {
            LogUtils.d("performViewClickByText look for webText");
            nodeInfos = mService.findAll(mFocusNode, AbstractTF.newWebText(text));
        }
        if (nodeInfos == null || nodeInfos.size() == 0) {
            return AccessResponse.NO_RESULT;
        } else {
            if (mViewCmdAppAdapter != null) {
                nodeInfos = mViewCmdAppAdapter.selectStrategy(displayId, text, nodeInfos);
            }
            if (nodeInfos == null || nodeInfos.size() == 0) {
                return AccessResponse.NO_RESULT;
            } else if (nodeInfos.size() > 1) {
                return AccessResponse.MUL_RESULT;
            } else {
                AccessibilityNodeInfo node = nodeInfos.get(0);
                Rect outRect = new Rect();
                node.getBoundsInScreen(outRect);
                int x = outRect.centerX() - 70;
                int y = outRect.centerY() - 70;
                ClickRippleEffect.show(Utils.getApp(), displayId, x, y, () -> mService.clickView(displayId, node));
                return AccessResponse.NORMAL;
            }
        }
    }

    /**
     * 模拟点击某个文字
     *
     * @param displayId 屏幕id
     * @param text      文字
     */
    private AccessResponse performViewClickByContentDescription(int displayId, String text) {
        LogUtils.d("performViewClickByContentDescription() called with: displayId:" + displayId + ", text:" + text);
        if (mService == null) {
            return AccessResponse.SERVER_ERR;
        }
        if (mFocusNode == null) {
            return AccessResponse.NO_RESULT;
        }
        List<AccessibilityNodeInfo> nodeInfos = mService.findAll(mFocusNode, AbstractTF.newContentDescription(text));
        if (nodeInfos == null || nodeInfos.size() == 0) {
            return AccessResponse.NO_RESULT;
        } else {
            if (mViewCmdAppAdapter != null) {
                nodeInfos = mViewCmdAppAdapter.selectStrategy(displayId, text, nodeInfos);
            }
            if (nodeInfos == null || nodeInfos.size() == 0) {
                return AccessResponse.NO_RESULT;
            } else if (nodeInfos.size() > 1) {
                return AccessResponse.MUL_RESULT;
            } else {
                AccessibilityNodeInfo node = nodeInfos.get(0);
                Rect outRect = new Rect();
                node.getBoundsInScreen(outRect);
                int x = outRect.centerX() - 70;
                int y = outRect.centerY() - 70;
                ClickRippleEffect.show(Utils.getApp(), displayId, x, y, () -> mService.clickView(displayId, node));
                return AccessResponse.NORMAL;
            }
        }
    }

    /**
     * 根据text获取fullName
     */
    public String getFullNameByText(String text) {
        if (mViewCmdAppAdapter != null) {
            return mViewCmdAppAdapter.fullNameMap.get(text);
        } else {
            return null;
        }
    }

    /**
     * 模拟点击某个id
     *
     * @param displayId  屏幕id
     * @param text       指令
     * @param idFullName id全称:com.android.xxx:id/tv_main
     */
    private AccessResponse performViewClickById(int displayId, String text, String idFullName) {
        LogUtils.d("performViewClickById() called with: idFullName = [" + idFullName + "]");
        if (mService == null) {
            return AccessResponse.SERVER_ERR;
        }
        if (mFocusNode == null) {
            return AccessResponse.NO_RESULT;
        }
        List<AccessibilityNodeInfo> nodeInfos = mService.findAll(mFocusNode, AbstractTF.newId(idFullName));
        if (nodeInfos == null || nodeInfos.size() == 0) {
            return AccessResponse.NO_RESULT;
        } else {
            if (mViewCmdAppAdapter != null) {
                nodeInfos = mViewCmdAppAdapter.selectStrategy(displayId, text, nodeInfos);
            }
            if (nodeInfos == null || nodeInfos.size() == 0) {
                return AccessResponse.NO_RESULT;
            } else if (nodeInfos.size() > 1) {
                return AccessResponse.MUL_RESULT;
            } else {
                AccessibilityNodeInfo node = nodeInfos.get(0);
                Rect outRect = new Rect();
                node.getBoundsInScreen(outRect);
                int x = outRect.centerX() - 70;
                int y = outRect.centerY() - 70;
                ClickRippleEffect.show(Utils.getApp(), displayId, x, y, () -> mService.clickView(displayId, node));
                return AccessResponse.NORMAL;
            }
        }
    }

    /**
     * 模拟手势
     *
     * @param displayId 屏幕id
     * @param gesture   手势方向
     */
    public AccessResponse performGesture(int displayId, String gesture) {
        LogUtils.d(TAG, "performGesture() called with: displayId = [" + displayId + "], gesture = [" + gesture + "]");
        if (mService == null) {
            return AccessResponse.SERVER_ERR;
        }
        Path path = new Path();

        if (gesture.contains(ViewCmdGesture.UP.direct())) { //上滑
            path.moveTo(mScreenWidth / 2f, mScreenHeight / 3f);
            path.lineTo(mScreenWidth / 2f, mScreenHeight / 2f);
        } else if (gesture.contains(ViewCmdGesture.DOWN.direct())) { //下滑
            path.moveTo(mScreenWidth / 2f, mScreenHeight * 2 / 3f);
            path.lineTo(mScreenWidth / 2f, mScreenHeight / 2f);
        } else if (gesture.contains(ViewCmdGesture.LEFT.direct())) { //左滑
            path.moveTo(mScreenWidth / 2f, mScreenHeight / 2f);
            path.lineTo(mScreenWidth * 2 / 3f, mScreenHeight / 2f);
        } else if (gesture.contains(ViewCmdGesture.RIGHT.direct())) { //右滑
            path.moveTo(mScreenWidth * 2 / 3f, mScreenHeight / 2f);
            path.lineTo(mScreenWidth / 2f, mScreenHeight / 2f);
        }
        mService.dispatchGestureMove(displayId, path, 300);
        return AccessResponse.NORMAL;
    }

    /**
     * 模拟点击某个文字
     *
     * @param displayId 屏幕id
     * @param text      文字
     */
    private AccessResponse performWidgetViewClickByText(int displayId, String text) {
        LogUtils.d("performWidgetViewClickByText() called with: displayId:" + displayId + ", text:" + text);
        if (mService == null) {
            return AccessResponse.SERVER_ERR;
        }
        if (mWidgetNode == null) {
            return AccessResponse.NO_RESULT;
        }
        List<AccessibilityNodeInfo> nodeInfos = mService.findAll(mWidgetNode, AbstractTF.newWidgetText(text));
        if (nodeInfos == null || nodeInfos.size() == 0) {
            return AccessResponse.NO_RESULT;
        } else {
            if (nodeInfos.size() > 1) {
                return AccessResponse.MUL_RESULT;
            } else {
                AccessibilityNodeInfo node = nodeInfos.get(0);
                Rect outRect = new Rect();
                node.getBoundsInScreen(outRect);
                int x = outRect.centerX() - 70;
                int y = outRect.centerY() - 70;
                ClickRippleEffect.show(Utils.getApp(), displayId, x, y, () -> mService.clickView(displayId, node));
                return AccessResponse.NORMAL;
            }
        }
    }

    /**
     * 扫描界面所有的文字
     */
    private void scanFocusUIText(AccessibilityNodeInfo root, List<String> viewIdList, List<String> textList) {
        if (mService == null) {
            LogUtils.e("accessibility is no available");
            return;
        }

        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child == null) {
                continue;
            }
            int childCount = child.getChildCount();
            boolean visibleToUser = child.isVisibleToUser();
            String resId = child.getViewIdResourceName();
            if (visibleToUser && resId != null && !viewIdList.contains(resId)) {
                viewIdList.add(resId);
            }
            if (childCount == 0 && child.getText() != null && visibleToUser) {
                String text = child.getText().toString().trim();
                if (!TextUtils.isEmpty(text) && !textList.contains(text)) {
                    textList.add(text);
                }
            } else {
                scanFocusUIText(child, viewIdList, textList);
            }
        }
    }

    /**
     * 扫描界面所有的文字
     */
    private void scanFocusContentDescription(AccessibilityNodeInfo root, List<String> viewIdList, List<String> textList) {
        if (mService == null) {
            LogUtils.e("accessibility is no available");
            return;
        }

        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child == null) {
                continue;
            }
            boolean visibleToUser = child.isVisibleToUser();
            String resId = child.getViewIdResourceName();
            if (visibleToUser && resId != null && !viewIdList.contains(resId)) {
                viewIdList.add(resId);
            }
            if (visibleToUser && child.getContentDescription() != null) {
                String text = child.getContentDescription().toString().trim();
                if (!TextUtils.isEmpty(text) && !textList.contains(text)) {
                    textList.add(text);
                }
            } else {
                scanFocusContentDescription(child, viewIdList, textList);
            }
        }
    }

    /**
     * 辅助功能是否启动
     */
    @Override
    public boolean isAccessibilityServiceRunning() {
        return mService != null; //这样简单判断即可
    }


    /**
     * 无障碍服务开关
     */
    @Override
    public void enableAccessibilityService(boolean enable) {
        LogUtils.d("enableAccessibilityService() called with: enable = [" + enable + "]");
        if (enable) {
            CommonSystemUtils.startAccessibilityService();
        } else {
            if (mService != null) {
                mService.disableSelf();
            }
            clear();
        }
    }


    /**
     * 设置使用无障碍可见的app列表
     */
    public void setInterestedAppList() {
        if (mService == null) {
            LogUtils.e("accessibility is no available");
            return;
        }

        if (mTempAppList == null) {
            String json = ResourceUtils.readAssets2String("accessibility/cn.json");
            mTempAppList = JSON.parseArray(json, String.class);
        }
        LogUtils.d("setInterestedAppList() called with: list = [" + mTempAppList + "]");
        if (mService != null) {
            AccessibilityServiceInfo serviceInfo = mService.getServiceInfo();
            if (serviceInfo != null) {
//                serviceInfo.packageNames = mTempAppList.toArray(new String[0]);
                mService.setServiceInfo(serviceInfo);
            }
        }
    }

    /**
     * 分屏扫描
     */
    private synchronized AccessibilityNodeInfo scanUiTextLocked(int displayId) {
        LogUtils.w("scanUiTextLocked() called, start...");
        AccessibilityNodeInfo accessibilityNodeInfo = getAccessibilityNodeInfo(displayId);
        if (accessibilityNodeInfo == null) {
            LogUtils.e("current focus node is null!!!");
            return null;
        }

        String pkg = accessibilityNodeInfo.getPackageName().toString();

        if (!isAccessibilityApp(pkg)) {
            LogUtils.e("is filter app ,so return, pkg:" + pkg);
            return null;
        }

        if (DeviceHolder.INS().getDevices().getViewCmd().isShowingTopCoverView(pkg, displayId)) {
            LogUtils.e("top cover view showing ,so return");
            return null;
        }

        List<String> viewIdList = new ArrayList<>();
        List<String> textList = new ArrayList<>();
        if (mViewCmdAppAdapter.getViewCmdStrategy(pkg) == ViewCmdStrategy.UI_TEXT) {
            scanFocusUIText(accessibilityNodeInfo, viewIdList, textList);
        } else {
            scanFocusContentDescription(accessibilityNodeInfo, viewIdList, textList);
            scanFocusUIText(accessibilityNodeInfo, viewIdList, textList);
        }

        LogUtils.v("scanUiTextLocked Scan texts: " + textList);
        mScanViewIdtList = viewIdList;
        mScanTextList = textList;
        //针对应用的特殊处理
        ArrayList<String> tempScanTextList = new ArrayList<>(mScanTextList);
        if (!TextUtils.isEmpty(pkg) && mViewCmdAppAdapter != null) {
            List<String> appendList = mViewCmdAppAdapter.handle(displayId, pkg, tempScanTextList, viewIdList);
            if (appendList != null && appendList.size() > 0) {
                LogUtils.d("appendList:" + appendList);
                for (String str : appendList) {
                    if (!tempScanTextList.contains(str)) {
                        tempScanTextList.add(str);
                    }
                }
            }
        }
        int splitScreenId = SplitScreenId.FULL_SCREEN;
        if (DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSplitScreening()) {
            splitScreenId = SplitScreenId.RIGHT_SCREEN;
        }
        DeviceHolder.INS().getDevices().getViewCmd().addViewCommands(TAG, displayId, splitScreenId, tempScanTextList);
        LogUtils.w("scanUiTextLocked() called, end...");
        return accessibilityNodeInfo;
    }

    private AccessibilityNodeInfo getAccessibilityNodeInfo(int display) {
        if (mService == null) {
            LogUtils.e("accessibility is no available");
            return null;
        }
        SparseArray<List<AccessibilityWindowInfo>> windowsOnAllDisplays = mService.getWindowsOnAllDisplays();
        int size = windowsOnAllDisplays.size();
        if (size == 0) {
            LogUtils.d("windowsOnAllDisplays.size() == 0");
            return null;
        }

        for (int i = 0; i < windowsOnAllDisplays.size(); i++) {
            List<AccessibilityWindowInfo> accessibilityWindowInfos = windowsOnAllDisplays.get(i);
            if (accessibilityWindowInfos == null || accessibilityWindowInfos.isEmpty()) {
                continue;
            }

            for (AccessibilityWindowInfo info : accessibilityWindowInfos) {
                int displayId = info.getDisplayId();
                if (displayId == display) {
                    if (info.isFocused()) {
                        if (info.getChildCount() == 0) {
                            return info.getRoot();
                        } else {
                            int childCount = info.getChildCount();
                            for (int j = 0; j < childCount; j++) {
                                AccessibilityWindowInfo child = info.getChild(j);
                                if (child.getChildCount() == 0) {
                                    return child.getRoot();
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public AccessibilityNodeInfo getWidgetAccessibilityNodeInfo(int display) {
        if (mService == null) {
            LogUtils.e("accessibility is no available");
            return null;
        }
        SparseArray<List<AccessibilityWindowInfo>> windowsOnAllDisplays = mService.getWindowsOnAllDisplays();
        int size = windowsOnAllDisplays.size();
        if (size == 0) {
            LogUtils.d("windowsOnAllDisplays.size() == 0");
            return null;
        }
        List<String> viewIdList = new ArrayList<>();
        List<String> textList = new ArrayList<>();
        for (int i = 0; i < windowsOnAllDisplays.size(); i++) {
            List<AccessibilityWindowInfo> accessibilityWindowInfos = windowsOnAllDisplays.get(i);
            if (accessibilityWindowInfos == null || accessibilityWindowInfos.isEmpty()) {
                continue;
            }

            for (AccessibilityWindowInfo info : accessibilityWindowInfos) {
                int displayId = info.getDisplayId();
                if (displayId == display) {
                    AccessibilityNodeInfo root = info.getRoot();
                    if (root != null && TextUtils.equals(ApplicationConstant.PKG_LAUNCHER, root.getPackageName())) {
                        viewIdList.clear();
                        textList.clear();
                        scanFocusUIText(root, viewIdList, textList);
                        LogUtils.d("textList:" + textList);
                        if (textList.size() > 0 && !containsTimeAndDate(textList)) {
                            LogUtils.d("match widget node");
                            return root;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static boolean containsTimeAndDate(List<String> stringList) {
        String timePattern = "^\\d{1,2}:\\d{2}$"; // 匹配 "15:20"
        String datePattern = "^\\d{1,2}月\\d{1,2}日 周[一二三四五六日]$"; // 匹配  "3月7日 周五"

        Pattern timeRegex = Pattern.compile(timePattern);
        Pattern dateRegex = Pattern.compile(datePattern);
        boolean hasTime = false;
        boolean hasDate = false;

        for (String str : stringList) {
            if (timeRegex.matcher(str).matches()) {
                hasTime = true;
            }
            if (dateRegex.matcher(str).matches()) {
                hasDate = true;
            }
            if (hasTime && hasDate) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean handleViewCommand(int displayId, @DhDirection int direction, String text) {
        AccessResponse response = AccessResponse.NO_RESULT;
        String fullName = getFullNameByText(text);
        BeanTtsInterface tts = DeviceHolder.INS().getDevices().getTts();
        if (fullName != null) {
            if (fullName.contains(ResponseType.GESTURE.type())) {
                response = performGesture(displayId, fullName);
            } else if (fullName.contains(ResponseType.ID.type())) {
                response = performViewClickById(displayId, text, fullName);
            } else if (fullName.contains(ResponseType.TEXT.type())) {
                text = fullName.substring(fullName.indexOf("/") + 1);
            }
            LogUtils.d("handleViewCommand fullName:" + fullName + ", response:" + response);
            if (response == AccessResponse.NORMAL) {
                tts.speak(TtsReplyUtils.getViewCmdReply(), direction, null);
                return true;
            } else if (response == AccessResponse.MUL_RESULT) {
                tts.speak(Utils.getApp().getString(R.string.viewCmd_execute_multi_select), direction, null);
                return true;
            }
        }
        if (response == AccessResponse.NO_RESULT) {
            String packageName = CommonSystemUtils.getTopPackageName(displayId);
            if (mViewCmdAppAdapter.getViewCmdStrategy(packageName) == ViewCmdStrategy.CONTENT_DESCRIPTION) {
                response = performViewClickByContentDescription(displayId, text);
                if (response == AccessResponse.NO_RESULT) {
                    response = performViewClickByText(displayId, text);
                }
            } else {
                response = performViewClickByText(displayId, text);
            }
            LogUtils.d("handleViewCommand text:" + text + ", response:" + response);
            if (response == AccessResponse.NORMAL) {
                tts.speak(TtsReplyUtils.getViewCmdReply(), direction, null);
                return true;
            } else if (response == AccessResponse.MUL_RESULT) {
                tts.speak(Utils.getApp().getString(R.string.viewCmd_execute_multi_select), direction, null);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getWidgetUiText(int display, List<String> excludeTexts) {
        if (mService == null) {
            LogUtils.e("accessibility is no available");
            return null;
        }
        if (mWidgetNode == null) {
            mWidgetNode = getWidgetAccessibilityNodeInfo(display);
        }
        if (mWidgetNode == null) {
            LogUtils.e("mWidgetNode is null");
            return null;
        }

        List<String> viewIdList = new ArrayList<>();
        List<String> textList = new ArrayList<>();
        scanFocusUIText(mWidgetNode, viewIdList, textList);
        textList.removeAll(excludeTexts);
        return textList;
    }

    @Override
    public boolean handleWidgetViewCommand(int displayId, @DhDirection int direction, String text) {
        AccessResponse response = performWidgetViewClickByText(displayId, text);
        LogUtils.d("handleWidgetViewCommand text:" + text + ", response:" + response);
        if (response == AccessResponse.NORMAL) {
            DeviceHolder.INS().getDevices().getTts().speak(TtsReplyUtils.getViewCmdReply(), direction, null);
            return true;
        } else if (response == AccessResponse.MUL_RESULT) {
            DeviceHolder.INS().getDevices().getTts().speak(Utils.getApp().getString(R.string.viewCmd_execute_multi_select), direction, null);
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        mScanViewIdtList.clear();
        mScanTextList.clear();
        if (mViewCmdAppAdapter != null) {
            mViewCmdAppAdapter.fullNameMap.clear();
        }
        mFocusNode = null;
    }

    enum AccessResponse {
        SERVER_ERR(-1, "accessibility is no available"),
        NORMAL(0, "accessibility handle and response"),
        MUL_RESULT(2, "accessibility find multi results"),
        NO_RESULT(3, "accessibility no find results");

        public final int code;
        public final String message;


        AccessResponse(int value, String message) {
            this.code = value;
            this.message = message;
        }

        @Override
        public String toString() {
            return "AccessResponse{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}

