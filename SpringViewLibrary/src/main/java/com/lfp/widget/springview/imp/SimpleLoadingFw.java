package com.lfp.widget.springview.imp;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.lfp.widget.springview.SpringView;
import com.lfp.widget.springview.i.ISpringbackExecutor;

/**
 * 一个简单的加载框架
 * Created by LiFuPing on 2018/3/20.
 */

public abstract class SimpleLoadingFw extends ImpSpringChild_Bottom {

    private static final long FLAG_START_LOADING = 0x1; /*开始加载*/
    private static final long FLAG_All_DATA_IS_LOADED = 0x1 << 4; /*已经加载了所有数据*/


    /**
     * 初始化
     */
    public static final int STATE_INIT = 3;
    /**
     * 准备加载
     */
    public static final int STATE_PREPARE_LOADING = 1;
    /**
     * 开始加载
     */
    public static final int STATE_START_LOADING = 2;
    /**
     * 加载完成
     */
    public static final int STATE_LOADING_FINISH = 5;
    /**
     * 正在刷新
     */
    public static final int STATE_REFRESH_NOT_OVER = 6;
    /**
     * 已完成所有数据的加载
     */
    public static final int STATE_All_DATA_IS_LOADED = 7;

    int mStartLoadingHeight;/*开始加载的高度*/
    float mDistance;
    long mFlag;
    int mCurrentState;
    long mFinishAnimationDuration = 500l; /*完成动画持续时间*/


    SimpleRefreshFw mSimpleRefeshFw; /*互斥控制*/


    /**
     * 设置完成动画持续时间
     *
     * @param duration 持续事件
     */
    public void setFinishAnimationDuration(long duration) {
        mFinishAnimationDuration = duration;
        mFinishLoadding.setDuration(duration);
    }

    /**
     * 设置所有数据已经加载
     */
    public void setIsLoadedAllData(boolean is) {
        if (is) mFlag |= FLAG_All_DATA_IS_LOADED;
        else mFlag &= ~FLAG_All_DATA_IS_LOADED;
    }

    @Override
    public float onSpring(View springContentView, float dis_y, float correction_distance_y) {
        mDistance += dis_y / 2;
        if (mDistance < -mStartLoadingHeight) mDistance = -mStartLoadingHeight;

        boolean isRefeshing = mSimpleRefeshFw != null && mSimpleRefeshFw.isRefeshing();
        boolean isAllDataIsLoaded = (mFlag & FLAG_All_DATA_IS_LOADED) != 0;
        if (isRefeshing || isAllDataIsLoaded) {
            if (isRefeshing) setState(STATE_REFRESH_NOT_OVER);
            else if (isAllDataIsLoaded) setState(STATE_All_DATA_IS_LOADED);

            scrollTo(mDistance);
            if (mDistance >= 0) onCancel();
            return mDistance;
        }

        if (!isStartLoading()) {
            if (mDistance < -mStartLoadingHeight * 0.95f) {
                mFlag |= FLAG_START_LOADING;
                setState(STATE_START_LOADING);
                onLoading();
            } else {
                setState(STATE_PREPARE_LOADING);
            }
        } else {
            setState(STATE_START_LOADING);
        }

        scrollTo(mDistance);
        if (mDistance >= 0) onCancel();

        return mDistance;
    }

    void scrollTo(float dis) {
        getView().setTranslationY((int) dis);
        getParent().getContentView().setTranslationY(dis);
    }

    public boolean isStartLoading() {
        return (mFlag & FLAG_START_LOADING) != 0;
    }

    void setState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            onLoadingStateChange(state);
        }
    }

    /*回调状态*/
    protected abstract void onLoadingStateChange(int state);

    @Override
    public void onCancel() {
        mDistance = 0;
        scrollTo(mDistance);
        release();
    }

    @Override
    public void onFinish() {
        if (!isStartLoading()) {
            finishLoading();
        }
    }


    protected void setStartLoadingHeight(int height) {
        mStartLoadingHeight = height;
    }

    @Override
    public void onAttachToSpringView(final View contentView, final SpringView springView) {
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        springView.addView(contentView, params);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                int height = contentView.getHeight();
                setStartLoadingHeight(height);

                params.gravity = Gravity.BOTTOM;
                params.bottomMargin = -height;
                contentView.setLayoutParams(params);
            }
        });
    }


    /**
     * 绑定刷新加载控件。当刷新未结束，不允许加载。
     *
     * @param refesh 刷新控件
     */
    public void setRefeshFx(SimpleRefreshFw refesh) {
        mSimpleRefeshFw = refesh;
    }

    /*完成加载*/
    public void finishLoading() {
        if (isStartLoading()) {
            mFlag &= ~FLAG_START_LOADING;
            setState(STATE_LOADING_FINISH);
            springback(mFinishLoadding, mFinishAnimationDuration);
        } else springback(mCancelLoadding);
    }

    public abstract void onLoading();

    /*完成事件*/
    FinishLoadingSpringBack mFinishLoadding = new FinishLoadingSpringBack(mFinishAnimationDuration);
    /*取消事件*/
    ISpringbackExecutor mCancelLoadding = new ISpringbackExecutor() {
        @Override
        public void onSpringback(float rate, long currentPlayTime, boolean animationIsEnde) {
            scrollTo(mDistance * rate);
            if (animationIsEnde) {
                setState(STATE_INIT);
                onCancel();
            }
        }
    };


    /*完成事件动画*/
    private final class FinishLoadingSpringBack implements ISpringbackExecutor {
        float mWaitingProportion;/*回弹效果比例*/
        long mDurationTime; /*动画持续时间*/
        double mOffset = 0;

        public FinishLoadingSpringBack(long duration) {
            setDuration(duration);
        }

        public void setDuration(long duration) {
            mDurationTime = duration;
            mWaitingProportion = 250f / duration;
        }

        @Override
        public void onSpringback(float rate, long currentPlayTime, boolean animationIsEnde) {
            if (currentPlayTime == 0) mOffset = 0;
            if (mDurationTime * (1 - mWaitingProportion) < currentPlayTime) {
                if (mOffset == 0) mOffset = rate;
                float dis = (float) (mDistance * rate / mOffset);
                scrollTo(dis);
            }

            if (animationIsEnde) {
                onCancel();
                setState(STATE_INIT);
            }
        }
    }
}
