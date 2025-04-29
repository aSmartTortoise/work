package com.voyah.h37z.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.voyah.h37z.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import q.rorbin.verticaltablayout.adapter.TabAdapter;
import q.rorbin.verticaltablayout.widget.TabView;

public class GlobalWakeupAdapter extends PagerAdapter implements TabAdapter {

    private final Context context;
    private final List<MenuBean> menus;
    private final List<Integer> viewResIds;

    public GlobalWakeupAdapter(Context context) {
        this.context = context;
        this.menus = new ArrayList<>();
        Collections.addAll(menus, new MenuBean(R.mipmap.ic_voice_video, R.mipmap.ic_voice_video, "媒体应用")
                , new MenuBean(R.mipmap.ic_voice_phone, R.mipmap.ic_voice_phone, "通讯相关")
                , new MenuBean(R.mipmap.ic_voice_carcenter, R.mipmap.ic_voice_carcenter, "车辆控制")
                , new MenuBean(R.mipmap.ic_voice_settings, R.mipmap.ic_voice_settings, "系统设置")
                , new MenuBean(R.mipmap.ic_voice_third, R.mipmap.ic_voice_third, "其它"));
        this.viewResIds = new ArrayList<>();
        Collections.addAll(viewResIds, R.layout.view_media_wakeup
                , R.layout.view_phone_wakeup
                , R.layout.view_carcontrol_wakeup
                , R.layout.view_system_wakeup
                , R.layout.view_other_wakeup);
    }

    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(viewResIds.get(position), null);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public TabView.TabBadge getBadge(int position) {
        return null;
    }

    @Override
    public TabView.TabIcon getIcon(int position) {
        MenuBean menu = menus.get(position);
        return new TabView.TabIcon.Builder()
                .setIcon(menu.mSelectIcon, menu.mNormalIcon)
                .setIconGravity(Gravity.START)
                .setIconSize(84, 84)
                .setIconMargin(10)
                .build();
    }

    @Override
    public TabView.TabTitle getTitle(int position) {
        MenuBean menu = menus.get(position);
        return new TabView.TabTitle.Builder()
                .setContent(menu.mTitle)
                .setTextColor(0xFF333333, 0x80333333)
                .build();
    }

    @Override
    public int getBackground(int position) {
        return -1;
    }

    public static class MenuBean {
        public int mSelectIcon;
        public int mNormalIcon;
        public String mTitle;

        public MenuBean(int mSelectIcon, int mNormalIcon, String mTitle) {
            this.mSelectIcon = mSelectIcon;
            this.mNormalIcon = mNormalIcon;
            this.mTitle = mTitle;
        }
    }
}