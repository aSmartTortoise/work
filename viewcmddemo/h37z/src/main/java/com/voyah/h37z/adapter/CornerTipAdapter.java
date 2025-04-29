package com.voyah.h37z.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.voyah.ai.sdk.manager.DialogueManager;
import com.voyah.h37z.CornerTipBean;
import com.voyah.h37z.databinding.ItemCornerTipBinding;
import com.voyah.viewcmd.VoiceViewCmdUtils;

import java.util.List;

/**
 * 带角标通用提示adapter
 */
public class CornerTipAdapter extends RecyclerView.Adapter<CornerTipAdapter.ViewHolder> {

    private static final String TAG = "CornerTipAdapter";
    private List<CornerTipBean> dataList;
    private OnItemClickListener mListener;
    private boolean showCorner;

    public CornerTipAdapter(List<CornerTipBean> dataList) {
        this.dataList = dataList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCornerTipBinding binding = ItemCornerTipBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CornerTipBean item = dataList.get(position);
        item.showCorner = showCorner;
        holder.binding.setTipBean(item);
        holder.binding.executePendingBindings();
    }

    public void updateShowCorner(RecyclerView recyclerView, boolean isWakeup) {
        int viewDisplayId = VoiceViewCmdUtils.getDisplayId(recyclerView);
        int voiceDisplayId = DialogueManager.getCurVpaDisplayId();
        // 主驾 -》 中控屏 ->>  0
        // 副驾 -》 副屏 ->>  1
        showCorner = (isWakeup && voiceDisplayId != -1 && viewDisplayId == voiceDisplayId);
        // todo台架模拟调试时，直接复值
        // showCorner = isWakeup;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateVisibleItemBadges(RecyclerView recyclerView) {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisible = getAdjustedFirstVisiblePosition(layoutManager);
            if (firstVisible != RecyclerView.NO_POSITION) {
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                Log.d(TAG, "firstVisible:" + firstVisible + ", lastVisible:" + lastVisible);
                for (int i = firstVisible; i <= lastVisible; i++) {
                    if (i >= 0 && i < dataList.size()) {
                        dataList.get(i).position = i - firstVisible + 1; // 从1开始计数
                    }
                }
                notifyDataSetChanged();
            } else {
                Log.e(TAG, "firstVisible = -1");
            }
        }
    }

    public int getAdjustedFirstVisiblePosition(GridLayoutManager layoutManager) {
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        if (firstVisiblePosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION; // 如果没有可见项，则返回NO_POSITION
        }
        View firstVisibleChild = layoutManager.findViewByPosition(firstVisiblePosition);
        if (firstVisibleChild == null) {
            return firstVisiblePosition; // 如果无法获取第一个可见子视图，则直接返回第一个可见位置
        }
        int firstVisibleTop = Math.abs(firstVisibleChild.getTop());
        int threshold = 56; // todo 要根据实际角标大小设置，建议设置成36dp (爱奇艺推荐值)
        Log.d(TAG, "firstVisiblePosition:" + firstVisiblePosition + ", firstVisibleTop:" + firstVisibleTop + ",threshold:" + threshold);
        Log.d(TAG, "layoutManager.getSpanCount():" + layoutManager.getSpanCount());

        if (firstVisibleTop > threshold) {
            return firstVisiblePosition + layoutManager.getSpanCount();
        }

        return firstVisiblePosition;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCornerTipBinding binding;

        public ViewHolder(@NonNull ItemCornerTipBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(dataList.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(CornerTipBean bean, int position);
    }
}