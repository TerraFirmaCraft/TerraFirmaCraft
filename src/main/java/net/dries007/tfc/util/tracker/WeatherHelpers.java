/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tracker;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.ClimateModel;

/**
 * Handler for custom weather and weather effects.
 */
public final class WeatherHelpers
{
    /**
     * Replaces a call to {@link Biome#getPrecipitationAt(BlockPos)} with one that is aware of both the local climate,
     * and the local rainfall. Note that of all the biome climate based methods, this is the only one we need to
     * aggressively replace callers of. All others are either (1) invoked in world gen we do not use, (2) invoked by
     * {@link ServerLevel#tickPrecipitation} which we disable, or (3) delegate / query something else which we do handle.
     *
     * @param defaultValue The default value to return, if the climate model does not support rain simulation.
     * @return the current precipitation mode at the given position, as per the climate model.
     */
    public static Biome.Precipitation getPrecipitationAt(Level level, BlockPos pos, Biome.Precipitation defaultValue)
    {
        final WorldTracker tracker = WorldTracker.get(level);
        final ClimateModel model = tracker.getClimateModel();

        if (!model.supportsRain())
        {
            return defaultValue;
        }

        final long calendarTicks = Calendars.get(level).getCalendarTicks();
        final float rainIntensity = tracker.isWeatherEnabled() ? model.getRain(calendarTicks) : -1;
        final float rainAverage = model.getRainfall(level, pos);

        return isPrecipitating(rainIntensity, rainAverage)
            ? model.getTemperature(level, pos) > 0f
                ? Biome.Precipitation.RAIN
                : Biome.Precipitation.SNOW
            : Biome.Precipitation.NONE;
    }

    public static boolean isPrecipitating(float rainIntensity, float rainAverage)
    {
        return rainIntensity > Mth.clampedMap(rainAverage, ClimateModel.MINIMUM_RAINFALL, ClimateModel.MAXIMUM_RAINFALL, 1, 0);
    }

    /**
     * Called in replacement of {@link ServerLevel#advanceWeatherCycle()} for worlds that have a climate-based weather cycle
     * @return {@code true} if the weather cycle was handled for this dimension.
     */
    public static boolean doClimateBasedWeatherCycle(ServerLevel level)
    {
        final WorldTracker tracker = WorldTracker.get(level);
        final ClimateModel model = tracker.getClimateModel();

        if (!model.supportsRain())
        {
            return false;
        }

        final long calendarTicks = Calendars.SERVER.getCalendarTicks();
        final float rain = tracker.isWeatherEnabled() ? model.getRain(calendarTicks) : -1;
        final boolean thunder = tracker.isWeatherEnabled() && model.getThunder(calendarTicks);
        final boolean wasRaining = level.isRaining();

        // Update vanilla's current and previous rain level, based on if it is currently raining. All clients will see this,
        // but clients will only visually see rain if it is within the rainfall range to occur. Clients also do their own
        // interpolation of this value
        level.oRainLevel = level.rainLevel;
        level.rainLevel = Mth.clamp(level.rainLevel + (rain >= 0 ? 0.01f : -0.01f), 0, 1);

        level.oThunderLevel = level.thunderLevel;
        level.thunderLevel = Mth.clamp(level.thunderLevel + (thunder ? 0.01f : -0.01f), 0, 1);

        // Now, if any updates were made, do syncing to all clients, via the vanilla packets.
        if (level.oRainLevel != level.rainLevel)
        {
            sendToAllInDimension(level, ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, level.rainLevel);
        }

        if (level.oThunderLevel != level.thunderLevel)
        {
            sendToAllInDimension(level, ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, level.thunderLevel);
        }

        if (wasRaining != level.isRaining())
        {
            sendToAllInDimension(level, wasRaining
                ? ClientboundGameEventPacket.STOP_RAINING
                : ClientboundGameEventPacket.START_RAINING, 0);
            sendToAllInDimension(level, ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, level.rainLevel);
            sendToAllInDimension(level, ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, level.thunderLevel);
        }

        return true;
    }

    private static void sendToAllInDimension(ServerLevel level, ClientboundGameEventPacket.Type event, float value)
    {
        level.getServer()
            .getPlayerList()
            .broadcastAll(new ClientboundGameEventPacket(event, value), level.dimension());
    }
}
