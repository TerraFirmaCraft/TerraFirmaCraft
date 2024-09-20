/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.function.Function;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.util.registry.RegistryHolder;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.feature.cave.CaveColumnFeature;
import net.dries007.tfc.world.feature.cave.CaveSpikesFeature;
import net.dries007.tfc.world.feature.cave.CaveVegetationConfig;
import net.dries007.tfc.world.feature.cave.CaveVegetationFeature;
import net.dries007.tfc.world.feature.cave.IceCaveFeature;
import net.dries007.tfc.world.feature.cave.LargeCaveSpikesFeature;
import net.dries007.tfc.world.feature.cave.ThinSpikeConfig;
import net.dries007.tfc.world.feature.cave.ThinSpikeFeature;
import net.dries007.tfc.world.feature.coral.TFCCoralClawFeature;
import net.dries007.tfc.world.feature.coral.TFCCoralMushroomFeature;
import net.dries007.tfc.world.feature.coral.TFCCoralTreeFeature;
import net.dries007.tfc.world.feature.plant.BananaFeature;
import net.dries007.tfc.world.feature.plant.BlockWithFluidFeature;
import net.dries007.tfc.world.feature.plant.BranchingCactusFeature;
import net.dries007.tfc.world.feature.plant.ColumnPlantConfig;
import net.dries007.tfc.world.feature.plant.CreepingPlantConfig;
import net.dries007.tfc.world.feature.plant.CreepingPlantFeature;
import net.dries007.tfc.world.feature.plant.EmergentPlantFeature;
import net.dries007.tfc.world.feature.plant.EpiphytePlantFeature;
import net.dries007.tfc.world.feature.plant.FruitTreeFeature;
import net.dries007.tfc.world.feature.plant.GiantKelpFeature;
import net.dries007.tfc.world.feature.plant.SpreadingBushFeature;
import net.dries007.tfc.world.feature.plant.SpreadingCropFeature;
import net.dries007.tfc.world.feature.plant.SubmergedTallPlantFeature;
import net.dries007.tfc.world.feature.plant.TFCBambooConfig;
import net.dries007.tfc.world.feature.plant.TFCBambooFeature;
import net.dries007.tfc.world.feature.plant.TFCKelpFeature;
import net.dries007.tfc.world.feature.plant.TFCTwistingVinesFeature;
import net.dries007.tfc.world.feature.plant.TFCVinesFeature;
import net.dries007.tfc.world.feature.plant.TFCWeepingVinesFeature;
import net.dries007.tfc.world.feature.plant.TallPlantFeature;
import net.dries007.tfc.world.feature.plant.TallWildCropFeature;
import net.dries007.tfc.world.feature.tree.ForestConfig;
import net.dries007.tfc.world.feature.tree.ForestFeature;
import net.dries007.tfc.world.feature.tree.KrummholzConfig;
import net.dries007.tfc.world.feature.tree.KrummholzFeature;
import net.dries007.tfc.world.feature.tree.OverlayTreeConfig;
import net.dries007.tfc.world.feature.tree.OverlayTreeFeature;
import net.dries007.tfc.world.feature.tree.RandomTreeConfig;
import net.dries007.tfc.world.feature.tree.RandomTreeFeature;
import net.dries007.tfc.world.feature.tree.StackedTreeConfig;
import net.dries007.tfc.world.feature.tree.StackedTreeFeature;
import net.dries007.tfc.world.feature.vein.ClusterVeinConfig;
import net.dries007.tfc.world.feature.vein.ClusterVeinFeature;
import net.dries007.tfc.world.feature.vein.DiscVeinConfig;
import net.dries007.tfc.world.feature.vein.DiscVeinFeature;
import net.dries007.tfc.world.feature.vein.KaolinDiscVeinFeature;
import net.dries007.tfc.world.feature.vein.PipeVeinConfig;
import net.dries007.tfc.world.feature.vein.PipeVeinFeature;

import static net.dries007.tfc.TerraFirmaCraft.*;

@SuppressWarnings("unused")
public class TFCFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MOD_ID);

    public static final Id<CaveSpikesFeature> CAVE_SPIKE = register("cave_spike", CaveSpikesFeature::new, NoneFeatureConfiguration.CODEC);
    public static final Id<LargeCaveSpikesFeature> LARGE_CAVE_SPIKE = register("large_cave_spike", LargeCaveSpikesFeature::new, NoneFeatureConfiguration.CODEC);
    public static final Id<ThinSpikeFeature> THIN_SPIKE = register("thin_spike", ThinSpikeFeature::new, ThinSpikeConfig.CODEC);
    public static final Id<RivuletFeature> RIVULET = register("rivulet", RivuletFeature::new, Codecs.BLOCK_STATE_CONFIG);
    public static final Id<FissureFeature> FISSURE = register("fissure", FissureFeature::new, FissureConfig.CODEC);
    public static final Id<HotSpringFeature> HOT_SPRING = register("hot_spring", HotSpringFeature::new, HotSpringConfig.CODEC);
    public static final Id<TFCGeodeFeature> GEODE = register("geode", TFCGeodeFeature::new, TFCGeodeConfig.CODEC);
    public static final Id<CaveColumnFeature> CAVE_COLUMN = register("cave_column", CaveColumnFeature::new, NoneFeatureConfiguration.CODEC);

    public static final Id<ClusterVeinFeature> CLUSTER_VEIN = register("cluster_vein", ClusterVeinFeature::new, ClusterVeinConfig.CODEC);
    public static final Id<DiscVeinFeature> DISC_VEIN = register("disc_vein", DiscVeinFeature::new, DiscVeinConfig.CODEC);
    public static final Id<KaolinDiscVeinFeature> KAOLIN_DISC_VEIN = register("kaolin_disc_vein", KaolinDiscVeinFeature::new, DiscVeinConfig.CODEC);
    public static final Id<PipeVeinFeature> PIPE_VEIN = register("pipe_vein", PipeVeinFeature::new, PipeVeinConfig.CODEC);

    public static final Id<BouldersFeature> BOULDER = register("boulder", BouldersFeature::new, BoulderConfig.CODEC);
    public static final Id<BabyBoulderFeature> BABY_BOULDER = register("baby_boulder", BabyBoulderFeature::new, BoulderConfig.CODEC);
    public static final Id<LooseRockFeature> LOOSE_ROCK = register("loose_rock", LooseRockFeature::new, NoneFeatureConfiguration.CODEC);

    public static final Id<TFCWeepingVinesFeature> HANGING_VINES = register("weeping_vines", TFCWeepingVinesFeature::new, ColumnPlantConfig.CODEC);
    public static final Id<TFCTwistingVinesFeature> TWISTING_VINES = register("twisting_vines", TFCTwistingVinesFeature::new, ColumnPlantConfig.CODEC);
    public static final Id<CreepingPlantFeature> CREEPING_PLANT = register("creeping_plant", CreepingPlantFeature::new, CreepingPlantConfig.CODEC);
    public static final Id<EpiphytePlantFeature> EPIPHYTE_PLANT = register("epiphyte_plant", EpiphytePlantFeature::new, EpiphytePlantFeature.CODEC);
    public static final Id<SubmergedTallPlantFeature> TALL_AQUATIC_PLANT = register("submerged_tall_plant", SubmergedTallPlantFeature::new, SubmergedTallPlantFeature.CODEC);
    public static final Id<TFCKelpFeature> KELP = register("kelp", TFCKelpFeature::new, ColumnPlantConfig.CODEC);
    public static final Id<GiantKelpFeature> KELP_TREE = register("kelp_tree", GiantKelpFeature::new, GiantKelpFeature.CODEC);
    public static final Id<EmergentPlantFeature> EMERGENT_PLANT = register("emergent_plant", EmergentPlantFeature::new, EmergentPlantFeature.CODEC);
    public static final Id<TallPlantFeature> TALL_PLANT = register("tall_plant", TallPlantFeature::new, TallPlantFeature.CODEC);
    public static final Id<TallWildCropFeature> TALL_WILD_CROP = register("tall_wild_crop", TallWildCropFeature::new, TallWildCropFeature.CODEC);
    public static final Id<SpreadingCropFeature> SPREADING_CROP = register("spreading_crop", SpreadingCropFeature::new, SpreadingCropFeature.CODEC);
    public static final Id<SpreadingBushFeature> SPREADING_BUSH = register("spreading_bush", SpreadingBushFeature::new, SpreadingBushFeature.CODEC);
    public static final Id<BlockWithFluidFeature> BLOCK_WITH_FLUID = register("block_with_fluid", BlockWithFluidFeature::new, SimpleBlockConfiguration.CODEC);
    public static final Id<BranchingCactusFeature> BRANCHING_CACTUS = register("branching_cactus", BranchingCactusFeature::new, BranchingCactusFeature.CODEC);

    public static final Id<TFCCoralClawFeature> CORAL_CLAW = register("coral_claw", TFCCoralClawFeature::new, NoneFeatureConfiguration.CODEC);
    public static final Id<TFCCoralMushroomFeature> CORAL_MUSHROOM = register("coral_mushroom", TFCCoralMushroomFeature::new, NoneFeatureConfiguration.CODEC);
    public static final Id<TFCCoralTreeFeature> CORAL_TREE = register("coral_tree", TFCCoralTreeFeature::new, NoneFeatureConfiguration.CODEC);

    public static final Id<CaveVegetationFeature> CAVE_VEGETATION = register("cave_vegetation", CaveVegetationFeature::new, CaveVegetationConfig.CODEC);
    public static final Id<TFCVinesFeature> VINES = register("vines", TFCVinesFeature::new, BlockStateConfiguration.CODEC);
    public static final Id<IceCaveFeature> ICE_CAVE = register("ice_cave", IceCaveFeature::new, NoneFeatureConfiguration.CODEC);
    public static final Id<TidePoolFeature> TIDE_POOL = register("tide_pool", TidePoolFeature::new, NoneFeatureConfiguration.CODEC);
    public static final Id<FruitTreeFeature> FRUIT_TREES = register("fruit_trees", FruitTreeFeature::new, Codecs.BLOCK_STATE_CONFIG);
    public static final Id<BananaFeature> BANANAS = register("bananas", BananaFeature::new, Codecs.BLOCK_STATE_CONFIG);
    public static final Id<TFCBambooFeature> BAMBOO = register("bamboo", TFCBambooFeature::new, TFCBambooConfig.CODEC);

    public static final Id<ForestFeature> FOREST = register("forest", ForestFeature::new, ForestConfig.CODEC);
    public static final Id<ForestFeature.Entry> FOREST_ENTRY = register("forest_entry", ForestFeature.Entry::new, ForestConfig.Entry.CODEC);
    public static final Id<OverlayTreeFeature> OVERLAY_TREE = register("overlay_tree", OverlayTreeFeature::new, OverlayTreeConfig.CODEC);
    public static final Id<RandomTreeFeature> RANDOM_TREE = register("random_tree", RandomTreeFeature::new, RandomTreeConfig.CODEC);
    public static final Id<StackedTreeFeature> STACKED_TREE = register("stacked_tree", StackedTreeFeature::new, StackedTreeConfig.CODEC);
    public static final Id<KrummholzFeature> KRUMMHOLZ = register("krummholz", KrummholzFeature::new, KrummholzConfig.CODEC);

    public static final Id<ErosionFeature> EROSION = register("erosion", ErosionFeature::new, NoneFeatureConfiguration.CODEC);
    public static final Id<IceAndSnowFeature> ICE_AND_SNOW = register("ice_and_snow", IceAndSnowFeature::new, NoneFeatureConfiguration.CODEC);

    public static final Id<FloodFillLakeFeature> FLOOD_FILL_LAKE = register("flood_fill_lake", FloodFillLakeFeature::new, FloodFillLakeConfig.CODEC);
    public static final Id<SpringFeature> SPRING = register("spring", SpringFeature::new, SpringConfiguration.CODEC);

    public static final Id<SoilDiscFeature> SOIL_DISC = register("soil_disc", SoilDiscFeature::new, SoilDiscConfig.CODEC);
    public static final Id<TFCIcebergFeature> ICEBERG = register("iceberg", TFCIcebergFeature::new, BlockStateConfiguration.CODEC);
    public static final Id<PowderSnowFeature> POWDER_SNOW = register("powder_snow", PowderSnowFeature::new, BlockStateConfiguration.CODEC);

    public static final Id<IfThenFeature> IF_THEN = register("if_then", IfThenFeature::new, IfThenConfig.CODEC);
    public static final Id<MultipleFeature> MULTIPLE = register("multiple", MultipleFeature::new, SimpleRandomFeatureConfiguration.CODEC);
    public static final Id<NoisyMultipleFeature> NOISY_MULTIPLE = register("noisy_multiple", NoisyMultipleFeature::new, SimpleRandomFeatureConfiguration.CODEC);
    public static final Id<DynamicDensityRandomPatchFeature> DYNAMIC_RANDOM_PATCH = register("dynamic_random_patch", DynamicDensityRandomPatchFeature::new, RandomPatchConfiguration.CODEC);

    private static <C extends FeatureConfiguration, F extends Feature<C>> Id<F> register(String name, Function<Codec<C>, F> factory, Codec<C> codec)
    {
        return new Id<>(FEATURES.register(name, () -> factory.apply(codec)));
    }
    
    public record Id<T extends Feature<?>>(DeferredHolder<Feature<?>, T> holder)
        implements RegistryHolder<Feature<?>, T> {}
}