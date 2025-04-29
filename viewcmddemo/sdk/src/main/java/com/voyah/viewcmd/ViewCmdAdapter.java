package com.voyah.viewcmd;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;
import com.voyah.viewcmd.interceptor.IInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.voyah.viewcmd.PromptGen.CMDS_GESTURE_DOWN;
import static com.voyah.viewcmd.PromptGen.CMDS_GESTURE_DOWN_END;
import static com.voyah.viewcmd.PromptGen.CMDS_GESTURE_LEFT;
import static com.voyah.viewcmd.PromptGen.CMDS_GESTURE_LEFT_BEGIN;
import static com.voyah.viewcmd.PromptGen.CMDS_GESTURE_PAGE_DOWN;
import static com.voyah.viewcmd.PromptGen.CMDS_GESTURE_PAGE_DOWN_END;
import static com.voyah.viewcmd.PromptGen.CMDS_GESTURE_PAGE_UP;
import static com.voyah.viewcmd.PromptGen.CMDS_GESTURE_PAGE_UP_BEGIN;
import static com.voyah.viewcmd.PromptGen.CMDS_GESTURE_RIGHT;
import static com.voyah.viewcmd.PromptGen.CMDS_GESTURE_RIGHT_END;
import static com.voyah.viewcmd.PromptGen.CMDS_GESTURE_UP;
import static com.voyah.viewcmd.PromptGen.CMDS_GESTURE_UP_BEGIN;
import static com.voyah.viewcmd.PromptGen.CMDS_LIST_AIMODEL;
import static com.voyah.viewcmd.PromptGen.CMDS_LIST_AUDIO;
import static com.voyah.viewcmd.PromptGen.CMDS_LIST_COMMON;
import static com.voyah.viewcmd.PromptGen.CMDS_LIST_CONTACT;
import static com.voyah.viewcmd.PromptGen.CMDS_LIST_GAME;
import static com.voyah.viewcmd.PromptGen.CMDS_LIST_KTV;
import static com.voyah.viewcmd.PromptGen.CMDS_LIST_PICTURE;
import static com.voyah.viewcmd.PromptGen.CMDS_LIST_POI;
import static com.voyah.viewcmd.PromptGen.CMDS_LIST_RECORDING;
import static com.voyah.viewcmd.PromptGen.CMDS_LIST_SCHEDULE;
import static com.voyah.viewcmd.PromptGen.CMDS_LIST_VIDEO;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_AIMODEL;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_AIMODEL_SUFFIX;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_AUDIO;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_AUDIO_SUFFIX;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_COMMON;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_COMMON_SUFFIX;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_CONTACT;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_CONTACT_SUFFIX;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_GAME;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_GAME_SUFFIX;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_KTV;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_KTV_SUFFIX;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_PICTURE;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_PICTURE_SUFFIX;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_POI;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_POI_SUFFIX;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_RECORDING;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_RECORDING_SUFFIX;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_SCHEDULE;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_SCHEDULE_SUFFIX;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_VIDEO;
import static com.voyah.viewcmd.PromptGen.CMDS_BADGE_VIDEO_SUFFIX;
import static com.voyah.viewcmd.PromptGen.CMDS_SWITCH_CLOSE;
import static com.voyah.viewcmd.PromptGen.CMDS_SWITCH_OPEN;
import static com.voyah.viewcmd.PromptGen.CMDS_TAB;
import static com.voyah.viewcmd.VcosViewUtil.VCOS_ITEM_VIEW_LIST;

/**
 * 可见即可说特殊适配：扫描过程中对控件的代码层的处理
 */
class ViewCmdAdapter {
    private static final String TAG = ViewCmdAdapter.class.getSimpleName();

    public final Map<ViewCmdBean, Integer> mResIdMap = new ConcurrentHashMap<>();

    public final Map<ViewCmdBean, Set<View>> mViewMap = new ConcurrentHashMap<>();

    public final Map<Integer, Set<View>> mGestureViewMap = new ConcurrentHashMap<>();

    /**
     * 开关本地泛化标识
     */
    private final boolean mLocalGenForSwitchFlag = true;

    protected void handleImageView(ImageView imageView, Set<ViewCmdBean> visibleTexts, IInterceptor<?>... interceptors) {
        // 布局中的处理
        String[] strArr = VoiceViewCmdUtils.getViewCmdAttr(imageView);
        if (strArr != null) {
            addCommandLoop(visibleTexts, null, strArr, imageView);

            if (VoiceViewCmdUtils.hasSwitchAttr(imageView)) {
                for (String str : strArr) {
                    handleViewSwitchAttr(visibleTexts, imageView, str);
                }
            }
        }
        // 代码中的处理
        List<String> list = strArr != null ? Arrays.asList(strArr) : null;
        handleBindLoop(imageView, list, visibleTexts, interceptors);
    }

    protected void handleCompoundButton(CompoundButton view, Set<ViewCmdBean> visibleTexts, IInterceptor<?>... interceptors) {
        List<String> list = new ArrayList<>();
        CharSequence cbDesc = view.getText();
        String text = (cbDesc != null) ? cbDesc.toString() : null;
        String[] strArr = VoiceViewCmdUtils.getViewCmdAttr(view);
        if (!TextUtils.isEmpty(text)) {
            list.add(text);
        }
        if (strArr != null) {
            list.addAll(Arrays.asList(strArr));
        }
        if (list.size() > 0) {
            handleCompoundButtonInner(view, visibleTexts, list);
        }
        // 代码中的处理
        handleBindLoop(view, list, visibleTexts, interceptors);
    }

    private void handleCompoundButtonInner(View view, Set<ViewCmdBean> visibleTexts, List<String> list) {
        if (view instanceof RadioButton) { // 针对radioButton修改前缀
            if (VoiceViewCmdUtils.isCompatibleMode) {
                for (String str : list) {
                    addCommand(visibleTexts, null, str, view);
                    for (String prefix : CMDS_TAB) {
                        addCommand(visibleTexts, null, prefix + str, view);
                    }
                }
            } else {
                addCommandLoop(visibleTexts, Prompt.PROMPT_TAB, list.toArray(new String[0]), view);
            }
        } else {
            if (VoiceViewCmdUtils.isCompatibleMode || mLocalGenForSwitchFlag) {
                String prompt = VoiceViewCmdUtils.isCompatibleMode ? null : (mLocalGenForSwitchFlag ? Prompt.PROMPT_SWITCH : null);
                for (String str : list) {
                    addCommand(visibleTexts, prompt, str, view);
                    for (String prefix : CMDS_SWITCH_OPEN) {
                        addCommand(visibleTexts, prompt, prefix + str, view);
                        addCommand(visibleTexts, prompt, str + prefix, view);
                    }
                    for (String prefix : CMDS_SWITCH_CLOSE) {
                        addCommand(visibleTexts, prompt, prefix + str, view);
                        addCommand(visibleTexts, prompt, str + prefix, view);
                    }
                }
            } else {
                addCommandLoop(visibleTexts, Prompt.PROMPT_SWITCH, list.toArray(new String[0]), view);
            }
        }
    }

    protected void handleTextView(TextView textView, Set<ViewCmdBean> visibleTexts, Set<String> ignoreTexts, IInterceptor<?>... interceptors) {
        boolean isDescText = VoiceViewCmdUtils.isDescText(textView);
        if (!isDescText) {
            List<String> list = new ArrayList<>();
            String text = (textView.getText() != null) ? textView.getText().toString() : null;
            boolean hasClickListener = true;
            if (VoiceViewCmdUtils.isTextView(textView) && text != null && text.trim().length() > 0) {
                View bindClickView = VoiceViewCmdUtils.getBindClickView(textView);
                if (bindClickView == null) {
                    View.OnClickListener listener = VoiceViewCmdUtils.getViewClickListenerWithDepth(textView, 2);
                    if (listener == null) {
                        ignoreTexts.add(text);
                        hasClickListener = false;
                    }
                }
            }
            if (hasClickListener && !TextUtils.isEmpty(text)) {
                list.add(text);
            }
            String[] strArr = VoiceViewCmdUtils.getViewCmdAttr(textView);
            if (strArr != null) {
                addCommandLoop(visibleTexts, null, strArr, textView);
                list.addAll(Arrays.asList(strArr));
            }
            if (list.size() > 0) {
                for (String str : list) {
                    handleViewAttrCommon(textView, visibleTexts, str);
                }
            }

            // 代码中处理
            handleBindLoop(textView, list, visibleTexts, interceptors);
        }
    }

    public void handleVCosSelectorView(View view, Set<ViewCmdBean> visibleTexts, IInterceptor<?>[] interceptors) {
        boolean isDescText = VoiceViewCmdUtils.isDescText(view);
        if (!isDescText) {
            List<String> list = new ArrayList<>();
            String text = VcosViewUtil.getVCOSSelectorViewText(view);
            if (!TextUtils.isEmpty(text)) {
                list.add(text);
            }
            String[] strArr = VoiceViewCmdUtils.getViewCmdAttr(view);
            if (strArr != null) {
                addCommandLoop(visibleTexts, null, strArr, view);
                list.addAll(Arrays.asList(strArr));
            }

            if (list.size() > 0) {
                for (String str : list) {
                    handleViewAttrCommon(view, visibleTexts, str);
                }
            }

            // 代码中处理
            handleBindLoop(view, list, visibleTexts, interceptors);
        }
    }

    public void handleVCosButton(View view, Set<ViewCmdBean> visibleTexts, IInterceptor<?>[] interceptors) {
        boolean isDescText = VoiceViewCmdUtils.isDescText(view);
        if (!isDescText) {
            List<String> list = new ArrayList<>();
            String text = VcosViewUtil.getVCOSButtonText(view);
            if (!TextUtils.isEmpty(text)) {
                list.add(text);
            }
            String[] strArr = VoiceViewCmdUtils.getViewCmdAttr(view);
            if (strArr != null) {
                addCommandLoop(visibleTexts, null, strArr, view);
                list.addAll(Arrays.asList(strArr));
            }

            if (list.size() > 0) {
                for (String str : list) {
                    handleViewAttrCommon(view, visibleTexts, str);
                }
            }

            // 代码中处理
            handleBindLoop(view, list, visibleTexts, interceptors);
        }
    }

    public void handlerVCOSSwitch(View view, Set<ViewCmdBean> visibleTexts, IInterceptor<?>[] interceptors) {
        String[] strArr = VoiceViewCmdUtils.getViewCmdAttr(view);
        if (strArr != null) {
            try {
                if (VoiceViewCmdUtils.isCompatibleMode || mLocalGenForSwitchFlag) {
                    String prompt = VoiceViewCmdUtils.isCompatibleMode ? null : (mLocalGenForSwitchFlag ? Prompt.PROMPT_SWITCH : null);
                    for (String str : strArr) {
                        addCommand(visibleTexts, prompt, str, view);
                        for (String prefix : CMDS_SWITCH_OPEN) {
                            addCommand(visibleTexts, prompt, prefix + str, view);
                            addCommand(visibleTexts, prompt, str + prefix, view);
                        }
                        for (String prefix : CMDS_SWITCH_CLOSE) {
                            addCommand(visibleTexts, prompt, prefix + str, view);
                            addCommand(visibleTexts, prompt, str + prefix, view);
                        }
                    }
                } else {
                    addCommandLoop(visibleTexts, Prompt.PROMPT_SWITCH, strArr, view);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 代码中的处理
        List<String> list = strArr != null ? Arrays.asList(strArr) : null;
        handleBindLoop(view, list, visibleTexts, interceptors);
    }

    public void setTabLayoutAttr(View view, View tabLayout) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                VoiceViewCmdUtils.setIsTab(child);
                if (child instanceof ViewGroup) {
                    setTabLayoutAttr(child, tabLayout);
                } else if (child instanceof TextView) {
                    if (tabLayout instanceof TabLayout) {
                        ViewParent parent = child.getParent();
                        while (parent != null) {
                            if (parent instanceof TabLayout.TabView) {
                                VoiceViewCmdUtils.setBindClickView(child, (View) parent);
                                break;
                            }
                            parent = parent.getParent();
                        }
                    }
                }
            }
        } else if (view instanceof TextView) {
            VoiceViewCmdUtils.setIsTab(view);
            if (tabLayout instanceof TabLayout) {
                ViewParent parent = view.getParent();
                while (parent != null) {
                    if (parent instanceof TabLayout.TabView) {
                        VoiceViewCmdUtils.setBindClickView(view, (View) parent);
                        break;
                    }
                    parent = parent.getParent();
                }
            }
        }
    }

    public void setUnsafeAttr(@NonNull View view, String attr) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                VoiceViewCmdUtils.setUnsafeOpe(view, attr);

                if (child instanceof ViewGroup) {
                    setUnsafeAttr(child, attr);
                } else {
                    VoiceViewCmdUtils.setUnsafeOpe(child, attr);
                }
            }
        } else {
            VoiceViewCmdUtils.setUnsafeOpe(view, attr);
        }
    }

    public void setPriorityAttr(@NonNull View view, String attr) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                VoiceViewCmdUtils.setPriority(view, attr);

                if (child instanceof ViewGroup) {
                    setPriorityAttr(child, attr);
                } else {
                    VoiceViewCmdUtils.setPriority(child, attr);
                }
            }
        } else {
            VoiceViewCmdUtils.setPriority(view, attr);
        }
    }

    public void seViewCmdType(@NonNull View view, String type) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                VoiceViewCmdUtils.setViewCmdType(view, type);

                if (child instanceof ViewGroup) {
                    seViewCmdType(child, type);
                } else {
                    VoiceViewCmdUtils.setViewCmdType(child, type);
                }
            }
        } else {
            VoiceViewCmdUtils.setViewCmdType(view, type);
        }
    }

    private void handleViewSwitchAttr(Set<ViewCmdBean> visibleTexts, View view, String str) {
        if (VoiceViewCmdUtils.isCompatibleMode || mLocalGenForSwitchFlag) {
            String prompt = VoiceViewCmdUtils.isCompatibleMode ? null : (mLocalGenForSwitchFlag ? Prompt.PROMPT_SWITCH : null);
            addCommand(visibleTexts, prompt, str, view);
            for (String prefix : CMDS_SWITCH_OPEN) {
                addCommand(visibleTexts, prompt, prefix + str, view);
                addCommand(visibleTexts, prompt, str + prefix, view);
            }
            for (String prefix : CMDS_SWITCH_CLOSE) {
                addCommand(visibleTexts, prompt, prefix + str, view);
                addCommand(visibleTexts, prompt, str + prefix, view);
            }
        } else {
            addCommand(visibleTexts, Prompt.PROMPT_SWITCH, str, view);
        }
    }

    private void handleViewTabAttr(Set<ViewCmdBean> visibleTexts, View view, String str) {
        if (VoiceViewCmdUtils.isCompatibleMode) {
            visibleTexts.add(new ViewCmdBean(null, str, VoiceViewCmdUtils.getViewCmdTypeAttr(view)));
            processGeneric(CMDS_TAB, str, visibleTexts, view);
        } else {
            addCommand(visibleTexts, Prompt.PROMPT_TAB, str, view);
        }
    }


    public void setVCOSItemViewAttr(View view, String name) {
        if (VCOS_ITEM_VIEW_LIST.contains(name)) {
            Resources resources = view.getContext().getResources();
            String pkgName = view.getContext().getPackageName();
            int swBtnId = resources.getIdentifier("sw_btn", "id", pkgName);
            int tvTitleId = resources.getIdentifier("tv_left_msg", "id", pkgName);
            int tvSubTitleId = resources.getIdentifier("tv_left_sub_msg", "id", pkgName);
            View swBtn = view.findViewById(swBtnId);
            TextView tvTitle = view.findViewById(tvTitleId);
            TextView tvSubTitle = view.findViewById(tvSubTitleId);
            if (swBtn != null && tvTitle != null && tvSubTitle != null) {
                if (swBtn.getVisibility() == View.VISIBLE) {
                    VoiceViewCmdUtils.setViewCmd(swBtn, tvTitle.getText().toString());
                    VoiceViewCmdUtils.setIsDescText(tvTitle);
                } else {
                    View.OnClickListener listener = VoiceViewCmdUtils.getViewOnClickListener(tvTitle);
                    if (listener == null) {
                        VoiceViewCmdUtils.setBindClickView(tvTitle, (View) tvTitle.getParent().getParent());
                    }
                }
                VoiceViewCmdUtils.setIsDescText(tvSubTitle);
            }
        }
    }

    private void handleViewListAttr(Set<ViewCmdBean> visibleTexts, View view, String str, String listAttr) {
        if (VoiceViewCmdUtils.isCompatibleMode) {
            visibleTexts.add(new ViewCmdBean(null, str, VoiceViewCmdUtils.getViewCmdTypeAttr(view)));
            switch (listAttr.toLowerCase()) {
                case "poi":
                case "route":
                    processGeneric(CMDS_LIST_POI, str, visibleTexts, view);
                    break;
                case "music":
                case "book":
                case "radio":
                case "audio":
                    processGeneric(CMDS_LIST_AUDIO, str, visibleTexts, view);
                    break;
                case "video":
                case "shortvideo":
                    processGeneric(CMDS_LIST_VIDEO, str, visibleTexts, view);
                    break;
                case "ktv":
                    processGeneric(CMDS_LIST_KTV, str, visibleTexts, view);
                    break;
                case "contact":
                    processGeneric(CMDS_LIST_CONTACT, str, visibleTexts, view);
                    break;
                case "game":
                    processGeneric(CMDS_LIST_GAME, str, visibleTexts, view);
                    break;
                case "picture":
                    processGeneric(CMDS_LIST_PICTURE, str, visibleTexts, view);
                    break;
                case "recording":
                    processGeneric(CMDS_LIST_RECORDING, str, visibleTexts, view);
                    break;
                case "schedule":
                    processGeneric(CMDS_LIST_SCHEDULE, str, visibleTexts, view);
                    break;
                case "aimodel":
                    processGeneric(CMDS_LIST_AIMODEL, str, visibleTexts, view);
                    break;
                default:
                    processGeneric(CMDS_LIST_COMMON, str, visibleTexts, view);
                    break;
            }
        } else {
            String prompt = Prompt.create(Prompt._PROMPT_TYPE_LIST, listAttr);
            addCommand(visibleTexts, prompt, str, view);
        }
    }

    private void handleViewBadgeAttr(Set<ViewCmdBean> visibleTexts, View view, String str, String badgeAttr) {
        if (str != null && !str.matches("\\d+")) {
            return;
        }
        if (VoiceViewCmdUtils.isCompatibleMode) {
            visibleTexts.add(new ViewCmdBean(null, str, VoiceViewCmdUtils.getViewCmdTypeAttr(view)));
            switch (badgeAttr.toLowerCase()) {
                case "poi":
                case "route":
                    for (String suffix : CMDS_BADGE_POI_SUFFIX) {
                        processGeneric(CMDS_BADGE_POI, "第" + str + suffix, visibleTexts, view);
                    }
                    break;
                case "music":
                case "book":
                case "radio":
                case "audio":
                    for (String suffix : CMDS_BADGE_AUDIO_SUFFIX) {
                        processGeneric(CMDS_BADGE_AUDIO, "第" + str + suffix, visibleTexts, view);
                    }
                    break;
                case "video":
                case "shortvideo":
                    for (String suffix : CMDS_BADGE_VIDEO_SUFFIX) {
                        processGeneric(CMDS_BADGE_VIDEO, "第" + str + suffix, visibleTexts, view);
                    }
                    break;
                case "ktv":
                    for (String suffix : CMDS_BADGE_KTV_SUFFIX) {
                        processGeneric(CMDS_BADGE_KTV, "第" + str + suffix, visibleTexts, view);
                    }
                    break;
                case "contact":
                    for (String suffix : CMDS_BADGE_CONTACT_SUFFIX) {
                        processGeneric(CMDS_BADGE_CONTACT, "第" + str + suffix, visibleTexts, view);
                    }
                    break;
                case "game":
                    for (String suffix : CMDS_BADGE_GAME_SUFFIX) {
                        processGeneric(CMDS_BADGE_GAME, "第" + str + suffix, visibleTexts, view);
                    }
                    break;
                case "picture":
                    for (String suffix : CMDS_BADGE_PICTURE_SUFFIX) {
                        processGeneric(CMDS_BADGE_PICTURE, "第" + str + suffix, visibleTexts, view);
                    }
                    break;
                case "recording":
                    for (String suffix : CMDS_BADGE_RECORDING_SUFFIX) {
                        processGeneric(CMDS_BADGE_RECORDING, "第" + str + suffix, visibleTexts, view);
                    }
                    break;
                case "schedule":
                    for (String suffix : CMDS_BADGE_SCHEDULE_SUFFIX) {
                        processGeneric(CMDS_BADGE_SCHEDULE, "第" + str + suffix, visibleTexts, view);
                    }
                    break;
                case "aimodel":
                    for (String suffix : CMDS_BADGE_AIMODEL_SUFFIX) {
                        processGeneric(CMDS_BADGE_AIMODEL, "第" + str + suffix, visibleTexts, view);
                    }
                    break;
                default:
                    for (String suffix : CMDS_BADGE_COMMON_SUFFIX) {
                        processGeneric(CMDS_BADGE_COMMON, "第" + str + suffix, visibleTexts, view);
                    }
                    break;
            }
        } else {
            String prompt = Prompt.create(Prompt._PROMPT_TYPE_BADGE, badgeAttr);
            addCommand(visibleTexts, prompt, str, view);
        }
    }

    public void handleGesture(View view, String orientation, Set<ViewCmdBean> visibleTexts) {
        Integer gesture = GestureUtils.ORIENTATION_MAP.get(orientation);
        if (gesture == null) {
            VaLog.e(TAG, "handleGesture, illegal orientation:" + orientation);
            return;
        }
        int value = GestureUtils.BIT_MASK & gesture;
        VaLog.v(TAG, "handleGesture, orientation:" + orientation + ",value:" + value + ", view:" + view);
        boolean vertical = (value & GestureUtils.BIT_VERTICAL) != 0;
        boolean horizontal = (value & GestureUtils.BIT_HORIZONTAL) != 0;
        boolean page = (value & GestureUtils.BIT_PAGE) != 0;
        int displayId = VoiceViewCmdUtils.getDisplayId(view);

        // 同一方向只支持一个手势
        if (vertical && (hasContainGesture(displayId, GestureUtils.RES_ID_GESTURE_UP)
                || hasContainGesture(displayId, GestureUtils.RES_ID_GESTURE_DOWN))) {
            VaLog.e(TAG, "handleGesture, have contain a vertical gesture, ignore gesture view:" + view);
            return;
        }
        if (horizontal && (hasContainGesture(displayId, GestureUtils.RES_ID_GESTURE_LEFT)
                || hasContainGesture(displayId, GestureUtils.RES_ID_GESTURE_RIGHT))) {
            VaLog.e(TAG, "handleGesture, have contain a horizontal gesture, ignore gesture view:" + view);
            return;
        }
        if (page && (hasContainGesture(displayId, GestureUtils.RES_ID_GESTURE_PAGE_UP)
                || hasContainGesture(displayId, GestureUtils.RES_ID_GESTURE_PAGE_DOWN))) {
            VaLog.e(TAG, "handleGesture, have contain a page gesture, ignore gesture view:" + view);
            return;
        }

        String type = VoiceViewCmdUtils.getViewCmdTypeAttr(view);
        if (VoiceViewCmdUtils.isCompatibleMode) {
            if (vertical) {
                addCommandLoop(visibleTexts, null, CMDS_GESTURE_UP, GestureUtils.RES_ID_GESTURE_UP, type);
                addCommandLoop(visibleTexts, null, CMDS_GESTURE_DOWN, GestureUtils.RES_ID_GESTURE_DOWN, type);
                addGesture(GestureUtils.RES_ID_GESTURE_UP, view);
                addGesture(GestureUtils.RES_ID_GESTURE_DOWN, view);

                addCommandLoop(visibleTexts, null, CMDS_GESTURE_UP_BEGIN, GestureUtils.RES_ID_GESTURE_UP_BEGIN, type);
                addCommandLoop(visibleTexts, null, CMDS_GESTURE_DOWN_END, GestureUtils.RES_ID_GESTURE_DOWN_END, type);
                addGesture(GestureUtils.RES_ID_GESTURE_UP_BEGIN, view);
                addGesture(GestureUtils.RES_ID_GESTURE_DOWN_END, view);
            }
            if (horizontal) {
                addCommandLoop(visibleTexts, null, CMDS_GESTURE_LEFT, GestureUtils.RES_ID_GESTURE_LEFT, type);
                addCommandLoop(visibleTexts, null, CMDS_GESTURE_RIGHT, GestureUtils.RES_ID_GESTURE_RIGHT, type);
                addGesture(GestureUtils.RES_ID_GESTURE_LEFT, view);
                addGesture(GestureUtils.RES_ID_GESTURE_RIGHT, view);

                addCommandLoop(visibleTexts, null, CMDS_GESTURE_LEFT_BEGIN, GestureUtils.RES_ID_GESTURE_LEFT_BEGIN, type);
                addCommandLoop(visibleTexts, null, CMDS_GESTURE_RIGHT_END, GestureUtils.RES_ID_GESTURE_RIGHT_END, type);
                addGesture(GestureUtils.RES_ID_GESTURE_LEFT_BEGIN, view);
                addGesture(GestureUtils.RES_ID_GESTURE_RIGHT_END, view);
            }
            if (page) {
                addCommandLoop(visibleTexts, null, CMDS_GESTURE_PAGE_UP, GestureUtils.RES_ID_GESTURE_PAGE_UP, type);
                addCommandLoop(visibleTexts, null, CMDS_GESTURE_PAGE_DOWN, GestureUtils.RES_ID_GESTURE_PAGE_DOWN, type);
                addGesture(GestureUtils.RES_ID_GESTURE_PAGE_UP, view);
                addGesture(GestureUtils.RES_ID_GESTURE_PAGE_DOWN, view);

                addCommandLoop(visibleTexts, null, CMDS_GESTURE_PAGE_UP_BEGIN, GestureUtils.RES_ID_GESTURE_PAGE_UP_BEGIN, type);
                addCommandLoop(visibleTexts, null, CMDS_GESTURE_PAGE_DOWN_END, GestureUtils.RES_ID_GESTURE_PAGE_DOWN_END, type);
                addGesture(GestureUtils.RES_ID_GESTURE_PAGE_UP_BEGIN, view);
                addGesture(GestureUtils.RES_ID_GESTURE_PAGE_DOWN_END, view);
            }
        } else {
            if (vertical) {
                addCommand(visibleTexts, Prompt.PROMPT_GESTURE_VERTICAL, "上滑", GestureUtils.RES_ID_GESTURE_UP, type);
                addCommand(visibleTexts, Prompt.PROMPT_GESTURE_VERTICAL, "下滑", GestureUtils.RES_ID_GESTURE_DOWN, type);
                addGesture(GestureUtils.RES_ID_GESTURE_UP, view);
                addGesture(GestureUtils.RES_ID_GESTURE_DOWN, view);

                addCommand(visibleTexts, Prompt.PROMPT_GESTURE_VERTICAL, "滑到最上", GestureUtils.RES_ID_GESTURE_UP_BEGIN, type);
                addCommand(visibleTexts, Prompt.PROMPT_GESTURE_VERTICAL, "滑到最下", GestureUtils.RES_ID_GESTURE_DOWN_END, type);
                addGesture(GestureUtils.RES_ID_GESTURE_UP_BEGIN, view);
                addGesture(GestureUtils.RES_ID_GESTURE_DOWN_END, view);
            }
            if (horizontal) {
                addCommand(visibleTexts, Prompt.PROMPT_GESTURE_HORIZONTAL, "左滑", GestureUtils.RES_ID_GESTURE_LEFT, type);
                addCommand(visibleTexts, Prompt.PROMPT_GESTURE_HORIZONTAL, "右滑", GestureUtils.RES_ID_GESTURE_RIGHT, type);
                addGesture(GestureUtils.RES_ID_GESTURE_LEFT, view);
                addGesture(GestureUtils.RES_ID_GESTURE_RIGHT, view);

                addCommand(visibleTexts, Prompt.PROMPT_GESTURE_HORIZONTAL, "滑到最左", GestureUtils.RES_ID_GESTURE_LEFT_BEGIN, type);
                addCommand(visibleTexts, Prompt.PROMPT_GESTURE_HORIZONTAL, "滑到最右", GestureUtils.RES_ID_GESTURE_RIGHT_END, type);
                addGesture(GestureUtils.RES_ID_GESTURE_LEFT_BEGIN, view);
                addGesture(GestureUtils.RES_ID_GESTURE_RIGHT_END, view);
            }
            if (page) {
                addCommand(visibleTexts, Prompt.PROMPT_GESTURE_PAGE, "上一页", GestureUtils.RES_ID_GESTURE_PAGE_UP, type);
                addCommand(visibleTexts, Prompt.PROMPT_GESTURE_PAGE, "下一页", GestureUtils.RES_ID_GESTURE_PAGE_DOWN, type);
                addGesture(GestureUtils.RES_ID_GESTURE_PAGE_UP, view);
                addGesture(GestureUtils.RES_ID_GESTURE_PAGE_DOWN, view);

                addCommand(visibleTexts, Prompt.PROMPT_GESTURE_PAGE, "第1页", GestureUtils.RES_ID_GESTURE_PAGE_UP_BEGIN, type);
                addCommand(visibleTexts, Prompt.PROMPT_GESTURE_PAGE, "最后1页", GestureUtils.RES_ID_GESTURE_PAGE_DOWN_END, type);
                addGesture(GestureUtils.RES_ID_GESTURE_PAGE_UP_BEGIN, view);
                addGesture(GestureUtils.RES_ID_GESTURE_PAGE_DOWN_END, view);
            }
        }
    }

    public void handleViewGroup(View view, Set<ViewCmdBean> visibleTexts, IInterceptor<?>... interceptors) {
        // 布局中的处理
        String[] strArr = VoiceViewCmdUtils.getViewCmdAttr(view);
        if (strArr != null) {
            addCommandLoop(visibleTexts, null, strArr, view);
            for (String str : strArr) {
                handleViewAttrCommon(view, visibleTexts, str);
            }
        }
        // 代码中的处理
        List<String> list = strArr != null ? Arrays.asList(strArr) : null;
        handleBindLoop(view, list, visibleTexts, interceptors);
    }

    private boolean hasContainGesture(int displayId, int gesture) {
        Set<View> views = mGestureViewMap.get(gesture);
        if (views != null && views.size() > 0) {
            for (View view : views) {
                if (VoiceViewCmdUtils.getDisplayId(view) == displayId) {
                    return true;
                }
            }
        }
        return false;
    }

    public void handleSpecialView(View view, Set<ViewCmdBean> visibleTexts, IInterceptor<?>[] interceptors) {
        // 布局中的处理
        String[] strArr = VoiceViewCmdUtils.getViewCmdAttr(view);
        if (strArr != null) {
            addCommandLoop(visibleTexts, null, strArr, view);
            if (VoiceViewCmdUtils.hasSwitchAttr(view)) {
                for (String str : strArr) {
                    handleViewSwitchAttr(visibleTexts, view, str);
                }
            } else if (VoiceViewCmdUtils.hasTabAttr(view)) {
                for (String str : strArr) {
                    handleViewTabAttr(visibleTexts, view, str);
                }
            } else if (VoiceViewCmdUtils.getListAttr(view) != null) {
                String listAttr = VoiceViewCmdUtils.getListAttr(view);
                for (String str : strArr) {
                    handleViewListAttr(visibleTexts, view, str, listAttr);
                }
            } else if (VoiceViewCmdUtils.getBadgeAttr(view) != null) {
                String badgeAttr = VoiceViewCmdUtils.getBadgeAttr(view);
                for (String str : strArr) {
                    handleViewBadgeAttr(visibleTexts, view, str, badgeAttr);
                }
            }
        }
        // 代码中的处理
        List<String> list = strArr != null ? Arrays.asList(strArr) : null;
        handleBindLoop(view, list, visibleTexts, interceptors);
    }

    private void handleBindLoop(View view, List<String> list, Set<ViewCmdBean> visibleTexts, IInterceptor<?>... interceptors) {
        if (list != null && list.size() > 0) {
            for (String text : list) {
                handleBind(view, text, visibleTexts, interceptors);
            }
        } else {
            handleBind(view, null, visibleTexts, interceptors);
        }
    }

    /**
     * 给text绑定属性和资源id处理
     */
    private void handleBind(View view, String text, Set<ViewCmdBean> visibleTexts, IInterceptor<?>... interceptors) {
        if (view.getId() == -1 && TextUtils.isEmpty(text)) {
            return;
        }
        for (IInterceptor<?> interceptor : interceptors) {
            if (interceptor != null) {
                Map<?, View> map = interceptor.bind(view, text);
                // 获取泛型类型参数的类型信息
                if (map != null && map.size() > 0) {
                    Set<?> set = map.keySet();
                    Object object = set.iterator().next();
                    if (object instanceof String) {
                        for (Map.Entry<?, View> entry : map.entrySet()) {
                            String key = (String) entry.getKey();
                            if (!TextUtils.isEmpty(key)) {
                                String[] array = key.trim().split("\\|");
                                addCommandLoop(visibleTexts, (String) null, array, view);
                            }
                        }
                    } else if (object instanceof ViewCmdBean) {
                        for (Map.Entry<?, View> entry : map.entrySet()) {
                            ViewCmdBean bean = (ViewCmdBean) entry.getKey();
                            if (bean.prompt != null) {
                                String prompt = VoiceViewCmdUtils.isCompatibleMode ? null : bean.prompt;
                                addCommand(visibleTexts, prompt, bean.text, entry.getValue());
                                if (bean.prompt.startsWith(Prompt._PROMPT_TYPE_LIST)) {
                                    String[] split = bean.prompt.split(":");
                                    handleViewListAttr(visibleTexts, view, bean.text, split[1]);
                                } else if (bean.prompt.startsWith(Prompt._PROMPT_TYPE_BADGE)) {
                                    String[] split = bean.prompt.split(":");
                                    handleViewBadgeAttr(visibleTexts, view, bean.text, split[1]);
                                } else if (bean.prompt.startsWith(Prompt._PROMPT_TYPE_TAB)) {
                                    handleViewTabAttr(visibleTexts, view, bean.text);
                                } else if (bean.prompt.startsWith(Prompt._PROMPT_TYPE_SWITCH)) {
                                    handleViewSwitchAttr(visibleTexts, view, bean.text);
                                } else {
                                    VaLog.d(TAG, "handleBind, no handle case, prompt:" + bean.prompt);
                                }
                            } else {
                                addCommand(visibleTexts, null, bean.text, view);
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleViewAttrCommon(View view, Set<ViewCmdBean> visibleTexts, String str) {
        String listAttr = VoiceViewCmdUtils.getListAttr(view);
        String badgeAttr = VoiceViewCmdUtils.getBadgeAttr(view);
        if (listAttr != null) {
            handleViewListAttr(visibleTexts, view, str, listAttr);
        } else if (badgeAttr != null) {
            handleViewBadgeAttr(visibleTexts, view, str, badgeAttr);
        } else if (VoiceViewCmdUtils.hasTabAttr(view)) {
            handleViewTabAttr(visibleTexts, view, str);
        } else if (VoiceViewCmdUtils.hasSwitchAttr(view)) {
            handleViewSwitchAttr(visibleTexts, view, str);
        } else if (isOriginText(view, str)) {
            ViewCmdBean bean = new ViewCmdBean(null, str, VoiceViewCmdUtils.getViewCmdTypeAttr(view));
            visibleTexts.add(bean);
        } else {
            addCommand(visibleTexts, null, str, view);
        }
    }

    private boolean isOriginText(View view, String text) {
        if (view instanceof TextView) {
            CharSequence charSequence = ((TextView) view).getText();
            return TextUtils.equals(text, charSequence);
        }
        return false;
    }

    /**
     * 应用自定义处理
     */
    public void handleBind(Set<ViewCmdBean> visibleTexts, IInterceptor<?>... interceptors) {
        for (IInterceptor<?> interceptor : interceptors) {
            if (interceptor != null) {
                Map<String, Integer> map = interceptor.bind();
                // 获取泛型类型参数的类型信息
                if (map != null && map.size() > 0) {
                    for (Map.Entry<?, Integer> entry : map.entrySet()) {
                        String text = (String) entry.getKey();
                        if (!TextUtils.isEmpty(text)) {
                            String[] array = text.trim().split("\\|");
                            addCommandLoop(visibleTexts, null, array, entry.getValue(), ViewCmdType.TYPE_NORMAL);
                        }
                    }
                }
            }
        }
    }

    /**
     * 应用自定义处理
     */
    public void handleGlobalBind(Set<ViewCmdBean> visibleTexts, IInterceptor<?>... interceptors) {
        for (IInterceptor<?> interceptor : interceptors) {
            if (interceptor != null) {
                List<String> list = interceptor.globalBind();
                if (list != null && list.size() > 0) {
                    for (String text : list) {
                        if (!TextUtils.isEmpty(text)) {
                            ViewCmdBean bean = new ViewCmdBean(null, text, ViewCmdType.TYPE_GLOBAL);
                            visibleTexts.add(bean);
                        }
                    }
                }
            }
        }
    }

    /**
     * 应用自定义处理
     */
    public void handleKwsBind(Set<ViewCmdBean> visibleTexts, IInterceptor<?>... interceptors) {
        for (IInterceptor<?> interceptor : interceptors) {
            if (interceptor != null) {
                List<String> list = interceptor.kwsBind();
                if (list != null && list.size() > 0) {
                    for (String text : list) {
                        if (!TextUtils.isEmpty(text)) {
                            ViewCmdBean bean = new ViewCmdBean(null, text, ViewCmdType.TYPE_KWS);
                            visibleTexts.add(bean);
                        }
                    }
                }
            }
        }
    }

    // 辅助方法
    private void addCommand(Set<ViewCmdBean> visibleTexts, String prompt, String text, View view) {
        String type = VoiceViewCmdUtils.getViewCmdTypeAttr(view);
        if (text != null && !TextUtils.isEmpty(text.trim())) {
            ViewCmdBean bean = new ViewCmdBean(prompt, text, type);
            visibleTexts.add(bean);
            ViewCmdBean key = (prompt != null ? new ViewCmdBean(Prompt.getType(prompt), bean.text, type) : bean);
            if (mViewMap.containsKey(key)) {
                Set<View> views = mViewMap.get(key);
                if (views != null) {
                    views.add(view);
                }
            } else {
                Set<View> views = new HashSet<>(Collections.singletonList(view));
                mViewMap.put(key, views);
            }
        }
    }

    private void addCommand(Set<ViewCmdBean> visibleTexts, String prompt, String text, int resId, String type) {
        if (text != null && !TextUtils.isEmpty(text.trim())) {
            ViewCmdBean bean = new ViewCmdBean(prompt, text, type);
            visibleTexts.add(bean);
            mResIdMap.put(prompt != null ? new ViewCmdBean(Prompt.getType(prompt), bean.text, type) : bean, resId);
        }
    }

    // 辅助方法
    private void addCommandLoop(Set<ViewCmdBean> visibleTexts, String prompt, String[] commands, View view) {
        for (String text : commands) {
            addCommand(visibleTexts, prompt, text, view);
        }
    }

    private void addCommandLoop(Set<ViewCmdBean> visibleTexts, String prompt, String[] commands, int resId, String type) {
        for (String text : commands) {
            addCommand(visibleTexts, prompt, text, resId, type);
        }
    }

    // 辅助方法
    private void processGeneric(String[] prefixes, String text, Set<ViewCmdBean> visibleTexts, View view) {
        if (text != null && !TextUtils.isEmpty(text.trim())) {
            for (String prefix : prefixes) {
                addCommand(visibleTexts, null, prefix + text, view);
            }
        }
    }

    private void addGesture(int gesture, View view) {
        if (mGestureViewMap.containsKey(gesture)) {
            Set<View> views = mGestureViewMap.get(gesture);
            if (views != null) {
                views.add(view);
            }
        } else {
            Set<View> views = new HashSet<>(Collections.singletonList(view));
            mGestureViewMap.put(gesture, views);
        }
    }

    public void clear() {
        this.mResIdMap.clear();
        this.mViewMap.clear();
        this.mGestureViewMap.clear();
    }
}
