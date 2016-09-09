package com.cleveroad.bubbleanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * BubbleAnimationLayout can contains two children. One of them must has
 * {@link LayoutParams} with containerType = {@link LayoutParams#BASE_CONTAINER} - base view.
 * BubbleAnimationLayout uses this child for determining size. Another one child must has
 * {@link LayoutParams} with containerType = {@link LayoutParams#CONTEXT_CONTAINER} - context view.
 * Animation can be launched by swipe with start point x < indicatorWidth and end point x < getWidth / 10;
 */

public class BubbleAnimationLayout extends ViewGroup {

    private static final int DEFAULT_INDICATOR_WIDTH = -1;

    private final Paint mIndicatorPaint = new Paint();
    private final RectF mIndicatorRectangle = new RectF();

    private int mAnimationColor = BubbleAnimationView.DEFAULT_ANIMATION_COLOR;
    private float mIndicatorWidth = DEFAULT_INDICATOR_WIDTH;

    @Nullable
    private BubbleAnimationView mAnimationView;

    @Nullable
    private View mBaseContainer;

    @Nullable
    private View mContextView;

    private AnimatorSet mForwardAnimatorSet;
    private AnimatorSet mReverseAnimatorSet;
    private Animator mDefaultShowAnimator;

    private List<BubbleAnimationEndListener> mAnimationEndListeners;

    private float mDownPositionX = -1;

    public BubbleAnimationLayout(Context context) {
        this(context, null);
    }

    public BubbleAnimationLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleAnimationLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("unused")
    public BubbleAnimationLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setWillNotDraw(false);
        if (!isInEditMode()) {
            if (attrs != null) {
                TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleAnimationView);
                mAnimationColor = typedArray.getColor(R.styleable.BubbleAnimationView_bav_animation_color, BubbleAnimationView.DEFAULT_ANIMATION_COLOR);
                mIndicatorWidth = typedArray.getDimension(R.styleable.BubbleAnimationView_bav_indicator_width, DEFAULT_INDICATOR_WIDTH);
                typedArray.recycle();
            }
        }
        mAnimationView = new BubbleAnimationView(getContext(), mAnimationColor);
        mAnimationView.setId(R.id.bav_bubble_animation_view);
        mAnimationView.setAnimated(false);
        addView(mAnimationView);
        mIndicatorPaint.setStyle(Paint.Style.FILL);
        mIndicatorPaint.setColor(Color.rgb(Color.red(mAnimationColor), Color.green(mAnimationColor), Color.blue(mAnimationColor)));
    }


    @Override
    protected void onDetachedFromWindow() {
        if (mForwardAnimatorSet != null && mForwardAnimatorSet.isRunning()) {
            mForwardAnimatorSet.end();
        }
        if (mReverseAnimatorSet != null && mReverseAnimatorSet.isRunning()) {
            mReverseAnimatorSet.end();
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mAnimationColor = mAnimationColor;
        savedState.mIndicatorWidth = mIndicatorWidth;
        if (mBaseContainer != null) {
            savedState.mBaseViewVisibility = mContextView != null && mContextView.getVisibility() == GONE ? VISIBLE : mBaseContainer.getVisibility();
        }
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        updateColor(savedState.mAnimationColor);
        mIndicatorWidth = savedState.mIndicatorWidth;
        if (mBaseContainer != null) {
            mBaseContainer.setVisibility(savedState.mBaseViewVisibility);
        }
        if (mContextView != null) {
            mContextView.setVisibility(savedState.mBaseViewVisibility == VISIBLE ? GONE : VISIBLE);
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = MeasureSpec.getSize(heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.getContainerType() == LayoutParams.BASE_CONTAINER) {

                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                height = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
                break;
            }
        }
        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightSpec);

        if (mIndicatorWidth == DEFAULT_INDICATOR_WIDTH) {
            mIndicatorWidth = getMeasuredWidth() / 100;
        }
        mIndicatorRectangle.set(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + mIndicatorWidth, getMeasuredHeight() - getPaddingBottom());

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int contentWidthSpec;
            if (params.getContainerType() == LayoutParams.BASE_CONTAINER) {
                contentWidthSpec = MeasureSpec.makeMeasureSpec((int) (getMeasuredWidth() - params.leftMargin - params.rightMargin - getPaddingLeft() - getPaddingRight() - mIndicatorWidth), MeasureSpec.EXACTLY);
            } else {
                contentWidthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - params.leftMargin - params.rightMargin - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
            }
            int contentHeightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - params.topMargin - params.bottomMargin - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);

            child.measure(contentWidthSpec, contentHeightSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int l = getPaddingLeft() + params.leftMargin;
            int t = getPaddingTop() + params.topMargin;
            int r = getMeasuredWidth() - getPaddingRight() - params.rightMargin;
            int b = getMeasuredHeight() - getPaddingBottom() - params.bottomMargin;

            if (params.getContainerType() == LayoutParams.BASE_CONTAINER) {
                l += mIndicatorWidth;
            }
            child.layout(l, t, r, b);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (params instanceof LayoutParams) {
            LayoutParams lp = (LayoutParams) params;
            if (lp.getContainerType() == LayoutParams.BASE_CONTAINER) {
                super.addView(child, 0, params);
            } else {
                super.addView(child, index, params);
            }
            checkChildrenCount();
            checkChildType(child);
        }
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        boolean added = super.addViewInLayout(child, index, params);
        checkChildrenCount();
        return added;
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
        boolean added = super.addViewInLayout(child, index, params, preventRequestLayout);
        checkChildrenCount();
        return added;
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        nullingView(view);
    }

    @Override
    public void removeViewInLayout(View view) {
        super.removeViewInLayout(view);
        nullingView(view);
    }

    @Override
    public void removeViewsInLayout(int start, int count) {
        for (int i = start; i < start + count && i < getChildCount(); i++) {
            View child = getChildAt(i);
            nullingView(child);
        }
        super.removeViewsInLayout(start, count);
    }

    @Override
    public void removeViewAt(int index) {
        View child = getChildAt(index);
        nullingView(child);
        super.removeViewAt(index);
    }

    @Override
    public void removeViews(int start, int count) {
        for (int i = start; i < start + count && i < getChildCount(); i++) {
            View child = getChildAt(i);
            nullingView(child);
        }
        super.removeViews(start, count);
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        nullingViews();
    }

    @Override
    public void removeAllViewsInLayout() {
        super.removeAllViewsInLayout();
        nullingViews();
    }

    @Override
    protected void removeDetachedView(View child, boolean animate) {
        super.removeDetachedView(child, animate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mIndicatorRectangle, mIndicatorPaint);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mContextView != null && mContextView.getVisibility() == VISIBLE) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            if (mDownPositionX < 0 && ev.getX() < getPaddingLeft() + mIndicatorWidth * 3) {
                return true;
            }
        } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            if (mDownPositionX > 0 && ev.getX() - mDownPositionX > getWidth() / 10) {
                return true;
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (mDownPositionX > 0 && ev.getX() - mDownPositionX > 0) {
                return true;
            }
        }
        mDownPositionX = -1;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mContextView != null && mContextView.getVisibility() == VISIBLE) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_DOWN) {
            if (event.getX() < getPaddingLeft() + mIndicatorWidth * 3) {
                mDownPositionX = event.getX();
                return true;
            }
        } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            if (mAnimationView != null && event.getX() - mDownPositionX > getWidth() / 10 && mAnimationView.getVisibility() != VISIBLE) {
                startViewAnimation(mDefaultShowAnimator);
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * Setup animation color
     * @param color animation color
     */
    @SuppressWarnings("unused")
    public void setAnimationColor(@ColorInt int color) {
        updateColor(color);
        invalidate();
    }

    /**
     * Return animation color
     * @return animation color
     */
    @SuppressWarnings("unused")
    public int getAnimationColor() {
        return mAnimationColor;
    }

    /**
     * Set indicator width
     * @param indicatorWidth indicator width in px
     */
    @SuppressWarnings("unused")
    public void setIndicatorWidth(int indicatorWidth) {
        mIndicatorWidth = indicatorWidth;
        mIndicatorRectangle.set(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + mIndicatorWidth, getMeasuredHeight() - getPaddingBottom());
        requestLayout();
    }

    /**
     * Return indicator width
     * @return indicator width
     */
    @SuppressWarnings("unused")
    public float getIndicatorWidth() {
        return mIndicatorWidth;
    }

    /**
     * Reset view to initial state
     */
    @SuppressWarnings("unused")
    public void resetView() {
        if (mForwardAnimatorSet != null && mForwardAnimatorSet.isRunning()) {
            mForwardAnimatorSet.end();
        }
        if (mReverseAnimatorSet != null && mReverseAnimatorSet.isRunning()) {
            mReverseAnimatorSet.end();
        }
        if (mAnimationView != null && mAnimationView.isPlaying()) {
            mAnimationView.stopAnimation();
        }
        if (mBaseContainer != null) {
            mBaseContainer.setVisibility(VISIBLE);
        }
        if (mAnimationView != null) {
            mAnimationView.setAnimated(false);
        }
        if (mContextView != null) {
            mContextView.setVisibility(GONE);
        }
    }

    /**
     * Show context view with animation
     * @param animator {@link Animator} for context view, if animator equals null, will launch default animation
     */
    @SuppressWarnings("unused")
    public void showContextViewWithAnimation(@Nullable Animator animator) {
        startViewAnimation(animator);
    }

    /**
     * Show base view with animation
     * @param animator Animator for base view, if animator equals null, will launch default animation
     */
    @SuppressWarnings("unused")
    public void showBaseViewWithAnimation(@Nullable Animator animator) {
        reverseAnimation(animator);
    }

    /**
     * Show bubbles with animation
     * @param listener {@link BubbleAnimationEndListener}
     */
    @SuppressWarnings("unused")
    public void showBubbleViewWithAnimation(@Nullable BubbleAnimationEndListener listener) {
        startBubbleAnimation(listener);
    }

    /**
     * Hide bubbles with animation
     * @param listener {@link BubbleAnimationEndListener}
     */
    public void hideBubbledViewWithAnimation(@Nullable BubbleAnimationEndListener listener) {
        reverseBubbleAnimation(listener);
    }

    /**
     * Show bubbles
     */
    @SuppressWarnings("unused")
    public void showBubbledView() {
        if ((mForwardAnimatorSet != null && mForwardAnimatorSet.isRunning())
                || (mAnimationView != null && mAnimationView.isPlaying())
                || (mReverseAnimatorSet != null && mReverseAnimatorSet.isRunning())) {
            return;
        }
        if (mAnimationView != null) {
            mAnimationView.setAnimated(true);
        }
    }

    /**
     * Hide bubbles
     */
    @SuppressWarnings("unused")
    public void hideBubbledView() {
        if ((mForwardAnimatorSet != null && mForwardAnimatorSet.isRunning())
                || (mAnimationView != null && mAnimationView.isPlaying())
                || (mReverseAnimatorSet != null && mReverseAnimatorSet.isRunning())) {
            return;
        }
        if (mBaseContainer != null) {
            mBaseContainer.setVisibility(VISIBLE);
        }
        if (mAnimationView != null) {
            mAnimationView.setAnimated(false);
        }
    }

    /**
     * Show context view
     */
    @SuppressWarnings("unused")
    public void showContextView() {
        if ((mForwardAnimatorSet != null && mForwardAnimatorSet.isRunning())
                || (mAnimationView != null && mAnimationView.isPlaying())
                || (mReverseAnimatorSet != null && mReverseAnimatorSet.isRunning())) {
            return;
        }
        if (mBaseContainer != null) {
            mBaseContainer.setVisibility(GONE);
        }
        if (mAnimationView != null) {
            mAnimationView.setAnimated(true);
        }
        if (mContextView != null) {
            mContextView.setVisibility(VISIBLE);
        }
    }

    /**
     * Show base view
     */
    @SuppressWarnings("unused")
    public void showBaseView() {
        if ((mForwardAnimatorSet != null && mForwardAnimatorSet.isRunning())
                || (mAnimationView != null && mAnimationView.isPlaying())
                || (mReverseAnimatorSet != null && mReverseAnimatorSet.isRunning())) {
            return;
        }
        if (mBaseContainer != null) {
            mBaseContainer.setVisibility(VISIBLE);
        }
        if (mAnimationView != null) {
            mAnimationView.setAnimated(false);
        }
        if (mContextView != null) {
            mContextView.setVisibility(GONE);
        }
    }

    /**
     * Add {@link BubbleAnimationEndListener} which will call when show or hide animation ends
     * @param animationEndListener {@link BubbleAnimationEndListener}
     */
    @SuppressWarnings("unused")
    public void addAnimationEndListener(BubbleAnimationEndListener animationEndListener) {
        if (mAnimationEndListeners == null) {
            mAnimationEndListeners = new ArrayList<>();
        }
        mAnimationEndListeners.add(animationEndListener);
    }

    /**
     * Remove {@link BubbleAnimationEndListener}
     * @param animationEndListener {@link BubbleAnimationEndListener}
     */
    @SuppressWarnings("unused")
    public void removeAnimationEndListener(BubbleAnimationEndListener animationEndListener) {
        if (mAnimationEndListeners != null) {
            mAnimationEndListeners.remove(animationEndListener);
        }
    }

    /**
     * Remove all {@link BubbleAnimationEndListener}
     */
    @SuppressWarnings("unused")
    public void removeAllAnimationEndListeners() {
        if (mAnimationEndListeners != null) {
            mAnimationEndListeners.clear();
        }
    }

    /**
     * Set animator which will be called by touch event
     * @param animator {@link Animator}
     */
    @SuppressWarnings("unused")
    public void setDefaultShowAnimator(Animator animator) {
        mDefaultShowAnimator = animator;
    }

    private void checkChildType(View child) {
        LayoutParams params = (LayoutParams) child.getLayoutParams();
        if (params.getContainerType() == LayoutParams.BASE_CONTAINER && mBaseContainer == null) {
            mBaseContainer = child;
        } else if (params.getContainerType() == LayoutParams.BASE_CONTAINER && mBaseContainer != null) {
            throw new IllegalArgumentException("Base container already defined");
        } else if (params.getContainerType() == LayoutParams.CONTEXT_CONTAINER && mContextView == null) {
            mContextView = child;
            child.setVisibility(GONE);
        } else if (params.getContainerType() == LayoutParams.CONTEXT_CONTAINER && mContextView != null) {
            throw new IllegalArgumentException("Context container already defined");
        }
    }

    private void checkChildrenCount() {
        if (getChildCount() > 3) {
            throw new IllegalArgumentException("Number of children must be less than three");
        }
    }

    private void startViewAnimation(@Nullable final Animator animator) {
        if (mForwardAnimatorSet == null) {
            mForwardAnimatorSet = new AnimatorSet();
        } else if (mForwardAnimatorSet.isRunning()) {
            return;
        }
        if (mContextView == null || mAnimationView == null || mAnimationView.isAnimated()
                || mAnimationView.isPlaying()
                || (mReverseAnimatorSet != null && mReverseAnimatorSet.isRunning())) {
            return;
        }
        Animator contextAnimator = animator;
        if (contextAnimator == null) {
            contextAnimator = ObjectAnimator.ofPropertyValuesHolder(mContextView, PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f));
            contextAnimator.setDuration(500);
        }
        mAnimationView.getAnimator().addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeListener(this);
                if (mBaseContainer != null) {
                    mBaseContainer.setVisibility(GONE);
                }
                if (mContextView != null) {
                    mContextView.setVisibility(VISIBLE);
                }
            }
        });
        mForwardAnimatorSet.playSequentially(mAnimationView.getAnimator(), contextAnimator);
        mForwardAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeListener(this);
                if (mAnimationEndListeners != null) {
                    for (BubbleAnimationEndListener listener : mAnimationEndListeners) {
                        listener.onEndAnimation(true, animation);
                    }
                }
            }
        });
        mForwardAnimatorSet.start();
    }

    private void reverseAnimation(@Nullable Animator animator) {
        if (mReverseAnimatorSet == null) {
            mReverseAnimatorSet = new AnimatorSet();
        } else if (mReverseAnimatorSet.isRunning()) {
            return;
        }
        if (mContextView == null || mAnimationView == null || !mAnimationView.isAnimated()
                || mAnimationView.isPlaying()
                || mForwardAnimatorSet != null && mForwardAnimatorSet.isRunning()) {
            return;
        }
        Animator contextAnimator = animator;
        if (contextAnimator == null) {
            contextAnimator = ObjectAnimator.ofPropertyValuesHolder(mContextView, PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f));
            contextAnimator.setDuration(500);
        }
        contextAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeListener(this);
                if (mBaseContainer != null) {
                    mBaseContainer.setVisibility(VISIBLE);
                }
                if (mContextView != null) {
                    mContextView.setVisibility(GONE);
                }
            }
        });
        mReverseAnimatorSet.playSequentially(contextAnimator, mAnimationView.getReverseAnimator());
        mReverseAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeListener(this);
                if (mAnimationEndListeners != null) {
                    for (BubbleAnimationEndListener listener : mAnimationEndListeners) {
                        listener.onEndAnimation(false, animation);
                    }
                }
            }
        });
        mReverseAnimatorSet.start();
    }

    private void startBubbleAnimation(@Nullable final BubbleAnimationEndListener listener) {
        if (mAnimationView == null || mAnimationView.isAnimated()
                || mAnimationView.isPlaying()
                || (mForwardAnimatorSet != null && mForwardAnimatorSet.isRunning())
                || (mReverseAnimatorSet != null && mReverseAnimatorSet.isRunning())) {
            return;
        }
        mAnimationView.getAnimator().addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeListener(this);
                if (listener != null) {
                    listener.onEndAnimation(false, animation);
                }
            }
        });
        mAnimationView.getAnimator().start();
    }

    private void reverseBubbleAnimation(@Nullable final BubbleAnimationEndListener listener) {
        if (mAnimationView == null || !mAnimationView.isAnimated()
                || mAnimationView.isPlaying()
                || (mForwardAnimatorSet != null && mForwardAnimatorSet.isRunning())
                || (mReverseAnimatorSet != null && mReverseAnimatorSet.isRunning())) {
            return;
        }
        if (mBaseContainer != null) {
            mBaseContainer.setVisibility(VISIBLE);
        }
        mAnimationView.getReverseAnimator().addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeListener(this);
                if (listener != null) {
                    listener.onEndAnimation(false, animation);
                }
            }
        });
        mAnimationView.getReverseAnimator().start();
    }

    private void nullingViews() {
        mBaseContainer = null;
        mContextView = null;
        mAnimationView = null;
    }

    private void nullingView(View view) {
        if (view != null && view.getLayoutParams() instanceof LayoutParams) {
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            if (params.getContainerType() == LayoutParams.BASE_CONTAINER) {
                mBaseContainer = null;
            } else if (params.getContainerType() == LayoutParams.CONTEXT_CONTAINER) {
                mContextView = null;
            }
        }
    }

    private void updateColor(@ColorInt int color) {
        mAnimationColor = color;
        mIndicatorPaint.setColor(Color.rgb(Color.red(color), Color.green(color), Color.blue(color)));
        if (mAnimationView != null) {
            mAnimationView.setAnimationColor(color);
        }
    }

    /**
     * Callback for determining end of animation
     */
    public interface BubbleAnimationEndListener {
        /**
         * Call when animation ends
         * @param isForwardAnimation true if show, false if hide
         * @param animation the animation which reached its end.
         */
        void onEndAnimation(boolean isForwardAnimation, Animator animation);
    }

    /**
     * Per-child layout information for layouts that support margins and view type.
     */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        public static final int BASE_CONTAINER = 1;
        public static final int CONTEXT_CONTAINER = 0;
        private static final int NONE = 2;

        private int mContainerType = NONE;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.BubbleAnimationView);
            mContainerType = typedArray.getInt(R.styleable.BubbleAnimationView_bav_view_type, NONE);
            typedArray.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            if (source instanceof LayoutParams) {
                mContainerType = ((LayoutParams) source).getContainerType();
            }
        }

        public int getContainerType() {
            return mContainerType;
        }

        @SuppressWarnings("unused")
        public void setContainerType(int containerType) {
            mContainerType = containerType;
        }
    }

    private static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };

        int mAnimationColor;
        float mIndicatorWidth;
        int mBaseViewVisibility;


        public SavedState(Parcel source) {
            super(source);
            mAnimationColor = source.readInt();
            mIndicatorWidth = source.readFloat();
            mBaseViewVisibility = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mAnimationColor);
            out.writeFloat(mIndicatorWidth);
            out.writeInt(mBaseViewVisibility);
        }
    }
}
