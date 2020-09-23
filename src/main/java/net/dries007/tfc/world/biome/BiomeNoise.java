package net.dries007.tfc.world.biome;

import java.util.function.LongFunction;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

/**
 * Collections of biome noise factories
 * These are built by hand and assigned to different biomes
 */
public class BiomeNoise
{
    /**
     * A flat base, with inverse exponential scaled ridge noise subtracted from it
     * Relief carving generates twisting canyons similar to vanilla mesas
     */
    public static INoise2D badlands(long seed)
    {
        final int seaLevel = TFCChunkGenerator.SEA_LEVEL;
        return new SimplexNoise2D(seed)
            .octaves(6)
            .spread(0.08f)
            .scaled(seaLevel + 22, seaLevel + 32)
            .add(new SimplexNoise2D(seed + 1)
                .octaves(4)
                .ridged()
                .spread(0.04f)
                .map(x -> 1.3f * -(x > 0 ? x * x * x : 0.5f * x))
                .scaled(-1f, 0.3f, -1f, 1f)
                .terraces(17)
                .scaled(-22, 0)
            )
            .map(x -> x < seaLevel ? seaLevel - 0.3f * (seaLevel - x) : x);
    }

    /**
     * Domain warping creates twisting land patterns
     */
    public static INoise2D canyons(long seed, int minHeight, int maxHeight)
    {
        final INoise2D warpX = new SimplexNoise2D(seed).octaves(4).spread(0.1f).scaled(-30, 30);
        final INoise2D warpZ = new SimplexNoise2D(seed + 1).octaves(4).spread(0.1f).scaled(-30, 30);
        return new SimplexNoise2D(seed).octaves(4).spread(0.2f).warped(warpX, warpZ).map(x -> x > 0.4 ? x - 0.8f : -x).scaled(-0.4f, 0.8f, TFCChunkGenerator.SEA_LEVEL + minHeight, TFCChunkGenerator.SEA_LEVEL + maxHeight).spread(0.3f);
    }

    /**
     * Simple noise with little variance.
     */
    public static INoise2D simple(long seed, int minHeight, int maxHeight)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.05f).scaled(TFCChunkGenerator.SEA_LEVEL + minHeight, TFCChunkGenerator.SEA_LEVEL + maxHeight);
    }

    public static INoise2D lake(long seed)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.15f).scaled(TFCChunkGenerator.SEA_LEVEL - 12, TFCChunkGenerator.SEA_LEVEL - 2);
    }

    /**
     * Noise right around sea level which has been flattened, to produce lots of small pockets above and below water
     */
    public static INoise2D lowlands(long seed)
    {
        return new SimplexNoise2D(seed).octaves(6).spread(0.55f).scaled(TFCChunkGenerator.SEA_LEVEL - 6, TFCChunkGenerator.SEA_LEVEL + 7).flattened(TFCChunkGenerator.SEA_LEVEL - 4, TFCChunkGenerator.SEA_LEVEL + 3);
    }

    public static INoise2D mountains(long seed, int baseHeight, int scaleHeight)
    {
        final int seaLevel = TFCChunkGenerator.SEA_LEVEL;
        final INoise2D baseNoise = new SimplexNoise2D(seed) // A simplex noise forms the majority of the base
            .octaves(6) // High octaves to create highly fractal terrain
            .spread(0.14f)
            .add(new SimplexNoise2D(seed + 1) // Ridge noise is added to mimic real mountain ridges. It is scaled smaller than the base noise to not be overpowering
                .octaves(4)
                .ridged() // Ridges are applied after octaves as it creates less directional artifacts this way
                .spread(0.02f)
                .scaled(-0.7f, 0.7f))
            .map(x -> 0.125f * (x + 1) * (x + 1) * (x + 1)) // Power scaled, flattens most areas but maximizes peaks
            .map(x -> seaLevel + baseHeight + scaleHeight * x); // Scale the entire thing to mountain ranges

        // Cliff noise consists of noise that's been artificially clamped over half the domain, which is then selectively added above a base height level
        // This matches up with the distinction between dirt and stone
        final INoise2D cliffNoise = new SimplexNoise2D(seed + 2).octaves(2).map(x -> x > 0 ? x : 0).spread(0.01f).scaled(-25, 25);
        final INoise2D cliffHeightNoise = new SimplexNoise2D(seed + 3).octaves(2).spread(0.01f).scaled(-20, 20);

        return (x, z) -> {
            float height = baseNoise.noise(x, z);
            if (height > 120) // Only sample each cliff noise layer if the base noise could be influenced by it
            {
                float cliffHeight = cliffHeightNoise.noise(x, z);
                if (height > 140 + cliffHeight)
                {
                    float cliff = cliffNoise.noise(x, z);
                    return height + cliff;
                }
            }
            return height;
        };
    }

    /**
     * Uses domain warping to achieve a swirly hills effect
     */
    public static INoise2D ocean(long seed, int depthMin, int depthMax)
    {
        final INoise2D warpX = new SimplexNoise2D(seed).octaves(4).spread(0.1f).scaled(-30, 30);
        final INoise2D warpZ = new SimplexNoise2D(seed + 1).octaves(4).spread(0.1f).scaled(-30, 30);
        return new SimplexNoise2D(seed).octaves(4).spread(0.04f).warped(warpX, warpZ).map(x -> x > 0.4 ? x - 0.8f : -x).scaled(-0.4f, 0.8f, TFCChunkGenerator.SEA_LEVEL + depthMin, TFCChunkGenerator.SEA_LEVEL + depthMax);
    }

    public static INoise2D shore(long seed)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.17f).scaled(TFCChunkGenerator.SEA_LEVEL, TFCChunkGenerator.SEA_LEVEL + 1.8f);
    }
}
