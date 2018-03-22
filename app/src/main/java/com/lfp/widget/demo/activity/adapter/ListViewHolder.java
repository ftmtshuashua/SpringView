package com.lfp.widget.demo.activity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lfp.widget.demo.R;


/**
 * Created by LiFuPing on 2018/3/17.
 */

public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mTV_Name;

    public ListViewHolder(View itemView) {
        super(itemView);
        mTV_Name = (TextView) itemView.findViewById(R.id.view_Name);
        mTV_Name.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "点击了:" + mTV_Name, Toast.LENGTH_SHORT).show();
    }
}


