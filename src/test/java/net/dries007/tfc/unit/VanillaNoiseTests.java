package net.dries007.tfc.unit;

import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.gen.PerlinNoiseGenerator;

import net.dries007.tfc.Artist;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.NoiseUtil;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import org.junit.jupiter.api.Test;

public class VanillaNoiseTests
{
    static final Artist.Noise<INoise2D> NOISE = Artist.<INoise2D>forNoise(target -> (x, y) -> target.noise((float) x, (float) y)).scale(Artist.Scales.DYNAMIC_RANGE).color(Artist.Colors.LINEAR_GRAY).center(100);

    @Test
    public void test()
    {
        long seed = System.currentTimeMillis();
        SharedSeedRandom random = new SharedSeedRandom(seed);

        PerlinNoiseGenerator surfaceDepthNoise = new PerlinNoiseGenerator(random, IntStream.rangeClosed(-3, 0));

        INoise2D surfaceDepthNoiseConverted = (x, z) -> (float) surfaceDepthNoise.getSurfaceNoiseValue(x * 0.0625, z * 0.0625, 0.0625, (x % 16) * 0.0625) * 15;

        INoise2D surfaceDepthImproved = new OpenSimplex2D(random.nextLong()).octaves(4).spread(0.0625f).scaled(-9, 9);

        NOISE.center(200);
        NOISE.draw("surface_depth_noise_vanilla", surfaceDepthNoiseConverted);
        NOISE.draw("surface_depth_noise_new", surfaceDepthImproved);

        PerlinNoiseGenerator icebergNoise = new PerlinNoiseGenerator(random, IntStream.rangeClosed(-3, 0));
        PerlinNoiseGenerator icebergRoofNoise = new PerlinNoiseGenerator(random, ImmutableList.of(0));

        INoise2D icebergNoiseImproved = new OpenSimplex2D(random.nextLong()).octaves(4).spread(0.1f).scaled(-15, 15);

        NOISE.draw("iceberg_noise_vanilla", (x, z) -> (float) icebergNoise.getValue(x * 0.1D, z * 0.1D, false) * 15.0f);
        NOISE.draw("iceberg_noise_new", icebergNoiseImproved);

        INoise2D icebergImproved = surfaceDepthImproved.abs().min(icebergNoiseImproved);

        NOISE.draw("iceberg_vanilla", (x, z) -> (float) Math.min(Math.abs(surfaceDepthNoiseConverted.noise(x, z)), icebergNoise.getValue(x * 0.1D, z * 0.1D, false) * 15.0D));
        NOISE.draw("iceberg_new", icebergImproved);

        INoise2D icebergRoofImproved = new OpenSimplex2D(random.nextLong()).spread(0.09765625f).abs();

        NOISE.center(50);
        NOISE.draw("iceberg_roof_vanilla", (x, z) -> (float) Math.abs(icebergRoofNoise.getValue(x * 0.09765625D, z * 0.09765625D, false)));
        NOISE.draw("iceberg_roof_new", icebergRoofImproved);

        NOISE.center(200);
        NOISE.draw("iceberg_max_y_vanilla", (x, z) -> {
            final double icebergValue = Math.min(Math.abs(surfaceDepthNoiseConverted.noise(x, z)), icebergNoise.getValue(x * 0.1D, z * 0.1D, false) * 15.0D);
            double icebergMaxY = 0;
            if (icebergValue > 1.8D)
            {
                final double icebergRoofValue = Math.abs(icebergRoofNoise.getValue(x * 0.09765625D, z * 0.09765625D, false));
                final double maxIcebergRoofValue = Math.ceil(icebergRoofValue * 40.0D) + 14.0D;

                icebergMaxY = icebergValue * icebergValue * 1.2D;
                if (icebergMaxY > maxIcebergRoofValue)
                {
                    icebergMaxY = maxIcebergRoofValue;
                }
            }
            return (float) icebergMaxY;
        });

        INoise2D icebergRoofImprovedPart2 = icebergRoofImproved.scaled(14, 54);
        INoise2D icebergMaxYImproved = (x, z) -> {
            float value = icebergImproved.noise(x, z);
            if (value > 1.8f)
            {
                float roof = icebergRoofImprovedPart2.noise(x, z);
                value = value * value * 1.2f;
                return value > roof ? roof : value;
            }
            return 0;
        };

        NOISE.draw("iceberg_max_y_new", icebergMaxYImproved);


        // Stuff
new OpenSimplex2D(random.nextLong())
    .octaves(4)
    .spread(0.0625f)
    .scaled(-9, 9)
    .abs()
    .min(new OpenSimplex2D(random.nextLong())
        .octaves(4)
        .spread(0.1f)
        .scaled(-15, 15));
new OpenSimplex2D(random.nextLong()).spread(0.09765625f).abs();
    }

}
