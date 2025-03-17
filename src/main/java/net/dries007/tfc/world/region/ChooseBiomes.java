/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import net.dries007.tfc.world.layer.framework.Area;

import static net.dries007.tfc.world.layer.TFCLayers.*;

public enum ChooseBiomes implements RegionTask
{
    INSTANCE;

    private static final int[] MOUNTAIN_ALTITUDE_BIOMES = {MOUNTAINS, MOUNTAINS, MOUNTAINS, OLD_MOUNTAINS, OLD_MOUNTAINS, PLATEAU, HIGHLANDS};
    private static final int[] OCEANIC_MOUNTAIN_ALTITUDE_BIOMES = {VOLCANIC_MOUNTAINS, VOLCANIC_OCEANIC_MOUNTAINS, VOLCANIC_OCEANIC_MOUNTAINS, OCEANIC_MOUNTAINS, OCEANIC_MOUNTAINS, ROLLING_HILLS};
    private static final int[][] ALTITUDE_BIOMES = {
        {PLAINS, PLAINS, HILLS, HILLS, ROLLING_HILLS, LOW_CANYONS, LOWLANDS, LOWLANDS}, // Low
        {PLAINS, HILLS, ROLLING_HILLS, HIGHLANDS, INVERTED_BADLANDS, BADLANDS, PLATEAU, CANYONS, LOW_CANYONS}, // Mid
        {HIGHLANDS, HIGHLANDS, HIGHLANDS, ROLLING_HILLS, BADLANDS, PLATEAU, PLATEAU, OLD_MOUNTAINS, OLD_MOUNTAINS}, // High
    };
    private static final int[][] ICE_SHEET_ALTITUDE_BIOMES = {
        {ICE_SHEET, ICE_SHEET, ICE_SHEET, ICE_SHEET, ICE_SHEET, ICE_SHEET_TUYAS}, // Low
        {ICE_SHEET, ICE_SHEET, ICE_SHEET, ICE_SHEET, ICE_SHEET, ICE_SHEET_TUYAS, ICE_SHEET, ICE_SHEET_TUYAS}, // Mid
        {ICE_SHEET, ICE_SHEET, ICE_SHEET, ICE_SHEET, ICE_SHEET_TUYAS, ICE_SHEET_TUYAS, ICE_SHEET_MOUNTAINS, ICE_SHEET_MOUNTAINS}, // High
    };
    private static final int[][] PALEO_ICE_SHEET_ALTITUDE_BIOMES = {
        {PATTERNED_GROUND, INVERTED_PATTERNED_GROUND, KNOB_AND_KETTLE, KNOB_AND_KETTLE, KNOB_AND_KETTLE, DRUMLINS, TUYAS, LOWLANDS, LOWLANDS}, // Low
        {PATTERNED_GROUND, KNOB_AND_KETTLE, DRUMLINS, DRUMLINS, DRUMLINS, DRUMLINS, TUYAS, TUYAS}, // Mid
        {DRUMLINS, DRUMLINS, DRUMLINS, BADLANDS, BADLANDS, PLATEAU, PLATEAU, PLATEAU, PLATEAU, ICE_SHEET_MOUNTAINS}, // High
    };
    private static final int[] KNOB_AND_KETTLE_BIOMES = {KNOB_AND_KETTLE, PATTERNED_GROUND, INVERTED_PATTERNED_GROUND};
    private static final int[] ISLAND_BIOMES = {PLAINS, HILLS, ROLLING_HILLS, VOLCANIC_OCEANIC_MOUNTAINS, VOLCANIC_OCEANIC_MOUNTAINS};
    private static final int[] MID_DEPTH_OCEAN_BIOMES = {DEEP_OCEAN, OCEAN, OCEAN, OCEAN_REEF, OCEAN_REEF, OCEAN_REEF};
    private static final int[] DRY_LOWLANDS_REPLACEMENT_BIOMES = {MUD_FLATS, MUD_FLATS, GRASSY_DUNES, GRASSY_DUNES};

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final Area blobArea = context.generator().biomeArea.get();
        final long rngSeed = context.random.nextLong();
        final long climateSeed = context.random.nextLong();

        for (final var point : region.points())
        {
            final int areaSeed = blobArea.get(point.x, point.z);
            if (point.island())
            {
                point.biome = randomSeededFrom(rngSeed, areaSeed, ISLAND_BIOMES);
            }
            else if (point.mountain())
            {
                final float temp = point.temperature;
                if (point.coastalMountain())
                {
                    // Different temperature limits used because biomes at different elevations
                    final float maxIceSheetTemp = -16f + 0.006f * point.rainfall;
                    if (temp < maxIceSheetTemp + 2)
                    {
                        point.biome = ICE_SHEET_OCEANIC_MOUNTAINS;
                    }
                    else if (temp < maxIceSheetTemp + 6)
                    {
                        point.biome = GLACIATED_OCEANIC_MOUNTAINS;
                    }
                    else if (temp < maxIceSheetTemp + 10)
                    {
                        point.biome = GLACIALLY_CARVED_OCEANIC_MOUNTAINS;
                    }
                    else
                    {
                        point.biome = randomSeededFrom(rngSeed, areaSeed, OCEANIC_MOUNTAIN_ALTITUDE_BIOMES);
                    }
                }
                else
                {
                    final float maxIceSheetTemp = -14f + 0.006f * point.rainfall;
                    if (temp < maxIceSheetTemp)
                    {
                        point.biome = ICE_SHEET_MOUNTAINS;
                    }
                    else if (temp < maxIceSheetTemp + 4)
                    {
                        point.biome = GLACIATED_MOUNTAINS;
                    }
                    else if (temp < maxIceSheetTemp + 10)
                    {
                        point.biome = GLACIALLY_CARVED_MOUNTAINS;
                    }
                    else
                    {
                        point.biome = randomSeededFrom(rngSeed, areaSeed, MOUNTAIN_ALTITUDE_BIOMES);
                    }
                }
            }
            else if (point.land())
            {
                final float maxIceSheetTemp = -17f + 0.006f * point.rainfall;
                final float temp = point.temperature;
                if (temp < maxIceSheetTemp)
                {
                    int biome = randomSeededFrom(rngSeed, areaSeed, ICE_SHEET_ALTITUDE_BIOMES[point.discreteBiomeAltitude()]);

                    if (point.distanceToOcean < 3 && isFlatIceSheet(biome))
                    {
                        biome = ICE_SHEET_OCEANIC;
                    }
                    point.biome = biome;
                }
                else if (temp < maxIceSheetTemp + 1)
                {
                    point.biome = ICE_SHEET_EDGE;
                }
                else if (temp < maxIceSheetTemp + 2.5)
                {
                    point.biome = randomSeededFrom(rngSeed, areaSeed, KNOB_AND_KETTLE_BIOMES);
                }
                else if (temp < maxIceSheetTemp + 6)
                {
                    point.biome = randomSeededFrom(rngSeed, areaSeed, PALEO_ICE_SHEET_ALTITUDE_BIOMES[point.discreteBiomeAltitude()]);
                }
                else
                {
                    point.biome = randomSeededFrom(rngSeed, areaSeed, ALTITUDE_BIOMES[point.discreteBiomeAltitude()]);
                }
            }
            else if (point.baseOceanDepth < 3)
            {
                point.biome = OCEAN;
            }
            else if (point.baseOceanDepth > 9)
            {
                point.biome = DEEP_OCEAN_TRENCH;
            }
            else if (point.baseOceanDepth >= 5 || point.distanceToEdge < 2)
            {
                point.biome = DEEP_OCEAN;
            }
            else
            {
                point.biome = randomSeededFrom(rngSeed, areaSeed, MID_DEPTH_OCEAN_BIOMES);
            }

            // Add hot spot biomes
            final byte age = point.hotSpotAge;
            if (age > 0)
            {
                if (age == 4 && point.biome == OCEAN || point.biome == DEEP_OCEAN || point.biome == OCEAN_REEF || point.biome == DEEP_OCEAN_TRENCH)
                {
                    point.biome = SUNKEN_SHIELD_VOLCANO;
                }
                else
                {
                    point.biome = getHotSpotBiome(point.hotSpotAge);
                }
            }

            // Adjust certain biome placements by climate. Low, freshwater biomes don't make much sense appearing in
            // Replacements for very low rainfall areas
            final float minRainForLowFreshWaterBiomes = 90f + Math.floorMod(areaSeed ^ climateSeed, 40);
            final float rainfall = point.rainfall;
            final float temperature = point.temperature;
            if (rainfall < minRainForLowFreshWaterBiomes)
            {
                if (rainfall <= 55)
                {
                    if (point.biome == LOWLANDS || point.biome == LOW_CANYONS) point.biome = SALT_FLATS;
                    else if (point.biome == HILLS || point.biome == ROLLING_HILLS || point.biome == PLATEAU) point.biome = DUNE_SEA;
                }
                else if (point.biome == LOWLANDS || point.biome == LOW_CANYONS) point.biome = randomSeededFrom(rngSeed, areaSeed, DRY_LOWLANDS_REPLACEMENT_BIOMES);
                else if (point.biome == HILLS || point.biome == ROLLING_HILLS) point.biome = GRASSY_DUNES;
            }
            if (rainfall < 145 && (point.biome == PATTERNED_GROUND || point.biome == INVERTED_PATTERNED_GROUND)) point.biome = STONE_CIRCLES;

            // Prevent badlands from appearing in very high rainfall environments
            final float maxRainfallForBadlands = 420f + Math.floorMod(areaSeed ^ climateSeed, 40);
            if (rainfall > maxRainfallForBadlands)
            {
                if (point.biome == BADLANDS) point.biome = HIGHLANDS;
                else if (point.biome == INVERTED_BADLANDS) point.biome = ROLLING_HILLS;
            }

            // Special Biome Glaciation
            final float maxIceSheetTemp = -14f + 0.006f * rainfall;
            if (point.land() && temperature < maxIceSheetTemp)
            {
                final int biome = point.biome;
                if (biome == ACTIVE_SHIELD_VOLCANO || biome == DORMANT_SHIELD_VOLCANO || biome == EXTINCT_SHIELD_VOLCANO) point.biome = ICE_SHEET_SHIELD_VOLCANO;
            }
            else if (temperature < maxIceSheetTemp + 4f)
            {
                final int biome = point.biome;
                if (biome == ACTIVE_SHIELD_VOLCANO || biome == DORMANT_SHIELD_VOLCANO || biome == EXTINCT_SHIELD_VOLCANO) point.biome = GLACIATED_SHIELD_VOLCANO;
            }

            // Karst Biomes
            if (point.isSurfaceRockKarst)
            {
                // High rainfall karst biomes
                if (rainfall > 375)
                {
                    // Check for hot, wet climates to place tower karsts
                    if (rainfall > 425 && rainfall + 10 * temperature > 500)
                    {
                        point.biome = getTowerKarstBiome(point.biome);
                    }
                    //Tropical/Subtropical wet areas not filled in by towers are Shilin
                    else if (temperature > 9)
                    {
                        point.biome = getShilinBiome(point.biome);
                    }
                    // Colder wet biomes are Burren
                    else if (temperature < 0)
                    {
                        point.biome = getBurrenBiome(point.biome);
                    }
                    else
                    {
                        point.biome = getDolineBiome(point.biome);
                    }
                }
                else if (rainfall > 250)
                {
                    if (temperature > 5)
                    {
                        point.biome = getCenoteBiome(point.biome);
                    }
                    else
                    {
                        point.biome = getDolineBiome(point.biome);
                    }
                }

            }
        }
    }

    private int getHotSpotBiome(int age)
    {
        if (age == 4)
            return ANCIENT_SHIELD_VOLCANO;
        if (age == 3)
            return EXTINCT_SHIELD_VOLCANO;
        if (age == 2)
            return DORMANT_SHIELD_VOLCANO;
        if (age == 1)
            return ACTIVE_SHIELD_VOLCANO;
        return PLAINS;
    }

    private int getTowerKarstBiome(int biome)
    {
        if (biome == SALT_MARSH)
            return TOWER_KARST_BAY;
        else if (biome == LOWLANDS)
            return TOWER_KARST_LAKE;
        else if (biome == PLAINS || biome == LOW_CANYONS)
            return TOWER_KARST_PLAINS;
        else if (biome == CANYONS)
            return TOWER_KARST_CANYONS;
        else if (biome == HILLS || biome == ROLLING_HILLS || biome == BADLANDS)
            return TOWER_KARST_HILLS;
        else if (biome == HIGHLANDS || biome == INVERTED_BADLANDS)
            return TOWER_KARST_HIGHLANDS;
        else if (biome == PLATEAU)
            return EXTREME_DOLINE_PLATEAU;
        else if (biome == OLD_MOUNTAINS || biome == MOUNTAINS || biome == OCEANIC_MOUNTAINS)
            return EXTREME_DOLINE_MOUNTAINS;
        else
            return biome;
    }

    private int getShilinBiome(int biome)
    {
        if (biome == PLAINS)
            return SHILIN_PLAINS;
        if (biome == CANYONS)
            return SHILIN_CANYONS;
        if (biome == ROLLING_HILLS || biome == BADLANDS)
            return SHILIN_HILLS;
        if (biome == PLATEAU || biome == INVERTED_BADLANDS)
            return SHILIN_PLATEAU;
        if (biome == HIGHLANDS)
            return SHILIN_HIGHLANDS;
        return biome;
    }

    private int getBurrenBiome(int biome)
    {
        if (biome == PLAINS || biome == CANYONS)
            return BURREN_PLAINS;
        if (biome == BADLANDS || biome == HILLS || biome == ROLLING_HILLS)
            return BURREN_BADLANDS;
        if (biome == DRUMLINS)
            return BURREN_ROCHE_MOUTONEE;
        if (biome == INVERTED_BADLANDS || biome == HIGHLANDS)
            return BURREN_BADLANDS_TALL;
        if (biome == PLATEAU)
            return BURREN_PLATEAU;
        else
            return biome;
    }

    private int getDolineBiome(int biome)
    {
        if (biome == CANYONS)
            return DOLINE_CANYONS;
        else if (biome == PLAINS || biome == LOW_CANYONS)
            return DOLINE_PLAINS;
        else if (biome == HILLS)
            return DOLINE_HILLS;
        else if (biome == ROLLING_HILLS)
            return DOLINE_ROLLING_HILLS;
        else if (biome == HIGHLANDS)
            return DOLINE_HIGHLANDS;
        else if (biome == PLATEAU)
            return DOLINE_PLATEAU;
        else
            return biome;
    }

    private int getCenoteBiome(int biome)
    {
        if (biome == CANYONS)
            return CENOTE_CANYONS;
        else if (biome == PLAINS || biome == LOW_CANYONS)
            return CENOTE_PLAINS;
        else if (biome == HILLS)
            return CENOTE_HILLS;
        else if (biome == ROLLING_HILLS)
            return CENOTE_ROLLING_HILLS;
        else if (biome == HIGHLANDS)
            return CENOTE_HIGHLANDS;
        else if (biome == PLATEAU)
            return CENOTE_PLATEAU;
        else
            return biome;
    }

    private int randomSeededFrom(long rngSeed, int areaSeed, int[] choices)
    {
        return choices[Math.floorMod(rngSeed ^ areaSeed, choices.length)];
    }
}
