package com.cleveroad.bubbleanimation;

import android.graphics.Paint;
import android.graphics.RectF;

class FifthCircle extends CircleDrawable {

    private static final float VISIBILITY_FRACTION_START = 80 * Constants.FRAME_SPEED;
    private static final float VISIBILITY_FRACTION_END = VISIBILITY_FRACTION_START + 24 * Constants.FRAME_SPEED;

    private float mRadius;
    private float mFirstX;
    private float mLastX;
    private float mFirstY;
    private float mLastY;


    public FifthCircle(Paint paint, int startColor) {
        super(paint, startColor);
        setAlpha(188);
    }

    @Override
    void initParams(RectF bounds) {
        mRadius = bounds.height() / 3 * 2;
        mFirstX = bounds.left - mRadius;
        mLastX = bounds.left + bounds.width() / 2.0f + mRadius;
        mFirstY = bounds.height() - mRadius / 2.0f;
        mLastY = bounds.height() - mRadius / 3.0f;
    }

    @Override
    public void updateParams(float dt) {
        setDraw(DrawableUtils.between(dt, VISIBILITY_FRACTION_START, VISIBILITY_FRACTION_END));
        if (!isDraw()) {
            return;
        }
        float t = DrawableUtils.normalize(dt, VISIBILITY_FRACTION_START, VISIBILITY_FRACTION_END);
        setRadius(mRadius);
        getCenter().x = DrawableUtils.enlarge(mFirstX, mLastX, t);
        getCenter().y = DrawableUtils.enlarge(mFirstY, mLastY, t);
    }

    @Override
    void initLastFrameParams() {

    }

}
