package com.voyah.voice.main.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.voice.drawing.api.model.DrawingInfo;
import com.voyah.voice.main.HistoryImageViewHolder;
import com.voyah.voice.main.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryImageViewHolder> {


    private boolean editState;
    private final List<DrawingInfo> drawingInfoList = new ArrayList<>();
    private final List<String> dateList = new ArrayList<>();
    private final List<Boolean> showDateList = new ArrayList<>();

    private int[] root;

    private boolean[][] selectState;

    public HistoryAdapter(ArrayList<DrawingInfo> drawingInfoList, boolean editState) {
        if (drawingInfoList != null) {
            this.drawingInfoList.clear();
            this.drawingInfoList.addAll(drawingInfoList);
            selectState = new boolean[drawingInfoList.size()][4];
            this.editState = editState;
            refresh();
        }
    }


    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull HistoryImageViewHolder holder, int position) {
        holder.bind(this, position, drawingInfoList.get(position), dateList.get(position), showDateList.get(position), editState, selectState);
        holder.checkBoxAll.setOnClickListener(v -> {
            Log.i("his", "holder.checkBoxAll.isChecked():" + isAllSelected(position));
            boolean isChecked = !isAllSelected(position);
            for (int i = 0; i < root.length; i++) {
                if (root[i] == position) {
                    Arrays.fill(selectState[i], isChecked);
                }
            }
            notifyDataSetChanged();
        });
        holder.checkBoxAll.setChecked(isAllSelected(position));
    }

    private boolean isAllSelected(int position) {
        Log.i("his", "root size:" + root.length + ",selectState:" + selectState.length);
        for (int i = 0; i < drawingInfoList.size(); i++) {
            int size = drawingInfoList.get(i).getUrlList() != null ? drawingInfoList.get(i).getUrlList().size() : 0;
            for (int j = 0; j < size; j++) {
                if (root[i] == position && !selectState[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @NonNull
    @Override
    public HistoryImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_his, parent, false);
        return new HistoryImageViewHolder(parent.getContext(), view);
    }

    @Override
    public int getItemCount() {
        return drawingInfoList.size();
    }

    public void refresh() {
        dateList.clear();
        showDateList.clear();
        root = new int[drawingInfoList.size()];
        Set<String> dateSet = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        long yesterday = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
        long today = System.currentTimeMillis();
        String yesterdayStr = sdf.format(new Date(yesterday));
        String todayStr = sdf.format(new Date(today));
        int lastRoot = 0;
        for (int i = 0; i < drawingInfoList.size(); i++) {
            String date = sdf.format(drawingInfoList.get(i).getTime());
            if (yesterdayStr.equals(date)) {
                date = "昨天";
            }
            if (todayStr.equals(date)) {
                date = "今天";
            }
            dateList.add(date);
            boolean isRoot = dateSet.add(date);
            if (isRoot) {
                lastRoot = i;
            }
            root[i] = lastRoot;
            showDateList.add(isRoot);
        }
        notifyDataSetChanged();
    }

    public void setEditState(boolean editState) {
        if (this.editState != editState) {
            this.editState = editState;
            if (selectState != null) {
                for (boolean[] arr : selectState) {
                    Arrays.fill(arr, false);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void selectAll(boolean select) {
        for (boolean[] arr : selectState) {
            Arrays.fill(arr, select);
        }
        notifyDataSetChanged();
    }

    public boolean[][] getSelectState() {
        return selectState;
    }

    public List<DrawingInfo> getDrawingInfoList() {
        return drawingInfoList;
    }

    public void clear() {
        selectState = null;
    }
}
