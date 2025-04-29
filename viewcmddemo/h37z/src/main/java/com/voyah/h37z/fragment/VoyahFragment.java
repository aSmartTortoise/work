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
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.voyah.h37z.R;
import com.voyah.h37z.databinding.FragmentVoayhBinding;

import java.util.ArrayList;
import java.util.List;

public class VoyahFragment extends Fragment {

    private static final String TAG = VoyahFragment.class.getSimpleName();

    private final List<View> viewList = new ArrayList<>(3);
    private final String[] strs = {"首页", "视频", "我的"};
    private FragmentVoayhBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voayh, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewList.clear();
        View view = getLayoutInflater().inflate(R.layout.view_media_wakeup, null);
        viewList.add(view);

        view = getLayoutInflater().inflate(R.layout.view_phone_wakeup, null);
        viewList.add(view);

        view = getLayoutInflater().inflate(R.layout.view_other_wakeup, null);
        viewList.add(view);

        initView();
        initEvn();
    }

    private void initEvn() {
        binding.tabLayout.addOnTabSelectedListener(listener);
    }

    private void initView() {
        binding.vpViewpager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = viewList.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return strs[position];
            }
        });
        binding.tabLayout.setupWithViewPager(binding.vpViewpager);
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
