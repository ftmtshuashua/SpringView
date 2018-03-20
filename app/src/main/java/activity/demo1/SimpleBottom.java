package activity.demo1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lfp.widget.springview.R;
import com.lfp.widget.springview.SpringView;
import com.lfp.widget.springview.imp.SimpleLoadingFw;

/**
 * 自定义底部加载
 * Created by LiFuPing on 2018/3/20.
 */

public abstract class SimpleBottom extends SimpleLoadingFw {
    TextView mTV_Info;
    ProgressBar mProgressBar;


    @Override
    public View onCreateView(Context context, SpringView springView) {
        View mRootView = LayoutInflater.from(context).inflate(R.layout.layout_simple_header_refresh, springView, false);
        mTV_Info = (TextView) mRootView.findViewById(R.id.view_Info);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.view_ProgressBar);
        return mRootView;
    }

}
