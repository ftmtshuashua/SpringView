package com.lfp.widget.springview.util;

/**
 * 距离修正工具
 * Created by Administrator on 2018/3/17.
 */

public class DistanceCorrectionUtil {

    double mMaxDistance;
    double mStartDistance;



    /**
     * @param max_distance 最大距离
     */
    public DistanceCorrectionUtil(double max_distance) {
        setMaxDistance(max_distance);
    }

    public void setMaxDistance(double max_distance) {
        mMaxDistance = max_distance;
    }


    /**
     * @param distance 需要修正的数据
     * @return 修正之后的数据
     */
    public double correction(double distance) {
        if (distance > mMaxDistance) return mMaxDistance;
        if (distance <= mStartDistance) return distance;
        return (1d - distance / (distance + mMaxDistance)) * mMaxDistance;
    }

}
