package com.lfp.widget.demo.activity.demo2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lfp.widget.demo.R;
import com.lfp.widget.springview.SpringView;
import com.lfp.widget.springview.imp.ImpSpringChild_Top;

/**
 * 简单头部布局
 * Created by LiFuPing on 2018/3/17.
 */

public abstract class CustomHeaderLayout extends ImpSpringChild_Top implements View.OnClickListener {
    int mDistance; /*当前位置*/

    @Override
    public float onSpring(View springContentView, float dis_y, float correction_distance_y) {
        mDistance += dis_y;
        if (mDistance > mMaxHeight) mDistance = mMaxHeight;
        if (mDistance <= 0) {
            onCancel();
        } else {
            scroll(mDistance);
        }
        return mDistance;
    }

    @Override
    public void onCancel() {
        scroll(0);
        release();
    }

    @Override
    public void onFinish() {

    }

    void scroll(int dis) {
        mDistance = dis;
        getView().setTranslationY(dis);
        getParent().getContentView().setTranslationY(dis);
    }

    TextView mTV_Name;
    ImageView mIV_Head;
    View mLT_Content;

    int mMaxHeight;

    @Override
    public View onCreateView(Context context, SpringView springView) {
        View mRootView = LayoutInflater.from(context).inflate(R.layout.layout_custom_header_layout, springView, false);
        mTV_Name = (TextView) mRootView.findViewById(R.id.view_Name);
        mIV_Head = (ImageView) mRootView.findViewById(R.id.view_Head);
        mLT_Content = mRootView.findViewById(R.id.layout_Content);
        mIV_Head.setOnClickListener(this);

        return mRootView;
    }


    @Override
    public void onAttachToSpringView(View contentView, SpringView springView ) {
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        springView.addView(contentView, params);
        springView.registerHolder(this);
        springView.post(new Runnable() {
            @Override
            public void run() {
                mMaxHeight = mLT_Content.getHeight();
                mDistance = mMaxHeight;
                params.topMargin = -mMaxHeight;
                scroll(mMaxHeight);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_Head:
                onClickHead();
                break;
        }
    }

    /**
     * 头像点击回调
     */
    public abstract void onClickHead();
}
