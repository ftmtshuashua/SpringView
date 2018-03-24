package com.lfp.widget.demo.activity.demo1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lfp.widget.demo.R;
import com.lfp.widget.springview.SpringView;
import com.lfp.widget.springview.imp.SimpleLoadingFw;

/**
 * 自定义底部加载
 * Created by LiFuPing on 2018/3/20.
 */
public abstract class SimpleBottom extends SimpleLoadingFw {
    TextView mTV_Info;

    @Override
    public View onCreateView(Context context, SpringView springView) {
        View mRootView = LayoutInflater.from(context).inflate(R.layout.layout_simple_bottom_loading, springView, false);
        mTV_Info = (TextView) mRootView.findViewById(R.id.view_Info);
        return mRootView;
    }

    @Override
    protected void onLoadingStateChange(int state) {
        switch (state) {
            case STATE_All_DATA_IS_LOADED:
                mTV_Info.setText("暂无更多数据");
                break;
            case STATE_REFRESH_NOT_OVER:
                mTV_Info.setText("刷新未完成,请稍后...");
                break;
            case STATE_INIT:
                mTV_Info.setText("上拉加载");
                break;
            case STATE_LOADING_FINISH:
                mTV_Info.setText("加载完成");
                break;
            case STATE_PREPARE_LOADING:
                mTV_Info.setText("上拉加载");
                break;
            case STATE_START_LOADING:
                mTV_Info.setText("正在加载...");
                break;
        }
    }
}
