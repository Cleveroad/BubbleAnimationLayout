package com.cleveroad.bubbleanimation;

import android.graphics.Paint;
import android.graphics.RectF;

class FirstCircle extends CircleDrawable {

    private static final float VISIBILITY_FRACTION_START = 0;
    private static final float VISIBILITY_FRACTION_END = 80 * Constants.FRAME_SPEED;

    private static final float MOVE_FRACTION_START = 0;
    private static final float MOVE_FRACTION_END = 24 * Constants.FRAME_SPEED;

    private static final float MOVE_TO_CENTER_FRACTION_START = 40 * Constants.FRAME_SPEED;
    private static final float MOVE_TO_CENTER_FRACTION_END = MOVE_TO_CENTER_FRACTION_START + 16 * Constants.FRAME_SPEED;

    private float mLastMoveRadius;

    private float mFirstCenterY;
    private float mLastCenterY;

    private float mLastMoveToCenterX;

    private float mLastRadius;

    public FirstCircle(Paint paint, int startColor) {
        super(paint, startColor);
    }

    @Override
    protected void initParams(RectF bounds) {
        mFirstCenterY = bounds.height() / 2;
        mLastCenterY = bounds.height() / 2 - bounds.height() / 10;

        mLastMoveToCenterX = bounds.left + bounds.width() / 2;

        mLastRadius = bounds.height() * 1.5f;
    }

    @Override
    public void updateParams(float dt) {
        setDraw(DrawableUtils.between(dt, VISIBILITY_FRACTION_START, VISIBILITY_FRACTION_END));
        if (!isDraw()) {
            return;
        }

        if (DrawableUtils.between(dt, MOVE_FRACTION_START, MOVE_FRACTION_END)) {
            float t = DrawableUtils.normalize(dt, MOVE_FRACTION_START, MOVE_FRACTION_END);
            getCenter().x = DrawableUtils.enlarge(getBounds().left, getBounds().left + getBounds().width() / 2 + getRadius(), t);
            mLastMoveRadius = getRadius();
            getCenter().y = DrawableUtils.reduce(mFirstCenterY, mLastCenterY, t);
        } else if (DrawableUtils.between(dt, MOVE_TO_CENTER_FRACTION_START, MOVE_TO_CENTER_FRACTION_END)) {
            float t = DrawableUtils.normalize(dt, MOVE_TO_CENTER_FRACTION_START, MOVE_TO_CENTER_FRACTION_END);
            getCenter().x = DrawableUtils.reduce(getBounds().left + getBounds().width() / 2 + mLastMoveRadius, mLastMoveToCenterX, t);
        }
        setRadius(DrawableUtils.enlarge(0, mLastRadius, dt));
        if (DrawableUtils.between(dt, VISIBILITY_FRACTION_START, MOVE_FRACTION_END)) {
            float t = DrawableUtils.normalize(dt, VISIBILITY_FRACTION_START, MOVE_FRACTION_END);
            setAlpha((int) DrawableUtils.enlarge(50, 255, t));
        }
    }

    @Override
    void initLastFrameParams() {
        getCenter().x = mLastMoveToCenterX;
        getCenter().y = mLastCenterY;
        setRadius(mLastRadius);
        setAlpha(255);
    }

}
