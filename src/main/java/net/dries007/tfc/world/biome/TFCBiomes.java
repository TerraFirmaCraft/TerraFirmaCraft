/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.world.carver.TFCWorldCarvers;
import net.dries007.tfc.world.feature.TFCFeatures;
import net.dries007.tfc.world.placement.TFCPlacements;
import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCBiomes
{
    public static final DeferredRegister<Biome> BIOMES = new DeferredRegister<>(ForgeRegistries.BIOMES, MOD_ID);

    // Aquatic biomes
    public static final BiomeVariantHolder OCEAN = new BiomeVariantHolder("ocean", (temp, rain) -> new OceanBiome(false, temp, rain)); // Ocean biome found near continents.
    public static final BiomeVariantHolder DEEP_OCEAN = new BiomeVariantHolder("deep_ocean", (temp, rain) -> new OceanBiome(true, temp, rain)); // Deep ocean biome covering most all oceans.
    public static final BiomeVariantHolder DEEP_OCEAN_RIDGE = new BiomeVariantHolder("deep_ocean_ridge", (temp, rain) -> new OceanBiome(true, temp, rain)); // Variant of deep ocean biomes, contains snaking ridge like formations.

    // Low biomes
    public static final BiomeVariantHolder PLAINS = new BiomeVariantHolder("plains", (temp, rain) -> new PlainsBiome(-4, 10, temp, rain)); // Very flat, slightly above sea level.
    public static final BiomeVariantHolder HILLS = new BiomeVariantHolder("hills", (temp, rain) -> new HillsBiome(16, temp, rain)); // Small hills, slightly above sea level.
    public static final BiomeVariantHolder LOWLANDS = new BiomeVariantHolder("lowlands", LowlandsBiome::new); // Flat, swamp-like, lots of shallow pools below sea level.
    public static final BiomeVariantHolder LOW_CANYONS = new BiomeVariantHolder("low_canyons", (temp, rain) -> new CanyonsBiome(-5, 14, temp, rain)); // Sharp, small hills, with lots of water / snaking winding rivers.

    // Mid biomes
    public static final BiomeVariantHolder ROLLING_HILLS = new BiomeVariantHolder("rolling_hills", (temp, rain) -> new HillsBiome(28, temp, rain)); // Higher hills, above sea level. Some larger / steeper hills.
    public static final BiomeVariantHolder BADLANDS = new BiomeVariantHolder("badlands", BadlandsBiome::new); // Very high flat area with steep relief carving, similar to vanilla mesas.
    public static final BiomeVariantHolder PLATEAU = new BiomeVariantHolder("plateau", (temp, rain) -> new PlainsBiome(20, 30, temp, rain)); // Very high area, very flat top.
    public static final BiomeVariantHolder OLD_MOUNTAINS = new BiomeVariantHolder("old_mountains", (temp, rain) -> new MountainsBiome(48, 28, false, temp, rain)); // Rounded top mountains, very large hills.

    // High biomes
    public static final BiomeVariantHolder MOUNTAINS = new BiomeVariantHolder("mountains", (temp, rain) -> new MountainsBiome(48, 56, false, temp, rain)); // High, picturesque mountains. Pointed peaks, low valleys well above sea level.
    public static final BiomeVariantHolder FLOODED_MOUNTAINS = new BiomeVariantHolder("flooded_mountains", (temp, rain) -> new MountainsBiome(30, 64, true, temp, rain)); // Mountains with high areas, and low, below sea level valleys. Water is salt water here.
    public static final BiomeVariantHolder CANYONS = new BiomeVariantHolder("canyons", (temp, rain) -> new CanyonsBiome(-7, 26, temp, rain)); // Medium height with snake like ridges, often slightly below sea level

    // Shores
    public static final BiomeVariantHolder SHORE = new BiomeVariantHolder("shore", (temp, rain) -> new ShoreBiome(temp, rain)); // Standard shore biome with a sandy beach
    public static final BiomeVariantHolder STONE_SHORE = new BiomeVariantHolder("stone_shore", (temp, rain) -> new ShoreBiome(temp, rain)); // Shore for mountain biomes

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
     * Called after all registry events
     * Initialize biome features that require them. This includes features, surface builders, carvers, etc.
     *
     * For an interpretation of the block mapping for surface builders, see {@link net.dries007.tfc.world.ChunkBlockReplacer}
     */
    public static void setup()
    {
        addOceanCarvers(OCEAN);
        OCEAN.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, getUnderwaterSurfaceConfig(biome)));

        addOceanCarvers(DEEP_OCEAN);
        DEEP_OCEAN.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, getUnderwaterSurfaceConfig(biome)));

        addOceanCarvers(DEEP_OCEAN_RIDGE);
        DEEP_OCEAN_RIDGE.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, getUnderwaterSurfaceConfig(biome)));

        addCarvers(PLAINS);
        PLAINS.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG));

        addCarvers(HILLS);
        HILLS.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG));

        addCarvers(LOWLANDS);
        LOWLANDS.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG));

        addCarvers(LOW_CANYONS);
        LOW_CANYONS.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG));

        addCarvers(ROLLING_HILLS);
        ROLLING_HILLS.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG));

        addCarvers(BADLANDS);
        BADLANDS.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.WOODED_BADLANDS, SurfaceBuilder.RED_SAND_WHITE_TERRACOTTA_GRAVEL_CONFIG));

        addCarvers(PLATEAU);
        PLATEAU.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.MOUNTAIN, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG));

        addCarvers(OLD_MOUNTAINS);
        OLD_MOUNTAINS.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.GRAVELLY_MOUNTAIN, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG));

        addCarvers(MOUNTAINS);
        MOUNTAINS.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.MOUNTAIN, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG));

        addOceanCarvers(FLOODED_MOUNTAINS);
        FLOODED_MOUNTAINS.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.GRAVELLY_MOUNTAIN, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG));

        addCarvers(CANYONS);
        CANYONS.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG));

        SHORE.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, TFCSurfaceBuilders.SANDSTONE_CONFIG));

        STONE_SHORE.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, TFCSurfaceBuilders.RED_SANDSTONE_CONFIG));

        addCarvers(MOUNTAINS_EDGE);
        MOUNTAINS_EDGE.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.MOUNTAIN, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG));

        addCarvers(LAKE);
        LAKE.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.SAND_CONFIG));

        addCarvers(RIVER);
        RIVER.getAll().forEach(biome -> biome.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.SAND_CONFIG));

        // Features applied to ALL biomes
        for (Biome biome : getBiomes())
        {
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, TFCFeatures.VEINS.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(NoPlacementConfig.NO_PLACEMENT_CONFIG)));

            biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.BOULDERS.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configure(new ChanceConfig(20))));
            biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.FISSURES.get().withConfiguration(new BlockStateFeatureConfig(Blocks.WATER.getDefaultState())).withPlacement(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configure(new ChanceConfig(60))));
            biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, TFCFeatures.FISSURES.get().withConfiguration(new BlockStateFeatureConfig(Blocks.LAVA.getDefaultState())).withPlacement(TFCPlacements.FLAT_SURFACE_WITH_CHANCE.get().configure(new ChanceConfig(80))));
        }
    }

    public static void addCarvers(BiomeVariantHolder biomeIn)
    {
        biomeIn.getAll().forEach(biome -> biome.get().addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCWorldCarvers.CAVE.get(), new ProbabilityConfig(0.10f))));
        biomeIn.getAll().forEach(biome -> biome.get().addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCWorldCarvers.CANYON.get(), new ProbabilityConfig(0.015f))));

        biomeIn.getAll().forEach(biome -> biome.get().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.LARGE_CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.02f)))));
        biomeIn.getAll().forEach(biome -> biome.get().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.09f)))));
    }

    public static void addOceanCarvers(BiomeVariantHolder biomeIn)
    {
        biomeIn.getAll().forEach(biome -> biome.get().addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCWorldCarvers.CAVE.get(), new ProbabilityConfig(0.06666667f))));
        biomeIn.getAll().forEach(biome -> biome.get().addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCWorldCarvers.CANYON.get(), new ProbabilityConfig(0.02f))));

        // todo: what with all the caves, these get really ugly when they intersect other cave systems. Is there any way these can be improved?
        //biomeIn.get().addCarver(GenerationStage.Carving.LIQUID, Biome.createCarver(TFCWorldCarvers.UNDERWATER_CANYON.get(), new ProbabilityConfig(0.02f)));
        //biomeIn.get().addCarver(GenerationStage.Carving.LIQUID, Biome.createCarver(TFCWorldCarvers.UNDERWATER_CAVE.get(), new ProbabilityConfig(0.06666667f)));

        biomeIn.getAll().forEach(biome -> biome.get().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.LARGE_CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.015f)))));
        biomeIn.getAll().forEach(biome -> biome.get().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.08f)))));
    }

    public static SurfaceBuilderConfig getUnderwaterSurfaceConfig(Supplier<TFCBiome> biome)
    {
        if (biome.get().getTemperature() == BiomeTemperature.LUKEWARM || biome.get().getTemperature() == BiomeTemperature.WARM)
        {
            return TFCSurfaceBuilders.SANDSTONE_CONFIG; // replace with underwater sand
        }
        else
        {
            return TFCSurfaceBuilders.RED_SANDSTONE_CONFIG; // replace with underwater gravel
        }
    }
}
