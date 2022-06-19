/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tracker;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.ServerLevelData;

import net.dries007.tfc.util.calendar.Calendars;

/**
 * Manager for advanced weather mechanics, that simulate local weather
 * Vanilla already handles switching rain/thunder on and off periodically
 * It then linearly interpolates between different weather events.
 *
 * Some data:
 * - Rain is for [12000, 24000] ticks on, [12000, 180000] ticks off, or 18000 on / 96000 off. P(rain) = 0.1875
 * - Thunder is for [3600, 15600] ticks on, [12000, 180000] ticks off, or 9600 on / 96000 off. P(thunder | rain) = 0.1, and P(thunder) = 0.01875
 *
 * Our model assumes that vanilla values for rain match a climate of 250mm annual rainfall - so we scale vanilla's model to rain twice as frequently - P(rain) and P(thunder | rain), and then exclude cases at < 500mm rainfall.
 *
 * We have two effects we can use to accomplish that
 * 1. Linearly scale rainfall duration compared to it's midpoint from full duration to zero. Pros: smooth interpolation. Cons: this doesn't mimic vanilla behavior at 250mm, rather it makes rain twice as common and last half as long.
 * 2. Each rainfall, assign an intensity, which only above that rainfall values will be raining. Pros: this mimics vanilla behavior w.r.t P(rain) and rain duration exactly. Cons: not smooth interpolation, rain won't appear to 'move'.
 *
 * So, we do both and average them.
 *
 * @see ServerLevel#advanceWeatherCycle()
 */
public final class WeatherHelpers
{
    private static final int MIN_RAIN_TIME = 18000;
    private static final int MAX_RAIN_TIME = 24000;

    private static final int MIN_RAIN_DELAY_TIME = 12000; // Same as vanilla
    private static final int MAX_RAIN_DELAY_TIME = 84000; // Lowered, so average rain delay time is half vanilla

    /**
     * Called before {@link ServerLevel#advanceWeatherCycle()}
     */
    public static void preAdvancedWeatherCycle(ServerLevel level)
    {
        if (level.dimensionType().hasSkyLight() && level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE) && level.getLevelData() instanceof ServerLevelData serverLevelData)
        {
            // Vanilla already handles decrementing the weather clear, thunder, and rain time counters.
            // We want to prevent the counters from reaching zero - since we want to reset them ourselves, and also know when they reset, so we can reset rain intensity, and duration values.

            int rainTime = serverLevelData.getRainTime();
            if (serverLevelData.getClearWeatherTime() <= 0 && rainTime <= 0)
            {
                // Vanilla would reset the rain time this tick, so we do it first
                // We also take this tick to record two things: the *midpoint* of the raining period, and a randomized intensity value for this rain period.
                if (serverLevelData.isRaining())
                {
                    rainTime = Mth.randomBetweenInclusive(level.random, MIN_RAIN_TIME, MAX_RAIN_TIME);

                    final long rainStartTick = Calendars.get(level).getTicks();
                    final long rainEndTick = rainStartTick + rainTime;
                    final float rainIntensity = level.random.nextFloat();

                    level.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(tracker -> tracker.setWeatherData(rainStartTick, rainEndTick, rainIntensity));
                }
                else
                {
                    rainTime = Mth.randomBetweenInclusive(level.random, MIN_RAIN_DELAY_TIME, MAX_RAIN_DELAY_TIME);
                }

                serverLevelData.setRainTime(rainTime);
            }
        }
    }

    /**
     * This is a mapped and documented version of {@link ServerLevel#advanceWeatherCycle()}. It's not used, just kept here for reference.
     */
    private static void advanceWeatherCycleVanillaImplementation(ServerLevel level, ServerLevelData serverLevelData)
    {
        // Called every tick
        boolean isRaining = level.isRaining();
        if (level.dimensionType().hasSkyLight())
        {
            if (level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE))
            {
                int weatherClearTime = serverLevelData.getClearWeatherTime(); // Ticks that the weather will remain clear for
                int thunderTime = serverLevelData.getThunderTime();
                int rainTime = serverLevelData.getRainTime();

                boolean isCurrentlyThundering = serverLevelData.isThundering();
                boolean isCurrentlyRaining = serverLevelData.isRaining();

                if (weatherClearTime > 0)
                {
                    // If clear, set to default values for clear weather
                    weatherClearTime--;
                    thunderTime = isCurrentlyThundering ? 0 : 1;
                    rainTime = isCurrentlyRaining ? 0 : 1;
                    isCurrentlyThundering = false;
                    isCurrentlyRaining = false;
                }
                else
                {
                    // Not clear - there are zero ticks of weather clear time
                    if (thunderTime > 0)
                    {
                        // It's currently thundering? or are these just ticks spent waiting for thunder
                        thunderTime--; // Count down thundering ticks
                        if (thunderTime == 0)
                        {
                            // When we reach zero, swap thundering and non-thundering
                            isCurrentlyThundering = !isCurrentlyThundering;
                        }
                    }
                    else if (isCurrentlyThundering)
                    {
                        // When it's thundering but no thunder time, we pick a random duration for it to thunder for
                        // (thundering, 0 ticks) -> (thundering, [3600, 15600] ticks)
                        // (thundering, >0 ticks) -> stays the same
                        thunderTime = Mth.randomBetweenInclusive(level.random, 3600, 15600);
                    }
                    else
                    {
                        // If it's *NOT* thundering, and thunder time < 0, we pick a random LONG time
                        // This is the time UNTIL it will start thundering again.
                        thunderTime = Mth.randomBetweenInclusive(level.random, 12000, 180000);
                    }

                    if (rainTime > 0)
                    {
                        // Does the same with rain, always counts down if there is a counter to be counted
                        rainTime--;
                        if (rainTime == 0)
                        {
                            // If we reach zero, swaps the current setup
                            isCurrentlyRaining = !isCurrentlyRaining;
                        }
                    }
                    else if (isCurrentlyRaining)
                    {
                        // If we're at zero and RAINING, then we need to start raining for a shorter duration
                        rainTime = Mth.randomBetweenInclusive(level.random, 12000, 24000);
                    }
                    else
                    {
                        // If we're at zero and NOT RAINING, then we need to start a LONG timer
                        rainTime = Mth.randomBetweenInclusive(level.random, 12000, 180000);
                    }
                }

                // Update the data
                serverLevelData.setThunderTime(thunderTime);
                serverLevelData.setRainTime(rainTime);
                serverLevelData.setClearWeatherTime(weatherClearTime);
                serverLevelData.setThundering(isCurrentlyThundering);
                serverLevelData.setRaining(isCurrentlyRaining);
            }

            // Linearly interpolation
            // o = Old
            // Change actual thunder level by 0.01 each tick based on if we are actually thundering or not
            level.oThunderLevel = level.thunderLevel;
            if (serverLevelData.isThundering())
            {
                level.thunderLevel += 0.01F;
            }
            else
            {
                level.thunderLevel -= 0.01F;
            }
            level.thunderLevel = Mth.clamp(level.thunderLevel, 0.0F, 1.0F);

            // Do the same with rain
            level.oRainLevel = level.rainLevel;
            if (serverLevelData.isRaining())
            {
                level.rainLevel += 0.01F;
            }
            else
            {
                level.rainLevel -= 0.01F;
            }

            level.rainLevel = Mth.clamp(level.rainLevel, 0.0F, 1.0F);

            // rainLevel and thunderLevel are now nicely clamped between [0, 1], and smoothly interpolating between values
            // Note when accessing, getThunderLevel() returns thunderLevel * rainLevel;
            // So if it's thundering but not raining, then it won't be doing either
        }

        if (level.oRainLevel != level.rainLevel)
        {
            level.getServer().getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, level.rainLevel), level.dimension());
        }

        if (level.oThunderLevel != level.thunderLevel)
        {
            level.getServer().getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, level.thunderLevel), level.dimension());
        }

        /* The function in use here has been replaced in order to only send the weather info to players in the correct dimension,
         * rather than to all players on the server. This is what causes the client-side rain, as the
         * client believes that it has started raining locally, rather than in another dimension.
         */
        if (isRaining != level.isRaining())
        {
            if (isRaining)
            {
                level.getServer().getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0F), level.dimension());
            }
            else
            {
                level.getServer().getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0F), level.dimension());
            }

            level.getServer().getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, level.rainLevel), level.dimension());
            level.getServer().getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, level.thunderLevel), level.dimension());
        }

    }
}
