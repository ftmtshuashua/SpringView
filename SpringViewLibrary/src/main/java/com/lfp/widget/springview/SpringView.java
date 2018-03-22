
package com.lfp.widget.springview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.lfp.widget.springview.i.ISpringChild;
import com.lfp.widget.springview.i.ISpringHolders;
import com.lfp.widget.springview.i.ISpringbackExecutor;
import com.lfp.widget.springview.imp.ImpSpringChild_Bottom;
import com.lfp.widget.springview.imp.ImpSpringChild_Top;
import com.lfp.widget.springview.util.MotionEventUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 弹动View
 * Created by LiFuPing on 2018/3/9.
 */
public class SpringView extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {
    /**
     * 开启回弹效果 - 给SpringView设置SpringChild会覆盖该效果
     */
    public static final long FLAG_OPEN_SPRINGBACK = 0x3;
    /**
     * 开启顶部回弹效果 - 当SpringView中午SpringChild的时候生效
     */
    public static final long FLAG_OPEN_SPRINGBACK_TOP = 0x1;
    /**
     * 开启底部回弹效果 - 当SpringView中午SpringChild的时候生效
     */
    public static final long FLAG_OPEN_SPRINGBACK_BOTTOM = 0x2;

    /**
     * 重新开始ContentView的滚动事件
     */
    private static final long FLAG_TOUCHE_EVENT_START_ALL_OVER_AGAIN = 0x10;
    /**
     * 表示滚动中状态
     */
    private static final long FLAG_IS_ROLLING = 0x40;

    /**
     * 暂停滑动事件 , 使得SpringView无法开始弹动，但是可以点击
     */
    public static final long FLAG_PAUSE_SCROLL_EVENT = 0x20;


    long mSpringFlag;
    int mTouchSlop;/*被认为是滑动的最小位移像素*/

    MotionEventUtil mMotionEventUtil;/*手势检查*/
    EdgeCheckUtil mEdgeCheckUtil;/*边缘检查*/
    TrendCheckUtil mTrendCheckUtil;/*趋势检查*/
    SpringHoldersUtil mSpringHoldersUtil;


    List<ISpringChild> mSpringChild = new ArrayList<>();
    View mContentView;

    /**
     * 获得边缘状态检测器
     *
     * @return SpringView的边缘检测器
     */
    public EdgeCheckUtil getEdgeCheckUtil() {
        return mEdgeCheckUtil;
    }

    /**
     * 获得触摸检测器
     *
     * @return SpringView的触摸检测器
     */
    public TrendCheckUtil getTrendCheckUtil() {
        return mTrendCheckUtil;
    }

    public SpringView(Context context) {
        this(context, null);
    }

    public SpringView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpringView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpringView);
            boolean isen = ta.getBoolean(R.styleable.SpringView_enable_springback, false);
            if (isen) enableSpringback();
            ta.recycle();
        }
    }

    void init() {
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(getContext())) / 2;
        mMotionEventUtil = new MotionEventUtil();
        mMotionEventUtil.setTrajectory(true);
        mEdgeCheckUtil = new EdgeCheckUtil();
        mTrendCheckUtil = new TrendCheckUtil();
        mSpringHoldersUtil = new SpringHoldersUtil();

    }

    @Override
    protected void onFinishInflate() {
        int childCount = getChildCount();
        if (childCount > 1)
            throw new IllegalStateException("SpringView中最多只能包含一个ContentView");
        if (childCount != 1) return;

        mContentView = getChildAt(0);
        super.onFinishInflate();
    }

    /**
     * 添加SpringChild
     *
     * @param childs SpringChild集合
     */
    public void setSpringChild(List<? extends ISpringChild> childs) {
        if (childs == null && childs.isEmpty()) return;
        setSpringChild(childs.toArray(new ISpringChild[childs.size()]));
    }

    /**
     * 添加SpringChild
     *
     * @param childs SpringChild集合
     */
    public void setSpringChild(ISpringChild... childs) {
        cleanSpringChild();
        addSpringChild(childs);
    }

    /*移除SpringChild*/
    public void removeSpringChild(ISpringChild... childs) {
        for (ISpringChild child : childs) {
            View contentView = child.getView();
            if (contentView != null) removeView(contentView);
            mSpringChild.remove(child);
        }
        checkEnableSpringback();
    }

    /**
     * 清理 SpringChild
     */
    public void cleanSpringChild() {
        if (!mSpringChild.isEmpty()) {
            for (ISpringChild child : mSpringChild) {
                View contentView = child.getView();
                if (contentView != null) removeView(contentView);
            }
            mSpringChild.clear();
        }
    }

    private void addSpringChild(List<? extends ISpringChild> childs) {
        if (childs == null && childs.isEmpty()) return;
        addSpringChild(childs.toArray(new ISpringChild[childs.size()]));
    }

    private void addSpringChild(ISpringChild... childs) {
        if (childs == null && childs.length == 0) return;
        for (ISpringChild child : childs) {
            child.attachParent(this);
            child.getView(getContext(), this);
            mSpringChild.add(child);
        }
        checkEnableSpringback();
        requestLayout();
    }

    public void setFlag(long flag) {
        mSpringFlag |= flag;
    }


    /*清除子View的事件*/
    void cancelChildMotionEvent(MotionEvent ev) {
        int action = ev.getAction();
        ev.setAction(MotionEvent.ACTION_CANCEL);
        super.dispatchTouchEvent(ev);
        ev.setAction(action);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mMotionEventUtil.onDispatchTouchEvent(ev);
        mEdgeCheckUtil.checkEdge(getContentView()); /*获得当前内容布局的边缘状态*/

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                mTrendCheckUtil.clean();
                mSpringFlag &= ~FLAG_IS_ROLLING;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                mTrendCheckUtil.setTouchMove(mMotionEventUtil.getMaxDistanceEvent());
                if (!isScroll()) {
                    if (mTouchSlop <= mTrendCheckUtil.getToucheDistance())
                        mSpringFlag |= FLAG_IS_ROLLING;
                }

                /*检查SpringChild是否开始执行*/
                if (!isPauseScroll()) {
                    for (ISpringChild child : mSpringChild) {
                        if (!mSpringHoldersUtil.isHold() || (!mSpringHoldersUtil.isHold(child) && child.getGroupId() == mSpringHoldersUtil.getGroupId())) {
                            boolean is = child.onCheckHoldSpringView(mEdgeCheckUtil, mTrendCheckUtil) && (mSpringHoldersUtil.isHold() ? true : isScroll());
                            if (is && !mSpringHoldersUtil.isHold()) {
                                mTrendCheckUtil.clean();
                                mTrendCheckUtil.setTouchMove(mMotionEventUtil.getMaxDistanceEvent());
                            }
                            if (is) {
                                registerHolder(child);

                                mSpringFlag |= FLAG_TOUCHE_EVENT_START_ALL_OVER_AGAIN;
                                cancelChildMotionEvent(ev);
                            }
                        }
                    }
                }

                /*当SpringChild全部释放的时候，重启ContentView的滚动事件*/
                if (((mSpringFlag & FLAG_TOUCHE_EVENT_START_ALL_OVER_AGAIN) != 0) && !mSpringHoldersUtil.isHold()) {
                    mSpringFlag &= ~FLAG_TOUCHE_EVENT_START_ALL_OVER_AGAIN;
                    int action = ev.getAction();
                    ev.setAction(MotionEvent.ACTION_DOWN);
                    super.dispatchTouchEvent(ev);
                    ev.setAction(action);
                }

                if (mSpringHoldersUtil.isHold()) {
                    if (isScroll()) {
                        if (isPauseScroll()) return true;
                        float correction_distance = 0;
                        for (int i = mSpringHoldersUtil.mRegisterHoldersArray.size() - 1; i >= 0; i--) {
                            ISpringHolders hold = mSpringHoldersUtil.mRegisterHoldersArray.get(i);
                            correction_distance = hold.onSpring(getContentView(), mTrendCheckUtil.getDistanceY(), correction_distance);
                        }
                    } else {
                        super.dispatchTouchEvent(ev);
                    }
                    return true;
                }
            }
            break;
            case MotionEvent.ACTION_UP:
                if (mSpringHoldersUtil.isHold()) {
                    if (isScroll()) {
                        if (isPauseScroll()) return true;
                        for (int i = mSpringHoldersUtil.mRegisterHoldersArray.size() - 1; i >= 0; i--) {
                            ISpringHolders hold = mSpringHoldersUtil.mRegisterHoldersArray.get(i);
                            hold.onFinish();
                        }
                    } else {
                        super.dispatchTouchEvent(ev);
                    }
                    mMotionEventUtil.onCleanTouchEvent(ev);
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mSpringHoldersUtil.isHold()) {
                    if (isScroll()) {
                        if (isPauseScroll()) return true;
                        for (int i = mSpringHoldersUtil.mRegisterHoldersArray.size() - 1; i >= 0; i--) {
                            ISpringHolders hold = mSpringHoldersUtil.mRegisterHoldersArray.get(i);
                            hold.onCancel();
                        }
                    } else {
                        super.dispatchTouchEvent(ev);
                    }
                    mMotionEventUtil.onCleanTouchEvent(ev);
                    return true;
                }
                break;
        }

        boolean is = super.dispatchTouchEvent(ev);
        mMotionEventUtil.onCleanTouchEvent(ev);
        return true;
    }

    /*获得内容View*/
    public View getContentView() {
        return mContentView;
    }

    /*边缘检测 */
    public static final class EdgeCheckUtil {
        /* 标识View的状态   1111 0000 0000 */
        public static final long FLAG_MOVE_TO_MASK = 0xF00;
        //        public static final long FLAG_MOVE_TO_LEFT = 0x1 << 8; /*移动到了左边缘*/
        public static final long FLAG_MOVE_TO_TOP = 0x1 << 9; /*移动到了上边缘*/
        //        public static final long FLAG_MOVE_TO_RIGHT = 0x1 << 10; /*移动到了右边缘*/
        public static final long FLAG_MOVE_TO_BOTTOM = 0x1 << 11;  /*移动到了下边缘*/

        long mFlag;

        /*检查边缘*/
        public void checkEdge(View view) {
            mFlag &= ~FLAG_MOVE_TO_MASK;
//            if (!ViewCompat.canScrollHorizontally(view, -1)) mFlag |= FLAG_MOVE_TO_LEFT;
//            if (!ViewCompat.canScrollHorizontally(view, 1)) mFlag |= FLAG_MOVE_TO_RIGHT;
            if (!ViewCompat.canScrollVertically(view, -1)) mFlag |= FLAG_MOVE_TO_TOP;
            if (!ViewCompat.canScrollVertically(view, 1)) mFlag |= FLAG_MOVE_TO_BOTTOM;
        }

        public boolean isEdge() {
            return ((mFlag & FLAG_MOVE_TO_MASK) != 0);
        }

        public boolean isTopEdge() {
            return ((mFlag & FLAG_MOVE_TO_TOP) != 0);
        }

        public boolean isBottomEdge() {
            return ((mFlag & FLAG_MOVE_TO_BOTTOM) != 0);
        }

        public void clean() {
            mFlag |= ~FLAG_MOVE_TO_MASK;
        }
    }

    /*趋势检查*/
    public static final class TrendCheckUtil {
        /* 并表示滑动趋势 1111 0000 */
        public static final int FLAG_MOVE_TREND_MASK = 0xf0;
        public static final int FLAG_MOVE_TREND_TOP = 0x1 << 4;/*向上趋势的滚动*/
        public static final int FLAG_MOVE_TREND_BOTTOM = 0x1 << 7;/*向下趋势的滚动*/

        long mFlag;

        float touch_X, touch_Y;/*总位移*/
        float dis_X, dis_Y; /* 一次检查的位移 */

        public void setTouchMove(MotionEventUtil.SingleMotionEvent event) {
            float moveDistanceX = event.getMoveDistanceX();
            float moveDistanceY = event.getMoveDistanceY();
            dis_X = moveDistanceX;
            dis_Y = moveDistanceY;
            touch_X += moveDistanceX;
            touch_Y += moveDistanceY;

            mFlag &= ~FLAG_MOVE_TREND_MASK;
            if (dis_Y > 0) mFlag |= FLAG_MOVE_TREND_TOP;
            if (dis_Y < 0) mFlag |= FLAG_MOVE_TREND_BOTTOM;
        }

        /*清理数据*/
        public void clean() {
            mFlag &= ~FLAG_MOVE_TREND_MASK;
            dis_X = 0;
            dis_Y = 0;
            touch_X = 0;
            touch_Y = 0;
        }

        public boolean isTopSpring() {
            return ((mFlag & FLAG_MOVE_TREND_TOP) != 0);
        }

        public boolean isBottomSpring() {
            return ((mFlag & FLAG_MOVE_TREND_BOTTOM) != 0);
        }

        public float getDistanceX() {
            return dis_X;
        }

        public float getDistanceY() {
            return dis_Y;
        }

        public float getTouchX() {
            return touch_X;
        }

        public float getTouchY() {
            return touch_Y;
        }


        public double getDistance() {
            return Math.sqrt(dis_X * dis_X + dis_Y * dis_Y);
        }

        public double getToucheDistance() {
            return Math.sqrt(touch_X * touch_X + touch_Y * touch_Y);
        }
    }

    /*当有SpringView被SpringChild持有时候,SpringView自身的事件将被持有者处理*/
    private static final class SpringHoldersUtil {
        List<ISpringHolders> mRegisterHoldersArray = new ArrayList<>();
        int mGroupId;

        private boolean registerHolder(ISpringHolders holder) {
            if ((isHold() && mGroupId != holder.getGroupId()) || isHold(holder)) return false;
            mGroupId = holder.getGroupId();
            return mRegisterHoldersArray.add(holder);
        }

        public int getGroupId() {
            return isHold() ? mGroupId : -1;
        }

        private boolean unregisterHolder(ISpringHolders holder) {
            return mRegisterHoldersArray.remove(holder);
        }

        /*判断某个对象是否被持有*/
        public boolean isHold(ISpringHolders holder) {
            return mRegisterHoldersArray.contains(holder);
        }

        /*判断SpringView是否被持有*/
        public boolean isHold() {
            return !mRegisterHoldersArray.isEmpty();
        }
    }

    public boolean registerHolder(ISpringHolders holder) {
        int size = mSpringHoldersUtil.mRegisterHoldersArray.size();
        boolean isSucce = mSpringHoldersUtil.registerHolder(holder);
        return isSucce;
    }

    public boolean unregisterHolder(ISpringHolders holder) {
        int size = mSpringHoldersUtil.mRegisterHoldersArray.size();
        boolean isSucce = mSpringHoldersUtil.unregisterHolder(holder);
        return isSucce;
    }

    /**
     * 判断是否正在滚动中
     */
    public final boolean isScroll() {
        return (mSpringFlag & FLAG_IS_ROLLING) != 0;
    }

    /**
     * 判断滚动事件是否已被暂停
     */
    public final boolean isPauseScroll() {
        return (mSpringFlag & FLAG_PAUSE_SCROLL_EVENT) != 0;
    }

    /**
     * 启用回弹效果
     */
    public final void enableSpringback() {
        setFlag(FLAG_OPEN_SPRINGBACK);
        checkEnableSpringback();
    }

    /*检查是否启用回弹*/
    private void checkEnableSpringback() {
        long flag = mSpringFlag;
        if ((flag & FLAG_OPEN_SPRINGBACK) != 0) {
            for (ISpringChild child : mSpringChild) {
                if (child.getGroupId() == SimperSpringTop.GROUP_ID) {
                    flag &= ~FLAG_OPEN_SPRINGBACK_TOP;
                } else if (child.getGroupId() == SimperSpringBottom.GROUP_ID) {
                    flag &= ~FLAG_OPEN_SPRINGBACK_BOTTOM;
                }
            }

            if ((flag & FLAG_OPEN_SPRINGBACK) != 0) {
                List<ISpringChild> springChilds = new ArrayList<>();
                if ((flag & FLAG_OPEN_SPRINGBACK_TOP) != 0) {
                    springChilds.add(new SimperSpringTop());
                }
                if ((flag & FLAG_OPEN_SPRINGBACK_BOTTOM) != 0) {
                    springChilds.add(new SimperSpringBottom());
                }
                if (!springChilds.isEmpty()) addSpringChild(springChilds);
            }
        }

    }

    /*-------------------------回弹效果--------------------------*/
    ValueAnimator mSpringbackAnimation;
    ISpringbackExecutor mISpringbackExecutor;

    /**
     * 执行回弹操作
     *
     * @param child              执行者的ISpringChild
     * @param springbackExecutor 执行回调，回弹逻辑实现的地方
     * @param duration           执行时间
     */
    public void starSpringback(ISpringChild child, final ISpringbackExecutor springbackExecutor, long duration) {
        if (mSpringHoldersUtil.isHold(child)) {
            mSpringFlag |= FLAG_PAUSE_SCROLL_EVENT;
            if (mSpringbackAnimation == null) {
                mSpringbackAnimation = ValueAnimator.ofFloat(1f, 0f);
                mSpringbackAnimation.setInterpolator(new LinearInterpolator());
                mSpringbackAnimation.addUpdateListener(this);
            } else if (mSpringbackAnimation.isRunning()) {
                mSpringbackAnimation.end();
            }
            mISpringbackExecutor = springbackExecutor;
            mSpringbackAnimation.setFloatValues(1f, 0f);
            mSpringbackAnimation.setDuration(duration);
            mSpringbackAnimation.start();
        }
    }

    @Override
    public final void onAnimationUpdate(ValueAnimator animation) {
        float value = (float) animation.getAnimatedValue();
        if (value == 0) {
            mSpringFlag &= ~FLAG_PAUSE_SCROLL_EVENT;
        } else mSpringFlag |= FLAG_PAUSE_SCROLL_EVENT;
        if (mISpringbackExecutor != null)
            mISpringbackExecutor.onSpringback(value, animation.getCurrentPlayTime());

    }

    private static final class SimperSpringTop extends ImpSpringChild_Top implements ISpringbackExecutor {
        float mDistance;

        public SimperSpringTop() {
        }

        @Override
        public float onSpring(View springContentView, float dis_y, float correction_distance_y) {
            mDistance += dis_y;
            if (mDistance <= 0) onCancel();
            else scoll(mDistance);
            return mDistance;
        }

        @Override
        public void onCancel() { /*取消*/
            scoll(0);
            mDistance = 0;
            /*移除持有*/
            release();
        }


        private void scoll(float dis) {
            getParent().getContentView().setTranslationY(dis / 2);
        }

        @Override
        public void onFinish() { /*完成*/
            springback(this);
        }

        @Override
        public View onCreateView(Context context, SpringView springView) {
            return null;
        }

        @Override
        public void onSpringback(float rate, long currentPlayTime) {
            scoll(mDistance * rate);
            if (rate == 0) onCancel();
        }
    }

    private static final class SimperSpringBottom extends ImpSpringChild_Bottom implements ISpringbackExecutor {

        float mDistance;

        public SimperSpringBottom() {
        }

        @Override
        public float onSpring(View springContentView, float dis_y, float correction_distance_y) {
            mDistance += dis_y;
            if (mDistance >= 0) onCancel();
            else scoll(mDistance);
            return mDistance;
        }

        @Override
        public void onCancel() { /*取消*/
            scoll(0);
            mDistance = 0;
            /*移除持有*/
            release();
        }


        private void scoll(float dis) {
            getParent().getContentView().setTranslationY(dis / 2);
        }

        @Override
        public void onFinish() { /*完成*/
            springback(this);
        }

        @Override
        public View onCreateView(Context context, SpringView springView) {
            return null;
        }

        @Override
        public void onSpringback(float rate, long currentPlayTime) {
            scoll(mDistance * rate);
            if (rate == 0) onCancel();
        }
    }

}