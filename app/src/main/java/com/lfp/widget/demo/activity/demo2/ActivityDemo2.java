package com.lfp.widget.demo.activity.demo2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.lfp.widget.demo.R;
import com.lfp.widget.springview.SpringView;
import com.lfp.widget.demo.activity.adapter.RecyclerViewAdapter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiFuPing on 2018/3/17.
 */

public class ActivityDemo2 extends AppCompatActivity {

    public static final void start(Context c) {
        Intent intent = new Intent(c, ActivityDemo2.class);
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
        mSpringView.setSpringChild(mCustomHeaderLayout);

        List<String> buildArray = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            buildArray.add(MessageFormat.format("原始数据 - {0,number,0}", i));
        }
        mAdapter.setData(buildArray);

    }

    CustomHeaderLayout mCustomHeaderLayout = new CustomHeaderLayout() {
        @Override
        public void onClickHead() {
            Toast.makeText(ActivityDemo2.this, "点击了头像", Toast.LENGTH_SHORT).show();
        }
    };

}
