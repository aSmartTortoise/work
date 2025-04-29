package com.voyah.voice.main;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.voyah.voice.main.model.PhotoItem;

public class BottomImageViewHolder extends RecyclerView.ViewHolder {
    private final RoundedImageView imageView;

    private final TextView textStyle;

    private final View viewSelected;
    private final Context context;

    public BottomImageViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        imageView = itemView.findViewById(R.id.image_view);
        textStyle = itemView.findViewById(R.id.text_style);
        viewSelected = itemView.findViewById(R.id.view_selected);
    }

    public void bind(PhotoItem photoItem, boolean selected) {
        Object res = photoItem.getSmallRes();
        if (res instanceof String) {
            int resId = context.getResources().getIdentifier((String) res, "drawable", context.getPackageName());
            Glide.with(imageView).load(resId).placeholder(R.drawable.shape_loading).into(imageView);
        } else {
            Glide.with(imageView).load((int) res).placeholder(R.drawable.shape_loading).into(imageView);
        }
        textStyle.setText(photoItem.getStyle());
        viewSelected.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        imageView.setAlpha(selected ? 1.0f : imageView.getContext().getResources().getFloat(R.dimen.bottom_alpha));
    }
}
