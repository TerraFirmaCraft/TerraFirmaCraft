/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.Placement;

import net.dries007.tfc.world.carver.TFCCarvers;
import net.dries007.tfc.world.feature.TFCFeatures;

/**
 * @see net.minecraft.world.biome.DefaultBiomeFeatures
 */
public class TFCDefaultBiomeFeatures
{
    public static void addCarvers(Biome biome)
    {
        biome.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCCarvers.CAVE.get(), new ProbabilityConfig(0.10f)));
        biome.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCCarvers.CANYON.get(), new ProbabilityConfig(0.015f)));

        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.LARGE_CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.02f))));
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.09f))));
    }

    public static void addOceanCarvers(Biome biome)
    {
        biome.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCCarvers.CAVE.get(), new ProbabilityConfig(0.06666667f)));
        biome.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TFCCarvers.CANYON.get(), new ProbabilityConfig(0.03f)));

        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.LARGE_CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.015f))));
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, TFCFeatures.CAVE_SPIKES.get().withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.08f))));
    }
}
