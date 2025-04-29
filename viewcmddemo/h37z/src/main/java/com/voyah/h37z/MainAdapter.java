package com.voyah.h37z;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    AudioManager audioManager;
    private int mChoosePosition = 0;
    private final Context mContext;
    private final List<FragmentTabInfo> mList;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public MainAdapter(Context context, List<FragmentTabInfo> list) {
        this.mContext = context;
        this.mList = list;
        this.audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_main_bg, parent, false);
        return new MainViewHolder(view);
    }

    @SuppressLint({"RecyclerView", "ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        if (mList.isEmpty() || mList.get(position) == null) {
            return;
        }

        int selectedId = mList.get(position).selectedIcon;
        int unSelectedId = mList.get(position).unSelectedIcon;

        if (mChoosePosition == position) {
            holder.ivTabIcon.setBackgroundResource(selectedId);
            holder.ivChooseBg.setVisibility(View.VISIBLE);
            holder.tvTabTitle.setTextColor(mContext.getColor(R.color.gray_33));
        } else {
            holder.ivTabIcon.setBackgroundResource(unSelectedId);
            holder.ivChooseBg.setVisibility(View.GONE);
            holder.tvTabTitle.setTextColor(mContext.getColor(R.color.gray_833));
        }
        String s = mList.get(position).tabTitle;
        if (!TextUtils.isEmpty(s)) {
            holder.tvTabTitle.setText(s);
            holder.tvTabTitle.setIncludeFontPadding(false);
        }

        //on click (always reserved)
        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(mList, position);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setChoosePosition(int position) {
        mChoosePosition = position;
        notifyDataSetChanged();
    }

    public int getChoosePosition() {
        return mChoosePosition;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class MainViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivTabIcon;
        private final ImageView ivChooseBg;
        private final TextView tvTabTitle;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTabIcon = itemView.findViewById(R.id.tab_icon);
            tvTabTitle = itemView.findViewById(R.id.tab_title);
            ivChooseBg = itemView.findViewById(R.id.iv_choose_bg);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(List<FragmentTabInfo> list, int position);
    }
}
