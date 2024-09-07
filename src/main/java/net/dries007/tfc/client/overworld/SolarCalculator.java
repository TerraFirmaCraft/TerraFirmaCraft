/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.overworld;

import net.minecraft.util.Mth;

import net.dries007.tfc.util.Helpers;

/**
 * Models for realistic solar positioning and calculations.
 *
 * <h3>Poles</h3>
 * Poles are calculated from the temperature scale of the climate model. We use a basic cyclical model,
 * that assigns {@code z = 0} to the northern hemisphere, and then proceed where cold regions are poles,
 * and tropical regions are equatorial. We thus traverse north or south, cyclically around a "spherical"
 * earth. Thus, with the default of a {@code 10,000} scale, noting that north is negative Z values:
 * <pre>
 *     z (km) Type                          Latitude (°)
 *     -40    Temperate (South Hemisphere)  -45
 *     -30    Equator                         0
 *     -20    Temperate (North Hemisphere)   45
 *     -10    Pole (North)                   90
 *       0    Temperate (North Hemisphere)   45
 *      10    Equator                         0
 *      20    Temperate (South Hemisphere)  -45
 *      30    Pole (South)                  -90
 *      40    Temperate (South Hemisphere)  -45
 * </pre>
 *
 * <h3>References</h3>
 * <ul>
 *     <li><a href="https://en.wikipedia.org/wiki/Solar_zenith_angle">Solar Zenith Angle</a></li>
 *     <li><a href="https://en.wikipedia.org/wiki/Solar_azimuth_angle">Solar Azimuth Angle</a></li>
 *     <li><a href="https://pvpmc.sandia.gov/modeling-guide/1-weather-design-inputs/sun-position/basic-solar-position-models/">Solar Position Models</a></li>
 * </ul>
 */
public final class SolarCalculator
{
    /**
     * Using the position of the sun, computes a relative "day time", of a kind that vanilla expects (i.e. with a total range of 24_000,
     * where 0 = midnight).
     *
     * @param z The z location in blocks, used to determine the latitude
     * @param temperatureScale The temperature scale, in blocks. This is used to determine where the poles are. Default 10km.
     * @param fractionOfYear The fraction of the current year, in {@code [0, 1]}. Where the beginning (January) is 0, and the end is 1
     * @param fractionOfDay The fraction of the current day, in {@code [0, 1]}. Where 0 is midnight and onwards
     * @return A day time, in the range {@code [0, 24_000]}
     */
    public static int getSunBasedDayTime(int z, float temperatureScale, float fractionOfYear, float fractionOfDay)
    {
        final float zenith = getSunPosition(z, temperatureScale, fractionOfYear, fractionOfDay).zenith();
        if (fractionOfDay < 0.5)
        {
            // Between midnight - noon
            // If the zenith angle is below the horizon, interpolate from the lowest zenith angle to 0 as the [0, 6_000] range
            // If the zenith angle is above the horizon, interpolate from the horizon to the highest zenith angle as the [6_000, 12_000] range
            if (zenith > Mth.HALF_PI)
            {
                final float minZenith = getSunPosition(z, temperatureScale, fractionOfYear, 0f).zenith();
                return (int) Mth.clampedMap(zenith, minZenith, Mth.HALF_PI, 0, 6_000);
            }
            else
            {
                final float maxZenith = getSunPosition(z, temperatureScale, fractionOfYear, 0.5f).zenith();
                return (int) Mth.clampedMap(zenith, Mth.HALF_PI, maxZenith, 6_000, 12_000);
            }
        }
        else
        {
            // Between noon - midnight
            // Note, if the zenith never reaches above the horizon at noon, this will never advance >6_000, and will instead create a "time skip",
            // from < 6_000 to > 18_000. This is... probably acceptable.
            if (zenith < Mth.HALF_PI)
            {
                final float maxZenith = getSunPosition(z, temperatureScale, fractionOfYear, 0.5f).zenith();
                return (int) Mth.clampedMap(zenith, maxZenith, Mth.HALF_PI, 12_000, 18_000);
            }
            else
            {
                final float minZenith = getSunPosition(z, temperatureScale, fractionOfYear, 1f).zenith();
                return (int) Mth.clampedMap(zenith, Mth.HALF_PI, minZenith, 18_000, 24_000);
            }
        }
    }

    /**
     * Calculates the position of the sun in the sky given the provided parameters.
     *
     * @param z The z location in blocks, used to determine the latitude
     * @param hemisphereScale The temperature scale, in blocks. This is used to determine where the poles are. Default 10km.
     * @param fractionOfYear The fraction of the current year, in {@code [0, 1]}. Where the beginning (January) is 0, and the end is 1
     * @param fractionOfDay The fraction of the current day, in {@code [0, 1]}. Where 0 is midnight and onwards
     *
     * @implNote This can't use the approximations for trigonometric functions in {@link Mth} because they are not accurate enough for
     * smooth movement
     */
    public static SkyPos getSunPosition(int z, float hemisphereScale, float fractionOfYear, float fractionOfDay)
    {
        final double latitude = getNorthHemisphereLatitude(z, hemisphereScale);
        // Declination
        // Approximation based on sin with amplitude of 23.44 degrees
        final double declination = 23.44f * Mth.DEG_TO_RAD * Mth.sin(Mth.TWO_PI * (284f / 365f + fractionOfYear));

        // Hour Angle
        // Approximation based on a full rotation on a single TFC day. We ignore the distinction between "solar time", and standard time.
        // By convention, this angle should be zero at solar noon (where the fraction of day = 0.5)
        final double hourAngle = Mth.TWO_PI * (0.5f - fractionOfDay);

        // Solar Zenith Angle
        //   cos(z) = sin(L)sin(d) + cos(L)cos(d)cos(h)
        // where
        //   z := Solar Zenith Angle
        //   L := Latitude (angle -90° south, 0° at the equator, and 90° north)
        //   d := Declination of the Sun
        //   h := Hour Angle, in the local Solar Time
        final double solarZenithAngle = Math.acos(Mth.clamp(Math.sin(latitude) * Math.sin(declination) + Math.cos(latitude) * Math.cos(declination) * Math.cos(hourAngle), -1,1));

        // Solar Azimuth Angle
        //   cos(a) = (sin(d) - cos(z)sin(L)) / sin(z)cos(L)
        //
        // with the assumption that this indicates an angle between [0, 180°] in morning, and [180°, 360°] in afternoon
        final double absSolarAzimuthAngle = Math.acos(Mth.clamp((Math.sin(declination) - Math.cos(solarZenithAngle) * Math.sin(latitude)) / (Math.sin(solarZenithAngle) * Math.cos(latitude)), -1, 1));
        final double solarAzimuthAngle = hourAngle < 0
            ? absSolarAzimuthAngle
            : Mth.TWO_PI - absSolarAzimuthAngle;

        return new SkyPos((float) solarZenithAngle, (float) solarAzimuthAngle);
    }

    /**
     * Return the corresponding latitude given a position and climate temperature scale. This <strong>does not</strong> currently use a hemispherical
     * model (although it could), due to introducing inconsistency with how the rest of TFC handles seasons. As a result, the latitude will always be
     * oriented in a "north hemisphere" convention.
     * @return A latitude, in {@code [0, pi / 2]}
     */
    public static float getNorthHemisphereLatitude(int z, float hemisphereScale)
    {
        return Mth.abs(getLatitude(z, hemisphereScale));
    }

    /**
     * Return the corresponding latitude given a position and climate temperature scale.
     * @return A latitude, in {@code [-pi / 2, pi / 2]}
     */
    public static float getLatitude(int z, float hemisphereScale)
    {
        return Helpers.triangle(-Mth.HALF_PI, 0, 1 / (4 * hemisphereScale), z - 0.5f * hemisphereScale);
    }

    private SolarCalculator() {}
}
