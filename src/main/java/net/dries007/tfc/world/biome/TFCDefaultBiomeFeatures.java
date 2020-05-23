package net.dries007.tfc.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import net.dries007.tfc.world.carver.TFCWorldCarvers;
import net.dries007.tfc.world.feature.TFCFeatures;
import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

/**
 * @see net.minecraft.world.biome.DefaultBiomeFeatures
 */
public class TFCDefaultBiomeFeatures
{
    public static void addCarvers(Biome biome)
    {
        biome.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCWorldCarvers.CAVE.get(), new ProbabilityConfig(0.10f)));
        biome.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCWorldCarvers.CANYON.get(), new ProbabilityConfig(0.015f)));

        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.LARGE_CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.02f))));
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.09f))));
    }

    public static void addOceanCarvers(Biome biome)
    {
        biome.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCWorldCarvers.CAVE.get(), new ProbabilityConfig(0.06666667f)));
        biome.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCWorldCarvers.CANYON.get(), new ProbabilityConfig(0.02f)));

        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.LARGE_CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.015f))));
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.08f))));
    }

    public static SurfaceBuilder<SurfaceBuilderConfig> getOceanSurfaceBuilder(TFCBiome biome)
    {
        if (biome.getTemperature() == BiomeTemperature.FROZEN)
        {
            return SurfaceBuilder.FROZEN_OCEAN;
        }
        else
        {
            return SurfaceBuilder.DEFAULT;
        }
    }

    public static SurfaceBuilderConfig getUnderwaterSurfaceConfig(TFCBiome biome)
    {
        if (biome.getTemperature() == BiomeTemperature.LUKEWARM || biome.getTemperature() == BiomeTemperature.WARM)
        {
            return TFCSurfaceBuilders.SANDSTONE_CONFIG; // replace with underwater sand
        }
        else
        {
            return TFCSurfaceBuilders.RED_SANDSTONE_CONFIG; // replace with underwater gravel
        }
    }
}
