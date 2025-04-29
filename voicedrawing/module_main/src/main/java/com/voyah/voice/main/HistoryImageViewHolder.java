package com.voyah.voice.main;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.voice.drawing.api.model.DrawingInfo;
import com.voyah.voice.main.adapter.HistoryAdapter;
import com.voyah.voice.main.ui.ViewActivity;

import java.util.ArrayList;

public class HistoryImageViewHolder extends RecyclerView.ViewHolder {

    private final LinearLayout layoutDate;
    private final TextView textDesc;
    private final TextView textDate;
    private final Context context;

    private final ImageView[] imgArr;

    private final CheckBox[] checkBoxArr;

    private final ConstraintLayout[] layoutArr;
    public final CheckBox checkBoxAll;


    public HistoryImageViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.layoutDate = itemView.findViewById(R.id.layout_date);
        this.textDesc = itemView.findViewById(R.id.text_desc);
        this.textDate = itemView.findViewById(R.id.text_date);
        this.checkBoxAll = itemView.findViewById(R.id.check_all);
        this.imgArr = new ImageView[4];
        imgArr[0] = itemView.findViewById(R.id.img_1);
        imgArr[1] = itemView.findViewById(R.id.img_2);
        imgArr[2] = itemView.findViewById(R.id.img_3);
        imgArr[3] = itemView.findViewById(R.id.img_4);
        this.checkBoxArr = new CheckBox[4];
        checkBoxArr[0] = itemView.findViewById(R.id.check_1);
        checkBoxArr[1] = itemView.findViewById(R.id.check_2);
        checkBoxArr[2] = itemView.findViewById(R.id.check_3);
        checkBoxArr[3] = itemView.findViewById(R.id.check_4);

        this.layoutArr = new ConstraintLayout[4];
        layoutArr[0] = itemView.findViewById(R.id.layout_1);
        layoutArr[1] = itemView.findViewById(R.id.layout_2);
        layoutArr[2] = itemView.findViewById(R.id.layout_3);
        layoutArr[3] = itemView.findViewById(R.id.layout_4);

        this.context = context;

    }

    public void bind(HistoryAdapter adapter, int position, DrawingInfo drawingInfo, String date, boolean showDate, boolean editState, boolean[][] selectState) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) textDesc.getLayoutParams();
        if (!showDate) {
            layoutDate.setVisibility(View.GONE);
            layoutParams.setMargins(0, (int) context.getResources().getDimension(R.dimen.dp_16), 0, 0);
        } else {
            layoutDate.setVisibility(View.VISIBLE);
            layoutParams.setMargins(0, 0, 0, 0);
        }
        textDesc.setLayoutParams(layoutParams);

        ViewGroup.MarginLayoutParams layoutParams2 = (ViewGroup.MarginLayoutParams) layoutDate.getLayoutParams();
        if (position == 0) {
            layoutParams2.setMargins(0, 0, 0, 0);

        } else {
            layoutParams2.setMargins(0, (int) context.getResources().getDimension(R.dimen.dp_32), 0, 0);
        }
        layoutDate.setLayoutParams(layoutParams2);
        textDesc.setText(drawingInfo.getPrompt());
        textDate.setText(date);
        int size = drawingInfo.getUrlList() != null ? drawingInfo.getUrlList().size() : 0;
        for (int i = 0; i < imgArr.length; i++) {
            if (i < size) {
                layoutArr[i].setVisibility(View.VISIBLE);
                Glide.with(imgArr[i]).load(drawingInfo.getUrlList().get(i)).placeholder(R.drawable.shape_loading).error(R.drawable.img_fail).into(imgArr[i]);

            } else {
                layoutArr[i].setVisibility(View.INVISIBLE);
            }
        }

        ViewGroup.MarginLayoutParams layoutParams3 = (ViewGroup.MarginLayoutParams) textDate.getLayoutParams();

        if (editState) {
            checkBoxAll.setVisibility(View.VISIBLE);
            for (CheckBox c : checkBoxArr) {
                c.setVisibility(View.VISIBLE);
            }
            layoutParams3.setMargins((int) context.getResources().getDimension(R.dimen.dp_8), 0, 0, 0);
        } else {
            checkBoxAll.setVisibility(View.GONE);
            for (CheckBox c : checkBoxArr) {
                c.setVisibility(View.GONE);
            }
            layoutParams3.setMargins(0, 0, 0, 0);

        }
        textDate.setLayoutParams(layoutParams3);


        for (int i = 0; i < layoutArr.length; i++) {
            int finalI = i;
            if (editState) {
                checkBoxArr[i].setOnCheckedChangeListener((buttonView, isChecked) -> selectState[position][finalI] = isChecked);
                checkBoxArr[i].setChecked(selectState[position][i]);
            }

            layoutArr[i].setOnClickListener(v -> {
                Log.i("HIS", "setOnClickListener:" + position + ",finalI:" + finalI);
                if (editState) {
                    selectState[position][finalI] = !selectState[position][finalI];
                    adapter.notifyDataSetChanged();
                } else {
                    ArrayList<String> list = new ArrayList<>(drawingInfo.getUrlList());
                    ViewActivity.Companion.start(context, list, finalI, true);
                }
            });

        }

    }
}
