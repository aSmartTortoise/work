package com.voyah.h37z;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.voyah.h37z.fragment.ConnectFragment;
import com.voyah.h37z.fragment.DisplayFragment;
import com.voyah.h37z.fragment.DrivePreferenceFragment;
import com.voyah.h37z.fragment.InterDriveFragment;
import com.voyah.h37z.fragment.LightFragment;
import com.voyah.h37z.fragment.SafetyMaintainFragment;
import com.voyah.h37z.fragment.SoundFragment;
import com.voyah.h37z.fragment.SystemFragment;
import com.voyah.h37z.fragment.VpaFragment;
import com.voyah.h37z.fragment.VehicleFragment;
import com.voyah.h37z.fragment.VoyahFragment;
import com.voyah.viewcmd.aspect.VoiceInterceptor;
import com.voyah.viewcmd.interceptor.IInterceptor;
import com.voyah.viewcmd.interceptor.SimpleInterceptor;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<FragmentTabInfo> mList;
    private MainAdapter mAdapter;
    private static int mCurrentIndex = 7;
    protected SparseArray<FragmentInfo> mFragmentInfos = new SparseArray<>();
    protected TsFragmentManager mTsFragmentManager;
    private boolean isBackPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Settings.canDrawOverlays(this)) {
            // 如果悬浮窗权限未被授予，跳转到系统设置界面请求权限
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1000);
        } else {
            // 悬浮窗权限已经被授予，可以执行相关操作
            Log.d("xyj", "app have overlay permission");
        }

        initRecyclerView();
        initData();
        // 默认tab
        mAdapter.setChoosePosition(mCurrentIndex);
        setAdapterItemChoose(mCurrentIndex);
    }

    @VoiceInterceptor
    private final IInterceptor<String> mInterceptor = new SimpleInterceptor() {
        @Override
        public boolean onLocateAdj(View view, Point point) {
            switch (view.getId()) {
                // 针对recycleView中点击的是Item, 所以填item的根布局id
                case R.id.layout_item_main:
                    point.x = point.x - 50;
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("xyj_test", "MainActivity onWindowFocusChanged() called with: hasFocus = [" + hasFocus + "]");
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
    }

    public void initData() {
        mList = new ArrayList<>();
        mList.add(new FragmentTabInfo(Constants.FRAGMENT_VOYAH_ID,
                R.mipmap.icon_my_voyah_n,
                R.mipmap.icon_my_voyah,
                getResources().getString(R.string.my_voyah)));
        mList.add(new FragmentTabInfo(Constants.FRAGMENT_VEHICLE_ID,
                R.mipmap.icon_vehicle_n,
                R.mipmap.icon_vehicle,
                getResources().getString(R.string.vehicle)));
        mList.add(new FragmentTabInfo(Constants.FRAGMENT_LIGHT_ID,
                R.mipmap.icon_light_n,
                R.mipmap.icon_light,
                getResources().getString(R.string.light)));
        mList.add(new FragmentTabInfo(Constants.FRAGMENT_DRIVE_PREFERENCE,
                R.mipmap.icon_driving_preference_n,
                R.mipmap.icon_driving_preference,
                getResources().getString(R.string.drive_preference)));
        mList.add(new FragmentTabInfo(Constants.FRAGMENT_INTELLIGENT_DRIVE,
                R.mipmap.icon_intelligent_driving_n,
                R.mipmap.icon_intelligent_driving,
                getResources().getString(R.string.drive_assist)));

        mList.add(new FragmentTabInfo(Constants.FRAGMENT_DISPLAY,
                R.mipmap.icon_display_n,
                R.mipmap.icon_display,
                getResources().getString(R.string.display)));

        mList.add(new FragmentTabInfo(Constants.FRAGMENT_SOUND,
                R.mipmap.icon_sound_n,
                R.mipmap.icon_sound,
                getResources().getString(R.string.sound)));

        mList.add(new FragmentTabInfo(Constants.FRAGMENT_VPA,
                R.mipmap.icon_vpa_n,
                R.mipmap.icon_vpa,
                getResources().getString(R.string.voice_assistant)));

        mList.add(new FragmentTabInfo(Constants.FRAGMENT_CONNECT,
                R.mipmap.icon_connect_n,
                R.mipmap.icon_connect,
                getResources().getString(R.string.connect)));

        mList.add(new FragmentTabInfo(Constants.FRAGMENT_SAFETY_AND_MAINTENANCE,
                R.mipmap.icon_safety_n,
                R.mipmap.icon_safety,
                getResources().getString(R.string.safety_and_repair)));

        mList.add(new FragmentTabInfo(Constants.FRAGMENT_SYSTEM,
                R.mipmap.icon_system_n,
                R.mipmap.icon_system,
                getResources().getString(R.string.system)));

        mAdapter = new MainAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((mList, position) -> {
            Log.d("xyj", "onItemClick: position:" + position);
            int selectIndex = mAdapter.getChoosePosition();
            if (selectIndex == position) {
                return;
            }
            mAdapter.setChoosePosition(position);
            FragmentTabInfo fragmentTabInfo = mList.get(position);
            if (fragmentTabInfo == null) {
                return;
            }
            setAdapterItemChoose(fragmentTabInfo.tabId);
            mCurrentIndex = position;
        });
    }

    private void setAdapterItemChoose(int tabId) {
        switch (tabId) {
            case Constants.FRAGMENT_VOYAH_ID:
                createFragmentInfo(R.id.fl_content,
                        Constants.FRAGMENT_VOYAH_ID,
                        new VoyahFragment());
                initFragments();
                showFragment(Constants.FRAGMENT_VOYAH_ID, false);
                break;
            case Constants.FRAGMENT_VEHICLE_ID:
                createFragmentInfo(R.id.fl_content,
                        Constants.FRAGMENT_VEHICLE_ID,
                        new VehicleFragment());
                initFragments();
                showFragment(Constants.FRAGMENT_VEHICLE_ID, false);
                break;
            case Constants.FRAGMENT_LIGHT_ID:
                createFragmentInfo(R.id.fl_content,
                        Constants.FRAGMENT_LIGHT_ID,
                        new LightFragment());
                initFragments();
                showFragment(Constants.FRAGMENT_LIGHT_ID, false);
                break;
            case Constants.FRAGMENT_DRIVE_PREFERENCE:
                createFragmentInfo(R.id.fl_content,
                        Constants.FRAGMENT_DRIVE_PREFERENCE,
                        new DrivePreferenceFragment());
                initFragments();
                showFragment(Constants.FRAGMENT_DRIVE_PREFERENCE, false);
                break;
            case Constants.FRAGMENT_INTELLIGENT_DRIVE:
                createFragmentInfo(R.id.fl_content,
                        Constants.FRAGMENT_INTELLIGENT_DRIVE,
                        new InterDriveFragment());
                initFragments();
                showFragment(Constants.FRAGMENT_INTELLIGENT_DRIVE, false);
                break;
            case Constants.FRAGMENT_DISPLAY:
                createFragmentInfo(R.id.fl_content,
                        Constants.FRAGMENT_DISPLAY,
                        new DisplayFragment());
                initFragments();
                showFragment(Constants.FRAGMENT_DISPLAY, false);
                break;
            case Constants.FRAGMENT_SOUND:
                createFragmentInfo(R.id.fl_content,
                        Constants.FRAGMENT_SOUND,
                        new SoundFragment());
                initFragments();
                showFragment(Constants.FRAGMENT_SOUND, false);
                break;
            case Constants.FRAGMENT_VPA:
                createFragmentInfo(R.id.fl_content,
                        Constants.FRAGMENT_VPA,
                        new VpaFragment());
                initFragments();
                showFragment(Constants.FRAGMENT_VPA, false);
                break;
            case Constants.FRAGMENT_CONNECT:
                createFragmentInfo(R.id.fl_content,
                        Constants.FRAGMENT_CONNECT,
                        new ConnectFragment());
                initFragments();
                showFragment(Constants.FRAGMENT_CONNECT, false);
                break;
            case Constants.FRAGMENT_SAFETY_AND_MAINTENANCE:
                createFragmentInfo(R.id.fl_content,
                        Constants.FRAGMENT_SAFETY_AND_MAINTENANCE,
                        new SafetyMaintainFragment());
                initFragments();
                showFragment(Constants.FRAGMENT_SAFETY_AND_MAINTENANCE, false);
                break;
            case Constants.FRAGMENT_SYSTEM:
                createFragmentInfo(R.id.fl_content,
                        Constants.FRAGMENT_SYSTEM,
                        new SystemFragment());
                initFragments();
                showFragment(Constants.FRAGMENT_SYSTEM, false);
                break;
            default:
                break;
        }
    }

    /**
     * 向fragment管理类加载所有通过createFragmentInfo已经加入到集合的fragments.
     */
    public void initFragments() {
        if (mTsFragmentManager == null) {
            mTsFragmentManager = new TsFragmentManager();
        }
        mTsFragmentManager.addViews(mFragmentInfos, getSupportFragmentManager());
    }

    /**
     * 创建FragmentInfo并存入Fragment管理容器.
     *
     * @param contentID  装载控件ID，Fragment所在FrameLayout的ID
     * @param fragmentID 自定义FragmentID，Fragment的唯一标识
     * @param fragment   Fragment对象
     */
    protected void createFragmentInfo(int contentID, int fragmentID, Fragment fragment) {
        FragmentInfo fragmentInfo;
        if (mFragmentInfos.get(fragmentID) == null) {
            fragmentInfo = new FragmentInfo.Builder()
                    .setContentID(contentID)
                    .setFragmentId(fragmentID)
                    .setFragment(fragment)
                    .build();
        } else {
            fragmentInfo = mFragmentInfos.get(fragmentID);
        }
        mFragmentInfos.put(fragmentID, fragmentInfo);
    }

    public void showFragment(int id, boolean isBackPage) {
        this.isBackPage = isBackPage;
        mTsFragmentManager.showView(id, isBackPage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("xyj_test", "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("xyj_test", "onPause() called");
    }
}