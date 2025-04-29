package com.voyah.h37z.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.voyah.h37z.CommonTipBean;
import com.voyah.h37z.databinding.ItemCommonTipBinding;

import java.util.List;

/**
 * 通用提示adapter
 */
public class CommonTipAdapter extends RecyclerView.Adapter<CommonTipAdapter.ViewHolder> {

    private List<CommonTipBean> dataList;
    private OnItemClickListener mListener;

    public CommonTipAdapter(List<CommonTipBean> dataList) {
        this.dataList = dataList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCommonTipBinding binding = ItemCommonTipBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommonTipBean item = dataList.get(position);
        holder.binding.setTipBean(item);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCommonTipBinding binding;

        public ViewHolder(@NonNull ItemCommonTipBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}