/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec2;

import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.events.SelectClimateModelEvent;

/**
 * <h1>Climate Models</h1>
 * Represents the model for the climate for a dimension. This is the core object which determines all aspects of TFC climate,
 * from temperature, rainfall, current rain, wind, and fog.
 * <p>
 * Climate models are assigned to dimensions using the {@link SelectClimateModelEvent}. TFC listens to this event at highest
 * priority and will assign an overworld climate model for the overworld. If no climate model is assigned, the dimension will
 * default to using a biome-based climate model. In order to override the climate model for the overworld or any other dimension,
 * you simply must listen to the event at normal priority and select the climate model appropriately.
 * <h3>Query Types</h3>
 * Most climate queries can be represented as one of two types:
 * <ul>
 *     <li>Queries that ask factors about the average annual climate, such as average temperature, or average rainfall</li>
 *     <li>Queries that ask factors about the climate at a given timestamp</li>
 * </ul>
 * Note that climate models are not ticked in any way, so all climate calculations must be able to be fully re-created from scratch,
 * on an arbitrary query time.
 * <h3>Client-Side</h3>
 * Climate models are synced to client, and can expose some properties to be synced via the stream codec on the {@link ClimateModelType}.
 * Note that these are not synced on modification - they are assumed to be constant.
 * <p>
 * On client side, there is a cache of the current climate at the current time and player's position which can be accessed
 * via {@link ClimateRenderCache}. If doing frequent climate queries on client-side, at the player position, consider using this
 * instead. It is updated on client tick to reflect the current climate.
 */
public interface ClimateModel
{
    // N.B. These min-max values are only for rainfall values that are average annual, not time-variant or groundwater-inclusive
    float MIN_RAINFALL = 0f;
    float MAX_RAINFALL = 500f;

    /**
     * The type of this climate model. Must be registered through {@link ClimateModels#REGISTRY}
     */
    ClimateModelType<?> type();

    /**
     * @return A scaling value for hemispheres. This represents, effectively, the distance between a polar and equatorial region, in blocks.
     * This is primarily used for day time scaling based effects.
     */
    default float hemisphereScale()
    {
        return 20_000;
    }

    /**
     * Get the base average annual temperature for a given XZ position.
     *
     * @return The average annual temperature at the given {@code pos} for this climate model. Should be time-invariant, and
     * typically in the range {@code [-25, 25]} but is not required to be.
     */
    float getAverageTemperature(LevelReader level, BlockPos pos);

    /**
     * @return The current temperature at the given {@code pos}.
     */
    default float getTemperature(LevelReader level, BlockPos pos)
    {
        final ICalendar calendar = Calendars.get(level);
        return getTemperature(level, pos, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    /**
     * @return The temperature at the given {@code pos} and timestamp given by {@code calendarTicks} and {@code daysInMonth}.
     * This is typically in the range {@code [-40, 40]} but is not required to be.
     */
    default float getTemperature(LevelReader level, BlockPos pos, long calendarTicks, int daysInMonth)
    {
        return getAverageTemperature(level, pos);
    }

    /**
     * @return The average annual rainfall, in {@code mm/year} at the given {@code pos}. Should be time-invariant, and
     * <strong>must</strong> be in the range {@code [0, 500]}
     */
    float getAverageRainfall(LevelReader level, BlockPos pos);

    /**
     * Get the annual variance in rainfall for a given position.
     * Positive values indicate wet summers, Negative values indicate wet winters.
     *
     * @return The annual variance in the immediate rate of rainfall, in percentage of annual. Should be in the range {@code [-1, 1]}.
     */
    default float getRainfallVariance(LevelReader level, BlockPos pos)
    {
        return 0;
    }

    /**
     * @return The average rainfall, in {@code mm/year}, at the given {@code pos} at the current time.
     */
    default float getRainfall(LevelReader level, BlockPos pos)
    {
        final ICalendar calendar = Calendars.get(level);
        return getRainfall(level, pos, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    /**
     * @return The average rainfall, in {@code mm/year}, at the given {@code pos} and timestamp given by {@code calendarTicks} and
     * {@code daysInMonth}. Note that this is allowed to vary with seasonal effects, but still returns an average.
     * <strong>Must</strong> be within the range {@code [0, 500]}.
     */
    default float getRainfall(LevelReader level, BlockPos pos, long calendarTicks, int daysInMonth)
    {
        return getAverageRainfall(level, pos);
    }

    /**
     * Get the base average annual groundwater level, based on proximity to freshwater bodies. This is an additional contribution
     * from local influences to rainfall. In TFC it is only used (so far) for rivers, to create gallery forests.
     *
     * @return The annual base groundwater in mm. Should be in the range [0, 500]
     */
    default float getBaseGroundwater(LevelReader level, BlockPos pos)
    {
        return 0;
    }

    /**
     * Get the average annual groundwater level, typically the sum of rainfall and base groundwater.
     *
     * @return The annual groundwater in mm. Should be in the range [0, 500]
     */
    default float getAverageGroundwater(LevelReader level, BlockPos pos)
    {
        return getAverageRainfall(level, pos);
    }

    /**
     * @return The groundwater - sum of base groundwater and the time-varying rainfall, at the provided {@code pos} and current time.
     * Should be in the range {@code [0, 100]}, in {@code mm/year}.
     */
    default float getGroundwater(LevelReader level, BlockPos pos)
    {
        final ICalendar calendar = Calendars.get(level);
        return getGroundwater(level, pos, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    /**
     * @return The groundwater - sum of base groundwater and the time-varying rainfall, at the provided {@code pos} and the timestamp
     * provided by {@code calendarTicks} and {@code daysInMonth}. Should be in the range {@code [0, 100]}, in {@code mm/year}.
     */
    default float getGroundwater(LevelReader level, BlockPos pos, long calendarTicks, int daysInMonth)
    {
        return getRainfall(level, pos, calendarTicks, daysInMonth);
    }

     /**
     * Check if it is raining at the timestamp given by {@code calendarTicks} - not annual average
     * rainfall. This is used for purposes of simulation, because we want to be able to query the exact rainfall
     * patterns historically for an area.
     * <p>
     * TFC will make this authoritative for a world (replacing vanilla's simulation of rainfall), and will handle interpolation
     * between raining and non-raining states, as well as filtering raining areas out by total rainfall value (local rainfall).
     *
     * @return A value between {@code [0, 1]} representing the intensity of the current rainfall. Any negative value indicates
     * it is not currently raining, and larger values represent a higher intensity of rain.
     * @see #supportsRain()
     */
    default float getRain(long calendarTicks)
    {
        return -1;
    }

    /**
     * @return {@code true} if, given that it is raining at the given timestamp, it should also be thundering.
     * @see #getRain(long)
     * @see #supportsRain()
     */
    default boolean getThunder(long calendarTicks)
    {
        return false;
    }

    /**
     * @return {@code true} if we support historical querying of rainfall values, and should be overriding the rain in that dimension.
     * Note that biome-based climate models do not support this, and so any mechanics relying on that do not function.
     */
    default boolean supportsRain()
    {
        return false;
    }

    /**
     * @return A unit vector representing the horizontal strength of the wind at the given {@code pos}.
     */
    default Vec2 getWind(Level level, BlockPos pos)
    {
        final ICalendar calendar = Calendars.get(level);
        return getWind(level, pos, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    /**
     * @return A unit vector representing the horizontal strength of the wind at the given {@code pos} and
     * timestamp given by {@code calendarTicks}
     */
    default Vec2 getWind(Level level, BlockPos pos, long calendarTicks, int daysInMonth)
    {
        return Vec2.ZERO;
    }

    /**
     * @return A value in the range [0, 1] scaling the sky fog as a % of the render distance
     */
    default float getFog(LevelReader level, BlockPos pos)
    {
        return 0f;
    }
}
