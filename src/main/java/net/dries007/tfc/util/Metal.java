/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCArmorMaterial;
import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.TFCTiers;
import net.dries007.tfc.common.items.*;

public class Metal
{
    public static final ResourceLocation UNKNOWN_ID = Helpers.identifier("unknown");
    public static final MetalManager MANAGER = new MetalManager();

    public static Metal unknown()
    {
        return Objects.requireNonNull(MANAGER.get(UNKNOWN_ID));
    }

    private final Tier tier;
    private final Fluid fluid;
    private final float meltTemperature;
    private final float heatCapacity;

    private final ResourceLocation id;
    private final String translationKey;

    public Metal(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.tier = Tier.valueOf(GsonHelper.getAsInt(json, "tier"));
        this.fluid = JsonHelpers.getRegistryEntry(json, "fluid", ForgeRegistries.FLUIDS);
        this.meltTemperature = JsonHelpers.getAsFloat(json, "melt_temperature");
        this.heatCapacity = JsonHelpers.getAsFloat(json, "heat_capacity");
        this.translationKey = "metal." + id.getNamespace() + "." + id.getPath();
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public Tier getTier()
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

    public float getHeatCapacity()
    {
        return heatCapacity;
    }

    public MutableComponent getDisplayName()
    {
        return new TranslatableComponent(translationKey);
    }

    public String getTranslationKey()
    {
        return translationKey;
    }

    /**
     * Default metals that are used for block registration calls.
     * Not extensible.
     *
     * @see Metal instead and register via json
     */
    public enum Default implements StringRepresentable
    {
        BISMUTH(0xFF486B72, Rarity.COMMON, true, false, false),
        BISMUTH_BRONZE(0xFF418E4F, Rarity.COMMON, TFCTiers.BISMUTH_BRONZE, TFCArmorMaterial.BISMUTH_BRONZE, true, true, true),
        BLACK_BRONZE(0xFF3B2636, Rarity.COMMON, TFCTiers.BLACK_BRONZE, TFCArmorMaterial.BLACK_BRONZE, true, true, true),
        BRONZE(0xFF96892E, Rarity.COMMON, TFCTiers.BRONZE, TFCArmorMaterial.BRONZE, true, true, true),
        BRASS(0xFF7C5E33, Rarity.COMMON, true, false, false),
        COPPER(0xFFB64027, Rarity.COMMON, TFCTiers.COPPER, TFCArmorMaterial.COPPER, true, true, true),
        GOLD(0xFFDCBF1B, Rarity.COMMON, true, false, false),
        NICKEL(0xFF4E4E3C, Rarity.COMMON, true, false, false),
        ROSE_GOLD(0xFFEB7137, Rarity.COMMON, true, false, false),
        SILVER(0xFF949495, Rarity.COMMON, true, false, false),
        TIN(0xFF90A4BB, Rarity.COMMON, true, false, false),
        ZINC(0xFFBBB9C4, Rarity.COMMON, true, false, false),
        STERLING_SILVER(0xFFAC927B, Rarity.COMMON, true, false, false),
        WROUGHT_IRON(0xFF989897, Rarity.COMMON, TFCTiers.WROUGHT_IRON, TFCArmorMaterial.WROUGHT_IRON, true, true, true),
        CAST_IRON(0xFF989897, Rarity.COMMON, true, false, false),
        PIG_IRON(0xFF6A595C, Rarity.COMMON, false, false, false),
        STEEL(0xFF5F5F5F, Rarity.UNCOMMON, TFCTiers.STEEL, TFCArmorMaterial.STEEL, true, true, true),
        BLACK_STEEL(0xFF111111, Rarity.RARE, TFCTiers.BLACK_STEEL, TFCArmorMaterial.BLACK_STEEL, true, true, true),
        BLUE_STEEL(0xFF2D5596, Rarity.EPIC, TFCTiers.BLUE_STEEL, TFCArmorMaterial.BLUE_STEEL, true, true, true),
        RED_STEEL(0xFF700503, Rarity.EPIC, TFCTiers.RED_STEEL, TFCArmorMaterial.RED_STEEL, true, true, true),
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
        @Nullable private final net.minecraft.world.item.Tier tier;
        @Nullable private final ArmorMaterial armorTier;
        private final Rarity rarity;
        private final int color;

        Default(int color, Rarity rarity, boolean parts, boolean armor, boolean utility)
        {
            this(color, rarity, null, null, parts, armor, utility);
        }

        Default(int color, Rarity rarity, @Nullable net.minecraft.world.item.Tier tier, @Nullable ArmorMaterial armorTier, boolean parts, boolean armor, boolean utility)
        {
            this.serializedName = name().toLowerCase(Locale.ROOT);
            this.tier = tier;
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
            return tier != null;
        }

        public boolean hasUtilities()
        {
            return utility;
        }

        public net.minecraft.world.item.Tier getTier()
        {
            return Objects.requireNonNull(tier, "Tried to get non-existent tier from " + name());
        }

        public ArmorMaterial getArmorTier()
        {
            return Objects.requireNonNull(armorTier, "Tried to get non-existent armor tier from " + name());
        }
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
     *
     * Devices:
     * T0 - Rock Anvil
     * T1 - Pit Kiln / Fire pit
     * T2 - Forge
     * T3 - Bloomery
     * T4 - Blast Furnace / Crucible
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

        public boolean isAtLeast(Tier requiredInclusive)
        {
            return this.ordinal() >= requiredInclusive.ordinal();
        }

        public boolean isAtMost(Tier requiredInclusive)
        {
            return this.ordinal() <= requiredInclusive.ordinal();
        }

        public Component getDisplayName()
        {
            return new TranslatableComponent(translationKey);
        }
    }

    public enum BlockType
    {
        ANVIL(Type.UTILITY, metal -> new Block(Block.Properties.of(Material.METAL).sound(SoundType.METAL).strength(4, 10).requiresCorrectToolForDrops())),
        LAMP(Type.UTILITY, metal -> new Block(Block.Properties.of(Material.METAL).sound(SoundType.METAL).strength(4, 10)));

        public static final Metal.BlockType[] VALUES = values();

        public static Metal.BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : ANVIL;
        }

        private final NonNullFunction<Metal.Default, Block> blockFactory;
        private final Type type;
        @Nullable private final String tag;

        BlockType(@Nullable String tag, Type type, NonNullFunction<Metal.Default, Block> blockFactory)
        {
            this.type = type;
            this.blockFactory = blockFactory;
            this.tag = tag;
        }

        BlockType(Type type, NonNullFunction<Metal.Default, Block> blockFactory)
        {
            this(null, type, blockFactory);
        }

        public Supplier<Block> create(Metal.Default metal)
        {
            return () -> blockFactory.apply(metal);
        }

        public boolean hasMetal(Default metal)
        {
            return type.hasType(metal);
        }

        @Nullable
        public String getTag()
        {
            return tag;
        }
    }

    public enum ItemType
    {
        // Generic
        INGOT(Type.DEFAULT, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        DOUBLE_INGOT(Type.PART, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        SHEET(Type.PART, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        DOUBLE_SHEET(Type.PART, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        ROD(Type.PART, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        TUYERE(Type.TOOL, metal -> new TieredItem(metal.getTier(), new Item.Properties().tab(TFCItemGroup.METAL))),
        FISH_HOOK(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        FISHING_ROD(Type.TOOL, metal -> new TFCFishingRodItem(new Item.Properties().tab(TFCItemGroup.METAL).defaultDurability(metal.getTier().getUses()))),

        // Tools and Tool Heads
        PICKAXE(Type.TOOL, metal -> new PickaxeItem(metal.getTier(), (int) ToolItem.calculateVanillaAttackDamage(0.75F, metal.getTier()), -2.8F, new Item.Properties().tab(TFCItemGroup.METAL))),
        PICKAXE_HEAD(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        PROPICK(Type.TOOL, metal -> new PropickItem(metal.getTier(), 0.5F, -2.8F, new Item.Properties().tab(TFCItemGroup.METAL))),
        PROPICK_HEAD(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        AXE(Type.TOOL, metal -> new AxeItem(metal.getTier(), ToolItem.calculateVanillaAttackDamage(1.5F, metal.getTier()), -3.2F, new Item.Properties().tab(TFCItemGroup.METAL))),
        AXE_HEAD(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        SHOVEL(Type.TOOL, metal -> new ShovelItem(metal.getTier(), ToolItem.calculateVanillaAttackDamage(0.875F, metal.getTier()), -3.0F, new Item.Properties().tab(TFCItemGroup.METAL))),
        SHOVEL_HEAD(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        HOE(Type.TOOL, metal -> new HoeItem(metal.getTier(), -1, -2f, new Item.Properties().tab(TFCItemGroup.METAL))),
        HOE_HEAD(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        CHISEL(Type.TOOL, metal -> new ChiselItem(metal.getTier(), 0.27F, -1.5F, new Item.Properties().tab(TFCItemGroup.METAL))),
        CHISEL_HEAD(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        HAMMER(Type.TOOL, metal -> new ToolItem(metal.getTier(), 1.0F, -3, TFCTags.Blocks.MINEABLE_WITH_HAMMER, new Item.Properties().tab(TFCItemGroup.METAL))),
        HAMMER_HEAD(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        SAW(Type.TOOL, metal -> new AxeItem(metal.getTier(), ToolItem.calculateVanillaAttackDamage(0.5F, metal.getTier()), -3, new Item.Properties().tab(TFCItemGroup.METAL))),
        SAW_BLADE(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        JAVELIN(Type.TOOL, metal -> new JavelinItem(metal.getTier(), 0.7F, -1.8F, new Item.Properties().tab(TFCItemGroup.METAL))),
        JAVELIN_HEAD(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        SWORD(Type.TOOL, metal -> new SwordItem(metal.getTier(), (int) ToolItem.calculateVanillaAttackDamage(1.0F, metal.getTier()), -2.4F, new Item.Properties().tab(TFCItemGroup.METAL))),
        SWORD_BLADE(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        MACE(Type.TOOL, metal -> new SwordItem(metal.getTier(), (int) ToolItem.calculateVanillaAttackDamage(1.3F, metal.getTier()), -3, new Item.Properties().tab(TFCItemGroup.METAL))),
        MACE_HEAD(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        KNIFE(Type.TOOL, metal -> new ToolItem(metal.getTier(), 0.54F, -1.5F, TFCTags.Blocks.MINEABLE_WITH_KNIFE, new Item.Properties().tab(TFCItemGroup.METAL))),
        KNIFE_BLADE(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        SCYTHE(Type.TOOL, metal -> new ToolItem(metal.getTier(), ToolItem.calculateVanillaAttackDamage(2, metal.getTier()), -3.2F, TFCTags.Blocks.MINEABLE_WITH_SCYTHE, new Item.Properties().tab(TFCItemGroup.METAL))),
        SCYTHE_BLADE(Type.TOOL, true, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        SHEARS(Type.TOOL, metal -> new ShearsItem(new Item.Properties().tab(TFCItemGroup.METAL).defaultDurability(metal.getTier().getUses()))),

        // Armor
        UNFINISHED_HELMET(Type.ARMOR, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        HELMET(Type.ARMOR, metal -> new ArmorItem(metal.getArmorTier(), EquipmentSlot.HEAD, new Item.Properties().tab(TFCItemGroup.METAL))),
        UNFINISHED_CHESTPLATE(Type.ARMOR, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        CHESTPLATE(Type.ARMOR, metal -> new ArmorItem(metal.getArmorTier(), EquipmentSlot.CHEST, new Item.Properties().tab(TFCItemGroup.METAL))),
        UNFINISHED_GREAVES(Type.ARMOR, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        GREAVES(Type.ARMOR, metal -> new ArmorItem(metal.getArmorTier(), EquipmentSlot.LEGS, new Item.Properties().tab(TFCItemGroup.METAL))),
        UNFINISHED_BOOTS(Type.ARMOR, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        BOOTS(Type.ARMOR, metal -> new ArmorItem(metal.getArmorTier(), EquipmentSlot.FEET, new Item.Properties().tab(TFCItemGroup.METAL))),

        SHIELD(Type.TOOL, metal -> new TFCShieldItem(metal.getTier(), new Item.Properties().tab(TFCItemGroup.TAB_COMBAT)));

        public static final Metal.ItemType[] VALUES = values();

        public static Metal.ItemType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : INGOT;
        }

        private final NonNullFunction<Metal.Default, Item> itemFactory;
        private final Type type;
        private final boolean mold;

        ItemType(Type type, NonNullFunction<Metal.Default, Item> itemFactory)
        {
            this(type, false, itemFactory);
        }

        ItemType(Type type, boolean mold, NonNullFunction<Metal.Default, Item> itemFactory)
        {
            this.type = type;
            this.mold = mold;
            this.itemFactory = itemFactory;
        }

        public Item create(Metal.Default metal)
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