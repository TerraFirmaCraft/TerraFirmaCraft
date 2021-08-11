/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCArmorMaterial;
import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.TFCItemTier;
import net.dries007.tfc.common.items.tools.*;
import net.dries007.tfc.util.Helpers;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;

public class Metal
{
    private final Tier tier;
    private final Fluid fluid;

    private final ResourceLocation id;

    public Metal(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.tier = Tier.valueOf(GsonHelper.getAsInt(json, "tier"));
        this.fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(json, "fluid")));
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

    public Component getDisplayName()
    {
        return new TranslatableComponent("metal." + id.getNamespace() + "." + id.getPath());
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
        BISMUTH_BRONZE(0xFF418E4F, Rarity.COMMON, TFCItemTier.BISMUTH_BRONZE, TFCArmorMaterial.BISMUTH_BRONZE, true, true, true),
        BLACK_BRONZE(0xFF3B2636, Rarity.COMMON, TFCItemTier.BLACK_BRONZE, TFCArmorMaterial.BLACK_BRONZE, true, true, true),
        BRONZE(0xFF96892E, Rarity.COMMON, TFCItemTier.BRONZE, TFCArmorMaterial.BRONZE, true, true, true),
        BRASS(0xFF7C5E33, Rarity.COMMON, true, false, false),
        COPPER(0xFFB64027, Rarity.COMMON, TFCItemTier.COPPER, TFCArmorMaterial.COPPER, true, true, true),
        GOLD(0xFFDCBF1B, Rarity.COMMON, true, false, false),
        NICKEL(0xFF4E4E3C, Rarity.COMMON, true, false, false),
        ROSE_GOLD(0xFFEB7137, Rarity.COMMON, true, false, false),
        SILVER(0xFF949495, Rarity.COMMON, true, false, false),
        TIN(0xFF90A4BB, Rarity.COMMON, true, false, false),
        ZINC(0xFFBBB9C4, Rarity.COMMON, true, false, false),
        STERLING_SILVER(0xFFAC927B, Rarity.COMMON, true, false, false),
        WROUGHT_IRON(0xFF989897, Rarity.COMMON, TFCItemTier.WROUGHT_IRON, TFCArmorMaterial.WROUGHT_IRON, true, true, true),
        CAST_IRON(0xFF989897, Rarity.COMMON, true, false, false),
        PIG_IRON(0xFF6A595C, Rarity.COMMON, false, false, false),
        STEEL(0xFF5F5F5F, Rarity.UNCOMMON, TFCItemTier.STEEL, TFCArmorMaterial.STEEL, true, true, true),
        BLACK_STEEL(0xFF111111, Rarity.RARE, TFCItemTier.BLACK_STEEL, TFCArmorMaterial.BLACK_STEEL, true, true, true),
        BLUE_STEEL(0xFF2D5596, Rarity.EPIC, TFCItemTier.BLUE_STEEL, TFCArmorMaterial.BLUE_STEEL, true, true, true),
        RED_STEEL(0xFF700503, Rarity.EPIC, TFCItemTier.RED_STEEL, TFCArmorMaterial.RED_STEEL, true, true, true),
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
        private final Tier tier;
        private final ArmorMaterial armorTier;
        private final Rarity rarity;
        private final int color;

        Default(int color, Rarity rarity, boolean parts, boolean armor, boolean utility)
        {
            this(color, rarity, null, null, parts, armor, utility);
        }

        Default(int color, Rarity rarity, @Nullable Tier tier, @Nullable ArmorMaterial armorTier, boolean parts, boolean armor, boolean utility)
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

        public Tier getTier()
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
            translationKey = Helpers.getEnumTranslationKey(this, "tier");
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
        ANVIL(Type.UTILITY, metal -> new Block(Block.Properties.of(Material.METAL).sound(SoundType.METAL).strength(4, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        LAMP(Type.UTILITY, metal -> new Block(Block.Properties.of(Material.METAL).sound(SoundType.METAL).strength(4, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)));

        public static final Metal.BlockType[] VALUES = values();

        public static Metal.BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : ANVIL;
        }

        private final NonNullFunction<Metal.Default, Block> blockFactory;
        private final Type type;
        private final String tag;

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
        INGOT("ingots", Type.DEFAULT, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        DOUBLE_INGOT("double_ingots", Type.PART, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        SHEET("sheets", Type.PART, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        DOUBLE_SHEET("double_sheets", Type.PART, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        ROD("rods", Type.PART, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        TUYERE(Type.TOOL, metal -> new TieredItem(metal.getTier(), new Item.Properties().tab(TFCItemGroup.METAL))),

        // Tools and Tool Heads
        PICKAXE(Type.TOOL, metal -> new TFCPickaxeItem(metal.getTier(), 0.75F, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))),
        PICKAXE_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        PROPICK(Type.TOOL, metal -> new PropickItem(metal.getTier(), 0.5F, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))),
        PROPICK_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        AXE(Type.TOOL, metal -> new TFCAxeItem(metal.getTier(), 1.5F, -3.2F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).addToolType(ToolType.AXE, metal.getTier().getLevel()))),
        AXE_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        SHOVEL(Type.TOOL, metal -> new TFCShovelItem(metal.getTier(), 0.875F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).addToolType(ToolType.SHOVEL, metal.getTier().getLevel()))),
        SHOVEL_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        HOE(Type.TOOL, metal -> new HoeItem(metal.getTier(), -1, -2f, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).addToolType(ToolType.HOE, metal.getTier().getLevel()))),
        HOE_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        CHISEL(Type.TOOL, metal -> new ChiselItem(metal.getTier(), 0.27F, -1.5F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).addToolType(TFCItemTier.CHISEL, metal.getTier().getLevel()))),
        CHISEL_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        HAMMER(Type.TOOL, metal -> new TFCToolItem(metal.getTier(), 1.0F, -3, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))),
        HAMMER_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL).addToolType(TFCItemTier.HAMMER, metal.getTier().getLevel()))),
        SAW(Type.TOOL, metal -> new TFCToolItem(metal.getTier(), 0.5F, -3, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))),
        SAW_BLADE(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL).addToolType(ToolType.AXE, 0))),
        JAVELIN(Type.TOOL, metal -> new JavelinItem(metal.getTier(), 0.7F, -1.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT))),
        JAVELIN_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        SWORD(Type.TOOL, metal -> new TFCSwordItem(metal.getTier(), 1.0F, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT))),
        SWORD_BLADE(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        MACE(Type.TOOL, metal -> new WeaponItem(metal.getTier(), 1.3F, -3, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT))),
        MACE_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        KNIFE(Type.TOOL, metal -> new TFCToolItem(metal.getTier(), 0.54F, -1.5F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))),
        KNIFE_BLADE(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL).addToolType(TFCItemTier.KNIFE, metal.getTier().getLevel()))),
        SCYTHE(Type.TOOL, metal -> new TFCToolItem(metal.getTier(), 2, -3.2F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).addToolType(TFCItemTier.KNIFE, 0))),
        SCYTHE_BLADE(Type.TOOL, metal -> new Item(new Item.Properties().tab(TFCItemGroup.METAL))),
        SHEARS(Type.TOOL, metal -> new TFCShearsItem(metal.getTier(), (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS))),

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
        private final String tag;

        ItemType(@Nullable String tag, Type type, NonNullFunction<Metal.Default, Item> itemFactory)
        {
            this.type = type;
            this.tag = tag;
            this.itemFactory = itemFactory;
        }

        ItemType(Type type, NonNullFunction<Metal.Default, Item> itemFactory)
        {
            this(null, type, itemFactory);
        }

        public Item create(Metal.Default metal)
        {
            return itemFactory.apply(metal);
        }

        public boolean hasMetal(Default metal)
        {
            return type.hasType(metal);
        }

        public String getTag()
        {
            return hasTag() ? tag : "";
        }

        public boolean hasTag()
        {
            return tag != null;
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