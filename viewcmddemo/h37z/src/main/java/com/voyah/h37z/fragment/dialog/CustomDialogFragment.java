package com.voyah.h37z.fragment.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.voyah.h37z.R;
import com.voyah.viewcmd.aspect.VoiceRegisterView;

public class CustomDialogFragment extends DialogFragment {

    private static final String TAG = CustomDialogFragment.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog() called with: savedInstanceState = [" + savedInstanceState + "]");
        return super.onCreateDialog(savedInstanceState);
    }

    @VoiceRegisterView(isSticky = true)
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume() called");
        super.onResume();
        setCancelable(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called with: inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        View view = inflater.inflate(R.layout.dialog_custom_greet, container);
        view.findViewById(R.id.dialog_button_confirm).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.dialog_button_cancel).setOnClickListener(v -> dismiss());
        return view;
    }
}