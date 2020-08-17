/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.ArrayList;
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
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MOD_ID);
    private static final List<BiomeVariants> ALL_BIOMES = new ArrayList<>(); // This has to come before any biomes, otherwise it is null during register()

    // Aquatic biomes
    public static final BiomeVariants OCEAN = register("ocean", (temp, rain) -> new OceanBiome(false, temp, rain)); // Ocean biome found near continents.
    public static final BiomeVariants DEEP_OCEAN = register("deep_ocean", (temp, rain) -> new OceanBiome(true, temp, rain)); // Deep ocean biome covering most all oceans.

    // Low biomes
    public static final BiomeVariants PLAINS = register("plains", PlainsBiome::new).setSpawnBiome(); // Very flat, slightly above sea level.
    public static final BiomeVariants HILLS = register("hills", (temp, rain) -> new HillsBiome(16, temp, rain)).setSpawnBiome(); // Small hills, slightly above sea level.
    public static final BiomeVariants LOWLANDS = register("lowlands", LowlandsBiome::new).setSpawnBiome(); // Flat, swamp-like, lots of shallow pools below sea level.
    public static final BiomeVariants LOW_CANYONS = register("low_canyons", (temp, rain) -> new CanyonsBiome(-5, 14, temp, rain)).setSpawnBiome(); // Sharp, small hills, with lots of water / snaking winding rivers.

    // Mid biomes
    public static final BiomeVariants ROLLING_HILLS = register("rolling_hills", (temp, rain) -> new HillsBiome(28, temp, rain)).setSpawnBiome(); // Higher hills, above sea level. Some larger / steeper hills.
    public static final BiomeVariants BADLANDS = register("badlands", BadlandsBiome::new).setSpawnBiome(); // Very high flat area with steep relief carving, similar to vanilla mesas.
    public static final BiomeVariants PLATEAU = register("plateau", PlateauBiome::new).setSpawnBiome(); // Very high area, very flat top.
    public static final BiomeVariants OLD_MOUNTAINS = register("old_mountains", (temp, rain) -> new MountainsBiome(48, 28, false, temp, rain)).setSpawnBiome(); // Rounded top mountains, very large hills.

    // High biomes
    public static final BiomeVariants MOUNTAINS = register("mountains", (temp, rain) -> new MountainsBiome(48, 56, false, temp, rain)).setSpawnBiome(); // High, picturesque mountains. Pointed peaks, low valleys well above sea level.
    public static final BiomeVariants FLOODED_MOUNTAINS = register("flooded_mountains", (temp, rain) -> new MountainsBiome(30, 64, true, temp, rain)).setSpawnBiome(); // Mountains with high areas, and low, below sea level valleys. Water is salt water here.
    public static final BiomeVariants CANYONS = register("canyons", (temp, rain) -> new CanyonsBiome(-7, 26, temp, rain)).setSpawnBiome(); // Medium height with snake like ridges, often slightly below sea level

    // Shores
    public static final BiomeVariants SHORE = register("shore", (temperature, rainfall) -> new ShoreBiome(true, temperature, rainfall)); // Standard shore biome with a sandy beach
    public static final BiomeVariants STONE_SHORE = register("stone_shore", (temperature, rainfall) -> new ShoreBiome(false, temperature, rainfall)); // Shore for mountain biomes

    // Technical biomes
    public static final BiomeVariants MOUNTAINS_EDGE = register("mountains_edge", (temp, rain) -> new MountainsBiome(36, 34, false, temp, rain)); // Edge biome for mountains
    public static final BiomeVariants LAKE = register("lake", LakeBiome::new); // Biome for freshwater ocean areas / landlocked oceans
    public static final BiomeVariants RIVER = register("river", RiverBiome::new); // Biome for river channels


    public static Set<TFCBiome> getBiomes()
    {
        return ALL_BIOMES.stream().flatMap(holder -> holder.getAll().stream()).map(RegistryObject::get).collect(Collectors.toSet());
    }

    public static List<Biome> getSpawnBiomes()
    {
        return ALL_BIOMES.stream().filter(BiomeVariants::isSpawnBiome).flatMap(holder -> holder.getAll().stream()).map(RegistryObject::get).collect(Collectors.toList());
    }

    /**
     * Delayed addition of Features / SurfaceBuilders to biomes (as all things involved are RegistryObjects)
     */
    public static void setup()
    {
        getBiomes().forEach(TFCBiome::registerFeatures);
    }

    private static BiomeVariants register(String baseName, BiomeVariants.IFactory factory)
    {
        BiomeVariants variants = new BiomeVariants(BIOMES, baseName, factory);
        ALL_BIOMES.add(variants);
        return variants;
    }
}
