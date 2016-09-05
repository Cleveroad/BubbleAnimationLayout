package com.cleveroad.bubbleanimation;

import android.graphics.Paint;
import android.graphics.RectF;

class FourthCircle extends CircleDrawable {

    private static final float VISIBILITY_FRACTION_START = 52 * Constants.FRAME_SPEED;
    private static final float VISIBILITY_FRACTION_END = Constants.TOTAL_FRAMES * Constants.FRAME_SPEED;

    private static final float MOVE_FRACTION_START = VISIBILITY_FRACTION_START;
    private static final float MOVE_FRACTION_END = MOVE_FRACTION_START + 32 * Constants.FRAME_SPEED;

    private static final float MOVE_CENTER_FRACTION_START = MOVE_FRACTION_END;
    private static final float MOVE_CENTER_FRACTION_END = MOVE_CENTER_FRACTION_START + 8 * Constants.FRAME_SPEED;

    private float mLastMoveX;
    private float mMoveY;
    private float mRadius;

    private float mFirstMoveToCenterX;
    private float mLastMoveToCenterX;

    private float mFirstMoveToCenterY;
    private float mLastMoveToCenterY;

    public FourthCircle(Paint paint, int startColor) {
        super(paint, startColor);
    }

    @Override
    void initParams(RectF bounds) {
        mLastMoveX = bounds.left + bounds.width() / 1.2f;
        mMoveY = bounds.height() / 1.4f;
        mRadius = Math.max(bounds.width(), bounds.height()) / 7 * 4;

        mFirstMoveToCenterX = bounds.left + bounds.width() / 1.2f;
        mLastMoveToCenterX = bounds.left + bounds.width() / 1.1f;

        mFirstMoveToCenterY = bounds.height() / 1.4f;
        mLastMoveToCenterY = bounds.height() / 2;
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
            getCenter().y = mMoveY;
            setRadius(mRadius);
            setAlpha((int) DrawableUtils.enlarge(50, 255, t));
        } else if (DrawableUtils.between(dt, MOVE_CENTER_FRACTION_START, MOVE_CENTER_FRACTION_END)) {
            float t = DrawableUtils.normalize(dt, MOVE_CENTER_FRACTION_START, MOVE_CENTER_FRACTION_END);
            getCenter().x = DrawableUtils.enlarge(mFirstMoveToCenterX, mLastMoveToCenterX, t);
            getCenter().y = DrawableUtils.reduce(mFirstMoveToCenterY, mLastMoveToCenterY, t);
        }
    }

    @Override
    void initLastFrameParams() {
        getCenter().x = mLastMoveToCenterX;
        getCenter().y = mLastMoveToCenterY;
        setRadius(mRadius);
        setAlpha(255);
    }

}
