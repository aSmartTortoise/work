package com.voyah.h37z.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TabFragment extends Fragment {
    private String title;

    TabFragment(String title) {
        this.title = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText(title + "1234");
        textView.setTextColor(Color.RED);
        textView.setTextSize(40);
        textView.setBackgroundColor(Color.BLUE);
        return textView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("xyj", "onCreate() called:" + title);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("xyj", "onViewCreated() called:" + title);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("xyj", "onResume() called:" + title + ",hashCode:" + hashCode());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("xyj", "onStop() called:" + title + ",hashCode:" +hashCode());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("xyj", "onDestroyView() called:" + title);
    }
}