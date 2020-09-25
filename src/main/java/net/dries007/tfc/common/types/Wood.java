/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.types;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.TFCLeavesBlock;
import net.dries007.tfc.common.blocks.wood.TFCSaplingBlock;
import net.dries007.tfc.common.blocks.wood.ToolRackBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.feature.TFCFeatures;
import net.dries007.tfc.world.feature.trees.OverlayTreeConfig;
import net.dries007.tfc.world.feature.trees.RandomTreeConfig;
import net.dries007.tfc.world.feature.trees.TFCTree;

public class Wood
{
    private static final Random rng = new Random();
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
        // todo: actual values for the three material colors, and fall foliage coords by wood type
        ACACIA(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        ASH(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        ASPEN(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        BIRCH(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        BLACKWOOD(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        CHESTNUT(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        DOUGLAS_FIR(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        HICKORY(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        KAPOK(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        MAPLE(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        OAK(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        PALM(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        PINE(true, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        ROSEWOOD(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        SEQUOIA(true, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        SPRUCE(true, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        SYCAMORE(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        WHITE_CEDAR(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0),
        WILLOW(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0);

        private final boolean conifer;
        private final MaterialColor mainColor;
        private final MaterialColor topColor;
        private final MaterialColor barkColor;
        private final TFCTree tree;
        private final int fallFoliageCoords;

        Default(boolean conifer, MaterialColor mainColor, MaterialColor topColor, MaterialColor barkColor, int fallFoliageCoords)
        {
            this.conifer = conifer;
            this.mainColor = mainColor;
            this.topColor = topColor;
            this.barkColor = barkColor;
            this.tree = new TFCTree(Helpers.identifier("tree/" + name().toLowerCase()), Helpers.identifier("tree/" + name().toLowerCase() + "_large"));
            this.fallFoliageCoords = rng.nextInt(256 * 256);
        }

        public boolean isConifer()
        {
            return conifer;
        }

        public TFCTree getTree()
        {
            return tree;
        }

        public int getFallFoliageCoords()
        {
            return fallFoliageCoords;
        }

        public MaterialColor getMainColor()
        {
            return mainColor;
        }

        public MaterialColor getTopColor()
        {
            return topColor;
        }

        public MaterialColor getBarkColor()
        {
            return barkColor;
        }
    }

    public enum BlockType
    {
        // These two constructors were lifted from Blocks#log
        LOG(wood -> new RotatedPillarBlock(AbstractBlock.Properties.of(Material.WOOD, stateIn -> stateIn.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.getTopColor() : wood.getBarkColor()).strength(2.0F).sound(SoundType.WOOD)), false),
        STRIPPED_LOG(wood -> new RotatedPillarBlock(AbstractBlock.Properties.of(Material.WOOD, stateIn -> stateIn.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.getTopColor() : wood.getBarkColor()).strength(2.0F).sound(SoundType.WOOD)), false),
        WOOD(wood -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F).sound(SoundType.WOOD)), false),
        STRIPPED_WOOD(wood -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F).sound(SoundType.WOOD)), false),
        LEAVES(wood -> TFCLeavesBlock.create(Block.Properties.of(Material.LEAVES, wood.getMainColor()).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion(), 6), false),
        PLANKS(wood -> new Block(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), false),
        SAPLING(wood -> new TFCSaplingBlock(wood.getTree(), Block.Properties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS)), false),
        BOOKSHELF(wood -> new Block(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        DOOR(wood -> new DoorBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()) {}, true),
        TRAPDOOR(wood -> new TrapDoorBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()) {}, true),
        FENCE(wood -> new FenceBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        LOG_FENCE(wood -> new FenceBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        FENCE_GATE(wood -> new FenceGateBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        BUTTON(wood -> new WoodButtonBlock(Block.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)) {}, true),
        PRESSURE_PLATE(wood -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.of(Material.WOOD, wood.getMainColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)) {}, true),
        SLAB(wood -> new SlabBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        STAIRS(wood -> new StairsBlock(() -> TFCBlocks.WOODS.get(wood).get(PLANKS).get().defaultBlockState(), Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        TOOL_RACK(wood -> new ToolRackBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F).sound(SoundType.WOOD).noOcclusion()) {}, true);

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