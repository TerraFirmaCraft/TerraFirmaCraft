/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.river;

import com.google.common.base.Preconditions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.RandomSource;

public class MidpointFractal
{
    private static final float JITTER_MIN = 0.2f;
    private static final float JITTER_RANGE = 2 * JITTER_MIN;

    private static final int MAX_BISECTIONS = 10;
    private static final float[] ENCOMPASSING_RANGES = new float[MAX_BISECTIONS];

    static
    {
        // All values are to be interpreted as linear functions of the initial inf-norm (n)
        float sqrt2 = (float) Math.sqrt(2);
        float delta = 0, midpointDelta = 0, prevMidpoint, norm = 1;
        for (int i = 0; i < MAX_BISECTIONS; i++)
        {
            ENCOMPASSING_RANGES[i] = Math.max(delta, midpointDelta);

            // Calculate the new midpoint, then the maximum two deltas will contain the midpoint, and max(delta, oldMidpoint)
            prevMidpoint = midpointDelta;
            midpointDelta = 0.5f * (delta + midpointDelta) + sqrt2 * JITTER_MIN * norm;
            delta = Math.max(prevMidpoint, delta);

            // Worst case reduction in the norm
            norm *= 0.5f + JITTER_MIN;
        }
    }

    /**
     * Performs {@code bisections} iterations of the line-bisection fractal algorithm.
     * The segments, both provided and returned, are a sequence of (x0, y0, x1, y1, ... xN, yN)
     */
    private static float[] bisect(RandomSource random, int bisections, float[] segments)
    {
        for (int i = 0; i < bisections; i++)
        {
            float[] splitSegments = new float[(segments.length << 1) - 2];

            // First point
            splitSegments[0] = segments[0];
            splitSegments[1] = segments[1];

            int splitIndex = 2;
            for (int index = 0; index < segments.length - 2; index += 2)
            {
                // Bisect the segment from [source, source + 1]
                // The first source should already be in the array, we need to set the bisection point and the drain

                float sourceX = segments[index];
                float sourceY = segments[index + 1];
                float drainX = segments[index + 2];
                float drainY = segments[index + 3];

                float norm = RiverHelpers.normInf(sourceX - drainX, sourceY - drainY);

                // Bisect at the midpoint, plus some variance scaled by the inf-norm of the line segment
                float bisectX = (random.nextFloat() * JITTER_RANGE - JITTER_MIN) * norm + (sourceX + drainX) * 0.5f;
                float bisectY = (random.nextFloat() * JITTER_RANGE - JITTER_MIN) * norm + (sourceY + drainY) * 0.5f;

                // Copy the new split segments
                splitSegments[splitIndex] = bisectX;
                splitSegments[splitIndex + 1] = bisectY;
                splitSegments[splitIndex + 2] = drainX;
                splitSegments[splitIndex + 3] = drainY;

                splitIndex += 4;
            }

            segments = splitSegments;
        }
        return segments;
    }

    public final float[] segments;
    private final float norm;

    public MidpointFractal(RandomSource random, int bisections, float sourceX, float sourceY, float drainX, float drainY)
    {
        Preconditions.checkArgument(bisections >= 0 && bisections < MAX_BISECTIONS, "Bisections must be within [0, MAX_BISECTIONS)");

        this.segments = bisect(random, bisections, new float[] {sourceX, sourceY, drainX, drainY});
        this.norm = ENCOMPASSING_RANGES[bisections] * RiverHelpers.normInf(sourceX - drainX, sourceY - drainY);
    }

    /**
     * Checks if a given point (x, y) comes within a minimum {@code distance} of the bounding box of the fractal.
     * Faster and more efficient than checking {@link #intersect(float, float, float)}.
     */
    public boolean maybeIntersect(float x, float y, float distance)
    {
        float d = RiverHelpers.distancePointToLineSq(segments[0], segments[1], segments[segments.length - 2], segments[segments.length - 1], x, y);
        float t = distance + norm;
        return d <= t * t;
    }

    /**
     * @return {@code true} the provided point (x, y) comes within a minimum {@code distance} of the fractal.
     */
    public boolean intersect(float x, float y, float distance)
    {
        return intersectIndex(x, y, distance * distance) != -1;
    }

    /**
     * Checks if the provided point (x, y) comes within a minimum {@code distance} of the fractal.
     *
     * @return A vector describing the flow of the river at the intersected location, if found.
     */
    public Flow intersectWithFlow(float x, float y, float distance)
    {
        final int i = intersectIndex(x, y, distance * distance);
        if (i != -1)
        {
            float sourceX = segments[i], sourceY = segments[i + 1], drainX = segments[i + 2], drainY = segments[i + 3];
            float angle = (float) Mth.atan2(-(drainY - sourceY), drainX - sourceX);
            return Flow.fromAngle(angle);
        }
        return Flow.NONE;
    }

    private int intersectIndex(float x, float y, float distSq)
    {
        for (int i = 0; i < segments.length - 2; i += 2)
        {
            float d = RiverHelpers.distancePointToLineSq(segments[i], segments[i + 1], segments[i + 2], segments[i + 3], x, y);
            if (d < distSq)
            {
                return i;
            }
        }
        return -1;
    }
}
