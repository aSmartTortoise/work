package com.voyah.viewcmd;

import static com.voyah.viewcmd.PromptGen.CMDS_SWITCH_CLOSE;
import static com.voyah.viewcmd.PromptGen.CMDS_SWITCH_OPEN;
import static com.voyah.viewcmd.VcosViewUtil.VCOS_BUTTON_LIST;
import static com.voyah.viewcmd.VcosViewUtil.VCOS_ITEM_VIEW_LIST;
import static com.voyah.viewcmd.VcosViewUtil.VCOS_SELECTOR_VIEW;
import static com.voyah.viewcmd.VcosViewUtil.VCOS_SWITCH;
import static com.voyah.viewcmd.VcosViewUtil.VCOS_TAB;
import static com.voyah.viewcmd.VcosViewUtil.VCOS_TAB_HORIZONTAL;
import static com.voyah.viewcmd.VcosViewUtil.VCOS_TAB_VERTICAL;
import static com.voyah.viewcmd.WeakReferenceUtil.safeGetMap;
import static com.voyah.viewcmd.WeakReferenceUtil.safeGetMapActivity;
import static com.voyah.viewcmd.WeakReferenceUtil.safePutMap;
import static com.voyah.viewcmd.WeakReferenceUtil.safePutMapActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ScrollingView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.voyah.ai.sdk.bean.DhDirection;
import com.voyah.ai.sdk.bean.DhScreenType;
import com.voyah.ai.sdk.bean.DhSwitch;
import com.voyah.ai.sdk.manager.SettingManager;
import com.voyah.viewcmd.interceptor.IInterceptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VoiceViewCmdUtils {

    private static final String TAG = VoiceViewCmdUtils.class.getSimpleName();

    /**
     * 网络流行的rorbin VerticalTabLayout侧边栏
     */
    public static final String QRORBIN_VERTICAL_TABLAYOUT = "q.rorbin.verticaltablayout.VerticalTabLayout";

    // 资源ID正则表达式
    private static final String REGEX_RES_ID = "@\\+?id/[a-zA-Z0-9_]+";
    private static final String REGEX_STR_ID = "@string/[a-zA-Z0-9_]+";

    private static final String SPLIT_SCREEN_STATUS = "split_screen_status";
    /**
     * 上下文
     */
    public static Context mCtx;
    /**
     * 打开兼容模式: 兼容模式时是为了适配讯飞的可见即可说
     */
    public static boolean isCompatibleMode = false;
    /**
     * 自定义响应，即在应用中监听可见语义并执行响应动作
     */
    public static boolean isCustomResponse = false;

    /**
     * 点击动效悬浮窗type, 默认2038
     */
    public static int windowType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    private static final Map<WeakReference<View>, String> viewCmdMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<View>, String> listMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<View>, Integer> badgeMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<View>, WeakReference<View>> bindViewMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<View>, String> unsafeViewMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<View>, String> gestureMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<View>, Boolean> descTextMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<View>, String> priorityMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<View>, Boolean> tabMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<View>, Boolean> switchMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<View>, String> viewCmdTypeMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<Activity>, Boolean> dialogActivityMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<View>, Activity> viewAttachedActivityMap = new ConcurrentHashMap<>();
    private static final Map<WeakReference<Activity>, Boolean> nonViewCmdActivityMap = new ConcurrentHashMap<>();

    public static final int FULL_SCREEN = 10;
    public static final int LEFT_SPIT_SCREEN = 11;
    public static final int RIGHT_SPIT_SCREEN = 12;
    public static boolean flagViewCmdOn = true;

    protected static int mainDisplayId = 0, passengerDisplayId = 3, cellingDisplayId = 2;
    private static int leftSplitScreenWidth = 850;

    public static void init(Application application, boolean compatibleMode, boolean customResponse) {
        init(application.getApplicationContext(), compatibleMode, customResponse, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
    }

    public static void init(Application application, boolean compatibleMode, boolean customResponse, int type) {
        init(application.getApplicationContext(), compatibleMode, customResponse, type);
    }

    public static void init(Application application, boolean compatibleMode, boolean customResponse, int type, String processName) {
        init(application.getApplicationContext(), compatibleMode, customResponse, type, processName);
    }

    public static void init(Context context, boolean compatibleMode, boolean customResponse, int type) {
        String packageName = context.getPackageName();
        init(context, packageName, compatibleMode, customResponse, type, null);
    }

    public static void init(Context context, boolean compatibleMode, boolean customResponse, int type, String processName) {
        String packageName = context.getPackageName();
        init(context, packageName, compatibleMode, customResponse, type, processName);
    }

    public static void init(Context context, String packageName, boolean compatibleMode,
                            boolean customResponse, int type, String viceProcess) {
        String processName = getProcessName(context, android.os.Process.myPid());
        VaLog.d(TAG, "packageName:" + packageName + ", processName:" + processName);
        boolean isMainProcess = packageName.equals(processName);
        boolean isViceProcess = !isMainProcess && !TextUtils.isEmpty(viceProcess) && viceProcess.equals(processName);
        if (isMainProcess && mCtx == null || (isViceProcess && mCtx == null)) {
            VaLog.d(TAG, "init() called with: context = [" + context + "], compatibleMode = [" + compatibleMode + "], customResponse = [" + customResponse + "], windowType = [" + type + "]");
            mCtx = context;
            isCompatibleMode = compatibleMode;
            isCustomResponse = customResponse;
            windowType = type;
            VoiceViewCmdManager.getInstance().init();
            initVoiceSDK();
            registerReceiver();
            registerObserver();
        }
    }

    private static void registerObserver() {
        ContentObserver mSpitScreenObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                VoiceViewCmdManager.getInstance().triggerNotifyUiChangeById(mainDisplayId);
            }
        };

        // 监听分屏
        mCtx.getContentResolver().registerContentObserver(
                Settings.System.getUriFor(SPLIT_SCREEN_STATUS),
                false, mSpitScreenObserver);
    }

    private static String getProcessName(Context cxt, int pid) {
        //获取ActivityManager对象
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        //在运行的进程的
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    private static void initVoiceSDK() {
        try {
            com.voyah.ai.sdk.DhSpeechSDK.initialize(mCtx, () -> {
                VaLog.d(TAG, "onSpeechReady() called");
                if (!isCustomResponse) {
                    com.voyah.ai.sdk.manager.DialogueManager.setVAResultListener(new com.voyah.ai.sdk.listener.SimpleVAResultListener() {
                        @Override
                        public void onViewCommand(String tag, com.voyah.ai.sdk.bean.NluResult result) {
                            VaLog.d(TAG, "onViewCommand() called with: tag = [" + tag + "], result = [" + result + "]");
                            try {
                                JSONObject object = new JSONObject(result.data);
                                String prompt = object.optString("prompt");
                                String viewCmd = object.optString("viewCmd");
                                String type = object.optString("type", ViewCmdType.TYPE_NORMAL);
                                int direction = object.optInt("direction", -1);
                                int displayId = object.optInt("displayId", -1);
                                Response response = VoiceViewCmdManager.getInstance().onUIWordTriggered(prompt, viewCmd, type, direction, displayId);
                                VaLog.d(TAG, "response:" + response);
                                if (response != null) {
                                    try {
                                        boolean isEnable = SettingManager.isEnableSwitch(DhSwitch.NearbyTTS);
                                        if (isEnable) {
                                            com.voyah.ai.sdk.manager.TTSManager.speak(response.text, 16, getLocationByDirection(direction));
                                        } else {
                                            com.voyah.ai.sdk.manager.TTSManager.speakImt(response.text, 16);
                                        }
                                    } catch (NoSuchMethodError e) {
                                        e.printStackTrace();
                                        com.voyah.ai.sdk.manager.TTSManager.speakImt(response.text);
                                    }
                                }
                            } catch (JSONException | NoSuchMethodError e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                initDisplayIdParams();
            });
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }

    /**
     * * 0x0:ALL
     * * 0x1:FirstRowLeft
     * * 0x2:FirstRowRight
     * * 0x3:SecondRowLeft
     * * 0x4:SecondRowRight
     *
     * @param direction
     * @return
     */
    public static int getLocationByDirection(int direction) {
        if (direction == DhDirection.FRONT_LEFT) {
            return 0x1;
        } else if (direction == DhDirection.FRONT_RIGHT) {
            return 0x2;
        } else if (direction == DhDirection.REAR_LEFT) {
            return 0x3;
        } else if (direction == DhDirection.REAR_RIGHT) {
            return 0x4;
        } else {
            return 0;
        }
    }

    private static void registerReceiver() {
        ViewCmdReceiver receiver = new ViewCmdReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ViewCmdReceiver.ACTION_VIEW_CMD_TEXT);
        filter.addAction(ViewCmdReceiver.ACTION_TRIGGER_CHANGED);
        mCtx.registerReceiver(receiver, filter);
    }

    /**
     * 判断view是否申明描述性文本属性，不作为可见即可说文本上传
     */
    public static boolean isDescText(View view) {
        CharSequence contentDescription = view.getContentDescription();
        if (contentDescription != null) {
            String cd = contentDescription.toString();
            String[] strings = cd.split(":");
            boolean isDesc = mCtx.getString(R.string.attr_desc_text).equalsIgnoreCase(strings[0]);
            if (isDesc) {
                if (strings.length > 1) {
                    return "true".equalsIgnoreCase(strings[1]);
                } else {
                    return mCtx.getString(R.string.attr_desc_text).equalsIgnoreCase(strings[0]);
                }
            }
        } else {
            // 字符长度大于50，当作描述性字符
            if (view instanceof TextView) {
                String text = ((TextView) view).getText().toString();
                if (text.length() >= 50) {
                    return true;
                }
            }
        }
        Boolean value = safeGetMap(descTextMap, view);
        return value != null ? value : false;
    }

    /**
     * 设置为descText
     */
    public static void setIsDescText(View view) {
        safePutMap(descTextMap, view, true);
    }

    /**
     * 获取view的优先级属性
     */
    public static String getPriority(View view) {
        CharSequence contentDescription = view.getContentDescription();
        if (contentDescription != null) {
            String cd = contentDescription.toString();
            String[] strings = cd.split(":");
            if (strings.length > 1 && mCtx.getString(R.string.attr_priority).equalsIgnoreCase(strings[0])) {
                return strings[1];
            }
        }
        return safeGetMap(priorityMap, view);
    }

    /**
     * 设置Priority
     */
    public static void setPriority(View view, String value) {
        safePutMap(priorityMap, view, value);
    }

    /**
     * 获取view非安全操作属性值
     */
    public static String getUnsafe(View view) {
        CharSequence contentDescription = view.getContentDescription();
        if (contentDescription != null) {
            String cd = contentDescription.toString();
            String[] strings = cd.split(":");
            if (strings.length > 1 && mCtx.getString(R.string.attr_unsafe_ope).equalsIgnoreCase(strings[0])) {
                return strings[1];
            }
        }
        return safeGetMap(unsafeViewMap, view);
    }

    /**
     * 设置属性unsafe值
     */
    public static void setUnsafeOpe(View view, String value) {
        safePutMap(unsafeViewMap, view, value);
    }

    /**
     * 获取view布局中设置的viewCmd属性值
     */
    public static String[] getViewCmdAttr(View view) {
        Object tag = view.getTag();
        if (tag instanceof CharSequence) {
            String cd = tag.toString();
            String[] strings = cd.split(":");
            if (strings.length > 1 && mCtx.getString(R.string.attr_view_cmd).equalsIgnoreCase(strings[0])) {
                String parse = parse(view, strings[1]);
                return parse.split("\\|");
            }
        }
        String viewCmd = safeGetMap(viewCmdMap, view);
        return viewCmd == null ? null : viewCmd.split("\\|");
    }

    /**
     * 设置viewCmd
     */
    public static void setViewCmd(View view, String viewCmd) {
        safePutMap(viewCmdMap, view, viewCmd);
    }

    private static String parse(View view, String origStr) {
        // 编译正则表达式
        Pattern idPattern = Pattern.compile(REGEX_RES_ID);
        Pattern strPattern = Pattern.compile(REGEX_STR_ID);
        Matcher idMatcher = idPattern.matcher(origStr);

        String replacedStr = origStr;

        while (idMatcher.find()) {
            // 提取匹配的部分
            String match = idMatcher.group();
            // 解析控件id
            View bindView = parseResIdName(view, match);
            CharSequence text = null;
            String parsedString = "";
            if (bindView instanceof TextView) {
                text = ((TextView) bindView).getText();
            } else if (bindView != null && VCOS_BUTTON_LIST.contains(bindView.getClass().getName())) {
                text = VcosViewUtil.getVCOSButtonText(bindView);
            } else if (bindView != null && VCOS_SELECTOR_VIEW.equals(bindView.getClass().getName())) {
                text = VcosViewUtil.getVCOSSelectorViewText(bindView);
            }
            if (text != null) {
                parsedString = text.toString();
            }
            // 将解析后的字符串替换回去
            replacedStr = replacedStr.replace(match, parsedString);
        }

        Matcher strMatcher = strPattern.matcher(replacedStr);

        while (strMatcher.find()) {
            // 提取匹配的部分
            String match = strMatcher.group();
            // 解析字符串id
            String parsedString = parseStringId(view, match);
            // 将解析后的字符串替换回去
            replacedStr = replacedStr.replace(match, parsedString);
        }
        return replacedStr;
    }

    /**
     * 获取view将点击事件绑定的另外一个view
     */
    public static View getBindClickView(View view) {
        Object tag = view.getTag();
        if (tag instanceof CharSequence) {
            String cd = tag.toString();
            String[] strings = cd.split(":");
            if (mCtx.getString(R.string.attr_bind_click).equalsIgnoreCase(strings[0])) {
                if (strings.length > 1) {
                    return parseResIdName(view, strings[1]);
                }
            }
        }
        WeakReference<View> viewRef = safeGetMap(bindViewMap, view);
        if (viewRef != null) {
            return viewRef.get();
        }
        return null;
    }

    /**
     * 设置BindView
     */
    public static void setBindClickView(View view, View bindView) {
        safePutMap(bindViewMap, view, new WeakReference<>(bindView));
    }

    private static String parseStringId(View view, String resIdName) {
        int resId = view.getContext().getResources()
                .getIdentifier(resIdName.replace("@str/", ""), "string",
                        view.getContext().getPackageName());
        if (resId > 0) {
            return view.getContext().getString(resId);
        }
        return "";
    }

    private static View parseResIdName(View view, String resIdName) {
        Pattern idPattern = Pattern.compile(REGEX_RES_ID);
        Matcher idMatcher = idPattern.matcher(resIdName);
        View foundView = null;
        if (idMatcher.find()) { //限支持一个
            // 解析控件id
            int resId = view.getContext().getResources()
                    .getIdentifier(resIdName.replace("@id/", "")
                                    .replace("@+id/", ""), "id",
                            view.getContext().getPackageName());

            if (resId > 0) {
                ViewParent parent = view.getParent();
                if (parent != null) {
                    foundView = ((View) parent).findViewById(resId);
                    if (foundView == null) {
                        ViewParent parent1 = parent.getParent();
                        if (parent1 != null) {
                            foundView = ((View) parent1).findViewById(resId);
                        }
                    }
                }
                if (foundView == null) {
                    View rootView = view.getRootView();
                    if (rootView != null) {
                        return rootView.findViewById(resId);
                    }
                }
            }
        }
        return foundView;
    }

    /**
     * 获取view布局中设置的list属性值
     */
    public static String getListAttr(View view) {
        CharSequence contentDescription = view.getContentDescription();
        if (contentDescription != null) {
            String cd = contentDescription.toString();
            String[] strings = cd.split(":");
            if (mCtx.getString(R.string.attr_list).equalsIgnoreCase(strings[0])) {
                if (strings.length > 1) {
                    return strings[1];
                } else {
                    return mCtx.getString(R.string.attr_list_value_common);
                }
            }
        }
        return safeGetMap(listMap, view);
    }

    /**
     * 设置为list
     */
    public static void setList(View view, String value) {
        safePutMap(listMap, view, value);
    }

    /**
     * 获取view布局中设置的badge属性值
     */
    public static String getBadgeAttr(View view) {
        CharSequence contentDescription = view.getContentDescription();
        if (contentDescription != null) {
            String cd = contentDescription.toString();
            String[] strings = cd.split(":");
            if (mCtx.getString(R.string.attr_badge).equalsIgnoreCase(strings[0])) {
                if (strings.length > 1) {
                    return strings[1];
                } else {
                    return mCtx.getString(R.string.attr_badge_value_common);
                }
            }
        }
        Integer value = safeGetMap(badgeMap, view);
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 设置为badge
     */
    public static void setBadge(View view, int value) {
        safePutMap(badgeMap, view, value);
    }

    /**
     * 判断view是否申明tab属性
     */
    public static boolean hasTabAttr(View view) {
        CharSequence contentDescription = view.getContentDescription();
        if (contentDescription != null) {
            String cd = contentDescription.toString();
            String[] strings = cd.split(":");
            boolean isTab = mCtx.getString(R.string.attr_tab).equalsIgnoreCase(strings[0]);
            if (isTab) {
                if (strings.length > 1) {
                    return "true".equalsIgnoreCase(strings[1]);
                } else {
                    return mCtx.getString(R.string.attr_tab).equalsIgnoreCase(strings[0]);
                }
            }
        }
        Boolean value = safeGetMap(tabMap, view);
        return value != null ? value : false;
    }

    /**
     * 设置为tab
     */
    public static void setIsTab(View view) {
        safePutMap(tabMap, view, true);
    }

    /**
     * 判断view是否申明switch属性
     */
    public static boolean hasSwitchAttr(View view) {
        CharSequence contentDescription = view.getContentDescription();
        if (contentDescription != null) {
            String cd = contentDescription.toString();
            String[] strings = cd.split(":");
            boolean isSwitch = mCtx.getString(R.string.attr_switch).equalsIgnoreCase(strings[0]);
            if (isSwitch) {
                if (strings.length > 1) {
                    return "true".equalsIgnoreCase(strings[1]);
                } else {
                    return mCtx.getString(R.string.attr_switch).equalsIgnoreCase(strings[0]);
                }
            }
        }
        Boolean value = safeGetMap(switchMap, view);
        return value != null ? value : false;
    }

    /**
     * 设置为switch
     */
    public static void setIsSwitch(View view) {
        safePutMap(switchMap, view, true);
    }

    /**
     * 获取view手势方向属性
     */
    public static String getGestureAttr(View view) {
        CharSequence contentDescription = view.getContentDescription();
        if (contentDescription != null) {
            String cd = contentDescription.toString();
            String[] strings = cd.split(":");
            if (strings.length > 1 && mCtx.getString(R.string.attr_gesture).equalsIgnoreCase(strings[0])) {
                return strings[1];
            }
        }
        return safeGetMap(gestureMap, view);
    }

    /**
     * 设置为gesture
     */
    public static void setGesture(View view, String value) {
        safePutMap(gestureMap, view, value);
    }

    /**
     * 获取viewCmdType属性
     */
    public static String getViewCmdTypeAttr(View view) {
        CharSequence contentDescription = view.getContentDescription();
        if (contentDescription != null) {
            String cd = contentDescription.toString();
            String[] strings = cd.split(":");
            if (strings.length > 1 && mCtx.getString(R.string.attr_viewcmd_type).equalsIgnoreCase(strings[0])) {
                return strings[1];
            }
        }
        String type = safeGetMap(viewCmdTypeMap, view);
        return type != null ? type : ViewCmdType.TYPE_NORMAL;
    }

    /**
     * 设置可见类型
     */
    public static void setViewCmdType(View view, String type) {
        if (ViewCmdType.TYPE_NORMAL.equals(type) || ViewCmdType.TYPE_GLOBAL.equals(type) || ViewCmdType.TYPE_KWS.equals(type)) {
            safePutMap(viewCmdTypeMap, view, type);
        } else {
            VaLog.w(TAG, "viewCmdType is invalid, type:" + type + ",view:" + view);
        }
    }

    /**
     * 判断是否为手势
     */
    public static boolean isGesture(int resId) {
        return resId > GestureUtils.RES_ID_GESTURE_MIN
                && resId < GestureUtils.RES_ID_GESTURE_MAX;
    }

    /**
     * 判断Activity是否作为弹窗使用
     */
    protected static boolean isDialogActivity(Activity activity) {
        Boolean value = safeGetMapActivity(dialogActivityMap, activity);
        return value != null ? value : false;
    }

    /**
     * 将activity当作弹窗使用
     */
    public static void setDialogActivity(Activity activity) {
        safePutMapActivity(dialogActivityMap, activity, true);
    }

    protected static Activity getViewAttachActivity(View view) {
        return safeGetMap(viewAttachedActivityMap, view);
    }

    /**
     * 设置弹窗view附属的activity (dialog直接使用view注册时的弥补措施)
     */
    public static void setViewAttachActivity(View view, Activity activity) {
        VaLog.d(TAG, "setViewAttachActivity() called with: view hashCode:" + view.hashCode() + " , activity:" + activity);
        safePutMap(viewAttachedActivityMap, view, activity);
    }

    protected static VisibleResults distinct(@NonNull Set<ViewCmdBean> visibleTexts) {
        JSONArray array = new JSONArray();
        JSONArray globalArray = new JSONArray();
        JSONArray kwsArray = new JSONArray();
        for (ViewCmdBean bean : visibleTexts) {
            if (!VoiceViewCmdUtils.isCompatibleMode) {
                if (ViewCmdType.TYPE_GLOBAL.equals(bean.type)) {
                    globalArray.put(bean.toString());
                } else if (ViewCmdType.TYPE_KWS.equals(bean.type)) {
                    kwsArray.put(bean.toString());
                } else {
                    array.put(bean.toString());
                }
            } else {
                if (ViewCmdType.TYPE_GLOBAL.equals(bean.type)) {
                    globalArray.put(bean.text);
                } else if (ViewCmdType.TYPE_KWS.equals(bean.type)) {
                    kwsArray.put(bean.text);
                } else {
                    array.put(bean.text);
                }
            }
        }
        return new VisibleResults(array, globalArray, kwsArray);
    }

    public static void enableViewCmd(boolean on) {
        VaLog.d(TAG, "enableViewCmd() called with: on = [" + on + "]");
        flagViewCmdOn = on;
        VoiceViewCmdManager.getInstance().triggerNotifyUiChange();
    }

    protected static JSONArray mergeJsonArrays(@NonNull JSONArray origArray, JSONArray appendArray) {
        if (appendArray != null) {
            for (int i = 0; i < appendArray.length(); i++) {
                try {
                    origArray.put(appendArray.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return origArray;
    }

    public static int getDisplayId(Object object) {
        int displayId = -1;
        try {
            if (object instanceof View) {
                View view = (View) object;
                Display display = view.getDisplay();
                if (display != null) {
                    displayId = display.getDisplayId();
                }

                if (displayId == -1) {
                    display = view.getContext().getDisplay();
                    if (display != null) {
                        displayId = display.getDisplayId();
                    }
                }
            } else if (object instanceof Fragment) {
                Activity activity = ((Fragment) object).getActivity();
                if (activity != null) {
                    displayId = activity.getWindow().getDecorView().getDisplay().getDisplayId();
                }
            } else if (object instanceof Activity) {
                Activity activity = (Activity) object;
                displayId = activity.getWindow().getDecorView().getDisplay().getDisplayId();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return displayId;
    }

    public static int getSplitScreenId(int displayId, Object object) {
        if (displayId != mainDisplayId) {
            return FULL_SCREEN;
        }
        View view = null;
        if (object instanceof ViewGroup) {
            view = (View) object;
        } else if (object instanceof Fragment) {
            Activity activity = ((Fragment) object).getActivity();
            if (activity != null) {
                view = activity.getWindow().getDecorView();
            }
        } else if (object instanceof Activity) {
            Activity activity = (Activity) object;
            view = activity.getWindow().getDecorView();
        }
        if (view != null) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            if (isSplitScreening()) {
                if (location[0] < leftSplitScreenWidth) {
                    return LEFT_SPIT_SCREEN;
                } else {
                    return RIGHT_SPIT_SCREEN;
                }
            }
        }
        return FULL_SCREEN;
    }

    private static boolean isSplitScreening() {
        boolean isSplitState = false;
        Uri uri = Uri.parse("content://com.voyah.ai.voice.export/split_screen_status");
        Cursor cursor = mCtx.getContentResolver().query(uri, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                isSplitState = cursor.getInt(0) == 1;
            }
            cursor.close();
        } else {
            // 对旧项目兼容
            isSplitState = Settings.System.getInt(mCtx.getContentResolver(), SPLIT_SCREEN_STATUS, 0) == 1;
        }
        return isSplitState;
    }

    protected static int getWindowType(View view) {
        View rootView = view.getRootView();
        ViewGroup.LayoutParams params = rootView.getLayoutParams();
        if (params instanceof WindowManager.LayoutParams) {
            return ((WindowManager.LayoutParams) params).type;
        }
        return -1;
    }

    /**
     * 判断view是否是侧边栏Tab
     */
    protected static boolean isTabLayout(View view) {
        return view instanceof TabLayout
                || QRORBIN_VERTICAL_TABLAYOUT.equals(view.getClass().getName())
                || VCOS_TAB.equals(view.getClass().getName())
                || VCOS_TAB_HORIZONTAL.equals(view.getClass().getName())
                || VCOS_TAB_VERTICAL.equals(view.getClass().getName());
    }


    /**
     * 判断view是否是ItemView
     *
     * @param view
     * @return
     */
    protected static boolean isVcosItemView(View view) {
        return VCOS_ITEM_VIEW_LIST.contains(view.getClass().getName());
    }

    protected static boolean isShowingTopCoverView(int displayId) {
        boolean isShowTopView = false;
        try {
            isShowTopView = com.voyah.ai.sdk.manager.DialogueManager.isShowingTopCoverView(displayId);
        } catch (NoSuchMethodError e) {
            VaLog.e(TAG, "NoSuchMethodError isShowingTopCoverView");
            isShowTopView = com.voyah.ai.sdk.manager.DialogueManager.isShowingTopCoverView();
        }
        return isShowTopView;
    }

    protected static boolean isFragmentVisible(Fragment fragment) {
        boolean userVisibleHint = fragment.getUserVisibleHint();
        try {
            Method method = Fragment.class.getDeclaredMethod("isResumed");
            method.setAccessible(true);
            return ((boolean) method.invoke(fragment) && userVisibleHint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected static boolean isViewDisabled(View view, int maxDepth) {
        if (maxDepth <= 0) {
            return false;
        }
        if (!view.isEnabled()) {
            return true;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            if (!((View) parent).isEnabled()) {
                return true;
            } else {
                return isViewDisabled((View) parent, maxDepth - 1);
            }
        }
        return false;
    }

    protected static boolean isScrollingView(View view) {
        return view instanceof ScrollingView || view instanceof ScrollView || view instanceof HorizontalScrollView
                || view instanceof ViewPager || view instanceof ViewPager2 || getGestureAttr(view) != null;
    }

    /**
     * 通过反射获取view自身设置的onClickListener
     */
    public static View.OnClickListener getViewOnClickListener(View view) {
        View.OnClickListener onClickListener = null;
        try {
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listenerInfo = getListenerInfo.invoke(view);
            Class<?> listenerInfoClass = Class.forName("android.view.View$ListenerInfo");
            Field onClickListenerField = listenerInfoClass.getDeclaredField("mOnClickListener");
            onClickListenerField.setAccessible(true);
            onClickListener = (View.OnClickListener) onClickListenerField.get(listenerInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return onClickListener;
    }

    /**
     * 递归查找
     *
     * @param view
     * @param maxDepth
     * @return
     */
    protected static View.OnClickListener getViewClickListenerWithDepth(View view, int maxDepth) {
        if (maxDepth <= 0) {
            return VoiceViewCmdUtils.getViewOnClickListener(view);
        }

        View.OnClickListener listener = VoiceViewCmdUtils.getViewOnClickListener(view);
        if (listener != null) {
            return listener;
        } else {
            ViewParent parent = view.getParent();
            if (parent instanceof View) {
                return getViewClickListenerWithDepth((View) parent, maxDepth - 1);
            } else {
                return null;
            }
        }
    }

    protected static View getRootView(Object iSupport) {
        View rootView = null;
        if (iSupport instanceof ViewGroup) {
            rootView = (View) iSupport;
        } else if (iSupport instanceof Fragment) {
            Activity activity = ((Fragment) iSupport).getActivity();
            if (activity != null) {
                rootView = activity.getWindow().getDecorView().getRootView();
            }
        } else if (iSupport instanceof Activity) {
            Activity activity = (Activity) iSupport;
            rootView = activity.getWindow().getDecorView().getRootView();
        }
        return rootView;
    }

    /**
     * 检查一个View是否是另一个View的子View
     *
     * @param child
     * @param parent
     * @return
     */
    public static boolean isChildViewOf(View child, View parent) {
        if (child == parent) {
            return true;
        }
        ViewParent currentParent = child.getParent();
        int max = 30;
        while (max-- > 0 && currentParent != null) {
            if (currentParent == parent) {
                return true;
            }
            currentParent = currentParent.getParent();
        }
        return false;
    }

    public static void setLogLevel(int logLevel) {
        VaLog.logLevel = logLevel;
    }

    public static boolean isClickByViewCmd(View view) {
        Object tag = view.getTag(R.string.tag_viewcmd_click);
        return tag != null && mCtx.getString(R.string.tag_viewcmd_click).equals(tag);
    }

    /**
     * 设置不做可见的activity (只注册，但不扫描)
     */
    public static void setNoViewCmdActivity(Activity activity) {
        safePutMapActivity(nonViewCmdActivityMap, activity, true);
    }

    public static boolean isNoViewCmdActivity(Activity activity) {
        Boolean value = safeGetMapActivity(nonViewCmdActivityMap, activity);
        return value != null ? value : false;
    }

    protected static boolean isTextView(View view) {
        String name = view.getClass().getName();
        return "android.widget.TextView".equals(name)
                || (view instanceof TextView && name.endsWith("TextView"));
    }

    protected static boolean isViewInvisible(View view, int maxDepth) {
        if (maxDepth <= 0) {
            return false;
        }
        if (view.getVisibility() != View.VISIBLE) {
            return true;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            if (((View) parent).getVisibility() != View.VISIBLE) {
                return true;
            } else {
                return isViewInvisible((View) parent, maxDepth - 1);
            }
        }
        return false;
    }

    protected static int getCurDirection() {
        try {
            return com.voyah.ai.sdk.manager.DialogueManager.getWakeupDirection();
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
        return -1;
    }

    protected static int getCurVpaDisplayId() {
        try {
            return com.voyah.ai.sdk.manager.DialogueManager.getCurVpaDisplayId();
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static void initDisplayIdParams() {
        try {
            mainDisplayId = com.voyah.ai.sdk.manager.SettingManager.getDisplayIdForScreenType(DhScreenType.SCREEN_MAIN);
            passengerDisplayId = com.voyah.ai.sdk.manager.SettingManager.getDisplayIdForScreenType(DhScreenType.SCREEN_PASSENGER);
            cellingDisplayId = com.voyah.ai.sdk.manager.SettingManager.getDisplayIdForScreenType(DhScreenType.SCREEN_CEILING);
            leftSplitScreenWidth = com.voyah.ai.sdk.manager.SettingManager.getLeftSplitScreenWidth();
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
        if (mainDisplayId == -1) {
            mainDisplayId = 0;
        }
        if (passengerDisplayId == 0 || passengerDisplayId == -1) {
            passengerDisplayId = 3;
        }
        if (cellingDisplayId == 0 || cellingDisplayId == -1) {
            cellingDisplayId = 2;
        }
        if (leftSplitScreenWidth == 0) {
            leftSplitScreenWidth = 850;
        }
    }

    /**
     * 检查用户意图和开关当前状态
     *
     * @return EC_NORMAL：意图与开关不同状态  EC_OPENED：意图打开&当前打开 EC_CLOSED：意图是关闭&当前关闭
     */
    protected static int checkSwitchState(View view, ViewCmdBean bean) {
        if (view instanceof CompoundButton || VCOS_SWITCH.equals(view.getClass().getName())
                || (VoiceViewCmdUtils.hasSwitchAttr(view))) {
            boolean isChecked = false;
            if (view instanceof CompoundButton) {
                isChecked = ((CompoundButton) view).isChecked();
            } else if (VCOS_SWITCH.equals(view.getClass().getName())) {
                try {
                    isChecked = VcosViewUtil.isVCOSSwitchChecked(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (VoiceViewCmdUtils.hasSwitchAttr(view)) {
                isChecked = view.isSelected();
            }
            if (isChecked) {
                for (String prefix : CMDS_SWITCH_OPEN) {
                    if (!TextUtils.equals(bean.text, prefix) && (bean.text.startsWith(prefix) || bean.text.endsWith(prefix))) {
                        return Response.ErrCode.EC_OPENED;
                    }
                }
            } else {
                for (String prefix : CMDS_SWITCH_CLOSE) {
                    if (!TextUtils.equals(bean.text, prefix) && (bean.text.startsWith(prefix) || bean.text.endsWith(prefix))) {
                        return Response.ErrCode.EC_CLOSED;
                    }
                }
            }
        }
        return Response.ErrCode.EC_NORMAL;
    }

    protected static Activity getSupportActivityFromMap(Map<Object, IInterceptor<?>> newSupportObjectMap, List<IInterceptor<?>> interceptors) {
        if (newSupportObjectMap.size() > 0) {
            Activity firstSupport = null;
            Activity otherSupport = null;

            for (Map.Entry<Object, IInterceptor<?>> entry : newSupportObjectMap.entrySet()) {
                Object key = entry.getKey();
                if (key instanceof Activity && VoiceViewCmdUtils.isDialogActivity((Activity) key)) {
                    firstSupport = (Activity) key;
                    IInterceptor<?> iInterceptor = newSupportObjectMap.get(firstSupport);
                    if (iInterceptor != null) {
                        interceptors = Collections.singletonList(iInterceptor);
                    }
                } else {
                    if (key instanceof Activity && !isNoViewCmdActivity((Activity) key)) {
                        otherSupport = (Activity) key;
                        interceptors.clear();
                        if (entry.getValue() != null) {
                            interceptors.add(entry.getValue());
                        }
                    }
                    if (key instanceof Fragment) {
                        Fragment fragment = (Fragment) key;
                        if (VoiceViewCmdUtils.isFragmentVisible(fragment) && fragment.getActivity() == otherSupport) {
                            if (entry.getValue() != null) {
                                interceptors.add(entry.getValue());
                            }
                        }
                    }
                }
            }
            return firstSupport != null ? firstSupport : otherSupport;
        }
        return null;
    }

    protected static boolean isNoFocusDialogView(Map<Object, IInterceptor<?>> map, View view) {
        int windowType = VoiceViewCmdUtils.getWindowType(view);
        Activity attachActivity = VoiceViewCmdUtils.getViewAttachActivity(view);
        return windowType < WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW && attachActivity != null
                && !map.containsKey(attachActivity);
    }
}