package com.voyah.h37z;

import androidx.fragment.app.Fragment;

import java.io.Serializable;

/**
 * Fragment信息实体类
 */
public class FragmentInfo implements Serializable, Comparable {

    private static final long serialVersionUID = 3621444939891049543L;

    /**
     * 装载控件ID，Fragment所在FrameLayout的ID
     */
    private int contentID;

    /**
     * 自定义FragmentId,Fragment的唯一标识符
     */
    private int fragmentId;

    private Fragment fragment;

    private FragmentInfo(Builder builder) {
        this.contentID = builder.contentID;
        this.fragmentId = builder.fragmentId;
        this.fragment = builder.fragment;
    }

    /**
     * 获取装载控件ID，Fragment所在FrameLayout的ID
     */
    public int getContentID() {
        return contentID;
    }

    /**
     * 获取自定义FragmentID，Fragment的唯一标识
     */
    public int getFragmentId() {
        return fragmentId;
    }

    /**
     * 设置自定义FragmentID，Fragment的唯一标识
     */
    public void setFragmentId(int fragmentId) {
        this.fragmentId = fragmentId;
    }

    /**
     * 获取Fragment对象
     *
     * @return
     */
    public Fragment getFragment() {
        return fragment;
    }

    @Override
    public String toString() {
        return "FragmentInfo{" +
                "contentID=" + contentID +
                ", fragmentId=" + fragmentId +
                ", fragment=" + fragment +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        FragmentInfo fragmentInfo = (FragmentInfo) o;
        if (fragmentInfo.fragmentId < fragmentId) {
            return -1;
        }

        if (fragmentInfo.fragmentId > fragmentId) {
            return 1;
        }

        return 0;
    }

    public void release() {
        fragment = null;
    }

    public static class Builder {

        private int contentID;

        private int fragmentId;

        private Fragment fragment;

        public Builder setContentID(int contentID) {
            this.contentID = contentID;
            return this;
        }

        public Builder setFragmentId(int fragmentId) {
            this.fragmentId = fragmentId;
            return this;
        }

        public Builder setFragment(Fragment fragment) {
            this.fragment = fragment;
            return this;
        }


        public FragmentInfo build() {
            return new FragmentInfo(this);
        }
    }

}
