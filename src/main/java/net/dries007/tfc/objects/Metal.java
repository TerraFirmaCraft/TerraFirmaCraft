package net.dries007.tfc.objects;

import net.dries007.tfc.objects.items.metal.*;
import net.minecraft.item.Item;

import static net.dries007.tfc.objects.Metal.Tier.*;

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

    UNKNOWN();

    public final boolean usable;
    public final Tier tier;
    public final double specificHeat;
    public final int meltTemp;
    public final Item.ToolMaterial toolMetal;

    Metal(Tier tier, double sh, int melt)
    {
        this(tier, sh, melt, null);
    }

    Metal(Tier tier, double sh, int melt, Item.ToolMaterial toolMetal)
    {
        this.usable = true;
        this.tier = tier;
        this.specificHeat = sh;
        this.meltTemp = melt;
        this.toolMetal = toolMetal;
    }

    Metal()
    {
        this.usable = false;
        this.tier = TIER_I;
        this.specificHeat = 0.5;
        this.meltTemp = 1250;
        this.toolMetal = null;
    }

    public enum Tier
    {
        TIER_I("Pit Kiln"),
        TIER_II("Beehive Kiln"),
        TIER_III("Bloomery"),
        TIER_IV("Blast Furnace"),
        TIER_V("Crucible");

        public final String name;

        Tier(String name)
        {
            this.name = name;
        }
    }

    public enum ItemType
    {
        UNSHAPED(false, 100, ItemUnshaped.class),
        INGOT(false, 100, ItemIngot.class),
        DOUBLE_INGOT(false, 200),
        SCRAP(false, 100),
        DUST(false, 100),
        NUGGET(false, 10),
        SHEET(false, 200, ItemSheet.class),
        DOUBLE_SHEET(false, 400),
        LAMP(false, 100, ItemLamp.class),

        ANVIL(true, 1400, ItemAnvil.class),
        TUYERE(true, 400),
        PICK(true, 100, ItemMetalTool.class),
        PICK_HEAD(true, 100),
        SHOVEL(true, 100, ItemMetalTool.class),
        SHOVEL_HEAD(true, 100),
        AXE(true, 100, ItemMetalTool.class),
        AXE_HEAD(true, 100),
        HOE(true, 100, ItemMetalTool.class),
        HOE_HEAD(true, 100),
        CHISEL(true, 100, ItemMetalTool.class),
        CHISEL_HEAD(true, 100),
        SWORD(true, 100, ItemMetalTool.class),
        SWORD_BLADE(true, 200),
        MACE(true, 100, ItemMetalTool.class),
        MACE_HEAD(true, 200),
        SAW(true, 100, ItemMetalTool.class),
        SAW_BLADE(true, 100),
        JAVELIN(true, 100, ItemMetalTool.class), // todo: special class?
        JAVELIN_HEAD(true, 100),
        HAMMER(true, 100, ItemMetalTool.class),
        HAMMER_HEAD(true, 100),
        PROPICK(true, 100, ItemMetalTool.class),
        PROPICK_HEAD(true, 100),
        KNIFE(true, 100, ItemMetalTool.class),
        KNIFE_BLADE(true, 100),
        SCYTHE(true, 100, ItemMetalTool.class),
        SCYTHE_BLADE(true, 100),
        UNFINISHED_HELMET(true, 200),
        HELMET(true, 400, ItemMetalArmor.class),
        UNFINISHED_CHESTPLATE(true, 400),
        CHESTPLATE(true, 800, ItemMetalArmor.class),
        UNFINISHED_GREAVES(true, 400),
        GREAVES(true, 600, ItemMetalArmor.class),
        UNFINISHED_BOOTS(true, 200),
        BOOTS(true, 200, ItemMetalArmor.class),
        ;

        public final boolean toolItem;
        public final int smeltAmount;
        /** Internal use. */
        public final Class<? extends ItemMetal> clazz;

        ItemType(boolean toolItem, int smeltAmount, Class<? extends ItemMetal> clazz)
        {
            this.toolItem = toolItem;
            this.smeltAmount = smeltAmount;
            this.clazz = clazz;
        }

        ItemType(boolean toolItem, int smeltAmount)
        {
            this(toolItem, smeltAmount, ItemMetal.class);
        }
    }
}
