package net.dries007.tfc.tests;

import net.dries007.tfc.ImageUtil;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;
import org.junit.jupiter.api.Test;

class MountainsNoiseTests
{
    private static final ImageUtil<INoise2D> IMAGES = ImageUtil.noise(noise -> (x, y) -> noise.noise((float) x, (float) y), builder -> builder.scale(ImageUtil.Scales.DYNAMIC_RANGE).color(ImageUtil.Colors.LINEAR_BLUE_RED).size(1000).dimensions(1000));
    private static final long seed = System.currentTimeMillis();

    float power(float in)
    {
        return 0.125f * (in + 1) * (in + 1) * (in + 1);
    }

    @Test
    void testMountainsNoise1()
    {
        IMAGES.draw("mountains_1", new SimplexNoise2D(seed).octaves(6).spread(0.14f).map(this::power));
    }

    @Test
    void testRidgeNoise1()
    {
        IMAGES.draw("ridge_1", new SimplexNoise2D(seed).ridged().octaves(4).spread(0.02f));
    }

    @Test
    void testRidgeNoise2()
    {
        IMAGES.draw("ridge_2", new SimplexNoise2D(seed).octaves(4).ridged().spread(0.02f));
    }

    @Test
    void testRidgeMountainsNoise1()
    {
        final INoise2D baseNoise = new SimplexNoise2D(seed).octaves(6).spread(0.14f);
        final INoise2D ridgeNoise = new SimplexNoise2D(seed + 1).octaves(4).ridged().spread(0.02f).scaled(-0.5f, 0.5f);
        final INoise2D composited = baseNoise.add(ridgeNoise);
        IMAGES.draw("ridge_mountains_1", composited.map(this::power));
    }

    @Test
    void testRidgeMountainsNoise2()
    {
        // High octave simplex noise forms a base height map for the mountains
        final INoise2D baseNoise = new SimplexNoise2D(seed).octaves(6).spread(0.14f);

        // Ridge noise is added to create mountain ridges. This both functions as more octaves (thus higher range) and as variation
        final INoise2D ridgeNoise = new SimplexNoise2D(seed + 1).octaves(4).ridged().spread(0.02f).scaled(-0.7f, 0.7f);

        // Add the base and ridge noise, and power scale it. This flattens lower areas, and creates higher peaks in select areas
        final INoise2D composited = baseNoise.add(ridgeNoise).map(this::power);

        // Cliff noise consists of noise that's been artificially clamped over half the domain, which is then selectively added above a base height level
        final INoise2D cliffNoise = new SimplexNoise2D(seed + 2).octaves(2).map(x -> x > 0 ? x : 0).spread(0.01f).scaled(-0.6f, 0.6f);
        final INoise2D cliffHeightNoise = new SimplexNoise2D(seed + 3).octaves(2).spread(0.01f).scaled(-0.1f, 0.1f);

        // Add the cliff noise based on the current height, and an additional cliff start height noise
        final INoise2D result = (x, z) -> {
            // Only sample noise if necessary
            float height = composited.noise(x, z);
            if (height > 0.5)
            {
                float cliffHeight = cliffHeightNoise.noise(x, z);
                if (height > 0.6 + cliffHeight)
                {
                    float cliff = cliffNoise.noise(x, z);
                    return height + cliff;
                }
            }
            return height;
        };

        // todo: test this with actual mountain biomes
        // todo: values to adjust: the height at which cliffs appear (line this up with natural stone-gravel boundaries, the height contribution of cliffs, ridges, and base noise.
        IMAGES.draw("ridge_mountains_2", result);
    }

    @Test
    void testCliffBaseNoise()
    {
        IMAGES.draw("cliff_base", new SimplexNoise2D(seed).octaves(2).map(x -> x > 0 ? x : 0).spread(0.004f));
    }

    @Test
    void testCliffRidgedBaseNoise()
    {
        IMAGES.draw("ridged_cliff_base", new SimplexNoise2D(seed).octaves(2).ridged().map(x -> x > 0 ? x : 0).spread(0.004f));
    }

    @Test
    void testCliffHeightNoise()
    {
        IMAGES.draw("cliff_height", new SimplexNoise2D(seed).octaves(3).spread(0.01f));
    }

    @Test
    void testCliffNoise1()
    {
        final INoise2D baseNoise = new SimplexNoise2D(seed).octaves(2).map(x -> x > 0 ? x : 0).spread(0.01f);
        final INoise2D heightNoise = new SimplexNoise2D(seed).octaves(3).spread(0.025f);
        IMAGES.draw("cliff_noise_1", (x, z) -> {
            float base = baseNoise.noise(x, z);
            float height = heightNoise.noise(x, z);
            if (height > 0.05)
            {
                return base;
            }
            return 0;
        });
    }
}
