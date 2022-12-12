/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCArmorMaterials;
import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.TFCTiers;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCChainBlock;
import net.dries007.tfc.common.blocks.devices.AnvilBlock;
import net.dries007.tfc.common.blocks.devices.LampBlock;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.items.*;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.registry.RegistryMetal;
import org.jetbrains.annotations.Nullable;

public final class Metal
{
    public static final ResourceLocation UNKNOWN_ID = Helpers.identifier("unknown");
    public static final ResourceLocation WROUGHT_IRON_ID = Helpers.identifier("wrought_iron");

    public static final DataManager<Metal> MANAGER = new DataManager<>(Helpers.identifier("metals"), "metal", Metal::new, Metal::new, Metal::encode, Packet::new);

    private static final Map<Fluid, Metal> METAL_FLUIDS = new HashMap<>();

    /**
     * Reverse lookup for metals attached to fluids.
     * For the other direction, see {@link Metal#getFluid()}.
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
            if (metal.isIngot(stack))
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
            METAL_FLUIDS.put(metal.getFluid(), metal);
        }
    }

    private final int tier;
    private final Fluid fluid;
    private final float meltTemperature;
    private final float specificHeatCapacity;

    private final ResourceLocation id;
    private final ResourceLocation textureId;
    private final String translationKey;

    private final Ingredient ingots, sheets;

    public Metal(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.textureId = new ResourceLocation(id.getNamespace(), "block/metal/full/" + id.getPath());

        this.tier = JsonHelpers.getAsInt(json, "tier", 0);
        this.fluid = JsonHelpers.getRegistryEntry(json, "fluid", ForgeRegistries.FLUIDS);
        this.specificHeatCapacity = JsonHelpers.getAsFloat(json, "specific_heat_capacity");
        this.meltTemperature = JsonHelpers.getAsFloat(json, "melt_temperature");
        this.translationKey = "metal." + id.getNamespace() + "." + id.getPath();

        this.ingots = Ingredient.fromJson(JsonHelpers.get(json, "ingots"));
        this.sheets = Ingredient.fromJson(JsonHelpers.get(json, "sheets"));
    }

    public Metal(ResourceLocation id, FriendlyByteBuf buffer)
    {
        this.id = id;
        this.textureId = new ResourceLocation(id.getNamespace(), "block/metal/full/" + id.getPath());

        this.tier = buffer.readVarInt();
        this.fluid = buffer.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
        this.meltTemperature = buffer.readFloat();
        this.specificHeatCapacity = buffer.readFloat();
        this.translationKey = buffer.readUtf();

        this.ingots = Ingredient.fromNetwork(buffer);
        this.sheets = Ingredient.fromNetwork(buffer);
    }

    /**
     * <strong>Not for general purpose use!</strong> Explicitly creates unregistered metals outside of the system, which are able to act as rendering stubs.
     */
    public Metal(ResourceLocation id)
    {
        this.id = id;
        this.textureId = new ResourceLocation(id.getNamespace(), "block/metal/full/" + id.getPath());

        this.tier = 0;
        this.fluid = Fluids.EMPTY;
        this.meltTemperature = 0;
        this.specificHeatCapacity = 0;
        this.translationKey = "";

        this.ingots = Ingredient.EMPTY;
        this.sheets = Ingredient.EMPTY;
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(tier);
        buffer.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, fluid);
        buffer.writeFloat(meltTemperature);
        buffer.writeFloat(specificHeatCapacity);
        buffer.writeUtf(translationKey);

        ingots.toNetwork(buffer);
        sheets.toNetwork(buffer);
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public ResourceLocation getTextureId()
    {
        return textureId;
    }

    public int getTier()
    {
        return tier;
    }

    public Fluid getFluid()
    {
        return fluid;
    }

    public float getMeltTemperature()
    {
        return meltTemperature;
    }

    /**
     * @return The Specific Heat Capacity of the metal. Units of Energy / °C
     * @see IHeat#getHeatCapacity()
     */
    public float getHeatCapacity(float mB)
    {
        return getSpecificHeatCapacity() * mB;
    }

    /**
     * @return The Specific Heat Capacity of the metal. Units of Energy / (°C * mB)
     */
    public float getSpecificHeatCapacity()
    {
        return specificHeatCapacity;
    }

    public MutableComponent getDisplayName()
    {
        return Helpers.translatable(translationKey);
    }

    public String getTranslationKey()
    {
        return translationKey;
    }

    public boolean isIngot(ItemStack stack)
    {
        return ingots.test(stack);
    }

    public Ingredient getIngotIngredient()
    {
        return ingots;
    }

    public boolean isSheet(ItemStack stack)
    {
        return sheets.test(stack);
    }

    public Ingredient getSheetIngredient()
    {
        return sheets;
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

        private final String translationKey;

        Tier()
        {
            translationKey = Helpers.getEnumTranslationKey(this);
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
            return Helpers.translatable(translationKey);
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
        BISMUTH(0xFF486B72, Rarity.COMMON, true, false, false),
        BISMUTH_BRONZE(0xFF418E4F, Rarity.COMMON, Tier.TIER_II, TFCTiers.BISMUTH_BRONZE, TFCArmorMaterials.BISMUTH_BRONZE, true, true, true),
        BLACK_BRONZE(0xFF3B2636, Rarity.COMMON, Tier.TIER_II, TFCTiers.BLACK_BRONZE, TFCArmorMaterials.BLACK_BRONZE, true, true, true),
        BRONZE(0xFF96892E, Rarity.COMMON, Tier.TIER_II, TFCTiers.BRONZE, TFCArmorMaterials.BRONZE, true, true, true),
        BRASS(0xFF7C5E33, Rarity.COMMON, true, false, false),
        COPPER(0xFFB64027, Rarity.COMMON, Tier.TIER_I, TFCTiers.COPPER, TFCArmorMaterials.COPPER, true, true, true),
        GOLD(0xFFDCBF1B, Rarity.COMMON, true, false, false),
        NICKEL(0xFF4E4E3C, Rarity.COMMON, true, false, false),
        ROSE_GOLD(0xFFEB7137, Rarity.COMMON, true, false, false),
        SILVER(0xFF949495, Rarity.COMMON, true, false, false),
        TIN(0xFF90A4BB, Rarity.COMMON, true, false, false),
        ZINC(0xFFBBB9C4, Rarity.COMMON, true, false, false),
        STERLING_SILVER(0xFFAC927B, Rarity.COMMON, true, false, false),
        WROUGHT_IRON(0xFF989897, Rarity.COMMON, Tier.TIER_III, TFCTiers.WROUGHT_IRON, TFCArmorMaterials.WROUGHT_IRON, true, true, true),
        CAST_IRON(0xFF989897, Rarity.COMMON, true, false, false),
        PIG_IRON(0xFF6A595C, Rarity.COMMON, false, false, false),
        STEEL(0xFF5F5F5F, Rarity.UNCOMMON, Tier.TIER_IV, TFCTiers.STEEL, TFCArmorMaterials.STEEL, true, true, true),
        BLACK_STEEL(0xFF111111, Rarity.RARE, Tier.TIER_V, TFCTiers.BLACK_STEEL, TFCArmorMaterials.BLACK_STEEL, true, true, true),
        BLUE_STEEL(0xFF2D5596, Rarity.EPIC, Tier.TIER_VI, TFCTiers.BLUE_STEEL, TFCArmorMaterials.BLUE_STEEL, true, true, true),
        RED_STEEL(0xFF700503, Rarity.EPIC, Tier.TIER_VI, TFCTiers.RED_STEEL, TFCArmorMaterials.RED_STEEL, true, true, true),
        WEAK_STEEL(0xFF111111, Rarity.COMMON, false, false, false),
        WEAK_BLUE_STEEL(0xFF2D5596, Rarity.COMMON, false, false, false),
        WEAK_RED_STEEL(0xFF700503, Rarity.COMMON, false, false, false),
        HIGH_CARBON_STEEL(0xFF5F5F5F, Rarity.COMMON, false, false, false),
        HIGH_CARBON_BLACK_STEEL(0xFF111111, Rarity.COMMON, false, false, false),
        HIGH_CARBON_BLUE_STEEL(0xFF2D5596, Rarity.COMMON, false, false, false),
        HIGH_CARBON_RED_STEEL(0xFF700503, Rarity.COMMON, false, false, false),
        UNKNOWN(0xFF2F2B27, Rarity.COMMON, false, false, false);

        private final String serializedName;
        private final boolean parts, armor, utility;
        private final Tier metalTier;
        @Nullable private final net.minecraft.world.item.Tier toolTier;
        @Nullable private final ArmorMaterial armorTier;
        private final Rarity rarity;
        private final int color;

        Default(int color, Rarity rarity, boolean parts, boolean armor, boolean utility)
        {
            this(color, rarity, Tier.TIER_0, null, null, parts, armor, utility);
        }

        Default(int color, Rarity rarity, Tier metalTier, @Nullable net.minecraft.world.item.Tier toolTier, @Nullable ArmorMaterial armorTier, boolean parts, boolean armor, boolean utility)
        {
            this.serializedName = name().toLowerCase(Locale.ROOT);
            this.metalTier = metalTier;
            this.toolTier = toolTier;
            this.armorTier = armorTier;
            this.rarity = rarity;
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
    }

    public enum BlockType
    {
        ANVIL(Type.UTILITY, metal -> new AnvilBlock(ExtendedProperties.of(Material.METAL).noOcclusion().sound(SoundType.METAL).strength(10, 10).requiresCorrectToolForDrops().blockEntity(TFCBlockEntities.ANVIL), metal.metalTier())),
        CHAIN(Type.UTILITY, metal -> new TFCChainBlock(Block.Properties.of(Material.METAL, MaterialColor.NONE).requiresCorrectToolForDrops().strength(5, 6).sound(SoundType.CHAIN))),
        LAMP(Type.UTILITY, metal -> new LampBlock(ExtendedProperties.of(Material.METAL).noOcclusion().sound(SoundType.LANTERN).strength(4, 10).randomTicks().lightLevel(state -> state.getValue(LampBlock.LIT) ? 15 : 0).blockEntity(TFCBlockEntities.LAMP)), (block, properties) -> new LampBlockItem(block, properties.stacksTo(1))),
        TRAPDOOR(Type.UTILITY, metal -> new TrapDoorBlock(Block.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion().isValidSpawn(TFCBlocks::never)));

        private final Function<RegistryMetal, Block> blockFactory;
        private final BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;
        private final Type type;

        BlockType(Type type, Function<RegistryMetal, Block> blockFactory, BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory)
        {
            this.type = type;
            this.blockFactory = blockFactory;
            this.blockItemFactory = blockItemFactory;
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
    }

    public enum ItemType
    {
        // Generic
        INGOT(Type.DEFAULT, true, metal -> new IngotItem(properties())),
        DOUBLE_INGOT(Type.PART, false),
        SHEET(Type.PART, false),
        DOUBLE_SHEET(Type.PART, false),
        ROD(Type.PART, false),
        TUYERE(Type.TOOL, metal -> new TieredItem(metal.toolTier(), properties())),
        FISH_HOOK(Type.TOOL, false),
        FISHING_ROD(Type.TOOL, metal -> new TFCFishingRodItem(properties().defaultDurability(metal.toolTier().getUses()), metal.toolTier())),

        // Tools and Tool Heads
        PICKAXE(Type.TOOL, metal -> new PickaxeItem(metal.toolTier(), (int) ToolItem.calculateVanillaAttackDamage(0.75f, metal.toolTier()), -2.8F, properties())),
        PICKAXE_HEAD(Type.TOOL, true),
        PROPICK(Type.TOOL, metal -> new PropickItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.5f, metal.toolTier()), -2.8F, properties())),
        PROPICK_HEAD(Type.TOOL, true),
        AXE(Type.TOOL, metal -> new AxeItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(1.5f, metal.toolTier()), -3.1F, properties())),
        AXE_HEAD(Type.TOOL, true),
        SHOVEL(Type.TOOL, metal -> new ShovelItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.875F, metal.toolTier()), -3.0F, properties())),
        SHOVEL_HEAD(Type.TOOL, true),
        HOE(Type.TOOL, metal -> new TFCHoeItem(metal.toolTier(), -1, -2f, properties())),
        HOE_HEAD(Type.TOOL, true),
        CHISEL(Type.TOOL, metal -> new ChiselItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.27f, metal.toolTier()), -1.5F, properties())),
        CHISEL_HEAD(Type.TOOL, true),
        HAMMER(Type.TOOL, metal -> new ToolItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(1f, metal.toolTier()), -3, TFCTags.Blocks.MINEABLE_WITH_HAMMER, properties())),
        HAMMER_HEAD(Type.TOOL, true),
        SAW(Type.TOOL, metal -> new AxeItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.5f, metal.toolTier()), -3, properties())),
        SAW_BLADE(Type.TOOL, true),
        JAVELIN(Type.TOOL, metal -> new JavelinItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(1f, metal.toolTier()), -2.2F, properties(), metal.getSerializedName())),
        JAVELIN_HEAD(Type.TOOL, true),
        SWORD(Type.TOOL, metal -> new SwordItem(metal.toolTier(), (int) ToolItem.calculateVanillaAttackDamage(1f, metal.toolTier()), -2.4F, properties())),
        SWORD_BLADE(Type.TOOL, true),
        MACE(Type.TOOL, metal -> new MaceItem(metal.toolTier(), (int) ToolItem.calculateVanillaAttackDamage(1.3f, metal.toolTier()), -3, properties())),
        MACE_HEAD(Type.TOOL, true),
        KNIFE(Type.TOOL, metal -> new ToolItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.6f, metal.toolTier()), -2.0F, TFCTags.Blocks.MINEABLE_WITH_KNIFE, properties())),
        KNIFE_BLADE(Type.TOOL, true),
        SCYTHE(Type.TOOL, metal -> new ScytheItem(metal.toolTier(), ToolItem.calculateVanillaAttackDamage(0.7f, metal.toolTier()), -3.2F, TFCTags.Blocks.MINEABLE_WITH_SCYTHE, properties())),
        SCYTHE_BLADE(Type.TOOL, true),
        SHEARS(Type.TOOL, metal -> new ShearsItem(properties().defaultDurability(metal.toolTier().getUses()))),

        // Armor
        UNFINISHED_HELMET(Type.ARMOR, false),
        HELMET(Type.ARMOR, metal -> new ArmorItem(metal.armorTier(), EquipmentSlot.HEAD, properties())),
        UNFINISHED_CHESTPLATE(Type.ARMOR, false),
        CHESTPLATE(Type.ARMOR, metal -> new ArmorItem(metal.armorTier(), EquipmentSlot.CHEST, properties())),
        UNFINISHED_GREAVES(Type.ARMOR, false),
        GREAVES(Type.ARMOR, metal -> new ArmorItem(metal.armorTier(), EquipmentSlot.LEGS, properties())),
        UNFINISHED_BOOTS(Type.ARMOR, false),
        BOOTS(Type.ARMOR, metal -> new ArmorItem(metal.armorTier(), EquipmentSlot.FEET, properties())),

        SHIELD(Type.TOOL, metal -> new TFCShieldItem(metal.toolTier(), properties()));

        public static Item.Properties properties()
        {
            return new Item.Properties().tab(TFCItemGroup.METAL);
        }

        private final Function<RegistryMetal, Item> itemFactory;
        private final Type type;
        private final boolean mold;

        ItemType(Type type, boolean mold)
        {
            this(type, mold, metal -> new Item(properties()));
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

    public static class Packet extends DataManagerSyncPacket<Metal> {}
}