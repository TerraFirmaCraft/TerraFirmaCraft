/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.river;

import com.google.common.base.Preconditions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class MidpointFractal
{
    private static final double JITTER_MAX = 0.2f;
    private static final double JITTER_MIN = 0.05f;

    private static final int MAX_BISECTIONS = 10;
    private static final double[] ENCOMPASSING_RANGES = new double[MAX_BISECTIONS];

    static
    {
        // All values are to be interpreted as linear functions of the initial inf-norm (n)
        double sqrt2 = (double) Math.sqrt(2);
        double delta = 0, midpointDelta = 0, prevMidpoint, norm = 1;
        for (int i = 0; i < MAX_BISECTIONS; i++)
        {
            ENCOMPASSING_RANGES[i] = Math.max(delta, midpointDelta);

            // Calculate the new midpoint, then the maximum two deltas will contain the midpoint, and max(delta, oldMidpoint)
            prevMidpoint = midpointDelta;
            midpointDelta = 0.5f * (delta + midpointDelta) + sqrt2 * JITTER_MAX * norm;
            delta = Math.max(prevMidpoint, delta);

            // Worst case reduction in the norm
            norm *= 0.5f + JITTER_MAX;
        }
    }

    /**
     * Performs {@code bisections} iterations of the line-bisection fractal algorithm.
     * The segments, both provided and returned, are a sequence of (x0, y0, x1, y1, ... xN, yN)
     */
    private static double[] bisect(RandomSource random, int bisections, double[] segments)
    {
        for (int i = 0; i < bisections; i++)
        {
            double[] splitSegments = new double[(segments.length << 1) - 2];

            // First point
            splitSegments[0] = segments[0];
            splitSegments[1] = segments[1];

            int splitIndex = 2;
            for (int index = 0; index < segments.length - 2; index += 2)
            {
                // Bisect the segment from [source, source + 1]
                // The first source should already be in the array, we need to set the bisection point and the drain

                double sourceX = segments[index];
                double sourceY = segments[index + 1];
                double drainX = segments[index + 2];
                double drainY = segments[index + 3];

                double norm = RiverHelpers.normInf(sourceX - drainX, sourceY - drainY);

                // Bisect at the midpoint, plus some variance scaled by the inf-norm of the line segment
                double bisectX = randomJitter(random) * norm + (sourceX + drainX) * 0.5f;
                double bisectY = randomJitter(random) * norm + (sourceY + drainY) * 0.5f;

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

    /** Return a value x in [-JITTER_MAX, JITTER_MAX] s.t. |x| > JITTER_MIN */
    private static double randomJitter(RandomSource random)
    {
        final double value = 2f * random.nextDouble() - 1; // In [0, 1]
        return (JITTER_MAX - JITTER_MIN) * value + (value < 0 ? -JITTER_MIN : JITTER_MIN);
    }

    public final double[] segments;
    private final double norm;

    public MidpointFractal(RandomSource random, int bisections, double sourceX, double sourceY, double drainX, double drainY)
    {
        Preconditions.checkArgument(bisections >= 0 && bisections < MAX_BISECTIONS, "Bisections must be within [0, MAX_BISECTIONS)");

        this.segments = bisect(random, bisections, new double[] {sourceX, sourceY, drainX, drainY});
        this.norm = ENCOMPASSING_RANGES[bisections] * RiverHelpers.normInf(sourceX - drainX, sourceY - drainY);
    }

    /**
     * Checks if a given point (x, y) comes within a minimum {@code distance} of the bounding box of the fractal, using a heuristic to estimate
     * if this is remotely possible. This is an overestimation vs {@link #intersect(double, double, double)}, and is much faster to compute.
     */
    public boolean maybeIntersect(double x, double y, double distance)
    {
        final double d = RiverHelpers.distancePointToLineSq(segments[0], segments[1], segments[segments.length - 2], segments[segments.length - 1], x, y);
        final double t = distance + norm;
        return d <= t * t;
    }

    /**
     * @return {@code true} the provided point (x, y) comes within a minimum {@code distance} of the fractal.
     */
    public boolean intersect(double x, double y, double distance)
    {
        return maybeIntersect(x, y, distance) && intersectIndex(x, y, distance * distance) != -1;
    }

    /**
     * @return The square distance to the nearest river segment.
     */
    public double intersectDistance(double x, double y)
    {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < segments.length - 2; i += 2)
        {
            double d = RiverHelpers.distancePointToLineSq(segments[i], segments[i + 1], segments[i + 2], segments[i + 3], x, y);
            if (d < min)
            {
                min = d;
            }
        }
        return min;
    }

    /**
     * @return The best approximation of the flow near a point to this edge. Note that this will not return {@code Flow.NONE}
     * if a suitable flow cannot be found, rather, it needs to be pre-tested that this edge is the nearest edge to the target point.
     */
    public Flow calculateFlow(double x, double y)
    {
        double min = Double.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < segments.length - 2; i += 2)
        {
            double d = RiverHelpers.distancePointToLineSq(segments[i], segments[i + 1], segments[i + 2], segments[i + 3], x, y);
            if (d < min)
            {
                min = d;
                minIndex = i;
            }
        }
        final double sourceX = segments[minIndex], sourceY = segments[minIndex + 1], drainX = segments[minIndex + 2], drainY = segments[minIndex + 3];
        final double angle = Mth.atan2(-(drainY - sourceY), drainX - sourceX);
        return Flow.fromAngle(angle);
    }

    private int intersectIndex(double x, double y, double distSq)
    {
        for (int i = 0; i < segments.length - 2; i += 2)
        {
            double d = RiverHelpers.distancePointToLineSq(segments[i], segments[i + 1], segments[i + 2], segments[i + 3], x, y);
            if (d < distSq)
            {
                return i;
            }
        }
        return -1;
    }
}
