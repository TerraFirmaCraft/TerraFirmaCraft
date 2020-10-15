package net.dries007.tfc.tests;

import java.awt.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.Artist;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.IChunkDataProvider;
import net.dries007.tfc.world.feature.GlacierFeature;
import net.dries007.tfc.world.feature.TFCFeatures;
import net.dries007.tfc.world.noise.INoise2D;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
class GlacierFeatureTests
{
    static final Artist.Noise<INoise2D> NOISE = Artist.forNoise(noise -> Artist.NoisePixel.coerceFloat(noise::noise));
    static final Artist.Raw ARTIST = Artist.raw();

    static final Level UNIT_TEST = Level.forName("UNITTEST", 50);
    static final Logger LOGGER = LogManager.getLogger();

    @Test
    void testTemperatures()
    {
        ChunkDataProvider provider = (ChunkDataProvider) IChunkDataProvider.getOrThrow();

        LOGGER.log(UNIT_TEST, "Temperature From z=-20,000 to z=20,000");
        for (int i = -20_000; i <= 20_000; i += 1000)
        {
            float averageTemp = provider.getTemperatureNoise().noise(0, i);
            float minTemp = Climate.calculateMonthlyTemperature(i, TFCChunkGenerator.SEA_LEVEL, averageTemp, -1);
            float maxTemp = Climate.calculateMonthlyTemperature(i, TFCChunkGenerator.SEA_LEVEL, averageTemp, 1);
            LOGGER.log(UNIT_TEST, String.format("%.1f\t%.4f\t%.4f\t%.4f", i / 1000f, averageTemp, minTemp, maxTemp));
        }
    }

    @Test
    void testGlacierMap()
    {
        long seed = System.currentTimeMillis();

        ChunkDataProvider provider = (ChunkDataProvider) IChunkDataProvider.getOrThrow();
        GlacierFeature glacier = TFCFeatures.GLACIER.get();
        INoise2D temperatureNoise = provider.getTemperatureNoise();

        Color tooWarmColor = new Color(200, 140, 0);
        Color noGlacierColor = new Color(120, 120, 250);
        Color glacierColor = new Color(220, 220, 250);

        glacier.initSeed(seed);

        assertNotNull(glacier.getGlacierNoise());

        Artist.Pixel<Color> pixel = Artist.Pixel.coerceFloat((x, z) -> {
            float averageTemperature = temperatureNoise.noise(x, z);
            float maxTemperature = Climate.calculateMonthlyTemperature((int) z, TFCChunkGenerator.SEA_LEVEL, averageTemperature, 1);
            if (maxTemperature > 0)
            {
                // Summers are too warm to generate glaciers
                return tooWarmColor;
            }

            float glacierValue = glacier.getGlacierNoise().noise(x, z) + MathHelper.clamp(-0.2f * maxTemperature, 0, 4);
            if (glacierValue < 0)
            {
                // No glacier at this location
                return noGlacierColor;
            }

            return glacierColor;
        });

        ARTIST.center(20_000).draw("glacier_map_0_20k", pixel);
        ARTIST.center(0, -16_000, 4000).draw("glacier_map_-16k_4k", pixel);
        ARTIST.center(0, -18_000, 2000).draw("glacier_map_-18k_2k", pixel);
    }

    @Test
    void testGlacierHeight()
    {
        long seed = System.currentTimeMillis();
        GlacierFeature glacier = TFCFeatures.GLACIER.get();

        glacier.initSeed(seed);

        assertNotNull(glacier.getGlacierNoise());

        NOISE.center(100).color(Artist.Colors.LINEAR_BLUE_RED).draw("glacier_height", glacier.getGlacierNoise());
    }
}
