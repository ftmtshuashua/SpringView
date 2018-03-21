package com.lfp.widget.springview.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 负责处理触摸事件信息
 * Created by LiFuPing on 2018/3/10.
 */

public class MotionEventUtil {
    Map<Integer, SingleMotionEvent> mMotionEventArray = new HashMap<>();

    boolean mIsOpenTrajectory;

    public MotionEventUtil() {
    }

    /**
     * 设置是否开启轨迹
     * 请调用{@link #drawTrajectory(Canvas, Paint)} 绘制轨迹
     *
     * @param is 是否开启轨迹检查
     */
    public void setTrajectory(boolean is) {
        mIsOpenTrajectory = is;
    }

    public static String actionToString(int action) {
        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                return "ACTION_POINTER_DOWN";
            case MotionEvent.ACTION_DOWN:
                return "ACTION_DOWN";
            case MotionEvent.ACTION_MOVE:
                return "ACTION_MOVE";
            case MotionEvent.ACTION_UP:
                return "ACTION_UP";
            case MotionEvent.ACTION_POINTER_UP:
                return "ACTION_POINTER_UP";
            case MotionEvent.ACTION_CANCEL:
                return "ACTION_CANCEL";
        }
        return String.valueOf(action);
    }

    /*在 super.dispatchTouchEvent(ev); 之前调用*/
    public void onDispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN: {
                int pointerId = ev.getPointerId(MotionEventCompat.getActionIndex(ev));
                SingleMotionEvent mSingleEvent = getSingleMotionEvent(pointerId);
                mSingleEvent.setPointerId(pointerId);
                int pointerIndex = ev.findPointerIndex(pointerId);
                mSingleEvent.setTouch(ev.getX(pointerIndex), ev.getY(pointerIndex));
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                Iterator<Integer> pointerIdsArray = mMotionEventArray.keySet().iterator();
                while (pointerIdsArray.hasNext()) {
                    int pointerId = pointerIdsArray.next();
                    SingleMotionEvent mSingleEvent = mMotionEventArray.get(pointerId);
                    if (mSingleEvent.isTouch()) {
                        int pointerIndex = ev.findPointerIndex(pointerId);
                        if (pointerIndex == -1) {
                            mSingleEvent.clean();
                        } else {
                            mSingleEvent.setTouch(ev.getX(pointerIndex), ev.getY(pointerIndex));
                        }
                    }
                }
            }
            break;
        }
    }

    /*在 super.dispatchTouchEvent(ev); 之后调用*/
    public void onCleanTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                Iterator<Integer> pointerIdsArray = mMotionEventArray.keySet().iterator();
                while (pointerIdsArray.hasNext()) {
                    int pointerId = pointerIdsArray.next();
                    SingleMotionEvent mSingleEvent = getSingleMotionEvent(pointerId);
                    mSingleEvent.clean();
                }
            }
            break;
            case MotionEvent.ACTION_POINTER_UP: {
                int pointerId = ev.getPointerId(MotionEventCompat.getActionIndex(ev));
                SingleMotionEvent mSingleEvent = getSingleMotionEvent(pointerId);
                mSingleEvent.clean();
            }
            break;
        }

    }

    public SingleMotionEvent getSingleMotionEvent(int pointerId) {
        SingleMotionEvent mEvent = mMotionEventArray.get(pointerId);
        if (mEvent == null) {
            mEvent = new SingleMotionEvent();
            mEvent.setTrajectory(mIsOpenTrajectory);
            mMotionEventArray.put(pointerId, mEvent);
        }
        return mEvent;
    }


    /*获得最大位移对应的事件*/
    public SingleMotionEvent getMaxDistanceEvent() {
        Iterator<Integer> pointerIdsArray = mMotionEventArray.keySet().iterator();
        SingleMotionEvent maxEvent = null;
        while (pointerIdsArray.hasNext()) {
            int pointerId = pointerIdsArray.next();
            SingleMotionEvent event = mMotionEventArray.get(pointerId);
            if (!event.isTouch()) continue;
            if (maxEvent == null || event.getMoveDistance() > maxEvent.getMoveDistance()) {
                maxEvent = event;
            }
        }
        return maxEvent;
    }

    /**
     * 绘制轨迹
     *
     * @param canvas 画布
     * @param paint  画笔
     */
    public void drawTrajectory(Canvas canvas, Paint paint) {
        if (!mIsOpenTrajectory) return;
        Map<Integer, SingleMotionEvent> mdata = mMotionEventArray;
        Iterator<Integer> pointIds = mdata.keySet().iterator();
        while (pointIds.hasNext()) {
            int pointId = pointIds.next();
            MotionEventUtil.SingleMotionEvent event = mdata.get(pointId);
            canvas.drawPath(event.mPath, paint);
        }
    }


    /*单个手指的事件*/
    public static final class SingleMotionEvent {
        public static final int FLAG_TRAJECTORY = 0x1; /*轨迹开关*/
        static final int DEFAULT = -0xff0f;
        int mPointerId;/*手指对应ID*/
        float mTouchDown_X, mTouchDown_Y;/*事件起点坐标*/
        float mOldTouchMove_X, mOldTouchMove_Y;/*上一个事件点坐标*/
        float mTouchMove_X, mTouchMove_Y;/*当前事件点坐标*/

        int mFlag;

        Path mPath;

        SingleMotionEvent() {
            clean();
        }

        /*设置是否开启轨迹*/
        public void setTrajectory(boolean is) {
            if (is) {
                mFlag |= FLAG_TRAJECTORY;
                mPath = new Path();
            } else {
                mFlag &= ~FLAG_TRAJECTORY;
                mPath = null;
            }
        }

        public void setPointerId(int pointerId) {
            this.mPointerId = pointerId;
        }

        public void setTouch(float x, float y) {
            if (!isTouch()) {
                mTouchDown_X = x;
                mTouchDown_Y = y;
                if (mPath != null) mPath.moveTo(x, y);
            }
            mOldTouchMove_X = mTouchMove_X;
            mOldTouchMove_Y = mTouchMove_Y;

            mTouchMove_X = x;
            mTouchMove_Y = y;

            if (mPath != null) mPath.lineTo(x, y);
        }

        public int getPointerId() {
            return mPointerId;
        }

        /*标识手指是否放在屏幕之上*/
        public boolean isTouch() {
            return mTouchDown_X != DEFAULT && mTouchDown_Y != DEFAULT;
        }

        /*标识手指是否移动*/
        public boolean isMove() {
            boolean isMove = mOldTouchMove_X != DEFAULT && mOldTouchMove_Y != DEFAULT;
            if (isMove)
                isMove = mOldTouchMove_X != mTouchMove_X || mOldTouchMove_Y != mTouchMove_Y;
            return isMove;
        }

        /*获得位移距离*/
        public double getMoveDistance() {
            if (isMove()) {
                float distance_x = getMoveDistanceX();
                float distance_y = getMoveDistanceY();
                return Math.sqrt(distance_x * distance_x + distance_y * distance_y);
            }
            return 0;
        }

        /*获得X轴上的位移*/
        public float getMoveDistanceX() {
            if (isMove()) return mTouchMove_X - mOldTouchMove_X;
            return 0;
        }

        /*获得Y轴上的位移*/
        public float getMoveDistanceY() {
            if (isMove()) return mTouchMove_Y - mOldTouchMove_Y;
            return 0;
        }


        /*获得位移距离*/
        public double getTotalMoveDistance() {
            if (isMove()) {
                float distance_x = getTotalMoveDistanceX();
                float distance_y = getTotalMoveDistanceY();
                return Math.sqrt(distance_x * distance_x + distance_y * distance_y);
            }
            return 0;
        }

        /*获得X轴上的位移*/
        public float getTotalMoveDistanceX() {
            if (isMove()) return mTouchMove_X - mTouchDown_X;
            return 0;
        }

        /*获得Y轴上的位移*/
        public float getTotalMoveDistanceY() {
            if (isMove()) return mTouchMove_Y - mTouchDown_Y;
            return 0;
        }


        public float getTouchX() {
            return mTouchMove_X;
        }

        public float getTouchY() {
            return mTouchMove_Y;
        }

        public float getOldTouchX() {
            return mOldTouchMove_X;
        }

        public float getOldTouchY() {
            return mOldTouchMove_Y;
        }

        public float getDownX() {
            return mTouchDown_X;
        }

        public float getDownY() {
            return mTouchDown_Y;
        }

        /*清理事件信息*/
        public void clean() {
            mPointerId = DEFAULT;
            mTouchDown_X = mTouchDown_Y = DEFAULT;
            mOldTouchMove_X = mOldTouchMove_Y = DEFAULT;
            mTouchMove_X = mTouchMove_Y = DEFAULT;

            if (mPath != null) mPath.reset();
        }
    }

}
