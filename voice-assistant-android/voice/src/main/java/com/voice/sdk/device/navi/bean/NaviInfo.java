package com.voice.sdk.device.navi.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("unused")
public class NaviInfo implements Serializable {

    @SerializedName("mRemainedDistance")
    private int mRemainedDistance;
    @SerializedName("mRemainedTime")
    private int mRemainedTime;
    @SerializedName("mTotalDistance")
    private int mTotalDistance;
    @SerializedName("mTotalTime")
    private int mTotalTime;
    @SerializedName("mNextViaRemainDistance")
    private int mNextViaRemainDistance;
    @SerializedName("mNextViaRemainTime")
    private int mNextViaRemainTime;
    @SerializedName("mRemainingDistanceAfterNextWaypoint")
    private int mRemainingDistanceAfterNextWaypoint;
    @SerializedName("mRemainingDistanceAfterDestination")
    private int mRemainingDistanceAfterDestination;

    public int getRemainedDistance() {
        return mRemainedDistance;
    }

    public void setRemainedDistance(int mRemainedDistance) {
        this.mRemainedDistance = mRemainedDistance;
    }

    public int getRemainedTime() {
        return mRemainedTime;
    }

    public void setRemainedTime(int mRemainedTime) {
        this.mRemainedTime = mRemainedTime;
    }

    public int getTotalDistance() {
        return mTotalDistance;
    }

    public void setTotalDistance(int mTotalDistance) {
        this.mTotalDistance = mTotalDistance;
    }

    public int getTotalTime() {
        return mTotalTime;
    }

    public void setTotalTime(int mTotalTime) {
        this.mTotalTime = mTotalTime;
    }

    public int getNextViaRemainDistance() {
        return mNextViaRemainDistance;
    }

    public void setNextViaRemainDistance(int mNextViaRemainDistance) {
        this.mNextViaRemainDistance = mNextViaRemainDistance;
    }

    public int getNextViaRemainTime() {
        return mNextViaRemainTime;
    }

    public void setNextViaRemainTime(int mNextViaRemainTime) {
        this.mNextViaRemainTime = mNextViaRemainTime;
    }

    public int getRemainingDistanceAfterNextWaypoint() {
        return mRemainingDistanceAfterNextWaypoint;
    }

    public void setRemainingDistanceAfterNextWaypoint(int mRemainingDistanceAfterNextWaypoint) {
        this.mRemainingDistanceAfterNextWaypoint = mRemainingDistanceAfterNextWaypoint;
    }

    public int getRemainingDistanceAfterDestination() {
        return mRemainingDistanceAfterDestination;
    }

    public void setRemainingDistanceAfterDestination(int mRemainingDistanceAfterDestination) {
        this.mRemainingDistanceAfterDestination = mRemainingDistanceAfterDestination;
    }
}
