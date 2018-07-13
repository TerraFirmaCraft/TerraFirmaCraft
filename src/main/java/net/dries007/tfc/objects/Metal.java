/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import java.util.function.BiFunction;

import net.minecraft.item.Item;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.items.metal.*;

import static net.dries007.tfc.objects.Metal.Tier.*;

@MethodsReturnNonnullByDefault
public enum Metal
{
    BISMUTH(TIER_I, 0.14, 270),
    BISMUTH_BRONZE(TIER_I, 0.35, 985, ToolMaterialsTFC.BISMUTH_BRONZE),
    BLACK_BRONZE(TIER_I, 0.35, 1070, ToolMaterialsTFC.BLACK_BRONZE),
    BRASS(TIER_I, 0.35, 930),
    BRONZE(TIER_I, 0.35, 950, ToolMaterialsTFC.BRONZE),
    COPPER(TIER_I, 0.35, 1080, ToolMaterialsTFC.COPPER),
    GOLD(TIER_I, 0.6, 1060),
    LEAD(TIER_I, 0.22, 328),
    NICKEL(TIER_I, 0.48, 1453),
    ROSE_GOLD(TIER_I, 0.35, 960),
    SILVER(TIER_I, 0.48, 961),
    TIN(TIER_I, 0.14, 230),
    ZINC(TIER_I, 0.21, 420),
    STERLING_SILVER(TIER_I, 0.35, 900),

    WROUGHT_IRON(TIER_III, 0.35, 1535, ToolMaterialsTFC.IRON),
    PIG_IRON(TIER_IV, 0.35, 1535), // bloom: 0.35, 1500

    STEEL(TIER_IV, 0.35, 1540, ToolMaterialsTFC.STEEL),

    PLATINUM(TIER_V, 0.35, 1730),

    BLACK_STEEL(TIER_V, 0.35, 1485, ToolMaterialsTFC.BLACK_STEEL),
    BLUE_STEEL(TIER_V, 0.35, 1540, ToolMaterialsTFC.BLUE_STEEL),
    RED_STEEL(TIER_V, 0.35, 1540, ToolMaterialsTFC.RED_STEEL),

    WEAK_STEEL(false, TIER_V, 0.35, 1540),
    WEAK_BLUE_STEEL(false, TIER_V, 0.35, 1540),
    WEAK_RED_STEEL(false, TIER_V, 0.35, 1540),

    HIGH_CARBON_STEEL(false, TIER_V, 0.35, 1540),
    HIGH_CARBON_BLUE_STEEL(false, TIER_V, 0.35, 1540),
    HIGH_CARBON_RED_STEEL(false, TIER_V, 0.35, 1540),
    HIGH_CARBON_BLACK_STEEL(false, TIER_V, 0.35, 1540),

    UNKNOWN(false, TIER_I, 0.5, 1250);

    public final boolean usable;
    public final Tier tier;
    public final double specificHeat;
    public final int meltTemp;
    public final Item.ToolMaterial toolMetal;

    Metal(Tier tier, double sh, int melt)
    {
        this(true, tier, sh, melt, null);
    }

    Metal(Tier tier, double sh, int melt, Item.ToolMaterial toolMetal)
    {
        this(true, tier, sh, melt, toolMetal);
    }

    Metal(boolean usable, Tier tier, double sh, int melt)
    {
        this(usable, tier, sh, melt, null);
    }

    Metal(boolean usable, Tier tier, double sh, int melt, Item.ToolMaterial toolMetal)
    {
        this.usable = usable;
        this.tier = tier;
        this.specificHeat = sh;
        this.meltTemp = melt;
        this.toolMetal = toolMetal;
    }

    public boolean hasType(ItemType type)
    {
        if (!usable) return type == ItemType.INGOT || type == ItemType.UNSHAPED;
        return !type.toolItem || this.toolMetal != null;
    }

    public enum Tier
    {
        TIER_I,
        TIER_II, // Not implemented, but presumed to be a more advanced, more capable version of the pit kiln.
        TIER_III,
        TIER_IV,
        TIER_V
    }

    public enum ItemType
    {
        UNSHAPED(false, 100, null), // Special case, because it's a pottery item
        INGOT(false, 100, ItemIngot::new),
        DOUBLE_INGOT(false, 200),
        SCRAP(false, 100),
        DUST(false, 100),
        NUGGET(false, 10),
        SHEET(false, 200, ItemSheet::new),
        DOUBLE_SHEET(false, 400),
        LAMP(false, 100, ItemLamp::new),

        ANVIL(true, 1400, ItemAnvil::new),
        TUYERE(true, 400),

        PICK(true, 100, ItemMetalTool::new),
        PICK_HEAD(true, 100, true),
        SHOVEL(true, 100, ItemMetalTool::new),
        SHOVEL_HEAD(true, 100, true),
        AXE(true, 100, ItemMetalTool::new),
        AXE_HEAD(true, 100, true),
        HOE(true, 100, ItemMetalTool::new),
        HOE_HEAD(true, 100, true),
        CHISEL(true, 100, ItemMetalTool::new),
        CHISEL_HEAD(true, 100, true),
        SWORD(true, 200, ItemMetalTool::new),
        SWORD_BLADE(true, 200, true),
        MACE(true, 200, ItemMetalTool::new),
        MACE_HEAD(true, 200, true),
        SAW(true, 100, ItemMetalTool::new),
        SAW_BLADE(true, 100, true),
        JAVELIN(true, 100, ItemMetalTool::new), // todo: special class?
        JAVELIN_HEAD(true, 100, true),
        HAMMER(true, 100, ItemMetalTool::new),
        HAMMER_HEAD(true, 100, true),
        PROPICK(true, 100, ItemMetalTool::new),
        PROPICK_HEAD(true, 100, true),
        KNIFE(true, 100, ItemMetalTool::new),
        KNIFE_BLADE(true, 100, true),
        SCYTHE(true, 100, ItemMetalTool::new),
        SCYTHE_BLADE(true, 100, true),

        UNFINISHED_HELMET(true, 200),
        HELMET(true, 400, ItemMetalArmor::new),
        UNFINISHED_CHESTPLATE(true, 400),
        CHESTPLATE(true, 800, ItemMetalArmor::new),
        UNFINISHED_GREAVES(true, 400),
        GREAVES(true, 600, ItemMetalArmor::new),
        UNFINISHED_BOOTS(true, 200),
        BOOTS(true, 200, ItemMetalArmor::new);

        public final boolean toolItem;
        public final int smeltAmount;
        public final boolean hasMold;
        /**
         * Internal use only.
         */
        public final BiFunction<Metal, Metal.ItemType, Item> supplier;

        ItemType(boolean toolItem, int smeltAmount, BiFunction<Metal, Metal.ItemType, Item> supplier, boolean hasMold)
        {
            this.toolItem = toolItem;
            this.smeltAmount = smeltAmount;
            this.supplier = supplier;
            this.hasMold = hasMold;
        }

        ItemType(boolean toolItem, int smeltAmount, BiFunction<Metal, Metal.ItemType, Item> supplier)
        {
            this(toolItem, smeltAmount, supplier, false);
        }

        ItemType(boolean toolItem, int smeltAmount, boolean hasMold)
        {
            this(toolItem, smeltAmount, ItemMetal::new, hasMold);
        }

        ItemType(boolean toolItem, int smeltAmount)
        {
            this(toolItem, smeltAmount, false);
        }
    }
}
