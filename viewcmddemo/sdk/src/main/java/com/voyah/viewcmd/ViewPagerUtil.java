package com.voyah.viewcmd;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ViewPagerUtil {

    protected static View getViewPagerFragmentView(ViewPager viewPager) {
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter instanceof FragmentPagerAdapter) {
            FragmentPagerAdapter fragmentPagerAdapter = (FragmentPagerAdapter) adapter;
            Fragment item = getPagerAdapterCurrentFragment(viewPager, fragmentPagerAdapter);
            return item.getView();
        } else if (adapter instanceof FragmentStatePagerAdapter) {
            FragmentStatePagerAdapter fragmentStatePagerAdapter = (FragmentStatePagerAdapter) adapter;
            Fragment item = getStatePagerAdapterCurrentFragment(viewPager, fragmentStatePagerAdapter);
            return item.getView();
        }
        return null;
    }

    private static Fragment getPagerAdapterCurrentFragment(ViewPager viewPager, FragmentPagerAdapter pagerAdapter) {
        try {
            String tag = "android:switcher:" + viewPager.getId() + ":" + pagerAdapter.getItemId(viewPager.getCurrentItem());
            Field fragmentManagerField = FragmentPagerAdapter.class.getDeclaredField("mFragmentManager");
            fragmentManagerField.setAccessible(true);
            FragmentManager fragmentManager = (FragmentManager) fragmentManagerField.get(pagerAdapter);
            if (fragmentManager != null) {
                Fragment item = fragmentManager.findFragmentByTag(tag);
                if (item != null) {
                    return item;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pagerAdapter.getItem(viewPager.getCurrentItem());
    }

    private static Fragment getStatePagerAdapterCurrentFragment(ViewPager viewPager, FragmentStatePagerAdapter statePagerAdapter) {
        try {
            Field fragmentsField = FragmentStatePagerAdapter.class.getDeclaredField("mFragments");
            fragmentsField.setAccessible(true);
            ArrayList<Fragment> mFragments = (ArrayList<Fragment>) fragmentsField.get(statePagerAdapter);
            if (mFragments != null && mFragments.size() > viewPager.getCurrentItem()) {
                Fragment item = mFragments.get(viewPager.getCurrentItem());
                if (item != null) {
                    return item;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statePagerAdapter.getItem(viewPager.getCurrentItem());
    }
}
