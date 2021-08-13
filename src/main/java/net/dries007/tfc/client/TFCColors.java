/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.function.ToIntFunction;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.ColorResolver;

import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.calendar.Season;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCache;
import net.dries007.tfc.world.noise.NoiseUtil;

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

    public static int getSkyColor(BlockPos pos)
    {
        return getClimateColor(SKY_COLORS_CACHE, pos);
    }

    public static int getFogColor(BlockPos pos)
    {
        return getClimateColor(FOG_COLORS_CACHE, pos);
    }

    public static int getWaterColor(@Nullable BlockPos pos)
    {
        return pos != null ? getClimateColor(WATER_COLORS_CACHE, pos) : -1;
    }

    public static int getWaterFogColor(BlockPos pos)
    {
        return getClimateColor(WATER_FOG_COLORS_CACHE, pos);
    }

    public static int getSeasonalFoliageColor(@Nullable BlockPos pos, int tintIndex)
    {
        if (pos != null && tintIndex == 0)
        {
            switch (getAdjustedNoisySeason(pos))
            {
                case SPRING:
                case SUMMER:
                    return getClimateColor(FOLIAGE_COLORS_CACHE, pos);
                case FALL:
                    int index = NoiseUtil.hash(pos.getX() * 9283491, pos.getY() * 7483921, pos.getZ() * 673283712);
                    return FOLIAGE_FALL_COLORS_CACHE[index & COLORMAP_MASK];
                case WINTER:
                    return getClimateColor(FOLIAGE_WINTER_COLORS_CACHE, pos);
            }
        }
        return -1;
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
        if (tintIndex == 0)
        {
            if (pos != null)
            {
                return getClimateColor(GRASS_COLORS_CACHE, pos);
            }
            return getClimateColor(GRASS_COLORS_CACHE, 10f, 250f); // Default values
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
            case FEBRUARY:
            case MAY:
            case AUGUST:
            case NOVEMBER:
                seasonDelta = 0.5f * monthDelta;
                break;
            case MARCH:
            case JUNE:
            case SEPTEMBER:
            case DECEMBER:
                season = season.previous();
                seasonDelta = 0.5f + 0.5f * monthDelta;
                break;
        }

        // Smoothly transition - based on when the chunk updates - from one season to the next
        int positionDeltaHash = (NoiseUtil.hash(pos.getX() * 2834123, pos.getY() * 92349, pos.getZ() * 4792831) & 255);
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
        ChunkData data = ChunkDataCache.CLIENT.getOrEmpty(pos);
        float temperature = Climate.calculateTemperature(pos.getZ(), pos.getY(), data.getAverageTemp(pos), Calendars.CLIENT.getCalendarTicks(), Calendars.CLIENT.getCalendarDaysInMonth());
        float rainfall = data.getRainfall(pos);
        return getClimateColor(colorCache, temperature, rainfall);
    }

    /**
     * Queries a color map based on temperature and rainfall parameters. Temperature is horizontal, left is high. Rainfall is vertical, up is high.
     */
    private static int getClimateColor(int[] colorCache, float temperature, float rainfall)
    {
        final int temperatureIndex = 255 - MathHelper.clamp((int) ((temperature + 30f) * 255f / 60f), 0, 255);
        final int rainfallIndex = 255 - MathHelper.clamp((int) (rainfall * 255f / 500f), 0, 255);
        return colorCache[temperatureIndex | (rainfallIndex << 8)];
    }

    private static ColorResolver waterColorResolver(ToIntFunction<BlockPos> colorAccessor)
    {
        final BlockPos.Mutable cursor = new BlockPos.Mutable();
        return (biome, x, z) -> {
            cursor.set(x, TFCChunkGenerator.SEA_LEVEL, z);
            return colorAccessor.applyAsInt(cursor);
        };
    }
}