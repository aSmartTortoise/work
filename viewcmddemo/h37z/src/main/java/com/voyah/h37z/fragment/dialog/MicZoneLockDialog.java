package com.voyah.h37z.fragment.dialog;

import android.content.Context;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.lxj.xpopup.core.CenterPopupView;
import com.microsoft.assistant.model.SpeechLocation;
import com.voyah.h37z.R;
import com.voyah.h37z.VpaViewModel;
import com.voyah.h37z.databinding.DialogVoiceMicLockBinding;
import com.voyah.viewcmd.aspect.VoiceRegisterView;

/**
 * 多音区设定选择框
 */
public class MicZoneLockDialog extends CenterPopupView implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = MicZoneLockDialog.class.getSimpleName();

    private VpaViewModel viewModel;
    private DialogVoiceMicLockBinding binding;

    public MicZoneLockDialog(@NonNull Context context, @NonNull VpaViewModel viewModel) {
        super(context);
        this.viewModel = viewModel;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_voice_mic_lock;
    }

    @VoiceRegisterView
    @Override
    protected void onCreate() {
        super.onCreate();

        binding = DataBindingUtil.bind(contentView);
        assert binding != null;

        binding.cbMicZoneFl.setChecked(true);
        binding.cbMicZoneFr.setChecked(viewModel.isMicZoneEnable(SpeechLocation.FrontRight));
        binding.cbMicZoneRl.setChecked(viewModel.isMicZoneEnable(SpeechLocation.BackLeft));
        binding.cbMicZoneRr.setChecked(viewModel.isMicZoneEnable(SpeechLocation.BackRight));

        binding.cbMicZoneFl.setOnCheckedChangeListener(this);
        binding.cbMicZoneFr.setOnCheckedChangeListener(this);
        binding.cbMicZoneRl.setOnCheckedChangeListener(this);
        binding.cbMicZoneRr.setOnCheckedChangeListener(this);

        findViewById(R.id.iv_dialog_close).setOnClickListener(v -> dismiss());
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        Log.d(TAG, "onDismiss() called");
    }

    /**
     * 弹窗的宽度，用来动态设定当前弹窗的宽度，受getMaxWidth()限制
     *
     * @return
     */
    protected int getPopupWidth() {
        return 1320;
    }

    /**
     * 弹窗的高度，用来动态设定当前弹窗的高度，受getMaxHeight()限制
     *
     * @return
     */
    protected int getPopupHeight() {
        return 800;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "onCheckedChanged() called with: id = [" + buttonView.getId() + "], isChecked = [" + isChecked + "]");
        switch (buttonView.getId()) {
            case R.id.cb_mic_zone_fl:
                if (!isChecked) {
                    binding.cbMicZoneFl.setChecked(true);
                    Toast.makeText(getContext(), R.string.toast_fl_no_permit_close, Toast.LENGTH_SHORT).show();
                }
            case R.id.cb_mic_zone_fr:
                viewModel.setMicZoneEnable(SpeechLocation.FrontRight, isChecked);
                break;
            case R.id.cb_mic_zone_rl:
                viewModel.setMicZoneEnable(SpeechLocation.BackLeft, isChecked);
                break;
            case R.id.cb_mic_zone_rr:
                viewModel.setMicZoneEnable(SpeechLocation.BackRight, isChecked);
                break;
        }
    }
}