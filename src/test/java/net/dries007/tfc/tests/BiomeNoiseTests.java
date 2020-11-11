package net.dries007.tfc.tests;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.Artist;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.noise.INoise2D;
import org.junit.jupiter.api.Test;

public class BiomeNoiseTests
{
    static final Artist.Noise<INoise2D> NOISE = Artist.<INoise2D>forNoise(target -> Artist.NoisePixel.coerceFloat(target::noise)).scale(Artist.Scales.DYNAMIC_RANGE).color(Artist.Colors.LINEAR_GRAY);

    @Test
    public void testBadlands()
    {
        long seed = System.currentTimeMillis();
        INoise2D noise = TFCBiomes.BADLANDS.createNoiseLayer(seed);

        NOISE.center(500).size(1000);
        NOISE.draw("badlands", noise);
    }

    @Test
    public void testDeepOcean()
    {
        long seed = System.currentTimeMillis();
        INoise2D noise = TFCBiomes.DEEP_OCEAN.createNoiseLayer(seed);

        NOISE.center(500).size(1000);
        NOISE.draw("deep_ocean", noise);
    }

    @Test
    public void testOceanRidge()
    {
        long seed = System.currentTimeMillis();
        INoise2D noise = TFCBiomes.DEEP_OCEAN_RIDGE.createNoiseLayer(seed);

        NOISE.center(500).size(1000);
        NOISE.draw("deep_ocean_ridge", noise);
    }

    @Test
    public void testRiverCarving()
    {
        long seed = System.currentTimeMillis();
        Pair<INoise2D, INoise2D> noise = TFCBiomes.MOUNTAIN_RIVER.createCarvingNoiseLayer(seed);

        NOISE.center(50).size(100);
        NOISE.draw("river_carving_center", noise.getFirst());
        NOISE.draw("river_carving_height", noise.getSecond());
    }

    @Test
    public void testLakeCarving()
    {
        long seed = System.currentTimeMillis();
        Pair<INoise2D, INoise2D> noise = TFCBiomes.MOUNTAIN_LAKE.createCarvingNoiseLayer(seed);

        NOISE.center(50).size(100);
        NOISE.draw("lake_carving_center", noise.getFirst());
        NOISE.draw("lake_carving_height", noise.getSecond());
    }
}
