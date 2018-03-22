package com.lfp.widget.demo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lfp.widget.demo.R;
import com.lfp.widget.springview.SpringView;

import com.lfp.widget.demo.activity.demo1.ActivityDemo1;
import com.lfp.widget.demo.activity.demo2.ActivityDemo2;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SpringView mSpringView = (SpringView) findViewById(R.id.layout_RootView);
//        mSpringView.enableSpringback();/*可在xml中配置*/

        findViewById(R.id.view_Demo1).setOnClickListener(this);
        findViewById(R.id.view_Demo2).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_Demo1:
                ActivityDemo1.start(this);
                break;
            case R.id.view_Demo2:
                ActivityDemo2.start(this);
                break;
        }
    }
}
