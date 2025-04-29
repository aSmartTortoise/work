package com.voyah.h37z;

import android.util.SparseArray;

import java.util.List;

/**
 * 视图管理接口类
 */

public interface IFragmentManager<T, M> {

    /**
     * 加载所有视图
     *
     * @param viewSA  视图管理容器
     * @param manager 加载视图管理器
     */
    void addViews(SparseArray<T> viewSA, M manager);

    /**
     * 显示目标视图
     *
     * @param id 目标视图id
     */
    void showView(int id, boolean isBackPage);


    /**
     * 显示视图集合
     *
     * @param viewIDs 目标视图id集合
     */
    void showViews(List<Integer> viewIDs);


    /**
     * 回到上一个页面
     */
    void back();

    /**
     * 显示画面退栈中视图集合数是否多余1个
     *
     * @return true 多余一个， false 少于或等于一个
     */
    boolean isMoreOneFragmentInStack();

    /**
     * 获取当前显示视图
     *
     * @return 当前视图
     */
    List<T> getCurrentViewInfos();
}
