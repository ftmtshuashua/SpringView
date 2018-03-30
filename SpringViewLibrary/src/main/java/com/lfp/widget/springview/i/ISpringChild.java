package com.lfp.widget.springview.i;

import android.content.Context;
import android.view.View;

import com.lfp.widget.springview.SpringView;

/**
 * 划拉效果控制类，该类控制和接收SpringView的滑动事件，并控制SpringView的响应逻辑
 * Created by LiFuPing on 2018/3/12.
 */

public abstract class ISpringChild implements ISpringHolders {

    public static final long FLAG_DISABLE = 0x1 << 1; /*禁用该Child*/

    //    public static final int FLAG_RUNING = 1; /*标志这个ISpringChild正在被执行*/
    View mView; /*SpringChild的内容*/
    SpringView mParent;

    int mChildFlag;
    int mGroupId;/*组ID,SpringView同一时间只会执行同一组的SpringChild*/


    /*获取SpringChild的View*/
    public final View initView(Context context, SpringView springView) {
        mParent = springView;
        if (mView == null) {
            mView = onCreateView(context, springView);
            onViewCreated(mView);
        }
        return mView;
    }

    /*子类必须实现的方法，返回SpringChild的View*/
    public abstract View onCreateView(Context context, SpringView springView);

    public final View getView() {
        return mView;
    }

    /*当ContentView创建完成之后会回调此方法*/
    protected void onViewCreated(View view) {
    }

    /*这个方法完成将ContentView添加到SpringView中*/
    public void onAttachToSpringView(View contentView, SpringView springView) {
        if (contentView != null) springView.addView(contentView, 0);
    }

    public Context getContext() {
        return mParent.getContext();
    }

    public SpringView getParent() {
        return mParent;
    }

    public void setGroupId(int groupId) {
        mGroupId = groupId;
    }

    @Override
    public int getGroupId() {
        return mGroupId;
    }

    /**
     * 回弹(调用SpringView已经定义好的一种回弹方式,这个方式也可以自己实现)
     *
     * @param ise 执行回调，回弹逻辑实现的地方
     */
    public void springback(ISpringbackExecutor ise) {
        springback(ise, 250);
    }

    /**
     * 回弹(调用SpringView已经定义好的一种回弹方式,这个方式也可以自己实现)
     *
     * @param ise      执行回调，回弹逻辑实现的地方
     * @param duration 执行时间
     */
    public void springback(ISpringbackExecutor ise, long duration) {
        getParent().starSpringback(this, ise, duration);
    }

    /**
     * 清理 - 当SpringChild执行完成之后应该在适当的时候调用此方法来清理一些必要的状态，以确保SpringView能正常执行后续动作
     */
    public void release() {
        getParent().unregisterHolder(this);
    }

    /**
     * 检查是否需要持有这个SpringView，当SpringView被持有的时候，他将接管SpringView的所有操作
     *
     * @param edgeCheckUtil  边缘检测器
     * @param trendCheckUtil 手势检测器
     * @return 是否拦截SpringView的事件
     */
    public abstract boolean onCheckHoldSpringView(SpringView.EdgeCheckUtil edgeCheckUtil, SpringView.TrendCheckUtil trendCheckUtil);

    /**
     * 是否启用当前Child
     *
     * @param enable 启用与否
     */
    public void setEnable(boolean enable) {
        if (enable) mChildFlag &= ~FLAG_DISABLE;
        else mChildFlag |= FLAG_DISABLE;
    }

    /**是否启用当前Child
     * @return 启用与否*/
    public boolean isEnable() {
        return (mChildFlag & FLAG_DISABLE) == 0;
    }
}
