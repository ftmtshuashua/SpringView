package activity.demo1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lfp.widget.springview.R;
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
    protected void onRefreshStart(int state) {
        switch (state) {
            case SimpleRefeshFw.STATE_INIT:
                mTV_Info.setText("下拉刷新");
                mProgressBar.setVisibility(View.GONE);
                break;
            case SimpleRefeshFw.STATE_PREPARE_REFESH:
                mTV_Info.setText("松开刷新");
                mProgressBar.setVisibility(View.GONE);
                break;
            case SimpleRefeshFw.STATE_START_REFESH:
                mTV_Info.setText("正在刷新...");
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case SimpleRefeshFw.STATE_REFESH_FINISH:
                mTV_Info.setText("刷新完成");
                mProgressBar.setVisibility(View.GONE);
                break;
        }
    }

}
