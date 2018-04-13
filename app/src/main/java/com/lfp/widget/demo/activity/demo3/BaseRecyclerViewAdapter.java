package com.lfp.widget.demo.activity.demo3;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiFuPing on 2017/9/14.
 */
public abstract class BaseRecyclerViewAdapter<D> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.ViewHodler<D>> {
    Context mContext;

    @Deprecated
    public BaseRecyclerViewAdapter(Context c) {
        mContext = c;
    }

    public BaseRecyclerViewAdapter() {

    }

    @Deprecated
    public Context getContext() {
        return mContext;
    }


    List<D> mData = new ArrayList<>();

    /*重置数据并更新 UI*/
    public void setDataAndUpdata(List<? extends D> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
//        notifyItemRangeChanged(0, getItemCount());
        notifyDataSetChanged();
        onDataChange();
    }

    /*添加数据并更新 UI*/
    public void addDataAndUpdata(List<D> data) {
        int count = getItemCount();
        mData.addAll(data);
        notifyItemRangeChanged(count, getItemCount());
//        notifyDataSetChanged();
        onDataChange();
    }

    /*添加数据  需要自己更新UI*/
    public void addData(List<D> data) {
        mData.addAll(data);
        onDataChange();
    }

    /*添加数据  需要自己更新UI*/
    public void addData(D data) {
        mData.add(data);
        onDataChange();
    }

    /*获得Adapter附带的数据*/
    public List<D> getData() {
        return mData;
    }

    /*移除项*/
    public void removeDataAndUpdata(int postion) {
        mData.remove(postion);
        notifyDataSetChanged();
        onDataChange();
    }

    @Override
    public void onBindViewHolder(ViewHodler<D> holder, int position) {
        holder.setPostion(position);
        holder.setData(getItemData(position));
    }

    /*获得下标对应位置数据*/
    public D getItemData(int postion) {
        return mData.get(postion);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    /*View Hodler*/
    public static abstract class ViewHodler<D> extends RecyclerView.ViewHolder {
        D mSaveData;
        int mPosition;
        Context mContext;

        public ViewHodler(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
        }

        public Context getContext() {
            return mContext;
        }


        /*设置Data*/
        public void setData(D data) {
            mSaveData = data;
            updateUI(mSaveData);
        }

        public void setPostion(int postion) {
            mPosition = postion;
        }

        public D getSaveData() {
            return mSaveData;
        }

        public int getPostion() {
            return mPosition;
        }

        /**
         * 更新UI
         */
        public abstract void updateUI(D data);


    }

    protected void onDataChange() {
    }

}
