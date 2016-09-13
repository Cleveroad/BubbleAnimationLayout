package com.cleveroad.bubbleanimation;

/**
 * Helpful utils class.
 */
class DrawableUtils {

    private DrawableUtils() {
    }

    /**
     * Normalize value between minimum and maximum.
     *
     * @param val    value
     * @param minVal minimum value
     * @param maxVal maximum value
     * @return normalized value in range <code>0..1</code>
     * @throws IllegalArgumentException if value is out of range <code>[minVal, maxVal]</code>
     */
    public static float normalize(float val, float minVal, float maxVal) {
        if (val < minVal || val > maxVal)
            throw new IllegalArgumentException("Value must be between min and max values. [val, min, max]: [" + val + "," + minVal + ", " + maxVal + "]");
        return (val - minVal) / (maxVal - minVal);
    }

    /**
     * Checks if value belongs to range <code>[start, end]</code>
     *
     * @param value value
     * @param start start of range
     * @param end   end of range
     * @return true if value belogs to range, false otherwise
     */
    public static boolean between(float value, float start, float end) {
        if (start > end) {
            float tmp = start;
            start = end;
            end = tmp;
        }
        return value >= start && value <= end;
    }

    /**
     * Enlarge value from startValue to endValue
     *
     * @param startValue start size
     * @param endValue   end size
     * @param time       time of animation
     * @return new size value
     */
    public static float enlarge(float startValue, float endValue, float time) {
        if (startValue > endValue)
            throw new IllegalArgumentException("Start size can't be larger than end size.");
        return startValue + (endValue - startValue) * time;
    }

    /**
     * Reduce value from startValue to endValue
     *
     * @param startValue start size
     * @param endValue   end size
     * @param time       time of animation
     * @return new size value
     */
    public static float reduce(float startValue, float endValue, float time) {
        if (startValue < endValue)
            throw new IllegalArgumentException("End size can't be larger than start size.");
        return endValue + (startValue - endValue) * (1 - time);
    }
}
