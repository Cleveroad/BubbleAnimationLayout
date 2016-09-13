package com.cleveroad.bubbleanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

class BubbleAnimationView extends View {

    public static final int DEFAULT_ANIMATION_COLOR = Color.RED;

    private ValueAnimator mForwardAnimator;
    private ValueAnimator mReverseValueAnimator;

    private RectF mBaseRectangle;

    private Paint mCirclePaint;

    private CircleDrawable[] mCircleDrawables;

    private boolean mAnimated = true;

    public BubbleAnimationView(Context context, int color) {
        super(context);
        init(color);
    }

    @SuppressWarnings("unused")
    private BubbleAnimationView(Context context) {
        super(context);
    }

    @SuppressWarnings("unused")
    private BubbleAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    private BubbleAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private BubbleAnimationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(int color) {
        mCirclePaint = new Paint();
        mCirclePaint.setColor(color);
        mCirclePaint.setAntiAlias(true);

        mBaseRectangle = new RectF();

        mCircleDrawables = new CircleDrawable[6];

        CircleDrawable firstCircle = new FirstCircle(mCirclePaint, color);
        mCircleDrawables[0] = firstCircle;

        CircleDrawable secondCircle = new SecondCircle(mCirclePaint, color);
        mCircleDrawables[1] = secondCircle;

        CircleDrawable thirdCircle = new ThirdCircle(mCirclePaint, color);
        mCircleDrawables[2] = thirdCircle;

        CircleDrawable fourthCircle = new FourthCircle(mCirclePaint, color);
        mCircleDrawables[3] = fourthCircle;

        CircleDrawable fifthCircle = new FifthCircle(mCirclePaint, color);
        mCircleDrawables[4] = fifthCircle;

        CircleDrawable sixthCircle = new SixthCircle(mCirclePaint, color);
        mCircleDrawables[5] = sixthCircle;

        initAnimator();
        setAnimated(false);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mAnimated = mAnimated;
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
        setAnimated(savedState.mAnimated);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBaseRectangle.set(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingRight());
        for (CircleDrawable circleDrawable : mCircleDrawables) {
            circleDrawable.setBounds(mBaseRectangle);
        }
    }

    private void initAnimator() {
        mForwardAnimator = ValueAnimator.ofFloat(0, 1);
        mReverseValueAnimator = ValueAnimator.ofFloat(1, 0);

        LinearInterpolator interpolator = new LinearInterpolator();

        mForwardAnimator.setInterpolator(interpolator);
        mReverseValueAnimator.setInterpolator(interpolator);


        long duration = (long) (Constants.TOTAL_DURATION + Constants.DEFAULT_SPEED_COEFFICIENT);
        mForwardAnimator.setDuration(duration);
        mReverseValueAnimator.setDuration(duration);

        ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                updateParams((Float) valueAnimator.getAnimatedValue());
                Log.d("ANIMATION", "FRACTION = " + valueAnimator.getAnimatedValue());
                invalidate();
            }
        };
        mForwardAnimator.addUpdateListener(updateListener);
        mReverseValueAnimator.addUpdateListener(updateListener);

        mForwardAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimated = true;
            }
        });
        mReverseValueAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimated = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimated = false;
                setVisibility(INVISIBLE);
            }
        });
    }

    private void updateParams(float dt) {
        for (CircleDrawable circleDrawable : mCircleDrawables) {
            circleDrawable.updateParams(dt);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAnimated) {
            for (CircleDrawable circle : mCircleDrawables) {
                circle.drawLastFrame(canvas);
            }
        } else {
            for (CircleDrawable circle : mCircleDrawables) {
                circle.onDraw(canvas);
            }
        }
    }

    void setAnimationColor(@ColorInt int color) {
        mCirclePaint.setColor(color);
        for (CircleDrawable drawable : mCircleDrawables) {
            drawable.setStartColor(color);
        }
    }

    void setAnimated(boolean animated) {
        if (mAnimated != animated) {
            mAnimated = animated;
            setVisibility(animated ? VISIBLE : INVISIBLE);
            invalidate();
        }
    }

    boolean isAnimated() {
        return mAnimated;
    }

    boolean isPlaying() {
        return mForwardAnimator.isRunning()
                || mReverseValueAnimator.isRunning();
    }

    void stopAnimation() {
        if (mForwardAnimator.isRunning()) {
            mForwardAnimator.end();
        }
        if (mReverseValueAnimator.isRunning()) {
            mReverseValueAnimator.end();
        }
    }

    Animator getAnimator() {
        return mForwardAnimator;
    }

    Animator getReverseAnimator() {
        return mReverseValueAnimator;
    }

    private static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int i) {
                return new SavedState[0];
            }
        };

        boolean mAnimated;

        public SavedState(Parcel source) {
            super(source);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mAnimated ? 1 : 0);
        }
    }

}
