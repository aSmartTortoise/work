package com.voyah.h37z.fragment;

import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.voyah.h37z.R;
import com.voyah.h37z.databinding.FragmentInterdriveBinding;
import com.voyah.viewcmd.aspect.VoiceInterceptor;
import com.voyah.viewcmd.interceptor.SimpleInterceptor;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class InterDriveFragment extends Fragment {

    private static final String TAG = InterDriveFragment.class.getSimpleName();

    private FragmentInterdriveBinding binding;
    private List<String> years = Arrays.asList(
            "2018年",
            "2019年",
            "2020年",
            "2021年",
            "2022年",
            "2023年",
            "2024年"
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_interdrive, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Calendar calendar = Calendar.getInstance();
        int nowYear = calendar.get(Calendar.YEAR);

        binding.year.setData(years);
        for (String year : years) {
            // 默认选中值
            if (year.equals(nowYear + "年")) {
                binding.year.setDefaultValue(year);
            }
        }

        // 获取选中值
        String currentItem = binding.year.getCurrentItem().toString();
        int position = binding.year.getCurrentPosition();
        int indexOf = years.indexOf(currentItem);
        Log.d(TAG, "currentItem:" + currentItem + ", position:" + position + ", indexOf:" + indexOf);
    }


    @VoiceInterceptor
    private final SimpleInterceptor interceptor = new SimpleInterceptor() {
        @Override
        public Map<String, Integer> bind() {
            ArrayMap<String, Integer> resIdMap = new ArrayMap<>();
            for (int i = 0; i < years.size(); i++) {
                resIdMap.put(years.get(i), 1);
            }
            return resIdMap;
        }

        @Override
        public boolean onTriggered(String text, int resId) {
            if (years.contains(text)) {
                Log.d(TAG, "onTriggered() called with: text = [" + text + "], resId = [" + resId + "]");
                binding.year.post(() -> {
                    int indexOf = years.indexOf(text);
                    binding.year.smoothScrollTo(indexOf);
                });

                return true;
            }
            return false;
        }
    };
}
