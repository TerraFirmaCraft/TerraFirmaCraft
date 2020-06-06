/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.TFCItemGroup;
import net.dries007.tfc.objects.TFCItemTier;
import net.dries007.tfc.objects.items.tools.*;

public class Metal
{
    private final Tier tier;
    private final Fluid fluid;

    private final ResourceLocation id;

    public Metal(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.tier = Tier.valueOf(JSONUtils.getInt(json, "tier"));
        this.fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(JSONUtils.getString(json, "fluid")));
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public Tier getTier()
    {
        return tier;
    }

    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("metal." + id.getNamespace() + "." + id.getPath());
    }

    /**
     * Default metals that are used for block registration calls.
     * Not extensible.
     *
     * @see Metal instead and register via json
     */
    public enum Default
    {
        BISMUTH(0xFF486B72, true, null, false, false),
        BISMUTH_BRONZE(0xFF418E4F, true, TFCItemTier.BISMUTH_BRONZE, true, true),
        BLACK_BRONZE(0xFF3B2636, true, TFCItemTier.BLACK_BRONZE, true, true),
        BRONZE(0xFF96892E, true, TFCItemTier.BRONZE, true, true),
        BRASS(0xFF7C5E33, true, null, false, false),
        COPPER(0xFFB64027, true, TFCItemTier.COPPER, true, true),
        GOLD(0xFFDCBF1B, true, null, false, false),
        NICKEL(0xFF4E4E3C, true, null, false, false),
        ROSE_GOLD(0xFFEB7137, true, null, false, false),
        SILVER(0xFF949495, true, null, false, false),
        TIN(0xFF90A4BB, true, null, false, false),
        ZINC(0xFFBBB9C4, true, null, false, false),
        STERLING_SILVER(0xFFAC927B, true, null, false, false),
        WROUGHT_IRON(0xFF989897, true, TFCItemTier.WROUGHT_IRON, true, true),
        CAST_IRON(0xFF989897, false, null, false, false), // todo color
        PIG_IRON(0xFF6A595C, false, null, false, false),
        STEEL(0xFF5F5F5F, true, TFCItemTier.STEEL, true, true),
        BLACK_STEEL(0xFF111111, true, TFCItemTier.BLACK_STEEL, true, true),
        BLUE_STEEL(0xFF2D5596, true, TFCItemTier.BLUE_STEEL, true, true),
        RED_STEEL(0xFF700503, true, TFCItemTier.RED_STEEL, true, true),
        WEAK_STEEL(0xFF111111, false, null, false, false),
        WEAK_BLUE_STEEL(0xFF2D5596, false, null, false, false),
        WEAK_RED_STEEL(0xFF700503, false, null, false, false),
        HIGH_CARBON_STEEL(0xFF5F5F5F, false, null, false, false),
        HIGH_CARBON_BLACK_STEEL(0xFF111111, false, null, false, false),
        HIGH_CARBON_BLUE_STEEL(0xFF2D5596, false, null, false, false),
        HIGH_CARBON_RED_STEEL(0xFF700503, false, null, false, false),
        UNKNOWN(0xFF2F2B27, false, null, false, false);

        private final boolean parts, armor, utility;
        private final IItemTier itemTier;
        private final int color;

        Default(int color, boolean parts, @Nullable IItemTier itemTier, boolean armor, boolean utility)
        {
            this.parts = parts;
            this.itemTier = itemTier;
            this.armor = armor;
            this.utility = utility;
            this.color = color;
        }

        public int getColor()
        {
            return color;
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
            return itemTier != null;
        }

        public boolean hasUtilities()
        {
            return utility;
        }

        public IItemTier getItemTier()
        {
            //noinspection ConstantConditions - Should never be called without first checking hasTools()
            return itemTier;
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

        @Nonnull
        public static Tier valueOf(int tier)
        {
            return tier < 0 || tier > VALUES.length ? TIER_I : VALUES[tier];
        }

        @Nonnull
        public Tier next()
        {
            return this == TIER_VI ? TIER_VI : VALUES[this.ordinal() + 1];
        }

        @Nonnull
        public Tier previous()
        {
            return this == TIER_0 ? TIER_0 : VALUES[this.ordinal() - 1];
        }

        public boolean isAtLeast(@Nonnull Tier requiredInclusive)
        {
            return this.ordinal() >= requiredInclusive.ordinal();
        }

        public boolean isAtMost(@Nonnull Tier requiredInclusive)
        {
            return this.ordinal() <= requiredInclusive.ordinal();
        }

        public ITextComponent getDisplayName()
        {
            return new TranslationTextComponent(TerraFirmaCraft.MOD_ID + ".enum.tier." + this.name().toLowerCase());
        }
    }

    public enum BlockType
    {
        ANVIL(Type.UTILITY, metal -> new Block(Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(4, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        LAMP(Type.UTILITY, metal -> new Block(Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(4, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)));

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

        public Block create(Metal.Default metal)
        {
            return blockFactory.apply(metal);
        }

        public boolean hasType(Default metal)
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
        INGOT("ingots", Type.DEFAULT, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        NUGGET("nuggets", Type.DEFAULT, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        DUST("dusts", Type.DEFAULT, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),

        DOUBLE_INGOT("double_ingots", Type.PART, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SHEET("sheets", Type.PART, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        DOUBLE_SHEET("double_sheets", Type.PART, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        ROD("rods", Type.PART, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),

        TUYERE(Type.TOOL, metal -> new TieredItem(metal.getItemTier(), new Item.Properties().group(TFCItemGroup.METAL))),
        PICKAXE(Type.TOOL, metal -> new TFCPickaxeItem(metal.getItemTier(), 0.75F, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS))),
        PICKAXE_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        PROPICK(Type.TOOL, metal -> new PropickItem(metal.getItemTier(), 0.5F, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS))),
        PROPICK_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        AXE(Type.TOOL, metal -> new TFCAxeItem(metal.getItemTier(), 1.5F, -3.2F, (new Item.Properties()).group(ItemGroup.TOOLS))),
        AXE_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SHOVEL(Type.TOOL, metal -> new TFCShovelItem(metal.getItemTier(), 0.875F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS))),
        SHOVEL_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        HOE(Type.TOOL, metal -> new HoeItem(metal.getItemTier(), -1, (new Item.Properties()).group(ItemGroup.TOOLS))),
        HOE_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        CHISEL(Type.TOOL, metal -> new ChiselItem(metal.getItemTier(), 0.27F, -1.5F, (new Item.Properties()).group(ItemGroup.TOOLS))),
        CHISEL_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        HAMMER(Type.TOOL, metal -> new TFCToolItem(metal.getItemTier(), 1.0F, -3, (new Item.Properties()).group(ItemGroup.TOOLS))),
        HAMMER_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SAW(Type.TOOL, metal -> new TFCToolItem(metal.getItemTier(), 0.5F, -3, (new Item.Properties()).group(ItemGroup.TOOLS))),
        SAW_BLADE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        JAVELIN(Type.TOOL, metal -> new JavelinItem(metal.getItemTier(), 0.7F, -1.8F, (new Item.Properties()).group(ItemGroup.COMBAT))),
        JAVELIN_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SWORD(Type.TOOL, metal -> new TFCSwordItem(metal.getItemTier(), 1.0F, -2.4F, (new Item.Properties()).group(ItemGroup.COMBAT))),
        SWORD_BLADE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        MACE(Type.TOOL, metal -> new WeaponItem(metal.getItemTier(), 1.3F, -3, (new Item.Properties()).group(ItemGroup.COMBAT))),
        MACE_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        KNIFE(Type.TOOL, metal -> new TFCToolItem(metal.getItemTier(), 0.54F, -1.5F, (new Item.Properties()).group(ItemGroup.TOOLS))),
        KNIFE_BLADE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SCYTHE(Type.TOOL, metal -> new TFCToolItem(metal.getItemTier(), 2, -3.2F, (new Item.Properties()).group(ItemGroup.TOOLS))),
        SCYTHE_BLADE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SHEARS(Type.TOOL, metal -> new TFCShearsItem(metal.getItemTier(), (new Item.Properties()).group(ItemGroup.TOOLS))),

        UNFINISHED_HELMET(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        HELMET(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        UNFINISHED_CHESTPLATE(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        CHESTPLATE(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        UNFINISHED_GREAVES(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        GREAVES(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        UNFINISHED_BOOTS(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        BOOTS(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),

        SHIELD(Type.TOOL, metal -> new TFCShieldItem(metal.getItemTier(), new Item.Properties().group(TFCItemGroup.COMBAT)));


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

        public boolean hasType(Default metal)
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

        /*
        public boolean hasType(Metal metal)
        {
            if (!metal.usable)
            {
                return this == ItemType.INGOT;
            }
            return !this.isToolItem() || metal.getToolMetal() != null;
        }
        /**
         * Used to find out if the type has a mold
         *
         * @param metal Null, if checking across all types. If present, checks if the metal is compatible with the mold type
         * @return if the type + metal combo have a valid mold
         */
        /*
        public boolean hasMold(@Nullable Metal metal)
        {
            if (metal == null)
            {
                // Query for should the mold exist during registration
                return hasMold;
            }
            if (this == ItemType.INGOT)
            {
                // All ingots are able to be cast in molds
                return true;
            }
            if (hasMold)
            {
                // All tool metals can be used in tool molds with tier at most II
                return metal.isToolMetal() && metal.getTier().isAtMost(Tier.TIER_II);
            }
            return false;
        }
        /**
         * Does this item type require a tool metal to be made
         *
         * @return true if this must be made from a tool item type
         */
        /*
        public boolean isToolItem()
        {
            return toolItem;
        }
        public int getArmorSlot()
        {
            return armorSlot;
        }
        public boolean isArmor() { return armorSlot != -1; }
        /**
         * What armor slot this ItemArmor should use? If this is not armor, return the MainHand slot
         *
         * @return which slot this item should be equipped.
         */
        /*
        public EntityEquipmentSlot getEquipmentSlot()
        {
            switch (armorSlot)
            {
                case 0:
                    return EntityEquipmentSlot.HEAD;
                case 1:
                    return EntityEquipmentSlot.CHEST;
                case 2:
                    return EntityEquipmentSlot.LEGS;
                case 3:
                    return EntityEquipmentSlot.FEET;
                default:
                    return EntityEquipmentSlot.MAINHAND;
            }
        }
        public int getSmeltAmount()
        {
            return smeltAmount;
        }
        public String[] getPattern()
        {
            return pattern;
        }
        */
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
