package com.lfp.widget.springview.activity.demo1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lfp.widget.springview.R;
import com.lfp.widget.springview.SpringView;
import com.lfp.widget.springview.i.ISpringbackExecutor;
import com.lfp.widget.springview.imp.ImpSpringChild_Top;

/**
 * 自定义简单头部刷新
 * Created by Administrator on 2018/3/17.
 */

public abstract class SimpleHeaderRefresh extends ImpSpringChild_Top implements ISpringbackExecutor {

    TextView mTV_Info;
    ProgressBar mProgressBar;
    float mCurrentDistance;

    @Override
    public void onSpring(float dis_y, float distance_y) {
        mCurrentDistance += dis_y;
        if (mCurrentDistance > getContentView().getHeight()) {
            mTV_Info.setText("松开刷新");
        } else {
            mTV_Info.setText("下拉刷新");
        }
        scrollTo(mCurrentDistance);
    }

    private void scrollTo(float y) {
        mCurrentDistance = y;
        getContentView().setTranslationY(y - getContentView().getHeight());
        getParent().getContentView().setTranslationY(y);
    }

    @Override
    public void onCancel() {
        mProgressBar.setVisibility(View.INVISIBLE);
        scrollTo(0);

        clean();/*所有动作执行完成之后必须清理*/
    }

    @Override
    public void onFinish() {
        if (mCurrentDistance >= getContentView().getHeight()) { /**/
            mProgressBar.setVisibility(View.VISIBLE);
            springback(mPlaceExecutor);
            onRefrsh();
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (getParent().getContentView().getTranslationY() == 0) {
                onCancel();
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
                springback(this);
            }
        }
    }

    /*归位*/
    ISpringbackExecutor mPlaceExecutor = new ISpringbackExecutor() {
        @Override
        public void onSpringback(float rate) {
            int height = getContentView().getHeight();
            scrollTo((mCurrentDistance - height) * rate + height);
        }
    };

    @Override
    public View onCreateView(Context context, SpringView springView) {
        View mRootView = LayoutInflater.from(context).inflate(R.layout.layout_simple_header_refresh, springView, false);
        mTV_Info = (TextView) mRootView.findViewById(R.id.view_Info);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.view_ProgressBar);
        return mRootView;
    }

    @Override
    public void onSpringback(float rate) {
        if (getParent().getContentView().getTranslationY() > 0) {
            getContentView().setTranslationY(mCurrentDistance * rate - getContentView().getHeight());
            getParent().getContentView().setTranslationY(mCurrentDistance * rate);
            if (rate == 0) onCancel();
        }
    }

    /*完成刷新*/
    public void finishRefrsh() {
        if (getParent().getContentView().getTranslationY() == 0) {
            onCancel();
        } else {
            springback(this);
        }
    }

    /**
     * 刷新事件回调
     */
    public abstract void onRefrsh();

}
