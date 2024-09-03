/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.Random;
import net.minecraft.util.Mth;

import net.dries007.tfc.world.BiomeNoiseSampler;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

/**
 * Collections of biome noise factories
 * These are built by hand and assigned to different biomes
 */
public final class BiomeNoise
{
    /**
     * Generates a flat base with twisting carved canyons using many smaller terraces.
     * Inspired by imagery of Drumheller, Alberta
     */
    public static Noise2D badlands(long seed)
    {
        return new OpenSimplex2D(seed)
            .octaves(4)
            .spread(0.025f)
            .scaled(SEA_LEVEL_Y + 22, SEA_LEVEL_Y + 32)
            .add(new OpenSimplex2D(seed + 1)
                .octaves(4)
                .spread(0.04f)
                .ridged()
                .map(x -> 1.3f * -(x > 0 ? x * x * x : 0.5f * x))
                .scaled(-1f, 0.3f, -1f, 1f)
                .terraces(15)
                .scaled(-19.5f, 0)
            )
            .map(x -> x < SEA_LEVEL_Y ? SEA_LEVEL_Y - 0.3f * (SEA_LEVEL_Y - x) : x);
    }

    /**
     * Creates a variant of badlands with stacked pillar like structures, as opposed to relief carved.
     * Inspired by imagery of Bryce Canyon, Utah.
     */
    public static Noise2D bryceCanyon(long seed)
    {
        final Random generator = new Random(seed);

        Noise2D noise = new OpenSimplex2D(generator.nextLong()).octaves(4).spread(0.1f).scaled(SEA_LEVEL_Y + 2, SEA_LEVEL_Y + 14);
        for (int layer = 0; layer < 3; layer++)
        {
            final float threshold = 0.25f;
            final float delta = 0.015f;

            noise = noise.add(new OpenSimplex2D(generator.nextLong())
                .octaves(3)
                .spread(0.02f + 0.01f * layer)
                .abs()
                .affine(1, -0.05f * layer)
                .map(t -> Mth.clampedMap(t, threshold, threshold + delta, 0, 1))
                .lazyProduct(new OpenSimplex2D(generator.nextLong())
                    .octaves(4)
                    .spread(0.1f)
                    .scaled(5, 11)));
        }
        return noise;
    }

    /**
     * Domain warping creates twisting land patterns
     */
    public static Noise2D canyons(long seed, int minHeight, int maxHeight)
    {
        final OpenSimplex2D warp = new OpenSimplex2D(seed).octaves(4).spread(0.03f).scaled(-100f, 100f);
        return new OpenSimplex2D(seed + 1)
            .octaves(4)
            .spread(0.06f)
            .warped(warp)
            .map(x -> x > 0.4 ? x - 0.8f : -x)
            .scaled(-0.4f, 0.8f, SEA_LEVEL_Y + minHeight, SEA_LEVEL_Y + maxHeight);
    }

    /**
     * Simple noise with little variance.
     */
    public static Noise2D hills(long seed, int minHeight, int maxHeight)
    {
        return new OpenSimplex2D(seed).octaves(4).spread(0.05f).scaled(SEA_LEVEL_Y + minHeight, SEA_LEVEL_Y + maxHeight);
    }

    /**
     * Effectively a {@code lerp(noiseA(), noiseB(), piecewise(noiseB()) + noiseC()} with the following additional techniques:
     * <ul>
     *     <li>{@code noiseA} is scaled to outside it's range, then biased towards 1.0, to expose more cliffs, as opposed to hills </li>
     *     <li>{@code piecewise()} is a piecewise linear function that creates cliff shapes from the standard noise distribution.</li>
     *     <li>{@code noiseC} is added on top to provide additional variance (in places where the piecewise function would otherwise flatten areas.</li>
     * </ul>
     */
    public static Noise2D sharpHills(long seed)
    {
        final Noise2D base = new OpenSimplex2D(seed)
            .octaves(4)
            .spread(0.08f);

        final Noise2D lerp = new OpenSimplex2D(seed + 7198234123L)
            .spread(0.013f)
            .scaled(-0.3f, 1.6f)
            .clamped(0, 1);

        final Noise2D lerpMapped = (x, z) -> {
            double in = base.noise(x, z);
            return Mth.lerp(lerp.noise(x, z), in, sharpHillsMap(in));
        };

        final OpenSimplex2D variance = new OpenSimplex2D(seed + 67981832123L)
            .octaves(3)
            .spread(0.06f)
            .scaled(-0.2f, 0.2f);

        return lerpMapped
            .add(variance)
            .scaled(-0.75f, 0.7f, SEA_LEVEL_Y - 3, SEA_LEVEL_Y + 28);
    }

    public static double sharpHillsMap(double in)
    {
        final double in0 = 1.0f, in1 = 0.67f, in2 = 0.15f, in3 = -0.15f, in4 = -0.67f, in5 = -1.0f;
        final double out0 = 1.0f, out1 = 0.7f, out2 = 0.5f, out3 = -0.5f, out4 = -0.7f, out5 = -1.0f;

        if (in > in1)
            return Mth.map(in, in1, in0, out1, out0);
        if (in > in2)
            return Mth.map(in, in2, in1, out2, out1);
        if (in > in3)
            return Mth.map(in, in3, in2, out3, out2);
        if (in > in4)
            return Mth.map(in, in4, in3, out4, out3);
        else
            return Mth.map(in, in5, in4, out5, out4);
    }

    public static Noise2D lake(long seed)
    {
        return new OpenSimplex2D(seed).octaves(4).spread(0.15f).scaled(SEA_LEVEL_Y - 12, SEA_LEVEL_Y + 2)
            .add(new OpenSimplex2D(seed + 1)
                .octaves(5)
                .spread(0.1f)
                .map(val -> val * val * val * val)
                .scaled(-2, 2)
                .clamped(0, 2)
            );
    }

    /**
     * Noise right around sea level which has been flattened, to produce lots of small pockets above and below water
     */
    public static Noise2D lowlands(long seed)
    {
        return hills(seed, -3, -2)
            .add(new OpenSimplex2D(seed + 1)
                .octaves(6)
                .spread(0.55f)
                .scaled(-2, 2)
                .clamped(-2, 1)
            );
    }

    /**
     * Very flat biome
     */
    public static Noise2D flats(long seed)
    {
        return new OpenSimplex2D(seed)
                .octaves(4)
                .spread(0.03f)
                .scaled(SEA_LEVEL_Y - 12, SEA_LEVEL_Y + 8)
                .clamped(SEA_LEVEL_Y, SEA_LEVEL_Y + 2);
    }

    /**
     * Noise just above sea level
     */
    public static Noise2D saltFlats(long seed)
    {
        return new OpenSimplex2D(seed)
            .octaves(4)
            .spread(0.05f)
            .scaled(SEA_LEVEL_Y - 16, SEA_LEVEL_Y + 10)
            .clamped(SEA_LEVEL_Y - 2, SEA_LEVEL_Y);
    }

    public static Noise2D mountains(long seed, int baseHeight, int scaleHeight)
    {
        final Noise2D baseNoise = new OpenSimplex2D(seed) // A simplex noise forms the majority of the base
            .octaves(6) // High octaves to create highly fractal terrain
            .spread(0.14f)
            .add(new OpenSimplex2D(seed + 1) // Ridge noise is added to mimic real mountain ridges. It is scaled smaller than the base noise to not be overpowering
                .octaves(4)
                .spread(0.02f)
                .scaled(-0.7f, 0.7f)
                .ridged() // Ridges are applied after octaves as it creates less directional artifacts this way
            )
            .map(x -> {
                final double x0 = 0.125f * (x + 1) * (x + 1) * (x + 1); // Power scaled, flattens most areas but maximizes peaks
                return SEA_LEVEL_Y + baseHeight + scaleHeight * x0; // Scale the entire thing to mountain ranges
            });

        // Cliff noise consists of noise that's been artificially clamped over half the domain, which is then selectively added above a base height level
        // This matches up with the distinction between dirt and stone
        final Noise2D cliffNoise = new OpenSimplex2D(seed + 2).octaves(2).spread(0.01f).scaled(-25, 25).map(x -> x > 0 ? x : 0);
        final Noise2D cliffHeightNoise = new OpenSimplex2D(seed + 3).octaves(2).spread(0.01f).scaled(140 - 20, 140 + 20);

        return (x, z) -> {
            double height = baseNoise.noise(x, z);
            if (height > 120) // Only sample each cliff noise layer if the base noise could be influenced by it
            {
                final double cliffHeight = cliffHeightNoise.noise(x, z) - height;
                if (cliffHeight < 0)
                {
                    final double mappedCliffHeight = Mth.clampedMap(cliffHeight, 0, -1, 0, 1);
                    height += mappedCliffHeight * cliffNoise.noise(x, z);
                }
            }
            return height;
        };
    }

    /**
     * Uses domain warping to achieve a swirly hills effect
     */
    public static Noise2D ocean(long seed, int depthMin, int depthMax)
    {
        final OpenSimplex2D warp = new OpenSimplex2D(seed).octaves(2).spread(0.015f).scaled(-30, 30);
        return new OpenSimplex2D(seed + 1)
            .octaves(4)
            .spread(0.11f)
            .scaled(SEA_LEVEL_Y + depthMin, SEA_LEVEL_Y + depthMax)
            .warped(warp);
    }

    /**
     * Applies elements from deep ocean and badlands.
     * Inverse power scaled ridge noise (cubic) is used to create ridges, inside the domain warped ocean noise
     */
    public static Noise2D oceanRidge(long seed, int depthMin, int depthMax)
    {
        final OpenSimplex2D warp = new OpenSimplex2D(seed).octaves(2).spread(0.015f).scaled(-30, 30);
        final Noise2D ridgeNoise = new OpenSimplex2D(seed + 1).octaves(4).spread(0.015f).ridged().map(x -> { // In [-1, 1]
            if (x > -0.3f)
            {
                x = (x + 0.3f) / 1.3f;  // In [0, 1]
                x = x * x * x; // Power scaled
                return -16f * x; // In [0, -16]
            }
            return 0; // No modifications outside of ridge area
        });
        return new OpenSimplex2D(seed + 2).octaves(4).spread(0.11f).scaled(SEA_LEVEL_Y + depthMin, SEA_LEVEL_Y + depthMax).add(ridgeNoise).warped(warp);
    }

    public static Noise2D shore(long seed)
    {
        return new OpenSimplex2D(seed).octaves(4).spread(0.17f).scaled(SEA_LEVEL_Y, SEA_LEVEL_Y + 5f);
    }

    public static Noise2D tidalFlats(long seed)
    {
        return new OpenSimplex2D(seed).octaves(4).spread(0.17f).scaled(SEA_LEVEL_Y, SEA_LEVEL_Y + 1.8f);
    }

    /**
     * Adds volcanoes to a base noise height map
     */
    public static Noise2D addVolcanoes(long seed, Noise2D baseNoise, int rarity, int baseVolcanoHeight, int scaleVolcanoHeight)
    {
        final VolcanoNoise volcanoes = new VolcanoNoise(seed);
        return (x, z) -> volcanoes.modifyHeight(x, z, baseNoise.noise(x, z), rarity, baseVolcanoHeight, scaleVolcanoHeight);
    }

    public static BiomeNoiseSampler undergroundLakes(long seed, Noise2D heightNoise)
    {
        final Noise2D blobsNoise = new OpenSimplex2D(seed + 1).spread(0.04f).abs();
        final Noise2D depthNoise = new OpenSimplex2D(seed + 2).octaves(4).scaled(2, 18).spread(0.2f);
        final Noise2D centerNoise = new OpenSimplex2D(seed + 3).octaves(2).spread(0.06f).scaled(SEA_LEVEL_Y - 4, SEA_LEVEL_Y + 4);

        return new BiomeNoiseSampler()
        {
            private double surfaceHeight, center, height;

            @Override
            public void setColumn(int x, int z)
            {
                double h0 = Mth.clamp((0.7f - blobsNoise.noise(x, z)) * (1 / 0.3f), 0, 1);
                double h1 = depthNoise.noise(x, z);

                surfaceHeight = heightNoise.noise(x, z);
                center = centerNoise.noise(x, z);
                height = h0 * h1;
            }

            @Override
            public double height()
            {
                return surfaceHeight;
            }

            @Override
            public double noise(int y)
            {
                double delta = Math.abs(center - y);
                return Mth.clamp(0.4f + 0.05f * (height - delta), 0, 1);
            }
        };
    }
}
