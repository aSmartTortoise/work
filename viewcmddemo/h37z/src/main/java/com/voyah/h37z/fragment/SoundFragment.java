package com.voyah.h37z.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.voyah.h37z.R;

public class SoundFragment extends Fragment {

    private static final String TAG = SoundFragment.class.getSimpleName();

    private View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        rootView = inflater.inflate(R.layout.fragment_sound, container, false);
        return rootView;
    }
}
