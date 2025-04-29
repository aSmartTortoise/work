package com.voyah.h37z.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.voyah.h37z.CornerTipBean;
import com.voyah.h37z.MainApplication;
import com.voyah.h37z.R;
import com.voyah.h37z.VoiceViewModel;
import com.voyah.h37z.adapter.CornerTipAdapter;
import com.voyah.h37z.adapter.GridSpacingItemDecoration;
import com.voyah.h37z.databinding.FragmentVehicleBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class VehicleFragment extends Fragment {
    private static final String TAG = VehicleFragment.class.getSimpleName();
    private FragmentVehicleBinding binding;
    private CornerTipAdapter cornerTipAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vehicle, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VoiceViewModel voiceViewModel = new ViewModelProvider(MainApplication.getApplication(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(MainApplication.getApplication())).get(VoiceViewModel.class);
        voiceViewModel.voiceStateChangeData.observe(this, isWakeup -> {
            Log.d(TAG, "onChanged = [" + isWakeup + "]");
            if (cornerTipAdapter != null) {
                cornerTipAdapter.updateShowCorner(binding.recyclerViewCarControl, isWakeup);
                cornerTipAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called:" + hashCode());

        binding.recyclerViewCarControl.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.recyclerViewCarControl.addItemDecoration(new GridSpacingItemDecoration(3, 32, false));
        cornerTipAdapter = new CornerTipAdapter(getCommonTipBeans("vpa/car_control_tips.json"));
        binding.recyclerViewCarControl.setAdapter(cornerTipAdapter);
        cornerTipAdapter.setOnItemClickListener((bean, position) -> Toast.makeText(requireContext(), "你点击第" + (bean.position + 1) + "条目", Toast.LENGTH_SHORT).show());

        binding.recyclerViewCarControl.post(() -> cornerTipAdapter.updateVisibleItemBadges(binding.recyclerViewCarControl));

        binding.recyclerViewCarControl.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    cornerTipAdapter.updateVisibleItemBadges(recyclerView);
                }
            }
        });
    }

//    @VoiceInterceptor
//    private final IInterceptor<String> iInterceptor = new SimpleInterceptor() {
//
//        @Override
//        public Map<String, View> bind(View view, String text) {
//            ArrayMap<String, View> viewMap = new ArrayMap<>();
//            switch (view.getId()) {
//                case R.id.tv_corner:
//                    viewMap.put("第" + text + "个", view);
//                    viewMap.put("选择第" + text + "个", view);
//                    break;
//            }
//            return viewMap;
//        }
//    };

    /**
     * 从assert中获取CornerTipBean列表
     *
     * @param assetFileName
     * @return
     */
    private List<CornerTipBean> getCommonTipBeans(String assetFileName) {
        List<CornerTipBean> list = new ArrayList<>();
        try {
            InputStream inputStream = requireContext().getAssets().open(assetFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject object = new JSONObject(stringBuilder.toString());
            JSONArray jsonArray = object.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String image = jsonObject.getString("image");
                String title = jsonObject.getString("title");
                JSONArray tipsArray = jsonObject.getJSONArray("tips");
                List<String> tips = new ArrayList<>();
                for (int j = 0; j < tipsArray.length(); j++) {
                    tips.add(tipsArray.getString(j));
                }
                CornerTipBean tipBean = new CornerTipBean(image, tips, title);
                list.add(tipBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
