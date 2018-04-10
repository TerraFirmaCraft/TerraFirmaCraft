package net.dries007.tfc.objects;

public enum Ore
{
    NATIVE_COPPER(true),
    NATIVE_GOLD(true),
    NATIVE_PLATINUM(true),
    HEMATITE(true),
    NATIVE_SILVER(true),
    CASSITERITE(true),
    GALENA(true),
    BISMUTHINITE(true),
    GARNIERITE(true),
    MALACHITE(true),
    MAGNETITE(true),
    LIMONITE(true),
    SPHALERITE(true),
    TETRAHEDRITE(true),
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

    Ore(boolean graded)
    {
        this.graded = graded;
    }
}
