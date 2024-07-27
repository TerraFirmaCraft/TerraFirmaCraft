/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blockentities.LoomBlockEntity;
import net.dries007.tfc.common.blockentities.SluiceBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.rotation.WaterWheelBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.WindmillBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.JarShelfBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.blocks.devices.SluiceBlock;
import net.dries007.tfc.common.blocks.rotation.AxleBlock;
import net.dries007.tfc.common.blocks.rotation.BladedAxleBlock;
import net.dries007.tfc.common.blocks.rotation.ClutchBlock;
import net.dries007.tfc.common.blocks.rotation.EncasedAxleBlock;
import net.dries007.tfc.common.blocks.rotation.GearBoxBlock;
import net.dries007.tfc.common.blocks.rotation.WaterWheelBlock;
import net.dries007.tfc.common.blocks.rotation.WindmillBlock;
import net.dries007.tfc.common.items.BarrelBlockItem;
import net.dries007.tfc.common.items.ChestBlockItem;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryWood;

/**
 * Default wood types used for block registration calls.
 *
 * @see RegistryWood for addon support, to use {@link BlockType}.
 */
public enum Wood implements RegistryWood
{
    ACACIA(false, MapColor.TERRACOTTA_ORANGE, MapColor.TERRACOTTA_LIGHT_GRAY, 11, 210),
    ASH(false, MapColor.TERRACOTTA_PINK, MapColor.TERRACOTTA_ORANGE, 7, 10),
    ASPEN(false, MapColor.TERRACOTTA_GREEN, MapColor.TERRACOTTA_WHITE, 8, 250),
    BIRCH(false, MapColor.COLOR_BROWN, MapColor.TERRACOTTA_WHITE, 7, 145),
    BLACKWOOD(false, MapColor.COLOR_BLACK, MapColor.COLOR_BROWN, 8, 80),
    CHESTNUT(false, MapColor.TERRACOTTA_RED, MapColor.COLOR_LIGHT_GREEN, 7, 40),
    DOUGLAS_FIR(true, MapColor.TERRACOTTA_YELLOW, MapColor.TERRACOTTA_BROWN, 7, 0),
    HICKORY(false, MapColor.TERRACOTTA_BROWN, MapColor.COLOR_GRAY, 10, 230),
    KAPOK(false, MapColor.COLOR_PURPLE, MapColor.COLOR_BROWN, 7, 30),
    MANGROVE(false, MapColor.COLOR_RED, MapColor.COLOR_BROWN, 8, 100),
    MAPLE(false, MapColor.COLOR_ORANGE, MapColor.TERRACOTTA_GRAY, 7, 0),
    OAK(false, MapColor.WOOD, MapColor.COLOR_BROWN, 10, 120),
    PALM(false, MapColor.COLOR_ORANGE, MapColor.COLOR_BROWN, 7, 255),
    PINE(true, MapColor.TERRACOTTA_GRAY, MapColor.COLOR_GRAY, 7, 0),
    ROSEWOOD(false, MapColor.COLOR_RED, MapColor.TERRACOTTA_LIGHT_GRAY, 8, 170),
    SEQUOIA(true, MapColor.TERRACOTTA_RED, MapColor.TERRACOTTA_RED, 18, 0),
    SPRUCE(true, MapColor.TERRACOTTA_PINK, MapColor.TERRACOTTA_BLACK, 7, 0),
    SYCAMORE(false, MapColor.COLOR_YELLOW, MapColor.TERRACOTTA_LIGHT_GREEN, 8, 200),
    WHITE_CEDAR(true, MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_LIGHT_GRAY, 7, 0),
    WILLOW(false, MapColor.COLOR_GREEN, MapColor.TERRACOTTA_BROWN, 11, 225);

    public static final Wood[] VALUES = values();

    private final String serializedName;
    private final boolean conifer;
    private final MapColor woodColor;
    private final MapColor barkColor;
    private final TreeGrower tree;
    private final int daysToGrow;
    private final BlockSetType blockSet;
    private final WoodType woodType;
    private final int autumnIndex;

    Wood(boolean conifer, MapColor woodColor, MapColor barkColor, int daysToGrow, int autumnIndex)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.conifer = conifer;
        this.woodColor = woodColor;
        this.barkColor = barkColor;
        this.tree = new TreeGrower(
            Helpers.identifier(serializedName).toString(),
            Optional.empty(),
            Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, Helpers.identifier("tree/" + serializedName))),
            Optional.empty()
        );
        this.daysToGrow = daysToGrow;
        this.autumnIndex = autumnIndex;
        this.blockSet = new BlockSetType(serializedName);
        this.woodType = new WoodType(Helpers.identifier(serializedName).toString(), blockSet);
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

    @Override
    public BlockSetType getBlockSet()
    {
        return blockSet;
    }

    @Override
    public WoodType getVanillaWoodType()
    {
        return woodType;
    }

    @Override
    public MapColor woodColor()
    {
        return woodColor;
    }

    @Override
    public MapColor barkColor()
    {
        return barkColor;
    }

    @Override
    public TreeGrower tree()
    {
        return tree;
    }

    @Override
    public int daysToGrow()
    {
        return TFCConfig.SERVER.saplingGrowthDays.get(this).get();
    }

    @Override
    public int autumnIndex()
    {
        return autumnIndex;
    }

    public int defaultDaysToGrow()
    {
        return daysToGrow;
    }

    @Override
    public Supplier<Block> getBlock(BlockType type)
    {
        return TFCBlocks.WOODS.get(this).get(type);
    }

    public enum BlockType
    {
        LOG(false, (self, wood) -> new LogBlock(ExtendedProperties.of(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.woodColor() : wood.barkColor()).strength(8f).sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).requiresCorrectToolForDrops().flammableLikeLogs(), wood.getBlock(self.stripped()))),
        STRIPPED_LOG(false, wood -> new LogBlock(ExtendedProperties.of(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.woodColor() : wood.barkColor()).strength(7.5f).sound(SoundType.WOOD).requiresCorrectToolForDrops().flammableLikeLogs(), null)),
        WOOD(false, (self, wood) -> new LogBlock(properties(wood).strength(8f).requiresCorrectToolForDrops().flammableLikeLogs(), wood.getBlock(self.stripped()))),
        STRIPPED_WOOD(false, wood -> new LogBlock(properties(wood).strength(7.5f).requiresCorrectToolForDrops().flammableLikeLogs(), null)),
        LEAVES(false, (self, wood) -> new TFCLeavesBlock(ExtendedProperties.of().mapColor(MapColor.PLANT).strength(0.5F).sound(SoundType.GRASS).defaultInstrument().randomTicks().noOcclusion().isViewBlocking(TFCBlocks::never).flammableLikeLeaves(), wood.autumnIndex(), wood.getBlock(self.fallenLeaves()), wood.getBlock(self.twig()))),
        PLANKS(false, wood -> new ExtendedBlock(properties(wood).strength(1.5f, 3.0F).flammableLikePlanks())),
        SAPLING(false, wood -> new TFCSaplingBlock(wood.tree(), ExtendedProperties.of(MapColor.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS).flammableLikeLeaves().blockEntity(TFCBlockEntities.TICK_COUNTER), wood::daysToGrow, wood == Wood.PALM)),
        POTTED_SAPLING(false, wood -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, wood.getBlock(SAPLING), BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ACACIA_SAPLING))),
        BOOKSHELF(true, wood -> new BookshelfBlock(properties(wood).strength(2.0F, 3.0F).flammable(20, 30).enchantPower(BookshelfBlock::getEnchantPower).blockEntity(TFCBlockEntities.BOOKSHELF))),
        DOOR(true, wood -> new TFCDoorBlock(properties(wood).strength(3.0F).noOcclusion().flammableLikePlanks(), wood.getBlockSet())),
        TRAPDOOR(true, wood -> new TFCTrapDoorBlock(properties(wood).strength(3.0F).noOcclusion().flammableLikePlanks(), wood.getBlockSet())),
        FENCE(true, wood -> new TFCFenceBlock(properties(wood).strength(2.0F, 3.0F).flammableLikePlanks())),
        LOG_FENCE(true, wood -> new TFCFenceBlock(properties(wood).strength(2.0F, 3.0F).flammableLikeLogs())),
        FENCE_GATE(true, wood -> new TFCFenceGateBlock(properties(wood).strength(2.0F, 3.0F).flammableLikePlanks())),
        BUTTON(true, wood -> new TFCWoodButtonBlock(ExtendedProperties.of().noCollission().strength(0.5F).sound(SoundType.WOOD).flammableLikePlanks(), wood.getBlockSet())),
        PRESSURE_PLATE(true, wood -> new TFCPressurePlateBlock(wood.getBlockSet(), properties(wood).noCollission().strength(0.5F).sound(SoundType.WOOD).flammableLikePlanks())),
        SLAB(true, wood -> new TFCSlabBlock(properties(wood).strength(1.5f, 3.0F).flammableLikePlanks())),
        STAIRS(true, wood -> new TFCStairBlock(() -> wood.getBlock(PLANKS).get().defaultBlockState(), properties(wood).strength(1.5f, 3.0F).sound(SoundType.WOOD).flammableLikePlanks())),
        TOOL_RACK(true, wood -> new ToolRackBlock(properties(wood).strength(2.0F).noOcclusion().blockEntity(TFCBlockEntities.TOOL_RACK))),
        TWIG(false, wood -> GroundcoverBlock.twig(ExtendedProperties.of().strength(0.05F, 0.0F).sound(SoundType.WOOD).noCollission().flammableLikeWool())),
        FALLEN_LEAVES(false, (self, wood) -> new FallenLeavesBlock(ExtendedProperties.of().strength(0.05F, 0.0F).noOcclusion().noCollission().isViewBlocking(TFCBlocks::never).sound(SoundType.CROP).flammableLikeWool(), wood.getBlock(self.leaves()))),
        VERTICAL_SUPPORT(false, wood -> new VerticalSupportBlock(properties(wood).strength(1.0F).noOcclusion().flammableLikeLogs())),
        HORIZONTAL_SUPPORT(false, wood -> new HorizontalSupportBlock(properties(wood).strength(1.0F).noOcclusion().flammableLikeLogs())),
        WORKBENCH(true, wood -> new TFCCraftingTableBlock(properties(wood).strength(2.5F).flammableLikeLogs())),
        TRAPPED_CHEST(false, (self, wood) -> new TFCTrappedChestBlock(properties(wood).strength(2.5F).flammableLikeLogs().blockEntity(TFCBlockEntities.TRAPPED_CHEST).clientTicks(ChestBlockEntity::lidAnimateTick), wood.getSerializedName()), ChestBlockItem::new),
        CHEST(false, (self, wood) -> new TFCChestBlock(properties(wood).strength(2.5F).flammableLikeLogs().blockEntity(TFCBlockEntities.CHEST).clientTicks(ChestBlockEntity::lidAnimateTick), wood.getSerializedName()), ChestBlockItem::new),
        LOOM(true, (self, wood) -> new TFCLoomBlock(properties(wood).strength(2.5F).noOcclusion().flammableLikePlanks().blockEntity(TFCBlockEntities.LOOM).ticks(LoomBlockEntity::tick), self.planksTexture(wood))),
        SLUICE(false, wood -> new SluiceBlock(properties(wood).strength(3F).noOcclusion().flammableLikeLogs().blockEntity(TFCBlockEntities.SLUICE).serverTicks(SluiceBlockEntity::serverTick))),
        SIGN(true, wood -> new TFCStandingSignBlock(properties(wood).noCollission().strength(1F).flammableLikePlanks().blockEntity(TFCBlockEntities.SIGN).ticks(SignBlockEntity::tick), wood.getVanillaWoodType())),
        WALL_SIGN(true, wood -> new TFCWallSignBlock(properties(wood).noCollission().strength(1F).dropsLike(wood.getBlock(SIGN)).flammableLikePlanks().blockEntity(TFCBlockEntities.SIGN).ticks(SignBlockEntity::tick), wood.getVanillaWoodType())),
        BARREL(false, (self, wood) -> new BarrelBlock(properties(wood).strength(2.5f).flammableLikePlanks().noOcclusion().blockEntity(TFCBlockEntities.BARREL).serverTicks(BarrelBlockEntity::serverTick)), BarrelBlockItem::new),
        LECTERN(false, wood -> new TFCLecternBlock(properties(wood).noCollission().strength(2.5F).flammableLikePlanks().blockEntity(TFCBlockEntities.LECTERN))),
        SCRIBING_TABLE(false, wood -> new ScribingTableBlock(properties(wood).noOcclusion().strength(2.5F).flammable(20, 30))),
        SEWING_TABLE(false, wood -> new SewingTableBlock(properties(wood).noOcclusion().strength(2.5F).flammable(20, 30))),
        JAR_SHELF(false, wood -> new JarShelfBlock(properties(wood).noOcclusion().strength(2.5f).flammableLikePlanks().blockEntity(TFCBlockEntities.JARS))),
        AXLE(false, (self, wood) -> new AxleBlock(properties(wood).noOcclusion().strength(2.5F).flammableLikeLogs().pushReaction(PushReaction.DESTROY).blockEntity(TFCBlockEntities.AXLE), getBlock(wood, self.windmill()), self.planksTexture(wood))),
        BLADED_AXLE(false, (self, wood) -> new BladedAxleBlock(properties(wood).noOcclusion().strength(2.5F).flammableLikeLogs().pushReaction(PushReaction.DESTROY).blockEntity(TFCBlockEntities.BLADED_AXLE), getBlock(wood, self.axle()))),
        ENCASED_AXLE(false, (self, wood) -> new EncasedAxleBlock(properties(wood).strength(2.5F).flammableLikeLogs().pushReaction(PushReaction.DESTROY).blockEntity(TFCBlockEntities.ENCASED_AXLE))),
        CLUTCH(false, (self, wood) -> new ClutchBlock(properties(wood).strength(2.5F).flammableLikeLogs().pushReaction(PushReaction.DESTROY).blockEntity(TFCBlockEntities.CLUTCH), getBlock(wood, self.axle()))),
        GEAR_BOX(false, (self, wood) -> new GearBoxBlock(properties(wood).strength(2f).noOcclusion().blockEntity(TFCBlockEntities.GEAR_BOX), getBlock(wood, self.axle()))),
        WINDMILL(false, (self, wood) -> new WindmillBlock(properties(wood).strength(9f).noOcclusion().blockEntity(TFCBlockEntities.WINDMILL).ticks(WindmillBlockEntity::serverTick, WindmillBlockEntity::clientTick), getBlock(wood, self.axle()))),
        WATER_WHEEL(false, (self, wood) -> new WaterWheelBlock(properties(wood).strength(9f).noOcclusion().blockEntity(TFCBlockEntities.WATER_WHEEL).ticks(WaterWheelBlockEntity::serverTick, WaterWheelBlockEntity::clientTick), getBlock(wood, self.axle()), wood.getSerializedName()))
        ;

        private static ExtendedProperties properties(RegistryWood wood)
        {
            return ExtendedProperties.of(wood.woodColor()).sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS);
        }

        @SuppressWarnings("unchecked")
        private static <B extends Block> Supplier<? extends B> getBlock(RegistryWood wood, BlockType type)
        {
            return (Supplier<? extends B>) wood.getBlock(type);
        }

        private final boolean isPlanksVariant;
        private final BiFunction<BlockType, RegistryWood, Block> blockFactory;
        private final TriFunction<Block, Item.Properties, RegistryWood, ? extends BlockItem> blockItemFactory;

        BlockType(boolean isPlanksVariant, Function<RegistryWood, Block> blockFactory)
        {
            this(isPlanksVariant, (self, wood) -> blockFactory.apply(wood));
        }

        BlockType(boolean isPlanksVariant, BiFunction<BlockType, RegistryWood, Block> blockFactory)
        {
            this(isPlanksVariant, blockFactory, BlockItem::new);
        }

        BlockType(boolean isPlanksVariant, BiFunction<BlockType, RegistryWood, Block> blockFactory, BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory)
        {
            this.blockFactory = blockFactory;
            this.isPlanksVariant = isPlanksVariant;
            this.blockItemFactory = (block, properties, self) -> blockItemFactory.apply(block, properties);
        }

        BlockType(boolean isPlanksVariant, BiFunction<BlockType, RegistryWood, Block> blockFactory, TriFunction<Block, Item.Properties, RegistryWood, ? extends BlockItem> blockItemFactory)
        {
            this.blockFactory = blockFactory;
            this.isPlanksVariant = isPlanksVariant;
            this.blockItemFactory = blockItemFactory;
        }

        @Nullable
        public Function<Block, BlockItem> createBlockItem(RegistryWood wood, Item.Properties properties)
        {
            return needsItem() ? block -> blockItemFactory.apply(block, properties, wood) : null;
        }

        public String nameFor(RegistryWood wood)
        {
            return (isPlanksVariant ? "wood/planks/" + wood.getSerializedName() + "_" + name() : "wood/" + name() + "/" + wood.getSerializedName()).toLowerCase(Locale.ROOT);
        }

        public boolean needsItem()
        {
            return this != VERTICAL_SUPPORT && this != HORIZONTAL_SUPPORT && this != SIGN && this != WALL_SIGN && this != POTTED_SAPLING && this != WINDMILL;
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

        private ResourceLocation planksTexture(RegistryWood wood)
        {
            return Helpers.identifier("block/wood/planks/" + wood.getSerializedName());
        }

        private BlockType twig() { return TWIG; }
        private BlockType fallenLeaves() { return FALLEN_LEAVES; }
        private BlockType leaves() { return LEAVES; }
        private BlockType axle() { return AXLE; }
        private BlockType windmill() { return WINDMILL; }

        public Supplier<Block> create(RegistryWood wood)
        {
            return () -> blockFactory.apply(this, wood);
        }
    }

    public static void registerBlockSetTypes()
    {
        for (Wood wood : VALUES)
        {
            BlockSetType.register(wood.blockSet);
            WoodType.register(wood.woodType);
        }
    }
}
