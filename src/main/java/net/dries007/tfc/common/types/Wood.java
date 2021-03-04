/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.FallenLeavesBlock;
import net.dries007.tfc.common.blocks.wood.TFCLeavesBlock;
import net.dries007.tfc.common.blocks.wood.TFCSaplingBlock;
import net.dries007.tfc.common.blocks.wood.ToolRackBlock;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.feature.tree.TFCTree;

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
        ACACIA(false, MaterialColor.TERRACOTTA_ORANGE, MaterialColor.TERRACOTTA_ORANGE, MaterialColor.TERRACOTTA_LIGHT_GRAY, 0, 7, 11),
        ASH(false, MaterialColor.TERRACOTTA_PINK, MaterialColor.TERRACOTTA_PINK, MaterialColor.TERRACOTTA_ORANGE, 0, 8, 7),
        ASPEN(false, MaterialColor.TERRACOTTA_GREEN, MaterialColor.TERRACOTTA_GREEN, MaterialColor.TERRACOTTA_WHITE, 0, 7, 8),
        BIRCH(false, MaterialColor.COLOR_BROWN, MaterialColor.COLOR_BROWN, MaterialColor.TERRACOTTA_WHITE, 0, 7, 7),
        BLACKWOOD(false, MaterialColor.COLOR_BLACK, MaterialColor.COLOR_BLACK, MaterialColor.COLOR_BROWN, 0, 7, 8),
        CHESTNUT(false, MaterialColor.TERRACOTTA_RED, MaterialColor.TERRACOTTA_RED, MaterialColor.COLOR_LIGHT_GREEN, 0, 8, 7),
        DOUGLAS_FIR(false, MaterialColor.TERRACOTTA_YELLOW, MaterialColor.TERRACOTTA_YELLOW, MaterialColor.TERRACOTTA_BROWN, 0, 7, 7),
        HICKORY(false, MaterialColor.TERRACOTTA_BROWN, MaterialColor.TERRACOTTA_BROWN, MaterialColor.COLOR_GRAY, 0, 7, 10),
        KAPOK(true, MaterialColor.COLOR_PINK, MaterialColor.COLOR_PINK, MaterialColor.COLOR_BROWN, 0, 8, 7),
        MAPLE(false, MaterialColor.COLOR_ORANGE, MaterialColor.COLOR_ORANGE, MaterialColor.TERRACOTTA_GRAY, 0, 8, 7),
        OAK(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 0, 8, 10),
        PALM(true, MaterialColor.COLOR_ORANGE, MaterialColor.COLOR_ORANGE, MaterialColor.COLOR_BROWN, 0, 7, 7),
        PINE(true, MaterialColor.TERRACOTTA_GRAY, MaterialColor.TERRACOTTA_GRAY, MaterialColor.COLOR_GRAY, 0, 7, 7),
        ROSEWOOD(false, MaterialColor.COLOR_RED, MaterialColor.COLOR_RED, MaterialColor.TERRACOTTA_LIGHT_GRAY, 0, 9, 8),
        SEQUOIA(true, MaterialColor.TERRACOTTA_RED, MaterialColor.TERRACOTTA_RED, MaterialColor.TERRACOTTA_RED, 0, 7, 18),
        SPRUCE(true, MaterialColor.TERRACOTTA_PINK, MaterialColor.TERRACOTTA_PINK, MaterialColor.TERRACOTTA_BLACK, 0, 7, 7),
        SYCAMORE(false, MaterialColor.COLOR_YELLOW, MaterialColor.COLOR_YELLOW, MaterialColor.TERRACOTTA_LIGHT_GREEN, 0, 7, 8),
        WHITE_CEDAR(true, MaterialColor.TERRACOTTA_WHITE, MaterialColor.TERRACOTTA_WHITE, MaterialColor.TERRACOTTA_LIGHT_GRAY, 0, 7, 7),
        WILLOW(false, MaterialColor.COLOR_GREEN, MaterialColor.COLOR_GREEN, MaterialColor.TERRACOTTA_BROWN, 0, 7, 11);

        private final boolean conifer;
        private final MaterialColor mainColor;
        private final MaterialColor topColor;
        private final MaterialColor barkColor;
        private final TFCTree tree;
        private final int fallFoliageCoords;
        private final int maxDecayDistance;
        private final int daysToGrow;

        Default(boolean conifer, MaterialColor mainColor, MaterialColor topColor, MaterialColor barkColor, int fallFoliageCoords, int maxDecayDistance, int daysToGrow)
        {
            this.conifer = conifer;
            this.mainColor = mainColor;
            this.topColor = topColor;
            this.barkColor = barkColor;
            this.tree = new TFCTree(Helpers.identifier("tree/" + name().toLowerCase()), Helpers.identifier("tree/" + name().toLowerCase() + "_large"));
            this.fallFoliageCoords = rng.nextInt(256 * 256);
            this.maxDecayDistance = maxDecayDistance;
            this.daysToGrow = daysToGrow;
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

        public int getMaxDecayDistance()
        {
            return maxDecayDistance;
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

        public int getDaysToGrow()
        {
            return daysToGrow;
        }
    }

    public enum BlockType
    {
        // These two constructors were lifted from Blocks#log
        LOG(wood -> new RotatedPillarBlock(AbstractBlock.Properties.of(Material.WOOD, stateIn -> stateIn.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.getTopColor() : wood.getBarkColor()).strength(2.0F).sound(SoundType.WOOD)), false),
        STRIPPED_LOG(wood -> new RotatedPillarBlock(AbstractBlock.Properties.of(Material.WOOD, stateIn -> stateIn.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.getTopColor() : wood.getBarkColor()).strength(2.0F).sound(SoundType.WOOD)), false),
        WOOD(wood -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F).sound(SoundType.WOOD)), false),
        STRIPPED_WOOD(wood -> new RotatedPillarBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F).sound(SoundType.WOOD)), false),
        LEAVES(wood -> TFCLeavesBlock.create(Block.Properties.of(Material.LEAVES, wood.getMainColor()).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion(), wood.getMaxDecayDistance()), false),
        PLANKS(wood -> new Block(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), false),
        SAPLING(wood -> new TFCSaplingBlock(wood.getTree(), new ForgeBlockProperties(Block.Properties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS)).tileEntity(TickCounterTileEntity::new), wood.getDaysToGrow()), false),
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
        TOOL_RACK(wood -> new ToolRackBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F).sound(SoundType.WOOD).noOcclusion()) {}, true),
        TWIG(wood -> GroundcoverBlock.twig(Block.Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.WOOD).noOcclusion()), false),
        FALLEN_LEAVES(wood -> new FallenLeavesBlock(Block.Properties.of(Material.GRASS).strength(0.05F, 0.0F).noOcclusion().sound(SoundType.CROP)), false);

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

        public String nameFor(Default wood)
        {
            return (isPlanksVariant ? "wood/planks/" + wood.name() + "_" + name().toLowerCase() : "wood/" + name() + "/" + wood.name()).toLowerCase();
        }
    }
}