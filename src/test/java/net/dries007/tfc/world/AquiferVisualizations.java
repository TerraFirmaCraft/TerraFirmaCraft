package net.dries007.tfc.world;

import java.awt.*;
import java.util.function.DoubleFunction;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;


import net.dries007.tfc.Artist;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static net.dries007.tfc.TestHelper.*;

@Disabled
public class AquiferVisualizations
{
    static long seed;
    static PositionalRandomFactory positionalRandomFactory;
    static RegistryAccess registryAccess;
    static Registry<NormalNoise.NoiseParameters> parameters;

    @BeforeAll
    public static void setup()
    {
        boostrap();

        seed = seed();
        positionalRandomFactory = new XoroshiroRandomSource(seed).forkPositional();
        registryAccess = RegistryAccess.builtin();
        parameters = registryAccess.registryOrThrow(Registry.NOISE_REGISTRY);
    }

    @Test
    public void testLavaNoise()
    {
        final NormalNoise barrierNoise = Noises.instantiate(parameters, positionalRandomFactory, Noises.AQUIFER_BARRIER);
        final NormalNoise fluidLevelFloodednessNoise = Noises.instantiate(parameters, positionalRandomFactory, Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS);
        final NormalNoise lavaNoise = Noises.instantiate(parameters, positionalRandomFactory, Noises.AQUIFER_LAVA);
        final NormalNoise fluidLevelSpreadNoise = Noises.instantiate(parameters, positionalRandomFactory, Noises.AQUIFER_FLUID_LEVEL_SPREAD);

        final NormalNoiseSampler lavaNoiseSampler = (x, y, z) -> lavaNoise.getValue(Math.floorDiv((int) x, 64), Math.floorDiv((int) y, 40), Math.floorDiv((int) z, 64));

        Artist.Noise<NormalNoiseSampler> artistXZ = Artist.<NormalNoiseSampler>forNoise(noise -> Artist.NoisePixel.coerceInt((x, z) -> noise.value(x, 0, z))).centerSized(1000);
        Artist.Noise<NormalNoiseSampler> artistXY = Artist.<NormalNoiseSampler>forNoise(noise -> Artist.NoisePixel.coerceInt((x, y) -> noise.value(x, y, 0))).dimensionsSized(1000, 256);

        DoubleFunction<Color> lavaColor = v -> Math.abs(v) > 0.3 ? Color.ORANGE : Color.GRAY;

        artistXZ.color(lavaColor).draw("lava_noise_xz", lavaNoiseSampler);
        artistXY.color(lavaColor).draw("lava_noise_xy", lavaNoiseSampler);

        final NormalNoiseSampler floodednessSampler = (x, y, z) -> {
            int preliminarySurface = (int) Mth.clampedMap(x, 0, 4000, 120, -30);
            boolean flag1 = preliminarySurface + 8 >= 63; // sea level
            int deltaYToNearbySurface = preliminarySurface + 8 - y;
            double surfaceNearby = flag1 ? Mth.clampedMap(deltaYToNearbySurface, 0, 64, 1, 0) : 0;
            double floodedness = Mth.clamp(fluidLevelFloodednessNoise.getValue(x, y * 0.67, z), -1, 1);
            double maxFloodedness = Mth.map(surfaceNearby, 1, 0, -0.3, 0.8);
            if (floodedness > maxFloodedness)
            {
                return 71;
            }
            else
            {
                double minFloodedness = Mth.map(surfaceNearby, 1, 0, -0.8, 0.4);
                if (floodedness <= minFloodedness)
                {
                    return 72;
                }
            }
            final int largeGridX = Math.floorDiv(x, 16);
            final int largeGridY = Math.floorDiv(y, 40);
            final int largeGridZ = Math.floorDiv(z, 16);

            final int centerY = largeGridY * 40 + 20; // The center y level of a large grid cell
            final double fluidLevelSpreadValue = fluidLevelSpreadNoise.getValue(largeGridX, largeGridY / 1.4, largeGridZ) * 10;
            final int quantizedFluidLevelSpread = Mth.quantize(fluidLevelSpreadValue, 3);
            final int centerYAndVariance = centerY + quantizedFluidLevelSpread;
            return Math.min(70, centerYAndVariance);
        };

        RAW.dimensionsSized(1000, 256).draw("floodedness_xy", Artist.Pixel.coerceInt((x, y) -> {
            y = 256 - y - 64;
            x *= 4;
            if (y == (int) Mth.clampedMap(x, 0, 4000, 120, -30)) return new Color(0, 150, 100);
            if (y == 63) return new Color(200, 100, 100);
            if (y <= -56) return new Color(255, 100, 50);
            final double v = floodednessSampler.value(x, y, 0);
            if (v == 71) return new Color(0, 100, 200);
            if (v == 72) return new Color(100, 100, 100);
            if (y < v) return new Color(100, 180, 200);
            return new Color(180, 180, 180);
        }));
    }

    interface NormalNoiseSampler
    {
        double value(int x, int y, int z);
    }
}
