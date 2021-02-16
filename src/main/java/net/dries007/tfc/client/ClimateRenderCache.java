/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCache;
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
        World world = Minecraft.getInstance().world;
        PlayerEntity player = Minecraft.getInstance().player;
        if (world != null && player != null)
        {
            BlockPos pos = player.getPosition();
            ChunkData data = ChunkDataCache.CLIENT.getOrEmpty(pos);

            ticks = Calendars.CLIENT.getTicks();
            averageTemperature = data.getAverageTemp(pos);
            temperature = Climate.calculateTemperature(pos.getZ(), pos.getY(), data.getAverageTemp(pos), Calendars.CLIENT.getCalendarTicks(), Calendars.CLIENT.getCalendarDaysInMonth());
            rainfall = data.getRainfall(pos);
            plateTectonicsInfo = data.getPlateTectonicsInfo();
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
