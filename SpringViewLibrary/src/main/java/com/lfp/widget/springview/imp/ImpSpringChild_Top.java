package com.lfp.widget.springview.imp;


import com.lfp.widget.springview.SpringView;
import com.lfp.widget.springview.i.ISpringChild;

/**
 * 顶部SpringViewChild
 * Created by LiFuPing on 2017/9/13.
 */

public abstract class ImpSpringChild_Top extends ISpringChild {
    public static final int GROUP_ID = 100001;

    public ImpSpringChild_Top() {
        setGroupId(GROUP_ID);
    }

    @Override
    public boolean checkHoldSpringView(SpringView.EdgeCheckUtil edgeCheckUtil, SpringView.TrendCheckUtil trendCheckUtil) {
        return edgeCheckUtil.isTopEdge() && trendCheckUtil.isTopSpring();
    }

}
