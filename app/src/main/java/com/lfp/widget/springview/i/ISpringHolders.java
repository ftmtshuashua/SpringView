package com.lfp.widget.springview.i;

/**
 * SpringView 持有者,他的子类才能接收到SpringView的事件
 * Created by LiFuPing on 2018/3/14.
 */

public interface ISpringHolders {

    /**
     * @param dis_y      Y轴一次手势滑动的位移
     * @param distance_y Y轴上的总位移
     */
    void onSpring(float dis_y, float distance_y);

    /**
     * 当事件被取消
     */
    void onCancel();

    /**
     * 当事件完成 - 在这里执行逻辑
     */
    void onFinish();

    /**
     * 组ID,SpringView同一时间只会执行同一组的SpringChild
     */
    int getGroupId();
}
