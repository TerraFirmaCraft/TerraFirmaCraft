package net.dries007.tfc.objects;

import net.minecraft.util.IStringSerializable;

public enum Ore
{
    NATIVE_COPPER(true, Metal.COPPER),
    NATIVE_GOLD(true, Metal.GOLD),
    NATIVE_PLATINUM(true, Metal.PLATINUM),
    HEMATITE(true, Metal.PIG_IRON),
    NATIVE_SILVER(true, Metal.SILVER),
    CASSITERITE(true, Metal.TIN),
    GALENA(true, Metal.LEAD),
    BISMUTHINITE(true, Metal.BISMUTH),
    GARNIERITE(true, Metal.NICKEL),
    MALACHITE(true, Metal.COPPER),
    MAGNETITE(true, Metal.PIG_IRON),
    LIMONITE(true, Metal.PIG_IRON),
    SPHALERITE(true, Metal.ZINC),
    TETRAHEDRITE(true, Metal.COPPER),
    BITUMINOUS_COAL(false),
    LIGNITE(false),
    KAOLINITE(false),
    GYPSUM(false),
    SATINSPAR(false),
    SELENITE(false),
    GRAPHITE(false),
    KIMBERLITE(false),
    PETRIFIED_WOOD(false),
    SULFUR(false),
    JET(false),
    MICROCLINE(false),
    PITCHBLENDE(false),
    CINNABAR(false),
    CRYOLITE(false),
    SALTPETER(false),
    SERPENTINE(false),
    SYLVITE(false),
    BORAX(false),
    OLIVINE(false),
    LAPIS_LAZULI(false);

    public final boolean graded;
    public final Metal metal;

    Ore(boolean graded)
    {
        this(graded, null);
    }

    Ore(boolean graded, Metal metal)
    {
        this.graded = graded;
        this.metal = metal;
    }

    public enum Grade implements IStringSerializable
    {
        NORMAL(25), POOR(15), RICH(35);

        public final int smeltAmount;

        Grade(int smeltAmount) {this.smeltAmount = smeltAmount;}

        public static Grade byMetadata(int meta)
        {
            return Grade.values()[meta];
        }

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }

        public int getMeta()
        {
            return this.ordinal();
        }
    }
}
