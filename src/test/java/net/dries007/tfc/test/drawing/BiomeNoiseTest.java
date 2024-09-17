/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.drawing;

import java.awt.Color;
import java.util.function.DoubleFunction;
import net.minecraft.util.Mth;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.test.TestSetup;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Noise2D;

import static net.dries007.tfc.world.TFCChunkGenerator.*;
import static net.dries007.tfc.world.biome.BiomeNoise.*;

@Disabled
public class BiomeNoiseTest implements TestSetup
{
    private final DoubleFunction<Color> green = Artist.Colors.linearGradient(
        new Color(0, 90, 0),
        new Color(90, 240, 90));
    private final DoubleFunction<Color> blue = Artist.Colors.linearGradient(
        new Color(90, 170, 240),
        new Color(10, 80, 140));
    private final Artist.Noise<Noise2D> terrain = Artist.<Noise2D>forNoise(instance -> Artist.NoisePixel.coerceFloat(instance::noise))
        .scale((value, min, max) -> value > SEA_LEVEL_Y ?
            Mth.clampedMap((int) value, SEA_LEVEL_Y, max, 0, 1) :
            Mth.clampedMap((int) value, SEA_LEVEL_Y, min, 0, -1))
        .color(x -> x < 0 ? blue.apply(-x) : green.apply(x))
        .dimensions(400)
        .size(400);
    private final Artist.Noise<Noise2D> noise = Artist.<Noise2D>forNoise(instance -> Artist.NoisePixel.coerceFloat(instance::noise))
        .dimensions(400)
        .size(400);

    @Test
    public void testRollingHills()
    {
        terrain.draw("noise_rolling_hills", hills(seed(), -5, 28));
    }

    @Test
    public void testLowCanyons()
    {
        terrain.draw("noise_low_canyons", canyons(seed(), -8, 21));
    }

    @Test
    public void testCanyons()
    {
        terrain.draw("noise_canyons", canyons(seed(), -2, 40));
    }

    @Test
    public void testSharpHills()
    {
        terrain.draw("noise_sharp_hills", sharpHills(seed()));
    }

    @Test
    public void testLakes()
    {
        terrain.draw("noise_lakes", lake(seed()));
    }

    @Test
    public void testFlats()
    {
        terrain.draw("noise_flats", flats(seed()));
    }

    @Test
    public void testSaltFlats()
    {
        terrain.draw("noise_salt_flats", saltFlats(seed()));
    }

    @Test
    public void testDunes()
    {
        terrain.draw("noise_dunes", dunes(seed(), 2, 16));
    }

    @Test
    public void testKarren()
    {
        terrain.draw("noise_karren", karren(seed(), BiomeNoise.hills(seed(), 22, 32), 0.15, 0.4, 1.5));
    }

    @Test
    public void testShilin()
    {
        terrain.draw("noise_shilin", shilin(seed(), BiomeNoise.hills(seed(), 22, 32)));
    }
}
