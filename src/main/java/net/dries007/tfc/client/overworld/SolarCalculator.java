/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.overworld;

import net.minecraft.util.Mth;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 * Models for realistic solar positioning and calculations.
 *
 * <h3>Poles</h3>
 * Poles are calculated from the temperature scale of the climate model. We use a basic cyclical model,
 * that assigns {@code z = 0} to the northern hemisphere, and then proceed where cold regions are poles,
 * and tropical regions are equatorial. We thus traverse north or south, cyclically around a "spherical"
 * earth. Thus, with the default of a {@code 10,000} scale, noting that north is negative Z values:
 * <pre>
 * z (km) Type                          Latitude (°)
 * -40    Temperate (South Hemisphere)  -45
 * -30    Equator                         0
 * -20    Temperate (North Hemisphere)   45
 * -10    Pole (North)                   90
 *   0    Temperate (North Hemisphere)   45
 *  10    Equator                         0
 *  20    Temperate (South Hemisphere)  -45
 *  30    Pole (South)                  -90
 *  40    Temperate (South Hemisphere)  -45
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
     * Using the position of the sun, computes a relative "day time", of a kind that vanilla expects (i.e. where noon = 6_000,
     * midnight = 18_000, with a total range of {@code [0, 24_000]}). This gives the following constants:
     * <pre>
     * Time of Day | Vanilla Time (`getDayTime`)
     * Sunrise     | 0 or 24_000
     * Noon        | 6_000
     * Sunset      | 12_000
     * Midnight    | 18_000
     * </pre>
     *
     * @param z The z location in blocks, used to determine the latitude
     * @param hemisphereScale The temperature scale, in blocks. This is used to determine where the poles are. Default 10km.
     * @param fractionOfYear The fraction of the current year, in {@code [0, 1]}. Where the beginning (January) is 0, and the end is 1
     * @param fractionOfDay The fraction of the current day, in {@code [0, 1]}. Where 0 is midnight and onwards
     * @return A day time, in the range {@code [0, 24_000]}
     */
    public static int getSunBasedDayTime(int z, float hemisphereScale, float fractionOfYear, float fractionOfDay)
    {
        final float zenith = getSunPosition(z, hemisphereScale, fractionOfYear, fractionOfDay).zenith();
        if (fractionOfDay < 0.5)
        {
            // Midnight -> Noon
            // If the zenith angle is below the horizon, interpolate from the lowest zenith angle to 0 as the [0, 6_000] range
            // If the zenith angle is above the horizon, interpolate from the horizon to the highest zenith angle as the [6_000, 12_000] range
            if (zenith > Mth.HALF_PI)
            {
                // Midnight -> Sunrise
                final float minZenith = getSunPosition(z, hemisphereScale, fractionOfYear, 0f).zenith();
                return (int) Mth.clampedMap(zenith, minZenith, Mth.HALF_PI, 18_000, 24_000);
            }
            else
            {
                // Sunrise -> Noon
                final float maxZenith = getSunPosition(z, hemisphereScale, fractionOfYear, 0.5f).zenith();
                return (int) Mth.clampedMap(zenith, Mth.HALF_PI, maxZenith, 0, 6_000);
            }
        }
        else
        {
            // Noon -> Midnight
            // Note, if the zenith never reaches above the horizon at noon, this will never advance, and will instead create a "time skip",
            // from < 0 to > 12_000. This is... probably acceptable.
            if (zenith < Mth.HALF_PI)
            {
                // Noon -> Sunset
                final float maxZenith = getSunPosition(z, hemisphereScale, fractionOfYear, 0.5f).zenith();
                return (int) Mth.clampedMap(zenith, maxZenith, Mth.HALF_PI, 6_000, 12_000);
            }
            else
            {
                // Sunset -> Midnight
                final float minZenith = getSunPosition(z, hemisphereScale, fractionOfYear, 1f).zenith();
                return (int) Mth.clampedMap(zenith, Mth.HALF_PI, minZenith, 12_000, 18_000);
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

        final double sinL = Math.sin(latitude);
        final double cosL = cosFromSin(latitude, sinL);
        final double sinD = Math.sin(declination);
        final double cosD = cosFromSin(declination, sinD);

        // Solar Zenith Angle
        //   cos(z) = sin(L)sin(d) + cos(L)cos(d)cos(h)
        // where
        //   z := Solar Zenith Angle
        //   L := Latitude (angle -90° south, 0° at the equator, and 90° north)
        //   d := Declination of the Sun
        //   h := Hour Angle, in the local Solar Time
        final double solarZenithAngle = Math.acos(Mth.clamp(sinL * sinD + cosL * cosD * Math.cos(hourAngle), -1,1));

        final double sinZ = Math.sin(solarZenithAngle);
        final double cosZ = cosFromSin(solarZenithAngle, sinZ);

        // Solar Azimuth Angle
        //   cos(a) = (sin(d) - cos(z)sin(L)) / sin(z)cos(L)
        //
        // with the assumption that this indicates an angle between [0, 180°] in morning, and [180°, 360°] in afternoon
        final double absSolarAzimuthAngle = Math.acos(Mth.clamp((sinD - cosZ * sinL) / (sinZ * cosL), -1, 1));
        final double solarAzimuthAngle = hourAngle < 0
            ? absSolarAzimuthAngle
            : Mth.TWO_PI - absSolarAzimuthAngle;

        return SkyPos.of(solarZenithAngle, solarAzimuthAngle);
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

    /**
     * Calculates the current phase of the moon given the provided parameters, which is dependent only on the orbital period of the moon.
     * @param calendarTick The current calendar tick
     * @param lunarOrbitTicks The amount of ticks that consist of a lunar orbit. Is typically a number of days, to prevent lunar phases from
     *                        changing during the day
     * @return A number in {@code [0, 7]} which represents the phase, 0 = full moon, 4 = new moon
     */
    public static int getMoonPhase(long calendarTick, long lunarOrbitTicks)
    {
        return (int) Mth.clampedMap((float) ((calendarTick + lunarOrbitTicks / 8) % lunarOrbitTicks) / lunarOrbitTicks, 0, 1, 0, 7);
    }

    /** Equal to {@code tan(<earth's axial tilt> + <lunar orbit inclination>)} */
    private static final double MOON_ORBIT_PHI = Math.tan(Mth.DEG_TO_RAD * (24.33 + 5.14));

    public static SkyPos getMoonPosition(int z, float hemisphereScale, long calendarTick, long lunarOrbitTicks)
    {
        // Lunar orbit is calculated with a constant orbit, at an inclination of <earth axial tilt> + <lunar axial tilt>
        // We also incorporate slight procession (so the inclination of the orbit moves over time, out of sync with the orbital phase)
        final double lunarAzimuth = Mth.TWO_PI * (float) (calendarTick % lunarOrbitTicks) / lunarOrbitTicks;
        final double lunarZenith = Mth.HALF_PI - Math.atan2(Math.cos(Mth.TWO_PI * (float) ((calendarTick * 0.83) % lunarOrbitTicks) / lunarOrbitTicks) * MOON_ORBIT_PHI, 1);

        // The observer is the position on earth that we would be observing the moon from. We use this to rotate the location of the moon (initially
        // given in earth-centric coordinates), into coordinates based on the observer position.
        final double observerZenith = Mth.HALF_PI - getNorthHemisphereLatitude(z, hemisphereScale);
        final double observerAzimuth = Mth.TWO_PI * (1 - ICalendar.getFractionOfDay(calendarTick));

        // Rotation Rz(-observerAzimuth) are trivial to do in spherical coordinates
        final double deltaAzimuth = lunarAzimuth - observerAzimuth;

        // Rotation around the zenith angle require some computation. Ry(-observerZenith) . moon
        // Theta = Zenith, Phi = Azimuth, P = Observer, M = Moon, D = Delta
        // The formulation of the rotation matrix and resultant matrix multiplication was done with wolfram alpha
        final double sinPhiD = Math.sin(deltaAzimuth);
        final double cosPhiD = cosFromSin(deltaAzimuth, sinPhiD);
        final double sinThetaP = Math.sin(observerZenith);
        final double cosThetaP = cosFromSin(observerZenith, sinThetaP);
        final double sinThetaM = Math.sin(lunarZenith);
        final double cosThetaM = cosFromSin(lunarZenith, sinThetaM);

        final double lunarX = cosPhiD * cosThetaP * sinThetaM - cosThetaM * sinThetaP;
        final double lunarY = sinThetaM * sinPhiD;
        final double lunarZ = cosThetaM * cosThetaP + cosPhiD * sinThetaM * sinThetaP;

        final double relativeLunarZenith = Math.acos(lunarZ); // N.B. unit radius means lunarR = 1
        final double relativeLunarAzimuth = Mth.PI - Math.atan2(lunarY, lunarX);

        return SkyPos.of(relativeLunarZenith, relativeLunarAzimuth);
    }

    /**
     * Calculates the position describing the position of a star oriented at the pole (at {@link SkyPos#ZERO}) in the sky. With other stars positioned
     * relative to that star, this can be used to orient the entirety of star rendering. This actually is just calculating the relative position on earth,
     * of an observer relative to the surrounding solar system, and inverting it.
     * <p>
     * Right-ascension in stars is measured where zero is at the march equinox, using a right-hand rule from the (north) pole.
     */
    public static SkyPos getStarPosition(int z, float hemisphereScale, float fractionOfDay, float fractionOfYear)
    {
        // The zenith position is just based on the latitude, not inverted
        // The azimuth position is based on both the rotation of the earth, and the rotation of earth around the sun - different stars will be seen
        // on opposite years, during day and night.
        final double starZenith = Mth.HALF_PI - getNorthHemisphereLatitude(z, hemisphereScale);
        final double starAzimuth = Mth.TWO_PI * Mth.frac(fractionOfYear + fractionOfDay + 0.5f);
        return SkyPos.of(starZenith, starAzimuth);
    }

    /**
     * Use the identity {@code sin^2(x) + cos^2(x) = 1} which is faster - one sqrt - than a call to {@link Math#cos}.
     */
    private static double cosFromSin(double angle, double sin)
    {
        return org.joml.Math.cosFromSin(sin, angle);
    }

    private SolarCalculator() {}
}
