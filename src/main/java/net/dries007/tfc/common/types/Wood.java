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

import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.FallenLeavesBlock;
import net.dries007.tfc.common.blocks.wood.TFCLeavesBlock;
import net.dries007.tfc.common.blocks.wood.TFCSaplingBlock;
import net.dries007.tfc.common.blocks.wood.ToolRackBlock;
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
        ACACIA(false, MaterialColor.YELLOW_TERRACOTTA, MaterialColor.YELLOW_TERRACOTTA, MaterialColor.LIGHT_GRAY, 0, 7),
        ASH(false, MaterialColor.PINK_TERRACOTTA, MaterialColor.PINK_TERRACOTTA, MaterialColor.YELLOW_TERRACOTTA, 0, 8),
        ASPEN(false, MaterialColor.GREEN_TERRACOTTA, MaterialColor.GREEN_TERRACOTTA, MaterialColor.WOOL, 0, 7),
        BIRCH(false, MaterialColor.BROWN, MaterialColor.BROWN, MaterialColor.WOOL, 0, 7),
        BLACKWOOD(false, MaterialColor.BLACK, MaterialColor.BLACK, MaterialColor.BROWN, 0, 7),
        CHESTNUT(false, MaterialColor.RED_TERRACOTTA, MaterialColor.RED_TERRACOTTA, MaterialColor.LIME, 0, 8),
        DOUGLAS_FIR(false, MaterialColor.YELLOW, MaterialColor.YELLOW, MaterialColor.BROWN, 0, 7),
        HICKORY(false, MaterialColor.BROWN, MaterialColor.BROWN, MaterialColor.GRAY, 0, 7),
        KAPOK(true, MaterialColor.PINK, MaterialColor.PINK, MaterialColor.BROWN, 0, 7),
        MAPLE(false, MaterialColor.GOLD, MaterialColor.GOLD, MaterialColor.GRAY_TERRACOTTA, 0, 8),
        OAK(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.BROWN, 0, 8),
        PALM(true, MaterialColor.YELLOW_TERRACOTTA, MaterialColor.GOLD, MaterialColor.BROWN, 0, 7),
        PINE(true, MaterialColor.GRAY_TERRACOTTA, MaterialColor.GRAY_TERRACOTTA, MaterialColor.GRAY, 0, 7),
        ROSEWOOD(false, MaterialColor.RED, MaterialColor.RED, MaterialColor.LIGHT_GRAY, 0, 9),
        SEQUOIA(true, MaterialColor.RED_TERRACOTTA, MaterialColor.RED_TERRACOTTA, MaterialColor.RED_TERRACOTTA, 0, 7),
        SPRUCE(true, MaterialColor.PINK_TERRACOTTA, MaterialColor.PINK_TERRACOTTA, MaterialColor.BLACK_TERRACOTTA, 0, 7),
        SYCAMORE(false, MaterialColor.YELLOW, MaterialColor.YELLOW, MaterialColor.LIME, 0, 7),
        WHITE_CEDAR(true, MaterialColor.WOOL, MaterialColor.WOOL, MaterialColor.LIGHT_GRAY, 0, 7),
        WILLOW(false, MaterialColor.GREEN, MaterialColor.GREEN, MaterialColor.BROWN, 0, 7);

        private final boolean conifer;
        private final MaterialColor mainColor;
        private final MaterialColor topColor;
        private final MaterialColor barkColor;
        private final TFCTree tree;
        private final int fallFoliageCoords;
        private final int maxDecayDistance;

        Default(boolean conifer, MaterialColor mainColor, MaterialColor topColor, MaterialColor barkColor, int fallFoliageCoords, int maxDecayDistance)
        {
            this.conifer = conifer;
            this.mainColor = mainColor;
            this.topColor = topColor;
            this.barkColor = barkColor;
            this.tree = new TFCTree(Helpers.identifier("tree/" + name().toLowerCase()), Helpers.identifier("tree/" + name().toLowerCase() + "_large"));
            this.fallFoliageCoords = rng.nextInt(256 * 256);
            this.maxDecayDistance = maxDecayDistance;
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
    }

    public enum BlockType
    {
        // These two constructors were lifted from Blocks#log
        LOG(wood -> new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, stateIn -> stateIn.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.getTopColor() : wood.getBarkColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD)), false),
        STRIPPED_LOG(wood -> new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, stateIn -> stateIn.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.getTopColor() : wood.getBarkColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD)), false),
        WOOD(wood -> new RotatedPillarBlock(Block.Properties.create(Material.WOOD, wood.getMainColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD)), false),
        STRIPPED_WOOD(wood -> new RotatedPillarBlock(Block.Properties.create(Material.WOOD, wood.getMainColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD)), false),
        LEAVES(wood -> TFCLeavesBlock.create(Block.Properties.create(Material.LEAVES, wood.getMainColor()).hardnessAndResistance(0.5F).sound(SoundType.PLANT).tickRandomly().notSolid(), wood.getMaxDecayDistance()), false),
        PLANKS(wood -> new Block(Block.Properties.create(Material.WOOD, wood.getMainColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), false),
        SAPLING(wood -> new TFCSaplingBlock(wood.getTree(), Block.Properties.create(Material.PLANTS).notSolid().tickRandomly().hardnessAndResistance(0).sound(SoundType.PLANT)), false),
        BOOKSHELF(wood -> new Block(Block.Properties.create(Material.WOOD, wood.getMainColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        DOOR(wood -> new DoorBlock(Block.Properties.create(Material.WOOD, wood.getMainColor()).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()) {}, true),
        TRAPDOOR(wood -> new TrapDoorBlock(Block.Properties.create(Material.WOOD, wood.getMainColor()).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()) {}, true),
        FENCE(wood -> new FenceBlock(Block.Properties.create(Material.WOOD, wood.getMainColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        LOG_FENCE(wood -> new FenceBlock(Block.Properties.create(Material.WOOD, wood.getMainColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        FENCE_GATE(wood -> new FenceGateBlock(Block.Properties.create(Material.WOOD, wood.getMainColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        BUTTON(wood -> new WoodButtonBlock(Block.Properties.create(Material.MISCELLANEOUS).notSolid().hardnessAndResistance(0.5F).sound(SoundType.WOOD)) {}, true),
        PRESSURE_PLATE(wood -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD, wood.getMainColor()).notSolid().hardnessAndResistance(0.5F).sound(SoundType.WOOD)) {}, true),
        SLAB(wood -> new SlabBlock(Block.Properties.create(Material.WOOD, wood.getMainColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        STAIRS(wood -> new StairsBlock(() -> TFCBlocks.WOODS.get(wood).get(PLANKS).get().getDefaultState(), Block.Properties.create(Material.WOOD, wood.getMainColor()).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        TOOL_RACK(wood -> new ToolRackBlock(Block.Properties.create(Material.WOOD, wood.getMainColor()).hardnessAndResistance(2.0F).sound(SoundType.WOOD).notSolid()) {}, true),
        TWIG(wood -> GroundcoverBlock.twig(Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.05F, 0.0F).sound(SoundType.WOOD).notSolid()), false),
        FALLEN_LEAVES(wood -> new FallenLeavesBlock(Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.05F, 0.0F).notSolid().sound(SoundType.CROP)), false);

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