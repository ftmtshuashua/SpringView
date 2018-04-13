package com.lfp.widget.demo.activity.demo3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lfp.widget.demo.R;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by LiFuPing on 2018/4/13.
 */
public class ActivityDemo3 extends AppCompatActivity implements View.OnClickListener {

    public static final void start(Context c){
        Intent intent  = new Intent(c,ActivityDemo3.class);
        c.startActivity(intent);
    }

    FragmentControl mFragmentControl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo3);
        findViewById(R.id.view_Tab1).setOnClickListener(this);
        findViewById(R.id.view_Tab2).setOnClickListener(this);

        mFragmentControl = new FragmentControl(this, R.id.view_Content) {
            @Override
            public Fragment initFragment(String tag) {
                switch (Integer.parseInt(tag)) {
                    case R.id.view_Tab1:
                        return FragmentList.newInstance("Page_1111",buildData("Tab_1"));
                    case R.id.view_Tab2:
                        return FragmentList.newInstance("Page_2222",buildData("Page_222222"));
                }
                return null;
            }
        };

    }


    @Override
    public void onClick(View v) {
        mFragmentControl.change(String.valueOf(v.getId()));
    }

    public ArrayList<String> buildData(String str) {
        ArrayList<String> arrays = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            arrays.add(MessageFormat.format("{0}_{1}", str, i));
        }
        return arrays;
    }
}
