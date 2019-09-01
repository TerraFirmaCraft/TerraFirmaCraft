/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.noise;

/**
 * Created by Stefan Gustavson
 * <a href="http://webstaff.itn.liu.se/~stegu/simplexnoise/">Source</a>
 * Adapted to use {@link INoise2D}
 */
public class SimplexNoise2D extends AbstractSimplexNoise implements INoise2D
{
    private static final float F2 = (float) (Math.sqrt(3.0f) - 1.0f) * 0.5f;
    private static final float G2 = (float) (3.0f - Math.sqrt(3.0f)) / 6.0f;

    private static final Vec2[] VECTORS_2D = {new Vec2(1, 1), new Vec2(-1, 1), new Vec2(1, -1), new Vec2(-1, -1), new Vec2(1, 0), new Vec2(-1, 0), new Vec2(1, 0), new Vec2(-1, 0), new Vec2(0, 1), new Vec2(0, -1), new Vec2(0, 1), new Vec2(0, -1)};

    public SimplexNoise2D(long seed)
    {
        super(seed);
    }

    @Override
    public float noise(float x, float y)
    {
        // Noise contributions from the three corners
        float n0, n1, n2;

        // Skew the input space to determine which simplex cell we're in
        // Hairy factor for 2D
        float s = (x + y) * F2;
        int i = NoiseUtil.fastFloor(x + s);
        int j = NoiseUtil.fastFloor(y + s);
        float t = (i + j) * G2;

        // Unskew the cell origin back to (x,y) space
        float X0 = i - t;
        float Y0 = j - t;
        // The x,y distances from the cell origin
        float x0 = x - X0;
        float y0 = y - Y0;
        // For the 2D case, the simplex shape is an equilateral triangle.
        // Determine which simplex we are in.
        // Offsets for second (middle) corner of simplex in (i,j) coords
        int i1, j1;
        if (x0 > y0)
        {
            // lower triangle, XY order: (0,0)->(1,0)->(1,1)
            i1 = 1;
            j1 = 0;
        }
        else
        {
            // upper triangle, YX order: (0,0)->(0,1)->(1,1)
            i1 = 0;
            j1 = 1;
        }
        // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
        // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
        // c = (3-sqrt(3))/6

        // Offsets for middle corner in (x,y) unskewed coords
        float x1 = x0 - i1 + G2;
        float y1 = y0 - j1 + G2;
        // Offsets for last corner in (x,y) unskewed coords
        float x2 = x0 - 1.0f + 2.0f * G2;
        float y2 = y0 - 1.0f + 2.0f * G2;
        // Work out the hashed gradient indices of the three simplex corners
        int ii = i & 255;
        int jj = j & 255;
        int gi0 = permutationsMod12[ii + permutations[jj]];
        int gi1 = permutationsMod12[ii + i1 + permutations[jj + j1]];
        int gi2 = permutationsMod12[ii + 1 + permutations[jj + 1]];
        // Calculate the contribution from the three corners
        float t0 = 0.5f - x0 * x0 - y0 * y0;
        if (t0 < 0)
        {
            n0 = 0f;
        }
        else
        {
            t0 *= t0;
            n0 = t0 * t0 * VECTORS_2D[gi0].dot(x0, y0);
        }
        float t1 = 0.5f - x1 * x1 - y1 * y1;
        if (t1 < 0)
        {
            n1 = 0f;
        }
        else
        {
            t1 *= t1;
            n1 = t1 * t1 * VECTORS_2D[gi1].dot(x1, y1);
        }
        float t2 = 0.5f - x2 * x2 - y2 * y2;
        if (t2 < 0)
        {
            n2 = 0f;
        }
        else
        {
            t2 *= t2;
            n2 = t2 * t2 * VECTORS_2D[gi2].dot(x2, y2);
        }
        // Add contributions from each corner to get the final noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 70.0f * (n0 + n1 + n2);
    }
}
