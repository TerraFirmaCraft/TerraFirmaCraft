/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.LevelTier;
import net.dries007.tfc.common.TFCArmorMaterials;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.TFCTiers;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCChainBlock;
import net.dries007.tfc.common.blocks.devices.AnvilBlock;
import net.dries007.tfc.common.blocks.devices.LampBlock;
import net.dries007.tfc.common.items.ChiselItem;
import net.dries007.tfc.common.items.JavelinItem;
import net.dries007.tfc.common.items.LampBlockItem;
import net.dries007.tfc.common.items.MaceItem;
import net.dries007.tfc.common.items.PropickItem;
import net.dries007.tfc.common.items.ScytheItem;
import net.dries007.tfc.common.items.TFCFishingRodItem;
import net.dries007.tfc.common.items.TFCHoeItem;
import net.dries007.tfc.common.items.TFCShieldItem;
import net.dries007.tfc.common.items.ToolItem;
import net.dries007.tfc.util.data.FluidHeat;
import net.dries007.tfc.util.registry.RegistryMetal;

/**
 * Default metals that are used for block registration calls.
 * Not extensible.
 *
 * @see FluidHeat instead and register via json
 */
public enum Metal implements StringRepresentable, RegistryMetal
{
    BISMUTH(0xFF486B72, MapColor.TERRACOTTA_GREEN, Rarity.COMMON, PartType.DEFAULT),
    BISMUTH_BRONZE(0xFF418E4F, MapColor.TERRACOTTA_BLUE, Rarity.COMMON, TFCTiers.BISMUTH_BRONZE, TFCArmorMaterials.BISMUTH_BRONZE),
    BLACK_BRONZE(0xFF3B2636, MapColor.TERRACOTTA_PINK, Rarity.COMMON, TFCTiers.BLACK_BRONZE, TFCArmorMaterials.BLACK_BRONZE),
    BRONZE(0xFF96892E, MapColor.TERRACOTTA_ORANGE, Rarity.COMMON, TFCTiers.BRONZE, TFCArmorMaterials.BRONZE),
    BRASS(0xFF7C5E33, MapColor.GOLD, Rarity.COMMON, PartType.DEFAULT),
    COPPER(0xFFB64027, MapColor.COLOR_ORANGE, Rarity.COMMON, TFCTiers.COPPER, TFCArmorMaterials.COPPER),
    GOLD(0xFFDCBF1B, MapColor.GOLD, Rarity.COMMON, PartType.DEFAULT),
    NICKEL(0xFF4E4E3C, MapColor.STONE, Rarity.COMMON, PartType.DEFAULT),
    ROSE_GOLD(0xFFEB7137, MapColor.COLOR_PINK, Rarity.COMMON, PartType.DEFAULT),
    SILVER(0xFF949495, MapColor.COLOR_LIGHT_GRAY, Rarity.COMMON, PartType.DEFAULT),
    TIN(0xFF90A4BB, MapColor.COLOR_LIGHT_GRAY, Rarity.COMMON, PartType.DEFAULT),
    ZINC(0xFFBBB9C4, MapColor.COLOR_LIGHT_GRAY, Rarity.COMMON, PartType.DEFAULT),
    STERLING_SILVER(0xFFAC927B, MapColor.COLOR_LIGHT_GRAY, Rarity.COMMON, PartType.DEFAULT),
    WROUGHT_IRON(0xFF989897, MapColor.METAL, Rarity.COMMON, TFCTiers.WROUGHT_IRON, TFCArmorMaterials.WROUGHT_IRON),
    CAST_IRON(0xFF989897, MapColor.COLOR_BROWN, Rarity.COMMON, PartType.DEFAULT),
    PIG_IRON(0xFF6A595C, MapColor.COLOR_GRAY, Rarity.COMMON, PartType.INGOT_ONLY),
    STEEL(0xFF5F5F5F, MapColor.COLOR_LIGHT_GRAY, Rarity.UNCOMMON, TFCTiers.STEEL, TFCArmorMaterials.STEEL),
    BLACK_STEEL(0xFF111111, MapColor.COLOR_BLACK, Rarity.RARE, TFCTiers.BLACK_STEEL, TFCArmorMaterials.BLACK_STEEL),
    BLUE_STEEL(0xFF2D5596, MapColor.COLOR_BLUE, Rarity.EPIC, TFCTiers.BLUE_STEEL, TFCArmorMaterials.BLUE_STEEL),
    RED_STEEL(0xFF700503, MapColor.COLOR_RED, Rarity.EPIC, TFCTiers.RED_STEEL, TFCArmorMaterials.RED_STEEL),
    WEAK_STEEL(0xFF111111, MapColor.COLOR_GRAY, Rarity.COMMON, PartType.INGOT_ONLY),
    WEAK_BLUE_STEEL(0xFF2D5596, MapColor.COLOR_BLUE, Rarity.COMMON, PartType.INGOT_ONLY),
    WEAK_RED_STEEL(0xFF700503, MapColor.COLOR_RED, Rarity.COMMON, PartType.INGOT_ONLY),
    HIGH_CARBON_STEEL(0xFF5F5F5F, MapColor.COLOR_GRAY, Rarity.COMMON, PartType.INGOT_ONLY),
    HIGH_CARBON_BLACK_STEEL(0xFF111111, MapColor.COLOR_BLACK, Rarity.COMMON, PartType.INGOT_ONLY),
    HIGH_CARBON_BLUE_STEEL(0xFF2D5596, MapColor.COLOR_BLUE, Rarity.COMMON, PartType.INGOT_ONLY),
    HIGH_CARBON_RED_STEEL(0xFF700503, MapColor.COLOR_RED, Rarity.COMMON, PartType.INGOT_ONLY),
    UNKNOWN(0xFF2F2B27, MapColor.COLOR_BLACK, Rarity.COMMON, PartType.INGOT_ONLY);

    private final String serializedName;
    private final PartType partType;
    @Nullable private final LevelTier toolTier;
    @Nullable private final TFCArmorMaterials.Id armorTier;
    private final MapColor mapColor;
    private final Rarity rarity;
    private final int color;

    Metal(int color, MapColor mapColor, Rarity rarity, PartType partType)
    {
        this(color, mapColor, rarity, null, null, partType);
    }

    Metal(int color, MapColor mapColor, Rarity rarity, LevelTier toolTier, TFCArmorMaterials.Id armorTier)
    {
        this(color, mapColor, rarity, toolTier, armorTier, PartType.ALL);
    }

    Metal(int color, MapColor mapColor, Rarity rarity, @Nullable LevelTier toolTier, @Nullable TFCArmorMaterials.Id armorTier, PartType partType)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.toolTier = toolTier;
        this.armorTier = armorTier;
        this.rarity = rarity;
        this.mapColor = mapColor;
        this.color = color;
        this.partType = partType;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public int getColor()
    {
        return color;
    }

    public Rarity rarity()
    {
        return rarity;
    }

    public boolean defaultParts()
    {
        return partType != PartType.INGOT_ONLY;
    }

    public boolean allParts()
    {
        return partType == PartType.ALL;
    }

    @Override
    public LevelTier toolTier()
    {
        return Objects.requireNonNull(toolTier, "Tried to get non-existent tier from " + name());
    }

    @Override
    public Holder<ArmorMaterial> armorTier()
    {
        return Objects.requireNonNull(armorTier, "Tried to get non-existent armor tier from " + name()).holder();
    }

    @Override
    public MapColor mapColor()
    {
        return mapColor;
    }

    @Override
    public Supplier<Block> getFullBlock()
    {
        return TFCBlocks.METALS.get(this).get(BlockType.BLOCK);
    }

    public int tier()
    {
        return toolTier != null ? toolTier.level() : 0;
    }

    public enum BlockType
    {
        ANVIL(PartType.ALL, metal -> new AnvilBlock(ExtendedProperties.of().mapColor(metal.mapColor()).noOcclusion().sound(SoundType.ANVIL).strength(10, 10).requiresCorrectToolForDrops().blockEntity(TFCBlockEntities.ANVIL), metal.toolTier().level())),
        BLOCK(PartType.DEFAULT, metal -> new Block(BlockBehaviour.Properties.of().mapColor(metal.mapColor()).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL))),
        BLOCK_SLAB(PartType.DEFAULT, metal -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(metal.mapColor()).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL))),
        BLOCK_STAIRS(PartType.DEFAULT, metal -> new StairBlock(metal.getFullBlock().get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(metal.mapColor()).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL))),
        BARS(PartType.ALL, metal -> new IronBarsBlock(BlockBehaviour.Properties.of().mapColor(metal.mapColor()).requiresCorrectToolForDrops().strength(6.0F, 7.0F).sound(SoundType.METAL).noOcclusion())),
        CHAIN(PartType.ALL, metal -> new TFCChainBlock(Block.Properties.of().mapColor(metal.mapColor()).requiresCorrectToolForDrops().strength(5, 6).sound(SoundType.CHAIN).lightLevel(TFCBlocks.lavaLoggedBlockEmission()))),
        LAMP(PartType.ALL, metal -> new LampBlock(ExtendedProperties.of().mapColor(metal.mapColor()).noOcclusion().sound(SoundType.LANTERN).strength(4, 10).randomTicks().pushReaction(PushReaction.DESTROY).lightLevel(state -> state.getValue(LampBlock.LIT) ? 15 : 0).blockEntity(TFCBlockEntities.LAMP)), (block, properties) -> new LampBlockItem(block, properties.stacksTo(1))),
        TRAPDOOR(PartType.ALL, metal -> new TrapDoorBlock(BlockSetType.IRON, Block.Properties.of().mapColor(metal.mapColor()).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion().isValidSpawn(TFCBlocks::never)));

        private final Function<RegistryMetal, Block> blockFactory;
        private final BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;
        private final PartType type;
        private final String serializedName;

        BlockType(PartType type, Function<RegistryMetal, Block> blockFactory, BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory)
        {
            this.type = type;
            this.blockFactory = blockFactory;
            this.blockItemFactory = blockItemFactory;
            this.serializedName = name().toLowerCase(Locale.ROOT);
        }

        BlockType(PartType type, Function<RegistryMetal, Block> blockFactory)
        {
            this(type, blockFactory, BlockItem::new);
        }

        public Supplier<Block> create(RegistryMetal metal)
        {
            return () -> blockFactory.apply(metal);
        }

        public Function<Block, BlockItem> createBlockItem(Item.Properties properties)
        {
            return block -> blockItemFactory.apply(block, properties);
        }

        public boolean has(Metal metal)
        {
            return metal.partType.ordinal() >= type.ordinal();
        }

        public boolean isDefault()
        {
            return type != PartType.ALL;
        }

        public String createName(RegistryMetal metal)
        {
            if (this == BLOCK_SLAB || this == BLOCK_STAIRS)
            {
                return BLOCK.createName(metal) + (this == BLOCK_SLAB ? "_slab" : "_stairs");
            }
            else
            {
                return "metal/" + serializedName + "/" + metal.getSerializedName();
            }
        }
    }

    public enum ItemType
    {
        // Generic
        INGOT(PartType.INGOT_ONLY, true),
        DOUBLE_INGOT(PartType.DEFAULT, false),
        SHEET(PartType.DEFAULT, false),
        DOUBLE_SHEET(PartType.DEFAULT, false),
        ROD(PartType.DEFAULT, false),
        TUYERE(PartType.ALL, metal -> new TieredItem(metal.toolTier(), base(metal))),
        FISH_HOOK(PartType.ALL, false),
        FISHING_ROD(PartType.ALL, metal -> new TFCFishingRodItem(base(metal).durability(metal.toolTier().getUses()), metal.toolTier())),
        UNFINISHED_LAMP(PartType.ALL, metal -> new Item(base(metal))),

        // Tools and Tool Heads
        PICKAXE(PartType.ALL, metal -> new PickaxeItem(metal.toolTier(), tool(metal, 0.75f, -2.8f))),
        PICKAXE_HEAD(PartType.ALL, true),
        PROPICK(PartType.ALL, metal -> new PropickItem(metal.toolTier(), tool(metal, 0.5f, -2.8f))),
        PROPICK_HEAD(PartType.ALL, true),
        AXE(PartType.ALL, metal -> new AxeItem(metal.toolTier(), tool(metal, 1.5f, -3.1f))),
        AXE_HEAD(PartType.ALL, true),
        SHOVEL(PartType.ALL, metal -> new ShovelItem(metal.toolTier(), tool(metal, 0.875f, -3.0f))),
        SHOVEL_HEAD(PartType.ALL, true),
        HOE(PartType.ALL, metal -> new TFCHoeItem(metal.toolTier(), tool(metal, -1f, -2.0f))),
        HOE_HEAD(PartType.ALL, true),
        CHISEL(PartType.ALL, metal -> new ChiselItem(metal.toolTier(), tool(metal, -0.27f, 1.5f))),
        CHISEL_HEAD(PartType.ALL, true),
        HAMMER(PartType.ALL, metal -> new ToolItem(metal.toolTier(), TFCTags.Blocks.MINEABLE_WITH_HAMMER, tool(metal, 1f, -3f))),
        HAMMER_HEAD(PartType.ALL, true),
        SAW(PartType.ALL, metal -> new AxeItem(metal.toolTier(), tool(metal, 0.5f, -3f))),
        SAW_BLADE(PartType.ALL, true),
        JAVELIN(PartType.ALL, metal -> new JavelinItem(metal.toolTier(), tool(metal, 0.7f, -2.6f))),
        JAVELIN_HEAD(PartType.ALL, true),
        SWORD(PartType.ALL, metal -> new SwordItem(metal.toolTier(), tool(metal, 1f, -2.4f))),
        SWORD_BLADE(PartType.ALL, true),
        MACE(PartType.ALL, metal -> new MaceItem(metal.toolTier(), tool(metal, 1.3f, -3f))),
        MACE_HEAD(PartType.ALL, true),
        KNIFE(PartType.ALL, metal -> new ToolItem(metal.toolTier(), TFCTags.Blocks.MINEABLE_WITH_KNIFE, tool(metal, 0.6f, -2.0f))),
        KNIFE_BLADE(PartType.ALL, true),
        SCYTHE(PartType.ALL, metal -> new ScytheItem(metal.toolTier(), tool(metal, 0.7f, -3.2f))),
        SCYTHE_BLADE(PartType.ALL, true),
        SHEARS(PartType.ALL, metal -> new ShearsItem(base(metal).durability(metal.toolTier().getUses()))),

        // Armor
        UNFINISHED_HELMET(PartType.ALL, false),
        HELMET(PartType.ALL, metal -> new ArmorItem(metal.armorTier(), ArmorItem.Type.HELMET, base(metal))),
        UNFINISHED_CHESTPLATE(PartType.ALL, false),
        CHESTPLATE(PartType.ALL, metal -> new ArmorItem(metal.armorTier(), ArmorItem.Type.CHESTPLATE, base(metal))),
        UNFINISHED_GREAVES(PartType.ALL, false),
        GREAVES(PartType.ALL, metal -> new ArmorItem(metal.armorTier(), ArmorItem.Type.LEGGINGS, base(metal))),
        UNFINISHED_BOOTS(PartType.ALL, false),
        BOOTS(PartType.ALL, metal -> new ArmorItem(metal.armorTier(), ArmorItem.Type.BOOTS, base(metal))),
        HORSE_ARMOR(PartType.ALL, metal -> new AnimalArmorItem(metal.armorTier(), AnimalArmorItem.BodyType.EQUESTRIAN, false, base(metal))),

        SHIELD(PartType.ALL, metal -> new TFCShieldItem(metal.toolTier(), base(metal)));

        private static Item.Properties base(RegistryMetal metal)
        {
            return new Item.Properties().rarity(metal.rarity());
        }

        private static Item.Properties tool(RegistryMetal metal, float attackDamageFactor, float attackSpeed)
        {
            return base(metal).attributes(ToolItem.productAttributes(metal.toolTier(), attackDamageFactor, attackSpeed));
        }

        private final Function<RegistryMetal, Item> itemFactory;
        private final PartType type;
        private final boolean mold;

        ItemType(PartType type, boolean mold)
        {
            this(type, mold, metal -> new Item(base(metal)));
        }

        ItemType(PartType type, Function<RegistryMetal, Item> itemFactory)
        {
            this(type, false, itemFactory);
        }

        ItemType(PartType type, boolean mold, Function<RegistryMetal, Item> itemFactory)
        {
            this.type = type;
            this.mold = mold;
            this.itemFactory = itemFactory;
        }

        public Item create(RegistryMetal metal)
        {
            return itemFactory.apply(metal);
        }

        public boolean has(Metal metal)
        {
            return metal.partType.ordinal() >= type.ordinal();
        }

        public boolean hasMold()
        {
            return mold;
        }

        public boolean isDefault()
        {
            return type != PartType.ALL;
        }
    }

    enum PartType
    {
        INGOT_ONLY, DEFAULT, ALL
    }
}
