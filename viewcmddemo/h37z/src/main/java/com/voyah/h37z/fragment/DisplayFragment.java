package com.voyah.h37z.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.voyah.ai.sdk.manager.DialogueManager;
import com.voyah.h37z.MainApplication;
import com.voyah.h37z.R;
import com.voyah.h37z.VoiceViewModel;
import com.voyah.viewcmd.VoiceViewCmdUtils;

import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class DisplayFragment extends Fragment {

    private static final String TAG = "xyj_test";
    private ParentAdapter parentAdapter;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static boolean showCorner = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nested_recycler_view, container, false);

        View rootView = requireActivity().getWindow().getDecorView().getRootView();
        RecyclerView parentRecyclerView = view.findViewById(R.id.parentRecyclerView);
        parentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        parentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateBadge(parentRecyclerView, rootView);
                }
            }
        });

        List<List<String>> data = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<String> childData = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                childData.add("Child Item " + j + " in Parent " + i);
            }
            data.add(childData);
        }

        parentAdapter = new ParentAdapter(data);
        parentRecyclerView.setAdapter(parentAdapter);

        updateBadge(parentRecyclerView, rootView);

        // 监听语音状态
        VoiceViewModel voiceViewModel = new ViewModelProvider(MainApplication.getApplication(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(MainApplication.getApplication())).get(VoiceViewModel.class);
        voiceViewModel.voiceStateChangeData.observe(this, isWakeup -> {
            Log.d(TAG, "onChanged = [" + isWakeup + "]");
            updateShowCorner(parentRecyclerView, isWakeup);
            updateBadge(parentRecyclerView, rootView);
        });

        return view;
    }

    private void updateShowCorner(RecyclerView recyclerView, Boolean isWakeup) {
        int viewDisplayId = VoiceViewCmdUtils.getDisplayId(recyclerView);
        int voiceDisplayId = DialogueManager.getCurVpaDisplayId();
        showCorner = (isWakeup && voiceDisplayId != -1 && viewDisplayId == voiceDisplayId);
        Log.d(TAG, "updateShowCorner, isWakeup:" + isWakeup + ", viewDisplayId:" + viewDisplayId + ", voiceDisplayId:" + voiceDisplayId + ", showCorner:" + showCorner);
    }

    private void updateBadge(RecyclerView parentRecyclerView, View rootView) {
        handler.postDelayed(() -> {
            Log.d(TAG, "============== start =====================");
            LinearLayoutManager layoutManager = (LinearLayoutManager) parentRecyclerView.getLayoutManager();
            int outerFirst = layoutManager.findFirstVisibleItemPosition();
            int outerLast = layoutManager.findLastVisibleItemPosition();
            if (outerFirst == -1) {
                return;
            }
            int totalOffset = 0;
            int offset = 0;
            for (int i = outerFirst; i <= outerLast; i++) {
                RecyclerView childRecyclerView = parentAdapter.getChildRecyclerView(i);
                int firstItemPosition = -1;
                int lastItemPosition = -1;
                boolean findFirstItemPosition = false;
                ChildAdapter childAdapter = (ChildAdapter) childRecyclerView.getAdapter();
                if (childAdapter != null) {
                    int itemCount = childAdapter.getItemCount();
                    for (int j = 0; j < itemCount; j++) {
                        ChildAdapter.ChildViewHolder viewHolder = (ChildAdapter.ChildViewHolder) childRecyclerView.findViewHolderForAdapterPosition(j);
                        if (viewHolder != null) {
                            boolean isVisible = isViewVisibleOnScreen(rootView, viewHolder.tvBadge);
                            if (isVisible && !findFirstItemPosition) {
                                firstItemPosition = j;
                                findFirstItemPosition = true;
                            } else if (!isVisible && findFirstItemPosition) {
                                lastItemPosition = j - 1;
                                break;
                            }
                            if (firstItemPosition != -1 && lastItemPosition == -1) {
                                lastItemPosition = itemCount - 1;
                            }
                        }
                    }
                }
                if (i == outerFirst) {
                    totalOffset = 0;
                } else {
                    totalOffset += offset;
                }
                offset = lastItemPosition - firstItemPosition + 1;

                Log.d(TAG, "updateBadge, index:" + i + ", firstItemPosition:" + firstItemPosition
                        + ", lastItemPosition:" + lastItemPosition);
                ChildAdapter adapter = (ChildAdapter) childRecyclerView.getAdapter();
                adapter.updateVisibleItemBadges(firstItemPosition, lastItemPosition, totalOffset);
                //adapter2.updateVisibleItemBadges(firstVisiblePosition2, lastVisiblePosition2, (lastVisiblePosition1 - firstVisiblePosition1) + 1);
                //adapter3.updateVisibleItemBadges(firstVisiblePosition3, lastVisiblePosition3, (lastVisiblePosition1 - firstVisiblePosition1) + (lastVisiblePosition2 - firstVisiblePosition2) + 2);
            }
            Log.d(TAG, "============== end =====================");
        }, 50);
    }

    private boolean isViewVisibleOnScreen(View rootView, View view) {
        Rect scrollBounds = new Rect();
        rootView.getHitRect(scrollBounds);
        return view.getLocalVisibleRect(scrollBounds);
    }

    private static class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentViewHolder> {

        private final List<List<String>> data;
        private final List<RecyclerView> rvList;

        ParentAdapter(List<List<String>> data) {
            this.data = data;
            this.rvList = new ArrayList<>(data.size());
        }

        @NonNull
        @Override
        public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent_recycler_view, parent, false);
            return new ParentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
            holder.bind(data.get(position), rvList, position);
        }

        public RecyclerView getChildRecyclerView(int position) {
            return rvList.get(position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ParentViewHolder extends RecyclerView.ViewHolder {

            private final RecyclerView childRecyclerView;

            ParentViewHolder(@NonNull View itemView) {
                super(itemView);
                childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
            }

            void bind(List<String> childData, List<RecyclerView> list, int index) {
                ChildAdapter childAdapter = new ChildAdapter(childData, index);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(childRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
                childRecyclerView.setLayoutManager(linearLayoutManager);
                childRecyclerView.setAdapter(childAdapter);
                childRecyclerView.addItemDecoration(new MyItemDecoration());
                list.add(index, childRecyclerView);
            }
        }
    }

    private static class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {

        private final List<String> data;
        private final int index;
        private static final String PLAY_LOAD = "update_badge";

        ChildAdapter(List<String> data, int index) {
            this.data = data;
            this.index = index;
        }

        @NonNull
        @Override
        public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout2, parent, false);
            if (index == 0) {
                view.setBackgroundResource(android.R.color.holo_purple);
            } else if (index == 1) {
                view.setBackgroundResource(android.R.color.holo_green_dark);
            } else if (index == 2) {
                view.setBackgroundResource(android.R.color.holo_orange_dark);
            }
            return new ChildViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
            String s = data.get(position);
            String[] split = s.split("#");
            if (split.length > 0) {
                holder.tvItem.setText(split[0]);
            }
            if (split.length > 1) {
                holder.tvBadge.setText(split[1]);
            }
            holder.tvBadge.setVisibility(showCorner ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public void onBindViewHolder(@NonNull ChildViewHolder holder, int position, @NonNull List<Object> payloads) {
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
                        holder.tvBadge.setText(split[1]);
                    }
                    holder.tvBadge.setVisibility(showCorner ? View.VISIBLE : View.INVISIBLE);
                    break;
                }
            }
        }

        public void updateVisibleItemBadges(int firstVisible, int lastVisible, int offset) {
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

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ChildViewHolder extends RecyclerView.ViewHolder {

            TextView tvItem;
            TextView tvBadge;

            ChildViewHolder(@NonNull View itemView) {
                super(itemView);
                tvItem = itemView.findViewById(R.id.tvItem);
                tvBadge = itemView.findViewById(R.id.tv_corner);
            }
        }
    }

    private static class MyItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(26, 20, 26, 20); // 设置每个项的左、上、右、下边距为16dp
        }
    }
}
