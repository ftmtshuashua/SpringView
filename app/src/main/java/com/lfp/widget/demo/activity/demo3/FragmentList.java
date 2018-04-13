package com.lfp.widget.demo.activity.demo3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lfp.widget.demo.R;
import com.lfp.widget.demo.activity.demo1.SimpleHeader;
import com.lfp.widget.demo.activity.util.DelayTask;
import com.lfp.widget.springview.SpringView;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by LiFuPing on 2018/4/13.
 */
public class FragmentList extends Fragment {


    void log(String log) {
        LogUtil.getLogger().e(MessageFormat.format("{0}__{1}", mTag, log));
    }

    public static final Fragment newInstance(String tag, ArrayList<String> arrays) {
        FragmentList fragment = new FragmentList();
        Bundle bundle = new Bundle();
        bundle.putString("Taag", tag);
        bundle.putStringArrayList("Data", arrays);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.activity_demo3_fragment, null);
    }

    ListAdapter mAdapter;
    String mTag;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTag = getArguments().getString("Taag");
        ArrayList<String> arrays = getArguments().getStringArrayList("Data");

        log("onViewCreated");


        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.view_RecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter = new ListAdapter());

        mAdapter.setDataAndUpdata(arrays);

        SpringView springView = (SpringView) view.findViewById(R.id.view_SpringView);
        springView.setSpringChild(mHeader);
        mHeader.start();
    }

    SimpleHeader mHeader = new SimpleHeader() {
        @Override
        public void onRefresh() {
            new DelayTask(1000) {

                @Override
                public void onFinishDelay() {
                    finishRefresh();
                }
            }.execute();
        }
    };


    private static final class ListAdapter extends BaseRecyclerViewAdapter<String> {

        @Override
        public ViewHodler<String> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ListViewHodler(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_demo3_fragment_item, parent, false));
        }
    }

    private static final class ListViewHodler extends BaseRecyclerViewAdapter.ViewHodler<String> {

        TextView Info;

        public ListViewHodler(View itemView) {
            super(itemView);
            Info = (TextView) itemView.findViewById(R.id.view_Info);
        }

        @Override
        public void updateUI(String data) {
            Info.setText(data);
        }
    }
}
