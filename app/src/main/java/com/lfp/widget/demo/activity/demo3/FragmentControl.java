package com.lfp.widget.demo.activity.demo3;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;


/**
 * Fragment控制器
 * Created by LiFuPing on 2017/9/7.
 */
public abstract class FragmentControl {

    FragmentManager mFragmentManager;
    int mFragmentContent;
    Fragment mCurrentFragment;

    OnFragmentChange mOnFragmentChange;
    OnFragmentInit mOnFragmentInit;

    public FragmentControl(FragmentActivity activity, @IdRes int content) {
        mFragmentManager = activity.getSupportFragmentManager();
        mFragmentContent = content;
    }

    public FragmentControl(Fragment activity, @IdRes int content) {
        mFragmentManager = activity.getChildFragmentManager();
        mFragmentContent = content;
    }


    public void setOnFragmentChange(OnFragmentChange l) {
        mOnFragmentChange = l;
    }

    public void setOnFragmentInit(OnFragmentInit l) {
        mOnFragmentInit = l;
    }

    /**
     * 初始化Fragment
     */
    public abstract Fragment initFragment(String tag);

    /**
     * 切换Fragment
     */
    public void onChangeFragment(Fragment fragment, String tag) {
    }

    /**
     * 切换显示的Fragment
     */
    public void change(String tag) {
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        List<Fragment> mData = mFragmentManager.getFragments(); //Activity中添加的Fragment，就算Activity被系统回收了，Fragment也不会被回收
        if (mData != null) {
            for (Fragment f : mData)
                mFragmentTransaction.hide(f);
        }

        boolean isAddActivity = false;
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = initFragment(tag);
            if (mOnFragmentInit != null) {
                mOnFragmentInit.onInit(fragment, tag);
            }
            isAddActivity = true;
        }
        if (fragment == null) return;
        if (isAddActivity) {
            mFragmentTransaction.add(mFragmentContent, fragment, tag);
        }

        if (mCurrentFragment != null) mFragmentTransaction.hide(mCurrentFragment);
        mFragmentTransaction.show(fragment);/*切换Fragment */
        mCurrentFragment = fragment;
        if (mCurrentFragment instanceof OnFragmentControlLisenter) {
            OnFragmentControlLisenter fragm = (OnFragmentControlLisenter) mCurrentFragment;
            if (fragm.isFragmentCreated()) fragm.onShow();
        }
        mFragmentTransaction.commit();

        onChangeFragment(mCurrentFragment, tag);
        if (mOnFragmentChange != null) {
            mOnFragmentChange.onChange(mCurrentFragment, tag);
        }
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }


    /*当页面切换的时候回调方法*/
    public interface OnFragmentChange {
        void onChange(Fragment fragment, String tag);
    }

    public interface OnFragmentInit {
        void onInit(Fragment fragment, String tag);
    }

    /**
     * Fragment控制器统计控制器,需要Fragment实现
     */
    public interface OnFragmentControlLisenter {
        /**
         * 当展开这个fragment的时候调用
         */
        void onShow();

        /**
         * 判断这个Fragment是否创建完成 ,只有当他返回true的时候才会回调onShow()方法
         */
        boolean isFragmentCreated();
    }

}
