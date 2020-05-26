/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCBiomes
{
    public static final DeferredRegister<Biome> BIOMES = new DeferredRegister<>(ForgeRegistries.BIOMES, MOD_ID);

    // Aquatic biomes
    public static final BiomeVariantHolder OCEAN = new BiomeVariantHolder("ocean", (temp, rain) -> new OceanBiome(false, temp, rain)); // Ocean biome found near continents.
    public static final BiomeVariantHolder DEEP_OCEAN = new BiomeVariantHolder("deep_ocean", (temp, rain) -> new OceanBiome(true, temp, rain)); // Deep ocean biome covering most all oceans.
    public static final BiomeVariantHolder DEEP_OCEAN_RIDGE = new BiomeVariantHolder("deep_ocean_ridge", (temp, rain) -> new OceanBiome(true, temp, rain)); // Variant of deep ocean biomes, contains snaking ridge like formations.

    // Low biomes
    public static final BiomeVariantHolder PLAINS = new BiomeVariantHolder("plains", PlainsBiome::new); // Very flat, slightly above sea level.
    public static final BiomeVariantHolder HILLS = new BiomeVariantHolder("hills", (temp, rain) -> new HillsBiome(16, temp, rain)); // Small hills, slightly above sea level.
    public static final BiomeVariantHolder LOWLANDS = new BiomeVariantHolder("lowlands", LowlandsBiome::new); // Flat, swamp-like, lots of shallow pools below sea level.
    public static final BiomeVariantHolder LOW_CANYONS = new BiomeVariantHolder("low_canyons", (temp, rain) -> new CanyonsBiome(-5, 14, temp, rain)); // Sharp, small hills, with lots of water / snaking winding rivers.

    // Mid biomes
    public static final BiomeVariantHolder ROLLING_HILLS = new BiomeVariantHolder("rolling_hills", (temp, rain) -> new HillsBiome(28, temp, rain)); // Higher hills, above sea level. Some larger / steeper hills.
    public static final BiomeVariantHolder BADLANDS = new BiomeVariantHolder("badlands", BadlandsBiome::new); // Very high flat area with steep relief carving, similar to vanilla mesas.
    public static final BiomeVariantHolder PLATEAU = new BiomeVariantHolder("plateau", PlateauBiome::new); // Very high area, very flat top.
    public static final BiomeVariantHolder OLD_MOUNTAINS = new BiomeVariantHolder("old_mountains", (temp, rain) -> new MountainsBiome(48, 28, false, temp, rain)); // Rounded top mountains, very large hills.

    // High biomes
    public static final BiomeVariantHolder MOUNTAINS = new BiomeVariantHolder("mountains", (temp, rain) -> new MountainsBiome(48, 56, false, temp, rain)); // High, picturesque mountains. Pointed peaks, low valleys well above sea level.
    public static final BiomeVariantHolder FLOODED_MOUNTAINS = new BiomeVariantHolder("flooded_mountains", (temp, rain) -> new MountainsBiome(30, 64, true, temp, rain)); // Mountains with high areas, and low, below sea level valleys. Water is salt water here.
    public static final BiomeVariantHolder CANYONS = new BiomeVariantHolder("canyons", (temp, rain) -> new CanyonsBiome(-7, 26, temp, rain)); // Medium height with snake like ridges, often slightly below sea level

    // Shores
    public static final BiomeVariantHolder SHORE = new BiomeVariantHolder("shore", (temperature, rainfall) -> new ShoreBiome(true, temperature, rainfall)); // Standard shore biome with a sandy beach
    public static final BiomeVariantHolder STONE_SHORE = new BiomeVariantHolder("stone_shore", (temperature, rainfall) -> new ShoreBiome(false, temperature, rainfall)); // Shore for mountain biomes

    // Technical biomes
    public static final BiomeVariantHolder MOUNTAINS_EDGE = new BiomeVariantHolder("mountains_edge", (temp, rain) -> new MountainsBiome(36, 34, false, temp, rain)); // Edge biome for mountains
    public static final BiomeVariantHolder LAKE = new BiomeVariantHolder("lake", LakeBiome::new); // Biome for freshwater ocean areas / landlocked oceans
    public static final BiomeVariantHolder RIVER = new BiomeVariantHolder("river", RiverBiome::new); // Biome for river channels

    private static final List<BiomeVariantHolder> ALL_BIOMES = Arrays.asList(OCEAN, DEEP_OCEAN, DEEP_OCEAN_RIDGE, PLAINS, HILLS, LOWLANDS, LOW_CANYONS, ROLLING_HILLS, BADLANDS, PLATEAU, OLD_MOUNTAINS, MOUNTAINS, FLOODED_MOUNTAINS, CANYONS, SHORE, STONE_SHORE, MOUNTAINS_EDGE, LAKE, RIVER);

    private static final List<BiomeVariantHolder> SPAWN_BIOMES = Arrays.asList(PLAINS, HILLS, LOWLANDS, LOW_CANYONS, ROLLING_HILLS, BADLANDS, PLATEAU, OLD_MOUNTAINS, MOUNTAINS, FLOODED_MOUNTAINS, CANYONS, SHORE, STONE_SHORE, MOUNTAINS_EDGE);

    public static Set<TFCBiome> getBiomes()
    {
        return ALL_BIOMES.stream().flatMap(holder -> holder.getAll().stream()).map(RegistryObject::get).collect(Collectors.toSet());
    }

    public static List<Biome> getSpawnBiomes()
    {
        return SPAWN_BIOMES.stream().flatMap(holder -> holder.getAll().stream()).map(RegistryObject::get).collect(Collectors.toList());
    }

    /**
     * Delayed addition of Features / SurfaceBuilders to biomes (as all things involved are RegistryObjects
     */
    public static void setup()
    {
        getBiomes().forEach(TFCBiome::registerFeatures);
    }
}
