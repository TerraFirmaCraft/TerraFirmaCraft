/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ObjectHolder;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ObjectHolder(value = MOD_ID)
public class TFCBiomes
{
    // Aquatic biomes
    public static final TFCBiome OCEAN = Helpers.getNull(); // Ocean biome found near continents.
    public static final TFCBiome DEEP_OCEAN = Helpers.getNull(); // Deep ocean biome covering most all oceans.
    public static final TFCBiome DEEP_OCEAN_RIDGE = Helpers.getNull(); // Variant of deep ocean biomes, contains snaking ridge like formations.

    // Low biomes
    public static final TFCBiome PLAINS = Helpers.getNull(); // Very flat, slightly above sea level.
    public static final TFCBiome HILLS = Helpers.getNull(); // Small hills, slightly above sea level.
    public static final TFCBiome LOWLANDS = Helpers.getNull(); // Flat, swamp-like, lots of shallow pools below sea level.
    public static final TFCBiome LOW_CANYONS = Helpers.getNull(); // Sharp, small hills, with lots of water / snaking winding rivers.

    // Mid biomes
    public static final TFCBiome ROLLING_HILLS = Helpers.getNull(); // Higher hills, above sea level. Some larger / steeper hills.
    public static final TFCBiome BADLANDS = Helpers.getNull(); // Very high flat area with steep relief carving, similar to vanilla mesas.
    public static final TFCBiome PLATEAU = Helpers.getNull(); // Very high area, very flat top.
    public static final TFCBiome OLD_MOUNTAINS = Helpers.getNull(); // Rounded top mountains, very large hills.

    // High biomes
    public static final TFCBiome MOUNTAINS = Helpers.getNull(); // High, picturesque mountains. Pointed peaks, low valleys well above sea level.
    public static final TFCBiome FLOODED_MOUNTAINS = Helpers.getNull(); // Mountains with high areas, and low, below sea level valleys. Water is salt water here.
    public static final TFCBiome CANYONS = Helpers.getNull(); // Medium height with snake like ridges, often slightly below sea level

    // Shores
    public static final TFCBiome SHORE = Helpers.getNull(); // Standard shore biome with a sandy beach
    public static final TFCBiome STONE_SHORE = Helpers.getNull(); // Shore for mountain biomes

    // Technical biomes
    public static final TFCBiome MOUNTAINS_EDGE = Helpers.getNull(); // Edge biome for mountains
    public static final TFCBiome LAKE = Helpers.getNull(); // Biome for freshwater ocean areas / landlocked oceans
    public static final TFCBiome RIVER = Helpers.getNull(); // Biome for river channels

    // Lazy load the biomes in order to allow registry replacements
    private static final Lazy<Set<TFCBiome>> BIOMES = Lazy.of(() -> {
        Set<TFCBiome> values = new HashSet<>();
        for (TFCBiome biome : Arrays.asList(OCEAN, DEEP_OCEAN, DEEP_OCEAN_RIDGE, PLAINS, HILLS, LOWLANDS, LOW_CANYONS, ROLLING_HILLS, BADLANDS, PLATEAU, OLD_MOUNTAINS, MOUNTAINS, FLOODED_MOUNTAINS, CANYONS, SHORE, STONE_SHORE, MOUNTAINS_EDGE, LAKE, RIVER))
        {
            if (biome == null)
            {
                throw new IllegalStateException("Unable to locate biome! Did an object holder not copy correctly, or was a registry entry changed?");
            }
            values.add(biome);
        }
        return values;
    });

    @Nonnull
    public static Set<TFCBiome> getBiomes()
    {
        return BIOMES.get();
    }
}
