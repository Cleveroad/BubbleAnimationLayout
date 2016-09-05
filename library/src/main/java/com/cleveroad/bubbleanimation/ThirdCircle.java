package com.cleveroad.bubbleanimation;

import android.graphics.Paint;
import android.graphics.RectF;

class ThirdCircle extends CircleDrawable {

    private static final float VISIBILITY_FRACTION_START = 48 * Constants.FRAME_SPEED;
    private static final float VISIBILITY_FRACTION_END = VISIBILITY_FRACTION_START + 24 * Constants.FRAME_SPEED;

    private float mLastX;

    private float mFirstY;
    private float mLastY;

    private float radius;

    public ThirdCircle(Paint paint, int startColor) {
        super(paint, startColor);
    }

    @Override
    void initParams(RectF bounds) {
        mLastX = bounds.left + bounds.width();

        mFirstY = bounds.height() / 1.8f;
        mLastY = bounds.height() / 1.8f - bounds.height() / 5;

        radius = bounds.height() / 12;
    }

    @Override
    public void updateParams(float dt) {
        setDraw(DrawableUtils.between(dt, VISIBILITY_FRACTION_START, VISIBILITY_FRACTION_END));
        if (!isDraw()) {
            return;
        }
        float t = DrawableUtils.normalize(dt, VISIBILITY_FRACTION_START, VISIBILITY_FRACTION_END);
        getCenter().x = DrawableUtils.enlarge(getBounds().left, mLastX, t);
        getCenter().y = DrawableUtils.reduce(mFirstY, mLastY, t);
        setRadius(radius);
        setAlpha((int) DrawableUtils.enlarge(50, 255, t));
    }

    @Override
    void initLastFrameParams() {

    }

}
