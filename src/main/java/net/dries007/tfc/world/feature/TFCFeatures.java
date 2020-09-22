/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.world.feature.trees.*;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MOD_ID);

    public static final RegistryObject<CaveSpikesFeature> CAVE_SPIKES = FEATURES.register("cave_spikes", () -> new CaveSpikesFeature(NoFeatureConfig.CODEC));
    public static final RegistryObject<LargeCaveSpikesFeature> LARGE_CAVE_SPIKES = FEATURES.register("large_cave_spikes", () -> new LargeCaveSpikesFeature(NoFeatureConfig.CODEC));

    public static final RegistryObject<VeinsFeature> VEINS = FEATURES.register("veins", () -> new VeinsFeature(NoFeatureConfig.CODEC));

    public static final RegistryObject<BouldersFeature> BOULDERS = FEATURES.register("boulders", () -> new BouldersFeature(BoulderConfig.CODEC));
    public static final RegistryObject<FissureFeature> FISSURES = FEATURES.register("fissures", () -> new FissureFeature(BlockStateFeatureConfig.CODEC));

    public static final RegistryObject<ErosionFeature> EROSION = FEATURES.register("erosion", () -> new ErosionFeature(NoFeatureConfig.CODEC));

    public static final RegistryObject<ForestFeature> FORESTS = FEATURES.register("forest", () -> new ForestFeature(ForestFeatureConfig.CODEC));

    public static final RegistryObject<OverlayTreeFeature> OVERLAY_TREE = FEATURES.register("overlay_tree", () -> new OverlayTreeFeature(OverlayTreeConfig.CODEC));
    public static final RegistryObject<RandomTreeFeature> RANDOM_TREE = FEATURES.register("random_tree", () -> new RandomTreeFeature(RandomTreeConfig.CODEC));
    public static final RegistryObject<DoubleRandomTreeFeature> DOUBLE_RANDOM_TREE = FEATURES.register("double_random_tree", () -> new DoubleRandomTreeFeature(RandomTreeConfig.CODEC));
}