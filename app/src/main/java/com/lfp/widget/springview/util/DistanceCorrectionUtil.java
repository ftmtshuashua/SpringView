package com.lfp.widget.springview.util;

/**
 * 距离修正工具
 * Created by LiFuPing on 2018/3/17.
 */

public class DistanceCorrectionUtil {

    double mTouchDistance; /*触摸位移*/
    double mCorrectionDistance;

    double mMaxTouchDistance;

    public DistanceCorrectionUtil() {
    }

    /*设置最大位移值*/
    public void setMaxTouchDistance(double maxTouchDistance) {
        mMaxTouchDistance = maxTouchDistance;
    }


    public void setTouchDistance(double dis){
        mTouchDistance =dis;
    }

    /*接收滑动*/
    public void move(double distance) {
        mTouchDistance += distance;
        if (mMaxTouchDistance > 0 && mTouchDistance > mMaxTouchDistance)
            mTouchDistance = mMaxTouchDistance;

        mCorrectionDistance = mTouchDistance / 2;
    }

    /*获得实际的位移值*/
    public double getTouchDistance() {
        return mTouchDistance;
    }

    /*获得修正之后的位移值*/
    public double getCorrectionDistance() {
        return mCorrectionDistance;
    }


}
