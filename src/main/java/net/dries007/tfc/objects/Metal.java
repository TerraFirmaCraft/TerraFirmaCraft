package net.dries007.tfc.objects;

import static net.dries007.tfc.objects.Metal.Tier.*;

public enum Metal
{
    BISMUTH(TIER_I, 0.14, 270),
    BISMUTH_BRONZE(TIER_I, 0.35, 985, true),
    BLACK_BRONZE(TIER_I, 0.35, 1070, true),
    BRASS(TIER_I, 0.35, 930),
    BRONZE(TIER_I, 0.35, 950, true),
    COPPER(TIER_I, 0.35, 1080, true),
    GOLD(TIER_I, 0.6, 1060),
    LEAD(TIER_I, 0.22, 328),
    NICKEL(TIER_I, 0.48, 1453),
    ROSE_GOLD(TIER_I, 0.35, 960),
    SILVER(TIER_I, 0.48, 961),
    TIN(TIER_I, 0.14, 230),
    ZINC(TIER_I, 0.21, 420),
    STERLING_SILVER(TIER_I, 0.35, 900),

    WROUGHT_IRON(TIER_III, 0.35, 1535, true),
    PIG_IRON(TIER_IV, 0.35, 1535), // bloom: 0.35, 1500

    STEEL(TIER_IV, 0.35, 1540, true),

    PLATINUM(TIER_V, 0.35, 1730),

    BLACK_STEEL(TIER_V, 0.35, 1485, true),
    BLUE_STEEL(TIER_V, 0.35, 1540, true),
    RED_STEEL(TIER_V, 0.35, 1540, true),

    UNKNOWN();

    public final boolean usable;
    public final Tier tier;
    public final double specificHeat;
    public final int meltTemp;
    public final boolean toolMetal;

    Metal(Tier tier, double sh, int melt)
    {
        this(tier, sh, melt, false);
    }

    Metal(Tier tier, double sh, int melt, boolean toolMetal)
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
        this.toolMetal = false;
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
        UNSHAPED(false),
        INGOT(false),
        DOUBLE_INGOT(false),
        SCRAP(false),
        DUST(false),
        NUGGET(false),
        SHEET(false),
        DOUBLE_SHEET(false),
        LAMP(false),

        ANVIL(true),
        TUYERE(true),
        PICK(true),
        PICK_HEAD(true),
        SHOVEL(true),
        SHOVEL_HEAD(true),
        AXE(true),
        AXE_HEAD(true),
        HOE(true),
        HOE_HEAD(true),
        CHISEL(true),
        CHISEL_HEAD(true),
        SWORD(true),
        SWORD_BLADE(true),
        MACE(true),
        MACE_HEAD(true),
        SAW(true),
        SAW_BLADE(true),
        JAVELIN(true),
        JAVELIN_HEAD(true),
        HAMMER(true),
        HAMMER_HEAD(true),
        PROPICK(true),
        PROPICK_HEAD(true),
        KNIFE(true),
        KNIFE_BLADE(true),
        SCYTHE(true),
        SCYTHE_BLADE(true),
        UNFINISHED_CHESTPLATE(true),
        CHESTPLATE(true),
        UNFINISHED_GREAVES(true),
        GREAVES(true),
        UNFINISHED_BOOTS(true),
        BOOTS(true),
        UNFINISHED_HELMET(true),
        HELMET(true);

        public final boolean toolItem;

        ItemType(boolean toolItem)
        {
            this.toolItem = toolItem;
        }
    }
}
