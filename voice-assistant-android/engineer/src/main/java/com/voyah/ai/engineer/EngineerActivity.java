package com.voyah.ai.engineer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.google.android.material.tabs.TabLayout;
import com.voyah.ai.engineer.databinding.ActivityEngineerBinding;
import com.voyah.ai.engineer.fragment.AudioLogSaveFragment;
import com.voyah.ai.engineer.fragment.MicCheckFragment;

import java.util.ArrayList;
import java.util.List;

public class EngineerActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityEngineerBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("onCreate");
        binding = ActivityEngineerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AudioLogSaveFragment audioLogSaveFragment = new AudioLogSaveFragment();
        MicCheckFragment micCheckFragment = new MicCheckFragment();

        fragmentList.clear();
        fragmentList.add(audioLogSaveFragment);
        fragmentList.add(micCheckFragment);
        initView();
    }

    private final List<Fragment> fragmentList = new ArrayList<>(2);
    private final int[] titleList = {R.string.main_tab_dump, R.string.main_tab_mic_check};

    private void initView() {
        binding.viewPager.setSaveEnabled(false);
        binding.viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getString(titleList[position]);
            }
        });
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.tabLayout.addOnTabSelectedListener(listener);
        binding.ivClose.setOnClickListener(this);
    }

    private final TabLayout.OnTabSelectedListener listener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            LogUtils.v("onTabSelected:" + tab.getText());
            if (TextUtils.equals(Utils.getApp().getString(R.string.main_tab_dump), tab.getText())) {
                ((AudioLogSaveFragment)fragmentList.get(0)).refreshDumpSavePath();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.ivClose) {
            finish();
        }
    }
}