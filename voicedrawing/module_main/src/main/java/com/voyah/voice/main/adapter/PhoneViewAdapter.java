package com.voyah.voice.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.voyah.voice.main.R;
import com.voyah.voice.main.model.PhotoItem;

import java.util.ArrayList;
import java.util.List;

public class PhoneViewAdapter extends PagerAdapter {
    private static final String TAG = "PhoneViewAdapter";
    private final List<List<PhotoItem>> data;
    private final List<View> viewList = new ArrayList<>();
    private final LayoutInflater inflater;
    private int[] colors = new int[]{Color.RED, Color.GREEN, Color.YELLOW};

    private final View.OnClickListener onClickListener;
    private final Context context;


    public PhoneViewAdapter(Context context, List<List<PhotoItem>> data, LayoutInflater inflater, View.OnClickListener onClickListener) {
        this.inflater = inflater;
        this.data = data;
        this.onClickListener = onClickListener;
        this.context = context;
        initView();
    }


    @SuppressLint("SetTextI18n")
    private void initView() {
        for (int i = 0; i < getCount(); i++) {
            @SuppressLint("InflateParams") View itemView = inflater.inflate(R.layout.item_photo, null);
            ImageView imageView = itemView.findViewById(R.id.image_view);
            Object res = data.get(i).get(0).getRes();
            if (res instanceof String) {
                Log.i(TAG, "Glide load:" + res);
                int resId = context.getResources().getIdentifier((String) res, "drawable", context.getPackageName());
                Glide.with(imageView).load(resId).placeholder(R.drawable.shape_loading).error(R.drawable.img_fail).into(imageView);
            } else {
                Glide.with(imageView).load((int) res).placeholder(R.drawable.shape_loading).error(R.drawable.img_fail).into(imageView);
            }
            viewList.add(itemView);
            itemView.setTag(i);
            imageView.setTag(R.id.custom_tag, i);
            imageView.setOnClickListener(onClickListener);
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

    public void setSelect(int select, int position) {
        View itemView = viewList.get(position);
        ImageView imageView = itemView.findViewById(R.id.image_view);
        Object res = data.get(position).get(select).getRes();
        if (res instanceof String) {
            Log.i(TAG, "Glide load2:" + res);
            int resId = context.getResources().getIdentifier((String) res, "drawable", context.getPackageName());
            Glide.with(imageView).load(resId).placeholder(R.drawable.shape_loading).error(R.drawable.img_fail).into(imageView);
        } else {
            Glide.with(imageView).load((int) res).placeholder(R.drawable.shape_loading).error(R.drawable.img_fail).into(imageView);
        }
    }

}
