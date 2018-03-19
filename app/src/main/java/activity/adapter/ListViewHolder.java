package activity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lfp.widget.springview.R;

/**
 * Created by Administrator on 2018/3/17.
 */

public class ListViewHolder  extends RecyclerView.ViewHolder {
    public TextView mTV_Name;

    public ListViewHolder(View itemView) {
        super(itemView);
        mTV_Name = (TextView) itemView.findViewById(R.id.view_Name);
    }


}


