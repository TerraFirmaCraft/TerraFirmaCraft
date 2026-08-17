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
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.overworld.SolarCalculator;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.biome.TFCBiomes;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public final class TFCColors
{
    public static final ResourceLocation SKY_COLORS_LOCATION = Helpers.identifier("textures/colormap/sky.png");
    public static final ResourceLocation FOG_COLORS_LOCATION = Helpers.identifier("textures/colormap/fog.png");
    public static final ResourceLocation WATER_COLORS_LOCATION = Helpers.identifier("textures/colormap/water.png");
    public static final ResourceLocation WATER_FOG_COLORS_LOCATION = Helpers.identifier("textures/colormap/water_fog.png");
    public static final ResourceLocation FOLIAGE_COLORS_LOCATION = Helpers.identifier("textures/colormap/foliage.png");
    public static final ResourceLocation FOLIAGE_SUMMER_COLORS_LOCATION = Helpers.identifier("textures/colormap/foliage.png");
    public static final ResourceLocation FOLIAGE_FALL_COLORS_LOCATION = Helpers.identifier("textures/colormap/foliage_fall.png");
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
    private static int[] FOLIAGE_SUMMER_COLORS_CACHE = new int[COLORMAP_SIZE];
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

    public static void setFoliageSummerColors(int[] foliageSummerColorsCache)
    {
        FOLIAGE_SUMMER_COLORS_CACHE = foliageSummerColorsCache;
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
        if (tintIndex == 0)
        {
            if (pos != null)
            {
                return getSeasonalFoliageColor(pos, autumnIndex);
            }
            return getClimateColor(FOLIAGE_COLORS_CACHE, 10f, 250f); // Default values
        }
        return -1;
    }

    /**
     * Uses similar logic to {@link net.dries007.tfc.client.model.LeavesBlockModel#getModelFromBlockState} to display different colors for leaf blocks at different times of year
     * As an overview, the colormaps used are:
     * Winter - Uniform brown - Displayed in winter months in sufficiently cold climates, or during sufficiently extreme dry seasons of sufficiently warm climates
     * Summer - Variable green based on rainfall and time of year - Light green in spring/early wet season, darker green in summer and for evergreen trees/climates
     * Autumn - Variable bright colors based on the species of tree and the time of year, progressing from green at the start of autumn, to brown at the end
     */
    private static int getSeasonalFoliageColor(BlockPos pos, int autumnIndex)
    {
        final Level level = ClientHelpers.getLevel();
        final BlockPos seaLevelPos = new BlockPos(pos.getX(), SEA_LEVEL_Y, pos.getZ());
        float temp = Climate.getAverageTemperature(level, seaLevelPos);
        float rainVar = Climate.getRainfallVariance(level, pos);
        if ((temp > 11.7 && temp < 12.8) || (rainVar > 0.38 && rainVar < 0.42))
        {
            final int positionClimateHash = (Helpers.hash(912381187503828153L, pos) & 127);
            temp += (float) (positionClimateHash - 63) / 4_000f;
            rainVar += (float) (positionClimateHash - 63) / 60_000f;
        }
        final float rainVarAbs = Math.abs(rainVar);

        // Shortcut if evergreen climate
        if (temp > 12f && Math.abs(rainVar) < 0.4)
        {
            return getGreenSeasonFoliageColor(pos);
        }

        float timeOfYear = Calendars.CLIENT.getCalendarFractionOfYear();

        // See Desmos: https://www.desmos.com/calculator/jw5zkjxtnz
        final float x;
        final boolean inNorthernHemisphere = SolarCalculator.getInNorthernHemisphere(pos.getZ(), ClimateRenderCache.INSTANCE.getHemisphereScale());
        float seasonOffset = 0;
        if (temp <= 12f)
        {
            // Numbers chosen to create a 2.5-month summer at -20c avg, and a 12-month "summer" at 15c avg
            x = 1.25f * Math.max(temp, -20f) + 7.6f;
            if (!inNorthernHemisphere)
            {
                seasonOffset = 0.5f;
            }
        }
        else
        {
            // For dry-season controlled climates, the minimum rain must be below 120
            final float avgRain = Climate.getAverageRainfall(level, seaLevelPos);
            final float minRain = avgRain * (1 - rainVarAbs);

            // Small gap in temperature is so that there are small evergreen bands between dry-season controlled areas and winter-controlled areas
            if (rainVarAbs > 0.4 && temp > 12.5f && minRain <= 120)
            {
                if (rainVar < 0)
                {
                    seasonOffset = 0.5f;
                }
                // Numbers chosen to create a 4-month wet season at max rain var & min rain = 0, and a 12-month "wet season" at minimum rain var & min rain = 120
                // Uses multiple variables to ensure smooth transitions, and that biomes that have green grass year-round do not lose leaves
                x = -.2604f * (0.4f - rainVarAbs) * (120f - minRain) + 18.75f + 5.3f;
            }
            // If not in any of the above areas, must be in an evergreen border-belt
            else
            {
                return getGreenSeasonFoliageColor(pos);
            }
        }

        final float cubedTerm = x * x * x / 4096; // 1 / 16^3
        final float squaredTerm = x * x / 256; // 1 / 16^2

        // Offset the seasons by six months if in southern hemisphere, or if dry season is in the summer
        // Positional hashing to fuzz the time of year per-block
        final int positionDeltaHash = (Helpers.hash(836494187578334123L, pos) & 127);
        timeOfYear = (1 + timeOfYear + seasonOffset + ((positionDeltaHash - 63) / 4096f)) % 1;

        final float autumnStart = (cubedTerm - squaredTerm + 8.5f) / 12f;
        final float autumnEnd = (cubedTerm - squaredTerm + 10.5f) / 12f;

        if (timeOfYear > autumnEnd)
        {
            // Winter brown
            return getWinterFoliageColor();
        }
        else if (timeOfYear > autumnStart)
        {
            return getAutumnColor(FOLIAGE_FALL_COLORS_CACHE, timeOfYear, autumnStart, autumnEnd, pos, autumnIndex);
        }
        final float springStart = 1f - autumnEnd;
        if (timeOfYear > springStart)
        {
            return getSpringSummerColor(FOLIAGE_SUMMER_COLORS_CACHE, timeOfYear, springStart, autumnStart, pos);
        }
        else
        {
            // Winter brown
            return getWinterFoliageColor();
        }
    }

    public static int getWinterFoliageColor()
    {
        return 0x7c592b;
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
     * Queries a color map based on temperature and rainfall parameters, by sampling the client temperature and rainfall at a given position. Temperature is horizontal, left is high. Rainfall is vertical, up is high.
     */
    private static int getClimateColor(int[] colorCache, BlockPos pos)
    {
        final Level level = ClientHelpers.getLevel();
        if (level != null)
        {
            final ClimateModel model = Climate.get(level);
            final float temperature = model.getInstantTemperature(level, pos);
            final float groundwater = model.getInstantGroundwater(level, pos);
            return getClimateColor(colorCache, temperature, groundwater);
        }
        return 0;
    }

    /**
     * Queries a color map based on temperature and groundwater parameters. Temperature is horizontal, left is high. Groundwater is vertical, up is high.
     * Values
     */
    private static int getClimateColor(int[] colorCache, float temperature, float groundwater)
    {
        final int temperatureIndex = 255 - Mth.clamp((int) ((temperature + 20f) * 255f / 50f), 0, 255);
        final int rainfallIndex = 255 - Mth.clamp((int) (groundwater * 255f / 500f), 0, 255);
        return colorCache[temperatureIndex | (rainfallIndex << 8)];
    }

    private static int getAverageClimateColor(int[] colorCache, BlockPos pos, float averageTemperature)
    {
        final Level level = ClientHelpers.getLevel();
        if (level != null)
        {
            final float groundwater = Climate.getAverageGroundwater(level, pos);
            return getClimateColor(colorCache, averageTemperature, groundwater);
        }
        return 0;
    }

    /**
     * Queries a color map based on current groundwater and the time of year. Time is horizontal, left is spring. Groundwater is vertical, up is high.
     */
    private static int getSpringSummerColor(int[] colorCache, float timeOfYear, float springStartTime, float autumnStartTime, BlockPos pos)
    {
        final Level level = ClientHelpers.getLevel();
        if (level != null)
        {
            final ClimateModel model = Climate.get(level);
            final float groundwater = model.getInstantGroundwater(level, pos);


            final int summerProgressIndex = Mth.clamp((int) (255f * (timeOfYear - springStartTime) / (autumnStartTime - springStartTime)), 0, 255);
            final int rainfallIndex = 255 - Mth.clamp((int) (groundwater * 255f / 500f), 0, 255);

            return colorCache[summerProgressIndex | (rainfallIndex << 8)];
        }
        return 0;
    }

    /**
     * Queries a color map based on current groundwater, and a constant time of year, mid-summer. Used for deciduous trees in evergreen climates. Groundwater is vertical, up is high.
     */
    private static int getGreenSeasonFoliageColor(BlockPos pos)
    {
        final Level level = ClientHelpers.getLevel();
        if (level != null)
        {
            final ClimateModel model = Climate.get(level);
            final float groundwater = model.getInstantGroundwater(level, pos);
            final int rainfallIndex = 255 - Mth.clamp((int) (groundwater * 255f / 500f), 0, 255);

            return FOLIAGE_SUMMER_COLORS_CACHE[127 | (rainfallIndex << 8)];
        }
        return 0;
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