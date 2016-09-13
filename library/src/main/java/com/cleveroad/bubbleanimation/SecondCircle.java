package com.cleveroad.bubbleanimation;

import android.graphics.Paint;
import android.graphics.RectF;

class SecondCircle extends CircleDrawable {

    private static final float VISIBILITY_FRACTION_START = 32 * Constants.FRAME_SPEED;
    private static final float VISIBILITY_FRACTION_END = VISIBILITY_FRACTION_START + 56 * Constants.FRAME_SPEED;

    private static final float MOVE_FRACTION_START = VISIBILITY_FRACTION_START;
    private static final float MOVE_FRACTION_END = MOVE_FRACTION_START + 32 * Constants.FRAME_SPEED;

    private float mLastMoveX;

    private float mFirstMoveY;
    private float mLastMoveY;

    private float mLastRadius;

    public SecondCircle(Paint paint, int startColor) {
        super(paint, startColor);
    }

    @Override
    protected void initParams(RectF bounds) {
        mLastMoveX = bounds.left + bounds.width() / 7 * 6;

        mFirstMoveY = bounds.height() / 2.2f;
        mLastMoveY = bounds.height() / 2.2f - bounds.height() / 3;

        mLastRadius = bounds.height() / 4;
    }

    @Override
    public void updateParams(float dt) {
        setDraw(DrawableUtils.between(dt, VISIBILITY_FRACTION_START, VISIBILITY_FRACTION_END));
        if (!isDraw()) {
            return;
        }
        if (DrawableUtils.between(dt, MOVE_FRACTION_START, MOVE_FRACTION_END)) {
            float t = DrawableUtils.normalize(dt, MOVE_FRACTION_START, MOVE_FRACTION_END);
            getCenter().x = DrawableUtils.enlarge(getBounds().left, mLastMoveX, t);
            getCenter().y = DrawableUtils.reduce(mFirstMoveY, mLastMoveY, t);
        }
        float t = DrawableUtils.normalize(dt, VISIBILITY_FRACTION_START, VISIBILITY_FRACTION_END);
        setRadius(DrawableUtils.enlarge(0, mLastRadius, t));
        if (DrawableUtils.between(dt, VISIBILITY_FRACTION_START, MOVE_FRACTION_END)) {
            t = DrawableUtils.normalize(dt, VISIBILITY_FRACTION_START, MOVE_FRACTION_END);
            setAlpha((int) DrawableUtils.enlarge(50, 255, t));
        }
    }

    @Override
    void initLastFrameParams() {
        getCenter().x = mLastMoveX;
        getCenter().y = mLastMoveY;
        setRadius(mLastRadius);
        setAlpha(255);
    }

}
