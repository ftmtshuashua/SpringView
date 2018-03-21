package com.lfp.widget.demo.activity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lfp.widget.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiFuPing on 2018/3/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<ListViewHolder> {

    List<String> mData = new ArrayList<>();

    public void setData(List<String> mdata) {
        mData.clear();
        mData.addAll(mdata);
        notifyDataSetChanged();
    }

    public void addData(List<String> mdata) {
        mData.addAll(mdata);
        notifyDataSetChanged();
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_textview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        holder.mTV_Name.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}