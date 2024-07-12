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
import java.util.function.Predicate;
import java.util.function.Supplier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
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
import net.dries007.tfc.common.items.HammerItem;
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

        public Tier next()
        {
            return this == TIER_VI ? TIER_VI : VALUES[this.ordinal() + 1];
        }

        public Tier previous()
        {
            return this == TIER_0 ? TIER_0 : VALUES[this.ordinal() - 1];
        }

        public Component getDisplayName()
        {
            return Helpers.translateEnum(this);
        }
    }

    /**
     * Default metals that are used for block registration calls.
     * Not extensible.
     *
     * @see Metal instead and register via json
     */
    public enum Default implements StringRepresentable, RegistryMetal
    {
        BISMUTH(0xFF486B72, MapColor.TERRACOTTA_GREEN, Rarity.COMMON, true, false, false),
        BISMUTH_BRONZE(0xFF418E4F, MapColor.TERRACOTTA_BLUE, Rarity.COMMON, Tier.TIER_II, TFCTiers.BISMUTH_BRONZE, TFCArmorMaterials.BISMUTH_BRONZE, true, true, true),
        BLACK_BRONZE(0xFF3B2636, MapColor.TERRACOTTA_PINK, Rarity.COMMON, Tier.TIER_II, TFCTiers.BLACK_BRONZE, TFCArmorMaterials.BLACK_BRONZE, true, true, true),
        BRONZE(0xFF96892E, MapColor.TERRACOTTA_ORANGE, Rarity.COMMON, Tier.TIER_II, TFCTiers.BRONZE, TFCArmorMaterials.BRONZE, true, true, true),
        BRASS(0xFF7C5E33, MapColor.GOLD, Rarity.COMMON, true, false, false),
        COPPER(0xFFB64027, MapColor.COLOR_ORANGE, Rarity.COMMON, Tier.TIER_I, TFCTiers.COPPER, TFCArmorMaterials.COPPER, true, true, true),
        GOLD(0xFFDCBF1B, MapColor.GOLD, Rarity.COMMON, true, false, false),
        NICKEL(0xFF4E4E3C, MapColor.STONE, Rarity.COMMON, true, false, false),
        ROSE_GOLD(0xFFEB7137, MapColor.COLOR_PINK, Rarity.COMMON, true, false, false),
        SILVER(0xFF949495, MapColor.COLOR_LIGHT_GRAY, Rarity.COMMON, true, false, false),
        TIN(0xFF90A4BB, MapColor.COLOR_LIGHT_GRAY, Rarity.COMMON, true, false, false),
        ZINC(0xFFBBB9C4, MapColor.COLOR_LIGHT_GRAY, Rarity.COMMON, true, false, false),
        STERLING_SILVER(0xFFAC927B, MapColor.COLOR_LIGHT_GRAY, Rarity.COMMON, true, false, false),
        WROUGHT_IRON(0xFF989897, MapColor.METAL, Rarity.COMMON, Tier.TIER_III, TFCTiers.WROUGHT_IRON, TFCArmorMaterials.WROUGHT_IRON, true, true, true),
        CAST_IRON(0xFF989897,MapColor.COLOR_BROWN, Rarity.COMMON, true, false, false),
        PIG_IRON(0xFF6A595C, MapColor.COLOR_GRAY, Rarity.COMMON, false, false, false),
        STEEL(0xFF5F5F5F, MapColor.COLOR_LIGHT_GRAY, Rarity.UNCOMMON, Tier.TIER_IV, TFCTiers.STEEL, TFCArmorMaterials.STEEL, true, true, true),
        BLACK_STEEL(0xFF111111, MapColor.COLOR_BLACK, Rarity.RARE, Tier.TIER_V, TFCTiers.BLACK_STEEL, TFCArmorMaterials.BLACK_STEEL, true, true, true),
        BLUE_STEEL(0xFF2D5596, MapColor.COLOR_BLUE, Rarity.EPIC, Tier.TIER_VI, TFCTiers.BLUE_STEEL, TFCArmorMaterials.BLUE_STEEL, true, true, true),
        RED_STEEL(0xFF700503, MapColor.COLOR_RED, Rarity.EPIC, Tier.TIER_VI, TFCTiers.RED_STEEL, TFCArmorMaterials.RED_STEEL, true, true, true),
        WEAK_STEEL(0xFF111111, MapColor.COLOR_GRAY, Rarity.COMMON, false, false, false),
        WEAK_BLUE_STEEL(0xFF2D5596, MapColor.COLOR_BLUE, Rarity.COMMON, false, false, false),
        WEAK_RED_STEEL(0xFF700503, MapColor.COLOR_RED, Rarity.COMMON, false, false, false),
        HIGH_CARBON_STEEL(0xFF5F5F5F, MapColor.COLOR_GRAY, Rarity.COMMON, false, false, false),
        HIGH_CARBON_BLACK_STEEL(0xFF111111, MapColor.COLOR_BLACK, Rarity.COMMON, false, false, false),
        HIGH_CARBON_BLUE_STEEL(0xFF2D5596, MapColor.COLOR_BLUE, Rarity.COMMON, false, false, false),
        HIGH_CARBON_RED_STEEL(0xFF700503, MapColor.COLOR_RED, Rarity.COMMON, false, false, false),
        UNKNOWN(0xFF2F2B27, MapColor.COLOR_BLACK, Rarity.COMMON, false, false, false);

        private final String serializedName;
        private final boolean parts, armor, utility;
        private final Tier metalTier;
        @Nullable private final net.minecraft.world.item.Tier toolTier;
        @Nullable private final ArmorMaterial armorTier;
        private final MapColor mapColor;
        private final Rarity rarity;
        private final int color;

        Default(int color, MapColor mapColor, Rarity rarity, boolean parts, boolean armor, boolean utility)
        {
            this(color, mapColor, rarity, Tier.TIER_0, null, null, parts, armor, utility);
        }

        Default(int color, MapColor mapColor, Rarity rarity, Tier metalTier, @Nullable net.minecraft.world.item.Tier toolTier, @Nullable ArmorMaterial armorTier, boolean parts, boolean armor, boolean utility)
        {
            this.serializedName = name().toLowerCase(Locale.ROOT);
            this.metalTier = metalTier;
            this.toolTier = toolTier;
            this.armorTier = armorTier;
            this.rarity = rarity;
            this.mapColor = mapColor;
            this.color = color;

            this.parts = parts;
            this.armor = armor;
            this.utility = utility;
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

        public Rarity getRarity()
        {
            return rarity;
        }

        public boolean hasParts()
        {
            return parts;
        }

        public boolean hasArmor()
        {
            return armor;
        }

        public boolean hasTools()
        {
            return toolTier != null;
        }

        public boolean hasUtilities()
        {
            return utility;
        }

        @Override
        public net.minecraft.world.item.Tier toolTier()
        {
            return Objects.requireNonNull(toolTier, "Tried to get non-existent tier from " + name());
        }

        @Override
        public ArmorMaterial armorTier()
        {
            return Objects.requireNonNull(armorTier, "Tried to get non-existent armor tier from " + name());
        }

        @Override
        public Tier metalTier()
        {
            return metalTier;
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
        ANVIL(Type.UTILITY, metal -> new AnvilBlock(ExtendedProperties.of().mapColor(metal.mapColor()).noOcclusion().sound(SoundType.ANVIL).strength(10, 10).requiresCorrectToolForDrops().blockEntity(TFCBlockEntities.ANVIL), metal.metalTier())),
        BLOCK(Type.PART, metal -> new Block(BlockBehaviour.Properties.of().mapColor(metal.mapColor()).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL))),
        BLOCK_SLAB(Type.PART, metal -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(metal.mapColor()).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL))),
        BLOCK_STAIRS(Type.PART, metal -> new StairBlock(() -> metal.getFullBlock().get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(metal.mapColor()).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL))),
        BARS(Type.UTILITY, metal -> new IronBarsBlock(BlockBehaviour.Properties.of().mapColor(metal.mapColor()).requiresCorrectToolForDrops().strength(6.0F, 7.0F).sound(SoundType.METAL).noOcclusion())),
        CHAIN(Type.UTILITY, metal -> new TFCChainBlock(Block.Properties.of().mapColor(metal.mapColor()).requiresCorrectToolForDrops().strength(5, 6).sound(SoundType.CHAIN).lightLevel(TFCBlocks.lavaLoggedBlockEmission()))),
        LAMP(Type.UTILITY, metal -> new LampBlock(ExtendedProperties.of().mapColor(metal.mapColor()).noOcclusion().sound(SoundType.LANTERN).strength(4, 10).randomTicks().pushReaction(PushReaction.DESTROY).lightLevel(state -> state.getValue(LampBlock.LIT) ? 15 : 0).blockEntity(TFCBlockEntities.LAMP)), (block, properties) -> new LampBlockItem(block, properties.stacksTo(1))),
        TRAPDOOR(Type.UTILITY, metal -> new TrapDoorBlock(Block.Properties.of().mapColor(metal.mapColor()).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion().isValidSpawn(TFCBlocks::never), BlockSetType.IRON));

        private final Function<RegistryMetal, Block> blockFactory;
        private final BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;
        private final Type type;
        private final String serializedName;

        BlockType(Type type, Function<RegistryMetal, Block> blockFactory, BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory)
        {
            this.type = type;
            this.blockFactory = blockFactory;
            this.blockItemFactory = blockItemFactory;
            this.serializedName = name().toLowerCase(Locale.ROOT);
        }

        BlockType(Type type, Function<RegistryMetal, Block> blockFactory)
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
            return type.hasType(metal);
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
        INGOT(Type.DEFAULT, true, metal -> new IngotItem(properties(metal))),
        DOUBLE_INGOT(Type.PART, false),
        SHEET(Type.PART, false),
        DOUBLE_SHEET(Type.PART, false),
        ROD(Type.PART, false),
        TUYERE(Type.TOOL, metal -> new TieredItem(metal.toolTier(), properties(metal))),
        FISH_HOOK(Type.TOOL, false),
        FISHING_ROD(Type.TOOL, metal -> new TFCFishingRodItem(properties(metal).defaultDurability(metal.toolTier().getUses()), metal.toolTier())),
        UNFINISHED_LAMP(Type.UTILITY, metal -> new Item(properties(metal))),

        // Tools and Tool Heads
        PICKAXE(Type.TOOL, metal -> new PickaxeItem(metal.toolTier(), (int) ToolItem.calculateVanillaAttackDamage(0.75f, metal.toolTier()), -2.8F, properties(metal))),
        PICKAXE_HEAD(Type.TOOL, true),
        PROPICK(Type.TOOL, metal -> new PropickItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.5f, metal.toolTier()), -2.8F, properties(metal))),
        PROPICK_HEAD(Type.TOOL, true),
        AXE(Type.TOOL, metal -> new AxeItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(1.5f, metal.toolTier()), -3.1F, properties(metal))),
        AXE_HEAD(Type.TOOL, true),
        SHOVEL(Type.TOOL, metal -> new ShovelItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.875F, metal.toolTier()), -3.0F, properties(metal))),
        SHOVEL_HEAD(Type.TOOL, true),
        HOE(Type.TOOL, metal -> new TFCHoeItem(metal.toolTier(), -1, -2f, properties(metal))),
        HOE_HEAD(Type.TOOL, true),
        CHISEL(Type.TOOL, metal -> new ChiselItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.27f, metal.toolTier()), -1.5F, properties(metal))),
        CHISEL_HEAD(Type.TOOL, true),
        HAMMER(Type.TOOL, metal -> new HammerItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(1f, metal.toolTier()), -3, properties(metal), metal.getSerializedName())),
        HAMMER_HEAD(Type.TOOL, true),
        SAW(Type.TOOL, metal -> new AxeItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.5f, metal.toolTier()), -3, properties(metal))),
        SAW_BLADE(Type.TOOL, true),
        JAVELIN(Type.TOOL, metal -> new JavelinItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.7f, metal.toolTier()), 1.5f * metal.toolTier().getAttackDamageBonus(), -2.6F, properties(metal), metal.getSerializedName())),
        JAVELIN_HEAD(Type.TOOL, true),
        SWORD(Type.TOOL, metal -> new SwordItem(metal.toolTier(), (int) ToolItem.calculateVanillaAttackDamage(1f, metal.toolTier()), -2.4F, properties(metal))),
        SWORD_BLADE(Type.TOOL, true),
        MACE(Type.TOOL, metal -> new MaceItem(metal.toolTier(), (int) ToolItem.calculateVanillaAttackDamage(1.3f, metal.toolTier()), -3, properties(metal))),
        MACE_HEAD(Type.TOOL, true),
        KNIFE(Type.TOOL, metal -> new ToolItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.6f, metal.toolTier()), -2.0F, TFCTags.Blocks.MINEABLE_WITH_KNIFE, properties(metal))),
        KNIFE_BLADE(Type.TOOL, true),
        SCYTHE(Type.TOOL, metal -> new ScytheItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.7f, metal.toolTier()), -3.2F, TFCTags.Blocks.MINEABLE_WITH_SCYTHE, properties(metal))),
        SCYTHE_BLADE(Type.TOOL, true),
        SHEARS(Type.TOOL, metal -> new ShearsItem(properties(metal).defaultDurability(metal.toolTier().getUses()))),

        // Armor
        UNFINISHED_HELMET(Type.ARMOR, false),
        HELMET(Type.ARMOR, metal -> new ArmorItem(metal.armorTier(), ArmorItem.Type.HELMET, properties(metal))),
        UNFINISHED_CHESTPLATE(Type.ARMOR, false),
        CHESTPLATE(Type.ARMOR, metal -> new ArmorItem(metal.armorTier(), ArmorItem.Type.CHESTPLATE, properties(metal))),
        UNFINISHED_GREAVES(Type.ARMOR, false),
        GREAVES(Type.ARMOR, metal -> new ArmorItem(metal.armorTier(), ArmorItem.Type.LEGGINGS, properties(metal))),
        UNFINISHED_BOOTS(Type.ARMOR, false),
        BOOTS(Type.ARMOR, metal -> new ArmorItem(metal.armorTier(), ArmorItem.Type.BOOTS, properties(metal))),
        HORSE_ARMOR(Type.ARMOR, metal -> new HorseArmorItem(Mth.floor(metal.armorTier().getDefenseForType(ArmorItem.Type.CHESTPLATE) * 1.5), Helpers.identifier("textures/entity/animal/horse_armor/" + metal.getSerializedName() + ".png"), properties(metal))),

        SHIELD(Type.TOOL, metal -> new TFCShieldItem(metal.toolTier(), properties(metal)));

        public static Item.Properties properties(RegistryMetal metal)
        {
            return new Item.Properties().rarity(metal.getRarity());
        }

        private final Function<RegistryMetal, Item> itemFactory;
        private final Type type;
        private final boolean mold;

        ItemType(Type type, boolean mold)
        {
            this(type, mold, metal -> new Item(properties(metal)));
        }

        ItemType(Type type, Function<RegistryMetal, Item> itemFactory)
        {
            this(type, false, itemFactory);
        }

        ItemType(Type type, boolean mold, Function<RegistryMetal, Item> itemFactory)
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
            return type.hasType(metal);
        }

        public boolean hasMold()
        {
            return mold;
        }
    }

    private enum Type
    {
        DEFAULT(metal -> true),
        PART(Default::hasParts),
        TOOL(Default::hasTools),
        ARMOR(Default::hasArmor),
        UTILITY(Default::hasUtilities);

        private final Predicate<Metal.Default> predicate;

        Type(Predicate<Metal.Default> predicate)
        {
            this.predicate = predicate;
        }

        boolean hasType(Metal.Default metal)
        {
            return predicate.test(metal);
        }
    }
}