package com.voyah.h37z;

import android.util.Log;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * 视图管理类，主要用于视图的加载、显示、隐藏和当前画面的记录工作
 */
public class TsFragmentManager implements IFragmentManager<FragmentInfo, FragmentManager> {
    private static final String TAG = Constants.TAG + TsFragmentManager.class.getSimpleName();

    private FragmentManager mFragmentManager;
    private List<FragmentInfo> mCurrentFragmentInfos = new ArrayList<>();
    //窗口ID栈，用于记录view的显示顺序
    private Stack<List<Integer>> mViewStacks = new Stack<>();
    //所有View信息的存储器
    private SparseArray<FragmentInfo> mFragmentSA = new SparseArray<>();

    /**
     * 向fragment 集合中添加路径
     *
     * @param viewSA  视图管理容器
     * @param manager 加载视图管理器
     */
    @Override
    public void addViews(SparseArray<FragmentInfo> viewSA, FragmentManager manager) {
        int size = viewSA.size();
        Log.i("ts", "addViews size: " + size);
        mFragmentManager = manager;
        FragmentInfo fragmentInfo;
        int id;

        for (int i = 0; i < size; i++) {
            id = viewSA.keyAt(i);
            fragmentInfo = viewSA.get(id);
            Log.i(TAG, "addViews fragmentInfo: " + fragmentInfo.toString());
            mFragmentSA.put(id, fragmentInfo);

        }
    }

    @Override
    public void showView(int id, boolean isBackPage) {
        Log.i(TAG, "show fragmentInfo id: " + id);
        FragmentInfo view = mFragmentSA.get(id);
        if (view == null) {
            Log.i(TAG, "view is null " + id);
        }

        //判断当前显示视图是否与要显示的视图相同
        boolean isCurViews = mCurrentFragmentInfos.size() == 1
                && mCurrentFragmentInfos.contains(view);
        if (isCurViews) {
            Log.i(TAG, "dest views is current views!!!");
            return;
        }

        //Log.i(TAG, "show fragmentInfo : " + view.toString());
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Log.i(TAG, "show mCurrentFragmentInfos.isEmpty() : " + mCurrentFragmentInfos.isEmpty());
        hideCurrentViews(transaction);
        showView(id, view, transaction, isBackPage);
    }


    private void showView(int id, FragmentInfo view, FragmentTransaction transaction,
                          boolean isBackPage) {
        if (view == null || transaction == null) {
            return;
        }
        Log.i(TAG, "show fragmentInfo: " + view.toString());
        Fragment fragment = view.getFragment();
        if (fragment == null) {
            return;
        }
        int contentID = view.getContentID();
        /*if (fragment instanceof VehicleFragment && !fragment.isAdded()) {
            Log.i(TAG, "add fragment :" + fragment.toString());
            transaction.add(contentID, fragment);
        } else {
            transaction.replace(contentID, fragment);
        }*/
        transaction.replace(contentID, fragment);
        //当前界面为一级界面时,清除窗口ID栈
        if (!isBackPage) {
            mViewStacks.clear();
        }

        transaction.show(fragment).commitAllowingStateLoss();
        mCurrentFragmentInfos.add(view);

        List<Integer> list = new ArrayList<>();
        list.add(id);
        mViewStacks.add(list);
    }


    @Override
    public void showViews(List<Integer> viewIDs) {
        Log.i(TAG, "showViews start!");
        if (viewIDs == null) {
            return;
        }

        List<FragmentInfo> views = getFragmentInfos(viewIDs);

        //判断当前显示视图是否与要显示的视图相同
        boolean isCurViews = ListHelper.compare(views, mCurrentFragmentInfos);
        if (isCurViews) {
            Log.i(TAG, "dest views is current views!!!");
            return;
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        Log.i(TAG, "showViews,hideCurrentViews!");
        //隐藏当前显示画面
        hideCurrentViews(transaction);
        Log.i(TAG, "showViews,hideCurrentViews!");

        Log.i(TAG, "showViews,showViews!");
        showViews(viewIDs, views, transaction);
        Log.i(TAG, "showViews end!");
    }


    private void showViews(List<Integer> viewIDs, List<FragmentInfo> views,
                           FragmentTransaction transaction) {

        //显示所有目标视图
        showViews(views, transaction);
        mCurrentFragmentInfos.addAll(views);

        //将显示视图集合加入到显示堆栈中
        mViewStacks.add(viewIDs);
    }

    private void showViews(List<FragmentInfo> views, FragmentTransaction transaction) {
        FragmentInfo fragmentInfo;
        Fragment fragment;
        int contentID;
        int viewSize = views.size();
        for (int i = 0; i < viewSize; i++) {
            fragmentInfo = views.get(i);
            fragment = fragmentInfo.getFragment();
            contentID = fragmentInfo.getContentID();
            /*if (fragment instanceof VehicleFragment && !fragment.isAdded()) {
                Log.i(TAG, "add fragment :" + fragment.toString());
                transaction.add(contentID, fragment);
            } else {
                Log.i(TAG, "add fragment :" + fragment.toString());
                transaction.replace(contentID, fragment);
            }*/
            transaction.replace(contentID, fragment);
            transaction.show(fragment);
            Log.i(TAG, "showViews fragmentInfo " + i + ": " + fragmentInfo.toString());
        }

        transaction.commitAllowingStateLoss();
    }


    @Override
    public List<FragmentInfo> getCurrentViewInfos() {
        return mCurrentFragmentInfos;
    }

    @Override
    public void back() {
        //当前栈顶窗口退栈
        mViewStacks.pop();
        //获取上一组ViewID，打开上一组View
        List<Integer> lastViewIDs = mViewStacks.peek();

        //将ID转换为FragmentInfo，并判断是否当前画面已经显示其中的某一个ID视图，
        //如果当前画面已经显示其中某一个ID视图，将不再进行重新显示
        List<FragmentInfo> views = getFragmentInfos(lastViewIDs);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        //隐藏当前显示画面
        hideCurrentViews(transaction);

        //显示所有目标视图
        showViews(views, transaction);

        mCurrentFragmentInfos.addAll(views);
    }

    @Override
    public boolean isMoreOneFragmentInStack() {
        return mViewStacks.size() > 1 ? true : false;
    }

    private void hideCurrentViews(FragmentTransaction transaction) {
        if (!mCurrentFragmentInfos.isEmpty()) {
            Iterator it = mCurrentFragmentInfos.iterator();
            FragmentInfo fragmentInfo;
            while (it.hasNext()) {
                fragmentInfo = (FragmentInfo) it.next();
                Fragment curFragment = fragmentInfo.getFragment();
                transaction.hide(curFragment);
                Log.i(TAG, "show hide current fragmentInfo: " + fragmentInfo.toString());
                it.remove();
            }
        }
    }

    private List<FragmentInfo> getFragmentInfos(List<Integer> lastViewIDs) {
        List<FragmentInfo> views = new ArrayList<>();
        int viewIDSize = lastViewIDs.size();
        for (int i = 0; i < viewIDSize; i++) {
            int id = lastViewIDs.get(i);
            FragmentInfo fragmentInfo = mFragmentSA.get(id);
            views.add(fragmentInfo);
        }
        return views;
    }


    public void release() {
        if (mFragmentManager != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            hideCurrentViews(transaction);
        }
        mCurrentFragmentInfos.clear();
        mViewStacks.clear();
        mFragmentSA.clear();
    }
}
