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
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.world.carver.TFCWorldCarvers;
import net.dries007.tfc.world.feature.TFCFeatures;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCBiomes
{
    public static final DeferredRegister<Biome> BIOMES = new DeferredRegister<>(ForgeRegistries.BIOMES, MOD_ID);

    // Aquatic biomes
    public static final RegistryObject<TFCBiome> OCEAN = BIOMES.register("ocean", () -> new OceanBiome(false)); // Ocean biome found near continents.
    public static final RegistryObject<TFCBiome> DEEP_OCEAN = BIOMES.register("deep_ocean", () -> new OceanBiome(true)); // Deep ocean biome covering most all oceans.
    public static final RegistryObject<TFCBiome> DEEP_OCEAN_RIDGE = BIOMES.register("deep_ocean_ridge", () -> new OceanBiome(true)); // Variant of deep ocean biomes, contains snaking ridge like formations.

    // Low biomes
    public static final RegistryObject<TFCBiome> PLAINS = BIOMES.register("plains", () -> new PlainsBiome(-4, 10)); // Very flat, slightly above sea level.
    public static final RegistryObject<TFCBiome> HILLS = BIOMES.register("hills", () -> new HillsBiome(16)); // Small hills, slightly above sea level.
    public static final RegistryObject<TFCBiome> LOWLANDS = BIOMES.register("lowlands", LowlandsBiome::new); // Flat, swamp-like, lots of shallow pools below sea level.
    public static final RegistryObject<TFCBiome> LOW_CANYONS = BIOMES.register("low_canyons", () -> new CanyonsBiome(-5, 14)); // Sharp, small hills, with lots of water / snaking winding rivers.

    // Mid biomes
    public static final RegistryObject<TFCBiome> ROLLING_HILLS = BIOMES.register("rolling_hills", () -> new HillsBiome(28)); // Higher hills, above sea level. Some larger / steeper hills.
    public static final RegistryObject<TFCBiome> BADLANDS = BIOMES.register("badlands", BadlandsBiome::new); // Very high flat area with steep relief carving, similar to vanilla mesas.
    public static final RegistryObject<TFCBiome> PLATEAU = BIOMES.register("plateau", () -> new PlainsBiome(20, 30)); // Very high area, very flat top.
    public static final RegistryObject<TFCBiome> OLD_MOUNTAINS = BIOMES.register("old_mountains", () -> new MountainsBiome(48, 28, false)); // Rounded top mountains, very large hills.

    // High biomes
    public static final RegistryObject<TFCBiome> MOUNTAINS = BIOMES.register("mountains", () -> new MountainsBiome(48, 56, false)); // High, picturesque mountains. Pointed peaks, low valleys well above sea level.
    public static final RegistryObject<TFCBiome> FLOODED_MOUNTAINS = BIOMES.register("flooded_mountains", () -> new MountainsBiome(30, 64, true)); // Mountains with high areas, and low, below sea level valleys. Water is salt water here.
    public static final RegistryObject<TFCBiome> CANYONS = BIOMES.register("canyons", () -> new CanyonsBiome(-7, 26)); // Medium height with snake like ridges, often slightly below sea level

    // Shores
    public static final RegistryObject<TFCBiome> SHORE = BIOMES.register("shore", () -> new ShoreBiome(false)); // Standard shore biome with a sandy beach
    public static final RegistryObject<TFCBiome> STONE_SHORE = BIOMES.register("stone_shore", () -> new ShoreBiome(true)); // Shore for mountain biomes

    // Technical biomes
    public static final RegistryObject<TFCBiome> MOUNTAINS_EDGE = BIOMES.register("mountains_edge", () -> new MountainsBiome(36, 34, false)); // Edge biome for mountains
    public static final RegistryObject<TFCBiome> LAKE = BIOMES.register("lake", LakeBiome::new); // Biome for freshwater ocean areas / landlocked oceans
    public static final RegistryObject<TFCBiome> RIVER = BIOMES.register("river", RiverBiome::new); // Biome for river channels

    private static final List<RegistryObject<TFCBiome>> ALL_BIOMES = Arrays.asList(OCEAN, DEEP_OCEAN, DEEP_OCEAN_RIDGE, PLAINS, HILLS, LOWLANDS, LOW_CANYONS, ROLLING_HILLS, BADLANDS, PLATEAU, OLD_MOUNTAINS, MOUNTAINS, FLOODED_MOUNTAINS, CANYONS, SHORE, STONE_SHORE, MOUNTAINS_EDGE, LAKE, RIVER);

    public static Set<TFCBiome> getBiomes()
    {
        return ALL_BIOMES.stream().map(RegistryObject::get).collect(Collectors.toSet());
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
        OCEAN.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRAVEL_CONFIG);

        addOceanCarvers(DEEP_OCEAN);
        DEEP_OCEAN.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRAVEL_CONFIG);

        addOceanCarvers(DEEP_OCEAN_RIDGE);
        DEEP_OCEAN_RIDGE.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRAVEL_CONFIG);

        addCarvers(PLAINS);
        PLAINS.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG);

        addCarvers(HILLS);
        HILLS.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG);

        addCarvers(LOWLANDS);
        LOWLANDS.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG);

        addCarvers(LOW_CANYONS);
        LOW_CANYONS.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG);

        addCarvers(ROLLING_HILLS);
        ROLLING_HILLS.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG);

        addCarvers(BADLANDS);
        BADLANDS.get().setSurfaceBuilder(SurfaceBuilder.WOODED_BADLANDS, SurfaceBuilder.RED_SAND_WHITE_TERRACOTTA_GRAVEL_CONFIG);

        addCarvers(PLATEAU);
        PLATEAU.get().setSurfaceBuilder(SurfaceBuilder.MOUNTAIN, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG);

        addCarvers(OLD_MOUNTAINS);
        OLD_MOUNTAINS.get().setSurfaceBuilder(SurfaceBuilder.GRAVELLY_MOUNTAIN, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG);

        addCarvers(MOUNTAINS);
        MOUNTAINS.get().setSurfaceBuilder(SurfaceBuilder.MOUNTAIN, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG);

        addOceanCarvers(FLOODED_MOUNTAINS);
        FLOODED_MOUNTAINS.get().setSurfaceBuilder(SurfaceBuilder.GRAVELLY_MOUNTAIN, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG);

        addCarvers(CANYONS);
        CANYONS.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG);

        SHORE.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.SAND_SAND_GRAVEL_CONFIG);

        STONE_SHORE.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.SAND_SAND_GRAVEL_CONFIG);

        addCarvers(MOUNTAINS_EDGE);
        MOUNTAINS_EDGE.get().setSurfaceBuilder(SurfaceBuilder.MOUNTAIN, SurfaceBuilder.GRASS_DIRT_SAND_CONFIG);

        addCarvers(LAKE);
        LAKE.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.SAND_CONFIG);

        addCarvers(RIVER);
        RIVER.get().setSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.SAND_CONFIG);

        // Features applied to ALL biomes
        for (RegistryObject<? extends Biome> biome : ALL_BIOMES)
        {
            biome.get().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, TFCFeatures.VEINS.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(NoPlacementConfig.NO_PLACEMENT_CONFIG)));
        }
    }

    public static void addCarvers(RegistryObject<? extends Biome> biomeIn)
    {
        biomeIn.get().addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCWorldCarvers.CAVE.get(), new ProbabilityConfig(0.10f)));
        biomeIn.get().addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCWorldCarvers.CANYON.get(), new ProbabilityConfig(0.015f)));

        biomeIn.get().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.LARGE_CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.02f))));
        biomeIn.get().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.09f))));
    }

    public static void addOceanCarvers(RegistryObject<? extends Biome> biomeIn)
    {
        biomeIn.get().addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCWorldCarvers.CAVE.get(), new ProbabilityConfig(0.06666667f)));
        biomeIn.get().addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCWorldCarvers.CANYON.get(), new ProbabilityConfig(0.02f)));

        // todo: what with all the caves, these get really ugly when they intersect other cave systems. Is there any way these can be improved?
        //biomeIn.get().addCarver(GenerationStage.Carving.LIQUID, Biome.createCarver(TFCWorldCarvers.UNDERWATER_CANYON.get(), new ProbabilityConfig(0.02f)));
        //biomeIn.get().addCarver(GenerationStage.Carving.LIQUID, Biome.createCarver(TFCWorldCarvers.UNDERWATER_CAVE.get(), new ProbabilityConfig(0.06666667f)));

        biomeIn.get().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.LARGE_CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.015f))));
        biomeIn.get().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.08f))));
    }
}
