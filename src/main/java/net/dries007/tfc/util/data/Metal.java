/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
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
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.items.ChiselItem;
import net.dries007.tfc.common.items.IngotItem;
import net.dries007.tfc.common.items.JavelinItem;
import net.dries007.tfc.common.items.LampBlockItem;
import net.dries007.tfc.common.items.MaceItem;
import net.dries007.tfc.common.items.PropickItem;
import net.dries007.tfc.common.items.ScytheItem;
import net.dries007.tfc.common.items.TFCFishingRodItem;
import net.dries007.tfc.common.items.TFCHoeItem;
import net.dries007.tfc.common.items.TFCShieldItem;
import net.dries007.tfc.common.items.ToolItem;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryMetal;
import net.dries007.tfc.world.Codecs;


/**
 * @param specificHeatCapacity The Specific Heat Capacity of the metal. Units of Energy / (°C * mB)
 */
public record Metal(
    int tier,
    Fluid fluid,
    float meltTemperature,
    float specificHeatCapacity,

    ResourceLocation id,
    ResourceLocation textureId,
    ResourceLocation softTextureId,
    String translationKey,

    IngredientParts parts
) {
    public static final Codec<Metal> CODEC = RecordCodecBuilder.create(i -> i.group(
        DataManager.ID.forGetter(c -> null),
        Codec.INT.fieldOf("tier").forGetter(c -> c.tier),
        Codecs.FLUID.fieldOf("fluid").forGetter(c -> c.fluid),
        Codec.FLOAT.fieldOf("melt_temperature").forGetter(c -> c.meltTemperature),
        Codec.FLOAT.fieldOf("specific_heat_capacity").forGetter(c -> c.specificHeatCapacity),
        RecordCodecBuilder.<IngredientParts>mapCodec(j -> j.group(
            Ingredient.CODEC.optionalFieldOf("ingots").forGetter(c -> c.ingots),
            Ingredient.CODEC.optionalFieldOf("double_ingots").forGetter(c -> c.doubleIngots),
            Ingredient.CODEC.optionalFieldOf("sheets").forGetter(c -> c.sheets)
        ).apply(j, IngredientParts::new)).forGetter(c -> c.parts)
    ).apply(i, Metal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Metal> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, c -> c.id,
        ByteBufCodecs.VAR_INT, c -> c.tier,
        ByteBufCodecs.registry(Registries.FLUID), c -> c.fluid,
        ByteBufCodecs.FLOAT, c -> c.meltTemperature,
        ByteBufCodecs.FLOAT, c -> c.specificHeatCapacity,
        StreamCodec.composite(
            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), c -> c.ingots,
            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), c -> c.doubleIngots,
            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), c -> c.sheets,
            IngredientParts::new
        ), c -> c.parts,
        Metal::new
    );

    public record IngredientParts(
        Optional<Ingredient> ingots,
        Optional<Ingredient> doubleIngots,
        Optional<Ingredient> sheets
    ) {}

    public static final ResourceLocation UNKNOWN_ID = Helpers.identifier("unknown");
    public static final ResourceLocation WROUGHT_IRON_ID = Helpers.identifier("wrought_iron");

    public static final DataManager<Metal> MANAGER = new DataManager<>(Helpers.identifier("metals"), "metal", CODEC, STREAM_CODEC);

    private static final Map<Fluid, Metal> METAL_FLUIDS = new HashMap<>();

    /**
     * Reverse lookup for metals attached to fluids.
     * For the other direction, see {@link Metal#fluid()}.
     *
     * @param fluid The fluid, can be empty.
     * @return A metal if it exists, and null if it doesn't.
     */
    @Nullable
    public static Metal get(Fluid fluid)
    {
        return METAL_FLUIDS.get(fluid);
    }

    /**
     * Get the 'unknown' metal. This is the only metal that any assurances are made that it exists.
     */
    public static Metal unknown()
    {
        return MANAGER.getOrThrow(UNKNOWN_ID);
    }

    /**
     * @return The matching metal for a given ingot, as defined by the metal itself.
     */
    @Nullable
    public static Metal getFromIngot(ItemStack stack)
    {
        for (Metal metal : MANAGER.getValues())
        {
            if (metal.isIngot(stack) || metal.isDoubleIngot(stack))
            {
                return metal;
            }
        }
        return null;
    }

    @Nullable
    public static Metal getFromSheet(ItemStack stack)
    {
        for (Metal metal : MANAGER.getValues())
        {
            if (metal.isSheet(stack))
            {
                return metal;
            }
        }
        return null;
    }

    public static void updateMetalFluidMap()
    {
        // Ensure 'unknown' metal exists
        unknown();

        // Reload fluid -> metal map
        METAL_FLUIDS.clear();
        for (Metal metal : MANAGER.getValues())
        {
            METAL_FLUIDS.put(metal.fluid(), metal);
        }
    }

    /**
     * <strong>Not for general purpose use!</strong> Explicitly creates unregistered metals outside the system, which are able to act as rendering stubs.
     */
    public Metal(ResourceLocation id)
    {
        this(id, 0, Fluids.EMPTY, 0, 0, new IngredientParts(Optional.empty(), Optional.empty(), Optional.empty()));
    }

    public Metal(ResourceLocation id, int tier, Fluid fluid, float meltTemperature, float specificHeatCapacity, IngredientParts parts)
    {
        this(
            tier, fluid, meltTemperature, specificHeatCapacity, id,
            id.withPrefix("block/metal/block/"),
            id.withPrefix("block/metal/smooth/"),
            "metal." + id.getNamespace() + "." + id.getPath(),
            parts
        );
    }

    /**
     * @return The Specific Heat Capacity of the metal. Units of Energy / °C
     * @see IHeat#getHeatCapacity()
     */
    public float heatCapacity(float mB)
    {
        return specificHeatCapacity() * mB;
    }

    public MutableComponent getDisplayName()
    {
        return Component.translatable(translationKey);
    }

    public boolean isIngot(ItemStack stack)
    {
        return parts.ingots.isPresent() && parts.ingots.get().test(stack);
    }

    public boolean isDoubleIngot(ItemStack stack)
    {
        return parts.ingots.isPresent() && parts.ingots.get().test(stack);
    }

    public boolean isSheet(ItemStack stack)
    {
        return parts.sheets.isPresent() && parts.sheets.get().test(stack);
    }

    /**
     * Metals / Anvils:
     * T0 - Rock - Work None, Weld T1
     * T1 - Copper - Work T1, Weld T2
     * T2 - Bronze / Bismuth Bronze / Black Bronze - Work T2, Weld T3
     * T3 - Wrought Iron - Work T3, Weld T4
     * T4 - Steel - Work T4, Weld T5
     * T5 - Black Steel - Work T5, Weld T6
     * T6 - Red Steel / Blue Steel - Work T6, Weld T6
     */
    public enum Tier
    {
        TIER_0, TIER_I, TIER_II, TIER_III, TIER_IV, TIER_V, TIER_VI;

        private static final Tier[] VALUES = values();

        public static Tier valueOf(int tier)
        {
            return tier < 0 || tier > VALUES.length ? TIER_I : VALUES[tier];
        }
    }

    enum PartType
    {
        NONE, DEFAULT, ALL
    }

    /**
     * Default metals that are used for block registration calls.
     * Not extensible.
     *
     * @see Metal instead and register via json
     */
    public enum Default implements StringRepresentable, RegistryMetal
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
        CAST_IRON(0xFF989897,MapColor.COLOR_BROWN, Rarity.COMMON, PartType.DEFAULT),
        PIG_IRON(0xFF6A595C, MapColor.COLOR_GRAY, Rarity.COMMON, PartType.NONE),
        STEEL(0xFF5F5F5F, MapColor.COLOR_LIGHT_GRAY, Rarity.UNCOMMON, TFCTiers.STEEL, TFCArmorMaterials.STEEL),
        BLACK_STEEL(0xFF111111, MapColor.COLOR_BLACK, Rarity.RARE, TFCTiers.BLACK_STEEL, TFCArmorMaterials.BLACK_STEEL),
        BLUE_STEEL(0xFF2D5596, MapColor.COLOR_BLUE, Rarity.EPIC, TFCTiers.BLUE_STEEL, TFCArmorMaterials.BLUE_STEEL),
        RED_STEEL(0xFF700503, MapColor.COLOR_RED, Rarity.EPIC, TFCTiers.RED_STEEL, TFCArmorMaterials.RED_STEEL),
        WEAK_STEEL(0xFF111111, MapColor.COLOR_GRAY, Rarity.COMMON, PartType.NONE),
        WEAK_BLUE_STEEL(0xFF2D5596, MapColor.COLOR_BLUE, Rarity.COMMON, PartType.NONE),
        WEAK_RED_STEEL(0xFF700503, MapColor.COLOR_RED, Rarity.COMMON, PartType.NONE),
        HIGH_CARBON_STEEL(0xFF5F5F5F, MapColor.COLOR_GRAY, Rarity.COMMON, PartType.NONE),
        HIGH_CARBON_BLACK_STEEL(0xFF111111, MapColor.COLOR_BLACK, Rarity.COMMON, PartType.NONE),
        HIGH_CARBON_BLUE_STEEL(0xFF2D5596, MapColor.COLOR_BLUE, Rarity.COMMON, PartType.NONE),
        HIGH_CARBON_RED_STEEL(0xFF700503, MapColor.COLOR_RED, Rarity.COMMON, PartType.NONE),
        UNKNOWN(0xFF2F2B27, MapColor.COLOR_BLACK, Rarity.COMMON, PartType.NONE);

        private final String serializedName;
        private final PartType partType;
        @Nullable private final LevelTier toolTier;
        @Nullable private final TFCArmorMaterials.Id armorTier;
        private final MapColor mapColor;
        private final Rarity rarity;
        private final int color;

        Default(int color, MapColor mapColor, Rarity rarity, PartType partType)
        {
            this(color, mapColor, rarity, null, null, partType);
        }

        Default(int color, MapColor mapColor, Rarity rarity, LevelTier toolTier, TFCArmorMaterials.Id armorTier)
        {
            this(color, mapColor, rarity, toolTier, armorTier, PartType.ALL);
        }

        Default(int color, MapColor mapColor, Rarity rarity, @Nullable LevelTier toolTier, @Nullable TFCArmorMaterials.Id armorTier, PartType partType)
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

        public boolean has(Default metal)
        {
            return metal.partType.ordinal() >= type.ordinal();
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
        INGOT(PartType.DEFAULT, true, metal -> new IngotItem(base(metal))),
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

        public boolean has(Default metal)
        {
            return metal.partType.ordinal() >= type.ordinal();
        }

        public boolean hasMold()
        {
            return mold;
        }
    }
}