package com.lfp.widget.demo.activity.util;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 延时任务处理器,必须在UI线程内创建这个对象
 */
public abstract class DelayTask {

    private long mDelayTime = 0;

    /**
     * @param delay 延时时间
     */
    public DelayTask(long delay) {
        mDelayTime = delay;
    }

    /**
     * 获得延时时间
     *
     * @return
     */
    public long getDelayTime() {
        return mDelayTime;
    }

    /**
     * 执行这个任务
     */
    public void execute() {
        if (mDelayTime > 0) {
            run();
        } else onFinishDelay();
    }

    public void stop() {
        if (mTimerTask == null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    public boolean isStop() {
        return mTimerTask == null && mTimerTask == null;
    }


    Timer mTimer;
    TimerTask mTimerTask;

    private void run() {
        if (mTimer == null) mTimer = new Timer();
        if (mTimerTask == null) mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        };
        mTimer.schedule(mTimerTask, mDelayTime);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            onFinishDelay();
            stop();
        }
    };

    /**
     * 当设定时间完成的时候调用方法
     */
    public abstract void onFinishDelay();

}