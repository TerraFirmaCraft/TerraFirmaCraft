/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.dries007.tfc.common.blocks.ExtendedBlock;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blockentities.LoomBlockEntity;
import net.dries007.tfc.common.blockentities.SluiceBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.blocks.devices.SluiceBlock;
import net.dries007.tfc.common.items.ChestBlockItem;
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

    public static final Wood[] VALUES = values();

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
        LOG((self, wood) -> new LogBlock(fire(Properties.of(Material.WOOD, state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.getTopColor() : wood.getBarkColor()).strength(2.0F).sound(SoundType.WOOD).requiresCorrectToolForDrops()), getBlock(self.stripped(), wood)), false),
        STRIPPED_LOG(wood -> new LogBlock(fire(Properties.of(Material.WOOD, state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.getTopColor() : wood.getBarkColor()).strength(2.0F).sound(SoundType.WOOD).requiresCorrectToolForDrops()), null), false),
        WOOD((self, wood) -> new LogBlock(fire(properties(wood).strength(2.0F).requiresCorrectToolForDrops()), getBlock(self.stripped(), wood)), false),
        STRIPPED_WOOD(wood -> new LogBlock(fire(properties(wood).strength(2.0F).requiresCorrectToolForDrops()), null), false),
        LEAVES(wood -> TFCLeavesBlock.create(fire(Properties.of(Material.LEAVES, wood.getMainColor()).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion().isViewBlocking(TFCBlocks::never)), wood.getMaxDecayDistance()), false),
        PLANKS(wood -> new ExtendedBlock(fire(properties(wood).strength(2.0F, 3.0F))), false),
        SAPLING(wood -> new TFCSaplingBlock(wood.getTree(), fire(Properties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS)).blockEntity(TFCBlockEntities.TICK_COUNTER), wood.getDaysToGrow()), false),
        BOOKSHELF(wood -> new ExtendedBlock(fire(properties(wood).strength(2.0F, 3.0F))), true),
        DOOR(wood -> new TFCDoorBlock(fire(properties(wood).strength(3.0F).noOcclusion())), true),
        TRAPDOOR(wood -> new TFCTrapDoorBlock(fire(properties(wood).strength(3.0F).noOcclusion())), true),
        FENCE(wood -> new TFCFenceBlock(fire(properties(wood).strength(2.0F, 3.0F))), true),
        LOG_FENCE(wood -> new TFCFenceBlock(fire(properties(wood).strength(2.0F, 3.0F))), true),
        FENCE_GATE(wood -> new TFCFenceGateBlock(fire(properties(wood).strength(2.0F, 3.0F))), true),
        BUTTON(wood -> new TFCWoodButtonBlock(fire(Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD))), true),
        PRESSURE_PLATE(wood -> new TFCPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, fire(properties(wood).noCollission().strength(0.5F).sound(SoundType.WOOD))), true),
        SLAB(wood -> new TFCSlabBlock(fire(properties(wood).strength(2.0F, 3.0F))), true),
        STAIRS(wood -> new TFCStairBlock(() -> getBlock(PLANKS, wood).get().defaultBlockState(), fire(properties(wood).strength(2.0F, 3.0F).sound(SoundType.WOOD))), true),
        TOOL_RACK(wood -> new ToolRackBlock(fire(properties(wood).strength(2.0F).noOcclusion()).blockEntity(TFCBlockEntities.TOOL_RACK)), true),
        TWIG(wood -> GroundcoverBlock.twig(fire(Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.WOOD).noCollission())), false),
        FALLEN_LEAVES(wood -> new FallenLeavesBlock(fire(Properties.of(Material.GRASS).strength(0.05F, 0.0F).noOcclusion().sound(SoundType.CROP))), false),
        VERTICAL_SUPPORT(wood -> new VerticalSupportBlock(fire(properties(wood).strength(1.0F).noOcclusion()).flammable(60, 60)), false),
        HORIZONTAL_SUPPORT(wood -> new HorizontalSupportBlock(fire(properties(wood).strength(1.0F).noOcclusion()).flammable(60, 60)), false),
        WORKBENCH(wood -> new TFCCraftingTableBlock(fire(properties(wood).strength(2.5F)).flammable(60, 30)), true),
        TRAPPED_CHEST((self, wood) -> new TFCTrappedChestBlock(fire(properties(wood).strength(2.5F)).flammable(60, 30).blockEntity(TFCBlockEntities.TRAPPED_CHEST).clientTicks(ChestBlockEntity::lidAnimateTick), wood.getSerializedName()), false, ChestBlockItem::new),
        CHEST((self, wood) -> new TFCChestBlock(fire(properties(wood).strength(2.5F)).flammable(60, 30).blockEntity(TFCBlockEntities.CHEST).clientTicks(ChestBlockEntity::lidAnimateTick), wood.getSerializedName()), false, (block, properties) -> new ChestBlockItem(block, properties)),
        LOOM(wood -> new TFCLoomBlock(fire(properties(wood).strength(2.5F).noOcclusion()).flammable(60, 30).blockEntity(TFCBlockEntities.LOOM).ticks(LoomBlockEntity::tick), Helpers.identifier("block/wood/planks/" + wood.name().toLowerCase())), true),
        SLUICE(wood -> new SluiceBlock(fire(properties(wood).strength(3F).noOcclusion()).flammable(30, 30).blockEntity(TFCBlockEntities.SLUICE).serverTicks(SluiceBlockEntity::serverTick)), false),
        SIGN(wood -> new TFCStandingSignBlock(fire(properties(wood).noCollission().strength(1F)).flammable(60, 30).blockEntity(TFCBlockEntities.SIGN), wood), true),
        WALL_SIGN(wood -> new TFCWallSignBlock(fire(properties(wood).noCollission().strength(1F).lootFrom(getBlock(SIGN, wood))).flammable(60, 30).blockEntity(TFCBlockEntities.SIGN), wood), true),
        BARREL((self, wood) -> new BarrelBlock(fire(properties(wood).strength(2.5f)).flammable(60, 30).blockEntity(TFCBlockEntities.BARREL).serverTicks(BarrelBlockEntity::serverTick)), false);

        public static final BlockType[] VALUES = values();

        public static BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : LOG;
        }

        private final BiFunction<BlockType, Wood, Block> blockFactory;
        private final BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;
        private final boolean isPlanksVariant;

        BlockType(Function<Wood, Block> blockFactory, boolean isPlanksVariant)
        {
            this((self, wood) -> blockFactory.apply(wood), isPlanksVariant);
        }

        BlockType(BiFunction<BlockType, Wood, Block> blockFactory, boolean isPlanksVariant)
        {
            this(blockFactory, isPlanksVariant, BlockItem::new);
        }

        BlockType(BiFunction<BlockType, Wood, Block> blockFactory, boolean isPlanksVariant, BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory)
        {
            this.blockFactory = blockFactory;
            this.isPlanksVariant = isPlanksVariant;
            this.blockItemFactory = blockItemFactory;
        }

        public Supplier<Block> create(Wood wood)
        {
            return () -> blockFactory.apply(this, wood);
        }

        @Nullable
        public Function<Block, BlockItem> createBlockItem(Item.Properties properties)
        {
            return needsItem() ? block -> blockItemFactory.apply(block, properties) : null;
        }

        public String nameFor(Wood wood)
        {
            return (isPlanksVariant ? "wood/planks/" + wood.name() + "_" + name() : "wood/" + name() + "/" + wood.name()).toLowerCase(Locale.ROOT);
        }

        public boolean needsItem()
        {
            return this != VERTICAL_SUPPORT && this != HORIZONTAL_SUPPORT && this != SIGN && this != WALL_SIGN;
        }

        private BlockType stripped()
        {
            return switch (this)
                {
                    case LOG -> STRIPPED_LOG;
                    case WOOD -> STRIPPED_WOOD;
                    default -> throw new IllegalStateException("Block type " + name() + " does not have a stripped variant");
                };
        }

        private static Properties properties(Wood wood)
        {
            return Properties.of(Material.WOOD, wood.getMainColor()).sound(SoundType.WOOD);
        }

        private static ExtendedProperties fire(Properties properties)
        {
            return ExtendedProperties.of(properties).flammable(60, 30);
        }

        private static RegistryObject<Block> getBlock(BlockType type, Wood wood)
        {
            return TFCBlocks.WOODS.get(wood).get(type);
        }
    }
}
