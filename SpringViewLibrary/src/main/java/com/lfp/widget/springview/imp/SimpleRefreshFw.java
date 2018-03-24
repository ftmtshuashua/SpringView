package com.lfp.widget.springview.imp;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;

import com.lfp.widget.springview.SpringView;
import com.lfp.widget.springview.i.ISpringbackExecutor;

/**
 * 一个简单的刷新框架
 * Created by LiFuPing on 2018/3/19.
 */

public abstract class SimpleRefreshFw extends ImpSpringChild_Top {

    /*下拉刷新*/
    public static final int STATE_DOWN_REFESH = 4;
    /*准备刷新*/
    public static final int STATE_PREPARE_REFESH = 1;
    /*开始刷新*/
    public static final int STATE_START_REFESH = 2;
    /*初始化*/
    public static final int STATE_INIT = 3;
    /*刷新完成*/
    public static final int STATE_REFESH_FINISH = 5;
    /*加载未结束*/
    public static final int STATE_LOADING_NOT_OVER = 6;


    /*开始刷新*/
    private static final long FLAG_START_REFESH = 0x3;
    /*准备刷新*/
    private static final long FLAG_PREPARE_REFESH = 0x1;

    long mFlag;
    int mStartRefreshHeight; /*开始刷新的高度*/
    int mMaxHeight; /*允许拉动的最大高度*/
    float mDistance;
    int mCurrentState;
    long mFinishAnimationDuration = 800l; /*完成动画持续时间*/

    SimpleLoadingFw mSimpleLoadingFw;


    private boolean isStartLoading() {
        return mSimpleLoadingFw != null && mSimpleLoadingFw.isStartLoading();
    }

    /**
     * 设置完成动画持续时间
     *
     * @param duration 持续事件
     */
    public void setFinishAnimationDuration(long duration) {
        mFinishAnimationDuration = duration;
        mFinishRefeshSpringback.setDuration(duration);
    }

    @Override
    public float onSpring(View springContentView, float dis_y, float correction_distance_y) {
        mDistance += dis_y / 2;
        if (mDistance > mMaxHeight) mDistance = mMaxHeight;
        if (isStartLoading()) {
            setState(STATE_LOADING_NOT_OVER);
            scrollTo(mDistance);
            if (mDistance <= 0) onCancel();
            return mDistance;
        }

        if (isRefeshing()) {
            if (mDistance <= 0) {
                onCancel();
            } else scrollTo(mDistance);
        } else {
            if (mDistance <= 0) {
                onCancel();
            } else if (mDistance >= mStartRefreshHeight) {
                mFlag |= FLAG_PREPARE_REFESH;
                setState(STATE_PREPARE_REFESH);
                scrollTo(mDistance);
            } else {
                mFlag &= ~FLAG_PREPARE_REFESH;
                setState(STATE_DOWN_REFESH);
                scrollTo(mDistance);
            }
        }
        return mDistance;
    }

    void scrollTo(float dis) {
        getView().setTranslationY(dis);
        getParent().getContentView().setTranslationY(dis);
    }

    /*设置开始刷新的高度*/
    public void setStartRefreshHeight(int height) {
        mStartRefreshHeight = height;
    }

    public void setMaxHeight(int height) {
        mMaxHeight = height;
    }

    void setState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            onRefreshStateChange(state);
        }
    }

    /*回调状态*/
    protected abstract void onRefreshStateChange(int state);

    @Override
    public void onCancel() {
        mDistance = 0;
        scrollTo(0);
        release();
    }

    @Override
    public void onFinish() {
        if (isRefeshing()) {  /*刷新中恢复到刷新状态*/
            if (mDistance > mStartRefreshHeight)
                springback(mStartRefeshSpringback);
        } else {
            if ((mFlag & FLAG_PREPARE_REFESH) != 0) { /*准格被刷新状态，执行开始刷新*/
                if (mDistance > mStartRefreshHeight)
                    springback(mStartRefeshSpringback);

                mFlag |= FLAG_START_REFESH;
                setState(STATE_START_REFESH);
                onRefresh();
            } else {
                finishRefresh();
            }
        }
    }

    /*判断是否在刷新中*/
    public boolean isRefeshing() {
        return (mFlag & FLAG_START_REFESH) == FLAG_START_REFESH;
    }

    @Override
    public void onAttachToSpringView(final View contentView, final SpringView springView) {
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        springView.addView(contentView, params);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                int height = contentView.getHeight();
                setStartRefreshHeight(height);
                setMaxHeight(height * 2);
                params.topMargin = -height;
                contentView.setLayoutParams(params);
            }
        });
    }

    /**
     * 绑定加载加载控件。当加载未结束，不允许刷新。
     *
     * @param loading 加载控件
     */
    public void setLoadingFx(SimpleLoadingFw loading) {
        mSimpleLoadingFw = loading;
    }

    /*完成刷新*/
    public void finishRefresh() {
        if (isRefeshing()) {
            mFlag &= ~FLAG_START_REFESH;
            setState(STATE_REFESH_FINISH);
            if (mDistance <= 0) {
                onCancel();
            } else { /*刷新完成*/
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        springback(mFinishRefeshSpringback, mFinishAnimationDuration);
                    }
                }.sendEmptyMessageDelayed(0, 20);
            }
        } else {
            springback(mCancelRefeshSpringback);
        }
    }

    /**
     * 开始刷新
     */
    public void start() {
        if (isRefeshing() || isStartLoading()) return;
        getView().post(new Runnable() {
            @Override
            public void run() {
                if (isRefeshing() || isStartLoading()) return;
                getParent().registerHolder(SimpleRefreshFw.this);
                springback(mAutoRefeshSpringback);
            }
        });
    }


    /**
     * 刷新事件回调
     */
    public abstract void onRefresh();

    /*滚动到正在刷新的位置*/
    ISpringbackExecutor mStartRefeshSpringback = new ISpringbackExecutor() {
        @Override
        public void onSpringback(float rate, long currentPlayTime, boolean animationIsEnde) {
            if (animationIsEnde) {
                mDistance = mStartRefreshHeight;
                scrollTo(mDistance);
            } else {
                double dis = (mDistance - mStartRefreshHeight) * rate + mStartRefreshHeight;
                scrollTo((float) dis);
            }

        }
    };

    /*取消事件*/
    ISpringbackExecutor mCancelRefeshSpringback = new ISpringbackExecutor() {
        @Override
        public void onSpringback(float rate, long currentPlayTime, boolean animationIsEnde) {
            scrollTo(mDistance * rate);
            if (animationIsEnde) {
                setState(STATE_INIT);
                onCancel();
            }
        }
    };

    /*完成事件*/
    FinishRefeshSpringBack mFinishRefeshSpringback = new FinishRefeshSpringBack(mFinishAnimationDuration);

    /*自动刷新事件事件*/
    ISpringbackExecutor mAutoRefeshSpringback = new ISpringbackExecutor() {
        @Override
        public void onSpringback(float rate, long currentPlayTime, boolean animationIsEnde) {
            if (currentPlayTime == 0) {
                mFlag |= FLAG_START_REFESH;
                setState(STATE_START_REFESH);
            }
            float dis = mStartRefreshHeight * (1 - rate);
            mDistance = dis;
            scrollTo(dis);

            if (animationIsEnde) {
                onRefresh();
            }
        }
    };

    /*完成事件动画*/
    private final class FinishRefeshSpringBack implements ISpringbackExecutor {
        float mWaitingProportion;/*回弹效果比例*/
        long mDurationTime; /*动画持续时间*/
        double mOffset = 0;

        public FinishRefeshSpringBack(long duration) {
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
