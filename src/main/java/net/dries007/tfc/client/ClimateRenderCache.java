/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.tracker.WorldTracker;

/**
 * This stores the climate parameters at the current client player location, for quick lookup in rendering purposes
 */
public enum ClimateRenderCache
{
    INSTANCE;

    private long ticks;
    private float averageTemperature;
    private float heightAdjustedAverageTemperature;
    private float temperature;
    private float rainfall;
    private float rainVariance;
    private float monthlyRainfall;
    private float baseGroundwater;
    private float groundwater;
    private float monthlyGroundwater;
    private Vec2 wind = Vec2.ZERO;

    private float lastRainLevel, currRainLevel;

    /**
     * Called on client tick, updates the client parameters for the current client player location
     */
    public void onClientTick()
    {
        final Level level = ClientHelpers.getLevel();
        final Player player = ClientHelpers.getPlayer();
        if (level != null && player != null)
        {
            final BlockPos pos = player.blockPosition();

            ticks = Calendars.CLIENT.getTicks();
            averageTemperature = Climate.getAverageTemperature(level, pos);
            heightAdjustedAverageTemperature = Climate.getAverageTempElevationAdjusted(level, pos);
            temperature = Climate.getTemperature(level, pos);
            rainfall = Climate.getRainfall(level, pos);
            rainVariance = Climate.getRainVariance(level, pos);
            monthlyRainfall = Climate.getMonthlyRainfall(level, pos);
            baseGroundwater = Climate.getBaseGroundwater(level, pos);
            groundwater = Climate.getGroundwater(level, pos);
            monthlyGroundwater = Climate.getMonthlyGroundwater(level, pos);
            wind = Climate.getWindVector(level, pos);

            // Can't call level.getRainLevel() because it's redirected to exactly this
            final float targetRainLevel = level instanceof ClientLevel clientLevel ? clientLevel.rainLevel : 0;

            // We can't invoke EnvironmentHelpers.isRainingOrSnowing() either, because it goes through isRaining() -> getRainLevel()
            final float adjustedTargetRainLevel = WorldTracker.get(level).isRaining(level, pos) ? targetRainLevel : 0f;

            lastRainLevel = currRainLevel;
            if (currRainLevel < adjustedTargetRainLevel)
            {
                currRainLevel += 0.01f;
            }
            else if (currRainLevel > adjustedTargetRainLevel)
            {
                currRainLevel -= 0.01f;
            }
            currRainLevel = Mth.clamp(currRainLevel, 0f, 1f);
        }
    }

    public long getTicks()
    {
        return ticks;
    }

    public float getAverageTemperature()
    {
        return averageTemperature;
    }

    public float getTemperature()
    {
        return temperature;
    }

    public float getMonthlyRainfall()
    {
        return monthlyRainfall;
    }

    public float getRainfall()
    {
        return rainfall;
    }

    public float getRainVariance()
    {
        return rainVariance;
    }

    public float getRainLevel(float partialTick)
    {
        return Mth.lerp(partialTick, lastRainLevel, currRainLevel);
    }

    public float getBaseGroundwater()
    {
        return baseGroundwater;
    }

    public float getGroundwater()
    {
        return groundwater;
    }

    public float getMonthlyGroundwater()
    {
        return monthlyGroundwater;
    }

    public float getHeightAdjustedAverageTemperature()
    {
        return heightAdjustedAverageTemperature;
    }

    public Vec2 getWind()
    {
        return wind;
    }
}
