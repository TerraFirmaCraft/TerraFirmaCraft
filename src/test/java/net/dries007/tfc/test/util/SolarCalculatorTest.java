/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.util;

import net.minecraft.util.Mth;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.client.overworld.SolarCalculator;
import net.dries007.tfc.data.providers.BuiltinWorldPreset;

import static net.dries007.tfc.test.TestAssertions.*;

public class SolarCalculatorTest
{
    final float DEFAULT_SCALE = BuiltinWorldPreset.defaultSettings().temperatureScale();

    @Test
    public void testLatitude()
    {
        assertEquals(-Mth.HALF_PI, SolarCalculator.getLatitude(-50_000, DEFAULT_SCALE)); // South Pole
        assertEquals(-Mth.PI / 4f, SolarCalculator.getLatitude(-40_000, DEFAULT_SCALE)); // South, Temperate
        assertEquals(-0, SolarCalculator.getLatitude(-30_000, DEFAULT_SCALE)); // Equator
        assertEquals(Mth.PI / 4f, SolarCalculator.getLatitude(-20_000, DEFAULT_SCALE)); // North, Temperate
        assertEquals(Mth.HALF_PI, SolarCalculator.getLatitude(-10_000, DEFAULT_SCALE)); // North Pole
        assertEquals(Mth.PI / 4f, SolarCalculator.getLatitude(0, DEFAULT_SCALE)); // North, Temperate
        assertEquals(0, SolarCalculator.getLatitude(10_000, DEFAULT_SCALE)); // Equator
        assertEquals(-Mth.PI / 4f, SolarCalculator.getLatitude(20_000, DEFAULT_SCALE)); // South, Temperate
        assertEquals(-Mth.HALF_PI, SolarCalculator.getLatitude(30_000, DEFAULT_SCALE)); // South Pole
        assertEquals(-Mth.PI / 4f, SolarCalculator.getLatitude(40_000, DEFAULT_SCALE)); // South, Temperate
        assertEquals(0, SolarCalculator.getLatitude(50_000, DEFAULT_SCALE)); // Equator
    }

    @Test
    public void testDeclinationAtPoleAtSolsticeIsConstant()
    {
        final var pos = SolarCalculator.getSunPosition(-10_000, DEFAULT_SCALE, 0f, 0f);
        for (int h = 0; h < 24; h++)
            assertEquals(pos.zenith(), SolarCalculator.getSunPosition(-10_000, DEFAULT_SCALE, 0f, h / 24f).zenith(), 0.000001);
    }

    @Test
    public void testSunIsAlwaysVisibleAtNoonAtEquator()
    {
        for (int d = 0; d < 72; d++)
            assertTrue(SolarCalculator.getSunPosition(10_000, DEFAULT_SCALE, d / 72f, 0.5f).zenith() < Mth.HALF_PI);
    }

    @Test
    public void testSunIsNeverVisibleAtMidnightAtNorthHemisphereTemperate()
    {
        for (int d = 0; d < 72; d++)
            assertTrue(SolarCalculator.getSunPosition(0, DEFAULT_SCALE, d / 72f, 0f).zenith() > Mth.HALF_PI);
    }

    @Test
    public void testSunBasedDayTimeNoonAndMidnightAreConstantAroundEquator()
    {
        for (int d = 0; d < 72; d++)
        {
            // Assert that midnight, noon correspond daytime to the correct sun position at the equator all days of the year
            assertEquals(18_000, SolarCalculator.getSunBasedDayTime(10_000, DEFAULT_SCALE, d / 72f, 0));
            assertEquals(6_000, SolarCalculator.getSunBasedDayTime(10_000, DEFAULT_SCALE, d / 72f, 0.5f));

            // Same with temperate regions
            assertEquals(18_000, SolarCalculator.getSunBasedDayTime(0, DEFAULT_SCALE, d / 72f, 0));
            assertEquals(6_000, SolarCalculator.getSunBasedDayTime(0, DEFAULT_SCALE, d / 72f, 0.5f));
        }
    }

    @Test
    public void testSunBasedDayTimeNoonAndMidnightAreEndlessAroundPoles()
    {
        for (int h = 0; h < 24; h++)
        {
            // At the poles at peak seasons, day time should always be night/day
            final int northPoleWinter = SolarCalculator.getSunBasedDayTime(-10_000, DEFAULT_SCALE, 0f, h / 24f);
            final int northPoleSummer = SolarCalculator.getSunBasedDayTime(-10_000, DEFAULT_SCALE, 0.5f, h / 24f);

            assertTrue(northPoleWinter > 12_000);
            assertTrue(northPoleSummer < 12_000);
        }
    }
}
