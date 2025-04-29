package com.voice.sdk.model;

/**
 * author : jie wang
 * date : 2025/3/7 11:14
 * description :
 */
public class UIPageInfo {

    public UIPageInfo(int position, int itemCount) {
        this.position = position;
        this.itemCount = itemCount;
    }

    public UIPageInfo(int position, int itemCount, int maxItemCount) {
        this.position = position;
        this.itemCount = itemCount;
        this.maxItemCount = maxItemCount;
    }

    /**
     *  当前page的index
     */
    private int position;

    /**
     *  当前page中item的数目
     */
    private int itemCount;

    /**
     *  每一页中item的最大数量
     */
    private int maxItemCount;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getMaxItemCount() {
        return maxItemCount;
    }

    public void setMaxItemCount(int maxItemCount) {
        this.maxItemCount = maxItemCount;
    }

    @Override
    public String toString() {
        return "UIPageInfo{" +
                "position=" + position +
                ", itemCount=" + itemCount +
                ", maxItemCount=" + maxItemCount +
                '}';
    }
}
