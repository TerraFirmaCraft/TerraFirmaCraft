/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.PlateTectonicsClassification;

/**
 * This stores the climate parameters at the current client player location, for quick lookup in rendering purposes
 */
public enum ClimateRenderCache
{
    INSTANCE;

    private long ticks;
    private float averageTemperature;
    private float temperature;
    private float rainfall;
    private PlateTectonicsClassification plateTectonicsInfo = PlateTectonicsClassification.OCEANIC;

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
            temperature = Climate.getTemperature(level, pos);
            rainfall = Climate.getRainfall(level, pos);
            plateTectonicsInfo = ChunkData.get(level, pos).getPlateTectonicsInfo();
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

    public float getRainfall()
    {
        return rainfall;
    }

    public PlateTectonicsClassification getPlateTectonicsInfo()
    {
        return plateTectonicsInfo;
    }
}
