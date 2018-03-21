package com.lfp.widget.springview.imp;

import android.view.View;
import android.widget.FrameLayout;

import com.lfp.widget.springview.SpringView;
import com.lfp.widget.springview.i.ISpringbackExecutor;

/**
 * 一个简单的刷新框架
 * Created by LiFuPing on 2018/3/19.
 */

public abstract class SimpleRefeshFw extends ImpSpringChild_Top {

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

    SimpleLoadingFw mSimpleLoadingFw;

    @Override
    public float onSpring(View springContentView, float dis_y, float correction_distance_y) {
        mDistance += dis_y / 2;
        if (mDistance > mMaxHeight) mDistance = mMaxHeight;
        if (mSimpleLoadingFw != null && mSimpleLoadingFw.isStartLoading()) {
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
        if (isRefeshing()) {
            if (mDistance > mStartRefreshHeight)
                springback(mStartRefeshSpringback);
        } else {
            if ((mFlag & FLAG_PREPARE_REFESH) != 0) {
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
    protected void onAttachToSpringView(final View contentView, final SpringView springView, final FrameLayout.LayoutParams params) {
        springView.addView(contentView, params);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                int height = contentView.getHeight();
                setStartRefreshHeight(height);
                setMaxHeight(height * 2);
                params.topMargin = -height;
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
                springback(mFinishRefeshSpringback, 500);
            }
        } else springback(mCancelRefeshSpringback);
    }

    /**
     * 刷新事件回调
     */
    public abstract void onRefresh();

    /*滚动到正在刷新单位置*/
    ISpringbackExecutor mStartRefeshSpringback = new ISpringbackExecutor() {
        @Override
        public void onSpringback(float rate) {
            if (rate == 0) mDistance = mStartRefreshHeight;
            float dis = (mDistance - mStartRefreshHeight) * rate + mStartRefreshHeight;
            scrollTo(dis);

        }
    };

    /*取消事件*/
    ISpringbackExecutor mCancelRefeshSpringback = new ISpringbackExecutor() {
        @Override
        public void onSpringback(float rate) {
            scrollTo(mDistance * rate);
            if (rate == 0) {
                setState(STATE_INIT);
                onCancel();
            }
        }
    };

    /*完成事件*/
    ISpringbackExecutor mFinishRefeshSpringback = new ISpringbackExecutor() {
        @Override
        public void onSpringback(float rate) {
            if (rate < 0.5f) scrollTo(mDistance * rate * 2);

            if (rate == 0) {
                setState(STATE_INIT);
                onCancel();
            }

        }
    };


}
