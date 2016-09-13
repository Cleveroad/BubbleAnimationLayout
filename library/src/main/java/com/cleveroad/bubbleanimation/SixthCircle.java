package com.cleveroad.bubbleanimation;

import android.graphics.Paint;
import android.graphics.RectF;

class SixthCircle extends CircleDrawable {

    private static final float VISIBILITY_FRACTION_START = 88 * Constants.FRAME_SPEED;
    private static final float VISIBILITY_FRACTION_END = Constants.TOTAL_FRAMES * Constants.FRAME_SPEED;

    private static final float MOVE_FRACTION_START = VISIBILITY_FRACTION_START;
    private static final float MOVE_FRACTION_END = MOVE_FRACTION_START + 48 * Constants.FRAME_SPEED;

    private float mMoveCenterY;

    private float mCenterX;

    private float mRadius;

    public SixthCircle(Paint paint, int startColor) {
        super(paint, startColor);
    }

    @Override
    void initParams(RectF bounds) {
        float fourthCircleCenterX = bounds.left + bounds.width() / 1.1f;
        float fourthCircleCenterY = bounds.height() / 2;
        float fourthCircleRadius = Math.max(bounds.width(), bounds.height()) / 7 * 4;

        mMoveCenterY = bounds.height() / 1.8f;
        float topX = (float) ((2 * fourthCircleCenterX - Math.sqrt(4 * fourthCircleCenterX * fourthCircleCenterX - 4 * (fourthCircleCenterX * fourthCircleCenterX + fourthCircleCenterY * fourthCircleCenterY - fourthCircleRadius * fourthCircleRadius))) / 2.0f);
        mCenterX = topX / 4.0f;

        float distanceToLeftTop = (float) Math.sqrt(mCenterX * mCenterX + mMoveCenterY * mMoveCenterY);
        float distanceToTopX = (float) Math.sqrt((mCenterX - topX) * (mCenterX - topX) + mMoveCenterY * mMoveCenterY);
        mRadius = Math.max(distanceToLeftTop, distanceToTopX);
    }

    @Override
    public void updateParams(float dt) {
        setDraw(DrawableUtils.between(dt, VISIBILITY_FRACTION_START, VISIBILITY_FRACTION_END));
        if (!isDraw()) {
            return;
        }
        if (DrawableUtils.between(dt, MOVE_FRACTION_START, MOVE_FRACTION_END)) {
            float t = DrawableUtils.normalize(dt, MOVE_FRACTION_START, MOVE_FRACTION_END);
            getCenter().x = DrawableUtils.enlarge(getBounds().left, mCenterX, t);
            getCenter().y = mMoveCenterY;
            setAlpha((int) DrawableUtils.enlarge(50, 255, t));
        }
        float t = DrawableUtils.normalize(dt, VISIBILITY_FRACTION_START, VISIBILITY_FRACTION_END);
        float maxValue = Math.max(getBounds().width(), getBounds().height());
        setRadius(DrawableUtils.enlarge(maxValue / 10.0f, mRadius, t));
    }

    @Override
    void initLastFrameParams() {
        getCenter().x = mCenterX;
        getCenter().y = mMoveCenterY;
        setRadius(mRadius);
        setAlpha(255);
    }
}
