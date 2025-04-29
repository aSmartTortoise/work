package com.voyah.voice.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.voyah.viewcmd.VoiceViewCmdUtils;
import com.voyah.voice.main.BottomImageViewHolder;
import com.voyah.voice.main.R;
import com.voyah.voice.main.model.PhotoItem;

import java.util.ArrayList;
import java.util.List;

public class BottomImageAdapter extends RecyclerView.Adapter<BottomImageViewHolder> {


    private final List<PhotoItem> photoItemList = new ArrayList<>();

    private final OnSelectedChanged onSelectedChanged;

    private int selectIndex = 0;
    private final Context context;

    public BottomImageAdapter(Context context, List<PhotoItem> photoItemList, OnSelectedChanged onSelectedChanged, int selectIndex) {
        if (photoItemList != null) {
            this.photoItemList.addAll(photoItemList);
        }
        this.context = context;
        this.selectIndex = selectIndex;
        this.onSelectedChanged = onSelectedChanged;
    }


    public void setData(List<PhotoItem> photoItemList) {
        this.photoItemList.clear();
        this.photoItemList.addAll(photoItemList);
        notifyDataSetChanged();
    }


    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull BottomImageViewHolder holder, int position) {
        holder.bind(photoItemList.get(position), selectIndex == position);
        holder.itemView.setOnClickListener(v -> {
            if (selectIndex != position) {
                selectIndex = position;
                notifyDataSetChanged();
                if (onSelectedChanged != null) {
                    boolean isViewCmd = VoiceViewCmdUtils.isClickByViewCmd(holder.itemView);
                    onSelectedChanged.onSelectedChanged(selectIndex, photoItemList.get(selectIndex).getStyle(), isViewCmd);
                }
            }
        });
    }

    @NonNull
    @Override
    public BottomImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_view, parent, false);
        return new BottomImageViewHolder(context, view);
    }

    @Override
    public int getItemCount() {
        return photoItemList.size();
    }
}
