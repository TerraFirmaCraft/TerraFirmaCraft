package net.dries007.tfc.tests;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.Artist;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.INoise2D;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class BiomeNoiseTests
{
    static final Artist.Noise<INoise2D> NOISE = Artist.<INoise2D>forNoise(target -> Artist.NoisePixel.coerceFloat(target::noise)).scale(Artist.Scales.DYNAMIC_RANGE).color(Artist.Colors.LINEAR_GRAY).center(50).size(100);

    @Test
    void testRiverCarving()
    {
        long seed = System.currentTimeMillis();
        Pair<INoise2D, INoise2D> noise = BiomeNoise.riverCarving(seed);

        NOISE.draw("river_carving_center", noise.getFirst());
        NOISE.draw("river_carving_height", noise.getSecond());
    }

    @Test
    void testLakeCarving()
    {
        long seed = System.currentTimeMillis();
        Pair<INoise2D, INoise2D> noise = BiomeNoise.lakeCarving(seed);

        NOISE.draw("lake_carving_center", noise.getFirst());
        NOISE.draw("lake_carving_height", noise.getSecond());
    }
}
