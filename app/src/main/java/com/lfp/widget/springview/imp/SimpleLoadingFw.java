package com.lfp.widget.springview.imp;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.lfp.widget.springview.SpringView;

/**
 * 一个简单的加载框架
 * Created by LiFuPing on 2018/3/20.
 */

public abstract class SimpleLoadingFw extends ImpSpringChild_Bottom {

    float mDistance;

    @Override
    public float onSpring(View springContentView, float dis_y, float correction_distance_y) {
        mDistance += dis_y;
        scrollTo(mDistance);
        return mDistance;
    }


    void scrollTo(float dis) {
        getView().setTranslationY(dis);
        getParent().getContentView().setTranslationY(dis);
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    protected void onAttachToSpringView(final View contentView, final SpringView springView, final FrameLayout.LayoutParams params) {
        springView.addView(contentView, params);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                int height = contentView.getHeight();
                params.gravity = Gravity.BOTTOM;
                params.bottomMargin = -height;
            }
        });

    }

    public abstract void onLoading();
}
