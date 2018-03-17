package com.lfp.widget.springview.imp;


import com.lfp.widget.springview.SpringView;
import com.lfp.widget.springview.i.ISpringChild;

/**
 * 底部SpringViewChild
 * Created by LiFuPing on 2017/9/13.
 */

public abstract class ImpSpringChild_Bottom extends ISpringChild {
    public static final int GROUP_ID = 100000;
    public ImpSpringChild_Bottom(){
        setGroupId(GROUP_ID);
    }

    @Override
    public boolean checkHoldSpringView(SpringView.EdgeCheckUtil edgeCheckUtil, SpringView.TrendCheckUtil trendCheckUtil) {
        return edgeCheckUtil.isBottomEdge() && trendCheckUtil.isBottomSpring();
    }
}
