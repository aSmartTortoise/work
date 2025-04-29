package com.voyah.viewcmd;

import static com.voyah.viewcmd.VcosViewUtil.VCOS_BUTTON_LIST;
import static com.voyah.viewcmd.VcosViewUtil.VCOS_SELECTOR_VIEW;
import static com.voyah.viewcmd.VcosViewUtil.VCOS_SWITCH;

import android.graphics.Rect;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.voyah.viewcmd.interceptor.IInterceptor;
import com.voyah.viewcmd.proxy.RecyclerViewProxy;
import com.voyah.viewcmd.proxy.ScrollViewProxy;
import com.voyah.viewcmd.proxy.ViewProxy;

import java.lang.ref.WeakReference;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可见即可说核心处理类
 */
public class CoreProcessor {
    private static final String TAG = CoreProcessor.class.getSimpleName();
    private final ViewCmdAdapter viewCmdAdapter;
    private WeakReference<View> rootViewRef;
    private WeakReference<View> gestureViewRef;
    private static final int MAX_COUNT = 2000;
    private final Map<WeakReference<View>, ViewProxy> viewProxyMap = new ConcurrentHashMap<>();

    public CoreProcessor(ViewCmdAdapter viewCmdAdapter) {
        this.viewCmdAdapter = viewCmdAdapter;
    }


    public VisibleResults getAllVisibleText(View rootView, IInterceptor<?>... interceptors) {
        Set<ViewCmdBean> visibleTexts = new LinkedHashSet<>();
        Set<ViewCmdBean> globalVisibleTexts = new LinkedHashSet<>();
        Set<ViewCmdBean> kwsVisibleTexts = new LinkedHashSet<>();
        Set<String> ignoreTexts = new LinkedHashSet<>();

        this.rootViewRef = new WeakReference<>(rootView);
        // 递归遍历子视图
        traverseView(rootView, visibleTexts, ignoreTexts, interceptors);
        VaLog.v(TAG, "ignoreTexts:" + ignoreTexts);
        // 应用自定义命令处理
        viewCmdAdapter.handleBind(visibleTexts, interceptors);
        viewCmdAdapter.handleGlobalBind(globalVisibleTexts, interceptors);
        viewCmdAdapter.handleKwsBind(kwsVisibleTexts, interceptors);
        if (globalVisibleTexts.size() > 0) {
            visibleTexts.addAll(globalVisibleTexts);
        }
        if (kwsVisibleTexts.size() > 0) {
            visibleTexts.addAll(kwsVisibleTexts);
        }
        return VoiceViewCmdUtils.distinct(visibleTexts);
    }

    /**
     * 通用方法, 响应界面上所有可即可说文本
     */
    public int processVoiceInput(View rootView, int displayId, ViewCmdBean bean, List<View> matchedViews) {
        dump();
        Set<View> targetViews = viewCmdAdapter.mViewMap.get(bean);
        if (targetViews != null) {
            matchedViews.addAll(targetViews);
        }
        VaLog.d(TAG, "processVoiceInput bean:" + bean + ", by map matchedViews size:" + matchedViews.size());
        traverseView(rootView, bean.text, matchedViews);
        VaLog.d(TAG, "processVoiceInput bean:" + bean + ", by text matchedViews size:" + matchedViews.size());
        if (!matchedViews.isEmpty()) {
            return 0;
        }
        Integer resId = viewCmdAdapter.mResIdMap.get(bean);
        VaLog.d(TAG, "processVoiceInput bean:" + bean + ",resId:" + resId);

        if (resId != null && resId >= 0 && resId < GestureUtils.RES_ID_GESTURE_MAX) {
            Set<View> gestureViews = viewCmdAdapter.mGestureViewMap.get(resId);
            if (gestureViews != null) {
                for (View view : gestureViews) {
                    if (VoiceViewCmdUtils.getDisplayId(view) == displayId) {
                        gestureViewRef = new WeakReference<>(view);
                    }
                }
            }
            return resId;
        }
        return -1;
    }

    private void dump() {
        VaLog.d(TAG, "===== dump begin ===== ");
        for (Map.Entry<ViewCmdBean, Set<View>> entry : viewCmdAdapter.mViewMap.entrySet()) {
            VaLog.d(TAG, "  mViewMap key:" + entry.getKey() + ",value:" + entry.getValue());
        }
        for (Map.Entry<ViewCmdBean, Integer> entry : viewCmdAdapter.mResIdMap.entrySet()) {
            VaLog.d(TAG, "  mResIdMap key:" + entry.getKey() + ",value:" + entry.getValue());
        }
        VaLog.d(TAG, "===== dump end ===== ");
    }

    public View getGestureView() {
        return gestureViewRef != null ? gestureViewRef.get() : null;
    }

    /**
     * 判断view是否对用户可见
     */
    private boolean isViewVisibleOnScreen(View view) {
        if (rootViewRef == null) {
            return false;
        }
        View rootView = rootViewRef.get();
        if (rootView == null) {
            return false;
        }

        if (isSimpleJudgeAnimating(view)) {
            return true;
        }

        Rect scrollBounds = new Rect();
        rootView.getHitRect(scrollBounds);

        if (!view.getLocalVisibleRect(scrollBounds)) {
            return false;
        }

        float visibleArea = (float) (scrollBounds.width() * scrollBounds.height());
        float totalArea = (float) (view.getWidth() * view.getHeight());
        float visibilityRatio = visibleArea / totalArea;
        // 可见面积小于50%则判定为不可见
        if (!(view instanceof ViewGroup) && visibilityRatio < 0.5) {
            return false;
        }

        return true;
    }

    private boolean isSimpleJudgeAnimating(View view) {
        float translationX = view.getTranslationX();
        float translationY = view.getTranslationY();
        float translationZ = view.getTranslationZ();
        float scaleX = view.getScaleX();
        float scaleY = view.getScaleY();
        float rotationX = view.getRotationX();
        float rotationY = view.getRotationY();
        float rotationZ = view.getRotation();
        // Log.d(TAG, "translationX:" + translationX + ",translationY:" + translationY + ",translationZ:" + translationZ
        //        + ",scaleX:" + scaleX + ",scaleY:" + scaleX + ",rotationX:" + rotationX + ",rotationY:" + rotationY + ",rotationZ:" + rotationZ);
        return translationX != 0 || translationY != 0 || translationZ != 0 || scaleX != 1.0f
                || scaleY != 1.0f || rotationX != 0 || rotationY != 0 || rotationZ != 0;
    }

    /**
     * 注册扫描
     */
    private void traverseView(View view, Set<ViewCmdBean> visibleTexts, Set<String> ignoreTexts, IInterceptor<?>... interceptors) {
        if (view != null && view.getVisibility() == View.VISIBLE && isViewVisibleOnScreen(view)) {
            if (VoiceViewCmdUtils.isScrollingView(view)) {
                String orientation = VoiceViewCmdUtils.getGestureAttr(view);
                if (orientation != null) {
                    viewCmdAdapter.handleGesture(view, orientation, visibleTexts);
                }
                handleAutoRefresh(view);
            }
            if (view instanceof ImageView) {
                viewCmdAdapter.handleImageView((ImageView) view, visibleTexts, interceptors);
            } else if (view instanceof CompoundButton) {
                viewCmdAdapter.handleCompoundButton((CompoundButton) view, visibleTexts, interceptors);
            } else if (view instanceof TextView) {
                viewCmdAdapter.handleTextView((TextView) view, visibleTexts, ignoreTexts, interceptors);
            } else if (VCOS_SELECTOR_VIEW.equals(view.getClass().getName())) {
                viewCmdAdapter.handleVCosSelectorView(view, visibleTexts, interceptors);
            } else if (VCOS_SWITCH.equals(view.getClass().getName())) {
                viewCmdAdapter.handlerVCOSSwitch(view, visibleTexts, interceptors);
            } else if (VCOS_BUTTON_LIST.contains(view.getClass().getName())) {
                viewCmdAdapter.handleVCosButton(view, visibleTexts, interceptors);
            } else if (view instanceof RecyclerView) {
                if (VoiceViewCmdUtils.isDescText(view)) {
                    //VaLog.d(TAG, "RecyclerView isDescText=true, ignore!!!, RecyclerView:" + view);
                    return;
                }
                RecyclerView recyclerView = (RecyclerView) view;
                RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
                if (adapter != null) {
                    long startTime = System.currentTimeMillis();
                    while (recyclerView.hasPendingAdapterUpdates() && (System.currentTimeMillis() - startTime) < 1000) {
                        VaLog.e(TAG, "recyclerView hasPendingAdapterUpdates()=true, wait 100ms!");
                        SystemClock.sleep(100);
                    }
                    if (recyclerView.hasPendingAdapterUpdates()) {
                        VaLog.e(TAG, "recyclerView hasPendingAdapterUpdates()=true, return!");
                        return;
                    }
                    int itemCount = adapter.getItemCount();
                    // 规避列表设置一个无限大的值，导致程序卡死
                    if (itemCount > MAX_COUNT) {
                        VaLog.e(TAG, "RecyclerView has a unreachable size, itemCount:" + itemCount);
                        itemCount = MAX_COUNT;
                    }
                    for (int i = 0; i < itemCount; i++) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                        if (viewHolder != null && viewHolder.itemView.getVisibility() == View.VISIBLE) {
                            traverseView(viewHolder.itemView, visibleTexts, ignoreTexts, interceptors);
                        }
                    }
                }
            } else if (view instanceof ViewGroup) {
                if (VoiceViewCmdUtils.isDescText(view)) {
                    //VaLog.v(TAG, "viewGroup isDescText=true, ignore!!!, viewGroup:" + view);
                    return;
                }

                if (VoiceViewCmdUtils.getUnsafe(view) != null) {
                    //VaLog.v(TAG, "viewGroup has set unsafe attr:" + view);
                    viewCmdAdapter.setUnsafeAttr(view, VoiceViewCmdUtils.getUnsafe(view));
                }

                if (VoiceViewCmdUtils.getPriority(view) != null) {
                    //VaLog.v(TAG, "viewGroup has set priority attr:" + view);
                    viewCmdAdapter.setPriorityAttr(view, VoiceViewCmdUtils.getPriority(view));
                }
                String type = VoiceViewCmdUtils.getViewCmdTypeAttr(view);
                if (ViewCmdType.TYPE_GLOBAL.equals(type) || ViewCmdType.TYPE_KWS.equals(type)) {
                    //VaLog.v(TAG, "viewGroup has set viewCmdType attr:" + view + ", type:" + type);
                    viewCmdAdapter.seViewCmdType(view, type);
                }

                if (VoiceViewCmdUtils.isVcosItemView(view)) {
                    viewCmdAdapter.setVCOSItemViewAttr(view, view.getClass().getName());
                }

                if (VoiceViewCmdUtils.isTabLayout(view)) {
                    viewCmdAdapter.setTabLayoutAttr(view, view);
                }

                if (VoiceViewCmdUtils.getViewCmdAttr(view) != null) {
                    viewCmdAdapter.handleViewGroup(view, visibleTexts, interceptors);
                }

                if (view instanceof ViewPager) {
                    //VaLog.v(TAG, "shot viewPager:" + view);
                    View childView = ViewPagerUtil.getViewPagerFragmentView((ViewPager) view);
                    if (childView != null) {
                        traverseView(childView, visibleTexts, ignoreTexts, interceptors);
                        return;
                    }
                }
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                if (childCount > MAX_COUNT) {
                    VaLog.e(TAG, "ViewGroup has a unreachable size, childCount:" + childCount);
                    childCount = MAX_COUNT;
                }
                for (int i = 0; i < childCount; i++) {
                    View childView = viewGroup.getChildAt(i);
                    traverseView(childView, visibleTexts, ignoreTexts, interceptors);
                }

            } else if (VoiceViewCmdUtils.getViewCmdAttr(view) != null) {
                viewCmdAdapter.handleSpecialView(view, visibleTexts, interceptors);
            }
        }
    }


    private void handleAutoRefresh(View view) {
        ViewProxy proxy = WeakReferenceUtil.safeGetMap(viewProxyMap, view);
        if (proxy != null) {
            return;
        }
        ViewProxy viewProxy = null;
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            viewProxy = new RecyclerViewProxy(recyclerView);
            WeakReferenceUtil.safePutMap(viewProxyMap, view, viewProxy);
        } else if (VoiceViewCmdUtils.isScrollingView(view)) {
            viewProxy = new ScrollViewProxy(view);
            WeakReferenceUtil.safePutMap(viewProxyMap, view, viewProxy);
        }

        if (viewProxy != null) {
            viewProxy.setOnScrollViewListener(new OnScrollViewListener() {
                @Override
                public void onScrollStop() {
                    VaLog.d(TAG, view.getClass().getSimpleName() + " onScrollStop() called");
                    VoiceViewCmdManager.getInstance().triggerNotifyUiChangeById(VoiceViewCmdUtils.getDisplayId(view));
                }

                @Override
                public void onDetachedFromWindow() {
                    ViewProxy proxy = WeakReferenceUtil.safeGetMap(viewProxyMap, view);
                    if (proxy != null) {
                        proxy.destroy();
                        WeakReferenceUtil.safePutMap(viewProxyMap, view, null);
                    }
                }
            });
            viewProxy.init();
        }
    }


    /**
     * 响应扫描
     */
    private void traverseView(View view, String word, List<View> matchedViews) {
        if (view.getVisibility() == View.VISIBLE && isViewVisibleOnScreen(view)) {
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                boolean isDescText = VoiceViewCmdUtils.isDescText(textView);
                if (!isDescText) {
                    CharSequence viewText = textView.getText();
                    if (!TextUtils.isEmpty(viewText) && viewText.toString().equals(word) && !matchedViews.contains(view)) {
                        matchedViews.add(view);
                    }
                }
            } else if (VCOS_SELECTOR_VIEW.equals(view.getClass().getName())) {
                boolean isDescText = VoiceViewCmdUtils.isDescText(view);
                if (!isDescText) {
                    String viewText = VcosViewUtil.getVCOSSelectorViewText(view);
                    if (viewText != null && viewText.equals(word) && !matchedViews.contains(view)) {
                        matchedViews.add(view);
                    }
                }
            } else if (view instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) view;
                RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
                if (adapter != null) {
                    int itemCount = adapter.getItemCount();
                    // 规避列表设置一个无限大的值，导致程序卡死
                    if (itemCount > MAX_COUNT) {
                        VaLog.e(TAG, "RecyclerView has a unreachable size, itemCount:" + itemCount);
                        itemCount = MAX_COUNT;
                    }
                    for (int i = 0; i < itemCount; i++) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                        if (viewHolder != null) {
                            View itemView = viewHolder.itemView;
                            if (itemView instanceof ViewGroup) {
                                ViewGroup viewGroup = (ViewGroup) itemView;
                                int childCount = viewGroup.getChildCount();
                                for (int j = 0; j < childCount; j++) {
                                    View childView = viewGroup.getChildAt(j);
                                    traverseView(childView, word, matchedViews);
                                }
                            } else if (itemView instanceof TextView) {
                                traverseView(itemView, word, matchedViews);
                            } else {
                                VaLog.d(TAG, "no handle case, unknown RecyclerView.ViewHolder");
                            }
                        }
                    }
                } else {
                    VaLog.e(TAG, "RecyclerView's adapter=null, view:" + view);
                }
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                if (childCount > MAX_COUNT) {
                    VaLog.e(TAG, "ViewGroup has a unreachable size, childCount:" + childCount);
                    childCount = MAX_COUNT;
                }
                for (int i = 0; i < childCount; i++) {
                    View childView = viewGroup.getChildAt(i);
                    traverseView(childView, word, matchedViews);
                }
            }
        }
    }

    public void clear() {
        viewCmdAdapter.clear();
    }
}
