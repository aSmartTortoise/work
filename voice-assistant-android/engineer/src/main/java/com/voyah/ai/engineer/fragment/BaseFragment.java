package com.voyah.ai.engineer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.LogUtils;


public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    protected View mView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.dTag(getClass().getSimpleName(), "onCreateView");
        mView = inflater.inflate(setLayout(), null);
        init();
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.dTag(getClass().getSimpleName(), "onDestroyView");
        unInit();
        mView = null;
    }

    protected abstract int setLayout();

    protected abstract void init();

    protected abstract void unInit();
}