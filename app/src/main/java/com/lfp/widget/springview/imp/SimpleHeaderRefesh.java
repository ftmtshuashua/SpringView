package com.lfp.widget.springview.imp;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.lfp.widget.springview.SpringView;
import com.lfp.widget.springview.i.ISpringbackExecutor;

import java.text.MessageFormat;

/**
 * 简单的刷新
 * Created by Administrator on 2018/3/19.
 */

public abstract class SimpleHeaderRefesh extends ImpSpringChild_Top implements ISpringbackExecutor {

    /*准备刷新*/
    public static final int STATE_PREPARE_REFESH = 1;
    /*开始刷新*/
    public static final int STATE_START_REFESH = 2;
    /*初始化*/
    public static final int STATE_INIT = 3;

    /*准备刷新*/
    private static final long FLAG_PREPARE_REFESH = 0x1;
    /*开始刷新*/
    private static final long FLAG_START_REFESH = 0x1 << 1;

    long mFlag;
    int mStartRefreshHeight; /*开始刷新的高度*/
    float mDistance;
    int mCurrentState;

    @Override
    public float onSpring(View springContentView, float dis_y, float correction_distance_y) {
        mDistance += dis_y;
        if (mDistance > mStartRefreshHeight * 2) mDistance = mStartRefreshHeight * 2;

        if (isRefeshing()) {
            if (mDistance <= 0) {
                mDistance = 0;
                scrollTo(0);
                onCancel();
            } else scrollTo(mDistance);
        } else {
            if (mDistance >= mStartRefreshHeight) {
                mFlag |= FLAG_PREPARE_REFESH;
                setState(STATE_PREPARE_REFESH);
            } else {
                mFlag &= ~FLAG_PREPARE_REFESH;
                setState(STATE_INIT);
            }
            scrollTo(mDistance);
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

    void setState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            onRefreshStart(state);
        }
    }

    /*回调状态*/
    protected abstract void onRefreshStart(int state);

    @Override
    public void onCancel() {
        mDistance = 0;
        scrollTo(0);
        onCancel();
    }

    @Override
    public void onFinish() {
        if ((mFlag & FLAG_PREPARE_REFESH) != 0) {
            mFlag |= FLAG_START_REFESH;
            if (mDistance != mStartRefreshHeight)
                getParent().starSpringback(this, (long) (250 * (mDistance - mStartRefreshHeight) / mDistance));

            setState(STATE_START_REFESH);
            onRefresh();
        } else {/*还原状态*/
//            springback(this ,  );
        }
    }

    /*判断是否在刷新中*/
    public boolean isRefeshing() {
        return (mFlag & FLAG_START_REFESH) != 0;
    }

    @Override
    protected void onAttachToSpringView(final View contentView, final SpringView springView, final FrameLayout.LayoutParams params) {
        springView.addView(contentView,params );
        contentView.post(new Runnable() {
            @Override
            public void run() {
                int height = contentView.getHeight();
                Log.e("", MessageFormat.format("View高度：{0}" ,height ));
                setStartRefreshHeight(height);
                params.topMargin = -height;
            }
        });
    }


    @Override
    public void onSpringback(float rate) {
        if (isRefeshing()) {
            scrollTo((mDistance - mStartRefreshHeight) * rate + mDistance);
            if (rate == 0) mDistance = mStartRefreshHeight;
        } else {
            scrollTo(mDistance * rate);
            if (rate == 0) onCancel();
        }
    }

    /*完成刷新*/
    public void finishRefresh() {
        if (getParent().getContentView().getTranslationY() == 0) {
            onCancel();
        } else {
            springback(this);
        }
    }

    /**
     * 刷新事件回调
     */
    public abstract void onRefresh();

}
