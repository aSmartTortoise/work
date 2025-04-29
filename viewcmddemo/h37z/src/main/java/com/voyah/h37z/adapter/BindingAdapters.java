package com.voyah.h37z.adapter;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.voyah.h37z.R;

public class BindingAdapters {

    @BindingAdapter("resIdName")
    public static void setImageResourceByName(ImageView imageView, String resIdName) {
        int resId = imageView.getContext().getResources().getIdentifier(resIdName, "mipmap", imageView.getContext().getPackageName());
        if (resId != 0) {
            imageView.setImageResource(resId);
        } else {
            imageView.setImageResource(R.mipmap.ic_voice_settings);
        }
    }
}
