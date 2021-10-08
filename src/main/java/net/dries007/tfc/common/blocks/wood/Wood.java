/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Locale;
import java.util.function.Supplier;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.feature.tree.TFCTreeGrower;

/**
 * Default wood types used for block registration calls
 */
public enum Wood implements StringRepresentable
{
    ACACIA(false, MaterialColor.TERRACOTTA_ORANGE, MaterialColor.TERRACOTTA_ORANGE, MaterialColor.TERRACOTTA_LIGHT_GRAY, 7, 11),
    ASH(false, MaterialColor.TERRACOTTA_PINK, MaterialColor.TERRACOTTA_PINK, MaterialColor.TERRACOTTA_ORANGE, 8, 7),
    ASPEN(false, MaterialColor.TERRACOTTA_GREEN, MaterialColor.TERRACOTTA_GREEN, MaterialColor.TERRACOTTA_WHITE, 7, 8),
    BIRCH(false, MaterialColor.COLOR_BROWN, MaterialColor.COLOR_BROWN, MaterialColor.TERRACOTTA_WHITE, 7, 7),
    BLACKWOOD(false, MaterialColor.COLOR_BLACK, MaterialColor.COLOR_BLACK, MaterialColor.COLOR_BROWN, 7, 8),
    CHESTNUT(false, MaterialColor.TERRACOTTA_RED, MaterialColor.TERRACOTTA_RED, MaterialColor.COLOR_LIGHT_GREEN, 8, 7),
    DOUGLAS_FIR(false, MaterialColor.TERRACOTTA_YELLOW, MaterialColor.TERRACOTTA_YELLOW, MaterialColor.TERRACOTTA_BROWN, 7, 7),
    HICKORY(false, MaterialColor.TERRACOTTA_BROWN, MaterialColor.TERRACOTTA_BROWN, MaterialColor.COLOR_GRAY, 7, 10),
    KAPOK(true, MaterialColor.COLOR_PINK, MaterialColor.COLOR_PINK, MaterialColor.COLOR_BROWN, 8, 7),
    MAPLE(false, MaterialColor.COLOR_ORANGE, MaterialColor.COLOR_ORANGE, MaterialColor.TERRACOTTA_GRAY, 8, 7),
    OAK(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.COLOR_BROWN, 8, 10),
    PALM(true, MaterialColor.COLOR_ORANGE, MaterialColor.COLOR_ORANGE, MaterialColor.COLOR_BROWN, 7, 7),
    PINE(true, MaterialColor.TERRACOTTA_GRAY, MaterialColor.TERRACOTTA_GRAY, MaterialColor.COLOR_GRAY, 7, 7),
    ROSEWOOD(false, MaterialColor.COLOR_RED, MaterialColor.COLOR_RED, MaterialColor.TERRACOTTA_LIGHT_GRAY, 9, 8),
    SEQUOIA(true, MaterialColor.TERRACOTTA_RED, MaterialColor.TERRACOTTA_RED, MaterialColor.TERRACOTTA_RED, 7, 18),
    SPRUCE(true, MaterialColor.TERRACOTTA_PINK, MaterialColor.TERRACOTTA_PINK, MaterialColor.TERRACOTTA_BLACK, 7, 7),
    SYCAMORE(false, MaterialColor.COLOR_YELLOW, MaterialColor.COLOR_YELLOW, MaterialColor.TERRACOTTA_LIGHT_GREEN, 7, 8),
    WHITE_CEDAR(true, MaterialColor.TERRACOTTA_WHITE, MaterialColor.TERRACOTTA_WHITE, MaterialColor.TERRACOTTA_LIGHT_GRAY, 7, 7),
    WILLOW(false, MaterialColor.COLOR_GREEN, MaterialColor.COLOR_GREEN, MaterialColor.TERRACOTTA_BROWN, 7, 11);

    private final String serializedName;
    private final boolean conifer;
    private final MaterialColor mainColor;
    private final MaterialColor topColor;
    private final MaterialColor barkColor;
    private final TFCTreeGrower tree;
    private final int maxDecayDistance;
    private final int daysToGrow;

    Wood(boolean conifer, MaterialColor mainColor, MaterialColor topColor, MaterialColor barkColor, int maxDecayDistance, int daysToGrow)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.conifer = conifer;
        this.mainColor = mainColor;
        this.topColor = topColor;
        this.barkColor = barkColor;
        this.tree = new TFCTreeGrower(Helpers.identifier("tree/" + serializedName), Helpers.identifier("tree/" + serializedName + "_large"));
        this.maxDecayDistance = maxDecayDistance;
        this.daysToGrow = daysToGrow;
    }

    public static Wood[] VALUES = values();

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public boolean isConifer()
    {
        return conifer;
    }

    public TFCTreeGrower getTree()
    {
        return tree;
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

    public enum BlockType
    {
        LOG(wood -> new LogBlock(BlockBehaviour.Properties.of(Material.WOOD, stateIn -> stateIn.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.getTopColor() : wood.getBarkColor()).strength(2.0F).sound(SoundType.WOOD).requiresCorrectToolForDrops()), false),
        STRIPPED_LOG(wood -> new LogBlock(BlockBehaviour.Properties.of(Material.WOOD, stateIn -> stateIn.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.getTopColor() : wood.getBarkColor()).strength(2.0F).sound(SoundType.WOOD).requiresCorrectToolForDrops()), false),
        WOOD(wood -> new LogBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F).sound(SoundType.WOOD).requiresCorrectToolForDrops()), false),
        STRIPPED_WOOD(wood -> new LogBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F).sound(SoundType.WOOD).requiresCorrectToolForDrops()), false),
        LEAVES(wood -> TFCLeavesBlock.create(Block.Properties.of(Material.LEAVES, wood.getMainColor()).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion(), wood.getMaxDecayDistance()), false),
        PLANKS(wood -> new Block(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), false),
        SAPLING(wood -> new TFCSaplingBlock(wood.getTree(), ExtendedProperties.of(Block.Properties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS)).blockEntity(TFCBlockEntities.TICK_COUNTER), wood.getDaysToGrow()), false),
        BOOKSHELF(wood -> new Block(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        DOOR(wood -> new DoorBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()) {}, true),
        TRAPDOOR(wood -> new TrapDoorBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()) {}, true),
        FENCE(wood -> new FenceBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        LOG_FENCE(wood -> new FenceBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        FENCE_GATE(wood -> new FenceGateBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        BUTTON(wood -> new WoodButtonBlock(Block.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)), true),
        PRESSURE_PLATE(wood -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.of(Material.WOOD, wood.getMainColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)), true),
        SLAB(wood -> new SlabBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        STAIRS(wood -> new StairBlock(() -> TFCBlocks.WOODS.get(wood).get(PLANKS).get().defaultBlockState(), Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), true),
        TOOL_RACK(wood -> new ToolRackBlock(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(2.0F).sound(SoundType.WOOD).noOcclusion()) {}, true),
        TWIG(wood -> GroundcoverBlock.twig(Block.Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.WOOD).noOcclusion()), false),
        FALLEN_LEAVES(wood -> new FallenLeavesBlock(Block.Properties.of(Material.GRASS).strength(0.05F, 0.0F).noOcclusion().sound(SoundType.CROP)), false),
        VERTICAL_SUPPORT(wood -> new VerticalSupportBlock(ExtendedProperties.of(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(1.0F).noOcclusion().sound(SoundType.WOOD)).flammable(60, 60)), false),
        HORIZONTAL_SUPPORT(wood -> new HorizontalSupportBlock(ExtendedProperties.of(Block.Properties.of(Material.WOOD, wood.getMainColor()).strength(1.0F).noOcclusion().sound(SoundType.WOOD)).flammable(60, 60)), false),
        WORKBENCH(wood -> new TFCCraftingTableBlock(ExtendedProperties.of(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)).flammable(60, 30)), true);

        public static final BlockType[] VALUES = values();

        public static BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : LOG;
        }

        private final NonNullFunction<Wood, Block> blockFactory;
        private final boolean isPlanksVariant;

        BlockType(NonNullFunction<Wood, Block> blockFactory, boolean isPlanksVariant)
        {
            this.blockFactory = blockFactory;
            this.isPlanksVariant = isPlanksVariant;
        }

        public Supplier<Block> create(Wood wood)
        {
            return () -> blockFactory.apply(wood);
        }

        public String nameFor(Wood wood)
        {
            return (isPlanksVariant ? "wood/planks/" + wood.name() + "_" + name() : "wood/" + name() + "/" + wood.name()).toLowerCase(Locale.ROOT);
        }

        public boolean needsItem()
        {
            return this != VERTICAL_SUPPORT && this != HORIZONTAL_SUPPORT;
        }
    }
}
