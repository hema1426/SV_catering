package com.winapp.svcatering.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class CustomRecyclerView extends RecyclerView {
    private boolean mIsScrolling;
    private boolean mIsTouching;
    private OnScrollListener mOnScrollListener;
    private Runnable mScrollingRunnable;
    private int y = 0;
    public abstract static class OnScrollListener extends RecyclerView.OnScrollListener {
        void onScrollChanged(CustomRecyclerView RvView, int x, int y, int oldX, int oldY)
        {
            //if you need just override this method
        }

        void onEndScroll(CustomRecyclerView RvView)
        {
            //if you need just override this method
        }

        protected abstract void onGoUp();

        protected abstract void onGoDown();
    }

    public CustomRecyclerView(final Context context)
    {
        super(context);
    }

    public CustomRecyclerView(final Context context, @Nullable final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomRecyclerView(final Context context, @Nullable final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent iEv)
    {
        if (isEnabled())
        {
            processEvent(iEv);
            super.dispatchTouchEvent(iEv);
            return true; //to keep receive event that follow down event
        }

        return super.dispatchTouchEvent(iEv);
    }

    private void processEvent(final MotionEvent iEv)
    {
        switch (iEv.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                y = (int) iEv.getY();
                break;

            case MotionEvent.ACTION_UP:
                y = (int) iEv.getY();

                if (mIsTouching && !mIsScrolling && mOnScrollListener != null)
                {
                    mOnScrollListener.onEndScroll(this);
                }

                mIsTouching = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mIsTouching = true;
                mIsScrolling = true;

                int newY = (int) iEv.getY();
                int difY = y - newY;

                int MAX_VALUE = 200;
                int MIN_VALUE = -200;
                if (difY > MAX_VALUE)
                {
                    if (mOnScrollListener != null)
                    {
                        mOnScrollListener.onGoDown();
                    }
                    y = newY;
                }
                else if (difY < MIN_VALUE)
                {
                    if (mOnScrollListener != null)
                    {
                        mOnScrollListener.onGoUp();
                    }
                    y = newY;
                }

                break;
        }
    }

    @Override
    protected void onScrollChanged(int iX, int iY, int iOldX, int iOldY)
    {
        super.onScrollChanged(iX, iY, iOldX, iOldY);

        if (Math.abs(iOldX - iX) > 0)
        {
            if (mScrollingRunnable != null)
            {
                removeCallbacks(mScrollingRunnable);
            }

            mScrollingRunnable = () ->
            {
                if (mIsScrolling && !mIsTouching && mOnScrollListener != null)
                {
                    mOnScrollListener.onEndScroll(CustomRecyclerView.this);
                }

                mIsScrolling = false;
                mScrollingRunnable = null;
            };

            postDelayed(mScrollingRunnable, 200);
        }

        if (mOnScrollListener != null)
        {
            mOnScrollListener.onScrollChanged(this, iX, iY, iOldX, iOldY);
        }
    }

    public void scrollToView(final View iV)
    {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(CustomRecyclerView.this, iV.getParent(), iV, childOffset);
        // Scroll to child.

        CustomRecyclerView.this.scrollToY(childOffset.y);
    }

    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset)
    {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent))
        {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    public void scrollToY(final int iY)
    {
        CustomRecyclerView.this.postDelayed(() ->
        {
            int x = 0;
            int y = iY;
            ObjectAnimator xTranslate = ObjectAnimator.ofInt(CustomRecyclerView.this, "scrollX", x);
            ObjectAnimator yTranslate = ObjectAnimator.ofInt(CustomRecyclerView.this, "scrollY", y);

            AnimatorSet animators = new AnimatorSet();
            animators.setDuration(500L);
            animators.playTogether(xTranslate, yTranslate);
            animators.addListener(new Animator.AnimatorListener()
            {

                @Override
                public void onAnimationStart(Animator arg0)
                {
                    // noting
                }

                @Override
                public void onAnimationRepeat(Animator arg0)
                {
                    // noting
                }

                @Override
                public void onAnimationEnd(Animator arg0)
                {
                    // noting
                }

                @Override
                public void onAnimationCancel(Animator arg0)
                {
                    // noting
                }
            });
            animators.start();
        }, 300);
    }
    public void scrollToTop() {
        scrollToY(0);
    }
    public void setOnRvScrollListener(OnScrollListener mOnEndScrollListener) {
        this.mOnScrollListener = mOnEndScrollListener;
    }
}