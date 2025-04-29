package com.voyah.h37z.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.voyah.h37z.R;
import com.voyah.h37z.databinding.FragmentDrivePreferenceBinding;

import java.util.ArrayList;
import java.util.List;

public class DrivePreferenceFragment extends Fragment {

    private static final String TAG = DrivePreferenceFragment.class.getSimpleName();

    private final List<Fragment> viewList = new ArrayList<>(4);
    private final String[] strs = {"首页", "视频", "播客", "我的"};
    private FragmentDrivePreferenceBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_drive_preference, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewList.clear();
        viewList.add(new VehicleFragment());
        viewList.add(new TabFragment(strs[1]));
        viewList.add(new TabFragment(strs[2]));
        viewList.add(new TabFragment(strs[3]));

        initView();
        initEvn();
    }

    private void initEvn() {
        binding.tabLayout.addOnTabSelectedListener(listener);
    }

    private void initView() {
        binding.viewPager.setSaveEnabled(false);
        binding.viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return viewList.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return strs[position];
            }
        });
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    private final TabLayout.OnTabSelectedListener listener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            //选择的tab
            Log.e(TAG, "onTabSelected:" + tab.getText().toString());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            //离开的那个tab
            Log.e(TAG, "onTabUnselected" + tab.getText().toString());
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            //再次选择tab
            Log.e(TAG, "onTabReselected" + tab.getText().toString());
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }
}
