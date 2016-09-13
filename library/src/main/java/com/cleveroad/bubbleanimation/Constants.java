package com.cleveroad.bubbleanimation;

final class Constants {

    public static final float DEFAULT_SPEED_COEFFICIENT = 1f;
    //    real frame = 24, total frames = 24 * 8
    public static final int TOTAL_FRAMES = 192;
    public static final int MS_PER_FRAME = 5;
    public static final int TOTAL_DURATION = TOTAL_FRAMES * MS_PER_FRAME;
    public static final float FRAME_SPEED = 1f * MS_PER_FRAME / TOTAL_DURATION;

    private Constants() {
    }

}
