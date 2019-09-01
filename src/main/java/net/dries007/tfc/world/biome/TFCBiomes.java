/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ObjectHolder;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ObjectHolder(value = MOD_ID)
public class TFCBiomes
{
    // Aquatic biomes
    public static final TFCBiome OCEAN = Helpers.getNull(); // Ocean biome found near continents
    public static final TFCBiome DEEP_OCEAN = Helpers.getNull(); // Deep ocean biome covering most all oceans
    public static final TFCBiome DEEP_OCEAN_RIDGE = Helpers.getNull(); // Variant of deep ocean biomes, contains snaking ridge like formations

    // Low biomes
    public static final TFCBiome PLAINS = Helpers.getNull(); // Very flat, slightly above sea level
    public static final TFCBiome HILLS = Helpers.getNull(); // Small hills, slightly above sea level
    public static final TFCBiome LOWLANDS = Helpers.getNull(); // Flat, swamp-like, lots of shallow pools below sea level
    public static final TFCBiome LOW_CANYONS = Helpers.getNull(); // Sharp, small hills, with lots of water / snaking winding rivers

    // Mid biomes
    public static final TFCBiome ROLLING_HILLS = Helpers.getNull(); // Higher hills, above sea level. Some larger / steeper hills
    public static final TFCBiome BADLANDS = Helpers.getNull(); // High, flat area with relief / absolute value based noise carving
    public static final TFCBiome PLATEAU = Helpers.getNull(); // Very high area, very flat top.
    public static final TFCBiome OLD_MOUNTAINS = Helpers.getNull(); // Rounded top mountains, very large hills

    // High biomes
    public static final TFCBiome MOUNTAINS = Helpers.getNull(); // High, picturesque mountains. Pointed peaks, low valleys well above sea level
    public static final TFCBiome FLOODED_MOUNTAINS = Helpers.getNull(); // Mountains with high areas, and low, below sea level valleys. Water is salt water here
    public static final TFCBiome CANYONS = Helpers.getNull(); // Very high flat area with steep relief carving, similar to vanilla mesas

    // Shores
    public static final TFCBiome SHORE = Helpers.getNull(); // Standard shore biome. Different areas have different shores based on geology layers
    public static final TFCBiome STONE_SHORE = Helpers.getNull(); // Shore for mountain biomes

    // Technical biomes
    public static final TFCBiome MOUNTAINS_EDGE = Helpers.getNull(); // Edge biome for mountains
    public static final TFCBiome LAKE = Helpers.getNull(); // Biome for freshwater ocean areas / landlocked oceans

    @ObjectHolder(MOD_ID + ":plains")
    public static final TFCBiome DEFAULT = Helpers.getNull();

    private static final Set<TFCBiome> BIOMES = new HashSet<>();

    @Nonnull
    public static Set<TFCBiome> getBiomes()
    {
        return BIOMES;
    }

    public static BlockState getDebugBlockForBiome(Biome biome)
    {
        if (biome == OCEAN) return Blocks.LIGHT_BLUE_CONCRETE.getDefaultState();
        if (biome == DEEP_OCEAN) return Blocks.BLUE_CONCRETE.getDefaultState();
        if (biome == DEEP_OCEAN_RIDGE) return Blocks.LAPIS_BLOCK.getDefaultState();

        if (biome == PLAINS) return Blocks.GRASS_BLOCK.getDefaultState();
        if (biome == HILLS) return Blocks.MOSSY_COBBLESTONE.getDefaultState();
        if (biome == LOWLANDS) return Blocks.PODZOL.getDefaultState();
        if (biome == LOW_CANYONS) return Blocks.COARSE_DIRT.getDefaultState();

        if (biome == ROLLING_HILLS) return Blocks.GREEN_CONCRETE.getDefaultState();
        if (biome == BADLANDS) return Blocks.TERRACOTTA.getDefaultState();
        if (biome == PLATEAU) return Blocks.BROWN_TERRACOTTA.getDefaultState();
        if (biome == OLD_MOUNTAINS) return Blocks.GRANITE.getDefaultState();

        if (biome == MOUNTAINS) return Blocks.STONE.getDefaultState();
        if (biome == FLOODED_MOUNTAINS) return Blocks.MOSSY_STONE_BRICKS.getDefaultState();
        if (biome == CANYONS) return Blocks.ANDESITE.getDefaultState();

        if (biome == SHORE) return Blocks.SAND.getDefaultState();
        if (biome == STONE_SHORE) return Blocks.GRAVEL.getDefaultState();

        if (biome == MOUNTAINS_EDGE) return Blocks.COBBLESTONE.getDefaultState();
        if (biome == LAKE) return Blocks.LIGHT_BLUE_WOOL.getDefaultState();

        return Blocks.BEDROCK.getDefaultState();
    }

    static void addBiome(TFCBiome biome)
    {
        BIOMES.add(biome);
    }
}
