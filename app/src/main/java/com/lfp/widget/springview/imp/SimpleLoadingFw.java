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

public abstract class SimpleLoadingFw extends ImpSpringChild_Bottom implements ISpringbackExecutor {

    private static final long FLAG_START_LOADING = 0x1; /*开始加载*/

    /*准备加载*/
    public static final int STATE_PREPARE_LOADING = 1;
    /*开始加载*/
    public static final int STATE_START_LOADING = 2;
    /*初始化*/
    public static final int STATE_INIT = 3;
    /*加载完成*/
    public static final int STATE_LOADING_FINISH = 5;

    int mStartLoadingHeight;/*开始加载的高度*/
    float mDistance;
    long mFlag;
    int mCurrentState;


    @Override
    public float onSpring(View springContentView, float dis_y, float correction_distance_y) {
        mDistance += dis_y / 2;
        if (mDistance < -mStartLoadingHeight) mDistance = -mStartLoadingHeight;

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

//        FrameLayout.LayoutParams mParams = (FrameLayout.LayoutParams) getParent().getContentView().getLayoutParams();
//        mParams.bottomMargin = -(int) dis;
//        getParent().getContentView().requestLayout();
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
        clean();
    }

    @Override
    public void onFinish() {
        if (!isStartLoading()) finishLoading();
    }


    protected void setStartLoadingHeight(int height) {
        mStartLoadingHeight = height;
    }

    @Override
    protected void onAttachToSpringView(final View contentView, final SpringView springView, final FrameLayout.LayoutParams params) {
        springView.addView(contentView, params);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                int height = contentView.getHeight();
                setStartLoadingHeight(height);

                params.gravity = Gravity.BOTTOM;
                params.bottomMargin = -height;
            }
        });
    }

    @Override
    public void onSpringback(float rate) {
        scrollTo(mDistance * rate);
        if (rate == 0) {
            setState(STATE_INIT);
            onCancel();
        }
    }

    /*完成加载*/
    public void finishLoading() {
        if (isStartLoading()) {
            mFlag &= ~FLAG_START_LOADING;
            setState(STATE_LOADING_FINISH);
            springback(this);
        } else springback(this);
    }

    public abstract void onLoading();
}
