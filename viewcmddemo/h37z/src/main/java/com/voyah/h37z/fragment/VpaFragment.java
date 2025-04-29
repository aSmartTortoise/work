package com.voyah.h37z.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.voyah.h37z.MyRemoteService;
import com.voyah.h37z.TTSPlayCallback;
import com.voyah.h37z.TestActivity;
import com.voyah.h37z.adapter.GridSpacingItemDecoration;
import com.voyah.h37z.adapter.PersonalVoiceAdapter;
import com.voyah.h37z.R;
import com.voyah.h37z.VpaViewModel;
import com.voyah.h37z.databinding.FragmentVpaSettingBinding;
import com.voyah.viewcmd.Response;
import com.voyah.viewcmd.aspect.VoiceInterceptor;
import com.voyah.viewcmd.interceptor.IInterceptor;
import com.voyah.viewcmd.interceptor.SimpleInterceptor;

import static com.voyah.h37z.VpaViewModel.MS_VOICE_NAMES;
import static com.voyah.h37z.VpaViewModel.UI_VOICE_NAMES;

import java.util.ArrayList;
import java.util.List;


public class VpaFragment extends Fragment {
    private static final String TAG = VpaFragment.class.getSimpleName();

    private VpaViewModel vpaViewModel;
    private FragmentVpaSettingBinding binding;
    private String curTtsId;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vpa_setting, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider.Factory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        vpaViewModel = new ViewModelProvider(getViewModelStore(), factory).get(VpaViewModel.class);
        Log.d(TAG, "viewModel:" + vpaViewModel);

        // 绑定语音服务
        vpaViewModel.connect();
        vpaViewModel.getConnectLiveData().observe(this, isConnected -> {
            Log.d(TAG, "onChanged:" + isConnected);
            if (isConnected == null || !isConnected) {
                Toast.makeText(getContext(), "与语音断开连接，请检查", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "connected...");
                // 将viewModel设置到binding中
                handler.postDelayed(() -> binding.setViewModel(vpaViewModel), 100);
            }
        });
        vpaViewModel.getDataChangedLiveData().observe(this, dataChanged -> {
            if (dataChanged) {
                // 将viewModel设置到binding中
                Log.d(TAG, "dataChanged = true");
                handler.postDelayed(() -> binding.setViewModel(vpaViewModel), 100);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("xyj", "VpaFragment onResume() called");
        // 提前设置一次
        binding.setViewModel(vpaViewModel);
        binding.setLifecycleOwner(this);

        initFuncSwitch();
        initVoices();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("xyj", "VpaFragment onStop() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("xyj", "VpaFragment onPause() called");
    }

    private void initFuncSwitch() {
        binding.settingWakeup.swWakeupMain.setOnCheckedChangeListener(this::onCheckedChanged);
        binding.settingWakeup.swWakeupGlobal.setOnCheckedChangeListener(this::onCheckedChanged);
        binding.settingScene.swContinuousDialogue.setOnCheckedChangeListener(this::onCheckedChanged);
    }

    private void initVoices() {
        // 发声人角色列表
        binding.settingPersonalVoice.recyclerViewPersonalVoice.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.settingPersonalVoice.recyclerViewPersonalVoice.addItemDecoration(new GridSpacingItemDecoration(3, 52, false));
        PersonalVoiceAdapter voiceAdapter = new PersonalVoiceAdapter(UI_VOICE_NAMES);
        binding.settingPersonalVoice.recyclerViewPersonalVoice.setAdapter(voiceAdapter);
        // 获取当前选择的发声人
        String msVoiceName = vpaViewModel.getVoiceName();
        String uiName = vpaViewModel.sortedVoiceNameMap.get(msVoiceName);
        int curSelectedPos = 0;
        if (!TextUtils.isEmpty(uiName)) {
            curSelectedPos = UI_VOICE_NAMES.indexOf(uiName);
        }
        voiceAdapter.setSelectedItem(curSelectedPos);

        voiceAdapter.setOnItemClickListener(position -> {
            Log.d(TAG, "onItemClick() called with: position = [" + position + "]");
            voiceAdapter.setSelectedItem(position);
            if (curTtsId != null) {
                vpaViewModel.stopSpeak(curTtsId);
            }
            vpaViewModel.setVoiceName(MS_VOICE_NAMES.get(position));
            String[] voiceTips = requireContext().getResources().getStringArray(R.array.switch_voice_tts);
            curTtsId = vpaViewModel.speak(voiceTips[position], new TTSPlayCallback() {
                @Override
                public void onTTSPlayBegin(@NonNull String ttsId) {
                    Log.d(TAG, "onTTSPlayBegin() called with: ttsId = [" + ttsId + "]");
                }

                @Override
                public void onTTSPlayEnd(String ttsId) {
                    Log.d(TAG, "onTTSPlayEnd() called with: ttsId = [" + ttsId + "]");
                    curTtsId = null;
                }
            });

        });

        binding.settingPersonal.tvPersonalTitle.setOnClickListener(v -> {
                    Intent serviceIntent = new Intent(requireContext(), MyRemoteService.class);
                    requireContext().startService(serviceIntent);
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vpaViewModel.disconnect();
        binding.unbind();
        handler.removeCallbacksAndMessages(null);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_wakeup_main:
                if (isChecked) {
                    binding.settingWakeup.swWakeupGlobal.setEnabled(true); // 联动
                    vpaViewModel.enableMainWakeup(true);
                } else {
                    vpaViewModel.showMainWakeupDialog(buttonView);
                }
                break;
            case R.id.sw_wakeup_global:
                vpaViewModel.enableGlobalWakeup(isChecked);
                break;
            case R.id.sw_continuous_dialogue:
                vpaViewModel.enableContinuousDialog(isChecked);
                break;
        }
    }

    @VoiceInterceptor
    private final IInterceptor<String> mInterceptor = new SimpleInterceptor() {

        @Override
        public List<String> globalBind() {
            List<String> list = new ArrayList<>();
            list.add("测试");
            list.add("你好");
            return list;
        }

        @Override
        public Response onGlobalTriggered(String text) {
            Log.d(TAG, "onGlobalTriggered() called with: text = [" + text + "]");
            if ("测试".equals(text) || "你好".equals(text)) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "全局可见:" + text, Toast.LENGTH_SHORT).show());
                return Response.response("好的" + text);
            }
            return null;
        }

        @Override
        public List<String> kwsBind() {
            List<String> list = new ArrayList<>();
            list.add("确定");
            list.add("取消");
            return list;
        }

        @Override
        public Response onKwsTriggered(String text) {
            Log.d(TAG, "onKwsTriggered() called with: text = [" + text + "]");
            if ("确定".equals(text) || "取消".equals(text)) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "kws:" + text, Toast.LENGTH_SHORT).show());
                return Response.response("好的" + text);
            }
            return null;
        }
    };
}