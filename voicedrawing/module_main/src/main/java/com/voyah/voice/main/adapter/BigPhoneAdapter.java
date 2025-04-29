package com.voyah.voice.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.voyah.voice.main.R;

import java.util.ArrayList;
import java.util.List;

public class BigPhoneAdapter extends PagerAdapter {
    private static final String TAG = "BigPhoneAdapter";


    private final List<View> viewList = new ArrayList<>();
    private final LayoutInflater inflater;
    private final List<String> data;
    private boolean isFullScreen = false;
    private final boolean isRes;
    private Context context;


    public BigPhoneAdapter(Context context, List<String> data, LayoutInflater inflater, boolean isRes) {
        this.inflater = inflater;
        this.data = data;
        this.context = context;
        this.isRes = isRes;
        initView();
    }


    private void initView() {
        for (int i = 0; i < getCount(); i++) {
            @SuppressLint("InflateParams") View itemView = inflater.inflate(R.layout.item_photo_view, null);
            ImageView imageView = itemView.findViewById(R.id.image_view);
            imageView.setScaleType(isFullScreen ? ImageView.ScaleType.FIT_XY : ImageView.ScaleType.FIT_CENTER);
            if (isRes) {
                int resId = context.getResources().getIdentifier(data.get(i), "drawable", context.getPackageName());
                Glide.with(imageView).load(resId).placeholder(R.drawable.shape_loading).error(R.drawable.img_fail).into(imageView);
            } else {
                Log.i(TAG, "glide load:" + data.get(i));
                Glide.with(imageView).load(data.get(i)).placeholder(R.drawable.shape_loading).error(R.drawable.img_fail).into(imageView);
            }
            viewList.add(itemView);
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.i(TAG, "instantiateItem:" + position);
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView(viewList.get(position));
    }

    public List<String> getData() {
        return data;
    }

    public void setIsFullScreen(boolean fullScreen) {
        Log.i("setIsFullScreen", "setIsFullScreen:" + fullScreen);
        this.isFullScreen = fullScreen;
        if (!viewList.isEmpty()) {
            for (View view : viewList) {
                ImageView imageView = view.findViewById(R.id.image_view);
                imageView.setScaleType(isFullScreen ? ImageView.ScaleType.FIT_XY : ImageView.ScaleType.FIT_CENTER);
            }
        }
    }
}
