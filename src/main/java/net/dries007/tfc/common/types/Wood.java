/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.types;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.TFCSaplingBlock;
import net.dries007.tfc.common.blocks.wood.ToolRackBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.feature.TFCFeatures;
import net.dries007.tfc.world.feature.trees.DefaultTree;
import net.dries007.tfc.world.feature.trees.NormalTreeConfig;
import net.dries007.tfc.world.feature.trees.RandomlyChosenTreeConfig;

public class Wood
{
    /**
     * Default wood types used for block registration calls
     * Not extensible
     *
     * todo: re-evaluate if there is any data driven behavior that needs a json driven ore
     *
     * @see Wood instead and register via json
     */
    public enum Default
    {
        ACACIA(false, () -> TFCFeatures.RANDOM_TREE.get().withConfiguration(RandomlyChosenTreeConfig.forVariants("acacia", 35))),
        ASH(false, wood -> TFCFeatures.NORMAL_TREE.get().withConfiguration(new NormalTreeConfig(Helpers.identifier("ash/base"), Helpers.identifier("ash/overlay"), 1, 3, TFCBlocks.WOODS.get(wood).get(BlockType.LOG).get().getDefaultState()))),
        ASPEN(false, wood -> TFCFeatures.NORMAL_TREE.get().withConfiguration(new NormalTreeConfig(Helpers.identifier("aspen/base"), Helpers.identifier("aspen/overlay"), 1, 3, TFCBlocks.WOODS.get(wood).get(BlockType.LOG).get().getDefaultState()))),
        BIRCH(false, wood -> TFCFeatures.NORMAL_TREE.get().withConfiguration(new NormalTreeConfig(Helpers.identifier("birch/base"), Helpers.identifier("birch/overlay"), 1, 3, TFCBlocks.WOODS.get(wood).get(BlockType.LOG).get().getDefaultState()))),
        BLACKWOOD(false, wood -> TFCFeatures.NORMAL_TREE.get().withConfiguration(new NormalTreeConfig(Helpers.identifier("blackwood/base"), Helpers.identifier("blackwood/overlay"), 1, 3, TFCBlocks.WOODS.get(wood).get(BlockType.LOG).get().getDefaultState()))),
        CHESTNUT(false, wood -> TFCFeatures.NORMAL_TREE.get().withConfiguration(new NormalTreeConfig(Helpers.identifier("chestnut/base"), Helpers.identifier("chestnut/overlay"), 1, 3, TFCBlocks.WOODS.get(wood).get(BlockType.LOG).get().getDefaultState()))),
        DOUGLAS_FIR(false, wood -> TFCFeatures.NORMAL_TREE.get().withConfiguration(new NormalTreeConfig(Helpers.identifier("douglas_fir/base"), Helpers.identifier("douglas_fir/overlay"), 1, 3, TFCBlocks.WOODS.get(wood).get(BlockType.LOG).get().getDefaultState()))),
        HICKORY(false, wood -> TFCFeatures.NORMAL_TREE.get().withConfiguration(new NormalTreeConfig(Helpers.identifier("hickory/base"), Helpers.identifier("hickory/overlay"), 1, 3, TFCBlocks.WOODS.get(wood).get(BlockType.LOG).get().getDefaultState()))),
        KAPOK(false, () -> TFCFeatures.RANDOM_TREE.get().withConfiguration(RandomlyChosenTreeConfig.forVariants("kapok", 7))),
        MAPLE(false, wood -> TFCFeatures.NORMAL_TREE.get().withConfiguration(new NormalTreeConfig(Helpers.identifier("maple/base"), Helpers.identifier("maple/overlay"), 1, 3, TFCBlocks.WOODS.get(wood).get(BlockType.LOG).get().getDefaultState()))),
        OAK(false, wood -> TFCFeatures.NORMAL_TREE.get().withConfiguration(new NormalTreeConfig(Helpers.identifier("oak/base"), Helpers.identifier("oak/overlay"), 1, 3, TFCBlocks.WOODS.get(wood).get(BlockType.LOG).get().getDefaultState()))),
        PALM(false, () -> TFCFeatures.RANDOM_TREE.get().withConfiguration(RandomlyChosenTreeConfig.forVariants("palm", 7))),
        PINE(true, () -> TFCFeatures.RANDOM_TREE.get().withConfiguration(RandomlyChosenTreeConfig.forVariants("pine", 7))),
        ROSEWOOD(false, wood -> TFCFeatures.NORMAL_TREE.get().withConfiguration(new NormalTreeConfig(Helpers.identifier("rosewood/base"), Helpers.identifier("rosewood/overlay"), 1, 3, TFCBlocks.WOODS.get(wood).get(BlockType.LOG).get().getDefaultState()))),
        SEQUOIA(true, () -> TFCFeatures.RANDOM_TREE.get().withConfiguration(RandomlyChosenTreeConfig.forVariants("sequoia", 7))),
        SPRUCE(true, () -> TFCFeatures.RANDOM_TREE.get().withConfiguration(RandomlyChosenTreeConfig.forVariants("spruce", 7))),
        SYCAMORE(false, wood -> TFCFeatures.NORMAL_TREE.get().withConfiguration(new NormalTreeConfig(Helpers.identifier("sycamore/base"), Helpers.identifier("sycamore/overlay"), 1, 3, TFCBlocks.WOODS.get(wood).get(BlockType.LOG).get().getDefaultState()))),
        WHITE_CEDAR(false, wood -> TFCFeatures.NORMAL_TREE.get().withConfiguration(new NormalTreeConfig(Helpers.identifier("white_cedar/base"), Helpers.identifier("white_cedar/overlay"), 1, 3, TFCBlocks.WOODS.get(wood).get(BlockType.LOG).get().getDefaultState()))),
        WILLOW(false, () -> TFCFeatures.RANDOM_TREE.get().withConfiguration(RandomlyChosenTreeConfig.forVariants("willow", 7)));

        private final boolean conifer;
        private final DefaultTree tree;

        Default(boolean conifer, Supplier<ConfiguredFeature<?, ?>> feature)
        {
            this.conifer = conifer;
            this.tree = new DefaultTree(feature);
        }

        Default(boolean conifer, Function<Default, ConfiguredFeature<?, ?>> feature)
        {
            this.conifer = conifer;
            this.tree = new DefaultTree(() -> feature.apply(this)); // Allow the possibility to use a "this" reference in the initializer
        }

        public boolean isConifer()
        {
            return conifer;
        }

        public DefaultTree getTree()
        {
            return tree;
        }

        public MaterialColor getMaterialColor()
        {
            return MaterialColor.ADOBE; // todo: in 1.16 there are two material colors, one for the top, one for bark. We need to figure out which materials match our logs.
        }
    }

    public enum BlockType
    {
        LOG(wood -> new LogBlock(MaterialColor.SAND, Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(0.5F).sound(SoundType.WOOD)), false),
        STRIPPED_LOG(wood -> new LogBlock(MaterialColor.WOOD, Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD)), false),
        WOOD(wood -> new RotatedPillarBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD)), false),
        STRIPPED_WOOD(wood -> new RotatedPillarBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD)), false),
        LEAVES(wood -> new LeavesBlock(Block.Properties.create(Material.LEAVES, wood.getMaterialColor()).hardnessAndResistance(0.5F).sound(SoundType.PLANT).tickRandomly().notSolid()), false),
        PLANKS(wood -> new Block(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), false),
        SAPLING(wood -> new TFCSaplingBlock(wood.getTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0).sound(SoundType.PLANT)), false),
        BOOKSHELF(wood -> new Block(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        DOOR(wood -> new DoorBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()) {}, true),
        TRAPDOOR(wood -> new TrapDoorBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()) {}, true),
        FENCE(wood -> new FenceBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        LOG_FENCE(wood -> new FenceBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        FENCE_GATE(wood -> new FenceGateBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        BUTTON(wood -> new WoodButtonBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)) {}, true),
        PRESSURE_PLATE(wood -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD, wood.getMaterialColor()).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)) {}, true),
        SLAB(wood -> new SlabBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        STAIRS(wood -> new StairsBlock(() -> TFCBlocks.WOODS.get(wood).get(PLANKS).get().getDefaultState(), Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        TOOL_RACK(wood -> new ToolRackBlock(Block.Properties.create(Material.WOOD, wood.getMaterialColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD).notSolid()) {}, true);

        public static final BlockType[] VALUES = values();

        public static BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : LOG;
        }

        private final NonNullFunction<Default, Block> blockFactory;
        private final boolean isPlanksVariant;

        BlockType(NonNullFunction<Default, Block> blockFactory, boolean isPlanksVariant)
        {
            this.blockFactory = blockFactory;
            this.isPlanksVariant = isPlanksVariant;
        }

        public Supplier<Block> create(Default wood)
        {
            return () -> blockFactory.apply(wood);
        }

        public String id(Default wood)
        {
            return (isPlanksVariant ? "wood/planks/" + wood.name() + "_" + name().toLowerCase() : "wood/" + name() + "/" + wood.name()).toLowerCase();
        }
    }
}
