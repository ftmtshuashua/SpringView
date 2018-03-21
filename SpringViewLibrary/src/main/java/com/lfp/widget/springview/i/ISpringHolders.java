package com.lfp.widget.springview.i;

import android.view.View;

/**
 * SpringView 持有者,他的子类才能接收到SpringView的事件
 * Created by LiFuPing on 2018/3/14.
 */

public interface ISpringHolders {

    /**
     * @param springContentView     SpringView中的内容View
     * @param dis_y                 Y轴一次手势滑动的位移
     * @param correction_distance_y 上一个Holders控制之后单最终位置,如果是第一个Holders 那么它单值是 0
     * @return 返回实际单位移值
     */
    float onSpring(View springContentView, float dis_y, float correction_distance_y);

    /**
     * 当事件被取消
     */
    void onCancel();

    /**
     * 当用户手指抬起的时候 - 在这里执行逻辑
     */
    void onFinish();

    /**
     * 组ID,SpringView同一时间只会执行同一组的SpringChild
     *
     * @return 获得分组ID
     */
    int getGroupId();
}
