package com.voyah.h37z.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.voyah.h37z.R;
import com.voyah.h37z.databinding.ItemPersonalVoiceBinding;

import java.util.List;

/**
 * 个性化发声人adapter
 */
public class PersonalVoiceAdapter extends RecyclerView.Adapter<PersonalVoiceAdapter.ViewHolder> {

    private List<String> dataList;
    private OnItemClickListener mListener;
    private int selectedItem = -1;

    public PersonalVoiceAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemPersonalVoiceBinding binding = ItemPersonalVoiceBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = dataList.get(position);
        holder.binding.setRoleName(item);

        // 根据位置设置不同的背景图
        if (selectedItem == position) {
            if (position == 0) {
                holder.binding.cstItemPersonalVoice.setBackgroundResource(R.mipmap.item_personal_voice_bg_soft);
            } else {
                holder.binding.cstItemPersonalVoice.setBackgroundResource(R.mipmap.item_personal_voice_bg_cool);
            }
        } else {
            holder.binding.cstItemPersonalVoice.setBackgroundResource(R.mipmap.item_personal_voice_bg_default);
        }
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPersonalVoiceBinding binding;

        public ViewHolder(@NonNull ItemPersonalVoiceBinding binding) {
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