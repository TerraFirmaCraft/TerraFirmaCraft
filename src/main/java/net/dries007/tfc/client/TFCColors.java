/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.calendar.Season;

public final class TFCColors
{
    public static final ResourceLocation WATER_COLORS_LOCATION = Helpers.identifier("textures/colormap/water.png");
    public static final ResourceLocation WATER_FOG_COLORS_LOCATION = Helpers.identifier("textures/colormap/water_fog.png");
    public static final ResourceLocation FOLIAGE_COLORS_LOCATION = Helpers.identifier("textures/colormap/foliage.png");
    public static final ResourceLocation FOLIAGE_FALL_COLORS_LOCATION = Helpers.identifier("textures/colormap/foliage_fall.png");
    public static final ResourceLocation FOLIAGE_WINTER_COLORS_LOCATION = Helpers.identifier("textures/colormap/foliage_winter.png");
    public static final ResourceLocation GRASS_COLORS_LOCATION = Helpers.identifier("textures/colormap/grass.png");

    private static int[] WATER_COLORS_CACHE = new int[256 * 256];
    private static int[] FOLIAGE_COLORS_CACHE = new int[256 * 256];
    private static int[] FOLIAGE_FALL_COLORS_CACHE = new int[256 * 256];
    private static int[] FOLIAGE_WINTER_COLORS_CACHE = new int[256 * 256];
    private static int[] GRASS_COLORS_CACHE = new int[256 * 256];

    public static void setWaterColors(int[] waterColors)
    {
        TFCColors.WATER_COLORS_CACHE = waterColors;
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

    public static int getWaterColor(float temperature, float rainfall)
    {
        int temperatureIndex = MathHelper.clamp((int) ((temperature + 30f) * 255f / 60f), 0, 255);
        int rainfallIndex = 255 - MathHelper.clamp((int) (rainfall * 255f / 500f), 0, 255);
        return WATER_COLORS_CACHE[temperatureIndex | (rainfallIndex << 8)];
    }

    public static int getSeasonalFoliageColor(BlockState state, @Nullable BlockPos pos, int tintIndex, int fallColorBaseIndex)
    {
        if (pos != null && tintIndex == 0)
        {
            final Season season = state.get(TFCBlockStateProperties.SEASON_NO_SPRING);
            final Month month = Calendars.CLIENT.getCalendarMonthOfYear();
            switch (adjustSeason(season, month))
            {
                case SPRING:
                case SUMMER:
                    return getClimateColor(FOLIAGE_COLORS_CACHE, pos);
                case FALL:
                {
                    // todo: slightly vary the location chosen based on position
                    // int index = NoiseUtil.hash(pos.getY(), pos.getX(), pos.getZ()) & 0xFFFF;
                    return FOLIAGE_FALL_COLORS_CACHE[fallColorBaseIndex];
                }
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
     * Queries a color map based on temperature and rainfall parameters, by sampling the client temperature and rainfall at a given position. Temperature is horizontal, left is high. Rainfall is vertical, up is high.
     */
    private static int getClimateColor(int[] colorCache, BlockPos pos)
    {
        return getClimateColor(colorCache, Climate.getTemperature(pos), Climate.getRainfall(pos));
    }

    /**
     * Queries a color map based on temperature and rainfall parameters. Temperature is horizontal, left is high. Rainfall is vertical, up is high.
     */
    private static int getClimateColor(int[] colorCache, float temperature, float rainfall)
    {
        final int temperatureIndex = MathHelper.clamp((int) ((temperature + 30f) * 255f / 60f), 0, 255);
        final int rainfallIndex = 255 - MathHelper.clamp((int) (rainfall * 255f / 500f), 0, 255);
        return colorCache[temperatureIndex | (rainfallIndex << 8)];
    }

    /**
     * Given the current month, and the season property of a leaf block, return the season that should render
     * The first month of a rendered season use the season property, all others use the actual season
     * No need to include spring -> summer transition as leaves don't render differently during those two seasons
     */
    private static Season adjustSeason(Season seasonIn, Month monthIn)
    {
        switch (monthIn)
        {
            case SEPTEMBER:
                return seasonIn == Season.SUMMER ? Season.SUMMER : Season.FALL; // Transition to fall
            case DECEMBER:
                return seasonIn == Season.FALL ? Season.FALL : Season.WINTER; // Transition to winter
            case MARCH:
                return seasonIn == Season.WINTER ? Season.WINTER : Season.SUMMER; // Transition to spring
            default:
                return monthIn.getSeason();
        }
    }
}
