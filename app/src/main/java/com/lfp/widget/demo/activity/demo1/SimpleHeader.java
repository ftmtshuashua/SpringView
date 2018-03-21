package com.lfp.widget.demo.activity.demo1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lfp.widget.demo.R;
import com.lfp.widget.springview.SpringView;
import com.lfp.widget.springview.imp.SimpleRefeshFw;

/**
 * 自定义简单头部刷新
 * Created by LiFuPing on 2018/3/17.
 */
public abstract class SimpleHeader extends SimpleRefeshFw {

    TextView mTV_Info;
    ProgressBar mProgressBar;


    @Override
    public View onCreateView(Context context, SpringView springView) {
        View mRootView = LayoutInflater.from(context).inflate(R.layout.layout_simple_header_refresh, springView, false);
        mTV_Info = (TextView) mRootView.findViewById(R.id.view_Info);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.view_ProgressBar);
        return mRootView;
    }


    @Override
    protected void onRefreshStateChange(int state) {
        mProgressBar.setVisibility(state == SimpleRefeshFw.STATE_START_REFESH ? View.VISIBLE : View.GONE);
        switch (state) {
            case SimpleRefeshFw.STATE_LOADING_NOT_OVER:
                mTV_Info.setText("加载未完成,请稍后...");
                break;
            case SimpleRefeshFw.STATE_DOWN_REFESH:
            case SimpleRefeshFw.STATE_INIT:
                mTV_Info.setText("下拉刷新");
                break;
            case SimpleRefeshFw.STATE_PREPARE_REFESH:
                mTV_Info.setText("松开刷新");
                break;
            case SimpleRefeshFw.STATE_START_REFESH:
                mTV_Info.setText("正在刷新...");
                break;
            case SimpleRefeshFw.STATE_REFESH_FINISH:
                mTV_Info.setText("刷新完成");
                break;
        }
    }

}
