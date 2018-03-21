package com.lfp.widget.demo.activity.demo1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lfp.widget.demo.R;
import com.lfp.widget.demo.activity.adapter.RecyclerViewAdapter;
import com.lfp.widget.demo.activity.util.DelayTask;
import com.lfp.widget.springview.SpringView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiFuPing on 2018/3/17.
 */

public class ActivityDemo1 extends AppCompatActivity {

    public static final void start(Context c) {
        Intent intent = new Intent(c, ActivityDemo1.class);
        c.startActivity(intent);
    }

    RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_recyclerview);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.view_RecycleListView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter = new RecyclerViewAdapter());

        SpringView mSpringView = (SpringView) findViewById(R.id.view_SpringView);
        mSpringView.setSpringChild(mRefresh, mLoading);
        mLoading.setRefeshFx(mRefresh);
        mRefresh.setLoadingFx(mLoading);


        List<String> buildArray = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            buildArray.add(MessageFormat.format("原始数据 - {0,number,0}", i));
        }
        mAdapter.setData(buildArray);

        mRefresh.start(); /*自动刷新*/
    }

    SimpleHeader mRefresh = new SimpleHeader() {
        @Override
        public void onRefresh() {
            new DelayTask(2000) {
                public void onFinishDelay() {
                    List<String> buildArray = new ArrayList<>();
                    int mCount = (int) (Math.random() * 20 + 1);
                    for (int i = 0; i < mCount; i++) {
                        buildArray.add(MessageFormat.format("刷新数据 - {0,number,0}", i));
                    }
                    mAdapter.setData(buildArray);
                    finishRefresh();
                }
            }.execute();
        }

    };

    SimpleBottom mLoading = new SimpleBottom() {

        @Override
        public void onLoading() {
            new DelayTask(2000) {

                @Override
                public void onFinishDelay() {
                    List<String> buildArray = new ArrayList<>();
                    int mCount = (int) (Math.random() * 5 + 1);
                    for (int i = 0; i < mCount; i++) {
                        buildArray.add(MessageFormat.format("加载数据 - {0,number,0}", i));
                    }
                    mAdapter.addData(buildArray);
                    finishLoading();
                }
            }.execute();
        }
    };


}
