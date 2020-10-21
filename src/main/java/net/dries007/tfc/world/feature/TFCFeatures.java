/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.LiquidsConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.world.feature.trees.*;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class TFCFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MOD_ID);


    public static final RegistryObject<CaveSpikesFeature> CAVE_SPIKE = FEATURES.register("cave_spike", () -> new CaveSpikesFeature(NoFeatureConfig.CODEC));
    public static final RegistryObject<LargeCaveSpikesFeature> LARGE_CAVE_SPIKE = FEATURES.register("large_cave_spike", () -> new LargeCaveSpikesFeature(NoFeatureConfig.CODEC));

    public static final RegistryObject<VeinsFeature> ORE_VEINS = FEATURES.register("ore_veins", () -> new VeinsFeature(NoFeatureConfig.CODEC));

    public static final RegistryObject<BouldersFeature> BOULDER = FEATURES.register("boulder", () -> new BouldersFeature(BoulderConfig.CODEC));
    public static final RegistryObject<FissureFeature> FISSURE = FEATURES.register("fissure", () -> new FissureFeature(BlockStateFeatureConfig.CODEC));

    public static final RegistryObject<ForestFeature> FOREST = FEATURES.register("forest", () -> new ForestFeature(ForestConfig.CODEC));
    public static final RegistryObject<OverlayTreeFeature> OVERLAY_TREE = FEATURES.register("overlay_tree", () -> new OverlayTreeFeature(OverlayTreeConfig.CODEC));
    public static final RegistryObject<RandomTreeFeature> RANDOM_TREE = FEATURES.register("random_tree", () -> new RandomTreeFeature(RandomTreeConfig.CODEC));
    public static final RegistryObject<StackedTreeFeature> STACKED_TREE = FEATURES.register("stacked_tree", () -> new StackedTreeFeature(StackedTreeConfig.CODEC));

    public static final RegistryObject<ErosionFeature> EROSION = FEATURES.register("erosion", () -> new ErosionFeature(NoFeatureConfig.CODEC));
    public static final RegistryObject<IceAndSnowFeature> ICE_AND_SNOW = FEATURES.register("ice_and_snow", () -> new IceAndSnowFeature(NoFeatureConfig.CODEC));

    public static final RegistryObject<LakeFeature> LAKE = FEATURES.register("lake", () -> new LakeFeature(NoFeatureConfig.CODEC));
    public static final RegistryObject<FloodFillLakeFeature> FLOOD_FILL_LAKE = FEATURES.register("flood_fill_lake", () -> new FloodFillLakeFeature(NoFeatureConfig.CODEC));
    public static final RegistryObject<SpringFeature> SPRING = FEATURES.register("spring", () -> new SpringFeature(LiquidsConfig.CODEC));
}