/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.function.Supplier;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.common.MinecraftForge;

import net.dries007.tfc.mixin.accessor.BiomeAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.events.SelectClimateModelEvent;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

/**
 * Central location for all climate handling.
 * Models are assigned during world load with {@link SelectClimateModelEvent}
 *
 * This model is responsible for calculating the climate parameters (temperature, rainfall, precipitation, etc.) at given positions and locations.
 * All methods here are really bouncers to their relevant methods in {@link ClimateModel}, and are named the same. See them for documentation.
 *
 * @see ClimateModel
 */
public final class Climate
{
    private static final BiMap<ResourceLocation, ClimateModelType> REGISTRY = HashBiMap.create();

    /**
     * Register a new climate model factory.
     * The supplier should return a <strong>new instance</strong> each time it is invoked, as it may be used for multiple dimensions.
     * The only exception to this, is if the climate model has no persistent data (such as {@link BiomeBasedClimateModel}.
     */
    public static synchronized ClimateModelType register(ResourceLocation id, Supplier<ClimateModel> model)
    {
        final ClimateModelType type = new ClimateModelType(model, id);
        REGISTRY.put(id, type);
        return type;
    }

    public static ClimateModel create(ResourceLocation id)
    {
        return REGISTRY.getOrDefault(id, ClimateModels.BIOME_BASED.get()).create();
    }

    public static ResourceLocation getId(ClimateModel model)
    {
        return REGISTRY.inverse().getOrDefault(model.type(), ClimateModels.BIOME_BASED.get().id());
    }

    public static float getTemperature(Level level, BlockPos pos, long calendarTick, int daysInMonth)
    {
        return model(level).getTemperature(level, pos, calendarTick, daysInMonth);
    }

    public static float getTemperature(Level level, BlockPos pos, ICalendar calendar, long calendarTick)
    {
        return model(level).getTemperature(level, pos, calendarTick, calendar.getCalendarDaysInMonth());
    }

    public static float getTemperature(Level level, BlockPos pos, ICalendar calendar)
    {
        return model(level).getTemperature(level, pos, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    public static float getTemperature(Level level, BlockPos pos)
    {
        return getTemperature(level, pos, Calendars.get(level));
    }

    public static float getAverageTemperature(Level level, BlockPos pos)
    {
        return model(level).getAverageTemperature(level, pos);
    }

    public static float getRainfall(Level level, BlockPos pos)
    {
        return model(level).getRainfall(level, pos);
    }

    public static float getFogginess(Level level, BlockPos pos)
    {
        return model(level).getFogginess(level, pos, Calendars.get(level).getTicks());
    }

    public static float getWaterFogginess(Level level, BlockPos pos)
    {
        return model(level).getWaterFogginess(level, pos, Calendars.get(level).getTicks());
    }

    /**
     * @see Biome#warmEnoughToRain(BlockPos)
     */
    public static boolean warmEnoughToRain(Level level, BlockPos pos)
    {
        return getVanillaBiomeTemperature(level, pos) >= 0.15f;
    }

    /**
     * Defensive version, when it's unknown if we're in world generation or not
     * @see Biome#warmEnoughToRain(BlockPos)
     */
    public static boolean warmEnoughToRain(LevelReader level, BlockPos pos, Biome fallback)
    {
        return getVanillaBiomeTemperatureSafely(level, pos, fallback) >= 0.15f;
    }

    public static void onChunkLoad(WorldGenLevel level, ChunkAccess chunk, ChunkData chunkData)
    {
        model(level.getLevel()).onChunkLoad(level, chunk, chunkData);
    }

    public static void onWorldLoad(ServerLevel level)
    {
        final SelectClimateModelEvent event = new SelectClimateModelEvent(level);
        MinecraftForge.EVENT_BUS.post(event);
        level.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(c -> c.setClimateModel(event.getModel()));
        model(level).onWorldLoad(level);
    }

    /**
     * Calculates the temperature, scaled to vanilla like values.
     * References: 0.15 ~ 0 C (freezing point of water). Vanilla typically ranges from -0.5 to +1 in the overworld.
     * This scales 0 C -> 0.15, -30 C -> -0.51, +30 C -> 0.801
     */
    public static float toVanillaTemperature(float actualTemperature)
    {
        return actualTemperature * 0.0217f + 0.15f;
    }

    /**
     * The inverse of {@link #toVanillaTemperature(float)}
     */
    public static float toActualTemperature(float vanillaTemperature)
    {
        return (vanillaTemperature - 0.15f) / 0.0217f;
    }

    public static float getVanillaBiomeTemperature(Level level, BlockPos pos)
    {
        return toVanillaTemperature(getTemperature(level, pos, Calendars.get(level)));
    }

    public static float getVanillaBiomeTemperatureSafely(LevelReader maybeLevel, BlockPos pos, Biome fallback)
    {
        final Level unsafeLevel = Helpers.getUnsafeLevel(maybeLevel);
        if (unsafeLevel != null)
        {
            final ICalendar calendar = Calendars.get(maybeLevel);
            final ClimateModel model = model(unsafeLevel);
            if (maybeLevel instanceof WorldGenRegion worldGenLevel)
            {
                // World generation. If the model supports direct calls, then find the correct chunk data and pass it in
                if (model instanceof WorldGenClimateModel worldGenModel)
                {
                    final ChunkData data = ChunkDataProvider.get(worldGenLevel).get(worldGenLevel.getChunk(pos));
                    return toVanillaTemperature(worldGenModel.getTemperature(worldGenLevel, pos, data, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth()));
                }
            }
            else
            {
                // Pretty sure we're not in world generation, so we can call the model directly.
                return toVanillaTemperature(model.getTemperature(maybeLevel, pos, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth()));
            }
        }
        return ((BiomeAccessor) (Object) fallback).invoke$getTemperature(pos);
    }

    public static ClimateModel model(Level level)
    {
        return level.getCapability(WorldTrackerCapability.CAPABILITY)
            .map(WorldTracker::getClimateModel)
            .orElse(ClimateModels.BIOME_BASED.get().create());
    }
}
