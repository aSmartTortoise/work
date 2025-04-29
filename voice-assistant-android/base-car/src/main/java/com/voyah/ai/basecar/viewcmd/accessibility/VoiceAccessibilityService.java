package com.voyah.ai.basecar.viewcmd.accessibility;

import static com.voice.sdk.constant.ApplicationConstant.PKG_THUNDER_KTV;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.voice.sdk.context.AppReportData;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.viewcmd.AccessibleAbilityInterface;
import com.voyah.ai.basecar.manager.DialogueManager;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 安卓无障碍服务
 */
public class VoiceAccessibilityService extends AccessibilityService {

    public static AccessCallback mCallback;
    private static final int DELAY_MILLISECONDS = 500;  // 延迟处理事件的毫秒数

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0 && mCallback != null) {
                int curDisplay = MegaDisplayHelper.getVoiceDisplayId();
                if (curDisplay != -1) {
                    mCallback.handAccessibilityEvent(curDisplay);
                }
            }
        }
    };

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtils.d("onServiceConnected() called(), callback:" + mCallback);
        if (mCallback != null) {
            mCallback.setAccessibilityService(this);
        } else {
            init(this);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("onCreate() called");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            int mainScreenDisplayId = MegaDisplayHelper.getMainScreenDisplayId();
            boolean isSplitScreen = CommonSystemUtils.isSplitScreenState(mainScreenDisplayId);
            String topPackageName = CommonSystemUtils.getTopPackageName(mainScreenDisplayId);
            AppReportData.getInstance().updateFrontAppInfo(isSplitScreen, topPackageName);
        }
        if (!DialogueManager.get().isInteractionState()) {
            //LogUtils.d("voice is no active, return!");
            return;
        }
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                handler.removeMessages(0);
                handler.sendEmptyMessageDelayed(0, DELAY_MILLISECONDS);
                break;
            default:
                break;
        }
    }

    /**
     * 只有外部输入设备才会调用（虚拟键盘没用）
     */
    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        LogUtils.d("onKeyEvent() called with: event = [" + event + "]");
        return super.onKeyEvent(event);
    }

    @Override
    public void onInterrupt() {
        LogUtils.d("onInterrupt() called");
        if (mCallback != null) {
            mCallback.setAccessibilityService(null);
        } else {
            init(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("onDestroy() called");
        if (mCallback != null) {
            mCallback.setAccessibilityService(null);
        } else {
            init(null);
        }
    }

    private synchronized void init(VoiceAccessibilityService service) {
        AccessibleAbilityInterface accessibleAbility = DeviceHolder.INS().getDevices().getViewCmd().getAccessibleAbility();
        if (accessibleAbility != null) {
            accessibleAbility.init();
            if (mCallback != null) {
                mCallback.setAccessibilityService(service);
            }
        }
    }

    /**
     * 点击该控件
     *
     * @return true表示点击成功
     */
    public boolean clickView(int displayId, AccessibilityNodeInfo info) {
        if (info == null) {
            return false;
        }
        boolean ret;
        Rect rect = AbstractTF.mRecycleRect;
        info.getBoundsInScreen(rect);
        int clickY = rect.bottom - 10;
        if (rect.centerX() <= 0 || clickY <= 0) {
            ret = info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            LogUtils.d("clickView by action click, ret:" + ret);
        } else {
            ret = dispatchGestureClick(displayId, rect.centerX(), clickY);
            LogUtils.d("clickView by dispatchGestureClick, ret:" + ret);
        }
        return ret;
    }

    public List<AccessibilityNodeInfo> findAll(AccessibilityNodeInfo rootInfo, @NonNull AbstractTF<?> tfs) {
        if (rootInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> list = new ArrayList<>();

        if (tfs instanceof AbstractTF.IdTextTF) {
            list = ((AbstractTF.IdTextTF) tfs).findAll(rootInfo);
        } else {
            findAllRecursive(list, rootInfo, tfs);
        }

        rootInfo.recycle();
        return list;
    }

    private static void findAllRecursive(List<AccessibilityNodeInfo> list, AccessibilityNodeInfo parent, @NonNull AbstractTF<?> tfs) {
        if (parent == null || list == null) {
            return;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null) {
                continue;
            }
            // 强行适配KTV代码
            if (child.isVisibleToUser() && child.getPackageName() != null && PKG_THUNDER_KTV.equals(child.getPackageName().toString())) {
                CharSequence text = child.getText();
                if (text != null && text.toString().startsWith("已点") && tfs.mCheckData instanceof String && ((String) tfs.mCheckData).startsWith("已点")) {
                    LogUtils.d("add 已点， text:" + text);
                    list.add(child);
                    continue;
                }
            }
            if (tfs.checkOk(child) && child.isVisibleToUser()) {
                list.add(child);
            } else {
                findAllRecursive(list, child, tfs);
                child.recycle();
            }
        }
    }

    /**
     * 立即发送移动的手势
     *
     * @param displayId 屏幕id
     * @param path      移动路径
     * @param mills     持续总时间
     */
    public boolean dispatchGestureMove(int displayId, Path path, long mills) {
        LogUtils.d("dispatchGestureMove() called with: displayId = [" + displayId + "], path = [" + path + "], mills = [" + mills + "]");
        return dispatchGesture(new GestureDescription.Builder().setDisplayId(displayId)
                .addStroke(new GestureDescription.StrokeDescription
                        (path, 0, mills)).build(), null, null);
    }

    /**
     * 点击指定位置
     */
    public boolean dispatchGestureClick(int displayId, int x, int y) {
        Path path = new Path();
        path.moveTo(x - 1, y - 1);
        path.lineTo(x + 1, y + 1);
        return dispatchGesture(new GestureDescription.Builder().setDisplayId(displayId)
                .addStroke(new GestureDescription.StrokeDescription
                        (path, 0, 100)).build(), null, null);
    }

    /**
     * 无障碍服务回调
     */
    public interface AccessCallback {

        /**
         * 设置VoiceAccessibilityService实例
         *
         * @param service 无障碍服务
         */
        void setAccessibilityService(VoiceAccessibilityService service);

        /**
         * 处理指定屏幕的无障碍扫描事件
         *
         * @param displayId 屏幕id
         */
        void handAccessibilityEvent(int displayId);
    }
}