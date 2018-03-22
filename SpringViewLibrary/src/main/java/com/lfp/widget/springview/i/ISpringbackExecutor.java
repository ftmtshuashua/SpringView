package com.lfp.widget.springview.i;

/**
 * 回弹执行者
 * Created by LiFuPing on 2018/3/15.
 */

public interface ISpringbackExecutor {

    /**
     * @param rate            回弹比例(1.0f ~ 0.0f)
     * @param currentPlayTime 当前运行时间
     */
    void onSpringback(float rate, long currentPlayTime);

}
