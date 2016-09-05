package com.cleveroad.bubbleanimation;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;

abstract class CircleDrawable {

    private Paint mPaint;
    private int mStartColor;
    private RectF mBounds;
    private boolean mDraw = true;
    private float mRadius;
    private PointF mCenter;
    private int mAlpha;

    public CircleDrawable(Paint paint, int startColor) {
        mPaint = new Paint(paint);
        mStartColor = startColor;
        mRadius = 0;
        mCenter = new PointF();
        mAlpha = 0;
    }

    void setStartColor(int startColor) {
        mStartColor = startColor;
    }

    public void onDraw(Canvas canvas) {
        if (mDraw) {
            mPaint.setColor(getColor());
            canvas.drawCircle(mCenter.x, mCenter.y, mRadius, mPaint);
        }
    }

    public void drawLastFrame(Canvas canvas) {
        initLastFrameParams();
        mPaint.setColor(getColor());
        canvas.drawCircle(mCenter.x, mCenter.y, mRadius, mPaint);
    }

    public RectF getBounds() {
        return mBounds;
    }

    public void setBounds(@NonNull RectF bounds) {
        mBounds = bounds;
        initParams(mBounds);
    }

    boolean isDraw() {
        return mDraw;
    }

    void setDraw(boolean draw) {
        mDraw = draw;
    }

    PointF getCenter() {
        return mCenter;
    }

    float getRadius() {
        return mRadius;
    }

    void setRadius(float radius) {
        mRadius = radius;
    }

    void setAlpha(int alpha) {
        mAlpha = alpha;
    }

    int getStartColor() {
        return mStartColor;
    }

    private int getColor() {
        return Color.argb(mAlpha, Color.red(getStartColor()), Color.green(getStartColor()), Color.blue(getStartColor()));
    }

    abstract void initParams(RectF bounds);

    abstract void updateParams(float dt);

    abstract void initLastFrameParams();
}
