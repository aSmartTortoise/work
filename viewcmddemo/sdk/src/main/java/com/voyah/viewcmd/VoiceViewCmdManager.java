package com.voyah.viewcmd;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.ArrayMap;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.voyah.viewcmd.interceptor.IDirectRegisterInterceptor;
import com.voyah.viewcmd.interceptor.IInterceptor;
import com.voyah.viewcmd.Response.ErrCode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.voyah.viewcmd.VcosViewUtil.VCOS_SWITCH;
import static com.voyah.viewcmd.VoiceViewCmdUtils.mCtx;

/**
 * 可见即可说一键式处理类
 */
public class VoiceViewCmdManager {
    private static final String TAG = VoiceViewCmdManager.class.getSimpleName();
    private static final int DELAY_NOTIFY_CHANGED = 500; // 默认开启扫描延时时间

    private CoreProcessor coreProcessor;
    private Handler scanHandler, postHandler;
    private final Map<Object, IInterceptor<?>> supportObjectMap = new LinkedHashMap<>(); //有序
    private final Map<View, ViewAttrs> allViewMap = new LinkedHashMap<>(); //有序

    private final Object lock = new Object();
    private final Map<Object, Integer> retryCounterMap = new ConcurrentHashMap<>();
    private int curDirection;

    private VoiceViewCmdManager() {
        if (coreProcessor == null) {
            init();
        }
    }

    public void init() {
        coreProcessor = new CoreProcessor(new ViewCmdAdapter());
        HandlerThread scanThread = new HandlerThread("t_viewCmd");
        scanThread.start();
        scanHandler = new Handler(scanThread.getLooper());

        HandlerThread postThread = new HandlerThread("t_viewCmd_post");
        postThread.start();
        postHandler = new Handler(postThread.getLooper());
    }

    /**
     * 注册activity方式
     */
    public void register(Activity activity, IInterceptor<?> interceptor) {
        VaLog.d(TAG, "register() called with: activity:" + activity.getClass().getSimpleName());
        // 同一时刻只能存在一个activity（切换主题时会有多个activity onResume）
        // this.supportViewMap.entrySet().removeIf(next -> next.getKey() instanceof Activity);
        synchronized (lock) {
            supportObjectMap.remove(activity);
            supportObjectMap.put(activity, interceptor);
        }
        // 在注册时触发一次扫描(此时拿不到displayId)
        triggerNotifyUiChange();
    }

    /**
     * 注册Fragment方式
     */
    public void register(Fragment fragment, IInterceptor<?> interceptor) {
        if (!(fragment instanceof DialogFragment)) {
            VaLog.d(TAG, "register() called with: fragment:" + fragment.getClass().getSimpleName());
            synchronized (lock) {
                supportObjectMap.remove(fragment);
                supportObjectMap.put(fragment, interceptor);
            }
            // 在注册时触发一次扫描
            triggerNotifyUiChange();
        }
    }

    /**
     * 注册Dialog方式
     */
    public void register(Dialog dialog, boolean isSticky, IInterceptor<?> interceptor) {
        register(dialog, isSticky, false, interceptor);
    }

    public void register(Dialog dialog, boolean isSticky, boolean isTopCoverView, IInterceptor<?> interceptor) {
        Window window = dialog.getWindow();
        if (window != null) {
            View view = window.getDecorView();
            int windowType = VoiceViewCmdUtils.getWindowType(view);
            if (windowType < WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW) {
                Context context = dialog.getContext();
                if (context instanceof ContextThemeWrapper) {
                    Context baseContext = ((ContextThemeWrapper) context).getBaseContext();
                    if (baseContext instanceof Activity) {
                        VoiceViewCmdUtils.setViewAttachActivity(view, (Activity) baseContext);
                    }
                }
            }
            register(view, isSticky, isTopCoverView, interceptor);
        }
    }

    /**
     * 注册DialogFragment方式
     */
    public void register(DialogFragment dialogFragment, boolean isSticky, IInterceptor<?> interceptor) {
        View view = dialogFragment.getView();
        if (view != null) {
            int windowType = VoiceViewCmdUtils.getWindowType(view);
            if (windowType < WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW) {
                Activity attachActivity = dialogFragment.getActivity();
                if (attachActivity != null) {
                    VoiceViewCmdUtils.setViewAttachActivity(view, attachActivity);
                }
            }
            register(view, isSticky, false, interceptor);
        }
    }

    /**
     * 注册DialogFragment方式
     */
    public void register(PopupWindow popupWindow, boolean isSticky, IInterceptor<?> interceptor) {
        View view = popupWindow.getContentView();
        if (view != null) {
            register(view, isSticky, false, interceptor);
        }
    }

    /**
     * 注册view方式
     */
    public void register(View view, IInterceptor<?> interceptor) {
        register(view, false, false, interceptor);
    }

    /**
     * 注册view方式
     */
    public void register(View view, boolean isSticky, boolean isTopCoverView, IInterceptor<?> interceptor) {
        if (VoiceViewCmdUtils.mCtx == null) {
            VaLog.e(TAG, "VoiceViewCmdUtils.mCtx == null");
            return;
        }
        int displayId = VoiceViewCmdUtils.getDisplayId(view);
        if (displayId == -1) {
            VaLog.e(TAG, "register displayId=-1, please check the register time");
            return;
        }
        VaLog.d(TAG, "register() called with: view hashCode:" + view.hashCode() + " ,isSticky:" + isSticky + " ,isTopCoverView:" + isTopCoverView + ",hashCode:" + view.hashCode());
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                view.removeOnAttachStateChangeListener(this);
                unregister(v);
            }
        });
        ViewAttrs viewAttrs = new ViewAttrs(displayId, isTopCoverView, isSticky, false);
        viewAttrs.interceptor = interceptor;
        synchronized (lock) {
            allViewMap.put(view, viewAttrs);
        }
        triggerNotifyUiChangeById(displayId);
        postHandler.post(() -> {
            synchronized (lock) {
                if (!allViewMap.containsKey(view)) {
                    VaLog.w(TAG, "post ignore, view is not exist!");
                    return;
                }
            }
            int windowType = VoiceViewCmdUtils.getWindowType(view);
            VaLog.d(TAG, "windowType:" + windowType);
            if (windowType < WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW) {
                Activity attachActivity = VoiceViewCmdUtils.getViewAttachActivity(view);
                if (attachActivity == null) {
                    Activity activity = null;
                    Map<Object, IInterceptor<?>> newSupportObjectMap;
                    synchronized (lock) {
                        newSupportObjectMap = new LinkedHashMap<>(supportObjectMap);
                    }
                    for (Map.Entry<Object, IInterceptor<?>> entry : newSupportObjectMap.entrySet()) {
                        Object key = entry.getKey();
                        if (key instanceof Activity && VoiceViewCmdUtils.getDisplayId(key) == displayId) {
                            activity = (Activity) key;
                        }
                    }
                    VoiceViewCmdUtils.setViewAttachActivity(view, activity);
                }
            }
            setTopCoverViewShowingIfNeed(displayId, isTopCoverView);
        });
    }

    public void unregister(Activity activity) {
        VaLog.d(TAG, "unregister() called with: activity:" + activity.getClass().getSimpleName());
        synchronized (lock) {
            supportObjectMap.remove(activity);
        }
        postHandler.post(() -> {
            int displayId = VoiceViewCmdUtils.getDisplayId(activity);
            if (VoiceViewCmdUtils.isShowingTopCoverView(displayId)) {
                VaLog.d(TAG, "activity is behind of the top cover view");
            } else {
                register2VA(activity, displayId, null);
            }
        });
    }

    public void unregister(Fragment fragment) {
        if (!(fragment instanceof DialogFragment)) {
            VaLog.d(TAG, "unregister() called with: fragment:" + fragment.getClass().getSimpleName());
            synchronized (lock) {
                supportObjectMap.remove(fragment);
            }
        }
    }

    public void unregister(Dialog dialog) {
        VaLog.d(TAG, "unregister() called with: dialog:" + dialog.getClass().getSimpleName());
        Window window = dialog.getWindow();
        if (window != null) {
            View view = window.getDecorView();
            unregister(view);
        }
    }

    public void unregister(DialogFragment dialogFragment) {
        VaLog.d(TAG, "unregister() called with: dialogFragment:" + dialogFragment.getClass().getSimpleName());
        View view = dialogFragment.getView();
        if (view != null) {
            unregister(view);
        }
    }

    public void unregister(PopupWindow popupWindow) {
        VaLog.d(TAG, "unregister() called with: popupWindow:" + popupWindow.getClass().getSimpleName());
        View view = popupWindow.getContentView();
        if (view != null) {
            unregister(view);
        }
    }

    public void unregister(View view) {
        if (VoiceViewCmdUtils.mCtx == null) {
            VaLog.e(TAG, "VoiceViewCmdUtils.mCtx == null");
            return;
        }
        VaLog.d(TAG, "unregister() called, view hashCode:" + view.hashCode());
        int displayId = getDisplayIdFromMap(view);
        synchronized (lock) {
            allViewMap.remove(view);
        }
        postHandler.post(() -> {
            unsetTopCoverViewShowingIfNeed(displayId);
            Activity activity = VoiceViewCmdUtils.getViewAttachActivity(view);
            if (activity != null) {
                VoiceViewCmdUtils.setViewAttachActivity(view, null);
            }
            register2VA(view, displayId, null);
        });
    }

    private void executeActivityUiChange(@NonNull Activity activity, int displayId, IInterceptor<?>... interceptors) {
        VaLog.v(TAG, "executeActivityUiChange() called with: displayId:" + displayId + ", activity:" + activity);
        VisibleResults visibleResults = null;
        synchronized (lock) {
            if (!supportObjectMap.containsKey(activity)) {
                VaLog.w(TAG, activity.getClass().getSimpleName() + " isn't exist, triggerNotifyUiChange gain！");
                triggerNotifyUiChangeById(VoiceViewCmdUtils.getDisplayId(activity));
                return;
            }
        }
        VaLog.d(TAG, "scanning activity...");
        if (!VoiceViewCmdUtils.isShowingTopCoverView(displayId)) {
            View rootView = activity.getWindow().getDecorView().getRootView();
            visibleResults = coreProcessor.getAllVisibleText(rootView, interceptors);
        } else {
            VaLog.d(TAG, "invalid activity, it's under of top cover view");
        }

        if (visibleResults != null) {
            register2VA(activity, displayId, visibleResults);
        }
    }

    private void executeViewUiChange(@NonNull View view, ViewAttrs viewAttrs) {
        VaLog.v(TAG, "executeViewUiChange() called with: view hashCode:" + view.hashCode() + ", viewAttrs:" + viewAttrs);
        synchronized (lock) {
            if (!allViewMap.containsKey(view)) {
                VaLog.w(TAG, "view hashCode:" + view.hashCode() + " isn't exist, triggerNotifyUiChange gain！");
                triggerNotifyUiChangeById(viewAttrs.displayId);
                return;
            }
        }
        Map<Object, IInterceptor<?>> newSupportObjectMap;
        synchronized (lock) {
            newSupportObjectMap = new LinkedHashMap<>(supportObjectMap);
        }
        if (VoiceViewCmdUtils.isNoFocusDialogView(newSupportObjectMap, view)) {
            VaLog.w(TAG, "dialog has lost window focus and no upload, view:" + view.hashCode());
            return;
        }
        int displayId = viewAttrs.displayId;
        if (viewAttrs.isDirect) {
            JSONArray uploadArray = new JSONArray();
            for (String key : viewAttrs.directDataMap.keySet()) {
                uploadArray.put(key);
            }
            register2VA(view, displayId, new VisibleResults(uploadArray, null, null));
        } else if (!viewAttrs.isSticky) {
            VisibleResults visibleResults = coreProcessor.getAllVisibleText(view, viewAttrs.interceptor);
            if (visibleResults != null) {
                register2VA(view, displayId, visibleResults);
            }
        } else {
            VisibleResults allVisibleResults = new VisibleResults(new JSONArray(), new JSONArray(), new JSONArray());
            VisibleResults stickyVisibleResults = coreProcessor.getAllVisibleText(view, viewAttrs.interceptor);
            allVisibleResults.visibleTexts = stickyVisibleResults.visibleTexts;
            allVisibleResults.globalVisibleTexts = stickyVisibleResults.globalVisibleTexts;
            allVisibleResults.kwsVisibleTexts = stickyVisibleResults.kwsVisibleTexts;
            synchronized (lock) {
                if (supportObjectMap.size() == 0) {
                    register2VA(view, displayId, allVisibleResults);
                    return;
                }
            }
            triggerSupportObjectUiChange(viewAttrs.displayId, (activity, displayId2, interceptors1) -> {
                VisibleResults visibleResults = null;
                VaLog.d(TAG, "executeViewUiChange, scanning activity...");
                if (activity != null) {
                    View rootView = activity.getWindow().getDecorView().getRootView();
                    visibleResults = coreProcessor.getAllVisibleText(rootView, interceptors1);
                }
                if (visibleResults != null) {
                    VoiceViewCmdUtils.mergeJsonArrays(allVisibleResults.visibleTexts, visibleResults.visibleTexts);
                    VoiceViewCmdUtils.mergeJsonArrays(allVisibleResults.globalVisibleTexts, visibleResults.globalVisibleTexts);
                    VoiceViewCmdUtils.mergeJsonArrays(allVisibleResults.kwsVisibleTexts, visibleResults.kwsVisibleTexts);
                }
                register2VA(view, displayId2, allVisibleResults);
            });
        }
    }

    /**
     * 不扫描直接注册方式, 用于简单的界面
     */
    public void directRegister(View view, ArrayMap<String, View> map) {
        directRegisterDelay(view, false, map, null, 50);
    }

    /**
     * 不扫描直接注册方式, 用于简单的界面
     */
    public void directRegister(View view, boolean isTopCoverView, ArrayMap<String, View> map) {
        directRegisterDelay(view, isTopCoverView, map, null, 50);
    }

    /**
     * 不扫描直接注册方式, 用于简单的界面
     */
    public void directRegister(View view, boolean isTopCoverView, ArrayMap<String, View> map, IDirectRegisterInterceptor interceptor) {
        directRegisterDelay(view, isTopCoverView, map, interceptor, 50);
    }

    /**
     * 不扫描直接注册方式, 用于简单的界面
     */
    public void directRegisterDelay(View view, boolean isTopCoverView, ArrayMap<String, View> map, IDirectRegisterInterceptor interceptor, int delay) {
        if (!VoiceViewCmdUtils.flagViewCmdOn) {
            VaLog.i(TAG, "flagViewCmdOn=false, directRegister forbid!!!");
            return;
        }
        if ((map == null || map.size() == 0) && interceptor == null) {
            VaLog.e(TAG, "directRegister, register null , return!!");
            return;
        }
        int displayId = VoiceViewCmdUtils.getDisplayId(view);
        if (displayId == -1) {
            VaLog.e(TAG, "directRegister, displayId=-1, please check the register time");
            return;
        }
        synchronized (lock) {
            if (allViewMap.containsKey(view)) {
                VaLog.e(TAG, "directRegister, has registered , ignore!!");
                return;
            }
        }
        VaLog.d(TAG, "directRegister() called, view hashCode:" + view.hashCode() + ", isTopCoverView:" + isTopCoverView + ", interceptor:" + interceptor + ", delay:" + delay);

        ViewAttrs viewAttrs = new ViewAttrs(displayId, isTopCoverView, false, true);
        viewAttrs.directInterceptor = interceptor;
        synchronized (lock) {
            allViewMap.put(view, viewAttrs);
        }
        // 监听detach事件
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                view.removeOnAttachStateChangeListener(this);
                directUnRegister(v);
            }
        });

        scanHandler.removeCallbacksAndMessages(null);
        postHandler.postDelayed(() -> {
            synchronized (lock) {
                if (!allViewMap.containsKey(view)) {
                    VaLog.w(TAG, "post ignore, view is not exist!");
                    return;
                }
            }
            ArrayMap<String, Object> newMap = new ArrayMap<>();
            JSONArray uploadArray = new JSONArray();
            if (map != null) {
                for (Map.Entry<String, View> entry : map.entrySet()) {
                    String str = entry.getKey();
                    String[] array = str.split("\\|");
                    for (String text : array) {
                        String key = new ViewCmdBean(null, text, ViewCmdType.TYPE_NORMAL).toString();
                        newMap.put(key, entry.getValue());
                        if (!VoiceViewCmdUtils.isCompatibleMode) {
                            uploadArray.put(key);
                        } else {
                            uploadArray.put(text);
                        }
                    }
                }
            }
            if (interceptor != null) {
                Map<String, Integer> bindMap = interceptor.bind();
                if (bindMap != null) {
                    for (Map.Entry<String, Integer> entry : bindMap.entrySet()) {
                        String str = entry.getKey();
                        String[] array = str.split("\\|");
                        for (String text : array) {
                            String key = new ViewCmdBean(null, text, ViewCmdType.TYPE_NORMAL).toString();
                            newMap.put(key, entry.getValue());
                            if (!VoiceViewCmdUtils.isCompatibleMode) {
                                uploadArray.put(key);
                            } else {
                                uploadArray.put(text);
                            }
                        }
                    }
                }
            }
            viewAttrs.directDataMap = newMap;
            setTopCoverViewShowingIfNeed(displayId, isTopCoverView);
            register2VA(view, displayId, new VisibleResults(uploadArray, null, null));
        }, delay);
    }

    /**
     * 直接注册方式，反注册清除
     */
    public void directUnRegister(View view) {
        VaLog.d(TAG, "directUnRegister() called with: view:" + view);
        synchronized (lock) {
            if (!allViewMap.containsKey(view)) {
                int displayId = getDisplayIdFromMap(view);
                allViewMap.remove(view);
                postHandler.post(() -> {
                    unsetTopCoverViewShowingIfNeed(displayId);
                    register2VA(view, displayId, null);
                });
            }
        }
    }

    /**
     * 用语音侧注册可见即可说词条
     *
     * @param object         activity/fragment/view之一
     * @param visibleResults 可见即可说词条
     */
    private void register2VA(Object object, int displayId, VisibleResults visibleResults) {
        if (displayId == -1) {
            displayId = VoiceViewCmdUtils.getDisplayId(object);
        }
        if (visibleResults != null && visibleResults.isEmpty() && object instanceof View) {
            Integer retryCount = retryCounterMap.get(object);
            if (retryCount == null) {
                retryCount = 0;
            }
            if (retryCount++ < 3) {
                VaLog.i(TAG, "empty view, triggerNotifyUiChange!!!");
                retryCounterMap.put(object, retryCount);
                postHandler.postDelayed(() -> {
                    ViewAttrs attrs;
                    synchronized (lock) {
                        attrs = allViewMap.get(object);
                    }
                    if (attrs != null) {
                        triggerViewUiChange((View) object, attrs);
                    }
                }, 1000);
            } else {
                VaLog.w(TAG, "empty view, remove from map, view:" + object.hashCode());
                unsetTopCoverViewShowingIfNeed(displayId);
                retryCounterMap.remove(object);
                synchronized (lock) {
                    allViewMap.remove(object);
                }
            }
            return;
        }
        retryCounterMap.remove(object);
        boolean register = true;
        if (visibleResults == null) {
            coreProcessor.clear();
            register = false;
        }
        if (visibleResults == null) {
            visibleResults = new VisibleResults(new JSONArray(), new JSONArray(), new JSONArray());
        }
        boolean isFocusable = !(object instanceof View);
        int splitScreenId = VoiceViewCmdUtils.getSplitScreenId(displayId, object);
        // really upload
        register2VAReal(register, displayId, splitScreenId, isFocusable, visibleResults);
    }

    public void register2VAReal(boolean register, int displayId, int splitScreenId, boolean isFocusable, VisibleResults visibleResults) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uiContent", visibleResults.visibleTexts != null ? visibleResults.visibleTexts : new JSONArray());
            jsonObject.put("globalUiContent", visibleResults.globalVisibleTexts != null ? visibleResults.globalVisibleTexts : new JSONArray());
            jsonObject.put("kwsUiContent", visibleResults.kwsVisibleTexts != null ? visibleResults.kwsVisibleTexts : new JSONArray());
            jsonObject.put("displayId", displayId);
            jsonObject.put("splitScreenId", splitScreenId);
            jsonObject.put("isFocusable", isFocusable);
            jsonObject.put("isCompatibleMode", VoiceViewCmdUtils.isCompatibleMode);
            jsonObject.put("register", register);
            VaLog.v(TAG, "onVrViewContentChange:" + jsonObject);
            // 调用语音sdk接口做上传
            com.voyah.ai.sdk.manager.DialogueManager.onViewContentChange(jsonObject.toString());
        } catch (NoSuchMethodError | Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 响应
     */
    public Response onUIWordTriggered(String prompt, String word) {
        return onUIWordTriggered(prompt, word, ViewCmdType.TYPE_NORMAL, -1, -1);
    }

    public Response onUIWordTriggered(String prompt, String word, String type, int direction, int vpaDisplayId) {
        VaLog.d(TAG, "onUIWordTriggered() called with: prompt:" + prompt + ", word:" + word + ", type:" + type + ", direction:" + direction + ", displayId:" + vpaDisplayId);
        if (!VoiceViewCmdUtils.flagViewCmdOn) {
            VaLog.i(TAG, "flagViewCmdOn=false, onUIWordTriggered forbid!!!");
            return null;
        }
        if ("".equals(prompt)) {
            prompt = null;
        }
        ViewCmdBean bean = new ViewCmdBean(prompt, word, type);

        if (direction == -1) {
            direction = VoiceViewCmdUtils.getCurDirection();
        }
        this.curDirection = direction;
        if (vpaDisplayId == -1) {
            vpaDisplayId = VoiceViewCmdUtils.getCurVpaDisplayId();
        }
        if (vpaDisplayId == -1) {
            VaLog.d(TAG, "invalid displayId, set default 0");
            vpaDisplayId = 0;
        }

        if (!ViewCmdType.TYPE_NORMAL.equals(type)) {
            return deliverToKwsOrGlobalProcess(bean, vpaDisplayId);
        }
        View shotView = null;
        ViewAttrs attrs = null;
        Map<View, ViewAttrs> newAllViewMap;
        synchronized (lock) {
            newAllViewMap = new LinkedHashMap<>(allViewMap);
        }
        if (newAllViewMap.size() > 0) {
            Map<Object, IInterceptor<?>> newSupportObjectMap;
            synchronized (lock) {
                newSupportObjectMap = new LinkedHashMap<>(supportObjectMap);
            }
            for (Map.Entry<View, ViewAttrs> entry : newAllViewMap.entrySet()) {
                View view = entry.getKey();
                ViewAttrs value = entry.getValue();
                if (vpaDisplayId == value.displayId && !VoiceViewCmdUtils.isNoFocusDialogView(newSupportObjectMap, view)) {
                    shotView = view;
                    attrs = value;
                }
            }
        }
        Response response;
        if (shotView != null && attrs.isDirect) {
            response = deliverToDirectViewProcess(bean, shotView, attrs);
            if (response.errCode != ErrCode.EC_UNKNOWN) {
                VaLog.d(TAG, "onUIWordTriggered handled by direct!!");
                return response;
            }
        }
        if (shotView != null) {
            response = deliverToViewProcess(bean, shotView, attrs);
            if (response.errCode != ErrCode.EC_UNKNOWN) {
                VaLog.d(TAG, "onUIWordTriggered handled by view!!");
                return response;
            }
        }
        if (vpaDisplayId == VoiceViewCmdUtils.mainDisplayId) {
            return deliverToSupportObjectProcess(bean);
        } else {
            return Response.response(null, ErrCode.EC_UNKNOWN);
        }
    }

    @NonNull
    private Response deliverToSupportObjectProcess(ViewCmdBean bean) {
        VaLog.d(TAG, "deliverToSupportObjectProcess() called with: bean = [" + bean + "]");
        // activity/fragment默认不支持同时显示在不同屏幕上
        List<IInterceptor<?>> interceptors = new ArrayList<>();
        Map<Object, IInterceptor<?>> newSupportObjectMap;
        synchronized (lock) {
            newSupportObjectMap = new LinkedHashMap<>(supportObjectMap);
        }
        Activity supportObject = VoiceViewCmdUtils.getSupportActivityFromMap(newSupportObjectMap, interceptors);
        int displayId = VoiceViewCmdUtils.getDisplayId(supportObject);
        View rootView = VoiceViewCmdUtils.getRootView(supportObject);
        if (rootView != null) {
            return onUIWordTriggeredInner(rootView, displayId, interceptors, bean);
        } else {
            return Response.response(null, ErrCode.EC_UNKNOWN);
        }
    }

    @NonNull
    private Response deliverToKwsOrGlobalProcess(ViewCmdBean bean, int displayId) {
        VaLog.d(TAG, "deliverToKwsOrGlobalProcess() called with: bean = [" + bean + "]");
        View shotView = null;
        ViewAttrs attrs = null;
        Map<View, ViewAttrs> newAllViewMap;
        synchronized (lock) {
            newAllViewMap = new LinkedHashMap<>(allViewMap);
        }
        if (newAllViewMap.size() > 0) {
            Map<Object, IInterceptor<?>> newSupportObjectMap;
            synchronized (lock) {
                newSupportObjectMap = new LinkedHashMap<>(supportObjectMap);
            }
            for (Map.Entry<View, ViewAttrs> entry : newAllViewMap.entrySet()) {
                View view = entry.getKey();
                ViewAttrs value = entry.getValue();
                if (!value.isDirect && value.displayId == displayId && !VoiceViewCmdUtils.isNoFocusDialogView(newSupportObjectMap, view)) {
                    shotView = view;
                    attrs = value;
                }
            }
            if (shotView == null) {
                for (Map.Entry<View, ViewAttrs> entry : newAllViewMap.entrySet()) {
                    View view = entry.getKey();
                    ViewAttrs value = entry.getValue();
                    if (!value.isDirect && !VoiceViewCmdUtils.isNoFocusDialogView(newSupportObjectMap, view)) {
                        shotView = view;
                        attrs = value;
                    }
                }
            }
        }
        if (shotView != null) {
            List<IInterceptor<?>> interceptors = new ArrayList<>();
            if (attrs.interceptor != null) {
                interceptors.add(attrs.interceptor);
            }
            Response response = onUIWordTriggeredInner(shotView, displayId, interceptors, bean);
            if (response.errCode == ErrCode.EC_NORMAL) {
                return response;
            }
        }
        return deliverToSupportObjectProcess(bean);
    }

    private Response deliverToViewProcess(ViewCmdBean bean, View shotView, ViewAttrs attrs) {
        VaLog.d(TAG, "deliverToViewProcess() called with: bean = [" + bean + "]");
        List<IInterceptor<?>> interceptors = new ArrayList<>();
        if (attrs.interceptor != null) {
            interceptors.add(attrs.interceptor);
        }
        return onUIWordTriggeredInner(shotView, attrs.displayId, interceptors, bean);
    }

    private Response deliverToDirectViewProcess(ViewCmdBean bean, View shotView, ViewAttrs attrs) {
        VaLog.d(TAG, "deliverToDirectViewProcess() called with: bean = [" + bean + "]");
        Map<String, Object> map = attrs.directDataMap;
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                VaLog.d(TAG, "directViewMap key:" + entry.getKey() + ",value:" + entry.getValue());
            }
            Object object = map.get(bean.toString());
            if (object != null) {
                View view = object instanceof View ? (View) object : shotView;
                int errCode = ErrCode.EC_NORMAL;
                if (object instanceof View) {
                    View targetView = (View) object;
                    if (VoiceViewCmdUtils.isViewDisabled(targetView, 2)) {
                        errCode = ErrCode.EC_VIEW_DISABLED;
                    } else {
                        performClick(targetView, null);
                    }
                } else if (attrs.directInterceptor != null && object instanceof Integer) {
                    attrs.directInterceptor.onTriggered(bean.text, (Integer) object);
                }
                return Response.response(view, errCode);
            }
        }
        return Response.response(shotView, ErrCode.EC_UNKNOWN);
    }


    private Response onUIWordTriggeredInner(View rootView, int displayId, List<IInterceptor<?>> interceptors, ViewCmdBean bean) {
        List<View> matchedViews = new ArrayList<>();
        int resId = coreProcessor.processVoiceInput(rootView, displayId, bean, matchedViews);
        if (!matchedViews.isEmpty()) {
            List<View> copyViews = new ArrayList<>(matchedViews);
            handleBindClickView(matchedViews, copyViews);

            removeMeaninglessViews(rootView, matchedViews);

            Response response = handleUnsafeAttr(matchedViews, copyViews);
            if (response != null) return response;

            response = handleHighPriorityAttr(interceptors, bean, matchedViews);
            if (response != null) return response;

            if (matchedViews.size() == 0) {
                return Response.response(rootView, ErrCode.EC_UNKNOWN);
            } else if (matchedViews.size() > 1) {
                handleMultiResult(matchedViews);
                if (matchedViews.size() != 1) {
                    return Response.response(rootView, ErrCode.EC_MULTI_SELECT);
                }
            }
            View view = matchedViews.get(0);
            int ec = VoiceViewCmdUtils.checkSwitchState(view, bean);
            if (VoiceViewCmdUtils.isViewDisabled(view, 2)) {
                ec = ErrCode.EC_VIEW_DISABLED;
            } else if (ec == ErrCode.EC_NORMAL) {
                response = executeCustomResponse(view, interceptors, bean, view.getId());
                if (response != null) return response;
                if (view instanceof TabLayout.TabView) {
                    performClick(view, interceptors);
                } else if (view instanceof RadioButton) {
                    performClick(view, interceptors);
                } else if (VCOS_SWITCH.equals(view.getClass().getName())) {
                    performClick(view, interceptors);
                } else {
                    performClickWithListener(view, view, interceptors, 2);
                }
            }
            return Response.response(view, ec);

        } else if (VoiceViewCmdUtils.isGesture(resId)) {
            View gestureView = coreProcessor.getGestureView();
            if (gestureView != null) {
                Response response = executeCustomResponse(gestureView, interceptors, bean, resId);
                if (response != null) return response;
                // 执行手势
                int ec = performGesture(gestureView, resId, bean.text);
                return Response.response(gestureView, ec);
            } else {
                VaLog.e(TAG, "gestureView is null, exception");
            }
        } else if (resId != -1) {
            Response response = executeCustomResponse(rootView, interceptors, bean, resId);
            if (response != null) {
                return response;
            }
        }
        return Response.response(rootView, ErrCode.EC_UNKNOWN);
    }

    private void handleMultiResult(List<View> matchedViews) {
        List<View> copyMatchedViews = new ArrayList<>(matchedViews);
        for (View view : copyMatchedViews) {
            if (VoiceViewCmdUtils.hasTabAttr(view)) {
                matchedViews.remove(view);
            }
        }
    }

    private void removeMeaninglessViews(View rootView, List<View> matchedViews) {
        Iterator<View> iterator = matchedViews.iterator();
        while (iterator.hasNext()) {
            View view = iterator.next();
            View.OnClickListener listener = VoiceViewCmdUtils.getViewClickListenerWithDepth(view, 2);
            if (VoiceViewCmdUtils.isTextView(view) && listener == null) {
                VaLog.d(TAG, "textview has no onClick listener, remove!!!");
                iterator.remove();
            } else if (!VoiceViewCmdUtils.isChildViewOf(view, rootView)) {
                VaLog.d(TAG, "view is belong another rootView, remove!!!");
                iterator.remove();
            } else if (VoiceViewCmdUtils.isViewInvisible(view, 2)) {
                VaLog.d(TAG, "view is not visible, remove!!!");
                iterator.remove();
            }
        }
    }

    private Response handleUnsafeAttr(List<View> matchedViews, List<View> copyViews) {
        // 查找是否view是否有申明attr_unsafe_ope属性
        int errCode = ErrCode.EC_NORMAL;
        View unsafeView = null;
        for (int i = 0; i < copyViews.size(); i++) {
            View view = copyViews.get(i);
            String unsafeValue = VoiceViewCmdUtils.getUnsafe(view);
            if (mCtx.getString(R.string.attr_unsafe_value_none).equalsIgnoreCase(unsafeValue)) {
                matchedViews.remove(view);
                unsafeView = view;
                errCode = ErrCode.EC_UNSAFE_NONE;
            } else if (mCtx.getString(R.string.attr_unsafe_value_driver).equalsIgnoreCase(unsafeValue) && curDirection != 0) {
                unsafeView = view;
                matchedViews.remove(view);
                errCode = ErrCode.EC_UNSAFE_ONLY_DRIVER;
            } else if (mCtx.getString(R.string.attr_unsafe_value_front).equalsIgnoreCase(unsafeValue) && curDirection != 0 && curDirection != 1) {
                unsafeView = view;
                matchedViews.remove(view);
                errCode = ErrCode.EC_UNSAFE_ONLY_FRONT;
            }
        }
        // 非安全操作view
        if (matchedViews.size() == 0 && unsafeView != null) {
            return Response.response(unsafeView, errCode);
        }
        return null;
    }

    private void handleBindClickView(List<View> matchedViews, List<View> copyViews) {
        for (int i = 0; i < copyViews.size(); i++) {
            View view = copyViews.get(i);
            View bindClickView = VoiceViewCmdUtils.getBindClickView(view);
            if (bindClickView != null) {
                VaLog.d(TAG, "bind another view:" + bindClickView);
                matchedViews.remove(view);
                if (!matchedViews.contains(bindClickView)) {
                    matchedViews.add(bindClickView);
                }
            }
        }
    }

    private Response handleHighPriorityAttr(List<IInterceptor<?>> interceptors, ViewCmdBean bean, List<View> matchedViews) {
        // 查找是否view是否有申明priority属性
        List<View> highPriorityViews = new ArrayList<>();
        List<View> nonePriorityViews = new ArrayList<>();
        for (View view : matchedViews) {
            String priority = VoiceViewCmdUtils.getPriority(view);
            if (mCtx.getString(R.string.attr_priority_value_high).equalsIgnoreCase(priority)) {
                highPriorityViews.add(view);
            } else if (mCtx.getString(R.string.attr_priority_value_driver).equalsIgnoreCase(priority) && curDirection == 0) {
                highPriorityViews.add(view);
            } else if (mCtx.getString(R.string.attr_priority_value_passenger).equalsIgnoreCase(priority) && curDirection == 1) {
                highPriorityViews.add(view);
            } else if (mCtx.getString(R.string.attr_priority_value_driver).equalsIgnoreCase(priority) && curDirection != 0) {
                nonePriorityViews.add(view);
            } else if (mCtx.getString(R.string.attr_priority_value_passenger).equalsIgnoreCase(priority) && curDirection != 0 && curDirection != 1) {
                nonePriorityViews.add(view);
            }
        }

        // 有高优先级view
        if (!highPriorityViews.isEmpty()) {
            if (highPriorityViews.size() > 1) {
                return Response.response(highPriorityViews.get(0), ErrCode.EC_MULTI_SELECT);
            } else {
                View highPriorityView = highPriorityViews.get(0);
                int ec = VoiceViewCmdUtils.checkSwitchState(highPriorityView, bean);
                if (VoiceViewCmdUtils.isViewDisabled(highPriorityView, 2)) {
                    ec = ErrCode.EC_VIEW_DISABLED;
                } else if (ec == ErrCode.EC_NORMAL) {
                    Response response = executeCustomResponse(highPriorityView, interceptors, bean, highPriorityView.getId());
                    if (response != null) return response;
                    performClickWithListener(highPriorityView, highPriorityView, interceptors, 2);
                }
                return Response.response(highPriorityView, ec);
            }
        } else if (!nonePriorityViews.isEmpty()) {
            return Response.response(nonePriorityViews.get(0), ErrCode.EC_UNSAFE_NONE);
        }
        return null;
    }

    /**
     * 执行用户自定义处理方法
     */
    private Response executeCustomResponse(View view, List<IInterceptor<?>> interceptors, ViewCmdBean bean, int resId) {
        if (interceptors != null && interceptors.size() > 0) {
            for (int i = interceptors.size() - 1; i >= 0; i--) {
                IInterceptor<?> interceptor = interceptors.get(i);
                if (interceptor != null) {
                    if (ViewCmdType.TYPE_KWS.equals(bean.type)) {
                        Response response = interceptor.onKwsTriggered(bean.text);
                        if (response != null) {
                            return response;
                        }
                    } else if (ViewCmdType.TYPE_GLOBAL.equals(bean.type)) {
                        Response response = interceptor.onGlobalTriggered(bean.text);
                        if (response != null) {
                            return response;
                        }
                    } else {
                        Response response = interceptor.onTriggered(view, bean.text, resId);
                        if (response != null) {
                            return response;
                        }
                        boolean consumed = interceptor.onTriggered(bean.text, resId);
                        if (consumed) {
                            return Response.response(view, ErrCode.EC_NORMAL);
                        }
                    }
                }
            }
        }
        return null;
    }

    private void performClickWithListener(View orginView, View view, List<IInterceptor<?>> interceptors, int maxDepth) {
        if (maxDepth <= 0) {
            View.OnClickListener listener = VoiceViewCmdUtils.getViewOnClickListener(view);
            if (listener == null) {
                VaLog.d(TAG, "no find onclick listener and reach maxDepth, " + view);
                performClick(orginView, interceptors);
            } else {
                performClick(view, interceptors);
            }
            return;
        }

        View.OnClickListener listener = VoiceViewCmdUtils.getViewOnClickListener(view);
        if (listener != null) {
            performClick(view, interceptors);
        } else {
            ViewParent parent = view.getParent();
            if (parent instanceof View) {
                performClickWithListener(orginView, (View) parent, interceptors, maxDepth - 1);
            } else {
                VaLog.d(TAG, "no find onclick listener:" + view);
                performClick(orginView, interceptors);
            }
        }
    }

    public void triggerNotifyUiChange() {
        triggerNotifyUiChange(-1, DELAY_NOTIFY_CHANGED);
    }

    public void triggerNotifyUiChange(int delay) {
        triggerNotifyUiChange(-1, delay);
    }

    public void triggerNotifyUiChangeById(int displayId) {
        triggerNotifyUiChange(displayId, DELAY_NOTIFY_CHANGED);
    }

    public void triggerNotifyUiChange(int displayId, int delay) {
        if (VoiceViewCmdUtils.mCtx == null) {
            VaLog.e(TAG, "VoiceViewCmdUtils.mCtx == null");
            return;
        }
        synchronized (lock) {
            if (allViewMap.size() == 0 && supportObjectMap.size() == 0) {
                return;
            }
        }
        scanHandler.removeCallbacksAndMessages(null);
        scanHandler.postDelayed(() -> {
            Map<View, ViewAttrs> newAllViewMap;
            Map<Object, IInterceptor<?>> newSupportObjectMap;
            synchronized (lock) {
                if (allViewMap.size() == 0 && supportObjectMap.size() == 0) {
                    return;
                }
                newAllViewMap = new LinkedHashMap<>(allViewMap);
                newSupportObjectMap = new LinkedHashMap<>(supportObjectMap);
            }
            coreProcessor.clear();
            if (displayId == -1) {
                View mainView = null, passengerView = null, cellingView = null;
                if (newAllViewMap.size() > 0) {
                    for (Map.Entry<View, ViewAttrs> entry : newAllViewMap.entrySet()) {
                        View view = entry.getKey();
                        ViewAttrs value = entry.getValue();
                        if (!VoiceViewCmdUtils.isNoFocusDialogView(newSupportObjectMap, view)) {
                            if (value.displayId == VoiceViewCmdUtils.mainDisplayId) {
                                mainView = view;
                            } else if (value.displayId == VoiceViewCmdUtils.passengerDisplayId) {
                                passengerView = view;
                            } else if (value.displayId == VoiceViewCmdUtils.cellingDisplayId) {
                                cellingView = view;
                            }
                        }
                    }
                }
                VaLog.v(TAG, "triggerNotifyUiChange, mainView exist=" + (mainView != null)
                        + ", passengerView exist=" + (passengerView != null) + ", cellingView exist=" + (cellingView != null));
                triggerByDisplayId(VoiceViewCmdUtils.mainDisplayId, mainView, newAllViewMap);
                triggerByDisplayId(VoiceViewCmdUtils.passengerDisplayId, passengerView, newAllViewMap);
                triggerByDisplayId(VoiceViewCmdUtils.cellingDisplayId, cellingView, newAllViewMap);
            } else {
                View targetView = null;
                if (newAllViewMap.size() > 0) {
                    for (Map.Entry<View, ViewAttrs> entry : newAllViewMap.entrySet()) {
                        View view = entry.getKey();
                        ViewAttrs value = entry.getValue();
                        if (!VoiceViewCmdUtils.isNoFocusDialogView(newSupportObjectMap, view)) {
                            if (value.displayId == displayId) {
                                targetView = view;
                                break;
                            }
                        }
                    }
                }
                VaLog.v(TAG, "triggerNotifyUiChange, targetView exist=" + (targetView != null));
                triggerByDisplayId(displayId, targetView, newAllViewMap);
            }
        }, delay);
    }

    private void triggerByDisplayId(int displayId, View view, Map<View, ViewAttrs> allViewMap) {
        if (view != null && allViewMap.get(view) != null) {
            triggerViewUiChange(view, allViewMap.get(view));
        } else {
            triggerSupportObjectUiChange(displayId, this::executeActivityUiChange);
        }
    }

    private void triggerViewUiChange(View view, ViewAttrs viewAttrs) {
        if (!VoiceViewCmdUtils.flagViewCmdOn) {
            VaLog.i(TAG, "flagViewCmdOn=false, upload empty json to voice");
            if (viewAttrs.displayId != -1) {
                int splitScreenId = VoiceViewCmdUtils.getSplitScreenId(viewAttrs.displayId, view);
                register2VAReal(true, viewAttrs.displayId, splitScreenId, true, new VisibleResults());
            }
            return;
        }
        executeViewUiChange(view, viewAttrs);
    }

    private void triggerSupportObjectUiChange(int triggerDisplayId, Callback callback) {
        List<IInterceptor<?>> interceptors = new ArrayList<>();
        Map<Object, IInterceptor<?>> newSupportObjectMap;
        synchronized (lock) {
            newSupportObjectMap = new LinkedHashMap<>(supportObjectMap);
        }
        Activity activity = VoiceViewCmdUtils.getSupportActivityFromMap(newSupportObjectMap, interceptors);
        if (activity != null) {
            int displayId = VoiceViewCmdUtils.getDisplayId(activity);
            if (triggerDisplayId == -1 && displayId == -1) {
                return;
            }
            if (displayId == -1) {
                Integer retryCount = retryCounterMap.get(activity);
                if (retryCount == null) {
                    retryCount = 0;
                }
                if (retryCount++ < 3) {
                    VaLog.w(TAG, "activity is not currently attached to a window, triggerNotifyUiChange!!!");
                    retryCounterMap.put(activity, retryCount);
                    triggerNotifyUiChangeById(displayId);
                } else {
                    retryCounterMap.remove(activity);
                }
                return;
            }
            retryCounterMap.remove(activity);
            if (displayId == triggerDisplayId) {
                if (!VoiceViewCmdUtils.flagViewCmdOn) {
                    VaLog.i(TAG, "flagViewCmdOn=false, upload empty json to voice");
                    int splitScreenId = VoiceViewCmdUtils.getSplitScreenId(displayId, activity);
                    register2VAReal(true, displayId, splitScreenId, true, new VisibleResults());
                } else {
                    IInterceptor<?>[] interceptorArray = interceptors.toArray(new IInterceptor[0]);
                    callback.doSomething(activity, displayId, interceptorArray);
                }
            }
        }
    }

    /**
     * 执行可见即可说点击和动效
     *
     * @param view
     */
    public void performClick(View view, List<IInterceptor<?>> interceptors) {
        VaLog.d(TAG, "performClick: view:" + view);
        // 获取view在屏幕中的绝对坐标
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        if ((location[0] == 0 && location[1] == 0) || view.hasTransientState()) {
            VaLog.e(TAG, "performClick, view has no attached or measured or has transientState, performClick directly!!!");
            ClickRippleEffect.uiHandler.postDelayed(() -> performClickReal(view), 500);
            return;
        }
        int cx = location[0] + view.getWidth() / 2 - 70;
        int cy = location[1] + view.getHeight() / 2 - 70;
        // 坐标校正
        Point point = new Point(cx, cy);
        if (interceptors != null && interceptors.size() > 0) {
            for (int i = interceptors.size() - 1; i >= 0; i--) {
                IInterceptor<?> interceptor = interceptors.get(i);
                if (interceptor != null) {
                    boolean consumed = interceptor.onLocateAdj(view, point);
                    if (consumed) {
                        break;
                    }
                }
            }
        }
        int displayId = VoiceViewCmdUtils.getDisplayId(view);
        if (displayId == -1) {
            VaLog.e(TAG, "performClick, getDisplayId fail, performClick directly!!!");
            ClickRippleEffect.uiHandler.postDelayed(() -> performClickReal(view), 500);
            return;
        }
        ClickRippleEffect.show(VoiceViewCmdUtils.mCtx, displayId, point.x, point.y, () -> performClickReal(view));
    }

    private void performClickReal(View view) {
        view.setTag(R.string.tag_viewcmd_click, VoiceViewCmdUtils.mCtx.getString(R.string.tag_viewcmd_click));
        if (view instanceof CompoundButton) {
            // 优先使用开关设置的onClick事件
            View.OnClickListener listener = VoiceViewCmdUtils.getViewOnClickListener(view);
            if (listener != null) {
                VaLog.d(TAG, "CompoundButton perform click");
                listener.onClick(view);
            } else {
                CompoundButton cb = (CompoundButton) view;
                cb.toggle();
            }
        } else if (VCOS_SWITCH.equals(view.getClass().getName())) {
            VcosViewUtil.toggleVCOSSwitchForVoice(view);
        } else {
            view.performClick();
        }
        view.setTag(R.string.tag_viewcmd_click, null);
    }

    /**
     * 模拟手势
     *
     * @param gesture 手势方向
     * @param text    文本
     */
    private int performGesture(View view, int gesture, String text) {
        VaLog.d(TAG, "performGesture() called with: view = [" + view + "], gesture = [" + gesture + "], text = [" + text + "]");
        return GestureUtils.performGesture(view, gesture);
    }

    private void setTopCoverViewShowingIfNeed(int displayId, boolean isTopCoverView) {
        if (isTopCoverView && displayId != -1) {
            try {
                com.voyah.ai.sdk.manager.DialogueManager.setTopCoverViewShowing(displayId, true);
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            }
        }
    }

    private void unsetTopCoverViewShowingIfNeed(int displayId) {
        try {
            com.voyah.ai.sdk.manager.DialogueManager.setTopCoverViewShowing(displayId, false);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }

    private int getDisplayIdFromMap(View view) {
        int displayId;
        ViewAttrs viewAttrs;
        synchronized (lock) {
            viewAttrs = allViewMap.get(view);
        }
        if (viewAttrs != null) {
            displayId = viewAttrs.displayId;
        } else {
            displayId = -1;
        }
        return displayId;
    }

    public interface Callback {
        void doSomething(Activity activity, int displayId, IInterceptor<?>... interceptors);
    }

    private static class SingletonInstance {
        private static final VoiceViewCmdManager INSTANCE = new VoiceViewCmdManager();
    }

    public static VoiceViewCmdManager getInstance() {
        return SingletonInstance.INSTANCE;
    }
}