package com.voyah.viewcmd;

import static com.voyah.viewcmd.VoiceViewCmdUtils.mCtx;

import android.view.View;

import androidx.databinding.BindingAdapter;

public class AttrBindingAdapters {

    /**
     * viewcmd使用tag存储数据，是为了与其它属性共存
     */
    @BindingAdapter("viewcmd")
    public static void setViewCmd(View view, String cmd) {
        try {
            view.setTag(mCtx.getString(R.string.attr_view_cmd) + ":" + cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("gesture")
    public static void setGesture(View view, String orientation) {
        try {
            view.setContentDescription(mCtx.getString(R.string.attr_gesture) + ":" + orientation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("descText")
    public static void setDescText(View view, String enable) {
        try {
            view.setContentDescription(mCtx.getString(R.string.attr_desc_text) + ":" + enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("priority")
    public static void setPriority(View view, String value) {
        try {
            view.setContentDescription(mCtx.getString(R.string.attr_priority) + ":" + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("unsafe")
    public static void setUnsafe(View view, String value) {
        try {
            view.setContentDescription(mCtx.getString(R.string.attr_unsafe_ope) + ":" + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("tab")
    public static void setTab(View view, String enable) {
        try {
            view.setContentDescription(mCtx.getString(R.string.attr_tab) + ":" + enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("list")
    public static void setList(View view, String value) {
        try {
            view.setContentDescription(mCtx.getString(R.string.attr_list) + ":" + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("switch")
    public static void setSwitch(View view, String value) {
        try {
            view.setContentDescription(mCtx.getString(R.string.attr_switch) + ":" + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("bindClick")
    public static void bindClick(View view, String value) {
        try {
            view.setTag(mCtx.getString(R.string.attr_bind_click) + ":" + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("viewCmdType")
    public static void setViewCmdType(View view, String type) {
        try {
            view.setContentDescription(mCtx.getString(R.string.attr_viewcmd_type) + ":" + type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("badge")
    public static void setBadge(View view, String value) {
        try {
            view.setContentDescription(mCtx.getString(R.string.attr_badge) + ":" + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}