/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.noise;

/**
 * Created by Stefan Gustavson
 * <a href="http://webstaff.itn.liu.se/~stegu/simplexnoise/">Source</a>
 * Adapted to use {@link INoise3D}
 */
public class SimplexNoise3D extends AbstractSimplexNoise implements INoise3D
{
    private static final float F3 = 1.0f / 3.0f;
    private static final float G3 = 1.0f / 6.0f;

    private static final Vec3[] VECTORS_3D = {new Vec3(1, 1, 0), new Vec3(-1, 1, 0), new Vec3(1, -1, 0), new Vec3(-1, -1, 0),
        new Vec3(1, 0, 1), new Vec3(-1, 0, 1), new Vec3(1, 0, -1), new Vec3(-1, 0, -1),
        new Vec3(0, 1, 1), new Vec3(0, -1, 1), new Vec3(0, 1, -1), new Vec3(0, -1, -1)};

    public SimplexNoise3D(long seed)
    {
        super(seed);
    }

    @Override
    public float noise(float x, float y, float z)
    {
        // Noise contributions from the four corners
        float n0, n1, n2, n3;
        // Skew the input space to determine which simplex cell we're in
        float s = (x + y + z) * F3;
        int i = NoiseUtil.fastFloor(x + s);
        int j = NoiseUtil.fastFloor(y + s);
        int k = NoiseUtil.fastFloor(z + s);

        // Unskew the cell origin back to (x,y,z) space
        float t = (i + j + k) * G3;
        float X0 = i - t;
        float Y0 = j - t;
        float Z0 = k - t;

        // The x,y,z distances from the cell origin
        float x0 = x - X0;
        float y0 = y - Y0;
        float z0 = z - Z0;

        // For the 3D case, the simplex shape is a slightly irregular tetrahedron. First determine which simplex we are in
        int i1, j1, k1; // Offsets for second corner of simplex in (i,j,k) coords
        int i2, j2, k2; // Offsets for third corner of simplex in (i,j,k) coords
        if (x0 >= y0)
        {
            if (y0 >= z0)
            {
                // X Y Z order
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
            else if (x0 >= z0)
            {
                // X Z Y order
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
            else
            {
                // Z X Y order
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        }
        else // if x0 < y0
        {
            if (y0 < z0)
            {
                // Z Y X order
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            }
            else if (x0 < z0)
            {
                // Y Z X order
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            }
            else
            {
                // Y X Z order
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
        }
        // (i, j, k) unit vectors in (x, y, z): e1 = (1-c,-c,-c), e2 = (-c,1-c,-c), e3 = (-c,-c,1-c)
        // Offsets for second corner in (x,y,z) coords
        float x1 = x0 - i1 + G3;
        float y1 = y0 - j1 + G3;
        float z1 = z0 - k1 + G3;

        // Offsets for third corner in (x,y,z) coords
        float x2 = x0 - i2 + 2.0f * G3;
        float y2 = y0 - j2 + 2.0f * G3;
        float z2 = z0 - k2 + 2.0f * G3;

        // Offsets for last corner in (x,y,z) coords
        float x3 = x0 - 1.0f + 3.0f * G3;
        float y3 = y0 - 1.0f + 3.0f * G3;
        float z3 = z0 - 1.0f + 3.0f * G3;

        // Work out the hashed gradient indices of the four simplex corners
        int ii = i & 255;
        int jj = j & 255;
        int kk = k & 255;
        int gi0 = permutationsMod12[ii + permutations[jj + permutations[kk]]];
        int gi1 = permutationsMod12[ii + i1 + permutations[jj + j1 + permutations[kk + k1]]];
        int gi2 = permutationsMod12[ii + i2 + permutations[jj + j2 + permutations[kk + k2]]];
        int gi3 = permutationsMod12[ii + 1 + permutations[jj + 1 + permutations[kk + 1]]];

        // Calculate the contribution from the four corners
        float t0 = 0.6f - x0 * x0 - y0 * y0 - z0 * z0;
        if (t0 < 0)
        {
            n0 = 0f;
        }
        else
        {
            t0 *= t0;
            n0 = t0 * t0 * VECTORS_3D[gi0].dot(x0, y0, z0);
        }
        float t1 = 0.6f - x1 * x1 - y1 * y1 - z1 * z1;
        if (t1 < 0)
        {
            n1 = 0f;
        }
        else
        {
            t1 *= t1;
            n1 = t1 * t1 * VECTORS_3D[gi1].dot(x1, y1, z1);
        }
        float t2 = 0.6f - x2 * x2 - y2 * y2 - z2 * z2;
        if (t2 < 0)
        {
            n2 = 0f;
        }
        else
        {
            t2 *= t2;
            n2 = t2 * t2 * VECTORS_3D[gi2].dot(x2, y2, z2);
        }
        float t3 = 0.6f - x3 * x3 - y3 * y3 - z3 * z3;
        if (t3 < 0)
        {
            n3 = 0f;
        }
        else
        {
            t3 *= t3;
            n3 = t3 * t3 * VECTORS_3D[gi3].dot(x3, y3, z3);
        }
        // Add contributions from each corner to get the final noise value.
        // The result is scaled to stay just inside [-1,1]
        return 32.0f * (n0 + n1 + n2 + n3);
    }
}
