/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.function.ToIntFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.calendar.Season;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;

import org.jetbrains.annotations.Nullable;

public final class TFCColors
{
    public static final ResourceLocation SKY_COLORS_LOCATION = Helpers.identifier("textures/colormap/sky.png");
    public static final ResourceLocation FOG_COLORS_LOCATION = Helpers.identifier("textures/colormap/fog.png");
    public static final ResourceLocation WATER_COLORS_LOCATION = Helpers.identifier("textures/colormap/water.png");
    public static final ResourceLocation WATER_FOG_COLORS_LOCATION = Helpers.identifier("textures/colormap/water_fog.png");
    public static final ResourceLocation FOLIAGE_COLORS_LOCATION = Helpers.identifier("textures/colormap/foliage.png");
    public static final ResourceLocation FOLIAGE_FALL_COLORS_LOCATION = Helpers.identifier("textures/colormap/foliage_fall.png");
    public static final ResourceLocation FOLIAGE_WINTER_COLORS_LOCATION = Helpers.identifier("textures/colormap/foliage_winter.png");
    public static final ResourceLocation GRASS_COLORS_LOCATION = Helpers.identifier("textures/colormap/grass.png");
    public static final ResourceLocation TALL_GRASS_COLORS_LOCATION = Helpers.identifier("textures/colormap/tall_grass.png");

    public static final ColorResolver FRESH_WATER;
    public static final ColorResolver SALT_WATER;

    private static final int COLORMAP_SIZE = 256 * 256;
    private static final int COLORMAP_MASK = COLORMAP_SIZE - 1;

    private static int[] SKY_COLORS_CACHE = new int[COLORMAP_SIZE];
    private static int[] FOG_COLORS_CACHE = new int[COLORMAP_SIZE];
    private static int[] WATER_COLORS_CACHE = new int[COLORMAP_SIZE];
    private static int[] WATER_FOG_COLORS_CACHE = new int[COLORMAP_SIZE];
    private static int[] FOLIAGE_COLORS_CACHE = new int[COLORMAP_SIZE];
    private static int[] FOLIAGE_FALL_COLORS_CACHE = new int[COLORMAP_SIZE];
    private static int[] FOLIAGE_WINTER_COLORS_CACHE = new int[COLORMAP_SIZE];
    private static int[] GRASS_COLORS_CACHE = new int[COLORMAP_SIZE];
    private static int[] TALL_GRASS_COLORS_CACHE = new int[COLORMAP_SIZE];

    static
    {
        // IDEA's code ordering wants to rearrange these fields unless they're initialized after WATER_COLORS_CACHE
        FRESH_WATER = waterColorResolver(TFCColors::getWaterColor);
        SALT_WATER = waterColorResolver(TFCColors::getWaterColor);
    }

    public static void setSkyColors(int[] skyColors)
    {
        SKY_COLORS_CACHE = skyColors;
    }

    public static void setFogColors(int[] fogColors)
    {
        FOG_COLORS_CACHE = fogColors;
    }

    public static void setWaterColors(int[] waterColors)
    {
        WATER_COLORS_CACHE = waterColors;
    }

    public static void setWaterFogColors(int[] waterFogColors)
    {
        WATER_FOG_COLORS_CACHE = waterFogColors;
    }

    public static void setFoliageColors(int[] foliageColorsCache)
    {
        FOLIAGE_COLORS_CACHE = foliageColorsCache;
    }

    public static void setFoliageFallColors(int[] foliageFallColorsCache)
    {
        FOLIAGE_FALL_COLORS_CACHE = foliageFallColorsCache;
    }

    public static void setFoliageWinterColors(int[] foliageWinterColorsCache)
    {
        FOLIAGE_WINTER_COLORS_CACHE = foliageWinterColorsCache;
    }

    public static void setGrassColors(int[] grassColorsCache)
    {
        GRASS_COLORS_CACHE = grassColorsCache;
    }

    public static void setTallGrassColors(int[] tallGrassColorsCache)
    {
        TALL_GRASS_COLORS_CACHE = tallGrassColorsCache;
    }

    public static int getSkyColor(CommonLevelAccessor level, Biome biome, BlockPos pos)
    {
        return TFCBiomes.hasExtension(level, biome) ? getClimateColor(SKY_COLORS_CACHE, pos) : biome.getSkyColor();
    }

    public static int getFogColor(CommonLevelAccessor level, Biome biome, BlockPos pos)
    {
        return TFCBiomes.hasExtension(level, biome) ? getClimateColor(FOG_COLORS_CACHE, pos) : biome.getFogColor();
    }

    public static int getWaterColor(@Nullable BlockPos pos)
    {
        return pos != null ? getClimateColor(WATER_COLORS_CACHE, pos) : -1;
    }

    public static int getWaterFogColor(CommonLevelAccessor level, Biome biome, BlockPos pos)
    {
        return TFCBiomes.hasExtension(level, biome) ? getClimateColor(WATER_FOG_COLORS_CACHE, pos) : biome.getWaterFogColor();
    }

    public static int getSeasonalFoliageColor(@Nullable BlockPos pos, int tintIndex, int autumnIndex)
    {
        final Level level = ClientHelpers.getLevel();
        if (level != null && pos != null)
        {
            return getSeasonalFoliageColor(pos, tintIndex, level, autumnIndex);
        }
        return -1;
    }

    public static int getSeasonalFoliageColor(BlockPos pos, int tintIndex, Level level, int autumnIndex)
    {
        if (tintIndex == 0)
        {
            return getSeasonalFoliageColor(pos, level, autumnIndex);
        }
        return -1;
    }

    /**
     * Gets a color based on average temperature and time of year. Autumn occurs at different times of the year at height-adjusted average temperatures from the poles to 12c
     */
    private static int getSeasonalFoliageColor(BlockPos pos, LevelAccessor level, int autumnIndex)
    {
        ChunkData data = ChunkData.get(level, pos);
        float temp = OverworldClimateModel.getAdjustedAverageTempByElevation(pos, data);
        float timeOfYear = Calendars.CLIENT.getCalendarFractionOfYear();
        final float tempClamped = temp > 12f ? 12f : Math.max(temp, -20f);

        final float cubedTerm = 1.5f * (float) Math.pow(tempClamped + 3f, 3f) / 4913f;
        final float squaredTerm = 0.5f * (float) Math.pow(tempClamped + 3f, 2f) / 289f;
        final float autumnStart = (cubedTerm + squaredTerm + 8.5f) / 12f;
        final float autumnEnd = temp > 12f ? autumnStart : (cubedTerm - squaredTerm + 10.5f) / 12f;
        final float springStart = 1f - autumnEnd;

        if (timeOfYear > autumnEnd)
        {
            return getAverageTempClimateColor(FOLIAGE_WINTER_COLORS_CACHE, pos, temp);
        }
        else if (timeOfYear > autumnStart)
        {
            return getAutumnColor(FOLIAGE_FALL_COLORS_CACHE, timeOfYear, autumnStart, autumnEnd, pos, autumnIndex);
        }
        else if (timeOfYear > springStart)
        {
            return getClimateColor(FOLIAGE_COLORS_CACHE, pos);
        }
        else
        {
            return getAverageTempClimateColor(FOLIAGE_WINTER_COLORS_CACHE, pos, temp);
        }
    }

    public static int getFoliageColor(@Nullable BlockPos pos, int tintIndex)
    {
        if (tintIndex == 0)
        {
            if (pos != null)
            {
                return getClimateColor(FOLIAGE_COLORS_CACHE, pos);
            }
            return getClimateColor(FOLIAGE_COLORS_CACHE, 10f, 250f); // Default values
        }
        return -1;
    }

    public static int getGrassColor(@Nullable BlockPos pos, int tintIndex)
    {
        if (tintIndex == 0 || tintIndex == 1)
        {
            if (pos != null)
            {
                return getClimateColor(GRASS_COLORS_CACHE, pos);
            }
            return getClimateColor(GRASS_COLORS_CACHE, 10f, 250f); // Default values
        }
        return -1;
    }

    public static int getTallGrassColor(@Nullable BlockPos pos, int tintIndex)
    {
        if (tintIndex == 0)
        {
            if (pos != null)
            {
                return getClimateColor(TALL_GRASS_COLORS_CACHE, pos);
            }
            return getClimateColor(TALL_GRASS_COLORS_CACHE, 10f, 250f); // Default values
        }
        return -1;
    }

    /**
     * Gets a season for the current time and position, adjusting for noise based on the position.
     */
    private static Season getAdjustedNoisySeason(BlockPos pos)
    {
        Month currentMonth = Calendars.CLIENT.getCalendarMonthOfYear();
        Season season = currentMonth.getSeason();
        float seasonDelta = 0;
        float monthDelta = Calendars.CLIENT.getCalendarFractionOfMonth();
        switch (currentMonth)
        {
            case FEBRUARY, MAY, AUGUST, NOVEMBER -> seasonDelta = 0.5f * monthDelta;
            case MARCH, JUNE, SEPTEMBER, DECEMBER -> {
                season = season.previous();
                seasonDelta = 0.5f + 0.5f * monthDelta;
            }
        }

        // Smoothly transition - based on when the chunk updates - from one season to the next
        int positionDeltaHash = (Helpers.hash(836494186029734123L, pos) & 255);
        if (positionDeltaHash < 256 * seasonDelta)
        {
            season = season.next();
        }
        return season;
    }

    /**
     * Queries a color map based on temperature and rainfall parameters, by sampling the client temperature and rainfall at a given position. Temperature is horizontal, left is high. Rainfall is vertical, up is high.
     */
    private static int getClimateColor(int[] colorCache, BlockPos pos)
    {
        final Level level = ClientHelpers.getLevel();
        if (level != null)
        {
            final float temperature = Climate.getTemperature(level, pos);
            final float rainfall = Climate.getRainfall(level, pos);
            return getClimateColor(colorCache, temperature, rainfall);
        }
        return 0;
    }

    private static int getAverageTempClimateColor(int[] colorCache, BlockPos pos)
    {
        final Level level = ClientHelpers.getLevel();
        if (level != null)
        {
            final float averageTemperature = Climate.getAverageTemperature(level, pos);
            final float rainfall = Climate.getRainfall(level, pos);
            return getClimateColor(colorCache, averageTemperature, rainfall);
        }
        return 0;
    }

    private static int getAverageTempClimateColor(int[] colorCache, BlockPos pos, float averageTemperature)
    {
        final Level level = ClientHelpers.getLevel();
        if (level != null)
        {
            final float rainfall = Climate.getRainfall(level, pos);
            return getClimateColor(colorCache, averageTemperature, rainfall);
        }
        return 0;
    }


    /**
     * Queries a color map based on temperature and rainfall parameters. Temperature is horizontal, left is high. Rainfall is vertical, up is high.
     */
    private static int getClimateColor(int[] colorCache, float temperature, float rainfall)
    {
        final int temperatureIndex = 255 - Mth.clamp((int) ((temperature + 20f) * 255f / 50f), 0, 255);
        final int rainfallIndex = 255 - Mth.clamp((int) (rainfall * 255f / 500f), 0, 255);
        return colorCache[temperatureIndex | (rainfallIndex << 8)];
    }

    private static int getAutumnColor(int[] colorCache, float timeOfYear, float autumnStart, float autumnEnd, BlockPos pos, int autumnIndex)
    {
        final int positionDeltaHash = (Helpers.hash(836494186029734123L, pos) & 127) - 63;
        final int autumnProgressIndex = (int) Mth.clamp(255f * (timeOfYear - autumnStart) / (autumnEnd - autumnStart) + positionDeltaHash, 0, 255);

        return colorCache[autumnProgressIndex | (autumnIndex << 8)];
    }

    private static ColorResolver waterColorResolver(ToIntFunction<BlockPos> colorAccessor)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        return (biome, x, z) -> {
            cursor.set(x, TFCChunkGenerator.SEA_LEVEL_Y, z);
            return colorAccessor.applyAsInt(cursor);
        };
    }
}