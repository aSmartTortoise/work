package com.voyah.h37z.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.voyah.h37z.R;

import java.util.Arrays;
import java.util.List;

public class SafetyMaintainFragment extends Fragment {

    private static final String TAG = "SafetyMaintainFragment";
    private RecyclerView recyclerView1, recyclerView2, recyclerView3;
    private MyAdapter adapter1, adapter2, adapter3;
    private LinearLayoutManager layoutManager1, layoutManager2, layoutManager3;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safety, container, false);

        recyclerView1 = view.findViewById(R.id.recyclerView1);
        recyclerView2 = view.findViewById(R.id.recyclerView2);
        recyclerView3 = view.findViewById(R.id.recyclerView3);
        recyclerView1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateBadge();
                }
            }
        });
        recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateBadge();
                }
            }
        });
        recyclerView3.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateBadge();
                }
            }
        });

        // 设置布局管理器
        layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(layoutManager1);
        recyclerView1.addItemDecoration(new MyItemDecoration());

        layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.addItemDecoration(new MyItemDecoration());

        layoutManager3 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView3.setLayoutManager(layoutManager3);
        recyclerView3.addItemDecoration(new MyItemDecoration());

        // 准备数据
        List<String> data1 = Arrays.asList("Item 1-1", "Item 1-2", "Item 1-3", "Item 1-4", "Item 1-5", "Item 1-6", "Item 1-7", "Item 1-8");
        List<String> data2 = Arrays.asList("Item 2-1", "Item 2-2", "Item 2-3", "Item 2-4", "Item 2-5", "Item 2-6", "Item 2-7", "Item 2-8");
        List<String> data3 = Arrays.asList("Item 3-1", "Item 3-2", "Item 3-3");

        // 创建适配器
        adapter1 = new MyAdapter(data1);
        adapter2 = new MyAdapter(data2);
        adapter3 = new MyAdapter(data3);

        // 设置适配器
        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setAdapter(adapter3);

        updateBadge();

        return view;
    }

    private void updateBadge() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int firstVisiblePosition1 = adapter1.getAdjustedFirstVisiblePosition(layoutManager1);
                int lastVisiblePosition1 = adapter1.getAdjustedLastVisiblePosition(layoutManager1);
                int firstVisiblePosition2 = adapter2.getAdjustedFirstVisiblePosition(layoutManager2);
                int lastVisiblePosition2 = adapter2.getAdjustedLastVisiblePosition(layoutManager2);
                int firstVisiblePosition3 = adapter3.getAdjustedFirstVisiblePosition(layoutManager3);
                int lastVisiblePosition3 = adapter3.getAdjustedLastVisiblePosition(layoutManager3);

                Log.d(TAG, "================================");
                Log.d(TAG, "firstVisiblePosition1:" + firstVisiblePosition1 + ", lastVisiblePosition1:" + lastVisiblePosition1);
                Log.d(TAG, "firstVisiblePosition2:" + firstVisiblePosition2 + ", lastVisiblePosition2:" + lastVisiblePosition2);
                Log.d(TAG, "firstVisiblePosition3:" + firstVisiblePosition3 + ", lastVisiblePosition3:" + lastVisiblePosition3);
                Log.d(TAG, "================================");

                adapter1.updateVisibleItemBadges(layoutManager1, firstVisiblePosition1, lastVisiblePosition1, 0);
                adapter2.updateVisibleItemBadges(layoutManager2, firstVisiblePosition2, lastVisiblePosition2, (lastVisiblePosition1 - firstVisiblePosition1) + 1);
                adapter3.updateVisibleItemBadges(layoutManager3, firstVisiblePosition3, lastVisiblePosition3, (lastVisiblePosition1 - firstVisiblePosition1) + (lastVisiblePosition2 - firstVisiblePosition2) + 2);
            }
        }, 50);
    }

    // 适配器类
    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private final List<String> data;
        private static final String PLAY_LOAD = "update_badge";

        public MyAdapter(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String s = data.get(position);
            String[] split = s.split("#");
            if (split.length > 0) {
                holder.textView.setText(split[0]);
            }
            if (split.length > 1) {
                holder.badgeTextView.setText(split[1]);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position);
                return;
            }
            String s = data.get(position);
            String[] split = s.split("#");
            for (Object payload : payloads) {
                String payloadType = (String) payload;
                if (PLAY_LOAD.equals(payloadType)) {
                    if (split.length > 1) {
                        holder.badgeTextView.setText(split[1]);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        public void updateVisibleItemBadges(LinearLayoutManager layoutManager, int firstVisible, int lastVisible, int offset) {
            if (layoutManager != null) {
                if (firstVisible != RecyclerView.NO_POSITION) {
                    Log.d(TAG, "firstVisible:" + firstVisible + ", lastVisible:" + lastVisible + ", offset:" + offset);
                    for (int i = firstVisible; i <= lastVisible; i++) {
                        if (i >= 0 && i < data.size()) {
                            int badge = offset + (i - firstVisible) + 1;
                            String newStr = data.get(i).split("#")[0] + "#" + badge;
                            Log.d(TAG, "badge:" + badge);
                            data.set(i, newStr);
                        }
                    }
                    notifyItemRangeChanged(firstVisible, lastVisible - firstVisible + 1, PLAY_LOAD);
                } else {
                    Log.e(TAG, "firstVisible = -1");
                }
            }
        }

        public int getAdjustedFirstVisiblePosition(LinearLayoutManager layoutManager) {
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            if (firstVisiblePosition == RecyclerView.NO_POSITION) {
                return RecyclerView.NO_POSITION; // 如果没有可见项，则返回NO_POSITION
            }
            View firstVisibleChild = layoutManager.findViewByPosition(firstVisiblePosition);
            if (firstVisibleChild == null) {
                return firstVisiblePosition; // 如果无法获取第一个可见子视图，则直接返回第一个可见位置
            }
            int firstVisibleLef = Math.abs(firstVisibleChild.getLeft());

            int threshold = 56;
            if (firstVisibleLef > threshold) {
                return firstVisiblePosition + 1;
            }

            return firstVisiblePosition;
        }

        public int getAdjustedLastVisiblePosition(LinearLayoutManager layoutManager) {
            int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
            if (lastVisiblePosition == RecyclerView.NO_POSITION) {
                return RecyclerView.NO_POSITION; // 如果没有可见项，则返回NO_POSITION
            }
            View lastVisibleChild = layoutManager.findViewByPosition(lastVisiblePosition);
            if (lastVisibleChild == null) {
                return lastVisiblePosition; // 如果无法获取第一个可见子视图，则直接返回第一个可见位置
            }
            return lastVisiblePosition;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            TextView badgeTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.tvItem);
                badgeTextView = itemView.findViewById(R.id.tv_corner);
            }
        }
    }

    private static class MyItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(26, 56, 26, 56); // 设置每个项的左、上、右、下边距为16dp
        }
    }
}
